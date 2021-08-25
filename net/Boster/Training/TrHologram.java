package net.Boster.Training;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class TrHologram {
	
	private static TrainingStats stats = new TrainingStats();
	private static HashMap<String, TrHologram> hash = new HashMap<>();
	
	private String name;
	private Location loc;
	private List<ArmorStand> entities = new ArrayList<>();
	
	public TrHologram(String name, Location loc) {
		hash.put(name, this);
		this.name = name;
		this.loc = loc;
	}
	
	public static void saveHolo(String name, Location loc) {
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		String world = loc.getWorld().getName();
		
		Main.getInstance().getHolograms().set("data." + name + ".loc", world +  ", " + x + ", " + y + ", " + z);
		Main.getInstance().saveHolograms();
	}
	
	public static void loadHolograms() {
		try {
			for(String h : Main.getInstance().getHolograms().getConfigurationSection("data").getKeys(false)) {
				String[] ssd = Main.getInstance().getHolograms().getString("data." + h + ".loc").split(", ");
				World w = Bukkit.getWorld(ssd[0]);
				double x = Double.parseDouble(ssd[1]);
				double y = Double.parseDouble(ssd[2]);
				double z = Double.parseDouble(ssd[3]);
				new TrHologram(h, new Location(w, x, y, z)).spawnHolo();
			}
		} catch (Exception e) {
		}
	}
	
	public static TrHologram get(String s) {
		return hash.get(s);
	}
	
	public void spawnHolo() {
		int i = 0;
		while(i < 12) {
			ArmorStand entity = (ArmorStand) loc.subtract(0, 0.3, 0).getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
			entity.setGravity(false);
			entity.setCanPickupItems(false);
			entity.setCustomNameVisible(false);
			entity.setVisible(false);
			entity.setMarker(true);
			entities.add(entity);
			i++;
		}
		setText();
	}
	
	public void setText() {
		String format = Main.getInstance().getConfig().getString("Settings.TopHologram.Format");
		String[] text = {Main.getInstance().getConfig().getString("Settings.TopHologram.Title"),
				"",
				format.replace("%place%", "1").replace("%player%", "%1p%").replace("%time%", "%1s%"),
				format.replace("%place%", "2").replace("%player%", "%2p%").replace("%time%", "%2s%"),
				format.replace("%place%", "3").replace("%player%", "%3p%").replace("%time%", "%3s%"),
				format.replace("%place%", "4").replace("%player%", "%4p%").replace("%time%", "%4s%"),
				format.replace("%place%", "5").replace("%player%", "%5p%").replace("%time%", "%5s%"),
				format.replace("%place%", "6").replace("%player%", "%6p%").replace("%time%", "%6s%"),
				format.replace("%place%", "7").replace("%player%", "%7p%").replace("%time%", "%7s%"),
				format.replace("%place%", "8").replace("%player%", "%8p%").replace("%time%", "%8s%"),
				format.replace("%place%", "9").replace("%player%", "%9p%").replace("%time%", "%9s%"),
				format.replace("%place%", "10").replace("%player%", "%10p%").replace("%time%", "%10s%")};
		for(int i = 0; i < entities.size(); i++) {
			if(text[i].contains("%")) {
				String[] split = text[i].split("p%");
				String place = split[0].split("%")[1];
				String top = stats.getTopPlayer(Integer.parseInt(place));
				if(top != null) {
					entities.get(i).setCustomName(Utils.toColor(text[i]
							.replace("%" + place + "p%", top)
							.replace("%" + place + "s%", Utils.getTime(stats.getBestTime(top)))));
					entities.get(i).setCustomNameVisible(true);
				}
			} else {
				if(text[i] != null && !text[i].equals("")) {
					entities.get(i).setCustomName(Utils.toColor(text[i]));
					entities.get(i).setCustomNameVisible(true);	
				}
			}
		}
	}
	
	public void remove() {
		for(ArmorStand entity : entities) {
			entity.remove();
		}
	}
	
	public void removeAll() {
		for(ArmorStand entity : entities) {
			entity.remove();
		}
		Main.getInstance().getHolograms().set("data." + name, null);
		Main.getInstance().saveHolograms();
	}
	
	public void clear() {
		hash.remove(name);
	}
	
	public static Collection<TrHologram> getValues() {
		return hash.values();
	}

}
