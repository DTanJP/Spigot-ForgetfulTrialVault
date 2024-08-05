package com.gmail.dtanjp.ForgetfulTrialVault;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
/**
 * VaultConfig.java
 * 
 * @author David Tan
 */
public class VaultConfig {

	private static FileConfiguration config = ForgetfulTrialVault.instance.getConfig();
	
	//Milliseconds: How long should players wait before they can reset a vault
	public static long RESET_DELAY = 5000; //0 = no delay, allow players to spam keys
	
	//If there is a delay on the vault, should it send a message to the player letting them know how much time left
	public static boolean PRINT_TIME_REMAINING = true;
	
	//If any of the config is updated via commands, update the config file when server closes/reloads
	public static boolean REQUIRES_SAVING = false;
	
	public final String VERSION = "1.0.1";
	
	public final static File CONFIG_FILE = new File("./plugins/ForgetfulTrialVault/config.yml");
	
	public final static File VAULT_DATA_FILE = new File("./plugins/ForgetfulTrialVault/Vaults.dat");
	
	/** Returns the plugin logger **/
	public static Logger getLogger() {
		return ForgetfulTrialVault.instance.getLogger();
	}
	
	/** Load the config, return false if something is unable to load  and true if everything loaded **/
	public static void LOAD_CONFIG() {
		getLogger().info("Loading config...");
		config = YamlConfiguration.loadConfiguration(CONFIG_FILE);
		RESET_DELAY = config.getLong("RESET_DELAY", 0);
		PRINT_TIME_REMAINING = config.getBoolean("PRINT_TIME_REMAINING", true);
		
		if(RESET_DELAY < 0) RESET_DELAY = 0;
		
		getLogger().info("RESET DELAY: "+(RESET_DELAY > 0 ? VaultUtils.getWaitTimeToString(RESET_DELAY) : "disabled"));
		getLogger().info("PRINT TIME REMAINING: "+PRINT_TIME_REMAINING);
	}
	
	/** Saves the config file **/
	public static void SAVE_CONFIG() {
		if(!REQUIRES_SAVING) return;
		getLogger().info("Saving config...");
		config = ForgetfulTrialVault.instance.getConfig();
		getLogger().info("RESET DELAY: "+(RESET_DELAY > 0 ? VaultUtils.getWaitTimeToString(RESET_DELAY) : "disabled"));
		getLogger().info("PRINT TIME REMAINING: "+PRINT_TIME_REMAINING);
		config.setComments("RESET_DELAY", Arrays.asList("Milliseconds: How long should players wait before they can reset a vault", "0 = no delay, allow players to spam keys"));
		config.set("RESET_DELAY", RESET_DELAY);
		
		config.setComments("PRINT_TIME_REMAINING", Arrays.asList("If there is a delay on the vault, should it send a message to the player letting them know how much time left"));
		config.set("PRINT_TIME_REMAINING", PRINT_TIME_REMAINING);
		try {
			config.save(CONFIG_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}