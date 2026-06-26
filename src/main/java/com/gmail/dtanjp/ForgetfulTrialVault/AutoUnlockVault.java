package com.gmail.dtanjp.ForgetfulTrialVault;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * AutoUnlockVault.java
 * 
 * @author David Tan
 */
public class AutoUnlockVault {

	/** Singleton Instance **/
	private static AutoUnlockVault instance = null;
	
	private long nextCheckTime = 1L;
	
	/** Constructor **/
	private AutoUnlockVault() {
	}
	
	public void start() {
		new BukkitRunnable() {
			
			  @Override
			  public void run() {
				  process();
			  }
			  
		}.runTask(ForgetfulTrialVault.instance);
	}
	
	private void process() {
		//Auto check is disabled
	    if(!VaultConfig.ENABLE_AUTO_UNLOCK) return;
	    
	    //Plugin is disabled. Stop running checks.
		if(!ForgetfulTrialVault.instance.isEnabled()) return;
		
		//Find the smallest time to check again. Do not waste processing power continuously checking
		nextCheckTime = VaultConfig.NEXT_AUTO_CHECK_TIME;
		
		if(!TrialVault.VAULTS.isEmpty()) {
			//Set to hold any trial vaults that are empty to prevent them from being processed
			Set<Location> removeVaults = new HashSet<>(TrialVault.VAULTS.size());
			
			//If every trial vault for a player has been unlocked, notify them
			Set<Player> notifyPlayers = new HashSet<>(Bukkit.getOnlinePlayers().size());
			
			//Loop through all the trial vaults in memory
			for(Location location : TrialVault.VAULTS.keySet()) {
				//Skip null entries
				if(location == null) continue;
				
				//Make sure the block exists
				if(location.getBlock() == null) {
					removeVaults.add(location);
					continue;
				}
				
				//Check to make sure the block at this location is a trial vault
				if(!location.getBlock().getType().equals(Material.VAULT)) {
					removeVaults.add(location);
					continue;
				}
				
				TrialVault vault = (!TrialVault.VAULTS.containsKey(location)) ? new TrialVault(location) : TrialVault.VAULTS.get(location);
				
				//Do checks to make sure if we can remove this vault from memory
				if(vault == null) {
					removeVaults.add(location);
					continue;
				}
				
				if(vault.getPlayerUUIDS() == null) {
					removeVaults.add(location);
					continue;
				}
				
				if(vault.getPlayerUUIDS().isEmpty()) {
					removeVaults.add(location);
					continue;
				}
				
				//Loop through all the players inside the trial vault to auto unlock trial vaults for players
				for(UUID player : vault.timeStamps.keySet()) {
					if(vault.canReset(player)) {
						vault.removePlayerUUID(player);
						notifyPlayers.add(Bukkit.getPlayer(player));
					} else {
						long remainingTime = VaultConfig.RESET_DELAY-vault.getElapsedTime(player);
						//They still have vaults waiting to be auto reset. Kick them off the list.
						notifyPlayers.remove(Bukkit.getPlayer(player));
						if(nextCheckTime > remainingTime)
							nextCheckTime = remainingTime;
					}
				}
				
				nextCheckTime = nextCheckTime > 1000L ? nextCheckTime/1000L : 1L;
				
				//After updating the trial vault, if it doesn't contain any players, then just remove it from being processed
				if(vault.getPlayerUUIDS() != null) {
					if(vault.getPlayerUUIDS().isEmpty())
						removeVaults.add(location);
				}
			}
			if(!removeVaults.isEmpty()) {
				for(Location location : removeVaults)
					TrialVault.VAULTS.remove(location);
			}
			if(VaultConfig.ENABLE_PLAYER_NOTIFICATION) {
				notifyPlayers.stream()
				.filter(p -> p != null)
				.filter(p -> p.isOnline())
				.forEach(p -> {
					p.sendMessage("[ForgetfulTrialVault]: "+ChatColor.GREEN+VaultConfig.messages.get("VAULT_MSG.AUTO_RESET"));
				});
				notifyPlayers.clear();
			}
		}
		new BukkitRunnable() {
			
			  @Override
			  public void run() {
				  start();
			  }
			  
		}.runTaskLater(ForgetfulTrialVault.instance, 20L * nextCheckTime);
	}
	
	/** Singleton **/
	public static AutoUnlockVault getInstance() {
		if(instance == null)
			instance = new AutoUnlockVault();
		return instance;
	}
	
}
