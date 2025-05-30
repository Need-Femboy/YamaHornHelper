package com.YamaHornHelper;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
		name = "Soulflame Horn Helper",
		description = "Plugin to help horners",
		tags = {"pvm", "bossing"}
)
public class YamaHornHelperPlugin extends Plugin
{
	@Inject
	private Client client;
	
	@Inject
	private YamaHornHelperConfig config;
	
	@Inject
	private ClientThread clientThread;
	
	@Inject
	private OverlayManager overlayManager;
	
	@Inject
	private YamaHornOverlay yamaHornOverlay;
	@Inject
	private YamaHornTileOverlay yamaHornTileOverlay;
	
	public int yamaPlayers, yamaRadius;
	
	@Getter
	public boolean noHornEquipped;
	@Getter
	private List<Player> listOfPlayers = new ArrayList<>();
	
	
	@Override
	protected void startUp() throws Exception {
		clientThread.invoke(() ->
		{
			if (client.getGameState() == GameState.LOGGED_IN) {
				yamaPlayers = client.getVarbitValue(VarbitID.YAMA_HORN_MAX_PLAYERS);
				yamaRadius = client.getVarbitValue(VarbitID.YAMA_HORN_RADIUS);
			}
			else {
				yamaPlayers = yamaRadius = -1;
				noHornEquipped = true;
			}
		});
		overlayManager.add(yamaHornOverlay);
		overlayManager.add(yamaHornTileOverlay);
	}
	
	@Override
	protected void shutDown() throws Exception {
		yamaPlayers = yamaRadius = -1;
		listOfPlayers.clear();
		overlayManager.remove(yamaHornOverlay);
		overlayManager.remove(yamaHornTileOverlay);
	}
	
	
	@Subscribe
	public void onGameTick(GameTick event) {
		listOfPlayers.clear();
		
		if (noHornEquipped) {
			return;
		}
		
		Player localPlayer = client.getLocalPlayer();
		WorldPoint myLocation = localPlayer.getWorldLocation();
		
		listOfPlayers = client.getTopLevelWorldView().players()
				.stream()
				.filter(p -> p != localPlayer)
				.filter(p -> myLocation.distanceTo2D(p.getWorldLocation()) <= yamaRadius)
				.collect(Collectors.toList());
	}
	
	@Subscribe
	public void onVarbitChanged(VarbitChanged event) {
		if (event.getVarbitId() == VarbitID.YAMA_HORN_RADIUS) {
			yamaRadius = event.getValue();
		}
		else if (event.getVarbitId() == VarbitID.YAMA_HORN_MAX_PLAYERS) {
			yamaPlayers = event.getValue();
		}
	}
	
	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event) {
		if (event.getContainerId() != InventoryID.WORN) {
			return;
		}
		Item weapon = event.getItemContainer().getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
		noHornEquipped = weapon == null || weapon.getId() != ItemID.SOULFLAME_HORN; //Hide interface when not equipping a horn
	}
	
	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		if (noHornEquipped || !event.getMenuOption().startsWith("Use")) {
			return;
		}
		
		if (event.getMenuOption().equals("Use <col=00ff00>Special Attack</col>") || event.getMenuTarget().equals("<col=ff9040>Special Attack</col>"))
		{
			if (config.hardCapSpec() && listOfPlayers.size() > yamaPlayers) {
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ef1020>You blow your horn, but there are too many people nearby to hear it clearly.", null);
				event.consume();
			}
			else if (listOfPlayers.size() == 0) {
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ef1020>You blow your horn into the wind. No-one nearby is able to listen.", null);
				event.consume();
			}
		}
	}
	
	@Provides
	YamaHornHelperConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(YamaHornHelperConfig.class);
	}
}
