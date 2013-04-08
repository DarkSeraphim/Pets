package me.giinger.pets;

import org.bukkit.entity.Entity;

public class Pet {

	String petname;
	Entity pet;

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

}
