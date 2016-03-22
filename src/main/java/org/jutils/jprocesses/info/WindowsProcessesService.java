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

import com.profesorfalken.wmi4java.WMI4Java;
import com.profesorfalken.wmi4java.WMIClass;
import org.jutils.jprocesses.model.JProcessesResponse;
import org.jutils.jprocesses.model.ProcessInfo;
import org.jutils.jprocesses.util.ProcessesUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for Windows
 *
 * @author Javier Garcia Alonso
 */
class WindowsProcessesService extends AbstractProcessesService {
//TODO: This Windows implementation works but it is not optimized by lack of time.
//For example, the filter by name or the search by pid is done retrieving 
//all the processes searching in the returning list.
//Moreover, the information is dispersed and I had to get it from different sources (WMI classes, VBS scripts...)

    private final Map<String, String> userData = new HashMap<String, String>();
    private final Map<String, String> cpuData = new HashMap<String, String>();

    private static final String LINE_BREAK_REGEX = "\\r?\\n";

    private static final Map<String, String> keyMap;
    
    private static Map<String, String> processMap;

    static {
        Map<String, String> tmpMap = new HashMap<String, String>();
        tmpMap.put("Name", "proc_name");
        tmpMap.put("ProcessId", "pid");
        tmpMap.put("UserModeTime", "proc_time");
        tmpMap.put("Priority", "priority");
        tmpMap.put("VirtualSize", "virtual_memory");
        tmpMap.put("WorkingSetSize", "physical_memory");
        tmpMap.put("CommandLine", "command");
        tmpMap.put("CreationDate", "start_time");

        keyMap = Collections.unmodifiableMap(tmpMap);
    }

    @Override
    protected List<Map<String, String>> parseList(String rawData) {
        List<Map<String, String>> processesDataList = new ArrayList<Map<String, String>>();

        String[] dataStringLines = rawData.split(LINE_BREAK_REGEX);

        for (final String dataLine : dataStringLines) {
            if (dataLine.trim().length() > 0) {
                processLine(dataLine, processesDataList);
            }
        }

        if (nameFilter != null) {
            filterByName(processesDataList);
        }

        return processesDataList;
    }

    private void processLine(String dataLine, List<Map<String, String>> processesDataList) {
        if (dataLine.startsWith("Caption")) {
            if (processMap != null && processMap.size() > 0) {
                processesDataList.add(processMap);
            }
            processMap = new HashMap<String, String>();
        }

        if (processMap != null) {
            String[] dataStringInfo = dataLine.split(":", 2);
            if (dataStringInfo.length == 2) {
                processMap.put(normalizeKey(dataStringInfo[0].trim()),
                        normalizeValue(dataStringInfo[0].trim(), dataStringInfo[1].trim()));

                if ("ProcessId".equals(dataStringInfo[0].trim())) {
                    processMap.put("user", userData.get(dataStringInfo[1].trim()));
                    processMap.put("cpu_usage", cpuData.get(dataStringInfo[1].trim()));
                }
            }
        }
    }

    @Override
    protected String getProcessesData(String name) {
        fillExtraProcessData();

        if (name != null) {
            this.nameFilter = name;
        }

        return WMI4Java.get().VBSEngine().getRawWMIObjectOutput(WMIClass.WIN32_PROCESS);
    }

    @Override
    protected JProcessesResponse kill(int pid) {
        JProcessesResponse response = new JProcessesResponse();
        if (ProcessesUtils.executeCommandAndGetCode("taskkill", "/PID", String.valueOf(pid), "/F") == 0) {
            response.setSuccess(true);
        }

        return response;
    }

    @Override
    protected JProcessesResponse killGracefully(int pid) {
        JProcessesResponse response = new JProcessesResponse();
        if (ProcessesUtils.executeCommandAndGetCode("taskkill", "/PID", String.valueOf(pid)) == 0) {
            response.setSuccess(true);
        }

        return response;
    }

