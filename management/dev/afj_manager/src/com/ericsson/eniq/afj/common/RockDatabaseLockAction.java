/**
 * 
 */
package com.ericsson.eniq.afj.common;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

/**
 * @author eheijun
 * 
 */
public class RockDatabaseLockAction implements DatabaseLockAction {

  private final Logger log = Logger.getLogger(this.getClass().getName());

  private enum LockAction {
    LOCK, UNLOCK
  };

  private static final String CALL_LOCK_USER_COMMAND = "{call %s_user %s}";

  private static final String CALL_DROP_USER_CONNECTIONS = "{call drop_user_connections %s}";

  private static final String MSG = "msg";

  private final List<String> userList;

  private final RockFactory dwhDbaRock;

  /**
   * 
   * @param userList
   *          list of users
   * @param dwhDbaRock
   *          DBA database connection
   */
  public RockDatabaseLockAction(final RockFactory dwhDbaRock, final List<String> userList) {
    super();
    this.userList = userList;
    this.dwhDbaRock = dwhDbaRock;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.common.DatabaseLockAction#performLock()
   */
  @Override
  public Boolean performLock() {
    return doLockUnlock(userList, true, dwhDbaRock, LockAction.LOCK);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.common.DatabaseLockAction#performUnlock()
   */
  @Override
  public Boolean performUnlock() {
    return doLockUnlock(userList, true, dwhDbaRock, LockAction.UNLOCK);
  }

  /**
   * Locks or unlocks given users from the database.
   * 
   * @param userList
   *          List of user names which should be affected
   * @param undoIfErrors
   *          setting this true tries to undo changes if something goes wrong
   * @param dwhDbaRock
   *          Rock connection
   * @param opType
   *          LOCK or UNLOCK
   * @return true if operation was successfully         
   */
  private Boolean doLockUnlock(final List<String> userList, final boolean undoIfErrors, final RockFactory dwhDbaConn,
      final LockAction opType) {
    Boolean result;
    final List<String> locked = new ArrayList<String>();
    String currentUser = "";
    try {
      for (String user : userList) {
        currentUser = user;
        final String lockSql = String.format(CALL_LOCK_USER_COMMAND, new Object[] { opType.toString().toLowerCase(), user });
        final CallableStatement lockCs = dwhDbaConn.getConnection().prepareCall(lockSql);
        try {
          final ResultSet lockRs = lockCs.executeQuery();
          try {
            while (lockRs.next()) {
              final String lockResult = lockRs.getString(MSG);
              if (lockResult.startsWith("User " + user + " " + opType.toString().toLowerCase() + "ed at ")) {
                locked.add(user);
                log.info(user + " is now " + opType.toString().toLowerCase() + "ed.");
              }
            }
          } finally {
            lockRs.close();
          }
        } finally {
          lockCs.close();
        }
        if (opType.equals(LockAction.LOCK)) {
          dropUserConnections(user, dwhDbaConn);
        }
      }
      result = true;
    } catch (Exception e) {
      log.severe("Failed to " + opType.toString().toLowerCase() + " user '" + currentUser + "'");
      e.printStackTrace();
      result = false;
    }
    if (locked.size() != userList.size()) {
      LockAction undoOp = LockAction.LOCK;
      if (opType.equals(LockAction.LOCK)) {
        undoOp = LockAction.UNLOCK;
      }
      if (undoIfErrors) {
        doLockUnlock(locked, false, dwhDbaConn, undoOp);
      } else {
        log.warning("DBA needs to manually " + undoOp + " users " + locked + " using " + undoOp
            + "_user command (sp_iqlistlockedusers to show current locked users)");
      }
    }
    return result;
  }

  /**
   * Drops all user connections by given user name.
   * 
   * @param user
   *          userid to drop
   * @param dwhDbaConn
   *          DBA rock connection for dwh database
   * @throws SQLException
   */
  private void dropUserConnections(final String user, final RockFactory dwhDbaConn) throws SQLException {
    final String dropSql = String.format(CALL_DROP_USER_CONNECTIONS, user);
    final CallableStatement dropCs = dwhDbaConn.getConnection().prepareCall(dropSql);
    try {
      log.finest("execute: " + dropSql);
      final ResultSet dropRs = dropCs.executeQuery();
      try {
        while (dropRs.next()) {
          final String dropResult = dropRs.getString(MSG);
          log.info(dropResult);
        }
      } finally {
        dropRs.close();
      }
    } finally {
      dropCs.close();
    }
  }
  
}
