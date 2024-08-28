#!/bin/bash
#----------------------------------------------------------------------
# Ericsson Network IQ Installer installation script
#
# Usage: install_installer.sh [-v]
#
# ---------------------------------------------------------------------
# Copyright (c) 1999 - 2015 AB LM Ericsson Oy  All rights reserved.
# ---------------------------------------------------------------------

############## THE SCRIPT BEGINS HERE ##############

NAWK=/usr/bin/nawk
VERBOSE=0

if [ "$1" = "-v" ] ; then
  VERBOSE=1
fi

if [ -z "${CONF_DIR}" ] ; then
  echo "ERROR: CONF_DIR is not set"
  exit 1
fi

. ${CONF_DIR}/niq.rc

TIMESTAMP=`date +%d.%m.%y_%H:%M:%S`

LOGFILE=${LOG_DIR}/platform_installer/installer_${TIMESTAMP}.log

if [ -z "${INSTALLER_DIR}" ] ; then
  echo "ERROR: INSTALLER_DIR is not defined" | tee -a ${LOGFILE}
  exit 2
fi

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing platform_installer" | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/platform_installer ] ; then
  rm -f ${INSTALLER_DIR}/platform_installer | tee -a ${LOGFILE}
fi
cp installer/platform_installer ${INSTALLER_DIR}/platform_installer | tee -a ${LOGFILE}
chmod 540 ${INSTALLER_DIR}/platform_installer | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/tasks_platform_installer.xml ] ; then
  rm -f ${INSTALLER_DIR}/tasks_platform_installer.xml | tee -a ${LOGFILE}
fi
cp installer/tasks_platform_installer.xml ${INSTALLER_DIR}/tasks_platform_installer.xml | tee -a ${LOGFILE}
chmod 440 ${INSTALLER_DIR}/tasks_platform_installer.xml | tee -a ${LOGFILE}

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing tp_installer" | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/tp_installer ] ; then
  rm -f ${INSTALLER_DIR}/tp_installer | tee -a ${LOGFILE}
fi
cp installer/tp_installer ${INSTALLER_DIR}/tp_installer | tee -a ${LOGFILE}
chmod 540 ${INSTALLER_DIR}/tp_installer | tee -a ${LOGFILE}

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing set_correct_option_value.bsh" | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/set_correct_option_value.bsh ] ; then
  rm -f ${INSTALLER_DIR}/set_correct_option_value.bsh | tee -a ${LOGFILE}
fi
cp installer/set_correct_option_value.bsh ${INSTALLER_DIR}/set_correct_option_value.bsh | tee -a ${LOGFILE}
chmod 540 ${INSTALLER_DIR}/set_correct_option_value.bsh | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/tpec ] ; then
  rm -f ${INSTALLER_DIR}/tpec | tee -a ${LOGFILE}
fi

if [ -f ${INSTALLER_DIR}/tasks_tp_installer.xml ] ; then
  rm -f ${INSTALLER_DIR}/tasks_tp_installer.xml | tee -a ${LOGFILE}
fi
cp installer/tasks_tp_installer.xml ${INSTALLER_DIR}/tasks_tp_installer.xml | tee -a ${LOGFILE}
chmod 440 ${INSTALLER_DIR}/tasks_tp_installer.xml | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/tasks_install_utils.xml ] ; then
  rm -f ${INSTALLER_DIR}/tasks_install_utils.xml | tee -a ${LOGFILE}
fi
cp installer/tasks_install_utils.xml ${INSTALLER_DIR}/tasks_install_utils.xml | tee -a ${LOGFILE}
chmod 440 ${INSTALLER_DIR}/tasks_install_utils.xml | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/tasks_extract_reportpacks.xml ] ; then
  rm -f ${INSTALLER_DIR}/tasks_extract_reportpacks.xml | tee -a ${LOGFILE}
fi
cp installer/tasks_extract_reportpacks.xml ${INSTALLER_DIR}/tasks_extract_reportpacks.xml | tee -a ${LOGFILE}
chmod 440 ${INSTALLER_DIR}/tasks_extract_reportpacks.xml | tee -a ${LOGFILE}

if [ ! -d ${INSTALLER_DIR}/lib ] ; then
  mkdir ${INSTALLER_DIR}/lib | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/lib/installer.jar ] ; then
  rm -f ${INSTALLER_DIR}/lib/installer.jar | tee -a ${LOGFILE}
fi
cp lib/installer.jar ${INSTALLER_DIR}/lib/ | tee -a ${LOGFILE}
chmod 440 ${INSTALLER_DIR}/lib/installer.jar | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/extract_report_packages.bsh ] ; then
  rm -f ${INSTALLER_DIR}/extract_report_packages.bsh | tee -a ${LOGFILE}
fi
cp installer/extract_report_packages.bsh ${INSTALLER_DIR}/extract_report_packages.bsh | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/extract_report_packages.bsh | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/activate_interface ] ; then
  rm -f ${INSTALLER_DIR}/activate_interface | tee -a ${LOGFILE}
fi
cp installer/activate_interface ${INSTALLER_DIR}/activate_interface | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/activate_interface | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/deactivate_interface ] ; then
  rm -f ${INSTALLER_DIR}/deactivate_interface | tee -a ${LOGFILE}
fi
cp installer/deactivate_interface ${INSTALLER_DIR}/deactivate_interface | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/deactivate_interface | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/reactivate_interfaces ] ; then
  rm -f ${INSTALLER_DIR}/reactivate_interfaces | tee -a ${LOGFILE}
fi
cp installer/reactivate_interfaces ${INSTALLER_DIR}/reactivate_interfaces | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/reactivate_interfaces | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/installed_techpacks ] ; then
  rm -f ${INSTALLER_DIR}/installed_techpacks | tee -a ${LOGFILE}
fi
cp installer/installed_techpacks ${INSTALLER_DIR}/installed_techpacks | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/installed_techpacks | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/get_active_interfaces ] ; then
  rm -f ${INSTALLER_DIR}/get_active_interfaces| tee -a ${LOGFILE}
