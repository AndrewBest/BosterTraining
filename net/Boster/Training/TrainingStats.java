package net.Boster.Training;

import java.util.ArrayList;
import java.util.List;

public class TrainingStats {
	
	public boolean hasStat(String player, String stat) {
		return Main.getInstance().getUsers().getString("users." + player + "." + stat) != null;
	}
	
	public int getBestTime(String player) {
		if(Main.getInstance().getUsers().getString("users." + player + ".bestTime") == null) {
			return 0;
		} else {
			return Main.getInstance().getUsers().getInt("users." + player + ".bestTime");
		}
	}
	
	public void setBestTime(String player, int time) {
		Main.getInstance().getUsers().set("users." + player + ".bestTime", time);
		Main.getInstance().saveUsers();
	}
	
	public String getTopPlayer(int i) {
		List<String> players = new ArrayList<>();
		if(Main.getInstance().getUsers().getConfigurationSection("users") != null) {
			for(String player : Main.getInstance().getUsers().getConfigurationSection("users").getKeys(false)) {
				players.add(player);
			}	
		}
		
		if(players.size() < i) {
			return null;
		}
		
		int best = -1;
		String bestPlayer = "";
		int top = 0;
		while(top < i) {
			for(String player : players) {
				if(getBestTime(player) < best || best == -1) {
					best = getBestTime(player);
					bestPlayer = player;
				}
			}
			top++;
			players.remove(bestPlayer);
			best = -1;
		}
		return bestPlayer;
	}

}
