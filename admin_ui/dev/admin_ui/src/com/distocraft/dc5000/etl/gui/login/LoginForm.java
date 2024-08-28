/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.distocraft.dc5000.etl.gui.login;

import java.io.Serializable;

/**
 * @author ericker
 * @since 2010
 *
 */
public class LoginForm implements Serializable {

    /*  The properties */
    private String userName = "";

    private String userPassword = "";

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName == null ? "" : userName.trim();
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(final String userPassword) {
        this.userPassword = userPassword == null ? "" : userPassword.trim();
    }

    public boolean process() {

        boolean canContinue = false;

        if (this.getUserName().compareTo("") != 0 && this.getUserPassword().compareTo("") != 0) {
            canContinue = true;
        }
        return canContinue;
    }

    public void clearDetails() {
        // Clear the form
        userName = "";
        userPassword = "";
    }

}
