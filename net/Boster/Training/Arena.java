package net.Boster.Training;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Arena {
	
	private static HashMap<String, Arena> nameToArena = new HashMap<>();
	private static HashMap<Player, Arena> playerHash = new HashMap<>();
	private String name;
	private Player player;
	private Location location;
	private List<Block> blocks = new ArrayList<>();
	
	private int voidY = 0;
	
	private boolean timerStarted = false;
    
    Utils utils = new Utils();
    
    public Arena(String name) {
        nameToArena.put(name, this);
        this.name = name;
        this.voidY = Main.getInstance().getArenas().getInt("arenas." + name + ".voidPos-Y");
    }

    public static Arena getArena(String name) {
        return nameToArena.get(name);
    }
    
    public static Arena getArena(Player p) {
    	return playerHash.get(p);
    }
    
    public void addPlayer(Player p) {
    	player = p;
    	playerHash.put(p, this);
    	location = p.getLocation();
    }
    
    public Location getLastLocation() {
    	return location;
    }
    
    public void stop() {
    	playerHash.remove(player);
    	nameToArena.remove(name);
    }
    
    public void addBlock(Block block) {
    	blocks.add(block);
    }
    
    public void clearBlocks() {
        for(Block block : blocks) {
        	block.setType(Material.AIR);
        }
        blocks.clear();
    }
    
    public boolean isTimerStarted() {
    	return timerStarted;
    }
    
    public void setTimerStarted(boolean b) {
    	timerStarted = b;
    }
    
    public boolean isPlacedBlock(Block b) {
    	return !blocks.isEmpty() && blocks.contains(b);
    }
    
    public String getName() {
    	return name;
    }
    
    public Player getPlayer() {
    	return player;
    }
    
    public Location getLocation(String lc) {
    	String[] split = Main.getInstance().getArenas().getString("arenas." + name + "." + lc).split(", ");
    	World w = Bukkit.getWorld(split[0]);
    	double x = Double.parseDouble(split[1]);
    	double y = Double.parseDouble(split[2]);
    	double z = Double.parseDouble(split[3]);
    	float yaw = (float) Double.parseDouble(split[4]);
    	float pitch = (float) Double.parseDouble(split[5]);
    	return new Location(w, x, y, z, yaw, pitch);
    }
    
    public ItemStack getMaterial() {
    	try {
    		return new ItemStack(Material.valueOf(Main.getInstance().getArenas().getString("arenas." + name + ".BlocksMaterial")), Main.getInstance().getArenas().getInt("arenas." + name + ".Blocks"));
		} catch (NoSuchFieldError e) {
			return new ItemStack(Material.RED_SANDSTONE, 64);
		}
    }
    
    public boolean inLocation(Location loc, String lc) {
    	FileConfiguration arenas = Main.getInstance().getArenas();
		String[] split = arenas.getString("arenas." + name + "." + lc + "Pos-1").split(", ");
		String[] split2 = arenas.getString("arenas." + name + "." + lc + "Pos-2").split(", ");
		int x = Integer.parseInt(split[1]);
		int y = Integer.parseInt(split[2]);
		int z = Integer.parseInt(split[3]);
		int x2 = Integer.parseInt(split2[1]);
		int y2 = Integer.parseInt(split2[2]);
		int z2 = Integer.parseInt(split2[3]);
		if((x < x2 ? loc.getX() >= x && loc.getX() <= x2 : loc.getX() <= x && loc.getX() >= x2) && 
				(y < y2 ? loc.getY() >= y && loc.getY() <= y2 : loc.getY() <= y && loc.getY() >= y2) &&
				(z < z2 ? loc.getZ() >= z && loc.getZ() <= z2 : loc.getZ() <= z && loc.getZ() >= z2) && 
				loc.getWorld().getName().equals(split[0])) {
			return true;
		} else {return false;}
    }
    
    public int voidY() {
    	return voidY;
    }
    
    public static Collection<Arena> getPlayerValues() {
    	return playerHash.values();
    }

}
