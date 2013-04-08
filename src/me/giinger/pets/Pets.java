package me.giinger.pets;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class Pets extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");

	public void onEnable() {
		log.info("[MMORPG] Pets v1.0 Enabled!");
	}

	public void onDisable() {
		log.info("[MMORPG] Pets v1.0 Disabled!");
	}
}
