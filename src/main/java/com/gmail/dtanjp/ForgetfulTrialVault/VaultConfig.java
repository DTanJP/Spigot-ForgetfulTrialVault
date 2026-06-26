package com.gmail.dtanjp.ForgetfulTrialVault;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
/**
 * VaultConfig.java
 * 
 * @author David Tan
 */
public class VaultConfig {

	public static String Language = "English";
	
	public static Map<String, String> messages = new HashMap<>();
	
	private static FileConfiguration config = ForgetfulTrialVault.instance.getConfig();
	
	//Milliseconds: How long should players wait before they can reset a vault
	public static long RESET_DELAY = 0; //0 = no delay, allow players to spam keys
	
	//Let the system check and auto unlock the trial vaults for players.
	public static boolean ENABLE_AUTO_UNLOCK = true;
	
	//Once all trial vaults are unlocked, then notify them
	public static boolean ENABLE_PLAYER_NOTIFICATION = true;
	
	public static long NEXT_AUTO_CHECK_TIME = 30L;//30 seconds
	
	//If there is a delay on the vault, should it send a message to the player letting them know how much time left
	public static boolean PRINT_TIME_REMAINING = true;
	
	//If any of the config is updated via commands, update the config file when server closes/reloads
	public static boolean REQUIRES_SAVING = false;
	
	public final String VERSION = "1.0.3.2";
	
	public final static File CONFIG_FILE = new File("./plugins/ForgetfulTrialVault/config.yml");
	
	public final static File VAULT_DATA_FILE = new File("./plugins/ForgetfulTrialVault/Vaults.dat");
	
	/** Returns the plugin logger **/
	public static Logger getLogger() {
		return ForgetfulTrialVault.instance.getLogger();
	}
	
	public static void EXPORT_LOCALE_FILES() {
		ForgetfulTrialVault.instance.saveResource("locale/english.yml", true);
		ForgetfulTrialVault.instance.saveResource("locale/espanol.yml", true);
		ForgetfulTrialVault.instance.saveResource("locale/francais.yml", true);
		ForgetfulTrialVault.instance.saveResource("locale/portuguese.yml", true);
		ForgetfulTrialVault.instance.saveResource("locale/custom.yml", false);
		getLogger().info("English Locale found: "+new File("./plugins/ForgetfulTrialVault/locale/english.yml").exists());
		getLogger().info("Espanol Locale found: "+new File("./plugins/ForgetfulTrialVault/locale/espanol.yml").exists());
		getLogger().info("Francais Locale found: "+new File("./plugins/ForgetfulTrialVault/locale/francais.yml").exists());
		getLogger().info("Portuguese Locale found: "+new File("./plugins/ForgetfulTrialVault/locale/portuguese.yml").exists());
		getLogger().info("Custom Locale found: "+new File("./plugins/ForgetfulTrialVault/locale/custom.yml").exists());
	}
	
	/** Load Locale **/
	public static void LOAD_LANGUAGE() {
		messages.clear();
		getLogger().info("Language ["+Language+"]");
		String localePath = "./plugins/ForgetfulTrialVault/locale/";
		if(Language.equalsIgnoreCase("ESPANOL"))
			localePath += "espanol.yml";
		else if(Language.equalsIgnoreCase("FRANCAIS"))
			localePath += "francais.yml";
		else if(Language.equalsIgnoreCase("PORTUGUESE"))
			localePath += "portuguese.yml";
		else if(Language.equalsIgnoreCase("CUSTOM"))
			localePath += "custom.yml";
		else
			localePath += "english.yml";
		FileConfiguration langfile = YamlConfiguration.loadConfiguration(new File(localePath));
		for(String config : langfile.getKeys(false)) {
			for(String messageKey : langfile.getConfigurationSection(config).getKeys(true)) {
				String message = langfile.getString(config+"."+messageKey);
				messages.put(config+"."+messageKey, message);
			}
		}
	}
	
	/** Load the config, return false if something is unable to load  and true if everything loaded **/
	public static void LOAD_CONFIG() {
		getLogger().info("Loading config...");
		config = YamlConfiguration.loadConfiguration(CONFIG_FILE);
		Language = config.getString("LOCALE", "English");
		ENABLE_AUTO_UNLOCK = config.getBoolean("AUTO_UNLOCK", true);
		ENABLE_PLAYER_NOTIFICATION = config.getBoolean("UNLOCK_NOTIFICATION", true);
		RESET_DELAY = config.getLong("RESET_DELAY", 0);
		PRINT_TIME_REMAINING = config.getBoolean("PRINT_TIME_REMAINING", true);
		
		LOAD_LANGUAGE();
		
		if(RESET_DELAY < 0) RESET_DELAY = 0;
		getLogger().info("AUTO_UNLOCK: "+ENABLE_AUTO_UNLOCK);
		getLogger().info("UNLOCK_NOTIFICATION: "+ENABLE_PLAYER_NOTIFICATION);
		getLogger().info("RESET DELAY: "+(RESET_DELAY > 0 ? VaultUtils.getWaitTimeToString(RESET_DELAY) : "disabled"));
		getLogger().info("PRINT TIME REMAINING: "+PRINT_TIME_REMAINING);
	}
	
	/** Saves the config file **/
	public static void SAVE_CONFIG() {
		if(!REQUIRES_SAVING) return;
		getLogger().info("Saving config...");
		config = ForgetfulTrialVault.instance.getConfig();
		config.setComments("LOCALE", Arrays.asList("English", "Espanol", "Francais", "Portuguese", "Custom"));
		config.set("LOCALE", Language);
		config.setComments("AUTO UNLOCK", Arrays.asList("Enable this to have the trial vaults automatically reset themselves over time"));
		config.set("AUTO_UNLOCK", ENABLE_AUTO_UNLOCK);
		config.setComments("UNLOCK NOTIFICATION", Arrays.asList("Enable this to let players know when the vaults they used has all been auto reset."));
		config.set("UNLOCK_NOTIFICATION", ENABLE_PLAYER_NOTIFICATION);
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