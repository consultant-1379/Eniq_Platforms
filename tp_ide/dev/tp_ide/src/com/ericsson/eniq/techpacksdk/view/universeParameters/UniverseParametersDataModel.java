package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Universeparameters;
import com.distocraft.dc5000.repository.dwhrep.UniverseparametersFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;

public class UniverseParametersDataModel extends GenericUniverseDataModel<RockDBObject> {

	private static final Logger logger = Logger.getLogger(UniverseParametersDataModel.class.getName());

	private GenericUniverseTableModel<Universeparameters> universeParametersTableModel;
	
	public UniverseParametersDataModel(RockFactory etlRock) {
		super(etlRock);
		try {
			this.universeParametersTableModel = createTableModel(null);
		} catch (Exception e) {
			logger.warning("Could not create TableModel for UniverseParametersDataModel.\n" + e);
		}
	}
	
	@Override
	public void refresh(Versioning v) {
		try {
			this.versioning = v;
			this.universeParametersTableModel = createTableModel(v);
		} catch (Exception e) {
			logger.warning("Could not refresh UniverseParametersDataModel.\n" + e);
		}
	}
	
	public String getVersionId() {
		if (this.versioning != null) {
			return this.versioning.getVersionid();
		}
		else {
			return null;
		}
	}

	@Override
	public GenericUniverseTableModel<Universeparameters> getTableModel() {
		return this.universeParametersTableModel;
	}

	@Override
	public GenericUniverseTableModel<Universeparameters> createTableModel(Versioning v) throws RockException, SQLException {

		Vector<Universeparameters> universeParametersVector = null;
	    if (v != null) {
	      Universeparameters uP = new Universeparameters(this.etlRock);
	      uP.setVersionid(v.getVersionid());
	      UniverseparametersFactory uPFactory = new UniverseparametersFactory(this.etlRock, uP, "order by ordernro");
	      universeParametersVector = uPFactory.get();
	    }
	    UniverseParametersTableModel result = new UniverseParametersTableModel(universeParametersVector,
	    																   this.etlRock);
	    return result;
	}

	public void deleteItemsWithCurrentVersionId() throws Exception  {
		// Get versionid, and throw an exception if it is null
		String versionId = this.versioning.getVersionid();
		if (versionId == null || versionId.equals("")) {
			throw new Exception("Trying to delete without versionID");
		}
		
		// Versionid was valid. Next, delete all items with the versionId.
		try {
			Universeparameters universeParameters = new Universeparameters(this.etlRock);
			universeParameters.setVersionid(versionId);

			UniverseparametersFactory uPF = new UniverseparametersFactory(etlRock, universeParameters);
			Iterator<Universeparameters> deletionIterator = uPF.get().iterator();

			while (deletionIterator.hasNext()) {
				Universeparameters deletionItem = deletionIterator.next();
				deletionItem.deleteDB();
			}
		} catch (Exception e) {
			logger.warning("Unable to delete Universeparameters.\n" + e);
			throw e;
		}
	}
	
	@Override
	public void save() throws Exception {
		// Get versionid, and throw an exception if it is null
		String versionId = this.versioning.getVersionid();
		if (versionId == null || versionId.equals("")) {
			throw new Exception("Trying to save without versionID");
		}
		
		// Delete all items with the versionid.
		deleteItemsWithCurrentVersionId();
		
		// Save the parameters
		try {
			GenericUniverseTableModel<Universeparameters> tableModel = getTableModel();
			for (Universeparameters universeParameters : tableModel.data) {
				if (!versionId.equals(universeParameters.getVersionid()))
					throw new Exception("Trying to save for wrong versionId");
				
				universeParameters.insertDB();
			}
		} catch (Exception e) {
			logger.warning("Unable to save Universeparameters.\n" + e);
			throw e;
		}
		
	}

	@Override
	public Vector<String> validateData() {
		return null;
	}

}
