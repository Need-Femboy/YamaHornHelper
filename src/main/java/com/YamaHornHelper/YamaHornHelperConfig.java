package com.YamaHornHelper;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("YamaHornHelper")
public interface YamaHornHelperConfig extends Config
{
	@ConfigItem(
		keyName = "ShowHornSettings",
		name = "Display Horn Settings",
		description = "Shows you the horn settings if you have it equipped"
	)
	default boolean showHornSettings()
	{
		return true;
	}
	
	@ConfigItem(
			keyName = "showPlayersInOverlay",
			name = "Display Players In Overlay",
			description = "Play list of player names in overlay"
	)
	default boolean showPlayersInOverlay()
	{
		return false;
	}
	
	@ConfigItem(
			keyName = "displayTileOfHornedUsers",
			name = "Display Player Tiles",
			description = "Shows tile of the players that horn can affect"
	)
	default boolean displayTileOfHornedUsers()
	{
		return true;
	}
	
	@Alpha
	@ConfigItem(
			keyName = "tileColour",
			name = "Player Tile Colour",
			description = "Colour of the player tile"
	)
	default Color tileColour()
	{
		return new Color(255, 0, 255, 50);
	}
	
	@Range(
			min = 1,
			max = 20
	)
	@ConfigItem(
			keyName = "maxPlayerDisplay",
			name = "Player Display Limit",
			description = "Maximum amount of players to display in the interface"
	)
	default int maxPlayerDisplay()
	{
		return 5;
	}
}
