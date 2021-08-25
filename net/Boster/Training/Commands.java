package net.Boster.Training;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
	
	Main plugin;
	Utils utils = new Utils();
	FileConfiguration config = Main.getInstance().getConfig();

	public Commands(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Utils.toColor("&dBosterTraining &8| &cYou must be a player tto perform this command!"));
			return true;
		}
		
		Player p = (Player) sender;
		if(!utils.isUser(p)) return true;
		
		if(args.length == 0) {
			sender.sendMessage(utils.toColorAndPrefix(config.getString("Messages.nullArguments")));
			return true;
		}
		
		//HELP
		if(args.length >= 1 && args[0].equalsIgnoreCase("help")) {
			if(!sender.hasPermission("boster.training.help") && !sender.hasPermission("boster.training.help.admin")) {
				sender.sendMessage(utils.notPermitted());
				return true;
			}
			
			if(sender.hasPermission("boster.training.help") && !sender.hasPermission("boster.training.help.admin")) {
				for(String s : config.getStringList("Messages.help_user")) {
					s = s.replace("%prefix%", utils.getPrefix());
					sender.sendMessage(Utils.toColor(s.toString()));
				}
				return true;
			} else if(sender.hasPermission("boster.training.help") && sender.hasPermission("boster.training.help.admin") ||
					!sender.hasPermission("boster.training.help") && sender.hasPermission("boster.training.help.admin")) {
				for(String s : config.getStringList("Messages.help_admin")) {
					s = s.replace("%prefix%", utils.getPrefix());
					sender.sendMessage(Utils.toColor(s.toString()));
				}
				return true;
			}
			return true;
		//RELOAD
		} else if(args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
			if(!utils.isAdmin(p)) return true;
			
			plugin.reloadConfig();
			plugin.reloadArenas();
			plugin.reloadUsers();
			String s = config.getString("Messages.reload");
			sender.sendMessage(utils.toColorAndPrefix(s));
			return true;
		//CREATE
		} else if(args.length == 1 && args[0].equalsIgnoreCase("create")) {
			if(!utils.isAdmin((Player) sender)) {
				return true;
			}
			
			String s = config.getString("Messages.admin.create.usage");
			sender.sendMessage(utils.toColorAndPrefix(s));
			return true;
		} else if(args.length >= 2 && args[0].equalsIgnoreCase("create")) {
			if(!utils.isAdmin(p)) return true;
			
			if(utils.checkArena(args[1])) {
				sender.sendMessage(utils.toColorAndPrefix(Main.getInstance().getConfig().getString("Messages.admin.arenaExists").replace("%arg%", args[1])));
				return true;
			}
			
			plugin.getArenas().set("arenas." + args[1] + ".name", "&7None");
			plugin.getArenas().set("arenas." + args[1] + ".spawn", "none");
			plugin.getArenas().set("arenas." + args[1] + ".finalPos-1", "none");
			plugin.getArenas().set("arenas." + args[1] + ".finalPos-2", "none");
			plugin.getArenas().set("arenas." + args[1] + ".regionPos-1", "none");
			plugin.getArenas().set("arenas." + args[1] + ".regionPos-2", "none");
			plugin.getArenas().set("arenas." + args[1] + ".BlocksMaterial", utils.getDefaultBlocksMaterial());
			plugin.getArenas().set("arenas." + args[1] + ".Blocks", utils.getDefaultBlocksAmunt());
			plugin.getArenas().set("arenas." + args[1] + ".voidPos-Y", utils.getDefaultVoidPos());
			plugin.saveArenas();
			String s = config.getString("Messages.admin.create.success").replace("%name%", args[1]);
			sender.sendMessage(utils.toColorAndPrefix(s));
			return true;
		//DELETE
		} else if(args.length == 1 && args[0].equalsIgnoreCase("delete")) {
			if(!utils.isAdmin(p)) return true;
			
			String s = config.getString("Messages.admin.delete.usage");
			sender.sendMessage(utils.toColorAndPrefix(s));
			return true;
		} else if(args.length >= 2 && args[0].equalsIgnoreCase("delete")) {
			if(!utils.isAdmin(p)) return true;
			
			if(!utils.checkArena(args[1])) {
				sender.sendMessage(utils.toColorAndPrefix(Main.getInstance().getConfig().getString("Messages.admin.arenaNull").replace("%name%", args[1])));
				return true;
			}
			
			plugin.getArenas().set("arenas." + args[1], null);
			plugin.saveArenas();
			String s = config.getString("Messages.admin.delete.success");
			s = utils.toColorAndPrefix(s);
			s = s.replace("%arena%", args[1]);
			s = s.replace("%name%", args[1]);
			sender.sendMessage(s);
			if(plugin.getArenas().getStringList("arenas_list").contains(args[1])) {
				List<String> list = plugin.getArenas().getStringList("arenas_list");
				list.remove(args[1]);
				plugin.getArenas().set("arenas_list", list);
				plugin.saveArenas();
				plugin.setupAbleArenas();
			}
			return true;
		//SETNAME
		} else if(args.length < 3 && args[0].equalsIgnoreCase("setname")) {
			if(!utils.isAdmin(p)) return true;
			
			String s = config.getString("Messages.admin.setName.usage");
			sender.sendMessage(utils.toColorAndPrefix(s));
			return true;
		} else if(args.length >= 3 && args[0].equalsIgnoreCase("setname")) {
			if(!utils.isAdmin(p)) return true;
			
			if(!utils.checkArena(args[2])) {
				sender.sendMessage(utils.toColorAndPrefix(Main.getInstance().getConfig().getString("Messages.admin.arenaNull").replace("%name%", args[2])));
				return true;
			}
			
			plugin.getArenas().set("arenas." + args[2] + ".name", args[1]);
			plugin.saveArenas();
			String s = config.getString("Messages.admin.setName.success");
			s = s.replace("%name%", args[1]);
			s = s.replace("%arena%", args[2]);
			sender.sendMessage(utils.toColorAndPrefix(s));
			return true;
		//SETSPAWN
		} else if(args.length < 2 && args[0].equalsIgnoreCase("setspawn")) {
			if(!utils.isAdmin(p)) return true;
			
			String s = config.getString("Messages.admin.setSpawn.usage");
			sender.sendMessage(utils.toColorAndPrefix(s));
			return true;
		} else if(args.length >= 2 && args[0].equalsIgnoreCase("setspawn")) {
			if(!utils.isAdmin(p)) return true;
			
			if(!utils.checkArena(args[1])) {
				sender.sendMessage(utils.toColorAndPrefix(Main.getInstance().getConfig().getString("Messages.admin.arenaNull").replace("%name%", args[1])));
				return true;
			}
			
			utils.saveLocation(p.getLocation(), "arenas." + args[1] + ".spawn", true);
			Main.getInstance().setupAbleArenas();
			String s = config.getString("Messages.admin.setSpawn.success");
			s = s.replace("%team%", args[1]);
			s = s.replace("%arena%", args[1]);
			sender.sendMessage(utils.toColorAndPrefix(s));
			return true;
		//SETPOS
		} else if(args.length < 2 && (args[0].equalsIgnoreCase("setpos-1") || args[0].equalsIgnoreCase("setpos-2"))) {
			if(!utils.isAdmin(p)) return true;
			
			String s = config.getString("Messages.admin.setPos.usage");
			sender.sendMessage(utils.toColorAndPrefix(s));
			return true;
		} else if(args.length >= 2 && (args[0].equalsIgnoreCase("setpos-1") || args[0].equalsIgnoreCase("setpos-2"))) {
			if(!utils.isAdmin(p)) return true;
			
			if(!utils.checkArena(args[1])) {
				sender.sendMessage(utils.toColorAndPrefix(Main.getInstance().getConfig().getString("Messages.admin.arenaNull").replace("%name%", args[1])));
				return true;
			}
			
			utils.saveLocation(p.getLocation(), "arenas." + args[1] + (args[0].equalsIgnoreCase("setpos-1") ? "finalPos-1" : "finalPos-2"), false);
			Main.getInstance().setupAbleArenas();
			String s = config.getString("Messages.admin.setPos.success");
			s = s.replace("%team%", args[1]);
			s = s.replace("%arena%", args[1]);
			s = s.replace("%pos%", args[0].equalsIgnoreCase("setpos-1") ? "1" : "2");
			sender.sendMessage(utils.toColorAndPrefix(s));
			return true;
		//SETREGION
		} else if(args.length < 2 && (args[0].equalsIgnoreCase("setregion-1") || args[0].equalsIgnoreCase("setregion-2"))) {
			if(!utils.isAdmin(p)) return true;
			
			String s = config.getString("Messages.admin.setRegion.usage");
			sender.sendMessage(utils.toColorAndPrefix(s));
			return true;
		} else if(args.length >= 2 && (args[0].equalsIgnoreCase("setregion-1") || args[0].equalsIgnoreCase("setregion-2"))) {
			if(!utils.isAdmin(p)) return true;
			
			if(!utils.checkArena(args[1])) {
				sender.sendMessage(utils.toColorAndPrefix(Main.getInstance().getConfig().getString("Messages.admin.arenaNull").replace("%name%", args[1])));
				return true;
			}
			
			utils.saveLocation(p.getLocation(), "arenas." + args[1] + (args[0].equalsIgnoreCase("setregion-1") ? "regionPos-1" : "regionPos-2"), false);
			Main.getInstance().setupAbleArenas();
			String s = config.getString("Messages.admin.setRegion.success");
			s = s.replace("%team%", args[1]);
			s = s.replace("%arena%", args[1]);
			s = s.replace("%pos%", args[0].equalsIgnoreCase("setregion-1") ? "1" : "2");
			sender.sendMessage(utils.toColorAndPrefix(s));
			return true;
		//SETLOBBY
		} else if(args.length >= 1 && args[0].equalsIgnoreCase("setlobby")) {
			if(!utils.isAdmin(p)) return true;
			
			utils.setLobby(p);
			sender.sendMessage(utils.toColorAndPrefix(config.getString("Messages.setLobby")));
			return true;
		//PLAY
		} else if(args.length >= 1 && args[0].equalsIgnoreCase("play")) {
			if(plugin.onArena(p)) {
				String s = config.getString("Messages.isInGame");
				sender.sendMessage(utils.toColorAndPrefix(s));
				return true;
			}
			
			String arena = getAbleArena();
			if(arena != null) {
				utils.playerToArena(p, arena, plugin.getConfig().getString("Messages.play.success"));
			} else {
				String s = config.getString("Messages.play.noArenas");
				p.sendMessage(utils.toColorAndPrefix(s));
			}
			return true;
		//JOIN
		} else if(args.length == 1 && args[0].equalsIgnoreCase("join")) {
			String s = config.getString("Messages.join.usage");
			sender.sendMessage(utils.toColorAndPrefix(s));
			return true;
		} else if(args.length >= 2 && args[0].equalsIgnoreCase("join")) {
			if(plugin.onArena(p)) {
				String s = config.getString("Messages.isInGame");
				sender.sendMessage(utils.toColorAndPrefix(s));
				return true;
			}
			
			if(!utils.checkArena(args[1])) {
				String s = config.getString("Messages.admin.arenaNull").replace("%arena%", args[1]);
				p.sendMessage(utils.toColorAndPrefix(s));
			}
			
			if(Arena.getArena(args[1]) == null) {
				utils.playerToArena(p, args[1], plugin.getConfig().getString("Messages.join.success").replace("%arena%", args[1]).replace("%arena_name%", plugin.getArenas().getString("arenas." + args[1] + ".name")));
			} else {
				String s = config.getString("Messages.join.arenaIsUsed").replace("%arena%", args[1]);
				p.sendMessage(utils.toColorAndPrefix(s));
			}
			return true;
		//ARENAS
		} else if(args.length >= 1 && args[0].equalsIgnoreCase("arenas")) {
			if(plugin.onArena(p)) {
				String s = config.getString("Messages.isInGame");
				sender.sendMessage(utils.toColorAndPrefix(s));
				return true;
			}
			
			ArenasMenu menu = new ArenasMenu(p, arenasPages());
			menu.open();
			p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
			return true;
		//LEAVE
		} else if(args.length >= 1 && args[0].equalsIgnoreCase("leave")) {
			if(!plugin.onArena(p)) {
				String s = config.getString("Messages.notInGame");
				sender.sendMessage(utils.toColorAndPrefix(s));
				return true;
			}
			
			utils.playerFromArena(p);
			return true;
		//HOLO
		} else if(args.length < 2 && args[0].equalsIgnoreCase("spawnholo")) {
			if(!utils.isAdmin(p)) return true;
			
			p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.admin.spawnHolo.usage")));
			return true;
		} else if(args.length >= 2 && args[0].equalsIgnoreCase("spawnholo")) {
			if(!utils.isAdmin(p)) return true;
			
			if(TrHologram.get(args[1]) != null) {
				p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.admin.spawnHolo.alreadyExists").replace("%name%", args[1])));
				return true;
			}
			
			Location loc = p.getLocation().clone().add(0, 1, 0);
			TrHologram.saveHolo(args[1], loc);
			TrHologram t = new TrHologram(args[1], loc);
			t.spawnHolo();
			p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.admin.spawnHolo.success").replace("%name%", args[1])));
			return true;
		} else if(args.length < 2 && args[0].equalsIgnoreCase("deleteholo")) {
			if(!utils.isAdmin(p)) return true;
			
			p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.admin.deleteHolo.usage")));
			return true;
		} else if(args.length >= 2 && args[0].equalsIgnoreCase("deleteholo")) {
			if(!utils.isAdmin(p)) return true;
			
			if(TrHologram.get(args[1]) == null) {
				p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.admin.spawnHolo.doesNotExist").replace("%name%", args[1])));
				return true;
			}
			
			TrHologram h = TrHologram.get(args[1]);
			h.removeAll();
			h.clear();
			p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.admin.deleteHolo.success").replace("%name%", args[1])));
			return true;
		} else {
			String syntax = "";
			for(int i=0; i<args.length;i++){
				syntax+=args[i]+" ";
			}
			String s = plugin.getConfig().getString("Messages.wrongSyntax").replace("%syntax%", syntax);
			sender.sendMessage(utils.toColorAndPrefix(s));
			return true;
		}
	}
	
	private int arenasPages() {
		List<String> arenas = plugin.getArenas().getStringList("arenas_list");
		int pages = 0;
		if(arenas.size() <= 36) {
			pages = 1;
		} else {
			int sum = arenas.size() / 36;
			pages = sum;
			if(arenas.size() > 36 * sum) {
				pages++;
			}
		}
		return pages;
	}
	
	private String getAbleArena() {
		List<String> arenas = new ArrayList<>();
		for(String ar : plugin.arenas) {
			if(plugin.ArenaHasLocs(ar) && Arena.getArena(ar) == null) {
				arenas.add(ar);
			}
		}
		if(arenas.isEmpty()) {
			return null;	
		} else {
			return arenas.get((int) Math.floor(Math.random() * arenas.size()));
		}
	}

}
