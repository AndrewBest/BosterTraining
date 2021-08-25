package net.Boster.Training;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class SBoard {
	
	private static TrainingStats stats = new TrainingStats();
	
	private static Scoreboard scoreboard(Player p, boolean run) {
		String name = p.getUniqueId().toString().substring(0, 14);
		ScoreboardManager scorem = Bukkit.getScoreboardManager();
		Scoreboard board = scorem.getNewScoreboard();
		Objective objective = board.registerNewObjective(name, "Training");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("\u00a7d\u00a7lTRAINING");
		Score scn = objective.getScore("\u00a7с ");
		scn.setScore(9);
		Team timer = board.registerNewTeam(name + "gt");
		timer.addEntry("\u00a78▪ \u00a7fGame time\u00a77: ");
		timer.setSuffix("\u00a7d00\u00a77:\u00a7d00");
		timer.setPrefix("");
		Score sct = objective.getScore("\u00a78▪ \u00a7fGame time\u00a77: ");
		sct.setScore(8);
		objective.getScore("\u00a76 ").setScore(7);
		objective.getScore("\u00a78▪ \u00a7fYour best time\u00a77: \u00a7d" + Utils.getTime(stats.getBestTime(p.getName()))).setScore(6);
		objective.getScore("\u00a7a ").setScore(5);
		String top1 = stats.getTopPlayer(1);
		if(top1 != null) {
			objective.getScore(Utils.toColor("&8▪ &f1st &6" + top1 + " " + Utils.getTime(stats.getBestTime(top1)))).setScore(4);	
		}
		String top2 = stats.getTopPlayer(2);
		if(top2 != null) {
			objective.getScore(Utils.toColor("&8▪ &f2nd &6" + top2 + " " + Utils.getTime(stats.getBestTime(top2)))).setScore(3);	
		}
		String top3 = stats.getTopPlayer(3);
		if(top3 != null) {
			objective.getScore(Utils.toColor("&8▪ &f3rd &6" + top3 + " " + Utils.getTime(stats.getBestTime(top3)))).setScore(2);	
		}
		objective.getScore("\u00a7d ").setScore(1);
		objective.getScore("   \u00a76bosternike.space").setScore(0);
		if(run == true) {
			new BukkitRunnable() {
				int time = 0;
				int cycle = 0;
				@Override
				public void run() {
					if(!p.isOnline() || p.isOnline() && !Main.getInstance().onArena(p)) {
						cancel();
						return;
					}
					
					if(p.getScoreboard() == null || p.getScoreboard() != board) {
						this.cancel();
						return;
					}
					
					if(cycle < 10) {
						cycle++;
					} else {
						time++;
						cycle = 0;
					}
					int ms = time / 60;
					int ss = time % 60;
					String m = (ms < 10 ? "0" : "") + ms;
					String s = (ss < 10 ? "0" : "") + ss;
					String sm = "&d" + m + "&7:&d" + s;
					if(Arena.getArena(p).inLocation(p.getLocation(), "final")) {
						cancel();
						String finish = Utils.toColor("&d&lTraining &8| &fYou have got to finish for " + sm + "&f!");
						p.sendMessage(finish);
						if(time < stats.getBestTime(p.getName()) || !stats.hasStat(p.getName(), "bestTime")) {
							stats.setBestTime(p.getName(), time);
						}
						Utils.preparePlayer(p);
						Arena.getArena(p).setTimerStarted(false);
						this.cancel();
						scoreboard(p, false);
						return;
					}
					timer.setSuffix(Utils.toColor(sm));
				}
			}.runTaskTimer(Main.getInstance(), 0, 2);
		}
		return board;
	}
	
	public static void scoreboardAddPlayer(Player p, boolean run) {
		if(Main.getInstance().getConfig().getBoolean("Settings.Scoreboard.Enabled", true)) {
			p.setScoreboard(scoreboard(p, run));	
		}
	}
	
	public static void removeGameScoreboard(Player p) {
		if(Main.getInstance().getConfig().getBoolean("Settings.Scoreboard.Enabled", true)) {
			p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}
	}

}
