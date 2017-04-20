/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projecttester;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Poonam
 */
public class ThreadController extends Thread {

	Vector<URL> seeds;
	int maxThreads;
	String keyWord;
	String OntologySelected;
	boolean over;
	URL seedURL;
	DefaultTableModel tm;
	CrawlerThread[] threads;
	static Vector<RobotEliminator> re;
	static RobotEliminator r;
	public Vector<URLFronierElement> result;
	Vector<String> threadName;
	java.util.Date d1, d2;
	ProjectApp proApp;

	double totalRelevance = 0.0;
	double mappedRelevance = 0.0;

	ThreadController(ProjectApp obj) {
		proApp = obj;
	}

	ThreadController(int maxThreads, URL seed, DefaultTableModel tm, String splitTitle) {
		re = new Vector<RobotEliminator>(0, 1);
		this.maxThreads = maxThreads;
		this.tm = tm;
		seedURL = seed;
		threads = new CrawlerThread[maxThreads];
		over = false;
		keyWord = "";
		result = new Vector<URLFronierElement>(0, 1);
		threadName = new Vector<String>(0, 1);
		generateThreads(seed, splitTitle);
		for (URLFronierElement urlFronierElement : result) {
			totalRelevance = +urlFronierElement.getRelevance();
		}

	}

	ThreadController(int maxThreads, Vector<URL> seeds, DefaultTableModel tm, String k, String o, String splitTitle) {
		re = new Vector<RobotEliminator>(0, 1);
		this.maxThreads = maxThreads;
		this.tm = tm;
		this.seeds = new Vector<URL>(0, 1);
		for (int i = 0; i < seeds.size(); i++) {
			this.seeds.add(seeds.elementAt(i));
		}
		threads = new CrawlerThread[maxThreads];
		over = false;
		keyWord = k;
		OntologySelected = o;
		result = new Vector<URLFronierElement>(0, 1);
		threadName = new Vector<String>(0, 1);
		generateThreads2(seeds, splitTitle);

	}

	public void generateThreads(URL seed, String splitTitle) {
		System.out.println("BEEEEEEEPPPPPPPPPPPPPPPPPSAAAALAAAA");
		for (int i = 0; i < maxThreads; i++) {
			if (i == 0) {
				threads[i] = new CrawlerThread("thread " + i, seedURL, this, 0, splitTitle);
				threads[i].start();

			} else {
				threads[i] = new CrawlerThread("thread " + i, this, splitTitle);
			}
		}
	}

	public static int get(int s, char c) {
		int msb = s & 0x80;
		if (msb == 0) {
			return 2 * s + c;
		} else {
			return (2 * s + c) ^ 0x90;
		}
	}

	boolean urlCrawled(URL url) {
		boolean flag = false;
		for (int i = 0; i < maxThreads; i++) {
			if (threads[i].currentFrontiers.inCrawledFrontier(url)
					|| threads[i].currentFrontiers.inDiscoveredFrontier(url)) {
				return true;
			}
		}
		return flag;
	}

	public boolean processOver() {
		return over;
	}