fi
cp installer/get_active_interfaces ${INSTALLER_DIR}/get_active_interfaces | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/get_active_interfaces | tee -a ${LOGFILE}

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing snapshot_functions.bsh" | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/snapshot_functions.bsh ] ; then
  rm -f ${INSTALLER_DIR}/snapshot_functions.bsh | tee -a ${LOGFILE}
fi
cp installer/snapshot_functions.bsh ${INSTALLER_DIR}/snapshot_functions.bsh | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/snapshot_functions.bsh | tee -a ${LOGFILE}

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing change_db_users_perm.bsh" | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/change_db_users_perm.bsh ] ; then
  rm -f ${INSTALLER_DIR}/change_db_users_perm.bsh | tee -a ${LOGFILE}
fi
cp installer/change_db_users_perm.bsh ${INSTALLER_DIR}/change_db_users_perm.bsh | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/change_db_users_perm.bsh | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/run_dir_checker.bsh ] ; then
  rm -f ${INSTALLER_DIR}/run_dir_checker.bsh | tee -a ${LOGFILE}
fi
cp installer/run_dir_checker.bsh ${INSTALLER_DIR}/run_dir_checker.bsh | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/run_dir_checker.bsh | tee -a ${LOGFILE}

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing eniq_events_upgrade" | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/eniq_events_upgrade.bsh ] ; then
  rm -f ${INSTALLER_DIR}/eniq_events_upgrade.bsh | tee -a ${LOGFILE}
fi
cp installer/eniq_events_upgrade.bsh ${INSTALLER_DIR}/eniq_events_upgrade.bsh | tee -a ${LOGFILE}
chmod 540 ${INSTALLER_DIR}/eniq_events_upgrade.bsh | tee -a ${LOGFILE}

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing restore_dwhdb_database" | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/restore_dwhdb_database ] ; then
  rm -f ${INSTALLER_DIR}/restore_dwhdb_database | tee -a ${LOGFILE}
fi
cp installer/restore_dwhdb_database ${INSTALLER_DIR}/restore_dwhdb_database | tee -a ${LOGFILE}
chmod 540 ${INSTALLER_DIR}/restore_dwhdb_database | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/tasks_restore_dwhdb_utils.xml ] ; then
  rm -f ${INSTALLER_DIR}/tasks_restore_dwhdb_utils.xml | tee -a ${LOGFILE}
fi
cp installer/tasks_restore_dwhdb_utils.xml ${INSTALLER_DIR}/tasks_restore_dwhdb_utils.xml | tee -a ${LOGFILE}
chmod 440 ${INSTALLER_DIR}/tasks_restore_dwhdb_utils.xml | tee -a ${LOGFILE}

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing roll_over_partition" | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/partition_roll_over ] ; then
  rm -f ${INSTALLER_DIR}/partition_roll_over | tee -a ${LOGFILE}
fi
cp installer/partition_roll_over ${INSTALLER_DIR}/partition_roll_over | tee -a ${LOGFILE}
chmod 540 ${INSTALLER_DIR}/partition_roll_over | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/tasks_partition_roll_over_to_old_techpack.xml ] ; then
  rm -f ${INSTALLER_DIR}/tasks_partition_roll_over_to_old_techpack.xml | tee -a ${LOGFILE}
fi
cp installer/tasks_partition_roll_over_to_old_techpack.xml ${INSTALLER_DIR}/tasks_partition_roll_over_to_old_techpack.xml | tee -a ${LOGFILE}
chmod 440 ${INSTALLER_DIR}/tasks_partition_roll_over_to_old_techpack.xml | tee -a ${LOGFILE}

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing add_agg_flag" | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/add_agg_flag ] ; then
  rm -f ${INSTALLER_DIR}/add_agg_flag | tee -a ${LOGFILE}
fi
cp installer/add_agg_flag ${INSTALLER_DIR}/add_agg_flag | tee -a ${LOGFILE}
chmod 540 ${INSTALLER_DIR}/add_agg_flag | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/tasks_add_agg_flag_to_old_techpack.xml ] ; then
  rm -f ${INSTALLER_DIR}/tasks_add_agg_flag_to_old_techpack.xml | tee -a ${LOGFILE}
fi
cp installer/tasks_add_agg_flag_to_old_techpack.xml ${INSTALLER_DIR}/tasks_add_agg_flag_to_old_techpack.xml | tee -a ${LOGFILE}
chmod 440 ${INSTALLER_DIR}/tasks_add_agg_flag_to_old_techpack.xml | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/tasks_execute_partiton_upgrade_sql.xml ] ; then
  rm -f ${INSTALLER_DIR}/tasks_execute_partiton_upgrade_sql.xml | tee -a ${LOGFILE}
fi
cp installer/tasks_execute_partiton_upgrade_sql.xml ${INSTALLER_DIR}/tasks_execute_partiton_upgrade_sql.xml | tee -a ${LOGFILE}
chmod 440 ${INSTALLER_DIR}/tasks_execute_partiton_upgrade_sql.xml | tee -a ${LOGFILE}

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing datetime_minute_upgrade" | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/datetime_minute_upgrade.bsh ] ; then
  rm -f ${INSTALLER_DIR}/datetime_minute_upgrade.bsh | tee -a ${LOGFILE}
fi
cp installer/datetime_minute_upgrade.bsh ${INSTALLER_DIR}/datetime_minute_upgrade.bsh | tee -a ${LOGFILE}
chmod 540 ${INSTALLER_DIR}/datetime_minute_upgrade.bsh | tee -a ${LOGFILE}

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing change_dc_db_url" | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/change_dc_db_url.bsh ] ; then
  rm -f ${INSTALLER_DIR}/change_dc_db_url.bsh | tee -a ${LOGFILE}
fi
cp installer/change_dc_db_url.bsh ${INSTALLER_DIR}/change_dc_db_url.bsh | tee -a ${LOGFILE}
chmod 540 ${INSTALLER_DIR}/change_dc_db_url.bsh | tee -a ${LOGFILE}

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing run_if_file_exists.bsh" | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/run_if_file_exists.bsh ] ; then
  rm -f ${INSTALLER_DIR}/run_if_file_exists.bsh | tee -a ${LOGFILE}
