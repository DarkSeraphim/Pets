package me.giinger.pets;

import java.util.ArrayList;
import java.util.List;

import me.giinger.pets.enums.PetType;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class Pet {

	public List<String> sprinting = new ArrayList<String>();

	String petname;
	Entity pet;
	Location loc;

	long one;
	long two;
	long three;
	int particletask;
	float speed = 3.5F;

	PetType type;

	public String getPetName() {
		return petname;
	}

	public void setPetName(String s) {
		this.petname = s;
	}

	public Entity getPet() {
		return pet;
	}

	public void setPet(Entity e) {
		this.pet = e;
	}

	public Location getLocation() {
		return loc;
	}

	public void setLocation(Location l) {
		this.loc = l;
	}
}
