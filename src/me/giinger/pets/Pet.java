package me.giinger.pets;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class Pet {

	String petname;
	Entity pet;
	Location loc;

	long one;
	long two;
	long three;
	int particletask;

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
