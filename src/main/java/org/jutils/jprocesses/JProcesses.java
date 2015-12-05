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
package org.jutils.jprocesses;

import java.util.List;
import org.jutils.jprocesses.info.ProcessesFactory;
import org.jutils.jprocesses.info.ProcessesService;
import org.jutils.jprocesses.model.ProcessesInfo;

/**
 * Static class that gives access to Processes details.
 * 
 * @author Javier Garcia Alonso
 */
public class JProcesses {
    
    private JProcesses() {
    }
    
    public static List<ProcessesInfo> getProcessList() {                
        ProcessesService srv = ProcessesFactory.getService();
        
        return srv.getList();
    }
    
    public static boolean killProcess(int pid) {
         ProcessesService srv = ProcessesFactory.getService();
         
         return srv.killProcess(pid) == 0;
    }
}
