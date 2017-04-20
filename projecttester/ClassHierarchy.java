/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projecttester;

/**
 *
 * @author Poonam
 */
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.Filter;

import java.io.PrintStream;
import java.util.*;

/**
 * <p>
 * Simple demonstration program to show how to list a hierarchy of classes. This
 * is not a complete solution to the problem (sub-classes of restrictions, for example,
 * are not shown).  It is inteded only to be illustrative of the general approach.
 * </p>
 *
 * @author Ian Dickinson, HP Labs
 *         (<a  href="mailto:Ian.Dickinson@hp.com" >email</a>)
 * @version CVS $Id: ClassHierarchy.java.html,v 1.4 2007/01/17 10:44:18 andy_seaborne Exp $
 */
public class ClassHierarchy {
    // Constants
    //////////////////////////////////

    // Static variables
    //////////////////////////////////
    // Instance variables
    //////////////////////////////////
    protected OntModel m_model;
    private Map m_anonIDs = new HashMap();
    private int m_anonCount = 0;

    // Constructors
    //////////////////////////////////
    // External signature methods
    //////////////////////////////////
    /** Show the sub-class hierarchy encoded by the given model */
    public void showHierarchy(PrintStream out, OntModel m) {
        // create an iterator over the root classes that are not anonymous class expressions
        Iterator i = m.listHierarchyRootClasses().filterDrop(new Filter() {

            public boolean accept(Object o) {
                return ((Resource) o).isAnon();
            }
        });
        OntClass c=m.getOntClass("http://www.semanticweb.org/ontologies/2012/1/Ontology1329329289796.owl#MaliciousCode");
        //showClass(out, c, new ArrayList(), 0);
        while (i.hasNext()) {
            showClass(out, (OntClass) i.next(), new ArrayList(), 0);
        }
    }

    // Internal implementation methods
    //////////////////////////////////
    /** Present a class, then recurse down to the sub-classes.
     *  Use occurs check to prevent getting stuck in a loop
     */
    protected void showClass(PrintStream out, OntClass cls, List occurs, int depth) {
        String classURI = "" + cls.getURI();
        String className = "" + cls.getModel().shortForm(classURI);
        className = className.substring(className.indexOf(":") + 1);
        //System.out.println("" + className);
        //if (className.equalsIgnoreCase("Attack")) {
            renderClassDescription(out, cls, depth);
            out.println();
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

    /**
     * <p>Render a description of the given class to the given output stream.</p>
     * @param out A print stream to write to
     * @param c The class to render
     */
    public void renderClassDescription(PrintStream out, OntClass c, int depth) {
        indent(out, depth);

        if (c.isRestriction()) {
            renderRestriction(out, (Restriction) c.as(Restriction.class));
        } else {
            if (!c.isAnon()) {
                out.print("Class ");
                renderURI(out, c.getModel(), c.getURI());
                out.print(' ');
            } else {
                renderAnonymous(out, c, "class");
            }
        }
    }

    /**
     * <p>Handle the case of rendering a restriction.</p>
     * @param out The print stream to write to
     * @param r The restriction to render
     */
    protected void renderRestriction(PrintStream out, Restriction r) {
        if (!r.isAnon()) {
            out.print("Restriction ");
            renderURI(out, r.getModel(), r.getURI());
        } else {
            renderAnonymous(out, r, "restriction");
        }

        out.print(" on property ");
        renderURI(out, r.getModel(), r.getOnProperty().getURI());
    }

    /** Render a URI */
    protected void renderURI(PrintStream out, PrefixMapping prefixes, String uri) {
        System.out.println(uri);
        out.print(prefixes.shortForm(uri));
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
            out.print("\t");
        }
    }
    //==============================================================================
    // Inner class definitions
    //==============================================================================
}
