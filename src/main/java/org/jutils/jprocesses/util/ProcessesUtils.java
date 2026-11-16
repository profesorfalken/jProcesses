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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility methods
 *
 * @author Javier Garcia Alonso
 */
public class ProcessesUtils {

    private static final Logger LOGGER = Logger.getLogger(ProcessesUtils.class.getName());

    private static final String CRLF = "\r\n";

    /**
     * Maximum time a system command is allowed to run before being forcibly
     * destroyed. It can be overridden with the
     * {@code jprocesses.commandTimeoutMillis} system property.
     */
    private static final long COMMAND_TIMEOUT_MILLIS = resolveCommandTimeout();

    private static final long READER_JOIN_MILLIS = 2000L;

    /**
     * Charset used to decode command output. It can be overridden with the
     * {@code jprocesses.charset} system property when the platform default
     * does not match the output of {@code ps} or WMI.
     */
    private static final Charset COMMAND_CHARSET = resolveCommandCharset();

    private static volatile String customDateFormat;
    private static volatile Locale customLocale;

    //Hide constructor
    private ProcessesUtils() {
    }

    /**
     * Executes a command and returns its standard output. Standard error is
     * merged into the output. The command is destroyed if it does not finish
     * within {@code jprocesses.commandTimeoutMillis} (30 seconds by default).
     *
     * @param command command and arguments
     * @return command output, or an empty string if it could not be executed
     */
    public static String executeCommand(String... command) {
        Process process = null;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true); // redirect error stream to output stream

            process = processBuilder.start();
            closeProcessInput(process);

            //Read in a separate thread while waiting, so that a full output
            //buffer can never deadlock the child process.
            StreamCollector collector = new StreamCollector(process.getInputStream());
            Thread reader = new Thread(collector, "jprocesses-command-reader");
            reader.setDaemon(true);
            reader.start();

