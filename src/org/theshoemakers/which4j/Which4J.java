/*

Copyright (c) 2003, theshoemakers.org
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this 
      list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, 
      this list of conditions and the following disclaimer in the documentation 
      and/or other materials provided with the distribution.
    * Neither the name of the theshoemakers.org nor the names of its contributors 
      may be used to endorse or promote products derived from this software without 
      specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
OF SUCH DAMAGE.

*/

package org.theshoemakers.which4j;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Find occurrencens of a particular classname in the system classpath
 * or the specified ClassLoader.
 * 
 * This class can be run in command-line mode to search your system
 * classpath for all occurrences of the specified classname.
 * 
 * This class can also be used programmatically to search a particular
 * ClassLoader for the first occurrence of the specified classname.
 * <p>
 * Revision: $Revision: 1.1 $
 * 
 * @author <a href="mailto:Ryan.Shoemaker@Sun.COM">Ryan Shoemaker</a>, Sun Microsystems, Inc.
 * @version 0.1
 */
public class Which4J {

    private static boolean debug = false;
    
    /**
     * Main method used in command-line mode for searching the system
     * classpath for <em>all</em> occurrences of the specified classname.
     * 
     * @param args command-line arguments (run "<code>java org.theshoemakers.tools.which4j.Which4J 
     *             -help</code>" for a detailed description).
     */
    public static void main(String[] args) {
        String s = System.getProperty("line.separator");
        if( args.length == 2 ) {
            if( args[0].equals( "-debug" ) ) {
                debug = true;
                findIt( args[1] );
            } else {
                System.err.println( "error: unrecognized \"" + 
                                    args[0] + 
                                    "\" option " );
                System.exit( -1 );
            }
        } else if( args.length == 1 && args[0].equals( "-help" ) ) {
            usage();
        } else if( args.length == 1 ) {
            findIt( args[0] );
        } else {
            usage();
        }
        
        System.exit( 0 );
    }
    
    private static void usage() {
        System.err.println( "usage: java Which4J [-debug] <classname>" );
        System.err.println( "\n\tThe commandline version of Which4J will search the system");
        System.err.println( "\tclasspath defined in your environment for all occurences");
        System.err.println( "\tof the class.  Alternatively, you can use this class");
        System.err.println( "\tprogrammatically to search the current (or any) ClassLoader.");
        System.err.println( "\tSee the javadoc for more detail.");
        System.err.println( "\n\t");
        System.err.println( "\n\tNote: if the name of the jar file listed after \"found in:\"" );
        System.err.println( "\tdoesn't match the name of the jar listed next to \"url:\", then " );
        System.err.println( "\tthere is likely a \"Class-Path\" entry in the jar manifest " );
        System.err.println( "\tthat is causing the classloader to indirectly find the class." );
        System.exit( -1 );
    }
    
    /**
     * Iterate over the system classpath defined by "java.class.path" searching
     * for all occurrances of the given class name.
     * 
     * @param classname the fully qualified class name to search for
     */
    private static void findIt( String classname ) {
        
        try {
            // get the system classpath
            String classpath = System.getProperty( "java.class.path", "" );
            
            if( classpath.equals("") ) {
                System.err.println( "error: classpath is not set" );
            }
            
            if( debug ) {
                System.out.println( "classname: " + classname );
                System.out.println( "system classpath = " + classpath );
            }
            
            StringTokenizer st = 
                new StringTokenizer( classpath, File.pathSeparator );
                
            while( st.hasMoreTokens() ) {
                String token = st.nextToken();
                File classpathElement = new File( token );
                
                if( debug )
                    System.out.println( classpathElement.isDirectory() ? 
                                        "dir: " + token : 
                                        "jar: " + token );
                
                URL[] url = { classpathElement.toURL() };
                
                URLClassLoader cl = URLClassLoader.newInstance( url, null );
                
                String classnameAsResource = 
                    classname.replace( '.', '/' ) +
                    ".class";  
                     
                URL it = cl.findResource( classnameAsResource );               
                if( it != null ) {
                    System.out.println( "found in: " + token );
                    System.out.println( "     url: " + it.toString() );
                    System.out.println( "" );
                }
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
    
    /**
     * Search the current classloader for the given classname.
     * 
     * Equivalent to calling which( classname, Which4J.class.getClassLoader ).
     * 
     * @param classname the fully qualified name of the class to search for
     * @return the source location of the resource, or null if it wasn't found
     */
    public static String which( String classname ) {
        return which( classname, Which4J.class.getClassLoader() );
    }
    
    /**
     * Search the specified classloader for the given classname.
     * 
     * @param classname the fully qualified name of the class to search for
     * @param loader the classloader to search
     * @return the source location of the resource, or null if it wasn't found
     */
    public static String which( String classname, ClassLoader loader ) {

        String classnameAsResource = 
            classname.replace( '.', '/' ) +
            ".class";  
        
        URL it = loader.getResource( classnameAsResource );
        if( it != null ) {
            return it.toString();
        } else {
            return null;
        }
    }
}
