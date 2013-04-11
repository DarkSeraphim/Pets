package me.giinger.pets;

import me.giinger.pets.enums.PetType;

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
import org.bukkit.util.Vector;

public class PetsEvents implements Listener {

	PetsAPI api = PetsAPI.instance;
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
			api.spawnPet(p, api.getPetLoc(p), PetType.PET_ZOMBIE);
			p.getLocation().setX(p.getLocation().getX() - 0.4);
			PetsAPI.instance.zombienaming.remove(p.getName());
		} else if (PetsAPI.instance.ocelotnaming.contains(p.getName())) {
			e.setCancelled(true);
			api.setPetName(p, msg);
			api.spawnPet(p, api.getPetLoc(p), PetType.PET_CAT);
			p.getLocation().setX(p.getLocation().getX() - 0.4);
			PetsAPI.instance.ocelotnaming.remove(p.getName());
		} else if (PetsAPI.instance.mooshroomnaming.contains(p.getName())) {
			e.setCancelled(true);
			api.setPetName(p, msg);
			api.spawnPet(p, api.getPetLoc(p), PetType.PET_MOOSHROOM);
			p.getLocation().setX(p.getLocation().getX() - 0.4);
			PetsAPI.instance.mooshroomnaming.remove(p.getName());
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.hasItem()) {
			ItemStack i = e.getItem();
			if (i.getType() == Material.MONSTER_EGG) {
				CharSequence cs1 = "Baby";
				if (i.hasItemMeta())
					if (i.getItemMeta().getDisplayName().contains(cs1)) {
						if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
							api.mobegg.put(p, i);
							if (i.getDurability() == 54) {
								/* Zombie Pet */
								e.setCancelled(true);
								if (api.hasPet(p)) {
									api.killPet(p);
									return;
								}
								PetsAPI.instance.petlist.put(p.getName(),
										Pet.instance);
								if (api.hasName(p)) {
									e.setCancelled(true);
									api.spawnPet(p, e.getClickedBlock()
											.getLocation().add(0, 1, 0),
											PetType.PET_ZOMBIE);
								} else {
									System.out.println(api.hasName(p));
									api.setPetLoc(p, e.getClickedBlock()
											.getLocation().add(0, 1, 0));
									api.setupPet(p, EntityType.ZOMBIE);
								}
							} else if (i.getDurability() == 98) {
								/* Ocelot Pet */
								e.setCancelled(true);
								if (api.hasPet(p)) {
									api.killPet(p);
									return;
								}
								PetsAPI.instance.petlist.put(p.getName(),
										Pet.instance);
								if (api.hasName(p)) {
									e.setCancelled(true);
									api.spawnPet(p, e.getClickedBlock()
											.getLocation().add(0, 1, 0),
											PetType.PET_CAT);
								} else {
									api.setPetLoc(p, e.getClickedBlock()
											.getLocation().add(0, 1, 0));
									api.setupPet(p, EntityType.OCELOT);
								}
							} else if (i.getDurability() == 96) {
								/* Mooshroom Pet */
								e.setCancelled(true);
								if (api.hasPet(p)) {
									api.killPet(p);
									return;
								}
								PetsAPI.instance.petlist.put(p.getName(),
										Pet.instance);
								if (api.hasName(p)) {
									e.setCancelled(true);
									api.spawnPet(p, e.getClickedBlock()
											.getLocation().add(0, 1, 0),
											PetType.PET_MOOSHROOM);
								} else {
									api.setPetLoc(p, e.getClickedBlock()
											.getLocation().add(0, 1, 0));
									api.setupPet(p, EntityType.MUSHROOM_COW);
								}
							}
						} else if (e.getAction() == Action.LEFT_CLICK_AIR
								|| e.getAction() == Action.LEFT_CLICK_BLOCK) {
							if (i.getDurability() == 54) {
								/* Zombie Pet */
								api.setupPet(p, EntityType.ZOMBIE);
							} else if (i.getDurability() == 98) {
								/* Ocelot Pet */
								api.setupPet(p, EntityType.OCELOT);
							} else if (i.getDurability() == 96) {
								/* Mooshroom Pet */
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
		if (api.hasPet(p)) {
			Entity pet = api.getPet(p);
			if (!(p.getNearbyEntities(10D, 10D, 10D).contains(pet))) {
				if (!p.isFlying()) {
					api.doSmoke(pet.getLocation());
					Vector ivelo = pet.getVelocity();
					pet.teleport(p.getLocation());
					pet.setVelocity(ivelo);
					api.doSmoke(pet.getLocation());
				}
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
