package me.giinger.pets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class PetsAPI {

	public static Map<String, Pet> petlist = new HashMap<String, Pet>();
	public static List<String> naming = new ArrayList<String>();
	private Pet pet;

	public Entity getPet(Player player) {
		pet = petlist.get(player.getName());
		if (pet.getPet() != null)
			return pet.getPet();
		else
			return null;
	}

	public void setPet(Player player, Entity entity) {
		pet = petlist.get(player.getName());
		pet.setPet(entity);
	}

	public String getPetName(Player player) {
		pet = petlist.get(player.getName());
		if (pet.getPetName() != null)
			return pet.getPetName();
		else
			return null;
	}

	public void setPetName(Player player, String name) {
		pet = petlist.get(player.getName());
		pet.setPetName(name);
	}

	public void setPetLoc(Player player, Location location) {
		pet = petlist.get(player.getName());
		pet.setLocation(location);
	}

	public Location getPetLoc(Player player) {
		pet = petlist.get(player.getName());
		return pet.getLocation();
	}

	public boolean hasPet(Player player) {
		pet = petlist.get(player.getName());
		if (pet.getPet() == null)
			return false;
		else
			return true;
	}

	/**
	 * 
	 * @return true if their pet has a name
	 */
	public boolean hasName(Player p) {
		if (getPetName(p) == null) {
			return false;
		} else
			return true;
	}

	public void setupPet(Player p) {
		naming.add(p.getName());
		p.sendMessage(ChatColor.YELLOW
				+ "What would you like to name your pet?");
	}

	public void spawnZombiePet(Location location, Player p) {
		location = new Location(location.getWorld(), location.getX(),
				location.getY() + 1, location.getZ());
		pet = petlist.get(p.getName());
		Zombie e = (Zombie) p.getWorld().spawn(location, Zombie.class);
		pet.setPet((Entity) e);
		System.out.println(e);
		e.setBaby(true);
		e.setVillager(true);
		e.setCanPickupItems(false);
		e.setCustomName(pet.getPetName());
		e.setCustomNameVisible(true);
		e.setTarget(p);
		p.playEffect(location, Effect.SMOKE, 0);
		p.playEffect(location, Effect.SMOKE, 2);
		p.playEffect(location, Effect.SMOKE, 6);
		p.playEffect(location, Effect.SMOKE, 8);
	}
}
