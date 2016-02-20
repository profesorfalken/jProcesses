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

import java.util.List;
import org.jutils.jprocesses.model.JProcessesResponse;
import org.jutils.jprocesses.model.ProcessInfo;

/**
 * Interface for service retrieving processes information
 * 
 * @author Javier Garcia Alonso
 */
public interface ProcessesService {
    public List<ProcessInfo> getList();
    public List<ProcessInfo> getList(String name);
    
    public ProcessInfo getProcess(int pid);
    
    public JProcessesResponse killProcess(int pid);
    public JProcessesResponse changePriority(int pid, int priority);
}
