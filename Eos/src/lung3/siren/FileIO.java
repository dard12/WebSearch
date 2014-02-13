package lung3.siren;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

/**
 * General static FileIO operations useful as helpers.
 * @author Li-Hsing Lung
 *
 */
public class FileIO {
	
	/**
	 * Wrapper for the write function that adds a newline
	 * @param line to be written
	 * @param writer for the document being written to
	 * @throws IOException
	 */
	public static void writeln(String line, BufferedWriter writer) throws IOException {
		writer.write(line);
		writer.newLine();
	}
	
	/**
	 * Accumulate all lines of text until a blank line is encountered.
	 * @param line to start reading
	 * @param reader for the file being read
	 * @return all lines until the blank line concatenated together
	 * @throws IOException
	 */
	public static String getAllLines(String line, BufferedReader reader) throws IOException {
		String allNtriples = line;
		
		while (!line.equals("")) {
			allNtriples += line;
			line = reader.readLine();
		}
		
		return allNtriples;
	}
	
	/**
	 * Check if the given directory is a valid directory to operate upon.
	 * @param dir to be checked
	 * @return true if operable, false otherwise
	 */
	public static boolean isValidDirectory(File dir) {
		boolean isValid = (dir.isDirectory() && dir.exists());
		
		if (!isValid)
			System.out.println("Error! Invalid directory: " + dir.getName());
		
		return isValid;
	}
	
	/**
	 * Check if the given file is a valid file to operate upon
	 * @param file to be checked
	 * @return true if operable, false otherwise
	 */
	public static boolean isValidFile(File file) {
		boolean isValid = (!file.isHidden() && !file.isDirectory());
		
		if (!isValid)
			System.out.println("Error! Invalid file: " + file.getName());
		
		return isValid;
	}
	
	/**
	 * Find the next available file name numbering from 0 to 300. A file is unavailable after reaching the maxSize
	 * @param dir to check for availability
	 * @return the file name if found, or ERROR
	 */
	public static String nextAvailName(String dir, long maxSize) {
		for (int i = 0; i < 300; i++) {
			String currName = String.valueOf(i);
			File newOut = new File(dir + currName);
			
			if (!newOut.exists() || newOut.length() < maxSize)
				return currName;
		}
		
		return "ERROR";
	}
	
	/**
	 * Delete the file given if it is empty
	 * @param file to check and delete if empty
	 */
	public static void deleteIfEmpty(File file) {
		if (file.length() == 0) {
			System.out.println("File " + file + " is empty. Deleting the file...");
			file.delete();
		}
	}
	
	/**
	 * Delete the original file and rename the replacement as the original file.
	 * @param original file to replace
	 * @param replacement file to replace with
	 */
	public static void replaceFile(File original, File replacement) {
		original.delete();
		replacement.renameTo(original);
	}
}
