package me.giinger.pets;

import me.giinger.pets.enums.PetType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class PetsEvents implements Listener {

	PetsAPI api = PetsAPI.instance;
	Pet pet;

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		PetsAPI.instance.petlist.put(p.getName(), new Pet());

		SQLHandler.instance.giveEggs(p);
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

		if (PetsAPI.instance.pettype.containsKey(p.getName())) {
			e.setCancelled(true);
			if ((msg.matches("^[a-zA-Z0-9!#$_ ]*$")) && (msg.length() <= 16)
					&& (msg.length() >= 3)) {
				PetType type = PetsAPI.instance.pettype.get(p.getName());
				api.setPetName(p, msg);
				if (api.getPetLoc(p) != null)
					if (type == PetType.PET_ZOMBIE) {
						api.spawnPet(p, api.getPetLoc(p), type);
					} else if (type == PetType.PET_CAT) {
						api.spawnPet(p, api.getPetLoc(p), type);
					} else if (type == PetType.PET_MOOSHROOM) {
						api.spawnPet(p, api.getPetLoc(p), type);
					}
				p.getLocation().setY(p.getLocation().getY() + 0.5);
				PetsAPI.instance.pettype.remove(p.getName());
			} else {
				p.sendMessage(ChatColor.RED + "Invalid Pet Name!");
				p.sendMessage(ChatColor.RED
						+ "Pet name must be alphanumeric, can only contain special characters !,#,$,_, and space, and must be 3-16 characters long.");
			}
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
						if (!api.cantSpawn.contains(p.getName())) {
							if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
								if (!api.mobegg.containsKey(p.getName()))
									api.mobegg.put(p.getName(), i);
								if (i.getDurability() == 54) {
									/* Zombie Pet */
									e.setCancelled(true);
									if (api.hasPet(p)) {
										api.killPet(p);
										return;
									}
									if (api.hasName(p)) {
										e.setCancelled(true);
										api.spawnPet(p, e.getClickedBlock()
												.getLocation().add(0, 1, 0),
												PetType.PET_ZOMBIE);
									} else {
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
								if (!api.mobegg.containsKey(p.getName()))
									api.mobegg.put(p.getName(), i);
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
					} else {
						e.setCancelled(true);
						Pet pet = api.petlist.get(p.getName());
						long one = pet.one;
						long two = pet.two;
						long three = pet.three;
						getTimeLeft(p, one, two, three);
					}
			}
		}
	}

	private void getTimeLeft(Player p, long one, long two, long three) {
		long x = System.currentTimeMillis();

		if (x <= one) {
			p.sendMessage(ChatColor.RED + "Wait " + ChatColor.BOLD + "3s");
		} else if (x > one && x <= two) {
			p.sendMessage(ChatColor.RED + "Wait " + ChatColor.BOLD + "2s");
		} else if (x > two && x <= three) {
			p.sendMessage(ChatColor.RED + "Wait " + ChatColor.BOLD + "1s");
		}
	}

	@EventHandler
	public void onItemThrow(PlayerDropItemEvent event) {
		final ItemStack thrown = event.getItemDrop().getItemStack();
		CharSequence egg = "[Baby";
		if (thrown.getType() == Material.MONSTER_EGG) {
			if (thrown.hasItemMeta()) {
				ItemMeta meta = thrown.getItemMeta();
				if (meta.getDisplayName().contains(egg)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
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
