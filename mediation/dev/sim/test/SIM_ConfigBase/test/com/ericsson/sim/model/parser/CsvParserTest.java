package com.ericsson.sim.model.parser;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CsvParserTest {

	private CsvParser parser;
	private String[] array = {"header,","comma,","seperated,","variable,","list,","1,","2,","3"};
	private final String FILE_PATH = Paths.get(".").toAbsolutePath().normalize().toString();
	
	@Before
	public void setUp() throws IOException {
		parser = new CsvParser(); 
		BufferedWriter bw = null;
		try{
			File csvFile = new File(FILE_PATH + "/CSV.txt");
			bw = new BufferedWriter(new FileWriter(csvFile));
			int i;
			for(i = 0; i < array.length; i++){
				bw.write(array[i]);
				
				if(array[i].equals("1,") || array[i].equals("variable,")){
					bw.newLine();
					System.out.println();
				}
			}	
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}finally{
			try {
				bw.flush();
				bw.close();
				bw = null;
				System.gc();
				// without this, throws FileSystemException
				// Cannot be accessed as it's being used by another process
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} 
		}
	
		parser.loadFile(FILE_PATH + "/CSV.txt");
	}
	
	@After
	public void tearDown() throws Exception { 
		parser.bufferedReader.close();
		Files.delete(Paths.get((FILE_PATH + "/CSV.txt")));

	}
	
	
	
	@Test
	public void testGetContent() {
		
		
		assertEquals(0, parser.getContent(FILE_PATH + FILE_PATH + "/noFileHere.txt").size());
		assertEquals(8, parser.getContent(FILE_PATH + "/CSV.txt").size());
		assertEquals("header", parser.getContent(FILE_PATH + "/CSV.txt").get(0));
		assertEquals("3", parser.getContent(FILE_PATH + "/CSV.txt").get(7)); 
	}
	
	@Test
	public void testGetDocumentHeader() {
		String[] result = parser.getDocumentHeader();
		
		assertEquals("header", result[0]);
		assertEquals(4, result.length);
		assertEquals("variable", result[3]);
	}
	
}
