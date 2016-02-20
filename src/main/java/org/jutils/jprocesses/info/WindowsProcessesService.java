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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jutils.jprocesses.model.JProcessesResponse;
import org.jutils.jprocesses.model.ProcessInfo;
import org.jutils.jprocesses.util.ProcessesUtils;
import org.jutils.jprocesses.util.VBScriptUtil;

/**
 * Service implementation for Windows
 *
 * @author Javier Garcia Alonso
 */
class WindowsProcessesService extends AbstractProcessesService {

    private final Map<String, String> userData = new HashMap<String, String>();
    private final Map<String, String> cpuData = new HashMap<String, String>();
    
    private String nameFilter = null;

    protected List<Map<String, String>> parseList(String rawData) {
        List<Map<String, String>> processesDataList = new ArrayList<Map<String, String>>();

        String[] dataStringLines = rawData.split("\\r?\\n");

        Map<String, String> processMap = null;
        for (final String dataLine : dataStringLines) {
            if (dataLine.trim().length() > 0) {
                if (dataLine.startsWith("Caption")) {
                    if (processMap != null && processMap.size() > 0) {
                        processesDataList.add(processMap);
                    }
                    processMap = new HashMap<String, String>();
                }

                if (processMap != null) {
                    String[] dataStringInfo = dataLine.split(":");
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
        }
        
        if (nameFilter != null) {
            List<Map<String, String>> processesToRemove = new ArrayList<Map<String, String>>();
            for (final Map<String, String> process : processesDataList) {
                if (!nameFilter.equals(process.get("proc_name"))) {
                    processesToRemove.add(process);
                }
            }
            processesDataList.removeAll(processesToRemove);
        }

        return processesDataList;
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

    private String normalizeKey(String origKey) {
        if ("Name".equals(origKey)) {
            return "proc_name";
        } else if ("ProcessId".equals(origKey)) {
            return "pid";
        } else if ("UserModeTime".equals(origKey)) {
            return "proc_time";
        } else if ("Priority".equals(origKey)) {
            return "priority";
        } else if ("VirtualSize".equals(origKey)) {
            return "virtual_memory";
        } else if ("WorkingSetSize".equals(origKey)) {
            return "physical_memory";
        } else if ("CommandLine".equals(origKey)) {
            return "command";
        } else if ("CreationDate".equals(origKey)) {
            return "start_time";
        }

        return origKey;
    }

    private String normalizeValue(String origKey, String origValue) {
        if ("UserModeTime".equals(origKey)) {
            long longOrigValue = Long.valueOf(origValue);
            //100 nano to second - https://msdn.microsoft.com/en-us/library/windows/desktop/aa394372(v=vs.85).aspx
            long seconds = longOrigValue * 100 / 1000000 / 1000;
            return nomalizeTime(seconds);
        }
        if ("VirtualSize".equals(origKey) || "WorkingSetSize".equals(origKey)) {
            if (!(origValue.isEmpty())) {
                return (String.valueOf(Integer.valueOf(origValue) / 1024));
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

    private String nomalizeTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    private void fillExtraProcessData() {
        String perfData = WMI4Java.get().VBSEngine().getRawWMIObjectOutput(WMIClass.WIN32_PERFFORMATTEDDATA_PERFPROC_PROCESS);

        String[] dataStringLines = perfData.split("\\r?\\n");
        String pid = null;
        String cpuUsage = null;
        for (final String dataLine : dataStringLines) {
            if (dataLine.trim().length() > 0) {
                if (dataLine.startsWith("Caption")) {
                    if (pid != null && cpuUsage != null) {
                        cpuData.put(pid, cpuUsage);
                    }
                    continue;
                }

                if (dataLine.startsWith("IDProcess")) {
                    String[] dataStringInfo = dataLine.split(":");
                    if (dataStringInfo.length == 2) {
                        pid = dataStringInfo[1].trim();
                    }
                    continue;
                }

                if (dataLine.startsWith("PercentProcessorTime")) {
                    String[] dataStringInfo = dataLine.split(":");
                    if (dataStringInfo.length == 2) {
                        cpuUsage = dataStringInfo[1].trim();
                    }
                }
            }
        }

        String processesData = VBScriptUtil.getProcessesOwner();

        if (processesData != null) {
            dataStringLines = processesData.split("\\r?\\n");
            for (final String dataLine : dataStringLines) {
                String[] dataStringInfo = dataLine.split(":");
                if (dataStringInfo.length == 2) {
                    userData.put(dataStringInfo[0].trim(), dataStringInfo[1].trim());
                }
            }
        }
    }

    public JProcessesResponse changePriority(int pid, int priority) {
        JProcessesResponse response = new JProcessesResponse();
        String message = VBScriptUtil.changePriority(pid, priority);
        if (message.isEmpty()) {
            response.setSuccess(true);
        } else {
            response.setMessage(message);
        }
        return response;
    }

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
