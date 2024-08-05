package com.gmail.dtanjp.ForgetfulTrialVault;

import java.util.UUID;

/**
 * VaultUtils.java
 * 
 * @author David Tan
 */
public class VaultUtils {

	/** Used for when the player tries to reset the vault. Tells them the time left **/
	public static String getWaitTimeToString(Long time) {
		Long[] timer;
		long hours, minutes, seconds, milliseconds;
		
		long timeRemaining = time % 86400000L;
		hours = timeRemaining / 3600000L;
		timeRemaining %= 3600000L;
		minutes = timeRemaining / 60000L;
		timeRemaining %= 60000L;
		seconds = timeRemaining / 1000L;
		timeRemaining %= 1000L;
		milliseconds = timeRemaining;
		
		//days / hours/ minutes/ seconds / milliseconds
		timer = new Long[] {(time / 86400000L), hours, minutes, seconds};
		
		String result = timer[0] > 0 ? timer[0]+" day"+(timer[0] > 1 ? "s" : "") : "";
		String[] units = new String[] {"hour", "minute", "second"};
		for(int i=1; i<timer.length; i++) {
			if(timer[i] > 0) {
				for(int j=0; j<i; j++) {
					if(timer[j] > 0) {
						result += ", ";
						break;
					}
				}
				result += timer[i]+" "+units[i-1]+(timer[i] > 1 ? "s" : "");
			}
		}
		if(time < 1000L)
			result += milliseconds+"ms ";
		return result;
	}
	
	/** Parses the 32 bit UUID int array to a UUID **/
	public static UUID parseNBTUUID(int[] uuid) {
		if(uuid.length != 4) return null;
		//I have no idea how this works, but thank you tr7zw for the function
		//https://github.com/tr7zw/Item-NBT-API/blob/master/item-nbt-api/src/main/java/de/tr7zw/changeme/nbtapi/utils/UUIDUtil.java
		return new UUID((long) uuid[0] << 32 | (long) uuid[1] & 4294967295L, (long) uuid[2] << 32 | (long) uuid[3] & 4294967295L);
	}
	
	//https://github.com/tr7zw/Item-NBT-API/blob/master/item-nbt-api/src/main/java/de/tr7zw/changeme/nbtapi/utils/UUIDUtil.java
	public static int[] UUIDToIntArray(UUID uuid) {
		if(uuid == null) return null;
		long l = uuid.getLeastSignificantBits();
		long m = uuid.getMostSignificantBits();
		return new int[] { (int) (m >> 32), (int) m, (int) (l >> 32), (int) l};
    }
	
}
