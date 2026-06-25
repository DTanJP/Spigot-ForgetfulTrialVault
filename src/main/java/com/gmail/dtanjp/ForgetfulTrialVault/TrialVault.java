package com.gmail.dtanjp.ForgetfulTrialVault;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Vault;
import org.bukkit.block.data.type.Vault.State;

import com.saicone.rtag.RtagBlock;
/**
 * TrialVault.java
 * 
 * @author David Tan
 */
public class TrialVault {

	public Location location;
	public Map<UUID, Long> timeStamps;//The time stamp of the players who looted from this vault
	public static Map<Location, TrialVault> VAULTS = new HashMap<>();
	
	/** Constructor **/
	public TrialVault(Location location) {
		this.location = location;
		timeStamps = new HashMap<>();
	}
	
	/** Returns the vault block at that position: return null for any other blocks **/
	public Block getVaultBlock() {
		Block result = location.getBlock();
		return (result.getType().equals(Material.VAULT) ? result : null);
	}
	
	/** Get the time the trial vault was looted **/
	public Long getTimeStamp(UUID uuid) {
		if(!timeStamps.containsKey(uuid)) return -1L;//Not found in the registry. -1L allows the player to reset again
		return timeStamps.get(uuid);
	}
	
	/** Updates the timestamp for the player **/
	public void setTimeStamp(UUID uuid, Long timeStamp) {
		timeStamps.put(uuid, timeStamp);
	}
	
	/** Register the player time stamp to the registry. Does not change any NBT data on the Vault block **/
	public void registerPlayer(UUID uuid) {
		timeStamps.put(uuid, System.currentTimeMillis());
	}
	
	/** Check to see if a timestamp is registered for the player. Also checks the NBT data **/
	public boolean hasLooted(UUID uuid) {
		if(timeStamps.containsKey(uuid)) return true;
		ArrayList<UUID> uuids = getPlayerUUIDS();
		return uuids != null ? (uuids.contains(uuid)) : false;
	}
	
	/** Check the condition if the player can reset a trial vault **/
	public boolean canReset(UUID uuid) {
		if(VaultConfig.RESET_DELAY <= 0) return true;
		if(!timeStamps.containsKey(uuid)) return true;
		Long timeStamp = getTimeStamp(uuid);
		if(timeStamp == -1L) return true;
		return (System.currentTimeMillis() - timeStamp) >= VaultConfig.RESET_DELAY;
	}
	
	/** Is the Vault block ominous or regular **/
	public boolean isOminous() {
		Block block = getVaultBlock();
		if(block == null) return false;
		return ((Vault) block.getBlockData()).isOminous();
	}
	
	/** Get the facing direction of the Vault **/
	public BlockFace getFacing() {
		Block block = getVaultBlock();
		if(block == null) return null;
		return ((Vault) block.getBlockData()).getFacing();
	}
	
	/** https://minecraft.wiki/w/Vault#Block_states **/
	public State getVaultState() {
		Block block = getVaultBlock();
		if(block == null) return null;
		return ((Vault) block.getBlockData()).getTrialSpawnerState();
	}
	
	/** Calculate the time passed since the player has looted the Vault **/
	public Long getElapsedTime(UUID uuid) {
		if(!timeStamps.containsKey(uuid)) return -1L;
		return System.currentTimeMillis() - timeStamps.get(uuid);
	}
	
	/** Get the 128bit UUIDS from the NBT Data of this vault **/
	public ArrayList<UUID> getPlayerUUIDS() {
		ArrayList<UUID> result = null;
		ArrayList<int[]> uuids = getNBTUUIDS();
		if(uuids == null) return null;
		if(uuids.isEmpty()) return null;
		result = new ArrayList<>(uuids.size());
		for(int[] i : uuids)
			result.add(VaultUtils.parseNBTUUID(i));
		return result;
	}
	
	/** Remove the exact player data from the NBT Data and allow them to loot it again **/
	public void removePlayerUUID(UUID uuid) {
		Block block = getVaultBlock();
		if(block == null) {
			System.out.println("Block is not a vault. No UUIDs to remove.");
			return;
		}
		
		//Remove timestamp from registry
		if(timeStamps.containsKey(uuid)) 
			timeStamps.remove(uuid);
		
		
		//Get the NBT Data of the vault block
		RtagBlock tag = new RtagBlock(block);
		
		//Remove the UUID from server_data/rewarded_players NBT Data
		if(timeStamps.isEmpty()) {
			tag.remove("server_data", "rewarded_players");
		} else {
			ArrayList<int[]> uuids = new ArrayList<>(timeStamps.size());
			for(UUID i : timeStamps.keySet())
				uuids.add(VaultUtils.UUIDToIntArray(i));
			tag.set(uuids, "server_data", "rewarded_players");
		}
		
		//Finalize and publish the change
		tag.load();
	}
	
