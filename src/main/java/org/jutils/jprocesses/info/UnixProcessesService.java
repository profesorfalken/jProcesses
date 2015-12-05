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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for Unix/Linux
 *
 * @author Javier Garcia Alonso
 */
public class UnixProcessesService extends AbstractProcessesService {

    protected List<Map<String, String>> parseList(String rawData) {
        List<Map<String, String>> processesDataList = new ArrayList<Map<String, String>>();
        String[] dataStringLines = rawData.split("\\r?\\n");

        for (final String dataLine : dataStringLines) {
            if (!(dataLine.trim().startsWith("PID"))) {
                Map<String, String> element = new HashMap<String, String>();
                String[] elements = dataLine.split("\\s+");
                if (elements.length > 4) {
                    element.put("pid", elements[1]);
                    element.put("proc_time", elements[3]);
                    element.put("proc_name", elements[4]);

                    processesDataList.add(element);
                }
            }
        }

        return processesDataList;
    }
}
