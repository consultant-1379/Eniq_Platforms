package com.ericsson.sim.model.parser;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {

	protected BufferedReader bufferedReader;
	private List<String> contentList;

	/**
	 * Takes a filePath of a .csv file and creates a filereader wrapped in a
	 * buffered reader
	 * 
	 * 
	 * @param csvFile
	 */

	public void loadFile(String csvFile) {

		// File file = new File(csvFile);

		try {
			// if (file.isFile() && !file.isDirectory()){
			bufferedReader = new BufferedReader(new FileReader(csvFile));
			// }

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// }finally{
		// try {
		// bufferedReader.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

	}

	public String[] getNextLine() {

		String line;

		try {
			if ((line = bufferedReader.readLine()) != null) {
				// return line.split("\\s*\\|\\s*");
				return line.split("\\s*,\\s*|\\s*\\|\\s*");
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		return null;

	}

	public int getColumnLength() {
		if (bufferedReader != null) {
			return this.getDocumentHeader().length;
		} else {
			return 0;
		}

	}

	/**
	 * Takes parameter filePath, creates file reader wrapped in a buffered
	 * reader, reads line by line, splits with "," and adds each string to
	 * overall data structure
	 * 
	 * @param csvFile
	 * @return list of all string content from parameter
	 */

	public List<String> getContent(String csvFile) {
		contentList = new ArrayList<String>();

		String line = "";

		try {
			// if (file.isFile() && !file.isDirectory()){

			bufferedReader = new BufferedReader(new FileReader(csvFile));

			while ((line = bufferedReader.readLine()) != null) {
				String[] lineArray = line.split("\\s*,\\s*|\\s*\\|\\s*");

				contentList = addToArrayList(lineArray);
			}
			// }

		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
			}
		}

		return contentList;
	}

	/**
	 * Accepts a string array of a line and adds to overall data structure of
	 * the content of file
	 * 
	 * @param lineArray
	 * @return ArrayList of all Strings in file
	 */

	public List<String> addToArrayList(String[] lineArray) {

		for (int i = 0; i < lineArray.length; i++) {
			if (!(lineArray[i] == null) && !(lineArray[i].length() == 0)) {
				contentList.add(lineArray[i].trim());

				System.out.println(lineArray[i]);
			}
		}
		return contentList;
	}

	/**
	 * Uses the file stored in the classes buffered reader Checks if first line
	 * is not null and adds each string in the line data structure and returns
	 * it
	 * 
	 * @return List of all strings in the first line or an empty list if no
	 *         first line exists.
	 */

	public String[] getDocumentHeader() {
		String line;
		String[] headerValues = null;

		try {
			if ((line = bufferedReader.readLine()) != null) {
				headerValues = line.split("\\s*,\\s*|\\s*\\|\\s*");

			}
		} catch (IOException e) {
			System.out.println(e.getMessage());

		}

		return headerValues;
	}

	public void noHeader() throws IOException {
		String line;

		while ((line = bufferedReader.readLine()) != null) {
			System.out.println("ArrayList data: " + getContent(line) + "\n");
		}
	}

	public void closeFileParser() {
		try {
			bufferedReader.close();
		} catch (IOException e) {
			System.out
					.println("Exception in closing buffered reader in CSVParser"
							+ e.getMessage());
		}
	}

}
