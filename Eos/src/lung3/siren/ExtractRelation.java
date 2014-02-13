package lung3.siren;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/** 
 * Extract fields within NT files for outputting relations in use of query conversion and for analyzing anomalies to clean from OCLC dataset.
 * @author Li-Hsing Lung
 *
 */
public class ExtractRelation {
	
	public static HashMap<String, String> table = new HashMap<String, String>();

	/**
	 * Create a table of every relation that the NT files reference.
	 * @param source directory of NT files to extract from
	 * @throws IOException
	 */
	public static void storeRelations(String source) throws IOException {
		File dir = new File(source);
		if (!FileIO.isValidDirectory(dir))
			return;

		for (File currNT : dir.listFiles()) {
			if (!FileIO.isValidFile(currNT))
				continue;
			
			String currFileName = currNT.getName();
			System.out.println("Extracting from file: " + currFileName);
			BufferedReader reader = new BufferedReader(new FileReader(currNT));
			String line;

			while ((line = reader.readLine()) != null) {
				String relation = findRelation(line);
				
				if (!relation.equals("") && !table.containsKey(relation)) {
					table.put(relation, line);
					System.out.println("Extracted relation: " + relation);
				}
			}
			
			reader.close();
		}
	}
	
	/**
	 * Output all fields that are stored in the table.
	 * @param output file to write all fields into
	 * @throws IOException 
	 */
	public static void outputRelations(String output) throws IOException {
		System.out.println("Table size: " + table.size());
		
		Set<String> relationSet = table.keySet();
		Iterator<String> relationIter = relationSet.iterator();
		
		File outputFile = new File(output);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		
		while (relationIter.hasNext()) {
			String currType = relationIter.next();

			FileIO.writeln(currType, writer);
			System.out.println(currType);
		}
		
		writer.close();
	}	
	
	/**
	 * Find the object if it is contained within the inputed line and matches a target. Used for examining objects
	 * @param line to examine
	 * @return the object substring if it exists and matches the target, empty string otherwise
	 */
	private static String findObject(String line, String target) {
		int startPos = line.lastIndexOf("<");
		int endPos = line.lastIndexOf(">");
		
		if (Text.isValidSubstring(startPos, endPos)) {
			line = line.substring(startPos, endPos);
			
			if (!line.contains(target))
				line = "";
		}
		else {
			System.out.println("Couldn't find the object!");
			line = "";
		}
		
		return line;
	}
	
	/**
	 * Find the object if it is a URI and contained within the input line. Used for examining object URIs
	 * @param line to examine
	 * @return the object URI in its general form if it exists, empty string otherwise.
	 */
	private static String findObjectURI(String line) {
		if (line.equals("") || line.contains("schema.org/url") || line.contains("schema.org/image") || line.contains("schema.org/datePublished")
				|| line.contains("schema.org/copyrightYear"))
			return "";
		
		int secondURIPos = line.indexOf(" ") + 1;
		int startPos = line.indexOf(" ", secondURIPos) + 1;
		int startCheck = line.lastIndexOf("<");
		int endPos = line.lastIndexOf(" ");
		
		if (Text.isValidSubstring(startPos, endPos) && startPos == startCheck) {
			line = line.substring(startPos, endPos);
			endPos = line.lastIndexOf("/");
			if (Text.isValidSubstring(0, endPos))
				line = line.substring(0, endPos);
			else
				line = "";
			
			if (line.contains("dewey") || line.contains("urn:isbn"))
				line = "";
		}
		else {
			line = "";
		}
		
		return line;
	}
	
	/**
	 * Find the subject if it is contained within the inputed line. Used for examining subjects
	 * @param line to examine
	 * @return the subject substring if it exists, empty string otherwise
	 */
	private static String findSubject(String line) {
		int startPos = line.indexOf("<") + 1;
		int endPos = line.indexOf(">");
		
		if (Text.isValidSubstring(startPos, endPos)) {
			line = line.substring(startPos, endPos);
			endPos = line.lastIndexOf("/");
			line = line.substring(0, endPos);
		}
		else {
			System.out.println("Couldn't find the subject!");
			line = "";
		}
		
		return line;
	}
	
	/**
	 * Find the relation if it is contained within the inputed line. Used to extract relations
	 * @param line to examine
	 * @return the field substring if it exists, empty string otherwise
	 */
	private static String findRelation(String line) {
		if (line.equals(""))
			return line;
		
		int startPos = line.indexOf("<", 1) + 1;
		int endPos = line.indexOf(">", startPos);
		
		if (Text.isValidSubstring(startPos, endPos))
			line = line.substring(startPos, endPos);
		else {
			System.out.println("Couldn't find the relation! ");
			line = "";
		}
		
		return line;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		storeRelations("cwm-1.2.1/data/cleanOCLC/merged");
		
		outputRelations("cwm-1.2.1/data/relations/extractedRelations");
		
		System.out.println("Done extracting!");
	}
	
}
