/*
 * Copyright 2016 Javier Garcia Alonso.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jutils.jprocesses.util;

/**
 * Detects used OS
 * 
 * @author Javier Garcia Alonso
 */
public class OSDetector {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    
    //Hide constructor
    private OSDetector() {        
    }

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix") || OS.matches( "mac.*os.*x" );
    }
    
    public static boolean isLinux() {
        return OS.contains("linux");
    }

    public static boolean isSolaris() {
        return OS.contains("sunos");
    }
}
