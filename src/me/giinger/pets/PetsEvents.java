package me.giinger.pets;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PetsEvents implements Listener {

	PetsAPI api = new PetsAPI();
	Pet pet;

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		String msg = e.getMessage();
		Player p = e.getPlayer();

		if (PetsAPI.naming.contains(p.getName())) {
			e.setCancelled(true);
			api.setPetName(p, msg);
			api.spawnZombiePet(api.getPetLoc(p), p);
			PetsAPI.naming.remove(p.getName());
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
						if (api.hasPet(p)) {
							((Zombie) api.getPet(p)).setHealth(0);
							PetsAPI.petlist.remove(p.getName());
							p.sendMessage("Killed Pet");
							return;
						}
						PetsAPI.petlist.put(p.getName(), new Pet());
						if (api.hasName(p)) {
							e.setCancelled(true);
							api.spawnZombiePet(e.getClickedBlock()
									.getLocation(), p);
						} else {
							api.setPetLoc(p, e.getClickedBlock().getLocation());
							api.setupPet(p);
						}
					} else if (i.getDurability() == 96) {
						/* Mooshroom Pet */
						PetsAPI.petlist.put(p.getName(), new Pet());
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		Entity entity = e.getEntity();
		Entity damager = e.getDamager();
		if (entity instanceof Player) {
			Player p = (Player) entity;
			System.out.println(p.getName());
			if (api == null) {
				System.out.println("API is null");
			}
			if (api.getPet(p) != null)
				if (damager == api.getPet(p)) {
					e.setCancelled(true);
				}

			for (Player player : Bukkit.getOnlinePlayers()) {
				if (api.getPet(player) != null) {
					if (entity == api.getPet(player)) {
						e.setCancelled(true);
					}
				}
			}
		}
	}
}