            if (!process.waitFor(COMMAND_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                LOGGER.log(Level.WARNING, "Command timed out after {0} ms: {1}",
                        new Object[]{COMMAND_TIMEOUT_MILLIS, Arrays.toString(command)});
                process.destroyForcibly();
                process.waitFor();
            }

            reader.join(READER_JOIN_MILLIS);

            return collector.getOutput();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error executing command", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            if (process != null) {
                process.destroyForcibly();
            }
            LOGGER.log(Level.SEVERE, "Interrupted while executing command", ex);
        }

        return "";
    }

    /**
     * Executes a command and returns its exit code, discarding its output.
     *
     * @param command command and arguments
     * @return the command exit code, or {@code -1} if it could not be executed
     * or it timed out
     */
    public static int executeCommandAndGetCode(String... command) {
        Process process = null;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);

            process = processBuilder.start();
            closeProcessInput(process);

            StreamCollector collector = new StreamCollector(process.getInputStream());
            Thread reader = new Thread(collector, "jprocesses-command-reader");
            reader.setDaemon(true);
            reader.start();

            if (!process.waitFor(COMMAND_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                LOGGER.log(Level.WARNING, "Command timed out after {0} ms: {1}",
                        new Object[]{COMMAND_TIMEOUT_MILLIS, Arrays.toString(command)});
                process.destroyForcibly();
                process.waitFor();
                return -1;
            }

            reader.join(READER_JOIN_MILLIS);

            return process.exitValue();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return -1;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            if (process != null) {
                process.destroyForcibly();
            }
            LOGGER.log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    /**
     * Parse Window DateTime format to time format (hh:mm:ss).
     *
     * @param dateTime original datetime format
     * @see
     * <a href="https://msdn.microsoft.com/fr-fr/library/windows/desktop/aa387237(v=vs.85).aspx">
     * https://msdn.microsoft.com/fr-fr/library/windows/desktop/aa387237(v=vs.85).aspx</a>
     *
     * @return string with formatted time (hh:mm:ss)
     */
    public static String parseWindowsDateTimeToSimpleTime(String dateTime) {
        if (dateTime == null || dateTime.length() < 14) {
            return dateTime;
        }

        String hour = dateTime.substring(8, 10);
        String minutes = dateTime.substring(10, 12);
        String seconds = dateTime.substring(12, 14);

        return hour + ":" + minutes + ":" + seconds;
    }

    /**
     * Parse Window DateTime format to time format (MM/dd/yyyy hh:mm:ss)
     *
     * @param dateTime original datetime format
     * @see
     * <a href="https://msdn.microsoft.com/fr-fr/library/windows/desktop/aa387237(v=vs.85).aspx">
     * https://msdn.microsoft.com/fr-fr/library/windows/desktop/aa387237(v=vs.85).aspx</a>
     *
     * @return string with formatted time (mm/dd/yyyy HH:mm:ss)
     */
    public static String parseWindowsDateTimeToFullDate(String dateTime) {
        if (dateTime == null || dateTime.length() < 14) {
            return dateTime;
        }

        String year = dateTime.substring(0, 4);
        String month = dateTime.substring(4, 6);
        String day = dateTime.substring(6, 8);
        String hour = dateTime.substring(8, 10);
        String minutes = dateTime.substring(10, 12);
        String seconds = dateTime.substring(12, 14);

        return month + "/" + day + "/" + year + " " + hour + ":"
                + minutes + ":" + seconds;
    }

    /**
     * Parse Unix long date format(ex: Fri Jun 10 04:35:36 2016) to format
     * MM/dd/yyyy HH:mm:ss
     *
     * @param longFormatDate original datetime format
     *
     * @return string with formatted date and time (mm/dd/yyyy HH:mm:ss)
     * @throws ParseException if the date cannot be parsed with any known format
     */
    public static String parseUnixLongTimeToFullDate(String longFormatDate) throws ParseException {
        if (longFormatDate == null || longFormatDate.trim().isEmpty()) {
            throw new ParseException("Date is empty", 0);
        }

        DateFormat targetFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        List<String> formatsToTry = new ArrayList<String>();
        formatsToTry.addAll(Arrays.asList("MMM dd HH:mm:ss yyyy", "dd MMM HH:mm:ss yyyy"));
        List<Locale> localesToTry = new ArrayList<Locale>();
        localesToTry.addAll(Arrays.asList(Locale.getDefault(),
            Locale.getDefault(Locale.Category.FORMAT),
            Locale.ENGLISH)
        );
        if (getCustomDateFormat() != null) {
            formatsToTry.add(0, getCustomDateFormat());
        }
        if (getCustomLocale() != null) {
            localesToTry.add(0, getCustomLocale());
        }

        ParseException lastException = null;
        for (Locale locale : localesToTry) {
            for (String format : formatsToTry) {
                DateFormat originalFormat = new SimpleDateFormat(format, locale);
                try {
                    return targetFormat.format(originalFormat.parse(longFormatDate));
                } catch (ParseException ex) {
                    lastException = ex;
                }
            }
        }

        throw lastException;
    }

    public static String getCustomDateFormat() {
        return customDateFormat;
    }

    /**
     * Custom date format to be used when parsing date string in "ps" output<br>
     * NOTE: We assume 5 space separated fields for date, where we pass the last 4 to the parser, e.g.
     * for input text <code>søn 23 okt 08:30:00 2016</code> we would send <code>23 okt 08:30:00 2016</code>
     * to the parser, so a pattern <code>dd MMM HH:mm:ss yyyy</code> would work.
     * @param dateFormat the custom date format string to use
     */
    public static void setCustomDateFormat(String dateFormat) {
        customDateFormat = dateFormat;
    }

    public static Locale getCustomLocale() {
        return customLocale;
    }

    /**
     * Sets a custom locale if the defaults won't parse your ps output
     * @param customLocale the Locale object to use
     */
    public static void setCustomLocale(Locale customLocale) {
        ProcessesUtils.customLocale = customLocale;
    }

    private static void closeProcessInput(Process process) {
        try {
            //The commands used by this library never read from stdin
            process.getOutputStream().close();
        } catch (IOException ex) {
            LOGGER.log(Level.FINE, "Error closing command input", ex);
        }
    }

    private static long resolveCommandTimeout() {
        long timeout = Long.getLong("jprocesses.commandTimeoutMillis", 30000L);
        return timeout > 0 ? timeout : 30000L;
    }

    private static Charset resolveCommandCharset() {
        String charsetName = System.getProperty("jprocesses.charset");
        if (charsetName != null && !charsetName.trim().isEmpty()) {
            try {
                return Charset.forName(charsetName);
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Unsupported jprocesses.charset: " + charsetName, ex);
            }
        }
        return Charset.defaultCharset();
    }

    /**
     * Collects the output of a process in a background thread.
     */
    private static final class StreamCollector implements Runnable {

        private final BufferedReader reader;
        private final StringBuffer output = new StringBuffer();

        private StreamCollector(InputStream inputStream) {
            this.reader = new BufferedReader(new InputStreamReader(inputStream, COMMAND_CHARSET));
        }

        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty()) {
                        output.append(line).append(CRLF);
                    }
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Error reading command output", ex);
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Error closing reader", ex);
                }
            }
        }

        private String getOutput() {
            return output.toString();
        }
    }
}
