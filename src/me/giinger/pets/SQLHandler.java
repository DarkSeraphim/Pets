package me.giinger.pets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.giinger.pets.enums.EggType;
import me.giinger.pets.enums.PetType;

import org.bukkit.entity.Player;

public class SQLHandler {
	public volatile static SQLHandler instance;

	private final String url = Configuration.instance.config
			.getString("Pets.Options.MySQL_Host");
	private final String user = Configuration.instance.config
			.getString("Pets.Options.MySQL_Username");
	private final String password = Configuration.instance.config
			.getString("Pets.Options.MySQL_Password");

	public Connection con;
	public PreparedStatement pst;
	public ResultSet rs;

	public void connectSQL() {
		try {
			con = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeSQL() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean hasPet(Player p) {
		try {
			pst = con.prepareStatement("SELECT * FROM Pets WHERE Name='"
					+ p.getName() + "'");
			rs = pst.executeQuery();
			if (rs.isBeforeFirst()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String[] getPets(Player p) {
		try {
			pst = con.prepareStatement("SELECT * FROM Pets WHERE Name='"
					+ p.getName() + "'");
			rs = pst.executeQuery();
			if (!rs.isBeforeFirst()) {
				return null;
			}
			rs.next();
			String list = rs.getString("PetTypes");
			return list.split(",");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void giveEggs(Player p) {
		String[] eggtypes = getPets(p);
		if (eggtypes != null)
			for (String s : eggtypes)
				if (s.equalsIgnoreCase("pet_zombie")) {
					if (!PetsAPI.instance.hasEgg(p, EggType.ZOMBIE_EGG)) {
						PetsAPI.instance.giveEgg(p, EggType.ZOMBIE_EGG);
					}
				} else if (s.equalsIgnoreCase("pet_mooshroom")) {
					if (!PetsAPI.instance.hasEgg(p, EggType.MOOSHROOM_EGG)) {
						PetsAPI.instance.giveEgg(p, EggType.MOOSHROOM_EGG);
					}
				} else if (s.equalsIgnoreCase("pet_cat")) {
					if (!PetsAPI.instance.hasEgg(p, EggType.CAT_EGG)) {
						PetsAPI.instance.giveEgg(p, EggType.CAT_EGG);
					}
				}

	}

	public void setSQLPet(Player p, PetType type) {
		try {
			if (!hasPet(p)) {
				pst = con.prepareStatement("");
				pst = con
						.prepareStatement("INSERT INTO Pets(Name, PetTypes) VALUES('"
								+ p.getName()
								+ "', '"
								+ type.name().toString()
								+ "," + "')");
				pst.execute();
			} else {
				String[] eggtypes = getPets(p);
				for (String s : eggtypes) {
					if (s.equalsIgnoreCase(type.name())) {
						return;
					}
				}
				pst = con.prepareStatement("SELECT * FROM Pets WHERE Name='"
						+ p.getName() + "'");
				rs = pst.executeQuery();
				if (!rs.isBeforeFirst()) {
					return;
				}
				rs.next();
				String s = rs.getString("PetTypes");
				pst = con.prepareStatement("UPDATE Pets SET PetTypes='" + s
						+ type.name() + "," + "' WHERE Name='" + p.getName()
						+ "'");
				pst.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
