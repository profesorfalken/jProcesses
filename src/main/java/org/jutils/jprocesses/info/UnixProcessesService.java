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
package org.jutils.jprocesses.info;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jutils.jprocesses.model.JProcessesResponse;
import org.jutils.jprocesses.model.ProcessInfo;
import org.jutils.jprocesses.util.ProcessesUtils;

/**
 * Service implementation for Unix/BSD systems
 *
 * @author Javier Garcia Alonso
 */
class UnixProcessesService extends AbstractProcessesService {

    private static final Logger LOGGER = Logger.getLogger(UnixProcessesService.class.getName());

    //Use BSD sytle to get data in order to be compatible with Mac Systems(thanks to jkuharev for this tip)
    private static final String PS_COLUMNS = "pid,ruser,vsize,rss,%cpu,lstart,cputime,nice,ucomm";
    private static final String PS_FULL_COMMAND = "pid,command";

    private static final int PS_COLUMNS_SIZE = PS_COLUMNS.split(",").length;
    private static final int PS_FULL_COMMAND_SIZE = PS_FULL_COMMAND.split(",").length;
    //"lstart" is returned as five space separated fields (weekday included);
    //the command name is optional because it can be empty on some systems.
    private static final int PS_MIN_ELEMENTS = PS_COLUMNS_SIZE + 3;

    @Override
    protected List<Map<String, String>> parseList(String rawData) {
        List<Map<String, String>> processesDataList = new ArrayList<Map<String, String>>();

        if (rawData == null || rawData.isEmpty()) {
            return processesDataList;
        }

        String[] dataStringLines = rawData.split("\\r?\\n");

        for (final String dataLine : dataStringLines) {
            String line = dataLine.trim();

            //Skip the header and any unexpected line (warnings, error messages...)
            if (line.isEmpty() || line.startsWith("PID")) {
                continue;
            }

            String[] elements = line.split("\\s+");

            if (elements.length < PS_MIN_ELEMENTS) {
                LOGGER.log(Level.FINE, "Skipping unexpected ps output line: {0}", line);
                continue;
            }

            // LinkedHashMap keeps the insertion order, thus easier to debug
            Map<String, String> element = new LinkedHashMap<String, String>();
            int index = 0;
            element.put("pid", elements[index++]);
            element.put("user", elements[index++]);
            element.put("virtual_memory", elements[index++]);
            element.put("physical_memory", elements[index++]);
            element.put("cpu_usage", elements[index++]);
            index++; // Skip weekday
            String month = elements[index++];
            String day = elements[index++];
            String clock = elements[index++];
            String year = elements[index++];
            String longDate = month + " " + day + " " + clock + " " + year;
            element.put("start_time", clock);

            try {
                element.put("start_datetime",
                        ProcessesUtils.parseUnixLongTimeToFullDate(longDate));
            } catch (ParseException e) {
                //Do not invent a fake date: just leave the field out
                LOGGER.log(Level.WARNING, "Failed formatting date from ps: " + longDate, e);
            }

            element.put("proc_time", elements[index++]);
            element.put("priority", elements[index++]);

            StringBuilder procName = new StringBuilder();
            if (index < elements.length) {
                procName.append(elements[index++]);
                while (index < elements.length) {
                    procName.append(' ').append(elements[index++]);
                }
            }
            element.put("proc_name", procName.toString());
            // first init full command by content of proc_name
            element.put("command", procName.toString());

            processesDataList.add(element);
        }

        if (!processesDataList.isEmpty()) {
            loadFullCommandData(processesDataList);
        }

        return processesDataList;
    }

    @Override
    protected String getProcessesData(String name, boolean fastMode) {
        //Name filtering is done after parsing so that every Unix/BSD flavor
        //uses the same (exact) matching semantics.
        return executeCommand("ps", "-e", "-o", PS_COLUMNS);
    }

    @Override
    public List<ProcessInfo> getList(String name, boolean fastMode) {
        List<ProcessInfo> processes = super.getList(name, fastMode);

        if (name == null) {
            return processes;
        }

        List<ProcessInfo> filtered = new ArrayList<ProcessInfo>();
        for (final ProcessInfo process : processes) {
            if (name.equals(process.getName())) {
                filtered.add(process);
            }
        }
        return filtered;
    }

    @Override
    protected JProcessesResponse kill(int pid) {
        JProcessesResponse response = new JProcessesResponse();
        int exitCode = executeCommandAndGetCode("kill", "-9", String.valueOf(pid));
        if (exitCode == 0) {
            response.setSuccess(true);
        } else {
            response.setMessage("kill exited with code " + exitCode);
        }
        return response;
    }

    @Override
    protected JProcessesResponse killGracefully(int pid) {
        JProcessesResponse response = new JProcessesResponse();
        int exitCode = executeCommandAndGetCode("kill", "-15", String.valueOf(pid));
        if (exitCode == 0) {
            response.setSuccess(true);
        } else {
            response.setMessage("kill exited with code " + exitCode);
        }
        return response;
    }

    public JProcessesResponse changePriority(int pid, int priority) {
        validatePid(pid);
        JProcessesResponse response = new JProcessesResponse();
        int exitCode = executeCommandAndGetCode("renice", String.valueOf(priority),
                "-p", String.valueOf(pid));
        if (exitCode == 0) {
            response.setSuccess(true);
        } else {
            response.setMessage("renice exited with code " + exitCode);
        }
        return response;
    }

    public ProcessInfo getProcess(int pid) {
        return getProcess(pid, false);
    }

    public ProcessInfo getProcess(int pid, boolean fastMode) {
        validatePid(pid);
        List<Map<String, String>> processList
                = parseList(executeCommand("ps",
                        "-o", PS_COLUMNS, "-p", String.valueOf(pid)));

        if (!processList.isEmpty()) {
            return buildInfo(processList.get(0));
        }
        return null;
    }

    private void loadFullCommandData(List<Map<String, String>> processesDataList) {
        Map<String, String> commandsMap = new HashMap<String, String>();
        String data = executeCommand("ps",
                "-e", "-o", PS_FULL_COMMAND);

        if (data == null || data.isEmpty()) {
            return;
        }

        String[] dataStringLines = data.split("\\r?\\n");

        for (final String dataLine : dataStringLines) {
            String line = dataLine.trim();
            if (!line.isEmpty() && !line.startsWith("PID")) {
                String[] elements = line.split("\\s+", PS_FULL_COMMAND_SIZE);
                if (elements.length == PS_FULL_COMMAND_SIZE) {
                    commandsMap.put(elements[0], elements[1]);
                }
            }
        }

        for (final Map<String, String> process : processesDataList) {
            String pid = process.get("pid");
            if (commandsMap.containsKey(pid)) {
                process.put("command", commandsMap.get(pid));
            }
        }
    }

    /**
     * Executes a system command. Exposed as a protected method so that the
     * parser can be tested without depending on the host operating system.
     *
     * @param command command and arguments
     * @return command output
     */
    protected String executeCommand(String... command) {
        return ProcessesUtils.executeCommand(command);
    }

    /**
     * Executes a system command and returns its exit code.
     *
     * @param command command and arguments
     * @return command exit code
     */
    protected int executeCommandAndGetCode(String... command) {
        return ProcessesUtils.executeCommandAndGetCode(command);
    }
}
