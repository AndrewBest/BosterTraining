package net.Boster.Training;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;

public class Info {
	
	public static void sendInfo(String name, String info, ChatColor ct, ChatColor cp, ChatColor color) {
		Bukkit.getServer().getConsoleSender().sendMessage(ct + "+----------------------------------------------------+");
	    Bukkit.getServer().getConsoleSender().sendMessage(cp + "[" + name + "] >> The plugin has been " + Utils.toColor(info) + cp + "!");
	    Bukkit.getServer().getConsoleSender().sendMessage(cp + "[" + name + "] >> The plugin author: " + color + "Bosternike");
	    Bukkit.getServer().getConsoleSender().sendMessage(cp + "[" + name + "] >> The plugin version: " + color + Main.getInstance().getDescription().getVersion());
	    Bukkit.getServer().getConsoleSender().sendMessage(cp + "[" + name + "] >> The plugin creator: " + color + "Bosternike");
		Bukkit.getServer().getConsoleSender().sendMessage(ct + "+----------------------------------------------------+");
	}

}
