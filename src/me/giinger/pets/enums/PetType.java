package me.giinger.pets.enums;

public enum PetType {

	PET_ZOMBIE("zombie"), PET_CAT("cat"), PET_MOOSHROOM("mooshroom"), PET_CREEPER(
			"creeper");

	String petname;

	PetType(String s) {
		this.petname = s;
	}

}
