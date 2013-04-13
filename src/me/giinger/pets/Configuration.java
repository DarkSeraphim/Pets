package me.giinger.pets;

import org.bukkit.configuration.file.FileConfiguration;

public class Configuration {

	public FileConfiguration config = Pets.instance.getConfig();
	public static Configuration instance;

	public void setupConfig() {
		config.addDefault("Pets.Zombie.Dressable", false);
		config.addDefault("Pets.Zombie.Villager", true);
		config.addDefault("Pets.Cat.Random", true);
		config.addDefault("Pets.Cat.Navigate", true);
		config.addDefault("Pets.Mooshroom.Hearts", true);
		config.addDefault("Pets.Options.MySQL_Host", "");
		config.addDefault("Pets.Options.MySQL_Username", "");
		config.addDefault("Pets.Options.MySQL_Password", "");
		config.options().copyDefaults(true);
		Pets.instance.saveConfig();
	}

}