fi
cp installer/run_if_file_exists.bsh ${INSTALLER_DIR}/run_if_file_exists.bsh | tee -a ${LOGFILE}
chmod 540 ${INSTALLER_DIR}/run_if_file_exists.bsh | tee -a ${LOGFILE}

if [ -f ${BIN_DIR}/set_maxper_run_value.bsh ] ; then
  rm -f ${BIN_DIR}/set_maxper_run_value.bsh | tee -a ${LOGFILE}
fi
#Fix for HU53026
#cp installer/set_maxper_run_value.bsh ${BIN_DIR}/set_maxper_run_value.bsh | tee -a ${LOGFILE}
#chmod 550 ${BIN_DIR}/set_maxper_run_value.bsh | tee -a ${LOGFILE}

if [ -f ${BIN_DIR}/PLM_Merge_Scripts.bsh ] ; then
  rm -f ${BIN_DIR}/PLM_Merge_Scripts.bsh | tee -a ${LOGFILE}
fi
cp installer/PLM_Merge_Scripts.bsh ${BIN_DIR}/PLM_Merge_Scripts.bsh | tee -a ${LOGFILE}
chmod 550 ${BIN_DIR}/PLM_Merge_Scripts.bsh | tee -a ${LOGFILE}

if [ -f ${BIN_DIR}/remove_duplicates_DIM_E_GRAN_TG.bsh ] ; then
  rm -f ${BIN_DIR}/remove_duplicates_DIM_E_GRAN_TG.bsh | tee -a ${LOGFILE}
fi
cp installer/remove_duplicates_DIM_E_GRAN_TG.bsh ${BIN_DIR}/remove_duplicates_DIM_E_GRAN_TG.bsh | tee -a ${LOGFILE}
chmod 550 ${BIN_DIR}/remove_duplicates_DIM_E_GRAN_TG.bsh | tee -a ${LOGFILE}

if [ -f ${BIN_DIR}/retain_histdata_DIM_E_LTE_EUCELL_CELL.bsh ] ; then
  rm -f ${BIN_DIR}/retain_histdata_DIM_E_LTE_EUCELL_CELL.bsh | tee -a ${LOGFILE}
fi
cp installer/retain_histdata_DIM_E_LTE_EUCELL_CELL.bsh ${BIN_DIR}/retain_histdata_DIM_E_LTE_EUCELL_CELL.bsh | tee -a ${LOGFILE}
chmod 550 ${BIN_DIR}/retain_histdata_DIM_E_LTE_EUCELL_CELL.bsh | tee -a ${LOGFILE}

if [ -f ${BIN_DIR}/change_DIM_E_RAN_RBSLOCALCELL.bsh ] ; then
  rm -f ${BIN_DIR}/change_DIM_E_RAN_RBSLOCALCELL.bsh | tee -a ${LOGFILE}
fi
cp installer/change_DIM_E_RAN_RBSLOCALCELL.bsh ${BIN_DIR}/change_DIM_E_RAN_RBSLOCALCELL.bsh | tee -a ${LOGFILE}
chmod 550 ${BIN_DIR}/change_DIM_E_RAN_RBSLOCALCELL.bsh | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/change_catalog_cache.bsh ] ; then
  rm -f ${INSTALLER_DIR}/change_catalog_cache.bsh | tee -a ${LOGFILE}
fi
cp installer/change_catalog_cache.bsh ${INSTALLER_DIR}/change_catalog_cache.bsh | tee -a ${LOGFILE}
chmod 540 ${INSTALLER_DIR}/change_catalog_cache.bsh | tee -a ${LOGFILE}

if [ -f ${BIN_DIR}/MaxFilePerRunReduced.jar ] ; then
  rm -f ${BIN_DIR}/MaxFilePerRunReduced.jar | tee -a ${LOGFILE}
fi
#Fix for HU53026	
#cp installer/MaxFilePerRunReduced.jar ${BIN_DIR}/MaxFilePerRunReduced.jar | tee -a ${LOGFILE}
#chmod 550 ${BIN_DIR}/MaxFilePerRunReduced.jar | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/post_rollback.bsh ] ; then
  rm -f ${INSTALLER_DIR}/post_rollback.bsh | tee -a ${LOGFILE}
fi
cp installer/post_rollback.bsh ${INSTALLER_DIR}/post_rollback.bsh | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/post_rollback.bsh | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/post_integration_script.bsh ] ; then
  rm -f ${INSTALLER_DIR}/post_integration_script.bsh | tee -a ${LOGFILE}
fi
cp installer/post_integration_script.bsh ${INSTALLER_DIR}/post_integration_script.bsh | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/post_integration_script.bsh | tee -a ${LOGFILE}

if [ -f ${ADMIN_BIN}/wifipoller.bsh ] ; then
  rm -f ${ADMIN_BIN}/wifipoller.bsh | tee -a ${LOGFILE}
fi
cp installer/wifipoller.bsh ${ADMIN_BIN}/wifipoller.bsh | tee -a ${LOGFILE}
chmod 550 ${ADMIN_BIN}/wifipoller.bsh | tee -a ${LOGFILE}

if [ -f ${ADMIN_BIN}/twamppoller.bsh ] ; then
  rm -f ${ADMIN_BIN}/twamppoller.bsh | tee -a ${LOGFILE}
fi

if [ -f ${BIN_DIR}/remove_hidden_files.bsh ] ; then
  rm -f ${BIN_DIR}/remove_hidden_files.bsh | tee -a ${LOGFILE}
fi
cp installer/remove_hidden_files.bsh ${BIN_DIR}/remove_hidden_files.bsh | tee -a ${LOGFILE}
chmod 750 ${BIN_DIR}/remove_hidden_files.bsh | tee -a ${LOGFILE};

# For EQEV-34540
if [ -f ${BIN_DIR}/NetAnFileHandler.sh ] ; then
  rm -f ${BIN_DIR}/NetAnFileHandler.sh | tee -a ${LOGFILE}
