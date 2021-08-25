package net.Boster.Training;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class TrainingFile {
	
	private static HashMap<String, TrainingFile> hash = new HashMap<>();
	
	private String name;
	private File file;
	private FileConfiguration config;
	
	public TrainingFile(String file) {
		hash.put(file, this);
		this.name = file;
	}
	
	public static TrainingFile get(String f) {
		return hash.get(f);
	}
	
	public File getFile() {
		return file;
	}
	
	public FileConfiguration getConfiguration() {
		return config;
	}
	
	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {}
	}
	
	public void reload() {
		config = YamlConfiguration.loadConfiguration(file);
	}
	
	public void create() {
		file = new File(Main.getInstance().getDataFolder() + "/data", name + ".yml");
        if(!file.exists()) {
        	file.getParentFile().mkdirs();
            Main.getInstance().saveResource("data/" + name + ".yml", false);
        }
        
        config = YamlConfiguration.loadConfiguration(file);
	}
	
	public void clear() {
		hash.remove(name);
	}
	
	public static Collection<TrainingFile> getFiles() {
		return hash.values();
	}

}
