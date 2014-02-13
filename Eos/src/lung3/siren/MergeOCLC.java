package lung3.siren;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** 
 * Merge split OCLC entries into a collection of files that are sorted by their subject field.
 * @author Li-Hsing Lung
 *
 */
public class MergeOCLC {
	
	public static HashMap<String, List<String>> table = new HashMap<String, List<String>>();
	private static final int MAX_TABLE_SIZE = 1000; // Higher is faster but consumes more memory.

	/**
	 * Select a number of entries(limited by MAX_TABLE_SIZE) from the source directory and record it in table.
	 * Record into the table all other entries with the same subjects as the initially selected entries.
	 * All entries that are recorded into the table are deleted from their original files, and empty files
	 * are deleted from the source directory.
	 * 
	 * @param source directory of files that need to be merged
	 * @param temp directory of files that are being processed
	 * @throws IOException
	 */
	public static void mergeBySubject(String source, String temp) throws IOException {
		File dir = new File(source);
		
		if (!FileIO.isValidDirectory(dir))
			return;

		for (File currNT : dir.listFiles()) {
			if (!FileIO.isValidFile(currNT))
				continue;
			
			String currFileName = currNT.getName();
			System.out.println("Opening file for merge: " + currFileName);
			BufferedReader reader = new BufferedReader(new FileReader(currNT));
			File tempFile = new File(temp + currFileName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			String line;

			while ((line = reader.readLine()) != null) {
				String subject = findSubject(line);
				
				if (!subject.equals("")) {
					List<String> ntriple = getNtriple(subject);
					
					if (!writeNtriple(subject, line, ntriple))
						FileIO.writeln(line, writer);
				}
			}
			
			FileIO.replaceFile(currNT, tempFile);
			FileIO.deleteIfEmpty(currNT);
			
			reader.close();
			writer.close();
		}
	}
	
	/**
	 * Add a line to an ntriple if the ntriple exists, and then add the ntriple into the table.
	 * 
	 * @param subject of the ntriple being written
	 * @param line to add into the ntriple
	 * @param ntriple to write
	 * @return true if writing successful, false otherwise
	 */
	private static boolean writeNtriple(String subject, String line, List<String> ntriple) {
		boolean written = false;
		
		if (ntriple != null) {
			ntriple.add(line);
			table.put(subject, ntriple);
			written = true;
		}
		
		return written;
	}
	
	/**
	 * Create the ntriple only if it will not increase table size, or table size is smaller than max limit
	 * @param subject that the ntriple is mapped to
	 * @return a list for the given subject or null if conditions are not met.
	 */
	private static List<String> getNtriple(String subject) {
		List<String> ntriple = null;
		
		if (table.containsKey(subject))
			ntriple = table.get(subject);
		else if (table.size() < MAX_TABLE_SIZE)
			ntriple = new ArrayList<String>();
		
		return ntriple;
	}
	
	/**
	 * Find the subject if it is contained within the inputed line. Used for examining subjects
	 * @param line to examine
	 * @return the subject substring if it exists, empty string otherwise
	 */
	private static String findSubject(String line) {
		int startPos = line.indexOf("<") + 1;
		int endPos = line.indexOf(">");
		
		if (startPos > -1 && endPos > -1 && startPos < endPos)
			line = line.substring(startPos, endPos);
		else 
			line = "";
		
		return line;
	}
	
	/**
	 * Output all entries grouped by subject with an extra return between each subject. Output is cumulative/appended to the output file.
	 * @param output file to write to
	 * @throws IOException 
	 */
	public static void outputMerged(String output) throws IOException {
		System.out.println("Table size: " + table.size());
		System.out.println("Writing contents of the table to file: " + output);
		
		Set<String> relationSet = table.keySet();
		Iterator<String> relationIter = relationSet.iterator();
		
		File outputFile = new File(output);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true));
		
		while (relationIter.hasNext()) {
			String currSubject = relationIter.next();
			List<String> ntriple = table.get(currSubject);
			
			for(String line : ntriple)
				FileIO.writeln(line, writer);

			FileIO.writeln("", writer);
		}
		
		writer.close();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		do {
			System.out.println("Merge starting in 3 seconds...");
			Thread.sleep(3000);
			
			table.clear();
			String outDir = "cwm-1.2.1/data/cleanOCLC/merged/";
			mergeBySubject("cwm-1.2.1/data/cleanOCLC/", "cwm-1.2.1/data/cleanOCLC/temp/");
			
			String outFile = FileIO.nextAvailName(outDir, 50000000);
			outputMerged(outDir + outFile);
		}
		while (table.size() == MAX_TABLE_SIZE);
		
		System.out.println("Merging has finished!");
	}
	
}
