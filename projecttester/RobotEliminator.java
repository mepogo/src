/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projecttester;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 * @author Poonam
 */
public class RobotEliminator {

    String host;
    private Vector<String> dissallowed;
    final String DISALLOW = "Disallow:";

    public RobotEliminator() {
        this.dissallowed = new Vector<String>(0, 1);
    }

    public RobotEliminator(String host) {
        //System.out.println("GOT NEW HOST: " + host);
        this.dissallowed = new Vector<String>(0, 1);
        this.host = host;
        initializeDisallowVector();
    }

    void initializeDisallowVector() {
        String strRobot = "http://" + host + "/robots.txt";
        URL urlRobot = null;
        try {
            urlRobot = new URL(strRobot);
        } catch (MalformedURLException e) {
            // something weird is happening, so don't trust it
        }

        String strCommands;
        try {
            InputStream urlRobotStream = urlRobot.openStream();

            // read in entire file
            byte b[] = new byte[1000];
            int numRead = urlRobotStream.read(b);
            strCommands = new String(b, 0, numRead);
            while (numRead != -1) {
                /*if (Thread.currentThread() != searchThread)
                break;*/
                numRead = urlRobotStream.read(b);
                if (numRead != -1) {
                    String newCommands = new String(b, 0, numRead);
                    strCommands += newCommands;
                }
            }
            urlRobotStream.close();
            /*System.out.println("ROBOT START***************************************");
            System.out.println(strCommands);
            System.out.println("ROBOT END***************************************");*/
            int index = 0;
            while ((index = strCommands.indexOf(DISALLOW, index)) != -1) {
                index += DISALLOW.length();
                String strPath = strCommands.substring(index);

                StringTokenizer st = new StringTokenizer(strPath);

                if (!st.hasMoreTokens()) {
                    break;
                }

                String strBadPath = st.nextToken();
                //System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
                //System.out.println(strBadPath);
                dissallowed.add(strBadPath);
            }
        } catch (IOException e) {
            // if there is no robots.txt file, it is OK to search
            //System.out.println("LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
        }

    }

    boolean robotSafe(URL url) {

        // form URL of the robots.txt file
        //System.out.println("HELLOOO");

        // assume that this robots.txt refers to us and
        // search for "Disallow:" commands.
        String strURL = url.getFile();
        //System.out.println("KKKKKKKKKKKKKKKKKKKKKKKKKKKK: "+strURL);
        //System.out.println(strURL);
        //System.out.println("dddddddddddddddddddddddddddddddddd "+strBadPath);
        // if the URL starts with a disallowed path, it is not safe
        for (int i = 0; i < dissallowed.size(); i++) {
            if ((strURL.indexOf(new String("" + dissallowed.elementAt(i))) == 0)
                    && !("" + dissallowed.elementAt(i)).equals("/")) {
                //System.out.println("" + dissallowed.elementAt(i));
                //System.out.println("**************************************Disallow: " + url);
                return false;
            }
        }
        //System.out.println("hellllllll");
        return true;
    }
}