	public void map(URLFronierElement element) {
		try {
			URL url = element.getUrl();
			String u = "" + url.getFile();

			int start = 0;
			if (!urlCrawled(url)) {
				for (int i = 0; i < u.length(); i++) {
					start = get(start, u.charAt(i));
				}
				int thread = Math.abs(start % maxThreads);

				if ((!threads[thread].currentFrontiers.inCrawledFrontier(url))
						&& (!threads[thread].currentFrontiers.inDiscoveredFrontier(url))) {
					System.out.println("****************************************** " + url + " mapped to " + thread);
					if (!threads[thread].isAlive()) {

						// System.out.println(threads[thread]+"*****************************************************************
						// "+url);
						threads[thread].currentFrontiers.addDiscoveredURL(element);
						threads[thread].start();
					} else {
						// System.out.println("KKKKKKK");
						threads[thread].currentFrontiers.addDiscoveredURL(element);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void destroyThreads() {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		for (int i = 0; i < maxThreads; i++) {
			if (threads[i].isAlive()) {
				threads[i].stop();
			}
		}
		// this.stop();

		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("Bye");
	}

	public void run() {
		d1 = new Date();
		int count = 0;
		System.out.println("_____________________________________________TIME: " + d1.getTime());
		try {
			ProjectApp.jProgressBar1.setVisible(true);
			int s[] = new int[maxThreads];

			while (!over) {
				// System.out.println("hi");
				for (int i = 0; i < maxThreads; i++) {
					if ((threads[i].currentFrontiers.getCrawledSize()) > s[i]) {
						URLFronierElement element = threads[i].currentFrontiers.getCrawledURL();

						// tm.addRow(new Object[]{element.getUrl(), "Processed
						// by thread " + i, element.getRelevance()});
						if (count == 0) {
							result.add(element);
							threadName.add("processed by thread" + i);
						}
						int j = count;
						int temp = j;
						while (j > 0 && result.elementAt(j - 1).getRelevance() >= element.getRelevance()) {
							j--;
						}
						result.insertElementAt(element, j);
						threadName.insertElementAt("Processed by thread " + i, j);
						s[i]++;
						count++;
					}
					if (count >= 10) {
						over = true;
						ProjectApp.jProgressBar1.setVisible(false);
						ProjectApp.jLabel2.setText("Done: Crawled " + count + " links successfully");
						for (int i1 = 0; i1 < threads.length; i1++) {
							System.out.println(threads[i1].currentFrontiers.getDiscoveredSize());
							System.out.println(threads[i1].currentFrontiers.getDisallowedSize());
							System.out.println(threads[i1].currentFrontiers.getCrawledSize());
							System.out.println("*****************************************************");
						}
						// quick_srt(result, 0, result.size()-1);
						d2 = new Date();
						System.out.println("Computed in " + (d2.getTime() - d1.getTime()) / 1000 + " secs");
						destroyThreads();
					}
				}

			}
			int counter = 0;
			double total = 0.0;
			for (int ir = result.size() - 1; ir >= 0; ir--) {
				System.out.println(
						"URL Details : " + result.elementAt(ir).getUrl() + "\t" + result.elementAt(ir).getRelevance());

				if (result.elementAt(ir).getRelevance() != 0) {
					total += result.elementAt(ir).getRelevance();
					counter++;
				}
				
			}
			
			try{
				BufferedWriter bw = new BufferedWriter(
					new FileWriter("/Users/poonamgohil/Desktop/Relevance2.txt", true)); 

				/*if (relevance >= 100) {
					String content = "Relevance Percentage = 100%\n";
					proApp.jTextArea3.append("Relevance Percentage = 100%");
					bw.write(content);
					bw.close();

				} else {*/
					String content = "Relevant: " + (total / counter) + "\n";
					//proApp.jTextArea3.append("Relevance Percentage = " + (total / counter) * 100);
					bw.write(content + "------------------------------------------------------------------------------------------\n\n");
					bw.close();
				//}
			}catch(Exception e1){
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static RobotEliminator getRobotEliminator(URL url) {
		r = null;
		int i1 = 0;

		for (i1 = 0; i1 < re.size(); i1++) {
			System.out
					.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG: " + (re.elementAt(i1)).host + "\t" + re.size());
			if (url.getHost().equals((re.elementAt(i1)).host)) {
				r = re.elementAt(i1);
				break;
			}
		}
		if (i1 == re.size()) {
			re.add(new RobotEliminator(url.getHost()));
			r = re.elementAt(re.size() - 1);
		}
		return r;
	}

	public void generateThreads2(Vector<URL> seeds2, String splitTitle) {
		System.out.println("2 mein huuuuuuuuuuuuuuuuuu");
		boolean init[] = new boolean[maxThreads];
		for (int i = 0; i < maxThreads; i++) {
			if (i < seeds2.size()) {
				init[i] = true;
				threads[i] = new CrawlerThread("thread " + i, seeds2.elementAt(i), this, 0, splitTitle);
			} else {
				init[i] = false;
				threads[i] = new CrawlerThread("thread " + i, this, splitTitle);
			}
		}
		for (int i = 0; i < maxThreads; i++) {
			if (init[i])
				threads[i].start();
		}

	}

	private void sort() {
		for (int i = 0; i < result.size(); i++) {
			// for (int ) {
			// }
		}
	}

	private void quick_srt(Vector<URLFronierElement> r, int low, int n) {
		int lo = low;
		int hi = n;
		if (lo >= n) {
			return;
		}
		double mid = r.elementAt((lo + hi) / 2).getRelevance();
		while (lo < hi) {
			while (lo < hi && r.elementAt(lo).getRelevance() < mid) {
				lo++;
			}
			while (lo < hi && r.elementAt(hi).getRelevance() > mid) {
				hi--;
			}
			if (lo < hi) {
				URLFronierElement T = r.elementAt(lo);
				r.insertElementAt(r.elementAt(hi), lo);
				r.insertElementAt(T, hi);
			}
		}
		if (hi < lo) {
			int T = hi;
			hi = lo;
			lo = T;
		}
		quick_srt(r, low, lo);
		quick_srt(r, lo == low ? lo + 1 : lo, n);
	}

	void sortResult(DefaultTableModel tm) {
		for (int i = result.size() - 1; i >= 0; i--) {
			tm.addRow(new Object[] { result.elementAt(i).getUrl(), threadName.elementAt(i),
					result.elementAt(i).getRelevance() });
		}
	}
}
