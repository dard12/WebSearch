package lung3.siren;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/** 
 * Handle the batch processing of RDF files of the current format into NT files accepted by SIREn.
 * @author Li-Hsing Lung
 *
 */
public class ConvertRDF {
	
	/**
	 * Format the RDF files in the given directory and output the formatted files in the output directory.
	 * The formated files are now readable by CWM.
	 * @param source directory to get RDF files from
	 * @param output directory to place the formatted RDF files
	 * @throws IOException 
	 */
	public static void formatRDF(String source, String output) throws IOException {
		File dir = new File(source);

		if (!FileIO.isValidDirectory(dir))
			return;

		for (File currRDF : dir.listFiles()) {
			if (!FileIO.isValidFile(currRDF))
				continue;
			
			String currFileName = currRDF.getName();
			System.out.println("Formating file: " + currFileName);
			
			BufferedReader reader = new BufferedReader(new FileReader(currRDF));
			File outputFile = new File(output + currFileName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			
			// Write the header
			String line = reader.readLine();
			String docAbout = findAbout(line);
			line = formatHeader(line, docAbout);
			FileIO.writeln(line, writer);
			
			// Write the rest of the document
			while ((line = reader.readLine()) != null) {
				line = formatDcEntries(line, docAbout);
				FileIO.writeln(line, writer);
			}
			
			reader.close();
			writer.close();
		}
	}

	/**
	 * Batch convert the RDF files given into NT using a script that runs CWM.
	 * @param source directory of formated RDF files
	 * @param output NT files converted by CWM
	 * @param scriptPath that calls CWM with bash
	 * @param cwmPath path to CWM
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void convertRDF(String source, String output, String scriptPath, String cwmPath) throws IOException, InterruptedException {
		File dir = new File(source);

		if (!FileIO.isValidDirectory(dir)) 
			return;

		for (File currRDF : dir.listFiles()) {
			if (!FileIO.isValidFile(currRDF))
				continue;
			
			String currFileName = currRDF.getName();
			System.out.println("Converting file: " + currFileName);
			
			String currSource = source + currFileName;
			String newFileName = currFileName.replace(".rdf", ".nt");
			String currOutput = output + newFileName;
			
	        Process p = new ProcessBuilder("/bin/bash", scriptPath, cwmPath, currSource, currOutput).start();
	        p.waitFor();
		}
	}
	
	/**
	 * Batch format all NT files to be more read-friendly for SIREn.
	 * @param source directory of NT files
	 * @param output directory of formatted NT files
	 * @throws IOException
	 */
	public static void formatNT(String source, String output) throws IOException {
		File dir = new File(source);

		if (!FileIO.isValidDirectory(dir))
			return;

		for (File currNT : dir.listFiles()) {
			if (!FileIO.isValidFile(currNT))
				continue;
			
			String currFileName = currNT.getName();
			System.out.println("Formating file: " + currFileName);
			
			BufferedReader reader = new BufferedReader(new FileReader(currNT));
			File outputFile = new File(output + currFileName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			String line;
			
			while ((line = reader.readLine()) != null) {
				String trimmedLine = line.trim();
				
				if (trimmedLine.indexOf('#') != 0 && trimmedLine.length() != 0) {
					line = line.replace("  ", "");
					FileIO.writeln(line, writer);
				}
			}
			
			reader.close();
			writer.close();
		}
	}

	/**
	 * Change the header given to the appropriate format.
	 * @param line of the input header
	 * @param docAbout extracted from the header
	 * @return
	 * @throws IOException
	 */
	private static String formatHeader(String line, String docAbout) throws IOException {
		line = line.replace(docAbout, ">");
		line = line.replace("<rdf:Description", "<rdf:RDF");
		
		return line;
	}
	
	/**
	 * Format each entry of dc to be surrounded by a rdf:Description tag
	 * @param line to be processed
	 * @param about document identifier to be specified in the description tag
	 * @return formatted line
	 */
	private static String formatDcEntries(String line, String about) {
		line = removeType(line);
		
		if (line.contains("<dc:"))
			line = "<rdf:Description " + about + " " + line + " </rdf:Description>";
		else
			line = line.replace("</rdf:Description>", "</rdf:RDF>");
		
		return line;
	}
	
	private static String removeType(String line) {
		int breakPos = line.indexOf(" ");
		int endTag = line.indexOf(">");
		
		if (Text.isValidSubstring(breakPos, endTag)) {
			String type = line.substring(breakPos, endTag);
			line = line.replace(type, "");
		}
		
		return line;
	}
	
	/**
	 * Find the about tag if it is contained within the inputed line
	 * @param line to examine
	 * @return the output tag substring if it exists, empty string otherwise
	 */
	private static String findAbout(String line) {
		int aboutPos = line.indexOf("rdf:about");
		
		if (aboutPos != -1)
			line = line.substring(aboutPos);
		else {
			System.out.println("Couldn't find the about tag!");
			line = "";
		}
		
		return line;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		formatRDF("cwm-1.2.1/data/unformattedRDF/", "cwm-1.2.1/data/formattedRDF/");
		
		System.out.println("Done formatting RDF!");
		
		convertRDF("cwm-1.2.1/data/formattedRDF/", "cwm-1.2.1/data/unformattedNT/", "cwm-1.2.1/convert.sh", "cwm-1.2.1/cwm");
		
		System.out.println("Done Converting RDF!");
		
		formatNT("cwm-1.2.1/data/unformattedNT/", "cwm-1.2.1/data/formattedNT/");
		
		System.out.println("Done formatting NT!");
	}
	
}
