package com.YamaHornHelper;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("YamaHornHelper")
public interface YamaHornHelperConfig extends Config
{
	@ConfigItem(
		keyName = "ShowHornSettings",
		name = "Display Horn Settings",
		description = "Shows you the horn settings if you have it equipped",
		position = 0
	)
	default boolean showHornSettings()
	{
		return true;
	}
	
	@ConfigItem(
			keyName = "displayTileOfHornedUsers",
			name = "Display Player Tiles",
			description = "Shows tile of the players that horn can affect",
			position = 1
	)
	default boolean displayTileOfHornedUsers()
	{
		return true;
	}
	
	@ConfigItem(
			keyName = "showPlayersInOverlay",
			name = "Display Players In Overlay",
			description = "Play list of player names in overlay",
			position = 2
	)
	default boolean showPlayersInOverlay()
	{
		return false;
	}
	
	@ConfigItem(
			keyName = "hardCapSpec",
			name = "Hard Cap Spec",
			description = "Prevents player from using the spec if there ae too many players around. If you set the horn to 1 player, it'll only work if there's only 1 other player around, this prevents you from speccing someone else by mistake",
			position = 3
	)
	default boolean hardCapSpec()
	{
		return false;
	}
	
	@Range(
			min = 1,
			max = 20
	)
	@ConfigItem(
			keyName = "maxPlayerDisplay",
			name = "Player Display Limit",
			description = "Maximum amount of players to display in the interface",
			position = 4
	)
	default int maxPlayerDisplay()
	{
		return 5;
	}
	
	@Alpha
	@ConfigItem(
			keyName = "tileColour",
			name = "Player Tile Colour",
			description = "Colour of the player tile",
			position = 5
	)
	default Color tileColour()
	{
		return new Color(255, 0, 255, 50);
	}
}
