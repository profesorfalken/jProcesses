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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jutils.jprocesses.model.JProcessesResponse;
import org.jutils.jprocesses.model.ProcessInfo;
import org.jutils.jprocesses.util.ProcessesUtils;

/**
 * Service implementation for Unix/BSD systems
 *
 * @author Javier Garcia Alonso
 */
class UnixProcessesService extends AbstractProcessesService {

    // Use BSD sytle to get data in order to be compatible with Mac Systems(thanks to jkuharev for this tip)
	/* if using both comm and command the ps utility will truncate the first one of them.
	the fully expanded text columns must be the last one in the list of options
	there is a difference in displaying comm: 
	    on Linux: comm means the base of the binary file, e.g. bash
	    on UNIX: comm means the full binary path, e.g. /bin/bash
	*/
	private static final String PS_COLUMNS = "pid,ruser,vsize,rss,%cpu,start,cputime,nice,comm";
	private static final int PS_COLUMNS_COUNT = PS_COLUMNS.split( "," ).length;

    protected List<Map<String, String>> parseList(String rawData) {
        List<Map<String, String>> processesDataList = new ArrayList<Map<String, String>>();
        String[] dataStringLines = rawData.split("\\r?\\n");

        for (final String dataLine : dataStringLines) {
			// skip first line
			// PID RUSER VSZ RSS %CPU STARTED TIME NI COMM
			if (!( dataLine.trim().startsWith( "PID" ) ))
			{
                Map<String, String> element = new HashMap<String, String>();
				// ensure that we don't split the command
				String[] elements = dataLine.trim().split( "\\s+", PS_COLUMNS_COUNT );
				if (elements.length >= PS_COLUMNS_COUNT)
				{
                	// the trick is to use a column index and increment it stepwise
                	// so we can reorder columns at any time and forged about the numbers
					int col = 0;
					element.put( "pid", elements[col++] );
					element.put( "user", elements[col++] );
					element.put( "virtual_memory", elements[col++] );
					element.put( "physical_memory", elements[col++] );
					element.put( "cpu_usage", elements[col++] );
					element.put( "start_time", elements[col++] );
					element.put( "proc_time", elements[col++] );
					element.put( "priority", elements[col++] );
					element.put( "command", elements[col++] );
					element.put( "proc_name", element.get( "command" ) );
                    processesDataList.add(element);
                }
            }
        }

        return processesDataList;
    }

    @Override
    protected String getProcessesData(String name) {
        if (name != null) {
            return ProcessesUtils.executeCommand("bash", "-c", 
				"ps -e -o " + PS_COLUMNS + " | grep \"" + name + "\" | grep -v \"grep " + name + "\"" );
        }
        return ProcessesUtils.executeCommand("ps", "-e", "-o", PS_COLUMNS );
    }

    @Override
    protected JProcessesResponse kill(int pid) {
        JProcessesResponse response = new JProcessesResponse();
        if (ProcessesUtils.executeCommandAndGetCode("kill", "-9", String.valueOf(pid)) == 0) {
            response.setSuccess(true);
        }
        return response;
    }

    @Override
    protected JProcessesResponse killGracefully(int pid) {
        JProcessesResponse response = new JProcessesResponse();
        if (ProcessesUtils.executeCommandAndGetCode("kill", "-15", String.valueOf(pid)) == 0) {
            response.setSuccess(true);
        }
        return response;
    }

    public JProcessesResponse changePriority(int pid, int priority) {
        JProcessesResponse response = new JProcessesResponse();
        if (ProcessesUtils.executeCommandAndGetCode("renice", String.valueOf(priority),
                "-p", String.valueOf(pid)) == 0) {
            response.setSuccess(true);
        }
        return response;
    }

    public ProcessInfo getProcess(int pid) {
        List<Map<String, String>> processList
                = parseList(ProcessesUtils.executeCommand("ps",
				"-o", PS_COLUMNS, "-p", String.valueOf( pid ) ) );

        if (processList != null && !processList.isEmpty()) {
            Map<String, String> processData = processList.get(0);
            ProcessInfo info = new ProcessInfo();
            info.setPid(processData.get("pid"));
            info.setName(processData.get("proc_name"));
            info.setTime(processData.get("proc_time"));
            info.setCommand(processData.get("command"));
            info.setCpuUsage(processData.get("cpu_usage"));
            info.setPhysicalMemory(processData.get("physical_memory"));
            info.setStartTime(processData.get("start_time"));
            info.setUser(processData.get("user"));
            info.setVirtualMemory(processData.get("virtual_memory"));
            info.setPriority(processData.get("priority"));

            return info;
        }
        return null;
    }
}
