package net.Boster.Training;

import javax.annotation.Nullable;

import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class VaultSupport {
	
	public static boolean isLoaded = false;
	
	private static Economy economy = null;
	
	public static boolean setupEconomy() {
		try {
			if(Main.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {
				return economy == null;
			} else {
				RegisteredServiceProvider<Economy> rsp = Main.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
				economy = rsp.getProvider();
		        isLoaded = true;
		        return economy != null;
			}
		} catch (Exception e) {
			return false;
		}
    }
	
	@Nullable
	public static Economy getEconomy() {
        return economy;
    }
}
