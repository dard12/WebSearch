package lung3.siren;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.sindice.siren.solr.SirenParams;
import org.xml.sax.SAXException;

/**
 * Responsible for querying the SolrSIREn server
 * @author Li-Hsing Lung
 *
 */
public class QueryNT {
	private final String INDEX_URL = "http://localhost:8080/siren";
	private final SolrServer server;
	private static final String DEFINED_RELATIONS = "cwm-1.2.1/data/relations/definedRelations";
	private final int MAX_RESULTS = 50;
	private final String DEFAULT_CONJUNCTION = "AND";
	private final String NO_RESULTS = "No results were found. :(";
	private List<String> keywordQueryList;
	private List<String> ntripleQueryList;
	private final static String MULTI_QUERY_DELIMITER = " <> ";

	/**
	 * Initialize the connection to Solr Server and persistent tracking of queries
	 * @param solrServerUrl url of the server
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public QueryNT(final String solrServerUrl) throws SolrServerException, IOException {
		server = new HttpSolrServer(solrServerUrl == null ? INDEX_URL : solrServerUrl);
		newQueries();
	}
	
	/**
	 * Refresh and initialize query lists.
	 */
	private void newQueries() {
		keywordQueryList = new ArrayList<String>();
		ntripleQueryList = new ArrayList<String>();
	}

	/**
	 * Console user interface that gets input, processes it into a query and gets response from the server.
	 * @throws IOException
	 * @throws SolrServerException
	 */
	private void query() throws IOException, SolrServerException {
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		String input = getInput(bufferRead);

		while (!input.equals("exit")) {
			if (input.equals("clear")) {
				newQueries();
				input = "";
				continue;
			}

			SolrDocumentList resultList = executeNaturalQuery(input, keywordQueryList, ntripleQueryList);
			printResponse(resultList);
			input = getInput(bufferRead);
		}
	}
	
	/**
	 * Take in an input and lists of queries to store the input, execute the resulting query.
	 * @param naturalInput to parse for queries
	 * @return list of results from executing the input
	 * @throws SolrServerException
	 */
	private SolrDocumentList executeNaturalQuery(String naturalInput, List<String> keywordQueries, List<String> ntripleQueries) throws SolrServerException {
		SolrQuery query = createSIREnQuery();
		
		parseNaturalInputToQuery(naturalInput, keywordQueries, ntripleQueries);
		setQuery(query, keywordQueries, ntripleQueries);
		SolrDocumentList resultList = getResponse(query);
		
		return resultList;
	}
	
	/**
	 * Create and return a SIREn query
	 * @return the constructed SIREn query
	 */
	private SolrQuery createSIREnQuery() {
		SolrQuery query = new SolrQuery();
		//TODO: try setRequestHandler, standard, terms, /select
		query.setRequestHandler("standard");
		
		return query;
	}

	/**
	 * Process input into keyword or ntriple query lists, reject empty string inputs.
	 * @param naturalInput to be processed
	 * @throws SolrServerException 
	 */
	private void parseNaturalInputToQuery(String naturalInput, List<String> keywordQueries, List<String> ntripleQueries) throws SolrServerException {
		if (naturalInput.equals(""))
			return;

		String queryInput = naturalToSirenMulti(naturalInput);
		
		if (queryInput.equals(naturalInput))
			keywordQueries.add(queryInput);
		else
			ntripleQueries.add(queryInput);
	}
	
	/**
	 * Convert natural language multi-queries to SIREn queries
	 * @param line of natural language input
	 * @return
	 * @throws SolrServerException
	 */
	private String naturalToSirenMulti(String line) throws SolrServerException {
		if (!line.contains(MULTI_QUERY_DELIMITER))
			return ConvertQuery.naturalToSiren(line);
		
		String convertedQuery = line;
		String[] clauses = line.split(MULTI_QUERY_DELIMITER);
		int size = clauses.length;
		
		String firstNaturalQuery = clauses[size - 1];
		List<String> keywordQueries = new ArrayList<String>();
		List<String> ntripleQueries = new ArrayList<String>();
		SolrDocumentList results = executeNaturalQuery(firstNaturalQuery, keywordQueries, ntripleQueries);
		List<String> subjects = getSubjects(results);
		String predicate;
			
		for (int i = size - 2; i > 0; i--) {
			predicate = clauses[i];
			subjects = accumulateResults(predicate, subjects);
		}
			
		predicate = clauses[0];
		predicate = ConvertQuery.getQueryPredicate(predicate);
		convertedQuery = ConvertQuery.constructSirenMultiQuery(predicate, subjects);
		
		return convertedQuery;
	}
	
