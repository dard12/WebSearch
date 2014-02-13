package lung3.siren;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Responsible for converting natural language queries into queries that are accepted by SIREn.
 * @author Li-Hsing Lung
 *
 */
public class ConvertQuery {
	private static Set<String> queryPredicates;
	private static HashMap<String, String> predicateTable = new HashMap<String, String>();
	private static final String DEFAULT_OBJECT = "";

	/**
	 * Create a table of all predicates where the key is the natural language predicate and value is the queryPredicate.
	 * Initialize the set of all possible natural language predicates.
	 * @param source directory of NT files to extract from
	 * @throws IOException
	 */
	public static void makeConversionTable(String source) throws IOException {
		File relationDefine = new File(source);
		BufferedReader reader = new BufferedReader(new FileReader(relationDefine));
		String line;

		while ((line = reader.readLine()) != null) 
			insertEntry(line);

		reader.close();
		queryPredicates = predicateTable.keySet();
	}

	/**
	 * Add an entry to the predicateTable based on the input line.
	 * @param line that contains the query predicate and natural language predicate
	 */
	private static void insertEntry(String line) {
		int breakPos = line.lastIndexOf(":");

		if (breakPos != -1) {
			String queryPredicate = line.substring(0, breakPos);
			String naturalPredicate = line.substring(breakPos + 2);
			predicateTable.put(naturalPredicate, queryPredicate);
		}
	}

	/**
	 * Find the object given a line of input and the predicate for that line.
	 * @param line containing both predicate and object
	 * @param predicate of the line
	 * @return object if it can be found, DEFAULT_OBJECT otherwise
	 */
	public static String getObject(String line, String predicate) {
		int limit = line.length();
		int cmdEnd = predicate.length();
		String object = DEFAULT_OBJECT;

		if (cmdEnd < limit)
			object = line.substring(cmdEnd + 1);

		return object;
	}
	
	/**
	 * Find a natural language predicate from queryPredicates at the very front of the given line.
	 * @param line to examine for possible predicates
	 * @return the predicate found or empty string if nothing is found
	 */
	public static String getNaturalPredicate(String line) {
		Iterator<String> cmdIter = queryPredicates.iterator();
		int cmdIndex = -1;

		while (cmdIter.hasNext() && cmdIndex != 0) {
			String predicate = cmdIter.next();
			cmdIndex = line.indexOf(predicate);

			if (cmdIndex == 0) {
				return predicate;
			}
		}
		
		return "";
	}
	
	/**
	 * Get the query predicate equivalent of a natural predicate.
	 * @param naturalPredicate the natural language predicate to match with a query predicate
	 * @return the query predicate, or empty string if not found
	 */
	public static String getQueryPredicate(String naturalPredicate) {
		String queryPredicate = "";
		
		if (predicateTable.containsKey(naturalPredicate))
			queryPredicate = predicateTable.get(naturalPredicate);
		
		return queryPredicate;
	}
	
	/**
	 * Construct a term query given a predicate and object.
	 * @param predicate of the query
	 * @param object of the query
	 * @return query of the form * <predicate> 'object'
	 */
	public static String constructTermQuery(String predicate, String object) {
		//TODO: input handling, make all ' into \' and , into \, etc
		String convertedQuery = "* <" + predicate + "> '" + object + "'";
		
		return convertedQuery;
	}
	
	/**
	 * Construct a phrase query given a predicate and object.
	 * @param predicate of the query
	 * @param object of the query
	 * @return query of the form * <predicate> "object"
	 */
	public static String constructPhraseQuery(String predicate, String object) {
		String convertedQuery = "* <" + predicate + "> \"" + object + "\"";
		
		return convertedQuery;
	}
	
	/**
	 * Construct a large number of queries combined together with the OR conjunction
	 * @param predicate of all the queries
	 * @param objects list of all objects for the queries
	 * @return all queries in form * <predicate> <object1> OR * <predicate> <object2> OR ...
	 */
	public static String constructSirenMultiQuery(String predicate, List<String> objects) {
		List<String> convertedQueryList = new ArrayList<String>();
		
		for (String object : objects) {
			//TODO: remove this, this is just a hack to remove a set of URI that never appear as objects in the corpus
			if (object.contains("www.worldcat.org/oclc"))
				continue;
			
			object = Text.encloseInDoubleQuotes(object);
			convertedQueryList.add(object);
		}
		
		String convertedQuery = Text.combineList(convertedQueryList, "OR");
		convertedQuery = constructTermQuery(predicate, convertedQuery);
		convertedQuery = Text.encloseInParentheses(convertedQuery);
		
		return convertedQuery;
	}
	
	/**
	 * Convert a single phrase of natural language input into a SIREn query.
	 * @param line phrase in natural language
	 * @return the SIREn query
	 */
	public static String naturalToSiren(String line) {
		String convertedQuery = line;
		String naturalCmd = getNaturalPredicate(line);
		
		if (!naturalCmd.equals("")) {
			String object = getObject(line, naturalCmd);
			String predicate = getQueryPredicate(naturalCmd);
			
			convertedQuery = constructTermQuery(predicate, object);
			convertedQuery = Text.encloseInParentheses(convertedQuery);
		}
		
		//convertedQuery = "url: \67234565\"";
		
		return convertedQuery;
	}
}
