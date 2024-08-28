package com.ericsson.eniq.storedprocedures;

import java.util.List;
import java.util.ArrayList;

public class LockUserDummy {
	public static List<String> usersLocked = new ArrayList<String>();
	public static List<String> usersUnlocked = new ArrayList<String>();
	public static List<String> usersDropped = new ArrayList<String>();
	public static String lock_user(final String user){
		usersLocked.add(user);
		return "User " + user + " locked at now";
	}
	public static String unlock_user(final String user){
		usersUnlocked.add(user);
		return "User " + user + " unlocked at now";
	}
	public static String drop_user_connections(final String user){
		usersDropped.add(user);
		return "Dropped user connections for " + user;
	}
}
