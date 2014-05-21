package server_utilities_tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import server_utilities.CSVReader;


public class CSVReaderTests {

	private CSVReader reader;
	
	private void setup() {
		
		try {
			this.reader = new CSVReader("servererrors");
		}
		catch(IOException ie) {
			fail(ie.getLocalizedMessage());
		}
	}
	
	@Test
	public void testCSVReader() {
		setup();
		
		ArrayList<String[]> arr = this.reader.getCSVArray();
		assertNotNull(arr);
		
		for(String[] next: arr) {
			for(int i=0; i < next.length; i++)
			{
				System.out.print(next[i] + " ");
			}
			System.out.println();
		}
	}

}