fi
cp installer/NetAnFileHandler.sh ${BIN_DIR}/NetAnFileHandler.sh | tee -a ${LOGFILE}
chmod 750 ${BIN_DIR}/NetAnFileHandler.sh | tee -a ${LOGFILE};

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing SOEM/IPTNMS setup files" | tee -a ${LOGFILE}
fi

# Adding IPTNMS/SOEM Import File
if [ -f ${BIN_DIR}/soem_iptnms_import.bsh ] ; then
  rm -f ${BIN_DIR}/soem_iptnms_import.bsh | tee -a ${LOGFILE}
fi
cp installer/soem_iptnms_import.bsh ${BIN_DIR}/soem_iptnms_import.bsh | tee -a ${LOGFILE}
chmod 755 ${BIN_DIR}/soem_iptnms_import.bsh | tee -a ${LOGFILE}

# Adding IPTNMS/SOEM Setup File
if [ -f ${BIN_DIR}/soem_iptnms_setup.bsh ] ; then
  rm -f ${BIN_DIR}/soem_iptnms_setup.bsh | tee -a ${LOGFILE}
fi
cp installer/soem_iptnms_setup.bsh ${BIN_DIR}/soem_iptnms_setup.bsh | tee -a ${LOGFILE}
chmod 755 ${BIN_DIR}/soem_iptnms_setup.bsh | tee -a ${LOGFILE}

# Adding IPTNMS/SOEM Config File
if [ ! -f ${CONF_DIR}/soem_iptnms_config.ini ] ; then
  cp installer/soem_iptnms_config.ini ${CONF_DIR}/soem_iptnms_config.ini | tee -a ${LOGFILE}
  chmod 755 ${CONF_DIR}/soem_iptnms_config.ini | tee -a ${LOGFILE}
#start - Fix for TR HT82252
elif [ -f ${CONF_DIR}/soem_iptnms_config.ini ] ; then
        grep -c "\*_Configuration_Data_\*" ${CONF_DIR}/soem_iptnms_config.ini > /dev/null 2>&1
        if [ $? = 0 ]; then
                cp installer/soem_iptnms_config.ini ${CONF_DIR}/soem_iptnms_config.ini | tee -a ${LOGFILE}
                chmod 755 ${CONF_DIR}/soem_iptnms_config.ini | tee -a ${LOGFILE}
        fi
fi

if [ -f ${INSTALLER_DIR}/data_delete.sh ] ; then
  rm -f ${INSTALLER_DIR}/data_delete.sh | tee -a ${LOGFILE}
fi
cp installer/data_delete.sh ${INSTALLER_DIR}/data_delete.sh | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/data_delete.sh | tee -a ${LOGFILE}
#end - Fix for TR HT82252

# Adding remove_mediator File
if [ -f ${ADMIN_BIN}/remove_mediator.bsh ] ; then
  rm -f ${ADMIN_BIN}/remove_mediator.bsh | tee -a ${LOGFILE}
fi
cp installer/remove_mediator.bsh ${ADMIN_BIN}/remove_mediator.bsh | tee -a ${LOGFILE}
chmod 755 ${ADMIN_BIN}/remove_mediator.bsh | tee -a ${LOGFILE}

if [ -f ${BIN_DIR}/count_repbd_dwhdb_conn.bsh ] ; then
  rm -f ${BIN_DIR}/count_repbd_dwhdb_conn.bsh | tee -a ${LOGFILE}
fi
cp installer/count_repbd_dwhdb_conn.bsh ${BIN_DIR}/count_repbd_dwhdb_conn.bsh | tee -a ${LOGFILE}
chmod 550 ${BIN_DIR}/count_repbd_dwhdb_conn.bsh | tee -a ${LOGFILE}

if [ -f ${BIN_DIR}/count_dwhdb_conn.sql ] ; then
  rm -f ${BIN_DIR}/count_dwhdb_conn.sql | tee -a ${LOGFILE}
fi
cp installer/count_dwhdb_conn.sql ${BIN_DIR}/count_dwhdb_conn.sql | tee -a ${LOGFILE}
chmod 550 ${BIN_DIR}/count_dwhdb_conn.sql | tee -a ${LOGFILE}

if [ -f ${BIN_DIR}/count_repdb_conn.sql ] ; then
  rm -f ${BIN_DIR}/count_repdb_conn.sql | tee -a ${LOGFILE}
fi
cp installer/count_repdb_conn.sql ${BIN_DIR}/count_repdb_conn.sql | tee -a ${LOGFILE}
chmod 550 ${BIN_DIR}/count_repdb_conn.sql | tee -a ${LOGFILE}

if [ -f ${BIN_DIR}/create_rep_dwh_temp.sql ] ; then
  rm -f ${BIN_DIR}/create_rep_dwh_temp.sql | tee -a ${LOGFILE}
fi
cp installer/create_rep_dwh_temp.sql ${BIN_DIR}/create_rep_dwh_temp.sql | tee -a ${LOGFILE}
chmod 550 ${BIN_DIR}/create_rep_dwh_temp.sql | tee -a ${LOGFILE}

if [ -f ${BIN_DIR}/Topo_Null_Check.bsh ] ; then
  rm -f ${BIN_DIR}/Topo_Null_Check.bsh | tee -a ${LOGFILE}
fi
cp installer/Topo_Null_Check.bsh ${BIN_DIR}/Topo_Null_Check.bsh | tee -a ${LOGFILE}
chmod 550 ${BIN_DIR}/Topo_Null_Check.bsh | tee -a ${LOGFILE}

if [ -f ${ADMIN_BIN}/ServiceRestart.bsh ] ; then
  rm -f ${ADMIN_BIN}/ServiceRestart.bsh | tee -a ${LOGFILE}
fi

if [ -f ${INSTALLER_DIR}/sybase_log.bsh ] ; then
  rm -f ${INSTALLER_DIR}/sybase_log.bsh | tee -a ${LOGFILE}
fi
cp installer/sybase_log.bsh ${INSTALLER_DIR}/sybase_log.bsh  | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/sybase_log.bsh  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/platform_log.bsh ] ; then
  rm -f ${INSTALLER_DIR}/platform_log.bsh | tee -a ${LOGFILE}
