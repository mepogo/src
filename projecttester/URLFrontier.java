/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package projecttester;

import java.net.URL;
import java.util.Vector;

/**
 *
 * @author Poonam
 */
public class URLFrontier {
    private Vector<URLFronierElement> urlsDiscovered;
    private Vector<URLFronierElement> urlsCrawled;
    private Vector<URLFronierElement> urlsDisallowed;
    private Vector<URLFronierElement> urlsToCrawl;
    private int maxIndex,crawlIndex=0,disallowIndex=0;
    public URLFrontier() {
        urlsDiscovered=new Vector<URLFronierElement>(0,1);
        urlsCrawled = new Vector<URLFronierElement>(0,1);
        urlsDisallowed = new Vector<URLFronierElement>(0,1);
        urlsToCrawl=new Vector<URLFronierElement>(0, 1);
        maxIndex=0;
    }

    public URLFrontier(URLFronierElement discoveredURL) {
        urlsDiscovered=new Vector<URLFronierElement>(0,1);
        urlsCrawled = new Vector<URLFronierElement>(0,1);
        urlsDisallowed = new Vector<URLFronierElement>(0,1);
        maxIndex=0;
    }

    public void addDiscoveredURL(URLFronierElement e){
        urlsDiscovered.add(e);
        double r=urlsDiscovered.get(maxIndex).getRelevance();
        if(r<e.getRelevance()){
            maxIndex=urlsDiscovered.size()-1;
        }
    }

    public void addCrawledURL(URLFronierElement e){
        urlsCrawled.add(e);
        /*double r=urlsCrawled.get(maxIndex).getRelevance();
        if(r<e.getRelevance())
            maxIndex=urlsCrawled.size()-1;*/
    }

    public void addDisallowedURL(URLFronierElement e){
        urlsDisallowed.add(e);
        /*double r=urlsDisallowed.get(maxIndex).getRelevance();
        if(r<e.getRelevance())
            maxIndex=urlsDisallowed.size()-1;*/
    }

    public URLFronierElement getDiscoveredURL(){
        URLFronierElement url=null;
        url=urlsDiscovered.get(maxIndex);
        
        urlsDiscovered.removeElementAt(maxIndex);
        setmaxIndex();
        return url;
    }

    public URLFronierElement getCrawledURL(){
        URLFronierElement url=null;
        url=urlsCrawled.get(crawlIndex++);
        
        //setmaxIndex(urlsCrawled);
        return url;
    }

    /*public URL getDisallowedURL(){
        URL url=null;
        url=urlsDisallowed.get(maxIndex).getUrl();
        //setmaxIndex(urlsDisallowed);
        return url;
    }*/

    private void setmaxIndex() {

        int index=-1;double m=0;
        for(int i=0;i<urlsDiscovered.size();i++){
            double r=urlsDiscovered.get(i).getRelevance();
            if(r>m){
                m=r;
                index=i;
            }
        }
        if(index==-1)index=0;
        maxIndex=index;
        System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
    }

    public boolean inCrawledFrontier(URL url){
        for(int i=0;i<urlsCrawled.size();i++){
            if((""+urlsCrawled.get(i).getUrl()).equals(""+url))
                return true;
        }
        return false;
    }

    public boolean inDiscoveredFrontier(URL url){
        for(int i=0;i<urlsDiscovered.size();i++){
            if((""+urlsDiscovered.get(i).getUrl()).equals(""+url))
                return true;
        }
        return false;
    }

    public boolean inDisallowedFrontier(URL url){
        for(int i=0;i<urlsDisallowed.size();i++){
            if((""+urlsDisallowed.get(i).getUrl()).equals(""+url))
                return true;
        }
        return false;
    }

    public int getCrawledSize(){
        return urlsCrawled.size();
    }

    public int getDiscoveredSize(){
        return urlsDiscovered.size();
    }

    public int getDisallowedSize(){
        return urlsDisallowed.size();
    }
}
