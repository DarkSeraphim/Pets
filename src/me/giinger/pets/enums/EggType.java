package me.giinger.pets.enums;

public enum EggType {

	ZOMBIE_EGG("zombie"), MOOSHROOM_EGG("mooshroom"), CAT_EGG("cat"), CREEPER_EGG(
			"creeper");

	public String eggtype;

	EggType(String type) {
		this.eggtype = type;
	}

}
