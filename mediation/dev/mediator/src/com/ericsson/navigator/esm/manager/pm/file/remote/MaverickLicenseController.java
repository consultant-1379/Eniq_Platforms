package com.ericsson.navigator.esm.manager.pm.file.remote;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.ericsson.navigator.esm.util.component.Component;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;
import com.maverick.ssh.LicenseManager;

public class MaverickLicenseController implements Component {
	
	private final String licenseFilePath;

	public MaverickLicenseController(final String path){
		licenseFilePath = path;
	}
	
	@Override
	public String getComponentName() {
		return "Maverick License Controller";
	}

	@Override
	public void initialize() throws ComponentInitializationException {
		try {
			LicenseManager.addLicense(getFileContent(licenseFilePath));
		} catch (FileNotFoundException e) {
			throw new ComponentInitializationException(
					"Failed to find Maverick license file: " + licenseFilePath + 
					", no SSH based access (SCP/SFTP) in ESM will work " +
					"until a valid license is available.", null, false);
		} catch (IOException e) {
			throw new ComponentInitializationException(
					"Failed to read Maverick license file: " + licenseFilePath + 
					", no SSH based access (SCP/SFTP) in ESM will work " +
					"until problem with the license file is fixed.", e, false);
		}
	}
	
	protected String getFileContent(final String filePath) throws IOException {
        final StringBuffer result = new StringBuffer();
        final FileReader fileReader = new FileReader(filePath);
        final BufferedReader bufferedReader = new BufferedReader(fileReader);
        String tmpStr = null;
        do {
            tmpStr = bufferedReader.readLine();
            if (tmpStr != null) {
                result.append(tmpStr).append(System.getProperty("line.separator"));
            }
        } while (tmpStr != null);

        return result.toString();
    }

	@Override
	public void shutdown() throws ComponentShutdownException {}
}
