package com.gmail.dtanjp.ForgetfulTrialVault;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
			player.sendMessage(VaultConfig.messages.get("COMMAND.INSUFFICIENT_PRIVILEGE"));
			return false;
		}
		
		if(args.length < 1) {
			player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault]");
			player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"reset "+ChatColor.GREEN+"delay"+ChatColor.WHITE+" - "+VaultConfig.messages.get("COMMAND.HELP-RESET"));
			player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"autounlock "+ChatColor.GREEN+"true"+ChatColor.WHITE+"/"+ChatColor.RED+"false"+ChatColor.WHITE+" - "+VaultConfig.messages.get("COMMAND.HELP-AUTOUNLOCK"));
			player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"unlocknotification "+ChatColor.GREEN+"true"+ChatColor.WHITE+"/"+ChatColor.RED+"false"+ChatColor.WHITE+" - "+VaultConfig.messages.get("COMMAND.HELP-UNLOCKNOTIFICATION"));
			player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"timer "+ChatColor.GREEN+"true/false "+ChatColor.WHITE+"- "+VaultConfig.messages.get("COMMAND.HELP-TIMER"));
			player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"locale "+ChatColor.GREEN+"English"+ChatColor.WHITE+" | "+ChatColor.GREEN+"Espanol "+"English"+ChatColor.WHITE+" | "+ChatColor.GREEN+"Francais"+ChatColor.WHITE+" | "+ChatColor.GREEN+"Portuguese"+ChatColor.WHITE+" | "+ChatColor.GREEN+"Custom");
			player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"cleardelay "+ChatColor.GREEN+"player"+ChatColor.WHITE+" - "+ChatColor.GREEN+VaultConfig.messages.get("COMMAND.CLEAR-DELAY-HELP"));
			return true;
		}
		
		switch (args[0].toLowerCase()) {
		
			case "help":
				player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault]");
				player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"reset "+ChatColor.GREEN+"delay"+ChatColor.WHITE+" - "+VaultConfig.messages.get("COMMAND.HELP-RESET"));
				player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"autounlock "+ChatColor.GREEN+"true"+ChatColor.WHITE+"/"+ChatColor.RED+"false"+ChatColor.WHITE+" - "+VaultConfig.messages.get("COMMAND.HELP-AUTOUNLOCK"));
				player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"unlocknotification "+ChatColor.GREEN+"true"+ChatColor.WHITE+"/"+ChatColor.RED+"false"+ChatColor.WHITE+" - "+VaultConfig.messages.get("COMMAND.HELP-UNLOCKNOTIFICATION"));
				player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"timer "+ChatColor.GREEN+"true/false "+ChatColor.WHITE+"- "+VaultConfig.messages.get("COMMAND.HELP-TIMER"));
				player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"locale "+ChatColor.GREEN+"English"+ChatColor.WHITE+" | "+ChatColor.GREEN+"Espanol "+"English"+ChatColor.WHITE+" | "+ChatColor.GREEN+"Francais"+ChatColor.WHITE+" | "+ChatColor.GREEN+"Portuguese"+ChatColor.WHITE+" | "+ChatColor.GREEN+"Custom");
				player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"cleardelay "+ChatColor.GREEN+"player"+ChatColor.WHITE+" - "+ChatColor.GREEN+VaultConfig.messages.get("COMMAND.CLEAR-DELAY-HELP"));
				break;
				
			case "reset":
				if(args.length == 1) {
					if(VaultConfig.RESET_DELAY > 0)
						player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault] "+ChatColor.RESET+ChatColor.GOLD+"Reset Delay: "+ChatColor.GREEN+VaultUtils.getWaitTimeToString(VaultConfig.RESET_DELAY));
					else
						player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault] "+ChatColor.RESET+ChatColor.GOLD+"Reset Delay: "+ChatColor.WHITE+VaultConfig.messages.get("COMMAND.DISABLED")+".");
				} else if(args.length == 2) {
					try {
					Long delay = Long.parseLong(args[1]);
					VaultConfig.RESET_DELAY = (delay > 0 ? delay : 0);
					if(VaultConfig.RESET_DELAY > 0)
						player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault] "+ChatColor.RESET+ChatColor.GOLD+"Reset Delay: "+ChatColor.GREEN+VaultUtils.getWaitTimeToString(VaultConfig.RESET_DELAY));
					else {
						//Delay cannot be below 0.
						VaultConfig.RESET_DELAY = Math.max(delay, 0);
						player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault] "+ChatColor.RESET+ChatColor.GOLD+"Reset Delay: "+ChatColor.WHITE+VaultConfig.messages.get("COMMAND.DISABLED")+".");
					}
					VaultConfig.REQUIRES_SAVING = true;
					} catch (NumberFormatException e) {
						player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault] "+ChatColor.RESET+ChatColor.RED+VaultConfig.messages.get("COMMAND.INVALID_DELAY"));
						player.sendMessage(ChatColor.BOLD+VaultConfig.messages.get("COMMAND.USAGE")+" "+ChatColor.RESET+ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"reset "+ChatColor.WHITE+"1000");
					}
				}
				break;
				
			case "timer":
				if(args.length == 1)
					player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault] "+ChatColor.RESET+ChatColor.GOLD+VaultConfig.messages.get("COMMAND.REMAINING_TIMER")+(VaultConfig.PRINT_TIME_REMAINING ? ChatColor.GREEN : ChatColor.RED)+VaultConfig.PRINT_TIME_REMAINING);
				else if(args.length == 2) {
					VaultConfig.PRINT_TIME_REMAINING = Boolean.parseBoolean(args[1]);
					player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault] "+ChatColor.RESET+ChatColor.GOLD+VaultConfig.messages.get("COMMAND.REMAINING_TIMER")+(VaultConfig.PRINT_TIME_REMAINING ? ChatColor.GREEN : ChatColor.RED)+VaultConfig.PRINT_TIME_REMAINING);
					VaultConfig.REQUIRES_SAVING = true;
				}
				break;
			
			case "locale":
				if(args.length == 1)
					player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"locale "+ChatColor.GREEN+"English"+ChatColor.WHITE+" | "+ChatColor.GREEN+"Espanol "+"English"+ChatColor.WHITE+" | "+ChatColor.GREEN+"Francais"+ChatColor.WHITE+" | "+ChatColor.GREEN+"Portuguese"+ChatColor.WHITE+" | "+ChatColor.GREEN+"Custom");
				else if(args.length == 2) {
					String language = args[1].toLowerCase();
					switch(language) {
					case "francais":
						VaultConfig.Language = "Francais";
						break;
					
					case "espanol":
						VaultConfig.Language = "Espanol";
						break;
					
					case "portuguese":
						VaultConfig.Language = "Portuguese";
						break;
					
					case "english":
						VaultConfig.Language = "English";
						break;
					
					case "custom":
						VaultConfig.Language = "Custom";
						break;
						
					default:
						VaultConfig.Language = "English";
						player.sendMessage("Available Languages: English"+ChatColor.GREEN+"/"+ChatColor.WHITE+"Espanol"+ChatColor.GREEN+"/"+ChatColor.WHITE+"Francais"+ChatColor.GREEN+"/"+ChatColor.WHITE+"Portuguese"+ChatColor.GREEN+"/"+ChatColor.WHITE+"Custom");
						break;
					}
					player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault] "+ChatColor.RESET+ChatColor.GOLD+"Locale: "+ChatColor.WHITE+VaultConfig.Language);
					VaultConfig.EXPORT_LOCALE_FILES();
					VaultConfig.LOAD_LANGUAGE();
					VaultConfig.REQUIRES_SAVING = true;
				}
				break;
				
			case "cleardelay":
				if(args.length < 2) {
					player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"cleardelay "+ChatColor.GREEN+"delay"+ChatColor.WHITE+" - "+ChatColor.GREEN+VaultConfig.messages.get("COMMAND.CLEAR-DELAY-HELP"));
					player.sendMessage("[ForgetfulTrialVault]: "+VaultConfig.messages.get("ERROR_MSG.CLEAR-DELAY-ERROR-NO-PLAYER-NAME"));
				} else {
					Player targetPlayer = Bukkit.getPlayerExact(args[1]);
					if(targetPlayer == null) {
						player.sendMessage("[ForgetfulTrialVault]: "+VaultConfig.messages.get("ERROR_MSG.CLEAR-DELAY-ERROR-PLAYER-NAME"));
						return false;
					}
					int vaultcount = 0;
					
					//No vaults to clear from. Skip it
					if(TrialVault.VAULTS.isEmpty())
						return true;
					
					//Loop through all trial vaults in memory and remove the player from the vaults.
					for(Location location : TrialVault.VAULTS.keySet()) {
						TrialVault vault = TrialVault.VAULTS.get(location);
						vault.removePlayerUUID(targetPlayer.getUniqueId());
						vaultcount++;
					}
					player.sendMessage("[ForgetfulTrialVault]: Reset cooldown delay for "+ChatColor.AQUA+args[1]+ChatColor.WHITE+" ["+vaultcount+ChatColor.GREEN+"/"+ChatColor.AQUA+TrialVault.VAULTS.size()+ChatColor.WHITE+"]");
				}
				break;
				
			case "autounlock":
				if(args.length < 2) {
					player.sendMessage("[ForgetfulTrialVault]: Auto Unlock ["+(VaultConfig.ENABLE_AUTO_UNLOCK ? ChatColor.GREEN : ChatColor.RED)+VaultConfig.ENABLE_AUTO_UNLOCK+ChatColor.WHITE+"]");
					return true;
				} else {
					boolean value = Boolean.parseBoolean(args[1]);
					if(VaultConfig.ENABLE_AUTO_UNLOCK != value) {
						AutoUnlockVault.getInstance().start();
						VaultConfig.ENABLE_AUTO_UNLOCK = value;
						VaultConfig.REQUIRES_SAVING = true;
					}
					player.sendMessage("[ForgetfulTrialVault]: Auto Unlock ["+(VaultConfig.ENABLE_AUTO_UNLOCK ? ChatColor.GREEN : ChatColor.RED)+VaultConfig.ENABLE_AUTO_UNLOCK+ChatColor.WHITE+"]");
				}
				break;
			
			case "unlocknotification":
				if(args.length < 2) {
					player.sendMessage("[ForgetfulTrialVault]: Unlock Notification ["+(VaultConfig.ENABLE_PLAYER_NOTIFICATION ? ChatColor.GREEN : ChatColor.RED)+VaultConfig.ENABLE_PLAYER_NOTIFICATION+ChatColor.WHITE+"]");
					return true;
				} else {
					boolean value = Boolean.parseBoolean(args[1]);
					VaultConfig.ENABLE_PLAYER_NOTIFICATION = value;
					VaultConfig.REQUIRES_SAVING = true;
					player.sendMessage("[ForgetfulTrialVault]: Unlock Notification ["+(VaultConfig.ENABLE_PLAYER_NOTIFICATION ? ChatColor.GREEN : ChatColor.RED)+VaultConfig.ENABLE_PLAYER_NOTIFICATION+ChatColor.WHITE+"]");
				}
				break;
			
			default:
				player.sendMessage(ChatColor.BOLD+"[ForgetfulTrialVault]");
				player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"reset "+ChatColor.GREEN+"delay"+ChatColor.WHITE+" - "+VaultConfig.messages.get("COMMAND.HELP-RESET"));
				player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"autounlock "+ChatColor.GREEN+"true"+ChatColor.WHITE+"/"+ChatColor.RED+"false"+ChatColor.WHITE+" - "+VaultConfig.messages.get("COMMAND.HELP-AUTOUNLOCK"));
				player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"unlocknotification "+ChatColor.GREEN+"true"+ChatColor.WHITE+"/"+ChatColor.RED+"false"+ChatColor.WHITE+" - "+VaultConfig.messages.get("COMMAND.HELP-UNLOCKNOTIFICATION"));
				player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"timer "+ChatColor.GREEN+"true/false "+ChatColor.WHITE+"- "+VaultConfig.messages.get("COMMAND.HELP-TIMER"));
				player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"locale "+ChatColor.GREEN+"English"+ChatColor.WHITE+" | "+ChatColor.GREEN+"Espanol "+"English"+ChatColor.WHITE+" | "+ChatColor.GREEN+"Francais"+ChatColor.WHITE+" | "+ChatColor.GREEN+"Portuguese"+ChatColor.WHITE+" | "+ChatColor.GREEN+"Custom");
				player.sendMessage(ChatColor.GOLD+"/ftv "+ChatColor.AQUA+"cleardelay "+ChatColor.GREEN+"player"+ChatColor.WHITE+" - "+ChatColor.GREEN+VaultConfig.messages.get("COMMAND.CLEAR-DELAY-HELP"));
				break;
		}
		
		return true;
	}

}