fi
cp installer/platform_log.bsh ${INSTALLER_DIR}/platform_log.bsh  | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/platform_log.bsh  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/runtime_log.bsh ] ; then
  rm -f ${INSTALLER_DIR}/runtime_log.bsh | tee -a ${LOGFILE}
fi

if [ -f ${BIN_DIR}/monitor_heap.bsh ] ; then
  rm -f ${BIN_DIR}/monitor_heap.bsh | tee -a ${LOGFILE}
fi
cp installer/monitor_heap.bsh ${BIN_DIR}/monitor_heap.bsh  | tee -a ${LOGFILE}
chmod 550 ${BIN_DIR}/monitor_heap.bsh  | tee -a ${LOGFILE}

if [ -f ${BIN_DIR}/monitor_cache_usage.bsh ] ; then
  rm -f ${BIN_DIR}/monitor_cache_usage.bsh | tee -a ${LOGFILE}
fi
cp installer/monitor_cache_usage.bsh ${BIN_DIR}/monitor_cache_usage.bsh  | tee -a ${LOGFILE}
chmod 550 ${BIN_DIR}/monitor_cache_usage.bsh | tee -a ${LOGFILE}


if [ -f ${BIN_DIR}/create_engine_heap.sql ] ; then
 rm -f ${BIN_DIR}/create_engine_heap.sql | tee -a ${LOGFILE}
fi
cp installer/create_engine_heap.sql ${BIN_DIR}/create_engine_heap.sql | tee -a ${LOGFILE}
chmod 550 ${BIN_DIR}/create_engine_heap.sql | tee -a ${LOGFILE}


if [ -f ${BIN_DIR}/create_monitor_cache.sql ] ; then 
 rm -f ${BIN_DIR}/create_monitor_cache.sql | tee -a ${LOGFILE}
fi
cp installer/create_monitor_cache.sql ${BIN_DIR}/create_monitor_cache.sql | tee -a ${LOGFILE}
chmod 550 ${BIN_DIR}/create_monitor_cache.sql | tee -a ${LOGFILE}

if [ -f ${BIN_DIR}/MovedFailedLoaderFile.bsh ] ; then 
 rm -f ${BIN_DIR}/MovedFailedLoaderFile.bsh | tee -a ${LOGFILE}
fi
cp installer/MovedFailedLoaderFile.bsh ${BIN_DIR}/MovedFailedLoaderFile.bsh | tee -a ${LOGFILE}
chmod 755 ${BIN_DIR}/MovedFailedLoaderFile.bsh | tee -a ${LOGFILE}

if [ -f ${BIN_DIR}/DiskManager_AllInterface.bsh ] ; then 
 rm -f ${BIN_DIR}/DiskManager_AllInterface.bsh | tee -a ${LOGFILE}
fi
cp installer/DiskManager_AllInterface.bsh ${BIN_DIR}/DiskManager_AllInterface.bsh | tee -a ${LOGFILE}
chmod 755 ${BIN_DIR}/DiskManager_AllInterface.bsh | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/generate_erbs_combined_view.sql ] ; then
  rm -f ${INSTALLER_DIR}/generate_erbs_combined_view.sql | tee -a ${LOGFILE}
fi
cp installer/generate_erbs_combined_view.sql ${INSTALLER_DIR}/generate_erbs_combined_view.sql  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/generate_erbs_combined_view.sql  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/erbscombinedview.bsh ] ; then
  rm -f ${INSTALLER_DIR}/erbscombinedview.bsh | tee -a ${LOGFILE}
fi
cp installer/erbscombinedview.bsh ${INSTALLER_DIR}/erbscombinedview.bsh  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/erbscombinedview.bsh  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/generate_DIM_E_LTE_LLE_EUCELL_view.sql ] ; then
  rm -f ${INSTALLER_DIR}/generate_DIM_E_LTE_LLE_EUCELL_view.sql | tee -a ${LOGFILE}
fi
cp installer/generate_DIM_E_LTE_LLE_EUCELL_view.sql ${INSTALLER_DIR}/generate_DIM_E_LTE_LLE_EUCELL_view.sql  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/generate_DIM_E_LTE_LLE_EUCELL_view.sql  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/generate_DIM_E_LTE_LLE_ERBS_view.sql ] ; then
  rm -f ${INSTALLER_DIR}/generate_DIM_E_LTE_LLE_ERBS_view.sql | tee -a ${LOGFILE}
fi
cp installer/generate_DIM_E_LTE_LLE_ERBS_view.sql ${INSTALLER_DIR}/generate_DIM_E_LTE_LLE_ERBS_view.sql  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/generate_DIM_E_LTE_LLE_ERBS_view.sql  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/generate_rbs_combined_view.sql ] ; then
  rm -f ${INSTALLER_DIR}/generate_rbs_combined_view.sql | tee -a ${LOGFILE}
fi
cp installer/generate_rbs_combined_view.sql ${INSTALLER_DIR}/generate_rbs_combined_view.sql  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/generate_rbs_combined_view.sql  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/WCDMACombinedViewConfigFile.csv ] ; then
  rm -f ${INSTALLER_DIR}/WCDMACombinedViewConfigFile.csv | tee -a ${LOGFILE}
fi
cp installer/WCDMACombinedViewConfigFile.csv ${INSTALLER_DIR}/WCDMACombinedViewConfigFile.csv  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/WCDMACombinedViewConfigFile.csv  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/WCDMACombinedViewCreation.bsh ] ; then
  rm -f ${INSTALLER_DIR}/WCDMACombinedViewCreation.bsh | tee -a ${LOGFILE}
fi
cp installer/WCDMACombinedViewCreation.bsh ${INSTALLER_DIR}/WCDMACombinedViewCreation.bsh  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/WCDMACombinedViewCreation.bsh  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/dcpublic_wcdma.sql ] ; then
  rm -f ${INSTALLER_DIR}/dcpublic_wcdma.sql | tee -a ${LOGFILE}
