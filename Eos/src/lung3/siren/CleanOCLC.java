package lung3.siren;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** 
 * Clean the OCLC dataset by removing useless or unreadable information for SIREn.
 * @author Li-Hsing Lung
 *
 */
public class CleanOCLC {
	
	public static HashMap<String, String> table = new HashMap<String, String>();
	
	/**
	 * Clean all files in the source directory by removing all unreadable or unnecessary entries from the files.
	 * @param source directory to look in
	 * @param output directory to put cleaned files
	 * @throws IOException
	 */
	private static void deleteAllAnomaly(String source, String output) throws IOException {
		File dir = new File(source);

		if (!FileIO.isValidDirectory(dir))
			return;

		for (File currNT : dir.listFiles()) {
			if (!FileIO.isValidFile(currNT))
				continue;
			
			String fileName = currNT.getName();
			System.out.println("Deleting anomalies from file: " + fileName);
			BufferedReader reader = new BufferedReader(new FileReader(currNT));
			File outputFile = new File(output + fileName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			String line = "";

			while ((line = reader.readLine()) != null) {
				List<String> invalids = createInvalids();
				line = Text.deleteLine(line, invalids);
				
				if (!line.equals(""))
					FileIO.writeln(line, writer);
			}

			reader.close();
			writer.close();
		}
	}
	
	/**
	 * Create a list of invalid strings.
	 * @return the list of strings
	 */
	private static List<String> createInvalids() {
		List<String> invalids = new ArrayList<String>();
		
		invalids.add("<http://schema.org/Intangible>");
		invalids.add("_:A");
		invalids.add("<http://purl.oclc.org/dataset/WorldCat>");
		invalids.add("<http://purl.oclc.org/dataset/WorldCatMostHighlyHeld>");
		invalids.add("<http://www.oclc.org/worldcat/recorduse/policy/odcbynorms.htm>");
		
		return invalids;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		deleteAllAnomaly("cwm-1.2.1/data/splitOCLC/", "cwm-1.2.1/data/cleanOCLC/");
		
		System.out.println("Done deleting anamolies!");
	}
	
}
