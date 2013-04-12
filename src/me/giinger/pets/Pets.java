package me.giinger.pets;

import java.util.HashMap;
import java.util.logging.Logger;

import me.giinger.particleapi.ParticleAPI;
import me.giinger.pets.enums.EggType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;

import de.ntcomputer.minecraft.controllablemobs.api.ControllableMob;

public class Pets extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");

	public static Pets instance;
	public PetsAPI api = PetsAPI.instance;

	public void onEnable() {
		instance = this;
		PetsAPI.instance = new PetsAPI();
		PetsAPI.particleapi = new ParticleAPI();
		Configuration.instance = new Configuration();

		PetsAPI.instance.petzombies = new HashMap<String, ControllableMob<Zombie>>();
		PetsAPI.instance.petocelots = new HashMap<String, ControllableMob<Ocelot>>();
		PetsAPI.instance.petmooshrooms = new HashMap<String, ControllableMob<MushroomCow>>();

		Configuration.instance.setupConfig();
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

	public boolean onCommand(CommandSender sender, Command cmd, String lbl,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("petegg")) {
			if (sender.isOp()) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("zombie")) {
						PetsAPI.instance.giveEgg((Player) sender,
								EggType.ZOMBIE_EGG);
					} else if (args[0].equalsIgnoreCase("cat")) {
						PetsAPI.instance.giveEgg((Player) sender,
								EggType.CAT_EGG);
					} else if (args[0].equalsIgnoreCase("mooshroom")) {
						PetsAPI.instance.giveEgg((Player) sender,
								EggType.MOOSHROOM_EGG);
					} else
						sender.sendMessage(ChatColor.RED
								+ "Wrong type: zombie/cat/mooshroom");
				}
			}
		}
		return true;
	}
}