fi
cp installer/dcpublic_wcdma.sql ${INSTALLER_DIR}/dcpublic_wcdma.sql  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/dcpublic_wcdma.sql  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/dcpublic_erbs.sql ] ; then
  rm -f ${INSTALLER_DIR}/dcpublic_erbs.sql | tee -a ${LOGFILE}
fi
cp installer/dcpublic_erbs.sql ${INSTALLER_DIR}/dcpublic_erbs.sql | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/dcpublic_erbs.sql  | tee -a ${LOGFILE}


if [ -f ${INSTALLER_DIR}/install_parsers.bsh ] ; then
  rm -f ${INSTALLER_DIR}/install_parsers.bsh | tee -a ${LOGFILE}
fi
cp installer/install_parsers.bsh ${INSTALLER_DIR}/install_parsers.bsh  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/install_parsers.bsh  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/disableOrEnableBusyHourSets.bsh ] ; then
  rm -f ${INSTALLER_DIR}/disableOrEnableBusyHourSets.bsh | tee -a ${LOGFILE}
fi
cp installer/disableOrEnableBusyHourSets.bsh ${INSTALLER_DIR}/disableOrEnableBusyHourSets.bsh  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/disableOrEnableBusyHourSets.bsh  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/deltaviewcreation.bsh ] ; then
  rm -f ${INSTALLER_DIR}/deltaviewcreation.bsh | tee -a ${LOGFILE}
fi
cp installer/deltaviewcreation.bsh ${INSTALLER_DIR}/deltaviewcreation.bsh  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/deltaviewcreation.bsh  | tee -a ${LOGFILE} 


if [ -f ${INSTALLER_DIR}/configure_newkeystore.sh ] ; then
  rm -f ${INSTALLER_DIR}/configure_newkeystore.sh | tee -a ${LOGFILE}
fi
cp installer/configure_newkeystore.sh ${INSTALLER_DIR}/configure_newkeystore.sh  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/configure_newkeystore.sh  | tee -a ${LOGFILE} 

if [ -f ${ADMIN_BIN}/scheduler_check.bsh ] ; then
  rm -f ${ADMIN_BIN}/scheduler_check.bsh | tee -a ${LOGFILE}
fi

if [ -f ${ADMIN_BIN}/FileSystemCheck.bsh ] ; then
  rm -f ${ADMIN_BIN}/FileSystemCheck.bsh | tee -a ${LOGFILE}
fi
cp installer/FileSystemCheck.bsh ${ADMIN_BIN}/FileSystemCheck.bsh  | tee -a ${LOGFILE}
chmod 650 ${ADMIN_BIN}/FileSystemCheck.bsh  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/delete_duplicate_corrective.bsh ] ; then
  rm -f ${INSTALLER_DIR}/delete_duplicate_corrective.bsh | tee -a ${LOGFILE}
fi
cp installer/delete_duplicate_corrective.bsh ${INSTALLER_DIR}/delete_duplicate_corrective.bsh  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/delete_duplicate_corrective.bsh  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/delete_duplicate_preventive.bsh ] ; then
  rm -f ${INSTALLER_DIR}/delete_duplicate_preventive.bsh | tee -a ${LOGFILE}
fi
cp installer/delete_duplicate_preventive.bsh ${INSTALLER_DIR}/delete_duplicate_preventive.bsh  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/delete_duplicate_preventive.bsh  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/extract_BO.bsh ] ; then
  rm -f ${INSTALLER_DIR}/extract_BO.bsh | tee -a ${LOGFILE}
fi
cp installer/extract_BO.bsh ${INSTALLER_DIR}/extract_BO.bsh  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/extract_BO.bsh  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/extract_reports.bsh ] ; then
  rm -f ${INSTALLER_DIR}/extract_reports.bsh| tee -a ${LOGFILE}
fi
cp installer/extract_reports.bsh ${INSTALLER_DIR}/extract_reports.bsh  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/extract_reports.bsh  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/remove_techpack.bsh ] ; then
  rm -f ${INSTALLER_DIR}/remove_techpack.bsh| tee -a ${LOGFILE}
fi
cp installer/remove_techpack.bsh ${INSTALLER_DIR}/remove_techpack.bsh  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/remove_techpack.bsh  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/remove_techpack_from_dwhrep.sql ] ; then
  rm -f ${INSTALLER_DIR}/remove_techpack_from_dwhrep.sql| tee -a ${LOGFILE}
fi
cp installer/remove_techpack_from_dwhrep.sql ${INSTALLER_DIR}/remove_techpack_from_dwhrep.sql  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/remove_techpack_from_dwhrep.sql  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/remove_techpack_from_etlrep.sql ] ; then
  rm -f ${INSTALLER_DIR}/remove_techpack_from_etlrep.sql| tee -a ${LOGFILE}
fi
cp installer/remove_techpack_from_etlrep.sql ${INSTALLER_DIR}/remove_techpack_from_etlrep.sql  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/remove_techpack_from_etlrep.sql  | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/insane_WA.bsh ] ; then
  rm -f ${INSTALLER_DIR}/insane_WA.bsh| tee -a ${LOGFILE}
fi
cp installer/insane_WA.bsh ${INSTALLER_DIR}/insane_WA.bsh  | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/insane_WA.bsh  | tee -a ${LOGFILE}


SERVER_TYPE=`cat ${CONF_DIR}/niq.ini | grep -i server_type | $NAWK -F"=" '{print $2}'`

