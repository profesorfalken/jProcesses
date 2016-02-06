/*
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

/**
 * Service implementation for Windows
 *
 * @author Javier Garcia Alonso
 */
public class WindowsProcessesService extends AbstractProcessesService{

    protected List<Map<String, String>> parseList(String rawData) {
        List<Map<String, String>> processesDataList = new ArrayList<Map<String, String>>();
        String[] dataStringLines = rawData.split("\\r?\\n");
        
        boolean newProcess = true;
        for (final String dataLine : dataStringLines) {            
            Map<String, String> processMap = null;
            if (dataLine.trim().length() > 0) {
                if (newProcess == true) {
                    processMap = new HashMap<String, String>();                   
                    newProcess = false;
                }
                if (processMap != null) {
                    String[] dataStringInfo = dataLine.split(":");
                    if (dataStringInfo.length == 2) {
                        processMap.put(dataStringInfo[0].trim(), dataStringInfo[1].trim());
                    }                
                }
            } else {
                if (newProcess == false) {
                    processesDataList.add(processMap);
                }
                newProcess = true;
            }                  
        }
        
        return processesDataList;
    }

    @Override
    protected String getProcessesData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //return WMI4Java.get().VBSEngine().getWMIObject(WMIClass.WIN32_PROCESS);
    }

    @Override
    protected int kill(int pid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
