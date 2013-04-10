package me.giinger.pets;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;

import de.ntcomputer.minecraft.controllablemobs.api.ControllableMob;

public class Pets extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");

	public static Pets instance = new Pets();

	public void onEnable() {
		instance = this;
		PetsAPI.instance.petzombies = new HashMap<Entity, ControllableMob<Zombie>>();
		PetsAPI.instance.petocelots = new HashMap<Entity, ControllableMob<Ocelot>>();
		PetsAPI.instance.petmooshrooms = new HashMap<Entity, ControllableMob<MushroomCow>>();
		getServer().getPluginManager().registerEvents(new PetsEvents(), this);
		log.info("[MMORPG] Pets v1.0 Enabled!");

		for (Player p : Bukkit.getOnlinePlayers()) {
			PetsAPI.instance.petlist.put(p.getName(), new Pet());
		}
	}

	public void onDisable() {
		PetsAPI.killAllPets();
		log.info("[MMORPG] Pets v1.0 Disabled!");
	}
}
