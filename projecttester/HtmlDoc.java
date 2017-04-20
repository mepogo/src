/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projecttester;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
 

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Poonam
 */

public class HtmlDoc {
    static String url = ""; 
   static Document doc = null;
    static Elements newsHeadlines= null;
    HtmlDoc (String geturl){
    
    url = geturl;
   
        try {
            doc = (Document) Jsoup.connect(url).get();
        
 
        } catch (IOException ex) {
         
            System.out.println("Url is not supported");            
//  Logger.getLogger(HtmlDoc.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
  
    /**
     *
     * @param tag
     * @return
     */
    
   
    public  Element body(){
        Element body = doc.body();
    return body;
    }
    public  Elements metadata(String tag){
        newsHeadlines = (Elements) doc.select(tag);
    return newsHeadlines;
    }
    
        public  String Title(){
       String title = doc.title();
    return title;
    }
    
    
    
    
    
    
    
    
}