	/**
	 * Accumulate results by executing a list of queries and accumulating all results
	 * @param predicate for all queries
	 * @param objects list of objects to create multiple queries
	 * @return accumulated results from running all queries
	 * @throws SolrServerException
	 */
	private List<String> accumulateResults(String predicate, List<String> objects) throws SolrServerException {
		List<String> convertedQueryList = new ArrayList<String>();
		
		for (String object : objects) {
			//TODO: remove this, this is just a hack to remove a set of URI that never appear as objects in the corpus
			if (object.contains("www.worldcat.org/oclc"))
				continue;
			
			object = Text.encloseInDoubleQuotes(object);
			convertedQueryList.add(object);
		}
		
		String convertedQuery = Text.combineList(convertedQueryList, "OR");
		
		List<String> keywordQueries = new ArrayList<String>();
		List<String> ntripleQueries = new ArrayList<String>();
		
		SolrDocumentList resultList = executeNaturalQuery(convertedQuery, keywordQueries, ntripleQueries);
		List<String> allSubjects = getSubjects(resultList);
		
		return allSubjects;
	}

	/**
	 * Set the query based on lists of keyword and ntriple queries
	 * @param query to be set
	 * @param keywordQueryList to use for this particular query
	 * @param ntripleQueryList to use for this particular query
	 * @throws SolrServerException
	 */
	private void setQuery(SolrQuery query, List<String> keywordQueries, List<String> ntripleQueries) throws SolrServerException {
		String keywordQuery = Text.combineList(keywordQueries, DEFAULT_CONJUNCTION);
		String ntripleQuery = Text.combineList(ntripleQueries, DEFAULT_CONJUNCTION);
		
		query.setQuery(keywordQuery); //keyword search
		query.set(SirenParams.NQ, ntripleQuery); //ntriple search
		query.setRows(MAX_RESULTS);
	}

	/**
	 * Get console input given a bufferReader
	 * @param bufferRead read from console
	 * @throws IOException
	 */
	private String getInput(BufferedReader bufferRead) throws IOException {
		return bufferRead.readLine();
	}
	
	/**
	 * Print out each entry in the given document list, or NO_RESULTS if no results were found.
	 * @param resultList list to print entries from
	 */
	private void printResponse(SolrDocumentList resultList) {
		if (resultList.isEmpty())
			System.out.println(NO_RESULTS);
		
		System.out.println();
		
		for(SolrDocument doc: resultList) {
			System.out.println(doc);
		}
	
		System.out.println();
		System.out.println("input keywordQuery: " + keywordQueryList);
		System.out.println("input ntripleQuery: " + ntripleQueryList);
	}

	/**
	 * Get console response from the server given a constructed query
	 * @param query to be sent to the server
	 * @throws SolrServerException
	 */
	private SolrDocumentList getResponse(SolrQuery query) throws SolrServerException {
		//Post allows for longer queries
		QueryResponse response = server.query(query, METHOD.POST);
		SolrDocumentList resultList = response.getResults();
		
		return resultList;
	}
	
	/**
	 * Construct a list of subjects contained within a list of documents
	 * @param docList list of documents to extract subjects from
	 * @return list of subjects extracted
	 */
	private List<String> getSubjects(SolrDocumentList docList) {
		List<String> subjectList = new ArrayList<String>();
		
		for(SolrDocument doc: docList) {
			String subject = (String) doc.get("id");
			subject = subject.substring(1);
			subjectList.add(subject);
		}
		
		return subjectList;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, SolrServerException, ParserConfigurationException, SAXException {
		
		final QueryNT ntriple = new QueryNT(args.length == 1 ? args[0] : null);
		ConvertQuery.makeConversionTable(DEFINED_RELATIONS);
		ntriple.query();
	}

}
