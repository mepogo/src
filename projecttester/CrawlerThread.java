package projecttester;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author Poonam
 */
public class CrawlerThread extends Thread {

	String threadName;
	String DISALLOW = "Disallow:";
	URLFrontier currentFrontiers;
	ThreadController tc;
	RelevanceCalculator rc;
	URLFronierElement element;
	String title;
	ProjectApp proApp;
	// Vector<RobotEliminator> re;

	public CrawlerThread(String name, URL seed, ThreadController tc, double relevance, String splitTitle) {
		super(name);
		threadName = name;
		this.tc = tc;
		rc = new RelevanceCalculator(tc.keyWord, seed, splitTitle);
		element = new URLFronierElement(seed, rc.getRank());
		currentFrontiers = new URLFrontier(element);
		currentFrontiers.addDiscoveredURL(element);
		this.title = splitTitle;
		// re = new Vector<RobotEliminator>(0, 1);
	}

	public CrawlerThread(String name, ThreadController tc, String splitTitle) {
		super(name);
		threadName = name;
		this.tc = tc;
		currentFrontiers = new URLFrontier();
		this.title = splitTitle;
		// re = new Vector<RobotEliminator>(0, 1);
	}

	public CrawlerThread(ProjectApp projectApp) {
		// TODO Auto-generated constructor stub
		proApp = projectApp;
	}

	public void run() {
		long relevance = 0, count = 0;
		
		double mappedRelevance = 0.0;
		try {
			// System.out.println("" + getName() + " started");
			while (true) {
				if (this.currentFrontiers.getDiscoveredSize() == 0) {
					// System.out.println("" + getName() + " is sleeping");
					// sleep(15);
				} else {
					System.out.println("****************************************************************");
					while ((currentFrontiers.getDiscoveredSize() > 0)) {
						try {
							URLFronierElement ele = currentFrontiers.getDiscoveredURL();
							URL url = ele.getUrl();
							// url=new URL("https://www.gmail.com");
							String host = url.getHost();
							String temp = "" + url;
							if (temp.contains("?")) {
								temp = temp.substring(0, temp.indexOf("?"));
								if (temp.charAt(temp.length() - 1) == '/') {
									temp = temp.substring(0, temp.length() - 1);
								}
								url = new URL(temp);
							}
							ProjectApp.jLabel2.setText("" + url);
							if (!url.getProtocol().equals("http")) {
								continue;
							}
							// System.out.println("hello");
							RobotEliminator r = ThreadController.getRobotEliminator(url);
							if (!r.robotSafe(url)) {
								System.out.println("NOT ROBOT SAFE: " + url);
								currentFrontiers.addDisallowedURL(ele);
								continue;
							}
							if (true) {
								rc = new RelevanceCalculator(tc.keyWord, url, title);
								ele = new URLFronierElement(url, rc.getRank());
							}
							System.out.println("ROBOT SAFE " + url);

							Connection.Response response = null;
							response = Jsoup.connect("" + url)
									.userAgent(
											"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
									.timeout(10000).execute();

							Document doc = response.parse();
							Elements anchorLinks = doc.getElementsByTag("a");
							currentFrontiers.addCrawledURL(ele);
							System.out.println("Completed" + ele.getUrl());
							int noOfLinks = anchorLinks.size();
							// System.out.println(anchorLinks.size());
							for (int i = 0; i < anchorLinks.size(); i++) {
								try {
									URL url1 = new URL(anchorLinks.get(i).attr("href"));
									if (!r.robotSafe(url1)) {
										URLFronierElement d = new URLFronierElement(url);
										currentFrontiers.addDisallowedURL(d);
										anchorLinks.remove(i);
									}
								} catch (Exception e) {
									// System.out.println(e.getMessage()
									// +anchorLinks.attr("href"));
									anchorLinks.remove(i);
									continue;
								}
							}
							// System.out.println(anchorLinks.size());
							// System.exit(0);
							// Vector<URLFronierElement> urlsToCrawl =
							// getMoreRelevantLinks(anchorLinks);
							for (int i = 0; i < anchorLinks.size(); i++) {
								try {
									String link = anchorLinks.get(i).attr("href");
									if (link.startsWith("http") || link.startsWith("https")) {
										URL urlToMap = new URL(anchorLinks.get(i).attr("href"));
										String temp1 = "" + urlToMap;
										if (temp1.contains("?")) {
											temp1 = temp1.substring(0, temp1.indexOf("?"));
											urlToMap = new URL(temp1);
										}
										if (temp1.charAt(temp1.length() - 1) == '/') {
											temp1 = temp1.substring(0, temp1.length() - 1);
											urlToMap = new URL(temp1);
										}
										System.out.println("FFFFFFFFF" + temp1);
										// rc = new
										// RelevanceCalculator(tc.keyWord,
										// urlToMap);
										URLFronierElement e = new URLFronierElement(urlToMap, ele.getRelevance());
										relevance += ele.getRelevance();
										count++;
										tc.map(e);

									}
								} catch (Exception e) {
									e.printStackTrace();
									continue;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
					}
				}
				
			}
			
					
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	
}
