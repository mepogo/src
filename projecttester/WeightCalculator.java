/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projecttester;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WeightCalculator {
	Element body = null;
	Elements metadata = null;
	ArrayList<String> crawlurls = new ArrayList<>();

	WeightCalculator(ArrayList<String> ontologyurl) {

		crawlurls = ontologyurl;

	}

	public static boolean pingURL(String url, int timeout) {
		url = url.replaceFirst("^https", "http"); // Otherwise an exception may
													// be thrown on invalid SSL
													// certificates.

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			return (200 <= responseCode && responseCode <= 399);
		} catch (IOException exception) {
			return false;
		}
	}

	/*
	 * crawl urls and get weight
	 * 
	 * 
	 */
	int irrelevantweightSend = 1;

	int irrelevantweightSend() {
		return irrelevantweightSend;
	}

	int getKeywordweight(String keyword) {
		int relevantweight = 10;
		int irrelevantweight = 1;
		HtmlDoc parserobject = null;
		for (int w = 0; w < crawlurls.size(); w++) {
			// boolean val = serverConnection(crawlurls.get(w));

			Pattern purl = Pattern.compile("^(http|https)://"); // validate url

			Matcher mrl = purl.matcher(crawlurls.get(w));
			Pattern pattern = Pattern.compile("(.pdf|.doc|.Doc|.Pdf|.PDF)");
			Matcher matcher = pattern.matcher(crawlurls.get(w));
			if (mrl.find()) {
				if (!matcher.find()) {
					boolean val = pingURL(crawlurls.get(w), 5000);
					if (val == true) {

						parserobject = new HtmlDoc(crawlurls.get(w));
						Element body = parserobject.body(); // body words
						Elements metacontent = parserobject.metadata("meta[name=description]"); // meta
																								// tag
																								// words
						if (keyword.trim() != "") {
							Pattern p = Pattern.compile(keyword);
							Matcher m = p.matcher(body.text());
							Matcher m2 = p.matcher(metacontent.text());
							while (m.find()) {
								relevantweight = relevantweight + 10; // word
																		// found
																		// matches

							}
							while (m2.find()) {
								relevantweight = relevantweight + 10; //

							}
							irrelevantweight = countWords(body.text());// total
																		// no of
																		// words
							irrelevantweight += countWords(metacontent.text());
						}
					}
				}
			}
			// System.out.println(i);
		}
		irrelevantweightSend += irrelevantweight;
		// weight is frequency/total
		return relevantweight / irrelevantweight;

	}

	public int countWords(String s) {

		int wordCount = 0;

		boolean word = false;
		int endOfLine = s.length() - 1;

		for (int i = 0; i < s.length(); i++) {
			// if the char is a letter, word = true.
			if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
				word = true;
				// if char isn't a letter and there have been letters before,
				// counter goes up.
			} else if (!Character.isLetter(s.charAt(i)) && word) {
				wordCount++;
				word = false;
				// last word of String; if it doesn't end with a non letter, it
				// wouldn't count without this.
			} else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
				wordCount++;
			}
		}
		return wordCount;
	}

}
