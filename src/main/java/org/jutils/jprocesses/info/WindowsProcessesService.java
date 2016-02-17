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
import org.jutils.jprocesses.model.ProcessInfo;

/**
 * Service implementation for Windows
 *
 * @author Javier Garcia Alonso
 */
class WindowsProcessesService extends AbstractProcessesService {

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
                    }
                }
            }
        }

        return processesDataList;
    }

    @Override
    protected String getProcessesData(String name) {
        return WMI4Java.get().VBSEngine().getRawWMIObjectOutput(WMIClass.WIN32_PROCESS);
    }

    @Override
    protected int kill(int pid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private String normalizeKey(String origKey) {
        if ("Name".equals(origKey)) {
            return "proc_name";
        } else if ("ProcessId".equals(origKey)) {
            return "pid";
        } else if ("UserModeTime".equals(origKey)) {
            return "proc_time";
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
        
        return origValue;
    }

    private String nomalizeTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;       

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public boolean changePriority(int pid, int priority) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ProcessInfo getProcess(int pid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
