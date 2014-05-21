/**
 * A csv file format reader that defaults to the resource folder of a Spring Project.
 * 
 * The default path assumes that there is a csv/ directory located in the Spring Resource path.
 * 
 * @author Taylor Cressy
 * @version 1.0
 * @date 15 April, 2014
 */

package server_utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.springframework.core.io.ClassPathResource;

public class CSVReader {
	
	private static final String DEFAULT_CSV_PATH = "csv/";
	
	private ClassPathResource file;
	private String filename;
	private String path;
	
	private ArrayList<String[]> csvArray;
	
	/**
	 * Read a filename and append it to the default path
	 */
	public CSVReader(String filename) throws IOException{
		if(filename == null)
			throw new NullPointerException();
		
		if(filename.substring(filename.length() - 5).compareToIgnoreCase(".csv") != 0) {
			this.filename = filename + ".csv";
		}
		else {
			this.filename = filename;
		}
		
		this.path = DEFAULT_CSV_PATH;
		this.file = new ClassPathResource(DEFAULT_CSV_PATH + this.filename);
		
		if(this.file == null)
			throw new IllegalArgumentException();
		
		this.csvArray = new ArrayList<String[]>();
		
		this.readCSV();
	}
	
	public CSVReader(String path, String filename) throws IOException{
		if(filename == null || path == null) 
			throw new NullPointerException();
		
		if(filename.substring(filename.length() - 5).compareToIgnoreCase(".csv") != 0) {
			this.filename = filename + ".csv";
		}
		else {
			this.filename = filename;
		}
		
		this.path = path;
		this.file = new ClassPathResource(path + this.filename);
		
		if(this.file == null)
			throw new IllegalArgumentException();
		
		this.csvArray = new ArrayList<String[]>();
		
		this.readCSV();
	}
	
	
	/*
	 * Simple helper for reading the CSV file into the local variables
	 */
	private void readCSV() throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(this.file.getFile()));
		
		String nextLine, nextArr[];
		while((nextLine = reader.readLine()) != null) {
			nextArr = nextLine.split(",");
			
			for(int i=0; i < nextArr.length; i++) {
				nextArr[i] = nextArr[i].trim();
			}
			this.csvArray.add(nextArr);
			
		}

		reader.close();
	}
	
	
	/*Getters*/
	public String getPath() {
		return this.path;
	}
	
	public String getFileName() {
		return this.filename;
	}
	
	public ArrayList<String[]> getCSVArray() {
		return this.csvArray;
	}
}
