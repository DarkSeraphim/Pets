package me.giinger.pets;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class PetsAPI {

	public static Map<String, Pet> petlist = new HashMap<String, Pet>();
	private Pet pet;

	public Entity getPet(Player player) {
		pet = petlist.get(player.getName());
		return pet.getPet();
	}

	public void setPet(Player player, Entity entity) {
		pet = petlist.get(player.getName());
		pet.setPet(entity);
	}

	public String getPetName(Player player) {
		pet = petlist.get(player.getName());
		return pet.getPetName();
	}

	public void setPetName(Player player, String name) {
		pet = petlist.get(player.getName());
		pet.setPetName(name);
	}

	public void spawnPet(Location location, Player p) {
		pet = petlist.get(p.getName());
		Entity e = EntityType.ZOMBIE;
		Entity e = pet.getPet();
		pet.setPet(e);
		((Zombie) e).setBaby(true);
		((Zombie) e).setCanPickupItems(false);
		((Zombie) e).setCustomName(pet.getPetName());
		((Zombie) e).setCustomNameVisible(true);
		((Zombie) e).setTarget(p);
		p.getWorld().spawnEntity(location, e.getType());
	}

}
