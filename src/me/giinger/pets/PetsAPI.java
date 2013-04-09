package me.giinger.pets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Ocelot.Type;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import de.ntcomputer.minecraft.controllablemobs.api.ControllableMob;
import de.ntcomputer.minecraft.controllablemobs.api.ControllableMobs;

public class PetsAPI {

	public static PetsAPI instance;

	public Map<String, Pet> petlist = new HashMap<String, Pet>();

	public Map<Entity, ControllableMob<Zombie>> petzombies;
	public Map<Entity, ControllableMob<Ocelot>> petocelots;
	public Map<Entity, ControllableMob<MushroomCow>> petmooshrooms;

	public List<String> zombienaming = new ArrayList<String>();
	public List<String> ocelotnaming = new ArrayList<String>();
	public List<String> mooshroomnaming = new ArrayList<String>();

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

	public void killPet(Player player) {
		pet = petlist.get(player.getName());
		((LivingEntity) pet.getPet()).setHealth(1);
		((LivingEntity) pet.getPet()).damage(1);
		pet.setPet(null);
	}

	public static void killAllPets() {
		for (ControllableMob<Zombie> controlledZombie : instance.petzombies
				.values()) {
			controlledZombie.getActions().die();
		}
		for (ControllableMob<Ocelot> controlledOcelot : instance.petocelots
				.values()) {
			controlledOcelot.getActions().die();
		}
		for (ControllableMob<MushroomCow> controlledMooshroom : instance.petmooshrooms
				.values()) {
			controlledMooshroom.getActions().die();
		}
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

	public void setupPet(Player p, EntityType entitytype) {
		if (entitytype.equals(EntityType.ZOMBIE)) {
			zombienaming.add(p.getName());
		} else if (entitytype.equals(EntityType.OCELOT)) {
			ocelotnaming.add(p.getName());
		} else if (entitytype.equals(EntityType.MUSHROOM_COW)) {
			mooshroomnaming.add(p.getName());
		}
		p.sendMessage(ChatColor.YELLOW
				+ "What would you like to name your pet?");
	}

	public void spawnZombiePet(Location location, Player player) {
		Zombie zombie = location.getWorld().spawn(location, Zombie.class);
		ControllableMob<Zombie> controlledZombie = ControllableMobs.assign(
				zombie, true);
		setPet(player, zombie);
		controlledZombie.getActions().follow(player, false, 2, 1);
		controlledZombie.getActions().lookAt(player);
		controlledZombie.getEntity().getEquipment()
				.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		controlledZombie.getEntity().setBaby(true);
		controlledZombie.getEntity().setVillager(true);
		controlledZombie.getEntity().setCanPickupItems(false);
		controlledZombie.getEntity().setCustomName(pet.getPetName());
		controlledZombie.getEntity().setCustomNameVisible(true);
		doSmoke(location);
		petzombies.put(player, controlledZombie);
	}

	public void spawnOcelotPet(Location location, Player player) {
		Ocelot ocelot = location.getWorld().spawn(location, Ocelot.class);
		ControllableMob<Ocelot> controlledOcelot = ControllableMobs.assign(
				ocelot, true);
		setPet(player, ocelot);
		controlledOcelot.getEntity().setTamed(true);
		controlledOcelot.getEntity().setOwner(player);
		controlledOcelot.getProperties().setMovementSpeed(0.35F);
		controlledOcelot.getActions().follow(player, true, 2, 1);
		controlledOcelot.getActions().lookAt(player);
		Random gen = new Random();
		int x = gen.nextInt(4);
		if (x == 1) {
			controlledOcelot.getEntity().setCatType(Type.BLACK_CAT);
		} else if (x == 2) {
			controlledOcelot.getEntity().setCatType(Type.RED_CAT);
		} else if (x == 3) {
			controlledOcelot.getEntity().setCatType(Type.SIAMESE_CAT);
		} else if (x == 4) {
			controlledOcelot.getEntity().setCatType(Type.WILD_OCELOT);
		}
		controlledOcelot.getEntity().setBaby();
		controlledOcelot.getEntity().setAgeLock(true);
		controlledOcelot.getEntity().setCanPickupItems(false);
		controlledOcelot.getEntity().setCustomName(pet.getPetName());
		controlledOcelot.getEntity().setCustomNameVisible(true);
		doSmoke(location);
		petocelots.put(player, controlledOcelot);
	}

	public void spawnMooshroomPet(Location location, Player player) {
		MushroomCow mooshroom = location.getWorld().spawn(location,
				MushroomCow.class);
		ControllableMob<MushroomCow> controlledOcelot = ControllableMobs
				.assign(mooshroom, true);
		setPet(player, mooshroom);
		controlledOcelot.getProperties().setMovementSpeed(0.35F);
		controlledOcelot.getActions().follow(player, true, 2, 1);
		controlledOcelot.getActions().lookAt(player);
		controlledOcelot.getEntity().setBaby();
		controlledOcelot.getEntity().setAgeLock(true);
		controlledOcelot.getEntity().setCanPickupItems(false);
		controlledOcelot.getEntity().setCustomName(pet.getPetName());
		controlledOcelot.getEntity().setCustomNameVisible(true);
		doSmoke(location);
		petmooshrooms.put(player, controlledOcelot);
	}

	public void doSmoke(Location location) {
		location.getWorld().playEffect(location, Effect.SMOKE, 0);
		location.getWorld().playEffect(location, Effect.SMOKE, 2);
		location.getWorld().playEffect(location, Effect.SMOKE, 6);
		location.getWorld().playEffect(location, Effect.SMOKE, 8);
	}
}
