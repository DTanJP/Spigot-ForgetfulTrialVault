package com.gmail.dtanjp.ForgetfulTrialVault;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * VaultCommand.java
 * 
 * @author David Tan
 */
public class VaultCommand implements CommandExecutor {
	
	/** Constructor **/
	public VaultCommand() {
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if(!cmd.getName().equalsIgnoreCase("ftv")) return false;
		
		if(!player.isOp()) {
			player.sendMessage("You must be an operator to access this command.");
			return false;
		}
		
		if(args.length < 1) {
			player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault]");
			player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"reset "+ChatColor.GREEN+"delay"+ChatColor.WHITE+" - Sets the reset delay (milliseconds) for Trial Vaults (0 or lower to disable the delay)");
			player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"timer "+ChatColor.GREEN+"true/false "+ChatColor.WHITE+"- Toggle on/off the that tells players how much time remaining to reset Vault");
			return true;
		}
		
		switch (args[0].toLowerCase()) {
		
			case "help":
				player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault]");
				player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"reset "+ChatColor.GREEN+"delay"+ChatColor.WHITE+" - Sets the reset delay (milliseconds) for Trial Vaults (0 or lower to disable the delay)");
				player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"timer "+ChatColor.GREEN+"true/false "+ChatColor.WHITE+"- Toggle on/off the that tells players how much time remaining to reset Vault");
				break;
			case "reset":
				if(args.length == 1) {
					if(VaultConfig.RESET_DELAY > 0)
						player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault] "+ChatColor.RESET+ChatColor.GOLD+"Reset Delay: "+ChatColor.GREEN+VaultUtils.getWaitTimeToString(VaultConfig.RESET_DELAY));
					else
						player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault] "+ChatColor.RESET+ChatColor.GOLD+"Reset Delay: "+ChatColor.WHITE+"disabled.");
				} else if(args.length == 2) {
					try {
					Long delay = Long.parseLong(args[1]);
					VaultConfig.RESET_DELAY = (delay > 0 ? delay : 0);
					if(VaultConfig.RESET_DELAY > 0)
						player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault] "+ChatColor.RESET+ChatColor.GOLD+"Reset Delay: "+ChatColor.GREEN+VaultUtils.getWaitTimeToString(VaultConfig.RESET_DELAY));
					else
						player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault] "+ChatColor.RESET+ChatColor.GOLD+"Reset Delay: "+ChatColor.WHITE+"disabled.");
					VaultConfig.REQUIRES_SAVING = true;
					} catch (NumberFormatException e) {
						player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault] "+ChatColor.RESET+ChatColor.RED+"Invalid delay set.");
						player.sendMessage(ChatColor.BOLD+"USAGE: "+ChatColor.RESET+ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"reset "+ChatColor.WHITE+"1000");
					}
				}
				break;
				
			case "timer":
				if(args.length == 1)
					player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault] "+ChatColor.RESET+ChatColor.GOLD+"Vault remaining timer info: "+(VaultConfig.PRINT_TIME_REMAINING ? ChatColor.GREEN : ChatColor.RED)+VaultConfig.PRINT_TIME_REMAINING);
				else if(args.length == 2) {
					VaultConfig.PRINT_TIME_REMAINING = Boolean.parseBoolean(args[1]);
					player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault] "+ChatColor.RESET+ChatColor.GOLD+"Vault remaining timer info: "+(VaultConfig.PRINT_TIME_REMAINING ? ChatColor.GREEN : ChatColor.RED)+VaultConfig.PRINT_TIME_REMAINING);
					VaultConfig.REQUIRES_SAVING = true;
				}
					
				break;
				
			default:
				player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault]");
				player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"reset "+ChatColor.GREEN+"delay"+ChatColor.WHITE+" - Sets the reset delay (milliseconds) for Trial Vaults (0 or lower to disable the delay)");
				player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"timer "+ChatColor.GREEN+"true/false "+ChatColor.WHITE+"- Toggle on/off the that tells players how much time remaining to reset Vault");
				break;
		}
		
		return true;
	}

}