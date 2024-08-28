/**
 * ----------------------------------------------------------------------- *
 * Copyright (C) 2010 LM Ericsson Limited. All rights reserved. *
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.repository.dbusers;

import java.util.List;

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.ericsson.eniq.repository.DBUsersGet;
import com.ericsson.eniq.repository.UpdateDBUsers;

/**
 *
 * @author esuramo
 *
 */
public class UpdateDBPass {

  private UpdateDBPass() {
  }

	public static void main(final String[] args) {
	  String requestedUser = null;
    String oldPassword = null;
    String newPassword = null;

		if (args == null || args.length != 3) {
			System.out.println("Invalid parameters. Need to pass User Existing_Password New_Password");
			System.exit(1);
		}
		  requestedUser = args[0];
      oldPassword = args[1];
      newPassword = args[2];

		try {
		    final List<Meta_databases> databases = DBUsersGet.getMetaDatabases("ALL","ALL");
		     for ( Meta_databases m : databases ){
		      if ( oldPassword.equals(m.getPassword()) && requestedUser.equalsIgnoreCase(m.getUsername()) ){
		        System.out.println("Updating Password...");
		        final int affectedRows = UpdateDBUsers.updateMetaDatabases(requestedUser,oldPassword,newPassword);
		        System.out.println(affectedRows+" Rows Affected");
		        System.exit(0);
		        }
		      }
		     System.out.println("The combination "+requestedUser+" "+oldPassword+" does not exist in database.");
		     System.exit(2);
    } catch (Exception e) {
			System.out.println("Exception Occured");
			System.out.println(e.getMessage());
			System.exit(3);
		}
	}
}