if [ "$SERVER_TYPE" != "stats" ]; then

	#Adding all the test harness artefacts
	if [ ! -d ${INSTALLER_DIR}/test_harness ] ; then
		mkdir ${INSTALLER_DIR}/test_harness | tee -a ${LOGFILE}
	fi
	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing test_tp_installer.bsh" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/test_tp_installer.bsh ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/test_tp_installer.bsh | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/test_tp_installer.bsh ${INSTALLER_DIR}/test_harness/test_tp_installer.bsh | tee -a ${LOGFILE}
	chmod 550 ${INSTALLER_DIR}/test_harness/test_tp_installer.bsh | tee -a ${LOGFILE}

	if [ ${VERBOSE} = 1 ] ; then	
		echo "Installing test_config_stats" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/test_config_stats ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/test_config_stats | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/test_config_stats ${INSTALLER_DIR}/test_harness/test_config_stats | tee -a ${LOGFILE}
	chmod 440 ${INSTALLER_DIR}/test_harness/test_config_stats | tee -a ${LOGFILE}

	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing test_config_events" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/test_config_events ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/test_config_events | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/test_config_events ${INSTALLER_DIR}/test_harness/test_config_events | tee -a ${LOGFILE}
	chmod 440 ${INSTALLER_DIR}/test_harness/test_config_events | tee -a ${LOGFILE}

	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing mock_output_svcs-a_controlzoneDifferent" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/mock_output_svcs-a_controlzoneDifferent ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/mock_output_svcs-a_controlzoneDifferent | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/mock_output_svcs-a_controlzoneDifferent ${INSTALLER_DIR}/test_harness/mock_output_svcs-a_controlzoneDifferent | tee -a ${LOGFILE}
	chmod 440 ${INSTALLER_DIR}/test_harness/mock_output_svcs-a_controlzoneDifferent | tee -a ${LOGFILE}

	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing mock_output_svcs-a" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/mock_output_svcs-a ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/mock_output_svcs-a | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/mock_output_svcs-a ${INSTALLER_DIR}/test_harness/mock_output_svcs-a | tee -a ${LOGFILE}
	chmod 440 ${INSTALLER_DIR}/test_harness/mock_output_svcs-a | tee -a ${LOGFILE}

	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing mock_output_getActiveInterfaces" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/mock_output_getActiveInterfaces ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/mock_output_getActiveInterfaces | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/mock_output_getActiveInterfaces ${INSTALLER_DIR}/test_harness/mock_output_getActiveInterfaces | tee -a ${LOGFILE}
	chmod 440 ${INSTALLER_DIR}/test_harness/mock_output_getActiveInterfaces | tee -a ${LOGFILE}

	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing dependency_mocks.lib" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dependency_mocks.lib ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dependency_mocks.lib | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dependency_mocks.lib ${INSTALLER_DIR}/test_harness/dependency_mocks.lib | tee -a ${LOGFILE}
	chmod 550 ${INSTALLER_DIR}/test_harness/dependency_mocks.lib | tee -a ${LOGFILE}

	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing dependency_calls.lib" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dependency_calls.lib ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dependency_calls.lib | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dependency_calls.lib ${INSTALLER_DIR}/test_harness/dependency_calls.lib | tee -a ${LOGFILE}
	chmod 550 ${INSTALLER_DIR}/test_harness/dependency_calls.lib | tee -a ${LOGFILE}

	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing dep_expect_mtas_fail" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dep_expect_mtas_fail ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dep_expect_mtas_fail | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dep_expect_mtas_fail ${INSTALLER_DIR}/test_harness/dep_expect_mtas_fail | tee -a ${LOGFILE}
	chmod 440 ${INSTALLER_DIR}/test_harness/dep_expect_mtas_fail | tee -a ${LOGFILE}

	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing dep_expect_mtas" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dep_expect_mtas ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dep_expect_mtas | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dep_expect_mtas ${INSTALLER_DIR}/test_harness/dep_expect_mtas | tee -a ${LOGFILE}
	chmod 440 ${INSTALLER_DIR}/test_harness/dep_expect_mtas | tee -a ${LOGFILE}

	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing dep_expect_ltecfa_fail" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dep_expect_ltecfa_fail ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dep_expect_ltecfa_fail | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dep_expect_ltecfa_fail ${INSTALLER_DIR}/test_harness/dep_expect_ltecfa_fail | tee -a ${LOGFILE}
	chmod 440 ${INSTALLER_DIR}/test_harness/dep_expect_ltecfa_fail | tee -a ${LOGFILE}

	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing dep_expect_ltecfa" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dep_expect_ltecfa ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dep_expect_ltecfa | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dep_expect_ltecfa ${INSTALLER_DIR}/test_harness/dep_expect_ltecfa | tee -a ${LOGFILE}
	chmod 440 ${INSTALLER_DIR}/test_harness/dep_expect_ltecfa | tee -a ${LOGFILE}

	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing cxc_num_mtas" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/cxc_num_mtas ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/cxc_num_mtas | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/cxc_num_mtas ${INSTALLER_DIR}/test_harness/cxc_num_mtas | tee -a ${LOGFILE}
	chmod 440 ${INSTALLER_DIR}/test_harness/cxc_num_mtas | tee -a ${LOGFILE}

	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing cxc_num_ltecfa" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/cxc_num_ltecfa ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/cxc_num_ltecfa | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/cxc_num_ltecfa ${INSTALLER_DIR}/test_harness/cxc_num_ltecfa | tee -a ${LOGFILE}
	chmod 440 ${INSTALLER_DIR}/test_harness/cxc_num_ltecfa | tee -a ${LOGFILE}

	##And the dummy tech packs for the test harness
	if [ ! -d ${INSTALLER_DIR}/test_harness/dummy_techpacks ] ; then
		mkdir ${INSTALLER_DIR}/test_harness/dummy_techpacks | tee -a ${LOGFILE}
	fi
	dummyTechPack=DIM_E_IMSI_IMEI_R3A_b31.tpi
	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing ${dummyTechPack}" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dummy_techpacks/${dummyTechPack} ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	chmod 777 ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}

	dummyTechPack=DIM_E_IMSI_MSISDN_R6A_b26.tpi
	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing ${dummyTechPack}" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dummy_techpacks/${dummyTechPack} ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	chmod 777 ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}

	dummyTechPack=DIM_E_LTE_CFA_R4B_b24.tpi
	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing ${dummyTechPack}" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dummy_techpacks/${dummyTechPack} ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	chmod 777 ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}

	dummyTechPack=DIM_E_LTE_R8D_b133.tpi
	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing ${dummyTechPack}" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dummy_techpacks/${dummyTechPack} ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	chmod 777 ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}

	dummyTechPack=DIM_E_SGEH_R8D_b179.tpi
	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing ${dummyTechPack}" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dummy_techpacks/${dummyTechPack} ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	chmod 777 ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}

	dummyTechPack=DWH_BASE_R8A_b83.tpi
	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing ${dummyTechPack}" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dummy_techpacks/${dummyTechPack} ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	chmod 777 ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}

	dummyTechPack=DWH_MONITOR_R12A_b104.tpi
	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing ${dummyTechPack}" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dummy_techpacks/${dummyTechPack} ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	chmod 777 ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}

	dummyTechPack=EVENT_E_LTE_CFA_R3A_b31.tpi
	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing ${dummyTechPack}" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dummy_techpacks/${dummyTechPack} ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	chmod 777 ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}

	dummyTechPack=EVENTS_DWH_BASE_R3A_b31.tpi
	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing ${dummyTechPack}" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dummy_techpacks/${dummyTechPack} ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	chmod 777 ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}

	dummyTechPack=GROUP_TYPE_E_R6A_b39.tpi
	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing ${dummyTechPack}" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dummy_techpacks/${dummyTechPack} ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	chmod 777 ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}

	dummyTechPack=INTF_DIM_E_LTE_ERBS_R1C_b3.tpi
	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing ${dummyTechPack}" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dummy_techpacks/${dummyTechPack} ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	chmod 777 ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}

	dummyTechPack=INTF_DIM_E_SGEH_3G_R3A_b15.tpi
	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing ${dummyTechPack}" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dummy_techpacks/${dummyTechPack} ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	chmod 777 ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}

	dummyTechPack=INTF_DIM_E_SGEH_R3A_b15.tpi
	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing ${dummyTechPack}" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dummy_techpacks/${dummyTechPack} ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	chmod 777 ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}

	dummyTechPack=M_E_LTEEFA_R5A_b121.tpi
	if [ ${VERBOSE} = 1 ] ; then
		echo "Installing ${dummyTechPack}" | tee -a ${LOGFILE}
	fi
	if [ -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} ] ; then
		rm -f ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	fi
	cp installer/test_harness/dummy_techpacks/${dummyTechPack} ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}
	chmod 777 ${INSTALLER_DIR}/test_harness/dummy_techpacks/${dummyTechPack} | tee -a ${LOGFILE}

