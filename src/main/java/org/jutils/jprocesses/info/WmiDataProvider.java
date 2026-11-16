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

import com.profesorfalken.wmi4java.WMI4Java;
import com.profesorfalken.wmi4java.WMIClass;

import java.util.List;

/**
 * Minimal abstraction over the WMI calls needed by
 * {@link WindowsProcessesService}. It keeps the service testable without a
 * Windows machine and without mocking the WMI4Java final API.
 *
 * @author Javier Garcia Alonso
 */
interface WmiDataProvider {

    /**
     * Retrieves the raw output for the requested WMI process properties.
     *
     * @param properties properties to retrieve, or {@code null} for all
     * @param filters WQL filters, or {@code null} for no filtering
     * @return raw WMI output
     */
    String getRawProcessOutput(List<String> properties, List<String> filters);

    /**
     * Retrieves the raw performance data used to calculate CPU usage.
     *
     * @return raw performance data
     */
    String getRawPerformanceOutput();
}

/**
 * Default {@link WmiDataProvider} backed by WMI4Java.
 */
final class Wmi4JavaDataProvider implements WmiDataProvider {

    private final WMI4Java wmi4Java;

    Wmi4JavaDataProvider(WMI4Java wmi4Java) {
        this.wmi4Java = wmi4Java;
    }

    @Override
    public String getRawProcessOutput(List<String> properties, List<String> filters) {
        WMI4Java wmi = getWmi4Java().VBSEngine();

        if (properties != null) {
            wmi = wmi.properties(properties);
        }
        if (filters != null) {
            wmi = wmi.filters(filters);
        }

        return wmi.getRawWMIObjectOutput(WMIClass.WIN32_PROCESS);
    }

    @Override
    public String getRawPerformanceOutput() {
        return getWmi4Java().VBSEngine()
                .getRawWMIObjectOutput(WMIClass.WIN32_PERFFORMATTEDDATA_PERFPROC_PROCESS);
    }

    private WMI4Java getWmi4Java() {
        if (wmi4Java == null) {
            return WMI4Java.get();
        }
        return wmi4Java;
    }
}
