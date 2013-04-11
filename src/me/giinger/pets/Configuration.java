package me.giinger.pets;

import org.bukkit.configuration.file.FileConfiguration;

public class Configuration {

	public static FileConfiguration config = Pets.instance.getConfig();
	public static Configuration instance;

	public void setupConfig() {
		config.addDefault("Pets.Zombie.Dressable", false);
		config.addDefault("Pets.Zombie.Villager", true);
		config.addDefault("Pets.Cat.Random", true);
		config.addDefault("Pets.Mooshroom.Hearts", true);
		config.addDefault("Pets.Creeper.Explode", true);
		config.options().copyDefaults(true);
		Pets.instance.saveConfig();
	}

}
