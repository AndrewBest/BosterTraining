package net.Boster.Training;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.md_5.bungee.api.ChatColor;

public class Utils {
	
	public static ItemStack LEAVE;
	
	static {
		LEAVE = new ItemStack(Material.INK_SACK, 1, (byte) 1);
		ItemMeta meta = LEAVE.getItemMeta();
		meta.setDisplayName(toColor("&cLeave the game &7(RMB)"));
		ArrayList<String> lore = new ArrayList<>();
		lore.add("\u00a7f ");
		lore.add(toColor("&8Click here to leave the training."));
		meta.setLore(lore);
		LEAVE.setItemMeta(meta);
	}
	
	public static String toColor(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public String toColorAndPrefix(String s) {
		return ChatColor.translateAlternateColorCodes('&', s.replace("%n%", "\n").replace("%prefix%", getPrefix()));
	}
	
	public String getPrefix() {
		return Main.getInstance().getConfig().getString("Settings.Prefix");
	}
	
	public String notPermitted() {
		String s = toColorAndPrefix(Main.getInstance().getConfig().getString("Messages.notPermitted"));
		return s;
	}
	
	public boolean isAdmin(Player p) {
		if(p.hasPermission("boster.training.admin")) {
			return true;
		} else {
			p.sendMessage(notPermitted());
			return false;
		}
	}
	
	public boolean isUser(Player p) {
		if(Main.getInstance().getConfig().getString("Settings.permission_required").contains("true")) {
			if(p.hasPermission("boster.training.user")) {
				return true;
			} else {
				p.sendMessage(notPermitted());
				return false;
			}
		} else {
			return true;
		}
	}
	
	public boolean checkArena(String arena) {
		return Main.getInstance().getArenas().getString("arenas." + arena) != null;
	}
	
	public void saveLocation(Location loc, String where, boolean usePitchAndYaw) {
		double x = usePitchAndYaw ? loc.getX() : (int) loc.getX();
		double y = usePitchAndYaw ? loc.getY() : (int) loc.getY();
		double z = usePitchAndYaw ? loc.getZ() : (int) loc.getZ();
		String world = loc.getWorld().getName();
		String py = "";
		if(usePitchAndYaw) {
			py = ", " + loc.getYaw() + ", " + loc.getPitch();
		}
		
		Main.getInstance().getArenas().set(where, world +  ", " + x + ", " + y + ", " + z + py);
		Main.getInstance().saveArenas();
	}
	
	public void setLobby(Player p) {
		Location loc = p.getLocation();
		int x = (int) loc.getX();
		int y = (int) loc.getY();
		int z = (int) loc.getZ();
		float yaw = (float) loc.getYaw();
		float pitch = (float) loc.getPitch();
		String world = loc.getWorld().getName();
		
		Main.getInstance().getArenas().set("lobby_location", world +  ", " + x + ", " + y + ", " + z + ", " + yaw + ", " + pitch);
		Main.getInstance().saveArenas();
	}
	
	public Location getLobby() {
		try {
			String[] ssd = Main.getInstance().getArenas().getString("lobby_location").split(", ");
			World w = Bukkit.getWorld(ssd[0]);
			double x = Double.parseDouble(ssd[1]);
			double y = Double.parseDouble(ssd[2]);
			double z = Double.parseDouble(ssd[3]);
			float yaw = (float) Double.parseDouble(ssd[4]);
			float pitch = (float) Double.parseDouble(ssd[5]);
			Location loc = new Location(w, x, y, z, yaw, pitch);
			return loc;
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	public String getDefaultBlocksMaterial() {
		String material = Main.getInstance().getConfig().getString("Defaults.BlocksMaterial");
		return material;
	}
	
	public int getDefaultBlocksAmunt() {
		return Main.getInstance().getConfig().getInt("Defaults.Blocks");
	}
	
	public int getDefaultVoidPos() {
		return Main.getInstance().getConfig().getInt("Defaults.voidPos-Y");
	}
	
	public void playSound(Player p, String string) {
		try {
			String[] ss = Main.getInstance().getConfig().getString(string).split(":");
			int si = Integer.parseInt(ss[1]);
			p.playSound(p.getLocation(), Sound.valueOf(ss[0]), 10, si);
		} catch (IllegalArgumentException | NoSuchFieldError | ArrayIndexOutOfBoundsException | NullPointerException e) {
			Bukkit.getConsoleSender().sendMessage(toColor("&4&lERROR&8: &cThere is an error while to play sound! &cString: &f" + string.replace(".", "\u00a79.\u00a7f") + "&c."));
		}
	}
	
	public void playerToArena(Player p, String arena, String message) {
		p.sendMessage(this.toColorAndPrefix(message));
		Arena aren = new Arena(arena);
		aren.addPlayer(p);
		p.getInventory().clear();
		p.teleport(aren.getLocation("spawn"));
		p.setGameMode(GameMode.SURVIVAL);
		p.getInventory().addItem(aren.getMaterial());
		this.playSound(p, "Settings.JoinGameSound");
		SBoard.scoreboardAddPlayer(p, false);
		p.getInventory().setItem(7, LEAVE);
	}
	
	public static void preparePlayer(Player p) {
		Arena arena = Arena.getArena(p);
		arena.clearBlocks();
		p.teleport(arena.getLocation("spawn"));
		p.getInventory().clear();
		p.getInventory().addItem(arena.getMaterial());
		p.getInventory().setItem(7, LEAVE);
	}
	
	public void playerFromArena(Player p) {
		moneyActions(p, EconomyAction.LEAVE_ACTIONS);
		p.sendMessage(this.toColorAndPrefix(Main.getInstance().getConfig().getString("Messages.leave.message")));
		playSound(p, "Messages.leave.sound");
		Arena arena = Arena.getArena(p);
		p.teleport(arena.getLastLocation());
		arena.clearBlocks();
		arena.stop();
		p.getInventory().clear();
		p.setGameMode(GameMode.ADVENTURE);
		p.getInventory().remove(LEAVE);
		SBoard.removeGameScoreboard(p);
	}
	
	public ItemStack getCustomSkull(String value) {
	    GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
	    gameProfile.getProperties().put("textures", new Property("textures", value));

	    ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
	    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
	    Field profileField = null;
	    try {
	        profileField = skullMeta.getClass().getDeclaredField("profile");
	        profileField.setAccessible(true);
	        profileField.set(skullMeta, gameProfile);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    skull.setItemMeta(skullMeta);
	    return skull;
	}
	
	public void moneyActions(Player p, EconomyAction action) {
		if(!VaultSupport.isLoaded || !Main.getInstance().getConfig().getString("Settings.Economy.Enabled").contains("true")) return;
		
		VaultSupport.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), Main.getInstance().getConfig().getDouble(action.section + ".Withdraw"));
		VaultSupport.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), Main.getInstance().getConfig().getDouble(action.section + ".Deposit"));
	}
	
	public static String getTime(int time) {
		int ms = time / 60;
		int ss = time % 60;
		String m = (ms < 10 ? "0" : "") + ms;
		String s = (ss < 10 ? "0" : "") + ss;
		return "\u00a7d" + m + "\u00a77:\u00a7d" + s;
	}
	
	public enum EconomyAction {
		FINISH_ACTIONS("Settings.Economy.FinishActions"),
		LEAVE_ACTIONS("Settings.Economy.FinishActions"),
		DEATH_ACTIONS("Settings.Economy.DeathActions");

		public String section;
		
		EconomyAction(String s) {
			this.section = s;
		}
	}

}