	/** Gets the 32-bits of the UUIDS of the players who looted the vault as an ArrayList<int[]>**/
	private ArrayList<int[]> getNBTUUIDS() {
		ArrayList<int[]> result = null;
		Block block = getVaultBlock();
		
		//Not a Vault block. Skip
		if(block == null) return null;
		
		//Block isn't a vault
		if(!block.getType().equals(Material.VAULT)) return null;
		
		//Get the NBT Data of the vault block
		RtagBlock tag = RtagBlock.of(block);
		
		//Block doesn't have server_data/rewarded_players NBT data
		if(!tag.hasTag("server_data", "rewarded_players")) return null;
		
		//Returns an arraylist<int[]>
		result = tag.get("server_data", "rewarded_players");
		return result;
	}
	
	/** Load the Trial Vaults and their timestamp data **/
	public static boolean LOADFILE() {
		VAULTS.clear();
		TrialVault vault = null;
		BufferedReader reader;
		
		ForgetfulTrialVault.instance.getLogger().info("[TrialVault] Loading Save File...");
		if(!VaultConfig.VAULT_DATA_FILE.exists()) {
			ForgetfulTrialVault.instance.getLogger().info("[TrialVault] Save File not found. Generating new save file");
			try {
				VaultConfig.VAULT_DATA_FILE.createNewFile();
				return false;
			} catch (IOException e) {
				ForgetfulTrialVault.instance.getLogger().info("[TrialVault] Unable to generating Save File");
				e.printStackTrace();
				return false;
			}
		}
		
		try {
			reader = new BufferedReader(new FileReader(VaultConfig.VAULT_DATA_FILE));
			String line = "";
			try {
				while((line = reader.readLine()) != null) {
					if(line.startsWith("#")) continue;
					if(line.startsWith("Location: ")) {
						vault = null;
						String foo = line.substring("Location: ".length());
						String[] tokens = foo.split(", ");
						
						//Parse location
						World world = Bukkit.getWorld(tokens[0]);
						int x = Integer.parseInt(tokens[1]);
						int y = Integer.parseInt(tokens[2]);
						int z = Integer.parseInt(tokens[3]);

						if(world == null) {
							ForgetfulTrialVault.instance.getLogger().info("[Invalid Vault] Null world: "+tokens[0]);
							continue;
						}
						
						Location newLocation = new Location(world, x, y, z);
						
						//Verify if the block at that location is a vault otherwise skip it
						if(!newLocation.getBlock().getType().equals(Material.VAULT)) {
							ForgetfulTrialVault.instance.getLogger().info("[Vault "+x+", "+y+", "+z+"] is not a valid Vault block.");
							continue;
						}
						
						//Register the vault
						vault = new TrialVault(newLocation);
						VAULTS.put(newLocation, vault);
						
						//Check the NBT data of the Vault and pre-add them. Set the correct time stamps later
						ArrayList<UUID> uuids = vault.getPlayerUUIDS();
						if(uuids != null) {
							if(!uuids.isEmpty()) {
								Long timeStamp = System.currentTimeMillis();
								for(UUID i : uuids)
									vault.timeStamps.put(i, timeStamp);
							}
						}
					} else {
						//Load the UUIDS and set the correct timestamps, anything not in the save file will have their timer reset from the step before
						if(vault != null && line.contains(": ")) {
							String[] tokens = line.split(": ");
							UUID uuid = UUID.fromString(tokens[0]);
							Long timeStamp = Long.parseLong(tokens[1]);
							
							//Don't register any players whose timestamp has expired
							if((System.currentTimeMillis() - timeStamp) >= VaultConfig.RESET_DELAY)
								vault.removePlayerUUID(uuid);
							
							//Update the player with the correct time stamp
							vault.setTimeStamp(uuid, timeStamp);
						}
					}
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/** Saves all the timestamp for all of the Vaults **/
	public static void SAVEFILE() {
		ForgetfulTrialVault.instance.getLogger().info("Saving Vault instances...");
		if(!VaultConfig.VAULT_DATA_FILE.exists()) {
			ForgetfulTrialVault.instance.getLogger().info("[TrialVault] Generating Save File");
			try {
				VaultConfig.VAULT_DATA_FILE.createNewFile();
				return;
			} catch (IOException e) {
				ForgetfulTrialVault.instance.getLogger().info("[TrialVault] Unable to generating Save File");
				e.printStackTrace();
				return;
			}
		}
		
		//Nothing to save
		if(VAULTS.isEmpty()) return;
		
		int saved = 0;
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(VaultConfig.VAULT_DATA_FILE, false));
			
			//Loop through all the Trial Vaults and write down the Vault location and the UUID and their timestamps
			for(Location loc : VAULTS.keySet()) {
				TrialVault vault = VAULTS.get(loc);
				if(vault.timeStamps.isEmpty()) continue;
				
				//Location: world, 0, 0, 0
				writer.write("Location: "+loc.getWorld().getName()+", "+loc.getBlockX()+", "+loc.getBlockY()+", "+loc.getBlockZ());
				writer.newLine();
				
				//Record every player that looted from this vault and their timestamp
				for(UUID player : vault.timeStamps.keySet()) {
					Long timeStamp = vault.timeStamps.get(player);
					
					//Looter UUID: Millisecond timestamp
					writer.write(player.toString()+": "+timeStamp);
					writer.newLine();
				}
				saved++;
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ForgetfulTrialVault.instance.getLogger().info("Saved "+saved+" Trial Vault instances.");
	}
	
}