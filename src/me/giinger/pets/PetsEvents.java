package me.giinger.pets;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PetsEvents implements Listener {

	PetsAPI api = new PetsAPI();
	List<String> naming = new ArrayList<String>();

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		String msg = e.getMessage();
		Player p = e.getPlayer();

		if (msg.equalsIgnoreCase("pet")) {
			PetsAPI.petlist.put(p.getName(), new Pet());
			e.setCancelled(true);
			naming.add(p.getName());
			p.sendMessage("Type a name for your pet");
		}

		if (naming.contains(p.getName())) {
			e.setCancelled(true);
			api.setPetName(p, msg);
			api.spawnPet(p.getLocation(), p);
			naming.remove(p.getName());
		}
	}
}
