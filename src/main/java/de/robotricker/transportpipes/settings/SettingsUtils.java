package de.robotricker.transportpipes.settings;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import de.robotricker.transportpipes.pipeutils.config.PlayerSettingsConf;

public class SettingsUtils {

	private static Map<Player, PlayerSettingsConf> cachedSettings;

	public SettingsUtils() {
		cachedSettings = new HashMap<>();
	}
	
	public PlayerSettingsConf getOrLoadPlayerSettings(Player p) {
		if (!cachedSettings.containsKey(p)) {
			cachedSettings.put(p, new PlayerSettingsConf(p));
		}
		return cachedSettings.get(p);
	}

}
