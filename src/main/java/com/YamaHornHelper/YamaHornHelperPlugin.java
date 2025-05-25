package com.YamaHornHelper;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
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
		name = "Yama Horn Helper",
		description = "Plugin to help horners"
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
	public boolean isHidden;
	
	@Getter
	public boolean useCache;
	
	private List<Player> listOfPlayers = new ArrayList<>();
	
	private List<Player> cachedList = new ArrayList<>();
 
	@Override
	protected void startUp() throws Exception
	{
		clientThread.invoke(() ->
		{
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				yamaPlayers = client.getVarbitValue(VarbitID.YAMA_HORN_MAX_PLAYERS);
				yamaRadius = client.getVarbitValue(VarbitID.YAMA_HORN_RADIUS);
				//onItemContainerChanged(new ItemContainerChanged(InventoryID.WORN, client.getItemContainer(InventoryID.WORN)));
			}
			else
			{
				yamaPlayers = yamaRadius = -1;
				isHidden = true;
			}
		});
		overlayManager.add(yamaHornOverlay);
		overlayManager.add(yamaHornTileOverlay);
	}
	
	@Override
	protected void shutDown() throws Exception
	{
		yamaPlayers = yamaRadius = -1;
		useCache = false;
		listOfPlayers.clear();
		cachedList.clear();
		overlayManager.remove(yamaHornOverlay);
		overlayManager.remove(yamaHornTileOverlay);
	}
	
	
	@Subscribe
	public void onGameTick(GameTick event)
	{
		useCache = false;
		listOfPlayers.clear();
		
		if (isHidden)
		{
			return;
		}
		
		listOfPlayers.addAll(client.getTopLevelWorldView().players().stream().collect(Collectors.toList()));
	}
	
	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarbitId() == VarbitID.YAMA_HORN_RADIUS)
		{
			yamaRadius = event.getValue();
		}
		else if (event.getVarbitId() == VarbitID.YAMA_HORN_MAX_PLAYERS)
		{
			yamaPlayers = event.getValue();
		}
	}
	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() != InventoryID.WORN)
		{
			return;
		}
		Item weapon = event.getItemContainer().getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
		isHidden = weapon == null || weapon.getId() != ItemID.SOULFLAME_HORN; //Hide interface when not equipping a horn
	}
	
	public List<Player> getPlayersWithinRange()
	{
		if (useCache) //Stop recalculating entire list every time overlay renders it
		{
			return cachedList;
		}
		
		if (listOfPlayers.size() <= 1)
		{
			return new ArrayList<>();
		}
		
		List<Player> tempList = new ArrayList<>();
		Player localPlayer = client.getLocalPlayer();
		WorldPoint myLocation = localPlayer.getWorldLocation();
		
		for (Player p : listOfPlayers)
		{
			if (p == localPlayer)
			{
				continue;
			}
			
			WorldPoint theirLocation = p.getWorldLocation();
			if (myLocation.distanceTo2D(theirLocation) <= yamaRadius)
			{
				tempList.add(p);
			}
		}
		
		useCache = true;
		cachedList = tempList;
		
		return cachedList;
	}
	
	@Provides
	YamaHornHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(YamaHornHelperConfig.class);
	}
}
