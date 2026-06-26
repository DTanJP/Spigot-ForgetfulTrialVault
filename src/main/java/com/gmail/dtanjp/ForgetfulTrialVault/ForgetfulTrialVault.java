package com.gmail.dtanjp.ForgetfulTrialVault;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Vault.State;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 
 * ForgetfulTrialVault.java
 * 
 * @author David Tan
 * @description: Spigot plugin to reset a Trial Chamber Vault for reuse.
 * 
 */
public class ForgetfulTrialVault extends JavaPlugin implements Listener {

	public static ForgetfulTrialVault instance;
	
	public static FileConfiguration VAULT_DATA;
	
	@Override
	public void onEnable() {
		instance = this;
		this.getServer().getPluginManager().registerEvents(instance, instance);
		if(!this.getDataFolder().exists())
			this.getDataFolder().mkdirs();
		
		VaultConfig.EXPORT_LOCALE_FILES();
		
		//Generate config file if not found
		if(!VaultConfig.CONFIG_FILE.exists()) {
			instance.getLogger().info(VaultConfig.messages.get("GENERATION_MSG.CONFIG_FILE"));
			saveDefaultConfig();
		} else
			VaultConfig.LOAD_CONFIG();
		
		//Generate save file
		if(!VaultConfig.VAULT_DATA_FILE.exists()) {
			instance.getLogger().info(VaultConfig.messages.get("GENERATION_MSG.SAVE_FILE"));
			try {
				VaultConfig.VAULT_DATA_FILE.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				instance.getLogger().warning(VaultConfig.messages.get("GENERATION_MSG.SAVE_FILE_ERROR"));
			}
		}
		TrialVault.LOADFILE();
		getLogger().info("["+VaultConfig.messages.get("GENERATION_MSG.LOADED")+" "+TrialVault.VAULTS.size()+" "+VaultConfig.messages.get("GENERATION_MSG.TRIAL_VAULTS")+"]");

		getCommand("ftv").setExecutor(new VaultCommand());
		AutoUnlockVault.getInstance().start();
	}
	
	@Override
    public void onDisable() {
		VaultConfig.SAVE_CONFIG();
		TrialVault.SAVEFILE();
	}
	
	@EventHandler
    public static void onKeyRedeem(PlayerInteractEvent event) {
    	ItemStack usedItem = event.getItem();
    	Block block = event.getClickedBlock();
    	
    	if(block == null) return;
    	if(!block.getType().equals(Material.VAULT)) return;
    	if(usedItem == null) return;
    	if (usedItem.getType().equals(Material.AIR)) return;
    	
    	TrialVault vault = (!TrialVault.VAULTS.containsKey(block.getLocation())) ? new TrialVault(block.getLocation()) : TrialVault.VAULTS.get(block.getLocation());
    	UUID uuid = event.getPlayer().getUniqueId();
    	//Check to make sure the player is interacting with a vault.
    	if(block.getType().equals(Material.VAULT)) {

    		//If the vault is ominous. Require an ominous key otherwise a regular trial key
    		Material required_key = vault.isOminous() ? Material.OMINOUS_TRIAL_KEY : Material.TRIAL_KEY;
    		
    		if(usedItem.getType().equals(required_key)) {
    			
    			//Check if there is a delay
    			if(VaultConfig.RESET_DELAY > 0) {
    				if(vault.getElapsedTime(uuid) != -1L) {//-1L means the vault hasn't registered the player and timestamp
						long msTimeRemaining = (VaultConfig.RESET_DELAY - vault.getElapsedTime(uuid)); //How much time remaining until reset
						if((msTimeRemaining) > 0) {
							if(VaultConfig.PRINT_TIME_REMAINING) {
								event.getPlayer().sendMessage(VaultConfig.messages.get("VAULT_MSG.TIME_LEFT1")+ChatColor.GREEN+VaultUtils.getWaitTimeToString(msTimeRemaining)+ChatColor.WHITE+VaultConfig.messages.get("VAULT_MSG.TIME_LEFT2"));
							}
							return;
						}
    				}
				}
    			
    			//Don't do anything while the vault is ejecting
    			if(vault.getVaultState() == State.EJECTING) {
    				event.getPlayer().sendMessage(VaultConfig.messages.get("VAULT_MSG.BUSY"));
    				return;
    			}
    			
    			//Register the player and timestamp them
    			if(!vault.hasLooted(uuid)) {
    				event.useItemInHand();
    				vault.registerPlayer(uuid);
    				
    				if(!TrialVault.VAULTS.containsKey(vault.location))
    					TrialVault.VAULTS.put(vault.location, vault);
    			}
    			
    			//Set the next auto check time
    			if(VaultConfig.RESET_DELAY > 0) {
    				//If auto unlock is enabled and theres no vaults registered in memory, use the reset delay for the first vault registered
    				if(TrialVault.VAULTS.isEmpty() && VaultConfig.ENABLE_AUTO_UNLOCK && VaultConfig.RESET_DELAY > 0L)
    					VaultConfig.NEXT_AUTO_CHECK_TIME = VaultConfig.RESET_DELAY > 1000L ? VaultConfig.RESET_DELAY/1000L : 1L;
				} else if(VaultConfig.RESET_DELAY <= 0) {
					new BukkitRunnable() {
						@Override
						public void run() {
							vault.removePlayerUUID(uuid);
						}
					}.runTaskLater(ForgetfulTrialVault.instance, 1L);
				}
    			
    			//Check if player can reset vault
    			if(vault.hasLooted(uuid)) {
    				if(vault.getElapsedTime(uuid) >= VaultConfig.RESET_DELAY || vault.getElapsedTime(uuid) == -1L) {
    					if(vault.getVaultState() == State.INACTIVE)
    						event.getPlayer().sendMessage(ChatColor.GREEN+VaultConfig.messages.get("VAULT_MSG.RESET"));
	    				vault.removePlayerUUID(uuid);
						return;
    				}
    			}
        	}
    	}
    }
}