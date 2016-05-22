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
    private boolean fastMode = false;

    private JProcesses() {
    }

    public static JProcesses get() {
        return new JProcesses();
    }

    public JProcesses fastMode() {
        this.fastMode = true;
        return this;
    }

    public JProcesses fastMode(boolean enabled) {
        this.fastMode = enabled;
        return this;
    }

    public List<ProcessInfo> listProcesses() {
        return getService().getList(fastMode);
    }

    public List<ProcessInfo> listProcesses(String name) {
        return getService().getList(name, fastMode);
    }

    public static List<ProcessInfo> getProcessList() {
        return getService().getList();
    }

    public static List<ProcessInfo> getProcessList(String name) {
        return getService().getList(name);
    }

    public static ProcessInfo getProcess(int pid) {
        return getService().getProcess(pid);
    }

    public static JProcessesResponse killProcess(int pid) {
        return getService().killProcess(pid);
    }

    public static JProcessesResponse killProcessGracefully(int pid) {
        return getService().killProcessGracefully(pid);
    }

    public static JProcessesResponse changePriority(int pid, int newPriority) {
        return getService().changePriority(pid, newPriority);
    }

    private static ProcessesService getService() {
        return ProcessesFactory.getService();
    }
}
