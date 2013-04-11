package me.giinger.pets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.giinger.particleapi.ParticleAPI;
import me.giinger.particleapi.ParticleType;
import me.giinger.pets.enums.EggType;
import me.giinger.pets.enums.PetType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Ocelot.Type;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import de.ntcomputer.minecraft.controllablemobs.api.ControllableMob;
import de.ntcomputer.minecraft.controllablemobs.api.ControllableMobs;

public class PetsAPI {

	public static PetsAPI instance;
	public static ParticleAPI particleapi;

	public Map<String, Pet> petlist = new HashMap<String, Pet>();
	public static Map<Player, Integer> particles = new HashMap<Player, Integer>();

	public Map<Player, ItemStack> mobegg = new HashMap<Player, ItemStack>();

	public Map<Entity, ControllableMob<Zombie>> petzombies;
	public Map<Entity, ControllableMob<Ocelot>> petocelots;
	public Map<Entity, ControllableMob<MushroomCow>> petmooshrooms;
	public Map<Entity, ControllableMob<Creeper>> petcreepers;

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
		return pet.getPetName();
	}

	@SuppressWarnings("deprecation")
	public void setPetName(Player player, String name) {
		pet = petlist.get(player.getName());
		pet.setPetName(name);

		ItemStack egg = mobegg.get(player);
		ItemMeta meta = egg.getItemMeta();

		String oldname = meta.getDisplayName().replaceAll(".+-.", "");
		String newname = ChatColor.BLUE + pet.getPetName() + ""
				+ ChatColor.GRAY + " - " + oldname;

		meta.setDisplayName(newname);
		egg.setItemMeta(meta);

		System.out.println(egg.getItemMeta().getDisplayName());

		player.updateInventory();
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
		if (hasPet(player)) {
			((LivingEntity) pet.getPet()).setHealth(1);
			((LivingEntity) pet.getPet()).damage(1);
			pet.setPet(null);
			for (Player p : Bukkit.getOnlinePlayers())
				stopParticles(p);
		}
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
		for (Player p : Bukkit.getOnlinePlayers())
			stopParticles(p);
	}

	public boolean hasName(Player p) {
		pet = petlist.get(p.getName());
		if (getPetName(p) == null) {
			return false;
		} else
			return true;
	}

	public void setupPet(Player p, EntityType entitytype) {
		if (!hasPet(p)) {
			if (entitytype.equals(EntityType.ZOMBIE)) {
				if (!zombienaming.contains(p.getName()))
					zombienaming.add(p.getName());
			} else if (entitytype.equals(EntityType.OCELOT)) {
				if (!ocelotnaming.contains(p.getName()))
					ocelotnaming.add(p.getName());
			} else if (entitytype.equals(EntityType.MUSHROOM_COW)) {
				if (!mooshroomnaming.contains(p.getName()))
					mooshroomnaming.add(p.getName());
			}
			p.sendMessage(ChatColor.BLUE + "Please enter a " + ChatColor.BOLD
					+ "NAME" + ChatColor.BLUE + " for your pet.");
			p.sendMessage(ChatColor.GRAY
					+ "The name should be between 1-16 characters and cannot contain '[', ']', or blank spaces.");
		} else {
			p.sendMessage(ChatColor.RED + "You must " + ChatColor.BOLD
					+ "DISMISS" + ChatColor.RED
					+ " your pet before you can spawn another one!");
		}
	}

	public void spawnPet(Player player, Location location, PetType type) {
		if (type.equals(PetType.PET_ZOMBIE)) {
			spawnZombiePet(location, player);
		} else if (type.equals(PetType.PET_CAT)) {
			spawnOcelotPet(location, player);
		} else if (type.equals(PetType.PET_MOOSHROOM)) {
			spawnMooshroomPet(location, player);
		}
	}

	public void spawnZombiePet(Location location, Player player) {
		Zombie zombie = location.getWorld().spawn(location, Zombie.class);
		ControllableMob<Zombie> controlledZombie = ControllableMobs.assign(
				zombie, true);
		setPet(player, zombie);
		controlledZombie.getActions().follow(player, false, 2, 1);
		controlledZombie.getEntity().setBaby(true);
		if (Configuration.config.getBoolean("Pets.Zombie.Villager"))
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

	public void spawnMooshroomPet(Location location, final Player player) {
		final MushroomCow mooshroom = location.getWorld().spawn(location,
				MushroomCow.class);
		ControllableMob<MushroomCow> controlledMooshroom = ControllableMobs
				.assign(mooshroom, true);
		setPet(player, mooshroom);
		controlledMooshroom.getProperties().setMovementSpeed(0.35F);
		controlledMooshroom.getActions().follow(player, false, 2, 1);
		controlledMooshroom.getActions().lookAt(player);
		controlledMooshroom.getEntity().setBaby();
		controlledMooshroom.getEntity().setAgeLock(true);
		controlledMooshroom.getEntity().setCanPickupItems(false);
		controlledMooshroom.getEntity().setCustomName(pet.getPetName());
		controlledMooshroom.getEntity().setCustomNameVisible(true);
		doSmoke(location);
		petmooshrooms.put(player, controlledMooshroom);
		int task = 0;
		if (Configuration.config.getBoolean("Pets.Mooshroom.Hearts"))
			for (final Player p : Bukkit.getOnlinePlayers())
				task = Bukkit.getScheduler().scheduleSyncRepeatingTask(
						Pets.instance, new BukkitRunnable() {
							@Override
							public void run() {
								particleapi.sendToPlayer(
										ParticleType.ENCHANTMENT_TABLE, p,
										mooshroom.getLocation(), 0.5F, 0.5F,
										0.5F, 0.2F, 2);
							}
						}, 0L, 5L);
		particles.put(player, task);
	}

	public void spawnCreeperPet(Location location, final Player player) {
		final Creeper creeper = location.getWorld().spawn(location,
				Creeper.class);
		ControllableMob<Creeper> controlledCreeper = ControllableMobs.assign(
				creeper, true);
		setPet(player, creeper);
		controlledCreeper.getProperties().setMovementSpeed(0.35F);
		controlledCreeper.getActions().follow(player, false, 2, 1);
		controlledCreeper.getActions().lookAt(player);
		((Ageable) controlledCreeper.getEntity()).setBaby();
		((Ageable) controlledCreeper.getEntity()).setAgeLock(true);
		controlledCreeper.getEntity().setCanPickupItems(false);
		controlledCreeper.getEntity().setCustomName(pet.getPetName());
		controlledCreeper.getEntity().setCustomNameVisible(true);
		doSmoke(location);
		petcreepers.put(player, controlledCreeper);
	}

	public static void stopParticles(Player player) {
		if (particles.get(player) != null) {
			int task = particles.get(player);
			Bukkit.getScheduler().cancelTask(task);
		}
	}

	public void doSmoke(Location location) {
		location.getWorld().playEffect(location, Effect.SMOKE, 0);
		location.getWorld().playEffect(location, Effect.SMOKE, 2);
		location.getWorld().playEffect(location, Effect.SMOKE, 6);
		location.getWorld().playEffect(location, Effect.SMOKE, 8);
	}

	public void giveEgg(Player player, EggType type) {
		ItemStack egg = new ItemStack(Material.MONSTER_EGG, 1);
		ItemMeta meta = egg.getItemMeta();
		List<String> lore = new ArrayList<String>();
		pet = petlist.get(player.getName());
		lore.add(ChatColor.BLUE + "Right Click: " + ChatColor.GRAY
				+ "Summon/Dismiss Pet");
		lore.add(ChatColor.BLUE + "Left Click: " + ChatColor.GRAY
				+ "Rename Pet");
		lore.add(ChatColor.GRAY + "Permanent Untradeable");
		meta.setLore(lore);
		lore.clear();

		String name = "???";

		if (pet.getPetName() != null) {
			name = pet.getPetName();
		}

		if (type.equals(EggType.ZOMBIE_EGG)) {
			meta.setDisplayName(ChatColor.BLUE + name + ChatColor.GRAY
					+ " - [Baby Zombie]");
			egg.setDurability((short) 54);
		} else if (type.equals(EggType.CAT_EGG)) {
			meta.setDisplayName(ChatColor.BLUE + name + ChatColor.GRAY
					+ " - [Baby Ocelot]");
			egg.setDurability((short) 98);
		} else if (type.equals(EggType.MOOSHROOM_EGG)) {
			meta.setDisplayName(ChatColor.BLUE + name + ChatColor.GRAY
					+ " - [Baby Mooshroom]");
			egg.setDurability((short) 96);
		} else if (type.equals(EggType.CREEPER_EGG)) {
			meta.setDisplayName(ChatColor.BLUE + name + ChatColor.GRAY
					+ " - [Baby Mooshroom]");
			egg.setDurability((short) 50);
		}
		egg.setItemMeta(meta);
		player.getInventory().addItem(egg);
	}
}
