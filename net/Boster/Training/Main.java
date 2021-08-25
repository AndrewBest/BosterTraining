package net.Boster.Training;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {
	
	private static Main instance;
    
    Utils utils = new Utils();
    
    public List<String> arenas = new ArrayList<>();
	
	@Override
    public void onEnable() {
		instance = this;
    	
    	File config = new File(getDataFolder() + File.separator + "config.yml");
		if(!config.exists()) {
			getConfig().options().copyDefaults(true);
			saveDefaultConfig();
		}
		
		createArenas();
		createUsers();
		createHolograms();
		setupAbleArenas();
		VaultSupport.setupEconomy();
		
		TrHologram.loadHolograms();
		
		Bukkit.getPluginManager().registerEvents(new Events(this), this);
		getCommand("training").setExecutor(new Commands(this));
		
		runHologramsUpdate();
		
		Info.sendInfo("BosterTraining", "&aenabled", ChatColor.GREEN, ChatColor.YELLOW, ChatColor.AQUA);
	}
	
	@Override
	public void onDisable() {
		stopArenas();
		Info.sendInfo("BosterTraining", "&cdisabled", ChatColor.GREEN, ChatColor.YELLOW, ChatColor.AQUA);
		for(TrHologram h : TrHologram.getValues()) {
			h.remove();
			h.clear();
		}
	}
	
	public void stopArenas() {
		try {
			for(String arena : getArenas().getStringList("arenas_list")) {
				if(Arena.getArena(arena) != null) {Arena.getArena(arena).clearBlocks();}
			}
			for(Arena arena : Arena.getPlayerValues()) {
				Player p = arena.getPlayer();
				p.teleport(utils.getLobby());
				p.getInventory().clear();
				p.setGameMode(GameMode.ADVENTURE);
			}
		} catch (NullPointerException e) {}
	}
	
	public void runHologramsUpdate() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(TrHologram.getValues().size() == 0) return;
				
				for(TrHologram h : TrHologram.getValues()) {
					h.setText();
				}
			}
		}.runTaskTimer(this, 600, 600);
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public FileConfiguration getArenas() {
        return TrainingFile.get("arenas").getConfiguration();
    }
	public void saveArenas() {
		TrainingFile.get("arenas").save();
	}
	public void reloadArenas() {
		TrainingFile.get("arenas").reload();
	}
	private void createArenas() {
		new TrainingFile("arenas").create();
    }
	
	public FileConfiguration getUsers() {
        return TrainingFile.get("users").getConfiguration();
    }
	public void saveUsers() {
		TrainingFile.get("users").save();
	}
	public void reloadUsers() {
		TrainingFile.get("users").reload();
	}
	private void createUsers() {
		new TrainingFile("users").create();
    }
	
	public FileConfiguration getHolograms() {
        return TrainingFile.get("holograms").getConfiguration();
    }
	public void saveHolograms() {
		TrainingFile.get("holograms").save();
	}
	private void createHolograms() {
		new TrainingFile("holograms").create();
    }
	
	public boolean ArenaHasLocs(String s) {
		if(getArenas().getString("arenas." + s + ".spawn") != null && !getArenas().getString("arenas." + s + ".spawn").contains("none") &&
				getArenas().getString("arenas." + s + ".finalPos-1") != null && !getArenas().getString("arenas." + s + ".finalPos-1").contains("none") &&
				getArenas().getString("arenas." + s + ".finalPos-2") != null && !getArenas().getString("arenas." + s + ".finalPos-2").contains("none") &&
				getArenas().getString("arenas." + s + ".regionPos-1") != null && !getArenas().getString("arenas." + s + ".regionPos-1").contains("none") &&
				getArenas().getString("arenas." + s + ".regionPos-2") != null && !getArenas().getString("arenas." + s + ".regionPos-2").contains("none")) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setupAbleArenas() {
		try {
			if(!arenas.isEmpty()) {
				arenas.clear();	
			}
			for(String s : getArenas().getConfigurationSection("arenas").getKeys(false)) {
				if(ArenaHasLocs(s)) {
					arenas.add(s);
				}
			}
		} catch (NullPointerException e) {}
	}
	
	public boolean onArena(Player p) {
		return Arena.getArena(p) != null;
	}

}
