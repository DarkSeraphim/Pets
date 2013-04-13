package me.giinger.pets;

import java.util.HashMap;
import java.util.logging.Logger;

import me.giinger.particleapi.ParticleAPI;
import me.giinger.pets.enums.EggType;
import me.giinger.pets.enums.PetType;

import org.bukkit.Bukkit;
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
		/* Instantiate Stuff */
		instance = this;
		PetsAPI.instance = new PetsAPI();
		PetsAPI.particleapi = new ParticleAPI();
		Configuration.instance = new Configuration();
		SQLHandler.instance = new SQLHandler();

		PetsAPI.instance.petzombies = new HashMap<String, ControllableMob<Zombie>>();
		PetsAPI.instance.petocelots = new HashMap<String, ControllableMob<Ocelot>>();
		PetsAPI.instance.petmooshrooms = new HashMap<String, ControllableMob<MushroomCow>>();
		/* End of Instantiations */

		SQLHandler.instance.connectSQL();

		Configuration.instance.setupConfig();
		getServer().getPluginManager().registerEvents(new PetsEvents(), this);
		log.info("[MMORPG] Pets v1.0 Enabled!");

		for (Player p : Bukkit.getOnlinePlayers()) {
			PetsAPI.instance.petlist.put(p.getName(), new Pet());
		}
	}

	public void onDisable() {
		SQLHandler.instance.closeSQL();
		PetsAPI.killAllPets();
		log.info("[MMORPG] Pets v1.0 Disabled!");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String lbl,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("petegg")) {
			if (sender.isOp()) {
				if (args.length == 2) {
					Player p = getServer().getPlayer(args[0]);
					if (p != null)
						if (args[1].equalsIgnoreCase("zombie")) {
							if (!PetsAPI.instance.hasEgg(p, EggType.ZOMBIE_EGG))
								PetsAPI.instance.giveEgg(p, EggType.ZOMBIE_EGG);
							SQLHandler.instance
									.setSQLPet(p, PetType.PET_ZOMBIE);
							System.out.println(p.getName());
						} else if (args[1].equalsIgnoreCase("cat")) {
							if (!PetsAPI.instance.hasEgg(p, EggType.CAT_EGG))
								PetsAPI.instance.giveEgg(p, EggType.CAT_EGG);
							SQLHandler.instance.setSQLPet(p, PetType.PET_CAT);
							System.out.println(p.getName());
						} else if (args[1].equalsIgnoreCase("mooshroom")) {
							if (!PetsAPI.instance.hasEgg(p,
									EggType.MOOSHROOM_EGG))
								PetsAPI.instance.giveEgg(p,
										EggType.MOOSHROOM_EGG);
							SQLHandler.instance.setSQLPet(p,
									PetType.PET_MOOSHROOM);
							System.out.println(p.getName());
						} else
							sender.sendMessage("Wrong type: zombie/cat/mooshroom");
					else
						sender.sendMessage("Player: " + args[0]
								+ " is not online.");
				}
			}
		}
		return true;
	}
}