else 
	echo "Skipping Test Harness artefacts on $SERVER_TYPE"
 fi
 
# ---------------------------------------------------------------------
# Adding hosts_file_update.sh to automate hosts file updation
# ---------------------------------------------------------------------

if [ -f ${INSTALLER_DIR}/hosts_file_update.sh ] ; then
  rm -f ${INSTALLER_DIR}/hosts_file_update.sh | tee -a ${LOGFILE}
fi
cp installer/hosts_file_update.sh ${INSTALLER_DIR}/hosts_file_update.sh | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/hosts_file_update.sh | tee -a ${LOGFILE}

# ---------------------------------------------------------------------
# Adding retrieve_from_db.sh to retrieve alarm password from database
#fix for EQEV-30561 EQEV-30684
# ---------------------------------------------------------------------

if [ -f ${INSTALLER_DIR}/retrieve_from_db.sh ] ; then
  rm -f ${INSTALLER_DIR}/retrieve_from_db.sh | tee -a ${LOGFILE}
fi
cp installer/retrieve_from_db.sh ${INSTALLER_DIR}/retrieve_from_db.sh | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/retrieve_from_db.sh | tee -a ${LOGFILE}


# ---------------------------------------------------------------------
# Adding store_to_db.sh to store the password to database
#fix for EQEV-30561 EQEV-30684
# ---------------------------------------------------------------------

if [ -f ${INSTALLER_DIR}/store_to_db.sh ] ; then
  rm -f ${INSTALLER_DIR}/store_to_db.sh | tee -a ${LOGFILE}
fi
cp installer/store_to_db.sh ${INSTALLER_DIR}/store_to_db.sh | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/store_to_db.sh | tee -a ${LOGFILE}


# ---------------------------------------------------------------------
# Adding manage_alarm_reports.sh
#fix for EQEV-33129
# ---------------------------------------------------------------------

if [ -f ${INSTALLER_DIR}/manage_alarm_reports.sh ] ; then
  rm -f ${INSTALLER_DIR}/manage_alarm_reports.sh | tee -a ${LOGFILE}
fi
cp installer/manage_alarm_reports.sh ${INSTALLER_DIR}/manage_alarm_reports.sh | tee -a ${LOGFILE}
chmod 755 ${INSTALLER_DIR}/manage_alarm_reports.sh | tee -a ${LOGFILE}

# ---------------------------------------------------------------------
# Update or create versiondb.properties
# ---------------------------------------------------------------------

if [ ${VERBOSE} = 1 ] ; then
  echo "Updating version database..." | tee -a ${LOGFILE}
fi

TVER=`cat install/version.properties | grep module.version`
TBLD=`cat install/version.properties | grep module.build`

VER=${TVER##*=}
BLD=${TBLD##*=}

VTAG="module.installer=${VER}b${BLD}"

if [ ! -f ${INSTALLER_DIR}/versiondb.properties ] ; then

  echo "${VTAG}" > ${INSTALLER_DIR}/versiondb.properties
  chmod 640 ${INSTALLER_DIR}/versiondb.properties

else

  OLD=`cat ${INSTALLER_DIR}/versiondb.properties | grep module.installer`

  if [ -z "${OLD}" ] ; then
    echo "${VTAG}" >> ${INSTALLER_DIR}/versiondb.properties
  else
    cp ${INSTALLER_DIR}/versiondb.properties ${INSTALLER_DIR}/versiondb.properties.tmp
    sed -e "/${OLD}/s//${VTAG}/g" ${INSTALLER_DIR}/versiondb.properties.tmp > ${INSTALLER_DIR}/versiondb.properties
    rm ${INSTALLER_DIR}/versiondb.properties.tmp
  fi

fi

if [ $VERBOSE = 1 ] ; then
  echo "Installer installed" | tee -a ${LOGFILE}
fi

exit 0


