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

import org.jutils.jprocesses.model.JProcessesResponse;
import org.jutils.jprocesses.model.ProcessInfo;
import org.jutils.jprocesses.util.ProcessesUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Service implementation for Windows
 *
 * @author Javier Garcia Alonso
 */
class WindowsProcessesService extends AbstractProcessesService {

    private static final Logger LOGGER = Logger.getLogger(WindowsProcessesService.class.getName());

    /**
     * Only names made of these characters can safely be embedded in a WQL
     * query. Underscores are excluded on purpose because they are LIKE
     * wildcards. Everything else is filtered in Java after parsing.
     */
    private static final Pattern SAFE_WQL_NAME = Pattern.compile("[A-Za-z0-9 .-]+");

    private final Map<String, String> userData = new HashMap<String, String>();
    private final Map<String, String> cpuData = new HashMap<String, String>();

    private static final String LINE_BREAK_REGEX = "\\r?\\n";

    private static final Map<String, String> keyMap;

    private static final String NAME_PROPNAME = "Name";
    private static final String PROCESSID_PROPNAME = "ProcessId";
    private static final String USERMODETIME_PROPNAME = "UserModeTime";
    private static final String PRIORITY_PROPNAME = "Priority";
    private static final String VIRTUALSIZE_PROPNAME = "VirtualSize";
    private static final String WORKINGSETSIZE_PROPNAME = "WorkingSetSize";
    private static final String COMMANDLINE_PROPNAME = "CommandLine";
    private static final String CREATIONDATE_PROPNAME = "CreationDate";
    private static final String CAPTION_PROPNAME = "Caption";

    private final WmiDataProvider wmi;

    static {
        Map<String, String> tmpMap = new HashMap<String, String>();
        tmpMap.put(NAME_PROPNAME, "proc_name");
        tmpMap.put(PROCESSID_PROPNAME, "pid");
        tmpMap.put(USERMODETIME_PROPNAME, "proc_time");
        tmpMap.put(PRIORITY_PROPNAME, "priority");
        tmpMap.put(VIRTUALSIZE_PROPNAME, "virtual_memory");
        tmpMap.put(WORKINGSETSIZE_PROPNAME, "physical_memory");
        tmpMap.put(COMMANDLINE_PROPNAME, "command");
        tmpMap.put(CREATIONDATE_PROPNAME, "start_time");

        keyMap = Collections.unmodifiableMap(tmpMap);
    }

    public WindowsProcessesService() {
        this(new Wmi4JavaDataProvider(null));
    }

    WindowsProcessesService(WmiDataProvider wmi) {
        this.wmi = wmi;
    }

    @Override
    protected List<Map<String, String>> parseList(String rawData) {
        List<Map<String, String>> processesDataList = new ArrayList<Map<String, String>>();

        if (rawData == null || rawData.isEmpty()) {
            return processesDataList;
        }

        String[] dataStringLines = rawData.split(LINE_BREAK_REGEX);
        Map<String, String> processMap = null;

        for (final String dataLine : dataStringLines) {
            String line = dataLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            int separator = line.indexOf(':');
            if (separator < 0) {
                LOGGER.log(Level.FINE, "Skipping unexpected WMI output line: {0}", line);
                continue;
            }

            String key = line.substring(0, separator).trim();
            String value = line.substring(separator + 1).trim();

            if (CAPTION_PROPNAME.equals(key)) {
                processMap = new HashMap<String, String>();
                processesDataList.add(processMap);
            }

            if (processMap == null) {
                continue;
            }

            String normalizedKey = normalizeKey(key);
            if (normalizedKey != null) {
                processMap.put(normalizedKey, normalizeValue(key, value));
            }

            if (PROCESSID_PROPNAME.equals(key)) {
                processMap.put("user", userData.get(value));
                processMap.put("cpu_usage", cpuData.get(value));
            }

            if (CREATIONDATE_PROPNAME.equals(key)) {
                processMap.put("start_datetime",
                        ProcessesUtils.parseWindowsDateTimeToFullDate(value));
            }
        }

        return processesDataList;
    }

    @Override
    protected String getProcessesData(String name, boolean fastMode) {
        if (name != null && SAFE_WQL_NAME.matcher(name).matches()) {
            return retrieveProcessData(
                    Arrays.asList(CAPTION_PROPNAME, PROCESSID_PROPNAME, NAME_PROPNAME,
                            USERMODETIME_PROPNAME, COMMANDLINE_PROPNAME,
                            WORKINGSETSIZE_PROPNAME, CREATIONDATE_PROPNAME,
                            VIRTUALSIZE_PROPNAME, PRIORITY_PROPNAME),
                    Collections.singletonList("Name like '%" + name + "%'"),
                    fastMode);
        }

        return retrieveProcessData(null, null, fastMode);
    }

    private String retrieveProcessData(List<String> properties, List<String> filters,
            boolean fastMode) {
        //Never reuse user/CPU data from a previous call (PIDs can be reused)
        cpuData.clear();
        userData.clear();

        if (!fastMode) {
            fillExtraProcessData();
        }

        return wmi.getRawProcessOutput(properties, filters);
    }

    @Override
    public List<ProcessInfo> getList(String name, boolean fastMode) {
        List<ProcessInfo> processes = super.getList(name, fastMode);

        if (name == null) {
            return processes;
        }

        List<ProcessInfo> filtered = new ArrayList<ProcessInfo>();
        for (final ProcessInfo process : processes) {
            if (name.equalsIgnoreCase(process.getName())) {
                filtered.add(process);
            }
        }
        return filtered;
    }

