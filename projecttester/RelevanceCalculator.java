/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projecttester;

import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.hp.hpl.jena.ontology.OntModel;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

/**
 *
 * @author Poonam
 */
public class RelevanceCalculator {
	public static String[] stopwords = { "a", "as", "able", "about", "**", "above", "according", "accordingly",
			"across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost",
			"alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another",
			"any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear",
			"appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at",
			"available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been",
			"before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better",
			"between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant",
			"cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning",
			"consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could",
			"couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do",
			"does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight",
			"either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every",
			"everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few",
			"ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth",
			"four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going",
			"gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent",
			"having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein",
			"hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit",
			"however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc",
			"indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is",
			"isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows",
			"known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like",
			"liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me",
			"mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my",
			"myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never",
			"nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not",
			"nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on",
			"once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours",
			"ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps",
			"placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv",
			"rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively",
			"respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see",
			"seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious",
			"seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some",
			"somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon",
			"sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken",
			"tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their",
			"theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore",
			"therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third",
			"this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to",
			"together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two",
			"un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used",
			"useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants",
			"was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent",
			"what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas",
			"whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos",
			"whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont",
			"wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your",
			"yours", "yourself", "yourselves", "zero" };

	String keyWord;
	String title;
	URL pageLink;
	Double pageRank;
	Vector<String> synonym;
	Vector<OntologyClassNode> v1;

	public RelevanceCalculator(String keyWord, URL pageLink, String title) {
		this.keyWord = keyWord;
		this.title = title;
		this.pageLink = pageLink;
		synonym = getSynonyms();
		System.out.println(setKeyWordToMatchOntology());
		OntologyServer server = new OntologyServer();
		OntModel m = server.initializeOntology("dbpedia");
		/*
		 * System.out.println("dddddd"+keyWord); v1 =
		 * server.showHierarchy(System.out, m, setKeyWordToMatchOntology(),
		 * synonym); if (v1 != null) { for (int i = 0; i < v1.size(); i++) {
		 * System.out.println(""+v1.elementAt(i).getName()); } }
		 */
		// System.exit(0);
	}

	public static void enableSSLSocket() throws KeyManagementException, NoSuchAlgorithmException {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});

		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, new X509TrustManager[] { new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		} }, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	}

	public double getRank() {
		System.out.println("THIS IS " + keyWord);

		double sumOfWeights = 0.0;
		Elements body = null;

		String[] word = keyWord.split(" ");
		for (String string : word) {
			if (!stopwords.toString().contains(string)) {

				try {
					setKeyWordToMatchOntology();

					// Create a new trust manager that trust all certificates
					//enableSSLSocket();

					Connection.Response response = null;
					response = Jsoup.connect("" + pageLink)
							.userAgent(
									"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
							.timeout(10000).execute();

					Document doc = response.parse();
					Elements head = doc.select("meta[name=description]");
					String meta = head.attr("content");
					body = (doc.getElementsByTag("body"));

					StringTokenizer st = new StringTokenizer(head.text());
					// checking meta description.....
					st = new StringTokenizer(meta);
					while (st.hasMoreTokens()) {
						String s = st.nextToken();
						if (synonym.contains(s) || s.equalsIgnoreCase(string)) {
							sumOfWeights += 10;
						}
					}
					// checking Body Content......
					st = new StringTokenizer(body.text());
					while (st.hasMoreTokens()) {
						String s = st.nextToken();
						if (synonym.contains(s) || s.equalsIgnoreCase(string)) {
							sumOfWeights += 1;
						}
					}
					
				} catch (Exception e) {
					// System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVv
					// "+pageLink);
					// e.printStackTrace();
					return 0;
				}

			}
		}
		return (double) sumOfWeights / (double) body.size();
	}

	Vector<String> getSynonyms() {
		Vector<String> synonyms = new Vector<String>(0, 1);
		System.setProperty("wordnet.database.dir", "WordNet/2.1/dict/");
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		NounSynset nounSynset;
		NounSynset[] hyponyms;
		NounSynset[] hyeronyms;
		// WordNetDatabase database = WordNetDatabase.getFileInstance();
		String splitKeyword[] = keyWord.split(" ");
		for (String string : splitKeyword) {
			Synset[] synsets = database.getSynsets(keyWord, SynsetType.NOUN);
			for (int i = 0; i < synsets.length; i++) {
				nounSynset = (NounSynset) (synsets[i]);
				hyponyms = nounSynset.getHyponyms();
				hyeronyms = nounSynset.getHypernyms();
				/*
				 * System.err.println(nounSynset.getWordForms()[0] + ": " +
				 * nounSynset.getDefinition() + ") has " + hyponyms.length +
				 * " hyponyms"+" and in topic: "+hyeronyms.length);
				 */
				for (int z = 0; z < hyeronyms.length; z++) {
					String h[] = hyeronyms[z].getWordForms();
					for (int j = 0; j < h.length; j++) {
						// System.out.println(h[j]);
						synonyms.add("" + h[j]);
					}
				}
			}
		}

		return synonyms;
	}

	/*
	 * public static void main(String[] args) throws Exception {
	 * RelevanceCalculator r = new RelevanceCalculator("sedan", new
	 * URL("http://searchsecurity.techtarget.com/definition/authentication"));
	 * //r.getSynonyms(); //r.getRank();
	 * //System.out.println(r.setOntologyNameToMatchContent("WeakAuthentication"
	 * )); System.out.println(r.keyWord); r.setKeyWordToMatchOntology();
	 * System.out.println(r.keyWord); System.exit(0); }
	 */

	private String setKeyWordToMatchOntology() {
		// keyWord="network vulnerablility";
		String temp = title.replaceAll(" ", "_");
		return temp;
	}

	private String setOntologyNameToMatchContent(String name) {
		String s = "";
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) <= 90 && i != 0) {
				s += (" " + name.charAt(i)).toLowerCase();
			} else {
				s = s + name.charAt(i);
			}
		}
		return s;
	}

}
