/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projecttester;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.jena.util.FileManager;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.Filter;
/**
 *
 * @author Poonam
 */
class OntologyClassNode {

    private String name;
    private int weight;

    public OntologyClassNode(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}

public class OntologyServer {

    OntModel m;
    protected OntModel m_model;
    private Map m_anonIDs = new HashMap();
    private int m_anonCount = 0;
    Vector<OntologyClassNode> classes = new Vector<OntologyClassNode>(0, 1);

    public OntModel initializeOntology(String keyWord) {
            m = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF, null);
            InputStream ontologyIn = FileManager.get().open("/Users/poonamgohil/Desktop/dbpedia_3.9.owl");

            loadModel(m, ontologyIn);
            System.out.println("mila");
        
        return m;
        
    }
    
    protected static void loadModel(OntModel m, InputStream ontologyIn) {
        try {
             m.read(ontologyIn, "RDF/XML");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    
         
        
        


    private String setKeyWordToMatchOntology(String keyWord) {
        //keyWord="network vulnerablility";
    	String temp = keyWord.replaceAll(" ", "_");
        return temp;
        //System.out.println(temp);
    }

    public Vector<OntologyClassNode> showHierarchy(PrintStream out, OntModel m, String keyWord, Vector<String> s) {
        // create an iterator over the root classes that are not anonymous class expressions
        Iterator i = m.listHierarchyRootClasses().filterDrop(new Filter() {

            public boolean accept(Object o) {
                return ((Resource) o).isAnon();
            }
        });
        System.out.println("vvvv" + keyWord);
        //System.out.println("*(**************************************************called");
        OntClass c = m.getOntClass("http://www.w3.org/1999/02/22-rdf-syntax-ns#" + keyWord);
        int i1 = 0;
        //System.out.println("LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"+c);
        Vector<String> k1 = new Vector<String>(0, 1);
        if (c == null) {
            System.out.println("LOL");
            for (i1 = 0; i1 < s.size(); i1++) {
                String k = setKeyWordToMatchOntology(s.elementAt(i1));
                c = m.getOntClass("http://www.w3.org/1999/02/22-rdf-syntax-ns#" + k);
                if (c != null) {
                    break;
                }
            }
            if (i1 == s.size()) {
                //System.out.println("DOUBLE LOLL");
                getEntireHierarchy();

                for (int z = 0; z < classes.size(); z++) {
                    String className = classes.elementAt(z).getName();
                    //System.out.println(className);
                    //System.out.println(keyWord);
                    if (className.contains(keyWord)) {
                        k1.add(classes.elementAt(z).getName());
                    }
                }
            }
        }
        getAlternateHierarchy(k1, m);
        /*for(int ko=0;ko<classes.size();ko++){
        System.out.println(classes.elementAt(ko).getName());
        }*/
        if (c != null) {
            showClass(out, c, new ArrayList(), 0);
        }
        /*while (i.hasNext()) {
        showClass(out, (OntClass) i.next(), new ArrayList(), 0);
        }*/
        return classes;
    }

    protected void showClass(PrintStream out, OntClass cls, List occurs, int depth) {
        String classURI = "" + cls.getURI();
        String className = "" + cls.getModel().shortForm(classURI);
        className = className.substring(className.indexOf(":") + 1);
        //System.out.println("" + className);
        //if (className.equalsIgnoreCase("Attack")) {
        renderClassDescription(out, cls, depth);
        //out.println();
        //}
        // recurse to the next level down
        if (cls.canAs(OntClass.class) && !occurs.contains(cls)) {
            for (Iterator i = cls.listSubClasses(true); i.hasNext();) {
                OntClass sub = (OntClass) i.next();

                // we push this expression on the occurs list before we recurse
                occurs.add(cls);
                showClass(out, sub, occurs, depth + 1);
                occurs.remove(cls);
            }
        }

    }

    public void renderClassDescription(PrintStream out, OntClass c, int depth) {
        indent(out, depth);

        if (c.isRestriction()) {
            renderRestriction(out, (Restriction) c.as(Restriction.class));
        } else {
            if (!c.isAnon()) {
                //out.print("Class ");
                renderURI(out, c.getModel(), c.getURI(), depth);
                //out.print(' ');
            } else {
                renderAnonymous(out, c, "class");
            }
        }
    }

    protected void renderRestriction(PrintStream out, Restriction r) {
        if (!r.isAnon()) {
            //out.print("Restriction ");
            renderURI(out, r.getModel(), r.getURI(), 0);
        } else {
            renderAnonymous(out, r, "restriction");
        }

        out.print(" on property ");
        renderURI(out, r.getModel(), r.getOnProperty().getURI(), 0);
    }

    /** Render a URI */
    protected void renderURI(PrintStream out, PrefixMapping prefixes, String uri, int depth) {
        String name = prefixes.shortForm(uri);
        name = name.substring(name.indexOf(":") + 1);
        //out.print(prefixes.shortForm(uri));
        classes.add(new OntologyClassNode(name, (10 - depth)));
    }

    /** Render an anonymous class or restriction */
    protected void renderAnonymous(PrintStream out, Resource anon, String name) {
        String anonID = (String) m_anonIDs.get(anon.getId());
        if (anonID == null) {
            anonID = "a-" + m_anonCount++;
            m_anonIDs.put(anon.getId(), anonID);
        }

        out.print("Anonymous ");
        out.print(name);
        out.print(" with ID ");
        out.print(anonID);
    }

    /** Generate the indentation */
    protected void indent(PrintStream out, int depth) {
        for (int i = 0; i < depth; i++) {
            //out.print("\t");
        }
    }

    private Vector<OntologyClassNode> getEntireHierarchy() {
        Iterator i = m.listHierarchyRootClasses().filterDrop(new Filter() {

            public boolean accept(Object o) {
                return ((Resource) o).isAnon();
            }
        });
        while (i.hasNext()) {
            showClass(System.out, (OntClass) i.next(), new ArrayList(), 0);
        }
        return classes;
    }

    private Vector<OntologyClassNode> getAlternateHierarchy(Vector<String> k, OntModel m) {
        classes.removeAllElements();
        for (int i1 = 0; i1 < k.size(); i1++) {
            Iterator i = m.listHierarchyRootClasses().filterDrop(new Filter() {

                public boolean accept(Object o) {
                    return ((Resource) o).isAnon();
                }
            });
            //System.out.println("*(**************************************************called");
            OntClass c = m.getOntClass("http://www.w3.org/1999/02/22-rdf-syntax-ns#" + k.elementAt(i1));
            showClass(System.out, c, new ArrayList(), 0);
        }
        return classes;
    }
}