    @Override
    protected JProcessesResponse kill(int pid) {
        JProcessesResponse response = new JProcessesResponse();
        int exitCode = executeCommandAndGetCode("taskkill", "/PID", String.valueOf(pid), "/F");
        if (exitCode == 0) {
            response.setSuccess(true);
        } else {
            response.setMessage("taskkill exited with code " + exitCode);
        }

        return response;
    }

    @Override
    protected JProcessesResponse killGracefully(int pid) {
        JProcessesResponse response = new JProcessesResponse();
        int exitCode = executeCommandAndGetCode("taskkill", "/PID", String.valueOf(pid));
        if (exitCode == 0) {
            response.setSuccess(true);
        } else {
            response.setMessage("taskkill exited with code " + exitCode);
        }

        return response;
    }

    private static String normalizeKey(String origKey) {
        return keyMap.get(origKey);
    }

    private static String normalizeValue(String origKey, String origValue) {
        if (USERMODETIME_PROPNAME.equals(origKey)) {
            try {
                //100 nano to second - https://msdn.microsoft.com/en-us/library/windows/desktop/aa394372(v=vs.85).aspx
                long seconds = Long.parseLong(origValue) * 100 / 1000000 / 1000;
                return normalizeTime(seconds);
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.FINE, "Invalid UserModeTime value: " + origValue, ex);
                return origValue;
            }
        }
        if (VIRTUALSIZE_PROPNAME.equals(origKey) || WORKINGSETSIZE_PROPNAME.equals(origKey)) {
            if (!origValue.isEmpty()) {
                try {
                    return String.valueOf(Long.parseLong(origValue) / 1024);
                } catch (NumberFormatException ex) {
                    LOGGER.log(Level.FINE, "Invalid memory value: " + origValue, ex);
                }
            }
        }
        if (CREATIONDATE_PROPNAME.equals(origKey)) {
            return ProcessesUtils.parseWindowsDateTimeToSimpleTime(origValue);
        }

        return origValue;
    }

    private static String normalizeTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        return String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    private void fillExtraProcessData() {
        String perfData = null;
        try {
            perfData = getPerformanceData();
        } catch (RuntimeException ex) {
            LOGGER.log(Level.WARNING, "Could not retrieve process performance data", ex);
        }

        if (perfData != null && !perfData.isEmpty()) {
            String[] dataStringLines = perfData.split(LINE_BREAK_REGEX);
            String pid = null;
            String cpuUsage = null;

            for (final String dataLine : dataStringLines) {
                String line = dataLine.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith(CAPTION_PROPNAME)) {
                    putCpuData(pid, cpuUsage);
                    pid = null;
                    cpuUsage = null;
                    continue;
                }

                if (pid == null) {
                    pid = checkAndGetDataInLine("IDProcess", line);
                }
                if (cpuUsage == null) {
                    cpuUsage = checkAndGetDataInLine("PercentProcessorTime", line);
                }
            }

            //Flush the last block, which has no following Caption line
            putCpuData(pid, cpuUsage);
        }

        String processesData = null;
        try {
            processesData = getProcessOwners();
        } catch (RuntimeException ex) {
            LOGGER.log(Level.WARNING, "Could not retrieve process owners", ex);
        }

        if (processesData != null) {
            String[] dataStringLines = processesData.split(LINE_BREAK_REGEX);
            for (final String dataLine : dataStringLines) {
                String line = dataLine.trim();
                int separator = line.indexOf(':');
                if (separator > 0 && separator < line.length() - 1) {
                    userData.put(line.substring(0, separator).trim(),
                            line.substring(separator + 1).trim());
                }
            }
        }
    }

    private void putCpuData(String pid, String cpuUsage) {
        if (pid != null && cpuUsage != null) {
            cpuData.put(pid, cpuUsage);
        }
    }

    /**
     * Retrieves the performance data used to calculate CPU usage. Overridable
     * for testing purposes.
     *
     * @return raw performance data
     */
    protected String getPerformanceData() {
        return wmi.getRawPerformanceOutput();
    }

    /**
     * Retrieves the PID/owner mapping. Overridable for testing purposes.
     *
     * @return raw owner data, one {@code pid:user} entry per line
     */
    protected String getProcessOwners() {
        return VBScriptHelper.getProcessesOwner();
    }

    private static String checkAndGetDataInLine(String dataName, String dataLine) {
        if (dataLine.startsWith(dataName)) {
            int separator = dataLine.indexOf(':');
            if (separator >= 0) {
                return dataLine.substring(separator + 1).trim();
            }
        }
        return null;
    }

    public JProcessesResponse changePriority(int pid, int priority) {
        validatePid(pid);
        JProcessesResponse response = new JProcessesResponse();
        String message = VBScriptHelper.changePriority(pid, priority);
        if (message == null) {
            response.setMessage("Could not execute the WMI priority script");
        } else if (message.isEmpty()) {
            response.setSuccess(true);
        } else {
            response.setMessage(message);
        }
        return response;
    }

    public ProcessInfo getProcess(int pid) {
        return getProcess(pid, false);
    }

    public ProcessInfo getProcess(int pid, boolean fastMode) {
        validatePid(pid);
        List<Map<String, String>> allProcesses = parseList(
                retrieveProcessData(null,
                        Collections.singletonList("ProcessId = " + pid), fastMode));

        for (final Map<String, String> process : allProcesses) {
            if (String.valueOf(pid).equals(process.get("pid"))) {
                return buildInfo(process);
            }
        }
        return null;
    }

    /**
     * Executes a system command and returns its exit code. Exposed as a
     * protected method so that subclasses can replace it in tests.
     *
     * @param command command and arguments
     * @return command exit code
     */
    protected int executeCommandAndGetCode(String... command) {
        return ProcessesUtils.executeCommandAndGetCode(command);
    }
}
