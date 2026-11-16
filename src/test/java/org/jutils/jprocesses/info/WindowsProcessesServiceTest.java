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

import org.junit.Before;
import org.junit.Test;
import org.jutils.jprocesses.model.ProcessInfo;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class WindowsProcessesServiceTest {

    private FakeWmiDataProvider wmi;
    private TestWindowsProcessesService srv;

    @Before
    public void setUp() {
        wmi = new FakeWmiDataProvider(
                WMI_PROCESS1 + WMI_PROCESS2 + WMI_PROCESS3 + WMI_PROCESS4 + WMI_PROCESS5,
                WMI_PROCESS1_PERF + WMI_PROCESS2_PERF + WMI_PROCESS3_PERF
                        + WMI_PROCESS4_PERF + WMI_PROCESS5_PERF);
        srv = new TestWindowsProcessesService(wmi,
                "3644:Denis\n6936:Denis\n2876:Denis\n6700:Denis\n8160:Denis\n");
    }

    @Test
    public void parsesAllProcesses() {
        List<ProcessInfo> processes = srv.getList();

        assertEquals(5, processes.size());

        ProcessInfo idea = find(processes, "3644");
        assertNotNull(idea);
        assertEquals("idea64.exe", idea.getName());
        assertEquals("Denis", idea.getUser());
        assertEquals("4188632", idea.getVirtualMemory());
        assertEquals("1056660", idea.getPhysicalMemory());
        assertEquals("6", idea.getCpuUsage());
        assertEquals("13:58:15", idea.getStartTime());
        assertEquals("8", idea.getPriority());
        assertEquals("00:09:28", idea.getTime());
        assertTrue(idea.getCommand().contains("idea64.exe"));
        assertEquals("05/14/2016 13:58:15",
                idea.getExtraData().get("start_datetime"));

        ProcessInfo doom = find(processes, "6936");
        assertNotNull(doom);
        assertEquals("100", doom.getCpuUsage());
        assertEquals("00:01:25", doom.getTime());

        ProcessInfo chrome = find(processes, "2876");
        assertNotNull(chrome);
        assertEquals("0", chrome.getCpuUsage());
        assertEquals("00:03:14", chrome.getTime());

        ProcessInfo java1 = find(processes, "6700");
        assertNotNull(java1);
        assertEquals("2", java1.getCpuUsage());
        assertEquals("00:00:01", java1.getTime());

        //The last performance block has no following "Caption" line
        ProcessInfo java2 = find(processes, "8160");
        assertNotNull(java2);
        assertEquals("3", java2.getCpuUsage());
        assertEquals("00:00:00", java2.getTime());
        assertEquals("Denis", java2.getUser());
    }

    @Test
    public void filtersByNameCaseInsensitively() {
        List<ProcessInfo> processes = srv.getList("java.exe");

        assertEquals(2, processes.size());
        assertEquals("java.exe", processes.get(0).getName());
        assertEquals("java.exe", processes.get(1).getName());
        assertNotNull(wmi.getLastFilters());
        assertTrue(wmi.getLastFilters().get(0).contains("java.exe"));
        assertEquals(2, srv.getList("JAVA.EXE").size());
        //Matching is exact: "java" must not match "java.exe"
        assertTrue(srv.getList("java").isEmpty());
    }

    @Test
    public void unsafeNamesAreNotEmbeddedInWql() {
        assertTrue(srv.getList("java' OR 1=1 --").isEmpty());
        assertNull(wmi.getLastFilters());
    }

    @Test
    public void fastModeLeavesUserAndCpuEmpty() {
        List<ProcessInfo> processes = srv.getList(true);

        assertEquals(5, processes.size());
        assertNull(find(processes, "3644").getUser());
        assertNull(find(processes, "3644").getCpuUsage());
    }

    @Test
    public void getsProcessByPid() {
        ProcessInfo process = srv.getProcess(3644);

        assertNotNull(process);
        assertEquals("idea64.exe", process.getName());
        assertEquals("6", process.getCpuUsage());
        assertEquals("ProcessId = 3644", wmi.getLastFilters().get(0));

        assertNull(srv.getProcess(999999));
    }

    @Test
    public void invalidPidsAreRejected() {
        assertPidRejected(0);
        assertPidRejected(-1);
    }

    @Test
    public void extraDataIsNotReusedBetweenCalls() {
        assertNotNull(find(srv.getList(), "3644").getCpuUsage());

        wmi.setPerformanceOutput("");
        srv.setOwners("");

        ProcessInfo process = find(srv.getList(), "3644");
        assertNull(process.getCpuUsage());
        assertNull(process.getUser());
    }

    @Test
    public void malformedWmiOutputDoesNotThrow() {
        assertTrue(srv.parseList("").isEmpty());
        assertTrue(srv.parseList("garbage without colon\n").isEmpty());
        assertEquals(1, srv.parseList("Caption: x\nName: y\n").size());
        assertEquals(1, srv.parseList("Caption: x\nUserModeTime: \nVirtualSize: \n"
                + "WorkingSetSize: \nCreationDate: \n").size());
    }

    private void assertPidRejected(int pid) {
        try {
            srv.getProcess(pid);
            throw new AssertionError("getProcess must reject PID " + pid);
        } catch (IllegalArgumentException expected) {
            // expected
        }
        try {
            srv.killProcess(pid);
            throw new AssertionError("killProcess must reject PID " + pid);
        } catch (IllegalArgumentException expected) {
            // expected
        }
        try {
            srv.killProcessGracefully(pid);
            throw new AssertionError("killProcessGracefully must reject PID " + pid);
        } catch (IllegalArgumentException expected) {
            // expected
        }
        try {
            srv.changePriority(pid, 8);
            throw new AssertionError("changePriority must reject PID " + pid);
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    private static ProcessInfo find(List<ProcessInfo> processes, String pid) {
        for (ProcessInfo process : processes) {
            if (pid.equals(process.getPid())) {
                return process;
            }
        }
        return null;
    }

    private static final class TestWindowsProcessesService extends WindowsProcessesService {

        private String owners;

        private TestWindowsProcessesService(WmiDataProvider wmi, String owners) {
            super(wmi);
            this.owners = owners;
        }

        @Override
        protected String getProcessOwners() {
            return owners;
        }

        private void setOwners(String owners) {
            this.owners = owners;
        }
    }

    private static final class FakeWmiDataProvider implements WmiDataProvider {

        private final String processOutput;
        private String performanceOutput;
        private List<String> lastFilters;

        private FakeWmiDataProvider(String processOutput, String performanceOutput) {
            this.processOutput = processOutput;
            this.performanceOutput = performanceOutput;
        }

        public String getRawProcessOutput(List<String> properties, List<String> filters) {
            this.lastFilters = filters;
            return processOutput;
        }

        public String getRawPerformanceOutput() {
            return performanceOutput;
        }

        private void setPerformanceOutput(String performanceOutput) {
            this.performanceOutput = performanceOutput;
        }

        private List<String> getLastFilters() {
            return lastFilters;
        }
    }

    private static final String WMI_PROCESS1 = "Caption: idea64.exe\n"
            + "CommandLine: \"C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\bin\\idea64.exe\" \n"
            + "CreationClassName: Win32_Process\n"
            + "CreationDate: 20160514135815.444566+180\n"
            + "CSCreationClassName: Win32_ComputerSystem\n"
            + "CSName: DENIS-PC\n"
            + "Description: idea64.exe\n"
            + "ExecutablePath: C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\bin\\idea64.exe\n"
            + "ExecutionState: \n"
            + "Handle: 3644\n"
            + "HandleCount: 910\n"
            + "InstallDate: \n"
            + "KernelModeTime: 524007359\n"
            + "MaximumWorkingSetSize: 1380\n"
            + "MinimumWorkingSetSize: 200\n"
            + "Name: idea64.exe\n"
            + "OSCreationClassName: Win32_OperatingSystem\n"
            + "OSName: Microsoft Windows 7 Ultimate |C:\\Windows|\\Device\\Harddisk0\\Partition2\n"
            + "OtherOperationCount: 1934035\n"
            + "OtherTransferCount: 35525870\n"
            + "PageFaults: 2541972\n"
            + "PageFileUsage: 1117360\n"
            + "ParentProcessId: 500\n"
            + "PeakPageFileUsage: 1133232\n"
            + "PeakVirtualSize: 4297838592\n"
            + "PeakWorkingSetSize: 1071420\n"
            + "Priority: 8\n"
            + "PrivatePageCount: 1144176640\n"
            + "ProcessId: 3644\n"
            + "QuotaNonPagedPoolUsage: 75\n"
            + "QuotaPagedPoolUsage: 304\n"
            + "QuotaPeakNonPagedPoolUsage: 81\n"
            + "QuotaPeakPagedPoolUsage: 326\n"
            + "ReadOperationCount: 178223\n"
            + "ReadTransferCount: 661366634\n"
            + "SessionId: 1\n"
            + "Status: \n"
            + "TerminationDate: \n"
            + "ThreadCount: 64\n"
            + "UserModeTime: 5684832441\n"
            + "VirtualSize: 4289159168\n"
            + "WindowsVersion: 6.1.7601\n"
            + "WorkingSetSize: 1082019840\n"
            + "WriteOperationCount: 4141\n"
            + "WriteTransferCount: 1499671310\n";

    private static final String WMI_PROCESS2 = "Caption: DOOMx64.exe\n"
            + "CommandLine: \"D:\\SteamLibrary\\steamapps\\common\\DOOM\\DOOMx64.exe\"\n"
            + "CreationClassName: Win32_Process\n"
            + "CreationDate: 20160514144712.680566+180\n"
            + "CSCreationClassName: Win32_ComputerSystem\n"
            + "CSName: DENIS-PC\n"
            + "Description: DOOMx64.exe\n"
            + "ExecutablePath: D:\\SteamLibrary\\steamapps\\common\\DOOM\\DOOMx64.exe\n"
            + "ExecutionState:\n"
            + "Handle: 6936\n"
            + "HandleCount: 1110\n"
            + "InstallDate:\n"
            + "KernelModeTime: 709960551\n"
            + "MaximumWorkingSetSize: 628680\n"
            + "MinimumWorkingSetSize: 628568\n"
            + "Name: DOOMx64.exe\n"
            + "OSCreationClassName: Win32_OperatingSystem\n"
            + "OSName: Microsoft Windows 7 Ultimate |C:\\Windows|\\Device\\Harddisk0\\Partition2\n"
            + "OtherOperationCount: 10494\n"
            + "OtherTransferCount: 109196\n"
            + "PageFaults: 386552\n"
            + "PageFileUsage: 1585468\n"
            + "ParentProcessId: 2548\n"
            + "PeakPageFileUsage: 1585476\n"
            + "PeakVirtualSize: 2490068992\n"
            + "PeakWorkingSetSize: 1023348\n"
            + "Priority: 8\n"
            + "PrivatePageCount: 1623519232\n"
            + "ProcessId: 6936\n"
            + "QuotaNonPagedPoolUsage: 128\n"
            + "QuotaPagedPoolUsage: 3330\n"
            + "QuotaPeakNonPagedPoolUsage: 128\n"
            + "QuotaPeakPagedPoolUsage: 3340\n"
            + "ReadOperationCount: 2376\n"
            + "ReadTransferCount: 2433886942\n"
            + "SessionId: 1\n"
            + "Status:\n"
            + "TerminationDate:\n"
            + "ThreadCount: 33\n"
            + "UserModeTime: 857069494\n"
            + "VirtualSize: 2487513088\n"
            + "WindowsVersion: 6.1.7601\n"
            + "WorkingSetSize: 1047908352\n"
            + "WriteOperationCount: 210\n"
            + "WriteTransferCount: 11894\n";
    private static final String WMI_PROCESS3 = "Caption: chrome.exe\n"
            + "CommandLine: \"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe\" --no-startup-window /prefetch:5\n"
            + "CreationClassName: Win32_Process\n"
            + "CreationDate: 20160514115400.098497+180\n"
            + "CSCreationClassName: Win32_ComputerSystem\n"
            + "CSName: DENIS-PC\n"
            + "Description: chrome.exe\n"
            + "ExecutablePath: C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe\n"
            + "ExecutionState:\n"
            + "Handle: 2876\n"
            + "HandleCount: 1265\n"
            + "InstallDate:\n"
            + "KernelModeTime: 661912243\n"
            + "MaximumWorkingSetSize: 1380\n"
            + "MinimumWorkingSetSize: 200\n"
            + "Name: chrome.exe\n"
            + "OSCreationClassName: Win32_OperatingSystem\n"
            + "OSName: Microsoft Windows 7 Ultimate |C:\\Windows|\\Device\\Harddisk0\\Partition2\n"
            + "OtherOperationCount: 493715\n"
            + "OtherTransferCount: 18990227\n"
            + "PageFaults: 1548415\n"
            + "PageFileUsage: 104732\n"
            + "ParentProcessId: 500\n"
            + "PeakPageFileUsage: 138056\n"
            + "PeakVirtualSize: 764755968\n"
            + "PeakWorkingSetSize: 171256\n"
            + "Priority: 8\n"
            + "PrivatePageCount: 107245568\n"
            + "ProcessId: 2876\n"
            + "QuotaNonPagedPoolUsage: 65\n"
            + "QuotaPagedPoolUsage: 852\n"
            + "QuotaPeakNonPagedPoolUsage: 232\n"
            + "QuotaPeakPagedPoolUsage: 1247\n"
            + "ReadOperationCount: 1549009\n"
            + "ReadTransferCount: 1148378511\n"
            + "SessionId: 1\n"
            + "Status:\n"
            + "TerminationDate:\n"
            + "ThreadCount: 39\n"
            + "UserModeTime: 1948764492\n"
            + "VirtualSize: 567984128\n"
            + "WindowsVersion: 6.1.7601\n"
            + "WorkingSetSize: 144740352\n"
            + "WriteOperationCount: 1793811\n"
            + "WriteTransferCount: 1503510606\n";
    private static final String WMI_PROCESS4 = "Caption: java.exe\n"
            + "CommandLine: \"C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\bin\\java\" -Djava.awt.headless=true -Didea.version==2016.1.2 -Xmx512m -Didea.maven.embedder.version=3.0.5 -Dfile.encoding=windows-1251 -classpath \"C:/Program Files (x86)/JetBrains/IntelliJ IDEA Community Edition 2016.1/lib/resources_en.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\log4j.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\slf4j-api-1.7.10.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\slf4j-log4j12-1.7.10.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\jna.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\resources_en.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\util.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\oromatcher.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\annotations.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\snappy-in-java-0.3.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\trove4j.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\jna-platform.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\jdom.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\picocontainer.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\lucene-core-2.4.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven-server-api.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-common.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-lib\\archetype-catalog-2.2.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-lib\\archetype-common-2.2.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-lib\\maven-dependency-tree-1.2.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-lib\\nexus-indexer-3.0.4.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-lib\\nexus-indexer-artifact-1.0.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven30-server-impl.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\aether-api-1.13.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\aether-connector-wagon-1.13.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\aether-impl-1.13.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\aether-spi-1.13.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\aether-util-1.13.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\commons-cli-1.2.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\commons-io-2.2.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\commons-lang-2.6.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-aether-provider-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-artifact-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-compat-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-core-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-embedder-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-model-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-model-builder-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-plugin-api-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-repository-metadata-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-settings-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-settings-builder-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\plexus-cipher-1.7.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\plexus-component-annotations-1.5.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\plexus-interpolation-1.14.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\plexus-sec-dispatcher-1.3.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\plexus-utils-2.0.6.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\sisu-guava-0.9.9.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\sisu-guice-3.1.0-no_aop.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\sisu-inject-bean-2.3.0.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\sisu-inject-plexus-2.3.0.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\wagon-file-2.8.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\wagon-http-2.8-shaded.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\wagon-http-shared-2.8.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\wagon-provider-api-2.8.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\boot\\plexus-classworlds-2.4.jar\" org.jetbrains.idea.maven.server.RemoteMavenServer\n"
            + "CreationClassName: Win32_Process\n"
            + "CreationDate: 20160514135845.836304+180\n"
            + "CSCreationClassName: Win32_ComputerSystem\n"
            + "CSName: DENIS-PC\n"
            + "Description: java.exe\n"
            + "ExecutablePath: C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\bin\\java.exe\n"
            + "ExecutionState: \n"
            + "Handle: 6700\n"
            + "HandleCount: 324\n"
            + "InstallDate: \n"
            + "KernelModeTime: 1716011\n"
            + "MaximumWorkingSetSize: 1380\n"
            + "MinimumWorkingSetSize: 200\n"
            + "Name: java.exe\n"
            + "OSCreationClassName: Win32_OperatingSystem\n"
            + "OSName: Microsoft Windows 7 Ultimate |C:\\Windows|\\Device\\Harddisk0\\Partition2\n"
            + "OtherOperationCount: 12619\n"
            + "OtherTransferCount: 274107\n"
            + "PageFaults: 34915\n"
            + "PageFileUsage: 240784\n"
            + "ParentProcessId: 3644\n"
            + "PeakPageFileUsage: 245768\n"
            + "PeakVirtualSize: 2058690560\n"
            + "PeakWorkingSetSize: 94992\n"
            + "Priority: 8\n"
            + "PrivatePageCount: 246562816\n"
            + "ProcessId: 6700\n"
            + "QuotaNonPagedPoolUsage: 22\n"
            + "QuotaPagedPoolUsage: 169\n"
            + "QuotaPeakNonPagedPoolUsage: 26\n"
            + "QuotaPeakPagedPoolUsage: 171\n"
            + "ReadOperationCount: 9873\n"
            + "ReadTransferCount: 11487969\n"
            + "SessionId: 1\n"
            + "Status: \n"
            + "TerminationDate: \n"
            + "ThreadCount: 24\n"
            + "UserModeTime: 18252117\n"
            + "VirtualSize: 2054496256\n"
            + "WindowsVersion: 6.1.7601\n"
            + "WorkingSetSize: 85766144\n"
            + "WriteOperationCount: 118\n"
            + "WriteTransferCount: 3848\n";

    private static final String WMI_PROCESS5 = "Caption: java.exe\n"
            + "CommandLine: \"C:\\Program Files\\Java\\jdk1.8.0_60\\bin\\java\" -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:53995,suspend=y,server=n -ea -Didea.junit.sm_runner -Dfile.encoding=UTF-8 -classpath \"C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\idea_rt.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\junit\\lib\\junit-rt.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\charsets.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\deploy.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\javaws.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\jce.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\jfr.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\jfxswt.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\jsse.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\management-agent.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\plugin.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\resources.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\access-bridge-64.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\cldrdata.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\dnsns.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\jaccess.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\jfxrt.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\localedata.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\nashorn.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\sunec.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\sunjce_provider.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\sunmscapi.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\sunpkcs11.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\zipfs.jar;D:\\dev\\jProcesses\\target\\test-classes;D:\\dev\\jProcesses\\target\\classes;C:\\Users\\Denis\\.m2\\repository\\junit\\junit\\4.10\\junit-4.10.jar;C:\\Users\\Denis\\.m2\\repository\\org\\hamcrest\\hamcrest-core\\1.1\\hamcrest-core-1.1.jar;C:\\Users\\Denis\\.m2\\repository\\com\\profesorfalken\\WMI4Java\\1.1\\WMI4Java-1.1.jar;C:\\Users\\Denis\\.m2\\repository\\com\\profesorfalken\\jPowerShell\\1.3\\jPowerShell-1.3.jar;C:\\Users\\Denis\\.m2\\repository\\org\\mockito\\mockito-all\\1.8.4\\mockito-all-1.8.4.jar\" com.intellij.rt.execution.junit.JUnitStarter -ideVersion5 org.jutils.jprocesses.info.WindowsProcessesServiceTest,testGetListWithName\n"
            + "CreationClassName: Win32_Process\n"
            + "CreationDate: 20160514151848.264987+180\n"
            + "CSCreationClassName: Win32_ComputerSystem\n"
            + "CSName: DENIS-PC\n"
            + "Description: java.exe\n"
            + "ExecutablePath: C:\\Program Files\\Java\\jdk1.8.0_60\\bin\\java.exe\n"
            + "ExecutionState: \n"
            + "Handle: 8160\n"
            + "HandleCount: 252\n"
            + "InstallDate: \n"
            + "KernelModeTime: 2808018\n"
            + "MaximumWorkingSetSize: 1380\n"
            + "MinimumWorkingSetSize: 200\n"
            + "Name: java.exe\n"
            + "OSCreationClassName: Win32_OperatingSystem\n"
            + "OSName: Microsoft Windows 7 Ultimate |C:\\Windows|\\Device\\Harddisk0\\Partition2\n"
            + "OtherOperationCount: 7038\n"
            + "OtherTransferCount: 163028\n"
            + "PageFaults: 118732\n"
            + "PageFileUsage: 892380\n"
            + "ParentProcessId: 3644\n"
            + "PeakPageFileUsage: 894172\n"
            + "PeakVirtualSize: 3683155968\n"
            + "PeakWorkingSetSize: 462196\n"
            + "Priority: 8\n"
            + "PrivatePageCount: 913797120\n"
            + "ProcessId: 8160\n"
            + "QuotaNonPagedPoolUsage: 20\n"
            + "QuotaPagedPoolUsage: 178\n"
            + "QuotaPeakNonPagedPoolUsage: 119\n"
            + "QuotaPeakPagedPoolUsage: 178\n"
            + "ReadOperationCount: 4176\n"
            + "ReadTransferCount: 6494968\n"
            + "SessionId: 1\n"
            + "Status: \n"
            + "TerminationDate: \n"
            + "ThreadCount: 20\n"
            + "UserModeTime: 8268053\n"
            + "VirtualSize: 3683155968\n"
            + "WindowsVersion: 6.1.7601\n"
            + "WorkingSetSize: 472100864\n"
            + "WriteOperationCount: 14\n"
            + "WriteTransferCount: 6635\n";

    private static final String WMI_PROCESS1_PERF = "Caption: \n"
            + "CreatingProcessID: 2548\n"
            + "Description: \n"
            + "ElapsedTime: 361\n"
            + "Frequency_Object: \n"
            + "Frequency_PerfTime: \n"
            + "Frequency_Sys100NS: \n"
            + "HandleCount: 1739\n"
            + "IDProcess: 6936\n"
            + "IODataBytesPersec: 3631675\n"
            + "IODataOperationsPersec: 265\n"
            + "IOOtherBytesPersec: 949\n"
            + "IOOtherOperationsPersec: 296\n"
            + "IOReadBytesPersec: 3630607\n"
            + "IOReadOperationsPersec: 146\n"
            + "IOWriteBytesPersec: 1068\n"
            + "IOWriteOperationsPersec: 118\n"
            + "Name: DOOMx64\n"
            + "PageFaultsPersec: 0\n"
            + "PageFileBytes: 2646224896\n"
            + "PageFileBytesPeak: 2648330240\n"
            + "PercentPrivilegedTime: 67\n"
            + "PercentProcessorTime: 100\n"
            + "PercentUserTime: 80\n"
            + "PoolNonpagedBytes: 301176\n"
            + "PoolPagedBytes: 2782520\n"
            + "PriorityBase: 8\n"
            + "PrivateBytes: 2646224896\n"
            + "ThreadCount: 36\n"
            + "Timestamp_Object: \n"
            + "Timestamp_PerfTime: \n"
            + "Timestamp_Sys100NS: \n"
            + "VirtualBytes: 3179716608\n"
            + "VirtualBytesPeak: 3231305728\n"
            + "WorkingSet: 2166816768\n"
            + "WorkingSetPeak: 2166833152\n"
            + "WorkingSetPrivate: 1682055168\n";
    private static final String WMI_PROCESS2_PERF = "Caption: \n"
            + "CreatingProcessID: 500\n"
            + "Description: \n"
            + "ElapsedTime: 3298\n"
            + "Frequency_Object: \n"
            + "Frequency_PerfTime: \n"
            + "Frequency_Sys100NS: \n"
            + "HandleCount: 917\n"
            + "IDProcess: 3644\n"
            + "IODataBytesPersec: 1867\n"
            + "IODataOperationsPersec: 11\n"
            + "IOOtherBytesPersec: 13672\n"
            + "IOOtherOperationsPersec: 854\n"
            + "IOReadBytesPersec: 1867\n"
            + "IOReadOperationsPersec: 11\n"
            + "IOWriteBytesPersec: 0\n"
            + "IOWriteOperationsPersec: 0\n"
            + "Name: idea64\n"
            + "PageFaultsPersec: 3\n"
            + "PageFileBytes: 1155874816\n"
            + "PageFileBytesPeak: 1166286848\n"
            + "PercentPrivilegedTime: 0\n"
            + "PercentProcessorTime: 6\n"
            + "PercentUserTime: 6\n"
            + "PoolNonpagedBytes: 74096\n"
            + "PoolPagedBytes: 310792\n"
            + "PriorityBase: 8\n"
            + "PrivateBytes: 1155874816\n"
            + "ThreadCount: 65\n"
            + "Timestamp_Object: \n"
            + "Timestamp_PerfTime: \n"
            + "Timestamp_Sys100NS: \n"
            + "VirtualBytes: 4290207744\n"
            + "VirtualBytesPeak: 4300398592\n"
            + "WorkingSet: 1061761024\n"
            + "WorkingSetPeak: 1097666560\n"
            + "WorkingSetPrivate: 1050238976\n";
    private static final String WMI_PROCESS3_PERF = "Caption: \n"
            + "CreatingProcessID: 500\n"
            + "Description: \n"
            + "ElapsedTime: 10753\n"
            + "Frequency_Object: \n"
            + "Frequency_PerfTime: \n"
            + "Frequency_Sys100NS: \n"
            + "HandleCount: 1259\n"
            + "IDProcess: 2876\n"
            + "IODataBytesPersec: 236304\n"
            + "IODataOperationsPersec: 522\n"
            + "IOOtherBytesPersec: 443\n"
            + "IOOtherOperationsPersec: 106\n"
            + "IOReadBytesPersec: 107811\n"
            + "IOReadOperationsPersec: 174\n"
            + "IOWriteBytesPersec: 128493\n"
            + "IOWriteOperationsPersec: 348\n"
            + "Name: chrome\n"
            + "PageFaultsPersec: 0\n"
            + "PageFileBytes: 107134976\n"
            + "PageFileBytesPeak: 141369344\n"
            + "PercentPrivilegedTime: 0\n"
            + "PercentProcessorTime: 0\n"
            + "PercentUserTime: 0\n"
            + "PoolNonpagedBytes: 63448\n"
            + "PoolPagedBytes: 870848\n"
            + "PriorityBase: 8\n"
            + "PrivateBytes: 107134976\n"
            + "ThreadCount: 37\n"
            + "Timestamp_Object: \n"
            + "Timestamp_PerfTime: \n"
            + "Timestamp_Sys100NS: \n"
            + "VirtualBytes: 565362688\n"
            + "VirtualBytesPeak: 764755968\n"
            + "WorkingSet: 132898816\n"
            + "WorkingSetPeak: 175366144\n"
            + "WorkingSetPrivate: 86532096\n";
    private static final String WMI_PROCESS4_PERF = "Caption: \n"
            + "CreatingProcessID: 3644\n"
            + "Description: \n"
            + "ElapsedTime: 4808\n"
            + "Frequency_Object: \n"
            + "Frequency_PerfTime: \n"
            + "Frequency_Sys100NS: \n"
            + "HandleCount: 325\n"
            + "IDProcess: 6700\n"
            + "IODataBytesPersec: 0\n"
            + "IODataOperationsPersec: 0\n"
            + "IOOtherBytesPersec: 0\n"
            + "IOOtherOperationsPersec: 0\n"
            + "IOReadBytesPersec: 0\n"
            + "IOReadOperationsPersec: 0\n"
            + "IOWriteBytesPersec: 0\n"
            + "IOWriteOperationsPersec: 0\n"
            + "Name: java\n"
            + "PageFaultsPersec: 0\n"
            + "PageFileBytes: 246562816\n"
            + "PageFileBytesPeak: 251666432\n"
            + "PercentPrivilegedTime: 0\n"
            + "PercentProcessorTime: 2\n"
            + "PercentUserTime: 0\n"
            + "PoolNonpagedBytes: 23296\n"
            + "PoolPagedBytes: 172744\n"
            + "PriorityBase: 8\n"
            + "PrivateBytes: 246562816\n"
            + "ThreadCount: 24\n"
            + "Timestamp_Object: \n"
            + "Timestamp_PerfTime: \n"
            + "Timestamp_Sys100NS: \n"
            + "VirtualBytes: 2054496256\n"
            + "VirtualBytesPeak: 2058690560\n"
            + "WorkingSet: 85757952\n"
            + "WorkingSetPeak: 97271808\n"
            + "WorkingSetPrivate: 80453632\n";
    private static final String WMI_PROCESS5_PERF = "Caption: \n"
            + "CreatingProcessID: 3644\n"
            + "Description: \n"
            + "ElapsedTime: 5\n"
            + "Frequency_Object: \n"
            + "Frequency_PerfTime: \n"
            + "Frequency_Sys100NS: \n"
            + "HandleCount: 245\n"
            + "IDProcess: 8160\n"
            + "IODataBytesPersec: 0\n"
            + "IODataOperationsPersec: 0\n"
            + "IOOtherBytesPersec: 0\n"
            + "IOOtherOperationsPersec: 0\n"
            + "IOReadBytesPersec: 0\n"
            + "IOReadOperationsPersec: 0\n"
            + "IOWriteBytesPersec: 0\n"
            + "IOWriteOperationsPersec: 0\n"
            + "Name: java#1\n"
            + "PageFaultsPersec: 0\n"
            + "PageFileBytes: 250654720\n"
            + "PageFileBytesPeak: 255041536\n"
            + "PercentPrivilegedTime: 0\n"
            + "PercentProcessorTime: 3\n"
            + "PercentUserTime: 0\n"
            + "PoolNonpagedBytes: 19704\n"
            + "PoolPagedBytes: 179368\n"
            + "PriorityBase: 8\n"
            + "PrivateBytes: 250654720\n"
            + "ThreadCount: 20\n"
            + "Timestamp_Object: \n"
            + "Timestamp_PerfTime: \n"
            + "Timestamp_Sys100NS: \n"
            + "VirtualBytes: 3683155968\n"
            + "VirtualBytesPeak: 3683155968\n"
            + "WorkingSet: 40710144\n"
            + "WorkingSetPeak: 45494272\n"
            + "WorkingSetPrivate: 30330880\n";
}
