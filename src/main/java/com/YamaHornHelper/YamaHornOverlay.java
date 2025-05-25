package com.YamaHornHelper;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.util.Text;

public class YamaHornOverlay extends OverlayPanel
{
	private final Client client;
	private final YamaHornHelperPlugin plugin;
	private final YamaHornHelperConfig config;
	
	@Inject
	private YamaHornOverlay(Client client, YamaHornHelperPlugin plugin, YamaHornHelperConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT); // Adjust position as needed
		panelComponent.setWrap(true); // Allow wrapping for vertical layout
		panelComponent.setGap(new Point(0, 4)); // Vertical gap between text rows
		panelComponent.setPreferredSize(new Dimension(150, 0)); // Set width for text, height auto
		panelComponent.setOrientation(ComponentOrientation.VERTICAL); // One string per row
	}
	
	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (plugin.isHidden())
		{
			return null;
		}
		
		//panelComponent.getChildren().clear();
		
		if (config.showHornSettings())
		{
			LineComponent radius = generateLineBoth("Max Radius", String.format("%s", plugin.yamaRadius), Color.WHITE);
			panelComponent.getChildren().add(radius);
			LineComponent player = generateLineBoth("Max Players", String.format("%s (%s)", plugin.yamaPlayers, plugin.getPlayersWithinRange().size()), Color.WHITE);
			panelComponent.getChildren().add(player);
		}
		
		if (config.showPlayersInOverlay())
		{
			int playerCount = 0;
			
			for (Player player : plugin.getPlayersWithinRange())
			{
				if (playerCount + 1 > config.maxPlayerDisplay())
				{
					break;
				}
				
				if (player.getName() != null)
				{ //Put check here to still enable tile overlay for players
					LineComponent lineComponent = generateLine(Text.sanitize(player.getName()), Color.WHITE);
					panelComponent.getChildren().add(lineComponent);
					playerCount++;
				}
			}
		}
		
		return super.render(graphics);
	}
	
	private LineComponent generateLine(String str, Color color)
	{
		LineComponent lineComponent = LineComponent.builder()
				.left(str)
				.leftColor(color)
				.build();
		return lineComponent;
	}
	
	private LineComponent generateLineBoth(String strLeft, String strRight, Color color)
	{
		LineComponent lineComponent = LineComponent.builder()
				.left(strLeft)
				.leftColor(color)
				.right(strRight)
				.rightColor(color)
				.build();
		return lineComponent;
	}
}
