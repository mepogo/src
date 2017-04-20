/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package projecttester;

import java.net.URL;

/**
 *
 * @author Poonam
 */
public class URLFronierElement {
    private URL url;
    private double relevance;

    public URLFronierElement(URL url, double relevance) {
        this.url = url;
        this.relevance = relevance;
    }

    public URLFronierElement(URL url) {
        this.url = url;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }



}
