package com.YamaHornHelper;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import javax.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;

public class YamaHornTileOverlay extends Overlay
{
	private final Client client;
	private final YamaHornHelperPlugin plugin;
	private final YamaHornHelperConfig config;
	
	@Inject
	private YamaHornTileOverlay(Client client, YamaHornHelperPlugin plugin, YamaHornHelperConfig config) {
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}
	
	@Override
	public Dimension render(Graphics2D graphics2D) {
		if (!config.displayTileOfHornedUsers()) {
			return null;
		}
		
		for (Player player : plugin.getListOfPlayers()) {
			Shape playerShape = Perspective.getCanvasTilePoly(client, player.getLocalLocation());
			if (playerShape != null) {
				graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				graphics2D.setColor(config.tileColour());
				graphics2D.setStroke(new BasicStroke(2f));
				graphics2D.draw(playerShape);
			}
		}
		
		return null;
	}
}
