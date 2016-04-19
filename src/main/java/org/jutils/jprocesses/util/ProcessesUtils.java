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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility methods
 *
 * @author Javier Garcia Alonso
 */
public class ProcessesUtils {

    private static final String CRLF = "\r\n";

    //Hide constructor
    private ProcessesUtils() {
    }

    public static String executeCommand(String... command) {
        String commandOutput = null;

        try {
            Process process = Runtime.getRuntime().exec(command);
            commandOutput = readData(process);
        } catch (IOException ex) {
            Logger.getLogger(ProcessesUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return commandOutput;
    }

    private static String readData(Process process) {
        StringBuilder commandOutput = new StringBuilder();
        BufferedReader processOutput = null;
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessesUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            if (process.exitValue() == 0) {
                processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            } else {
                processOutput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            }

            String line;
            while ((line = processOutput.readLine()) != null) {
                if (!line.isEmpty()) {
                    commandOutput.append(line).append(CRLF);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ProcessesUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (processOutput != null) {
                    processOutput.close();
                }
            } catch (IOException ioe) {
                Logger.getLogger(ProcessesUtils.class.getName()).log(Level.SEVERE, null, ioe);
            }
        }

        return commandOutput.toString();
    }

    public static int executeCommandAndGetCode(String... command) {
        Process process;

        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (IOException ex) {
            Logger.getLogger(ProcessesUtils.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessesUtils.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        return process.exitValue();
    }

    /**
     * Parse Window DateTime format to time format (hh:mm:ss).
     *
     * @param dateTime original datetime format
     * @see <a href="https://msdn.microsoft.com/fr-fr/library/windows/desktop/aa387237(v=vs.85).aspx">
     * https://msdn.microsoft.com/fr-fr/library/windows/desktop/aa387237(v=vs.85).aspx</a>
     * 
     * @return string with formatted time (hh:mm:ss)
     */
    public static String parseWindowsDateTimeToSimpleTime(String dateTime) {
        String returnedDate = dateTime;
        if (dateTime != null && !dateTime.isEmpty()) {
            String hour = dateTime.substring(8, 10);
            String minutes = dateTime.substring(10, 12);
            String seconds = dateTime.substring(12, 14);

            returnedDate = hour + ":" + minutes + ":" + seconds;
        }
        return returnedDate;
    }

    /**
     * Parse Window DateTime format to time format (MM/dd/yyyy hh:mm:ss)
     *
     * @param dateTime original datetime format
     * @see <a href="https://msdn.microsoft.com/fr-fr/library/windows/desktop/aa387237(v=vs.85).aspx">
     * https://msdn.microsoft.com/fr-fr/library/windows/desktop/aa387237(v=vs.85).aspx</a>
     * 
     * @return string with formatted time (hh:mm:ss)
     */
    public static String parseWindowsDateTimeToFullDate(String dateTime) {
        String returnedDate = dateTime;
        if (dateTime != null && !dateTime.isEmpty()) {
            String year = dateTime.substring(0, 4);
            String month = dateTime.substring(4, 6);
            String day = dateTime.substring(6, 8);
            String hour = dateTime.substring(8, 10);
            String minutes = dateTime.substring(10, 12);
            String seconds = dateTime.substring(12, 14);

            returnedDate = month + "/" + day + "/" + year + " " + hour + ":"
                    + minutes + ":" + seconds;
        }
        return returnedDate;
    }
}
