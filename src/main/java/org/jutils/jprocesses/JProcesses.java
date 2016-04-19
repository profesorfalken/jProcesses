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
package org.jutils.jprocesses;

import org.jutils.jprocesses.info.ProcessesFactory;
import org.jutils.jprocesses.info.ProcessesService;
import org.jutils.jprocesses.model.JProcessesResponse;
import org.jutils.jprocesses.model.ProcessInfo;

import java.util.List;

/**
 * Static class that gives access to Processes details.
 * 
 * @author Javier Garcia Alonso
 */
public class JProcesses {
    
    //This mode retrieves less information but faster
    public static boolean fastMode = false;
    
    private JProcesses() {
    }    
    
    public static List<ProcessInfo> getProcessList() {                
        ProcessesService srv = ProcessesFactory.getService();
        
        return srv.getList(fastMode);
    }
    
    public static List<ProcessInfo> getProcessList(String name) {                
        ProcessesService srv = ProcessesFactory.getService();
        
        return srv.getList(fastMode);
    }
    
    public static ProcessInfo getProcess(int pid) {                
        ProcessesService srv = ProcessesFactory.getService();
        
        return srv.getProcess(pid);
    }
    
    public static JProcessesResponse killProcess(int pid) {
         ProcessesService srv = ProcessesFactory.getService();
         
         return srv.killProcess(pid);
    }

    public static JProcessesResponse killProcessGracefully (int pid) {
        ProcessesService srv = ProcessesFactory.getService();

        return srv.killProcessGracefully(pid);
    }
    
    public static JProcessesResponse changePriority(int pid, int newPriority) {
         ProcessesService srv = ProcessesFactory.getService();
        
        return srv.changePriority(pid, newPriority);
    }
}
