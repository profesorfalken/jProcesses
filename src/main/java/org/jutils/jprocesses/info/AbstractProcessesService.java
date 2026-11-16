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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Info related with processes
 *
 * @author Javier Garcia Alonso
 */
abstract class AbstractProcessesService implements ProcessesService {

    public List<ProcessInfo> getList() {
        return getList(null);
    }

    public List<ProcessInfo> getList(boolean fastMode) {
        return getList(null, fastMode);
    }

    public List<ProcessInfo> getList(String name) {
        return getList(name, false);
    }

    public List<ProcessInfo> getList(String name, boolean fastMode) {
        String rawData = getProcessesData(name, fastMode);

        List<Map<String, String>> mapList = parseList(rawData);

        return buildInfoFromMap(mapList);
    }

    public JProcessesResponse killProcess(int pid) {
        validatePid(pid);
        return kill(pid);
    }

    public JProcessesResponse killProcessGracefully(int pid) {
        validatePid(pid);
        return killGracefully(pid);
    }

    /**
     * Rejects PIDs that could have a special meaning for the operating system.
     * For example, on POSIX systems {@code kill -1} signals every process the
     * user is allowed to signal and {@code kill 0} signals the whole process
     * group, so they must never reach the command line by accident.
     *
     * @param pid process identifier
     * @throws IllegalArgumentException if the PID is not a positive number
     */
    protected static void validatePid(int pid) {
        if (pid <= 0) {
            throw new IllegalArgumentException("PID must be a positive integer (was " + pid + ")");
        }
    }

    protected abstract List<Map<String, String>> parseList(String rawData);

    protected abstract String getProcessesData(String name, boolean fastMode);

    protected abstract JProcessesResponse kill(int pid);

    protected abstract JProcessesResponse killGracefully(int pid);

    protected List<ProcessInfo> buildInfoFromMap(List<Map<String, String>> mapList) {
        List<ProcessInfo> infoList = new ArrayList<ProcessInfo>();

        if (mapList == null) {
            return infoList;
        }

        for (final Map<String, String> map : mapList) {
            if (map != null) {
                infoList.add(buildInfo(map));
            }
        }

        return infoList;
    }

    /**
     * Creates a {@link ProcessInfo} from the raw key/value data returned by the
     * platform specific parser.
     *
     * @param map raw process data
     * @return populated process information
     */
    protected ProcessInfo buildInfo(Map<String, String> map) {
        ProcessInfo info = new ProcessInfo();
        info.setPid(map.get("pid"));
        info.setName(map.get("proc_name"));
        info.setTime(map.get("proc_time"));
        info.setCommand((map.get("command") != null) ? map.get("command") : "");
        info.setCpuUsage(map.get("cpu_usage"));
        info.setPhysicalMemory(map.get("physical_memory"));
        info.setStartTime(map.get("start_time"));
        info.setUser(map.get("user"));
        info.setVirtualMemory(map.get("virtual_memory"));
        info.setPriority(map.get("priority"));

        //Adds extra data
        info.setExtraData(map);

        return info;
    }
}
