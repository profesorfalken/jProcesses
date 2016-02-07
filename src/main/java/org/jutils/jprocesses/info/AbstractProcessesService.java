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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jutils.jprocesses.model.ProcessesInfo;

/**
 * Info related with processes
 * 
 * @author Javier Garcia Alonso
 */
public abstract class AbstractProcessesService implements ProcessesService{

    public List<ProcessesInfo> getList() {
        String rawData = getProcessesData();
        
        List<Map<String, String>> mapList =  parseList(rawData);
        
        return buildInfoFromMap(mapList);
    }
    
    public int killProcess(int pid) {
        return kill(pid);
    }
    
    protected abstract List<Map<String, String>> parseList(String rawData);
    
    protected abstract String getProcessesData();
    
    protected abstract int kill(int pid);
    
    private List<ProcessesInfo> buildInfoFromMap(List<Map<String, String>> mapList) {
        List<ProcessesInfo> infoList = new ArrayList<ProcessesInfo>();
        
        for (final Map<String, String> map: mapList) {
            ProcessesInfo info = new ProcessesInfo();
            info.setPid(map.get("pid"));
            info.setName(map.get("proc_name"));
            info.setTime(map.get("proc_time"));
            infoList.add(info);
        }
        
        return infoList;
    }
    
}
