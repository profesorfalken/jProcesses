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
package org.jutils.jprocesses.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Model that encapsulates process information
 *
 * @author Javier Garcia Alonso
 */
public class ProcessInfo {

    private String pid;
    private String time;
    private String name;
    private String user;
    private String virtualMemory;
    private String physicalMemory;
    private String cpuUsage;
    private String startTime;
    private String priority;
    private String command;
    
    //Used to store system specific data
    private Map<String, String> extraData = new HashMap<String, String>();

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getVirtualMemory() {
        return virtualMemory;
    }

    public void setVirtualMemory(String virtualMemory) {
        this.virtualMemory = virtualMemory;
    }

    public String getPhysicalMemory() {
        return physicalMemory;
    }

    public void setPhysicalMemory(String physicalMemory) {
        this.physicalMemory = physicalMemory;
    }

    public String getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(String cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Map<String, String> getExtraData() {
        return extraData;
    }

    public void setExtraData(Map<String, String> extraData) {
        this.extraData = extraData;
    }
    
    public void addExtraData(String key, String value) {
        this.extraData.put(key, value);
    }
    
    @Override
    public String toString() {
        return "PID:" + pid + "	CPU:" + cpuUsage + "	MEM:" + physicalMemory
                + "	PRIORITY:" + priority + "	CMD:" + command;
    }
}
