package com.gmail.dtanjp.ForgetfulTrialVault;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Vault;
import org.bukkit.block.data.type.Vault.State;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
/**
 * 
 * ForgetfulTrialVault.java
 * 
 * @author David Tan
 * @description: Spigot plugin to reset a Trial Chamber Vault for reuse.
 */
public class ForgetfulTrialVault extends JavaPlugin implements Listener {

	public static ForgetfulTrialVault instance;
	
	@Override
	public void onEnable() {
		instance = this;
		this.getServer().getPluginManager().registerEvents(instance, instance);
	}
	
    	@Override
    	public void onDisable() {
	}
	
	@EventHandler
    	public static void onKeyRedeem(PlayerInteractEvent event) {
		ItemStack usedItem = event.getItem();
		Block block = event.getClickedBlock();
		
		if(block == null) return;
		if(!block.getType().equals(Material.VAULT)) return;
		if(usedItem == null) return;
		if (usedItem.getType().equals(Material.AIR)) return;
		
		Location block_loc = block.getLocation();
		
		//Check to make sure the player is interacting with a vault.
		if(block.getType().equals(Material.VAULT)) {
			//Vault Data aka State of the Vault.
			//See https://minecraft.wiki/w/Vault#Block_states
			Vault vault_data = (Vault) block.getBlockData();
			
			//Vault ominous tag
			boolean isOminous = vault_data.isOminous();
			
			//Block facing direction
			BlockFace facing = vault_data.getFacing();
			
			//If the vault is ominous. Require an ominous key otherwise a regular trial key
			Material required_key = isOminous ? Material.OMINOUS_TRIAL_KEY : Material.TRIAL_KEY;
			
			if(usedItem.getType().equals(required_key)) {
				if(vault_data.getTrialSpawnerState() == State.INACTIVE) {//Using a key on an inactive vault to revert it back to being active
					block.setType(Material.STONE); //Reset tile entity data to a regular block and erasing any data/nbt tags
					
					//Setting data on to "new" vault
					String reward_loot = isOminous ? "\"minecraft:chests/trial_chambers/reward_ominous\"" : "\"minecraft:chests/trial_chambers/reward\"";
					String position = block_loc.getBlockX()+" "+block_loc.getBlockY()+" "+block_loc.getBlockZ();
					String key_type = isOminous ? "\"minecraft:ominous_trial_key\"" : "\"minecraft:trial_key\"";
						
					//Revert the stone block back to vault and apply the data
					// See https://minecraft.wiki/w/Ominous_Vault#Obtaining
					String cmd = "setblock "+position+" minecraft:vault[facing=\""+facing.name().toLowerCase()+"\", ominous="+isOminous+"]{config:{key_item:{count:1,id:"+key_type+"},loot_table:"+reward_loot+"}} replace";
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
						
					event.useInteractedBlock();
					event.useItemInHand();
				} else
					event.useItemInHand();
			}
		}
    	}
	
}
