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
	public Map<String, Integer> particletask = new HashMap<String, Integer>();
	public Map<String, PetType> pettype = new HashMap<String, PetType>();

	public Map<String, ItemStack> mobegg = new HashMap<String, ItemStack>();

	public Map<String, ControllableMob<Zombie>> petzombies;
	public Map<String, ControllableMob<Ocelot>> petocelots;
	public Map<String, ControllableMob<MushroomCow>> petmooshrooms;

	public List<String> cantSpawn = new ArrayList<String>();

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

	@SuppressWarnings("deprecation")
	public void setPetName(Player player, String name) {
		pet = petlist.get(player.getName());
		pet.setPetName(name);

		ItemStack egg = mobegg.get(player.getName());
		ItemMeta meta = egg.getItemMeta();

		String oldname = meta.getDisplayName().replaceAll(".+-.", "");
		String newname = ChatColor.BLUE + pet.getPetName() + ""
				+ ChatColor.GRAY + " - " + oldname;

		meta.setDisplayName(newname);
		player.getInventory().remove(egg);
		egg.setItemMeta(meta);

		player.getInventory().addItem(egg);
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

	public String getPetName(ItemStack inhand) {
		ItemMeta meta = inhand.getItemMeta();
		String name = meta.getDisplayName();

		name = name.replaceAll(".-.+", "");
		name = name.replaceAll(String.valueOf(ChatColor.BLUE), "");
		name = name.replaceAll(String.valueOf(ChatColor.GRAY), "");
		return name;
	}

	public boolean hasName(Player p) {
		if (getPetName(p.getItemInHand()).equalsIgnoreCase("???"))
			return false;
		else {
			return true;
		}
	}

	public void killPet(Player player) {
		pet = petlist.get(player.getName());
		if (hasPet(player)) {
			((LivingEntity) pet.getPet()).setHealth(1);
			((LivingEntity) pet.getPet()).damage(1);
			pet.setPet(null);
			pet.setPetName(null);
			pet.type = null;
			stopParticles(player);
			mobegg.remove(player.getName());
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

	public void setupPet(Player p, EntityType entitytype) {
		if (!hasPet(p)) {
			if (entitytype.equals(EntityType.ZOMBIE)) {
				if (!pettype.containsKey(p.getName()))
					pettype.put(p.getName(), PetType.PET_ZOMBIE);
			} else if (entitytype.equals(EntityType.OCELOT)) {
				if (!pettype.containsKey(p.getName()))
					pettype.put(p.getName(), PetType.PET_CAT);
			} else if (entitytype.equals(EntityType.MUSHROOM_COW)) {
				if (!pettype.containsKey(p.getName()))
					pettype.put(p.getName(), PetType.PET_MOOSHROOM);
			}
			p.sendMessage(ChatColor.BLUE + "Please enter a " + ChatColor.BOLD
					+ "NAME" + ChatColor.BLUE + " for your pet.");
			p.sendMessage(ChatColor.GRAY
					+ "The name must be between 3-16 characters and must be alphanumeric, and can contain !,#,$,_ or space.");
		} else {
			p.sendMessage(ChatColor.RED + "You must " + ChatColor.BOLD
					+ "DISMISS" + ChatColor.RED
					+ " your pet before you can spawn another one!");
		}
	}

	public void spawnPet(Player player, Location location, PetType type) {
		pet = petlist.get(player.getName());
		if (type.equals(PetType.PET_ZOMBIE)) {
			spawnZombiePet(location, player);
			pet.type = PetType.PET_ZOMBIE;
		} else if (type.equals(PetType.PET_CAT)) {
			spawnOcelotPet(location, player);
			pet.type = PetType.PET_CAT;
		} else if (type.equals(PetType.PET_MOOSHROOM)) {
			spawnMooshroomPet(location, player);
			pet.type = PetType.PET_MOOSHROOM;
		}
		cantSpawn.add(player.getName());
		doTimer(player);
	}

	private void spawnZombiePet(Location location, Player player) {
		Zombie zombie = location.getWorld().spawn(location, Zombie.class);
		ControllableMob<Zombie> controlledZombie = ControllableMobs.assign(
				zombie, true);
		setPet(player, zombie);
		controlledZombie.getActions().follow(player, false, 2, 1);
		controlledZombie.getEntity().setBaby(true);
		if (Configuration.instance.config.getBoolean("Pets.Zombie.Villager"))
			controlledZombie.getEntity().setVillager(true);
		controlledZombie.getProperties().setMovementSpeed(0.35F);
		controlledZombie.getEntity().setCanPickupItems(false);
		controlledZombie.getEntity().setCustomName(
				getPetName(mobegg.get(player.getName())));
		controlledZombie.getEntity().setCustomNameVisible(true);
		doSmoke(location);
		petzombies.put(player.getName(), controlledZombie);
	}

	private void spawnOcelotPet(Location location, Player player) {
		Ocelot ocelot = location.getWorld().spawn(location, Ocelot.class);
		ControllableMob<Ocelot> controlledOcelot = ControllableMobs.assign(
				ocelot, true);
		setPet(player, ocelot);
		controlledOcelot.getEntity().setTamed(true);
		controlledOcelot.getEntity().setOwner(player);
		controlledOcelot.getActions().follow(player, true, 2, 1);
		controlledOcelot.getActions().lookAt(player);
		controlledOcelot.getProperties().setMovementSpeed(0.35F);
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
		controlledOcelot.getEntity().setCustomName(
				getPetName(mobegg.get(player.getName())));
		controlledOcelot.getEntity().setCustomNameVisible(true);
		doSmoke(location);
		petocelots.put(player.getName(), controlledOcelot);
	}

	private void spawnMooshroomPet(Location location, Player player) {
		final MushroomCow mooshroom = location.getWorld().spawn(location,
				MushroomCow.class);
		ControllableMob<MushroomCow> controlledMooshroom = ControllableMobs
				.assign(mooshroom, true);
		setPet(player, mooshroom);
		controlledMooshroom.getActions().follow(player, false, 2, 1);
		controlledMooshroom.getActions().lookAt(player);
		controlledMooshroom.getProperties().setMovementSpeed(0.35F);
		controlledMooshroom.getEntity().setBaby();
		controlledMooshroom.getEntity().setAgeLock(true);
		controlledMooshroom.getEntity().setCanPickupItems(false);
		controlledMooshroom.getEntity().setCustomName(
				getPetName(mobegg.get(player.getName())));
		controlledMooshroom.getEntity().setCustomNameVisible(true);
		doSmoke(location);
		petmooshrooms.put(player.getName(), controlledMooshroom);
		int task = 0;
		if (Configuration.instance.config.getBoolean("Pets.Mooshroom.Hearts"))
			task = Bukkit.getScheduler().scheduleSyncRepeatingTask(
					Pets.instance, new BukkitRunnable() {
						@Override
						public void run() {
							for (final Player p : Bukkit.getOnlinePlayers()) {
								particleapi.sendToPlayer(ParticleType.HEART, p,
										mooshroom.getLocation(), 0.5F, 0.5F,
										0.5F, 0.2F, 2);
							}
						}
					}, 0L, 5L);
		PetsAPI.instance.particletask.put(player.getName(), task);
	}

	public static void stopParticles(Player player) {
		if (PetsAPI.instance.particletask.get(player.getName()) != null) {
			int task = PetsAPI.instance.particletask.get(player.getName());
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
		lore.add(ChatColor.LIGHT_PURPLE + "Soulbound");
		meta.setLore(lore);
		lore.clear();

		String name = "???";

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
		}
		egg.setItemMeta(meta);
		player.getInventory().addItem(egg);
	}

	public boolean hasEgg(Player p, EggType type) {
		for (ItemStack i : p.getInventory().getContents()) {
			if (i != null) {
				if (i.hasItemMeta()) {
					ItemMeta meta = i.getItemMeta();
					if (i.getType() == Material.MONSTER_EGG) {
						CharSequence zombie = "Zombie";
						CharSequence ocelot = "Ocelot";
						CharSequence mooshroom = "Mooshroom";
						if (type == EggType.ZOMBIE_EGG) {
							if (meta.getDisplayName().contains(zombie)) {
								return true;
							}
						} else if (type == EggType.CAT_EGG) {
							if (meta.getDisplayName().contains(ocelot)) {
								return true;
							}
						} else if (type == EggType.MOOSHROOM_EGG) {
							if (meta.getDisplayName().contains(mooshroom)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private void doTimer(final Player p) {
		pet = petlist.get(p.getName());
		long one = System.currentTimeMillis() + 1000;
		long two = System.currentTimeMillis() + 2000;
		long three = System.currentTimeMillis() + 3000;
		pet.one = one;
		pet.two = two;
		pet.three = three;

		Pets.instance.getServer().getScheduler()
				.scheduleSyncDelayedTask(Pets.instance, new BukkitRunnable() {
					@Override
					public void run() {
						cantSpawn.remove(p.getName());
					}
				}, 60L);
	}
}
