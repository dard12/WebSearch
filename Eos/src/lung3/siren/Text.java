package lung3.siren;

import java.util.List;

/**
 * General static text operations useful as helpers.
 * @author Li-Hsing Lung
 *
 */
public class Text {
	
	/**
	 * Find out if the given positions are valid indices for performing a string substring operation.
	 * @param startPos starting index of substring
	 * @param endPos ending index of substring
	 * @return true if both positions are valid and starting index is smaller than ending index
	 */
	public static boolean isValidSubstring(int startPos, int endPos) {
		return (startPos != -1 && endPos != -1 && startPos < endPos);
	}
	
	/**
	 * Delete the given line if any of the invalid target string is found within the line.
	 * @param line to inspect
	 * @param invalid list of targets to be found in the line
	 * @return empty string if target is found, the original line string otherwise
	 */
	public static String deleteLine(String line, List<String> invalids) {
		for (String target : invalids) {
			if (line.contains(target))
				line = "";
		}
		
		return line;
	}
	
	/**
	 * Remove the first occurrence of the substring and return the modified string.
	 * @param line to modify
	 * @param target to remove from line
	 * @return the modified line
	 */
	public static String removeSubstring(String line, String target) {
		int position = line.indexOf(target);
		int targetLength = target.length();
		
		if (position != -1) {
			line = line.substring(position + targetLength);
		}
		
		return line;
	}
	
	/**
	 * Surround the given string with parentheses and return it.
	 * @param line string to format
	 * @return the string enclosed in parentheses
	 */
	public static String encloseInParentheses(String line) {
		return "(" + line + ")";
	}
	
	/**
	 * Surround the given string with double quotes and return it.
	 * @param line string to format
	 * @return the string enclosed in double quotes
	 */
	public static String encloseInDoubleQuotes(String line) {
		return "\"" + line + "\"";
	}
	
	/**
	 * Combine all strings in a given list into a single string with conjunctions between each element of the list
	 * @param list of strings to combine
	 * @return all queries strung together with conjunction, empty string if list is empty
	 */
	public static String combineList(List<String> queryInput, String conjunction) {
		int size = queryInput.size();

		if (size == 0)
			return "";

		String combinedQuery = queryInput.get(0);

		for (int i = 1; i < size; i++)
			combinedQuery += " " + conjunction + " " + queryInput.get(i);

		return combinedQuery;
	}
}
