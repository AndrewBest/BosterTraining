package net.Boster.Training;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenasMenu {
	
	private Player player;
	public static HashMap<Player, ArenasMenu> user = new HashMap<>();
	private int pages;
	private int pageNumber;
	private int actualArenasFrom;
	
	Utils utils = new Utils();
	
	public ArenasMenu(Player p, int pages) {
		user.put(p, this);
		this.player = p;
		this.pages = pages;
		this.pageNumber = 1;
		this.actualArenasFrom = 0;
	}
	
	public static ArenasMenu getMenu(Player p) {
		return user.get(p);
	}
	
	public int getActualArenasFrom() {
		return actualArenasFrom;
	}
	
	public void newPage() {
		pageNumber = pageNumber + 1;
		actualArenasFrom = actualArenasFrom + 36;
	}
	
	public void pastPage() {
		pageNumber = pageNumber - 1;
		actualArenasFrom = actualArenasFrom - 36;
	}
	
	public int getPageNumber() {
		return pageNumber;
	}
	
	public int getPages() {
		return pages;
	}
	
	public void open() {
		Inventory gui = Bukkit.createInventory(null, 54, Utils.toColor(Main.getInstance().getConfig().getString("ArenasMenu.Title").replace("%page%", Integer.toString(pageNumber))));
		ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 6);
		ItemMeta gm = glass.getItemMeta();
		gm.setDisplayName("\u00a7f ");
		glass.setItemMeta(gm);
		for(int i = 0; i < 9; i++) {
			gui.setItem(i, glass);
		}
		for(int i = 45; i < 54; i++) {
			gui.setItem(i, glass);
		}
		List<String> arenas = Main.getInstance().arenas;
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!player.isOnline()) {
					cancel();
					return;
				}
				if(player.getOpenInventory() == null) {
					cancel();
					return;
				}
				if(player.getOpenInventory().getTitle() == null || player.getOpenInventory().getTitle() != null &&
						!player.getOpenInventory().getTitle().equalsIgnoreCase(Utils.toColor(Main.getInstance().getConfig().getString("ArenasMenu.Title").replace("%page%", Integer.toString(pageNumber))))) {
					cancel();
					return;
				}
				
				for(int i = actualArenasFrom; i < arenas.size(); i++) {
					if(pageNumber == 1) {
						if(i + 9 < 45) {
							gui.setItem(9 + i, craftItemStack(arenas.get(i)));	
						} else continue;
					} else {
						if(i - actualArenasFrom + 9 < 45) {
							gui.setItem(9 + i - actualArenasFrom, craftItemStack(arenas.get(i)));	
						} else continue;
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, Main.getInstance().getConfig().getInt("ArenasMenu.UpdateDelay"));
		if(pageNumber < pages) {
			gui.setItem(53, next());
		}
		if(pageNumber > 1) {
			gui.setItem(45, past());
		}
		ItemStack close = new ItemStack(Material.BARRIER);
		ItemMeta cm = close.getItemMeta();
		cm.setDisplayName(Utils.toColor(Main.getInstance().getConfig().getString("ArenasMenu.Close")));
		close.setItemMeta(cm);
		gui.setItem(49, close);
		player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
		player.openInventory(gui);
	}
	
	private ItemStack next() {
		ItemStack item = utils.getCustomSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2MTg1YjFkNTE5YWRlNTg1ZjE4NGMzNGYzZjNlMjBiYjY0MWRlYjg3OWU4MTM3OGU0ZWFmMjA5Mjg3In19fQ==");
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("\u00a76Next page");
		item.setItemMeta(meta);
		return item;
	}
	private ItemStack past() {
		ItemStack item = utils.getCustomSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWQ3M2NmNjZkMzFiODNjZDhiODY0NGMxNTk1OGMxYjczYzhkOTczMjNiODAxMTcwYzFkODg2NGJiNmE4NDZkIn19fQ==");
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("\u00a76Previous page");
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack craftItemStack(String arena) {
		ItemStack item = new ItemStack(Material.STAINED_CLAY, 1, Arena.getArena(arena) == null ? (byte) 5 : (byte) 14);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(toReplace(Main.getInstance().getConfig().getString("ArenasMenu.Format.Name"), arena));
		ArrayList<String> lore = new ArrayList<>();
		for(String sl : Main.getInstance().getConfig().getStringList("ArenasMenu.Format.Lore")) {
			lore.add(toReplace(sl, arena));
		}
		if(Arena.getArena(arena) == null) {
			for(String sl : Main.getInstance().getConfig().getStringList("ArenasMenu.Format.Lore_addition_able")) {
				lore.add(toReplace(sl, arena));
			}
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private String toReplace(String s, String arena) {
		String st = Arena.getArena(arena) == null ? "Able" : "Unable";
		String status = Main.getInstance().getConfig().getString("ArenasMenu.Statuses." + st);
		s = s.replace("%name%", Main.getInstance().getArenas().getString("arenas." + arena + ".name"))
				.replace("%status%", status);
		s = Utils.toColor(s).replace("%arena%", arena);
		return s;
	}

}
