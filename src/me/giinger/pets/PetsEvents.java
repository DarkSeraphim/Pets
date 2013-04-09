package me.giinger.pets;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PetsEvents implements Listener {

	PetsAPI api = new PetsAPI();
	Pet pet;

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		PetsAPI.instance.petlist.put(p.getName(), new Pet());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		api.killPet(p);
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		String msg = e.getMessage();
		Player p = e.getPlayer();

		if (PetsAPI.instance.zombienaming.contains(p.getName())) {
			e.setCancelled(true);
			api.setPetName(p, msg);
			api.spawnZombiePet(api.getPetLoc(p), p);
			PetsAPI.instance.zombienaming.remove(p.getName());
		} else if (PetsAPI.instance.ocelotnaming.contains(p.getName())) {
			e.setCancelled(true);
			api.setPetName(p, msg);
			api.spawnOcelotPet(api.getPetLoc(p), p);
			PetsAPI.instance.ocelotnaming.remove(p.getName());
		} else if (PetsAPI.instance.mooshroomnaming.contains(p.getName())) {
			e.setCancelled(true);
			api.setPetName(p, msg);
			api.spawnMooshroomPet(api.getPetLoc(p), p);
			PetsAPI.instance.mooshroomnaming.remove(p.getName());
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.hasItem()) {
				ItemStack i = e.getItem();
				if (i.getType() == Material.MONSTER_EGG) {
					if (i.getDurability() == 54) {
						/* Zombie Pet */
						e.setCancelled(true);
						if (api.hasPet(p)) {
							api.killPet(p);
							return;
						}
						PetsAPI.instance.petlist.put(p.getName(), new Pet());
						if (api.hasName(p)) {
							e.setCancelled(true);
							api.spawnZombiePet(e.getClickedBlock()
									.getLocation().add(0, 1, 0), p);
						} else {
							api.setPetLoc(p, e.getClickedBlock().getLocation()
									.add(0, 1, 0));
							api.setupPet(p, EntityType.ZOMBIE);
						}
					} else if (i.getDurability() == 98) {
						/* Ocelot Pet */
						e.setCancelled(true);
						if (api.hasPet(p)) {
							api.killPet(p);
							return;
						}
						PetsAPI.instance.petlist.put(p.getName(), new Pet());
						if (api.hasName(p)) {
							e.setCancelled(true);
							api.spawnOcelotPet(e.getClickedBlock()
									.getLocation().add(0, 2, 0), p);
						} else {
							api.setPetLoc(p, e.getClickedBlock().getLocation()
									.add(0, 1, 0));
							api.setupPet(p, EntityType.OCELOT);
						}
					} else if (i.getDurability() == 96) {
						/* Mooshroom Pet */
						e.setCancelled(true);
						if (api.hasPet(p)) {
							api.killPet(p);
							return;
						}
						PetsAPI.instance.petlist.put(p.getName(), new Pet());
						if (api.hasName(p)) {
							e.setCancelled(true);
							api.spawnMooshroomPet(e.getClickedBlock()
									.getLocation().add(0, 2, 0), p);
						} else {
							api.setPetLoc(p, e.getClickedBlock().getLocation()
									.add(0, 1, 0));
							api.setupPet(p, EntityType.MUSHROOM_COW);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();

		double x = p.getLocation().getX() + 10;
		double y = p.getLocation().getY() + 10;
		double z = p.getLocation().getZ() + 10;

		if (api.hasPet(p)) {
			Entity pet = api.getPet(p);
			if (pet.getLocation().getX() >= x || pet.getLocation().getY() >= y
					|| pet.getLocation().getZ() >= z) {
				api.doSmoke(pet.getLocation());
				pet.teleport(p.getLocation());
				api.doSmoke(pet.getLocation());
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		Entity entity = e.getEntity();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (entity == PetsAPI.instance.petlist.get(player.getName())
					.getPet()) {
				e.setCancelled(true);
			}
		}
	}
}