    private static String normalizeKey(String origKey) {
        return keyMap.get(origKey);
    }

    private static String normalizeValue(String origKey, String origValue) {
        if ("UserModeTime".equals(origKey)) {
            //100 nano to second - https://msdn.microsoft.com/en-us/library/windows/desktop/aa394372(v=vs.85).aspx
            long seconds = Long.parseLong(origValue) * 100 / 1000000 / 1000;
            return nomalizeTime(seconds);
        }
        if ("VirtualSize".equals(origKey) || "WorkingSetSize".equals(origKey)) {
            if (!(origValue.isEmpty())) {
                return String.valueOf(Long.parseLong(origValue) / 1024);
            }
        }
        if ("CreationDate".equals(origKey)) {
            if (!origValue.isEmpty()) {
                String hour = origValue.substring(8, 10);
                String minutes = origValue.substring(10, 12);
                String seconds = origValue.substring(12, 14);

                return hour + ":" + minutes + ":" + seconds;
            }
        }

        return origValue;
    }

    private static String nomalizeTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void fillExtraProcessData() {
        String perfData = WMI4Java.get().VBSEngine().getRawWMIObjectOutput(WMIClass.WIN32_PERFFORMATTEDDATA_PERFPROC_PROCESS);

        String[] dataStringLines = perfData.split(LINE_BREAK_REGEX);
        String pid = null;
        String cpuUsage = null;
        for (final String dataLine : dataStringLines) {
            
            if (dataLine.trim().length() > 0) {
                if (dataLine.startsWith("Caption")) {
                    if (pid != null && cpuUsage != null) {
                        cpuData.put(pid, cpuUsage);
                        pid = null;
                        cpuUsage = null;
                    }
                    continue;
                }
                
                if (pid == null) {
                    pid = checkAndGetDataInLine("IDProcess", dataLine);
                }
                if (cpuUsage == null) {
                    cpuUsage = checkAndGetDataInLine("PercentProcessorTime", dataLine);
                }
            }
        }

        String processesData = VBScriptHelper.getProcessesOwner();

        if (processesData != null) {
            dataStringLines = processesData.split(LINE_BREAK_REGEX);
            for (final String dataLine : dataStringLines) {
                String[] dataStringInfo = dataLine.split(":", 2);
                if (dataStringInfo.length == 2) {
                    userData.put(dataStringInfo[0].trim(), dataStringInfo[1].trim());
                }
            }
        }
    }

    private static String checkAndGetDataInLine(String dataName, String dataLine) {
        if (dataLine.startsWith(dataName)) {
            String[] dataStringInfo = dataLine.split(":");
            if (dataStringInfo.length == 2) {
                return dataStringInfo[1].trim();
            }
        }
        return null;
    }

    @Override
    public JProcessesResponse changePriority(int pid, int priority) {
        JProcessesResponse response = new JProcessesResponse();
        String message = VBScriptHelper.changePriority(pid, priority);
        if (message.isEmpty()) {
            response.setSuccess(true);
        } else {
            response.setMessage(message);
        }
        return response;
    }

    @Override
    public ProcessInfo getProcess(int pid) {
        List<Map<String, String>> allProcesses = parseList(getProcessesData(null));

        for (final Map<String, String> process : allProcesses) {
            if (String.valueOf(pid).equals(process.get("pid"))) {
                ProcessInfo info = new ProcessInfo();
                info.setPid(process.get("pid"));
                info.setName(process.get("proc_name"));
                info.setTime(process.get("proc_time"));
                info.setCommand(process.get("command"));
                info.setCpuUsage(process.get("cpu_usage"));
                info.setPhysicalMemory(process.get("physical_memory"));
                info.setStartTime(process.get("start_time"));
                info.setUser(process.get("user"));
                info.setVirtualMemory(process.get("virtual_memory"));
                info.setPriority(process.get("priority"));

                return info;
            }
        }
        return null;
    }
}
