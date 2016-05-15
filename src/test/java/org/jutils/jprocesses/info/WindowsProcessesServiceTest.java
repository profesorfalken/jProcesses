package org.jutils.jprocesses.info;

import com.profesorfalken.wmi4java.WMI4Java;
import com.profesorfalken.wmi4java.WMIClass;
import org.junit.Before;
import org.junit.Test;
import org.jutils.jprocesses.model.ProcessInfo;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class WindowsProcessesServiceTest {

    @Mock
    WMI4Java wmi4Java;
    WindowsProcessesService srv;
    ProcessInfo processInfo1;
    ProcessInfo processInfo2;
    ProcessInfo processInfo3;
    ProcessInfo processInfo4;
    ProcessInfo processInfo5;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        srv = new WindowsProcessesService(wmi4Java);
        processInfo1 = new ProcessInfo("3644","00:09:568","idea64.exe","Denis","4188632","1056660","100","13:58:15","8", "\"C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\bin\\idea64.exe\"");
        processInfo2 = new ProcessInfo("6936","00:01:85","DOOMx64.exe","Denis","2429212","1023348","6","14:47:12","8", "\"D:\\SteamLibrary\\steamapps\\common\\DOOM\\DOOMx64.exe\"");
        processInfo3 = new ProcessInfo("2876","00:03:194","chrome.exe","Denis","554672","141348","0","11:54:00","8", "\"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe\" --no-startup-window /prefetch:5");
        processInfo4 = new ProcessInfo("6700","00:00:01","java.exe","Denis","2006344","83756","2","13:58:45","8", "\"C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\bin\\java\" -Djava.awt.headless=true -Didea.version==2016.1.2 -Xmx512m -Didea.maven.embedder.version=3.0.5 -Dfile.encoding=windows-1251 -classpath \"C:/Program Files (x86)/JetBrains/IntelliJ IDEA Community Edition 2016.1/lib/resources_en.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\log4j.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\slf4j-api-1.7.10.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\slf4j-log4j12-1.7.10.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\jna.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\resources_en.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\util.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\oromatcher.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\annotations.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\snappy-in-java-0.3.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\trove4j.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\jna-platform.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\jdom.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\picocontainer.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\lucene-core-2.4.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven-server-api.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-common.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-lib\\archetype-catalog-2.2.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-lib\\archetype-common-2.2.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-lib\\maven-dependency-tree-1.2.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-lib\\nexus-indexer-3.0.4.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-lib\\nexus-indexer-artifact-1.0.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven30-server-impl.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\aether-api-1.13.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\aether-connector-wagon-1.13.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\aether-impl-1.13.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\aether-spi-1.13.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\aether-util-1.13.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\commons-cli-1.2.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\commons-io-2.2.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\commons-lang-2.6.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-aether-provider-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-artifact-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-compat-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-core-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-embedder-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-model-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-model-builder-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-plugin-api-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-repository-metadata-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-settings-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-settings-builder-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\plexus-cipher-1.7.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\plexus-component-annotations-1.5.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\plexus-interpolation-1.14.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\plexus-sec-dispatcher-1.3.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\plexus-utils-2.0.6.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\sisu-guava-0.9.9.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\sisu-guice-3.1.0-no_aop.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\sisu-inject-bean-2.3.0.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\sisu-inject-plexus-2.3.0.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\wagon-file-2.8.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\wagon-http-2.8-shaded.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\wagon-http-shared-2.8.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\wagon-provider-api-2.8.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\boot\\plexus-classworlds-2.4.jar\" org.jetbrains.idea.maven.server.RemoteMavenServer");
        processInfo5 = new ProcessInfo("8160","00:00:00","java.exe","Denis","3596832","461036","3","15:18:48","8", "\"C:\\Program Files\\Java\\jdk1.8.0_60\\bin\\java\" -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:53995,suspend=y,server=n -ea -Didea.junit.sm_runner -Dfile.encoding=UTF-8 -classpath \"C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\idea_rt.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\junit\\lib\\junit-rt.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\charsets.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\deploy.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\javaws.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\jce.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\jfr.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\jfxswt.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\jsse.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\management-agent.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\plugin.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\resources.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\access-bridge-64.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\cldrdata.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\dnsns.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\jaccess.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\jfxrt.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\localedata.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\nashorn.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\sunec.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\sunjce_provider.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\sunmscapi.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\sunpkcs11.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\zipfs.jar;D:\\dev\\jProcesses\\target\\test-classes;D:\\dev\\jProcesses\\target\\classes;C:\\Users\\Denis\\.m2\\repository\\junit\\junit\\4.10\\junit-4.10.jar;C:\\Users\\Denis\\.m2\\repository\\org\\hamcrest\\hamcrest-core\\1.1\\hamcrest-core-1.1.jar;C:\\Users\\Denis\\.m2\\repository\\com\\profesorfalken\\WMI4Java\\1.1\\WMI4Java-1.1.jar;C:\\Users\\Denis\\.m2\\repository\\com\\profesorfalken\\jPowerShell\\1.3\\jPowerShell-1.3.jar;C:\\Users\\Denis\\.m2\\repository\\org\\mockito\\mockito-all\\1.8.4\\mockito-all-1.8.4.jar\" com.intellij.rt.execution.junit.JUnitStarter -ideVersion5 org.jutils.jprocesses.info.WindowsProcessesServiceTest,testGetListWithName");
        when(wmi4Java.getRawWMIObjectOutput(eq(WMIClass.WIN32_PROCESS))).thenReturn(WMI_PROCESS1 + WMI_PROCESS2 + WMI_PROCESS3 + WMI_PROCESS4 + WMI_PROCESS5);
        when(wmi4Java.getRawWMIObjectOutput(eq(WMIClass.WIN32_PERFFORMATTEDDATA_PERFPROC_PROCESS))).thenReturn(WMI_PROCESS1_PERF + WMI_PROCESS2_PERF + WMI_PROCESS3_PERF + WMI_PROCESS4_PERF + WMI_PROCESS5_PERF);
        when(wmi4Java.VBSEngine()).thenReturn(wmi4Java);
    }
    /**
     * Test of getProcessList method, of class JProcesses.
     */
    @Test
    public void testGetList() {

        List<ProcessInfo> list = srv.getList();
        assertEquals(5,list.size());
        assertTrue(list.contains(processInfo1));
        assertTrue(list.contains(processInfo2));
        assertTrue(list.contains(processInfo3));
        assertTrue(list.contains(processInfo4));
        assertTrue(list.contains(processInfo5));

    }

    @Test
    public void testGetListWithName() {

        List<ProcessInfo> list = srv.getList("java.exe");
        assertEquals(2,list.size());
        assertTrue(list.contains(processInfo4));
        assertTrue(list.contains(processInfo5));
    }

    private static final String WMI_PROCESS1 = "Caption: idea64.exe\n" +
            "CommandLine: \"C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\bin\\idea64.exe\" \n" +
            "CreationClassName: Win32_Process\n" +
            "CreationDate: 20160514135815.444566+180\n" +
            "CSCreationClassName: Win32_ComputerSystem\n" +
            "CSName: DENIS-PC\n" +
            "Description: idea64.exe\n" +
            "ExecutablePath: C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\bin\\idea64.exe\n" +
            "ExecutionState: \n" +
            "Handle: 3644\n" +
            "HandleCount: 910\n" +
            "InstallDate: \n" +
            "KernelModeTime: 524007359\n" +
            "MaximumWorkingSetSize: 1380\n" +
            "MinimumWorkingSetSize: 200\n" +
            "Name: idea64.exe\n" +
            "OSCreationClassName: Win32_OperatingSystem\n" +
            "OSName: Microsoft Windows 7 Ultimate |C:\\Windows|\\Device\\Harddisk0\\Partition2\n" +
            "OtherOperationCount: 1934035\n" +
            "OtherTransferCount: 35525870\n" +
            "PageFaults: 2541972\n" +
            "PageFileUsage: 1117360\n" +
            "ParentProcessId: 500\n" +
            "PeakPageFileUsage: 1133232\n" +
            "PeakVirtualSize: 4297838592\n" +
            "PeakWorkingSetSize: 1071420\n" +
            "Priority: 8\n" +
            "PrivatePageCount: 1144176640\n" +
            "ProcessId: 3644\n" +
            "QuotaNonPagedPoolUsage: 75\n" +
            "QuotaPagedPoolUsage: 304\n" +
            "QuotaPeakNonPagedPoolUsage: 81\n" +
            "QuotaPeakPagedPoolUsage: 326\n" +
            "ReadOperationCount: 178223\n" +
            "ReadTransferCount: 661366634\n" +
            "SessionId: 1\n" +
            "Status: \n" +
            "TerminationDate: \n" +
            "ThreadCount: 64\n" +
            "UserModeTime: 5684832441\n" +
            "VirtualSize: 4289159168\n" +
            "WindowsVersion: 6.1.7601\n" +
            "WorkingSetSize: 1082019840\n" +
            "WriteOperationCount: 4141\n" +
            "WriteTransferCount: 1499671310\n";

    private static final String WMI_PROCESS2 = "Caption: DOOMx64.exe\n" +
            "CommandLine: \"D:\\SteamLibrary\\steamapps\\common\\DOOM\\DOOMx64.exe\"\n" +
            "CreationClassName: Win32_Process\n" +
            "CreationDate: 20160514144712.680566+180\n" +
            "CSCreationClassName: Win32_ComputerSystem\n" +
            "CSName: DENIS-PC\n" +
            "Description: DOOMx64.exe\n" +
            "ExecutablePath: D:\\SteamLibrary\\steamapps\\common\\DOOM\\DOOMx64.exe\n" +
            "ExecutionState:\n" +
            "Handle: 6936\n" +
            "HandleCount: 1110\n" +
            "InstallDate:\n" +
            "KernelModeTime: 709960551\n" +
            "MaximumWorkingSetSize: 628680\n" +
            "MinimumWorkingSetSize: 628568\n" +
            "Name: DOOMx64.exe\n" +
            "OSCreationClassName: Win32_OperatingSystem\n" +
            "OSName: Microsoft Windows 7 Ultimate |C:\\Windows|\\Device\\Harddisk0\\Partition2\n" +
            "OtherOperationCount: 10494\n" +
            "OtherTransferCount: 109196\n" +
            "PageFaults: 386552\n" +
            "PageFileUsage: 1585468\n" +
            "ParentProcessId: 2548\n" +
            "PeakPageFileUsage: 1585476\n" +
            "PeakVirtualSize: 2490068992\n" +
            "PeakWorkingSetSize: 1023348\n" +
            "Priority: 8\n" +
            "PrivatePageCount: 1623519232\n" +
            "ProcessId: 6936\n" +
            "QuotaNonPagedPoolUsage: 128\n" +
            "QuotaPagedPoolUsage: 3330\n" +
            "QuotaPeakNonPagedPoolUsage: 128\n" +
            "QuotaPeakPagedPoolUsage: 3340\n" +
            "ReadOperationCount: 2376\n" +
            "ReadTransferCount: 2433886942\n" +
            "SessionId: 1\n" +
            "Status:\n" +
            "TerminationDate:\n" +
            "ThreadCount: 33\n" +
            "UserModeTime: 857069494\n" +
            "VirtualSize: 2487513088\n" +
            "WindowsVersion: 6.1.7601\n" +
            "WorkingSetSize: 1047908352\n" +
            "WriteOperationCount: 210\n" +
            "WriteTransferCount: 11894\n";
    private static final String WMI_PROCESS3 = "Caption: chrome.exe\n" +
            "CommandLine: \"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe\" --no-startup-window /prefetch:5\n" +
            "CreationClassName: Win32_Process\n" +
            "CreationDate: 20160514115400.098497+180\n" +
            "CSCreationClassName: Win32_ComputerSystem\n" +
            "CSName: DENIS-PC\n" +
            "Description: chrome.exe\n" +
            "ExecutablePath: C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe\n" +
            "ExecutionState:\n" +
            "Handle: 2876\n" +
            "HandleCount: 1265\n" +
            "InstallDate:\n" +
            "KernelModeTime: 661912243\n" +
            "MaximumWorkingSetSize: 1380\n" +
            "MinimumWorkingSetSize: 200\n" +
            "Name: chrome.exe\n" +
            "OSCreationClassName: Win32_OperatingSystem\n" +
            "OSName: Microsoft Windows 7 Ultimate |C:\\Windows|\\Device\\Harddisk0\\Partition2\n" +
            "OtherOperationCount: 493715\n" +
            "OtherTransferCount: 18990227\n" +
            "PageFaults: 1548415\n" +
            "PageFileUsage: 104732\n" +
            "ParentProcessId: 500\n" +
            "PeakPageFileUsage: 138056\n" +
            "PeakVirtualSize: 764755968\n" +
            "PeakWorkingSetSize: 171256\n" +
            "Priority: 8\n" +
            "PrivatePageCount: 107245568\n" +
            "ProcessId: 2876\n" +
            "QuotaNonPagedPoolUsage: 65\n" +
            "QuotaPagedPoolUsage: 852\n" +
            "QuotaPeakNonPagedPoolUsage: 232\n" +
            "QuotaPeakPagedPoolUsage: 1247\n" +
            "ReadOperationCount: 1549009\n" +
            "ReadTransferCount: 1148378511\n" +
            "SessionId: 1\n" +
            "Status:\n" +
            "TerminationDate:\n" +
            "ThreadCount: 39\n" +
            "UserModeTime: 1948764492\n" +
            "VirtualSize: 567984128\n" +
            "WindowsVersion: 6.1.7601\n" +
            "WorkingSetSize: 144740352\n" +
            "WriteOperationCount: 1793811\n" +
            "WriteTransferCount: 1503510606\n";
    private static final String WMI_PROCESS4 = "Caption: java.exe\n" +
            "CommandLine: \"C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\bin\\java\" -Djava.awt.headless=true -Didea.version==2016.1.2 -Xmx512m -Didea.maven.embedder.version=3.0.5 -Dfile.encoding=windows-1251 -classpath \"C:/Program Files (x86)/JetBrains/IntelliJ IDEA Community Edition 2016.1/lib/resources_en.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\log4j.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\slf4j-api-1.7.10.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\slf4j-log4j12-1.7.10.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\jna.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\resources_en.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\util.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\oromatcher.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\annotations.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\snappy-in-java-0.3.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\trove4j.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\jna-platform.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\jdom.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\picocontainer.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\lucene-core-2.4.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven-server-api.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-common.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-lib\\archetype-catalog-2.2.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-lib\\archetype-common-2.2.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-lib\\maven-dependency-tree-1.2.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-lib\\nexus-indexer-3.0.4.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3-server-lib\\nexus-indexer-artifact-1.0.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven30-server-impl.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\aether-api-1.13.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\aether-connector-wagon-1.13.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\aether-impl-1.13.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\aether-spi-1.13.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\aether-util-1.13.1.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\commons-cli-1.2.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\commons-io-2.2.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\commons-lang-2.6.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-aether-provider-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-artifact-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-compat-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-core-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-embedder-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-model-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-model-builder-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-plugin-api-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-repository-metadata-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-settings-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\maven-settings-builder-3.0.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\plexus-cipher-1.7.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\plexus-component-annotations-1.5.5.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\plexus-interpolation-1.14.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\plexus-sec-dispatcher-1.3.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\plexus-utils-2.0.6.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\sisu-guava-0.9.9.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\sisu-guice-3.1.0-no_aop.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\sisu-inject-bean-2.3.0.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\sisu-inject-plexus-2.3.0.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\wagon-file-2.8.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\wagon-http-2.8-shaded.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\wagon-http-shared-2.8.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\lib\\wagon-provider-api-2.8.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\maven\\lib\\maven3\\boot\\plexus-classworlds-2.4.jar\" org.jetbrains.idea.maven.server.RemoteMavenServer\n" +
            "CreationClassName: Win32_Process\n" +
            "CreationDate: 20160514135845.836304+180\n" +
            "CSCreationClassName: Win32_ComputerSystem\n" +
            "CSName: DENIS-PC\n" +
            "Description: java.exe\n" +
            "ExecutablePath: C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\bin\\java.exe\n" +
            "ExecutionState: \n" +
            "Handle: 6700\n" +
            "HandleCount: 324\n" +
            "InstallDate: \n" +
            "KernelModeTime: 1716011\n" +
            "MaximumWorkingSetSize: 1380\n" +
            "MinimumWorkingSetSize: 200\n" +
            "Name: java.exe\n" +
            "OSCreationClassName: Win32_OperatingSystem\n" +
            "OSName: Microsoft Windows 7 Ultimate |C:\\Windows|\\Device\\Harddisk0\\Partition2\n" +
            "OtherOperationCount: 12619\n" +
            "OtherTransferCount: 274107\n" +
            "PageFaults: 34915\n" +
            "PageFileUsage: 240784\n" +
            "ParentProcessId: 3644\n" +
            "PeakPageFileUsage: 245768\n" +
            "PeakVirtualSize: 2058690560\n" +
            "PeakWorkingSetSize: 94992\n" +
            "Priority: 8\n" +
            "PrivatePageCount: 246562816\n" +
            "ProcessId: 6700\n" +
            "QuotaNonPagedPoolUsage: 22\n" +
            "QuotaPagedPoolUsage: 169\n" +
            "QuotaPeakNonPagedPoolUsage: 26\n" +
            "QuotaPeakPagedPoolUsage: 171\n" +
            "ReadOperationCount: 9873\n" +
            "ReadTransferCount: 11487969\n" +
            "SessionId: 1\n" +
            "Status: \n" +
            "TerminationDate: \n" +
            "ThreadCount: 24\n" +
            "UserModeTime: 18252117\n" +
            "VirtualSize: 2054496256\n" +
            "WindowsVersion: 6.1.7601\n" +
            "WorkingSetSize: 85766144\n" +
            "WriteOperationCount: 118\n" +
            "WriteTransferCount: 3848\n";

    private static final String WMI_PROCESS5 = "Caption: java.exe\n" +
            "CommandLine: \"C:\\Program Files\\Java\\jdk1.8.0_60\\bin\\java\" -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:53995,suspend=y,server=n -ea -Didea.junit.sm_runner -Dfile.encoding=UTF-8 -classpath \"C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\lib\\idea_rt.jar;C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA Community Edition 2016.1\\plugins\\junit\\lib\\junit-rt.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\charsets.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\deploy.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\javaws.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\jce.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\jfr.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\jfxswt.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\jsse.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\management-agent.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\plugin.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\resources.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\access-bridge-64.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\cldrdata.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\dnsns.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\jaccess.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\jfxrt.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\localedata.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\nashorn.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\sunec.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\sunjce_provider.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\sunmscapi.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\sunpkcs11.jar;C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\ext\\zipfs.jar;D:\\dev\\jProcesses\\target\\test-classes;D:\\dev\\jProcesses\\target\\classes;C:\\Users\\Denis\\.m2\\repository\\junit\\junit\\4.10\\junit-4.10.jar;C:\\Users\\Denis\\.m2\\repository\\org\\hamcrest\\hamcrest-core\\1.1\\hamcrest-core-1.1.jar;C:\\Users\\Denis\\.m2\\repository\\com\\profesorfalken\\WMI4Java\\1.1\\WMI4Java-1.1.jar;C:\\Users\\Denis\\.m2\\repository\\com\\profesorfalken\\jPowerShell\\1.3\\jPowerShell-1.3.jar;C:\\Users\\Denis\\.m2\\repository\\org\\mockito\\mockito-all\\1.8.4\\mockito-all-1.8.4.jar\" com.intellij.rt.execution.junit.JUnitStarter -ideVersion5 org.jutils.jprocesses.info.WindowsProcessesServiceTest,testGetListWithName\n" +
            "CreationClassName: Win32_Process\n" +
            "CreationDate: 20160514151848.264987+180\n" +
            "CSCreationClassName: Win32_ComputerSystem\n" +
            "CSName: DENIS-PC\n" +
            "Description: java.exe\n" +
            "ExecutablePath: C:\\Program Files\\Java\\jdk1.8.0_60\\bin\\java.exe\n" +
            "ExecutionState: \n" +
            "Handle: 8160\n" +
            "HandleCount: 252\n" +
            "InstallDate: \n" +
            "KernelModeTime: 2808018\n" +
            "MaximumWorkingSetSize: 1380\n" +
            "MinimumWorkingSetSize: 200\n" +
            "Name: java.exe\n" +
            "OSCreationClassName: Win32_OperatingSystem\n" +
            "OSName: Microsoft Windows 7 Ultimate |C:\\Windows|\\Device\\Harddisk0\\Partition2\n" +
            "OtherOperationCount: 7038\n" +
            "OtherTransferCount: 163028\n" +
            "PageFaults: 118732\n" +
            "PageFileUsage: 892380\n" +
            "ParentProcessId: 3644\n" +
            "PeakPageFileUsage: 894172\n" +
            "PeakVirtualSize: 3683155968\n" +
            "PeakWorkingSetSize: 462196\n" +
            "Priority: 8\n" +
            "PrivatePageCount: 913797120\n" +
            "ProcessId: 8160\n" +
            "QuotaNonPagedPoolUsage: 20\n" +
            "QuotaPagedPoolUsage: 178\n" +
            "QuotaPeakNonPagedPoolUsage: 119\n" +
            "QuotaPeakPagedPoolUsage: 178\n" +
            "ReadOperationCount: 4176\n" +
            "ReadTransferCount: 6494968\n" +
            "SessionId: 1\n" +
            "Status: \n" +
            "TerminationDate: \n" +
            "ThreadCount: 20\n" +
            "UserModeTime: 8268053\n" +
            "VirtualSize: 3683155968\n" +
            "WindowsVersion: 6.1.7601\n" +
            "WorkingSetSize: 472100864\n" +
            "WriteOperationCount: 14\n" +
            "WriteTransferCount: 6635\n";

    private static final String WMI_PROCESS1_PERF ="Caption: \n" +
            "CreatingProcessID: 2548\n" +
            "Description: \n" +
            "ElapsedTime: 361\n" +
            "Frequency_Object: \n" +
            "Frequency_PerfTime: \n" +
            "Frequency_Sys100NS: \n" +
            "HandleCount: 1739\n" +
            "IDProcess: 6936\n" +
            "IODataBytesPersec: 3631675\n" +
            "IODataOperationsPersec: 265\n" +
            "IOOtherBytesPersec: 949\n" +
            "IOOtherOperationsPersec: 296\n" +
            "IOReadBytesPersec: 3630607\n" +
            "IOReadOperationsPersec: 146\n" +
            "IOWriteBytesPersec: 1068\n" +
            "IOWriteOperationsPersec: 118\n" +
            "Name: DOOMx64\n" +
            "PageFaultsPersec: 0\n" +
            "PageFileBytes: 2646224896\n" +
            "PageFileBytesPeak: 2648330240\n" +
            "PercentPrivilegedTime: 67\n" +
            "PercentProcessorTime: 100\n" +
            "PercentUserTime: 80\n" +
            "PoolNonpagedBytes: 301176\n" +
            "PoolPagedBytes: 2782520\n" +
            "PriorityBase: 8\n" +
            "PrivateBytes: 2646224896\n" +
            "ThreadCount: 36\n" +
            "Timestamp_Object: \n" +
            "Timestamp_PerfTime: \n" +
            "Timestamp_Sys100NS: \n" +
            "VirtualBytes: 3179716608\n" +
            "VirtualBytesPeak: 3231305728\n" +
            "WorkingSet: 2166816768\n" +
            "WorkingSetPeak: 2166833152\n" +
            "WorkingSetPrivate: 1682055168\n";
    private static final String WMI_PROCESS2_PERF = "Caption: \n" +
            "CreatingProcessID: 500\n" +
            "Description: \n" +
            "ElapsedTime: 3298\n" +
            "Frequency_Object: \n" +
            "Frequency_PerfTime: \n" +
            "Frequency_Sys100NS: \n" +
            "HandleCount: 917\n" +
            "IDProcess: 3644\n" +
            "IODataBytesPersec: 1867\n" +
            "IODataOperationsPersec: 11\n" +
            "IOOtherBytesPersec: 13672\n" +
            "IOOtherOperationsPersec: 854\n" +
            "IOReadBytesPersec: 1867\n" +
            "IOReadOperationsPersec: 11\n" +
            "IOWriteBytesPersec: 0\n" +
            "IOWriteOperationsPersec: 0\n" +
            "Name: idea64\n" +
            "PageFaultsPersec: 3\n" +
            "PageFileBytes: 1155874816\n" +
            "PageFileBytesPeak: 1166286848\n" +
            "PercentPrivilegedTime: 0\n" +
            "PercentProcessorTime: 6\n" +
            "PercentUserTime: 6\n" +
            "PoolNonpagedBytes: 74096\n" +
            "PoolPagedBytes: 310792\n" +
            "PriorityBase: 8\n" +
            "PrivateBytes: 1155874816\n" +
            "ThreadCount: 65\n" +
            "Timestamp_Object: \n" +
            "Timestamp_PerfTime: \n" +
            "Timestamp_Sys100NS: \n" +
            "VirtualBytes: 4290207744\n" +
            "VirtualBytesPeak: 4300398592\n" +
            "WorkingSet: 1061761024\n" +
            "WorkingSetPeak: 1097666560\n" +
            "WorkingSetPrivate: 1050238976\n";
    private static final String WMI_PROCESS3_PERF = "Caption: \n" +
            "CreatingProcessID: 500\n" +
            "Description: \n" +
            "ElapsedTime: 10753\n" +
            "Frequency_Object: \n" +
            "Frequency_PerfTime: \n" +
            "Frequency_Sys100NS: \n" +
            "HandleCount: 1259\n" +
            "IDProcess: 2876\n" +
            "IODataBytesPersec: 236304\n" +
            "IODataOperationsPersec: 522\n" +
            "IOOtherBytesPersec: 443\n" +
            "IOOtherOperationsPersec: 106\n" +
            "IOReadBytesPersec: 107811\n" +
            "IOReadOperationsPersec: 174\n" +
            "IOWriteBytesPersec: 128493\n" +
            "IOWriteOperationsPersec: 348\n" +
            "Name: chrome\n" +
            "PageFaultsPersec: 0\n" +
            "PageFileBytes: 107134976\n" +
            "PageFileBytesPeak: 141369344\n" +
            "PercentPrivilegedTime: 0\n" +
            "PercentProcessorTime: 0\n" +
            "PercentUserTime: 0\n" +
            "PoolNonpagedBytes: 63448\n" +
            "PoolPagedBytes: 870848\n" +
            "PriorityBase: 8\n" +
            "PrivateBytes: 107134976\n" +
            "ThreadCount: 37\n" +
            "Timestamp_Object: \n" +
            "Timestamp_PerfTime: \n" +
            "Timestamp_Sys100NS: \n" +
            "VirtualBytes: 565362688\n" +
            "VirtualBytesPeak: 764755968\n" +
            "WorkingSet: 132898816\n" +
            "WorkingSetPeak: 175366144\n" +
            "WorkingSetPrivate: 86532096\n";
    private static final String WMI_PROCESS4_PERF = "Caption: \n" +
            "CreatingProcessID: 3644\n" +
            "Description: \n" +
            "ElapsedTime: 4808\n" +
            "Frequency_Object: \n" +
            "Frequency_PerfTime: \n" +
            "Frequency_Sys100NS: \n" +
            "HandleCount: 325\n" +
            "IDProcess: 6700\n" +
            "IODataBytesPersec: 0\n" +
            "IODataOperationsPersec: 0\n" +
            "IOOtherBytesPersec: 0\n" +
            "IOOtherOperationsPersec: 0\n" +
            "IOReadBytesPersec: 0\n" +
            "IOReadOperationsPersec: 0\n" +
            "IOWriteBytesPersec: 0\n" +
            "IOWriteOperationsPersec: 0\n" +
            "Name: java\n" +
            "PageFaultsPersec: 0\n" +
            "PageFileBytes: 246562816\n" +
            "PageFileBytesPeak: 251666432\n" +
            "PercentPrivilegedTime: 0\n" +
            "PercentProcessorTime: 2\n" +
            "PercentUserTime: 0\n" +
            "PoolNonpagedBytes: 23296\n" +
            "PoolPagedBytes: 172744\n" +
            "PriorityBase: 8\n" +
            "PrivateBytes: 246562816\n" +
            "ThreadCount: 24\n" +
            "Timestamp_Object: \n" +
            "Timestamp_PerfTime: \n" +
            "Timestamp_Sys100NS: \n" +
            "VirtualBytes: 2054496256\n" +
            "VirtualBytesPeak: 2058690560\n" +
            "WorkingSet: 85757952\n" +
            "WorkingSetPeak: 97271808\n" +
            "WorkingSetPrivate: 80453632\n";
    private static final String WMI_PROCESS5_PERF = "Caption: \n" +
            "CreatingProcessID: 3644\n" +
            "Description: \n" +
            "ElapsedTime: 5\n" +
            "Frequency_Object: \n" +
            "Frequency_PerfTime: \n" +
            "Frequency_Sys100NS: \n" +
            "HandleCount: 245\n" +
            "IDProcess: 8160\n" +
            "IODataBytesPersec: 0\n" +
            "IODataOperationsPersec: 0\n" +
            "IOOtherBytesPersec: 0\n" +
            "IOOtherOperationsPersec: 0\n" +
            "IOReadBytesPersec: 0\n" +
            "IOReadOperationsPersec: 0\n" +
            "IOWriteBytesPersec: 0\n" +
            "IOWriteOperationsPersec: 0\n" +
            "Name: java#1\n" +
            "PageFaultsPersec: 0\n" +
            "PageFileBytes: 250654720\n" +
            "PageFileBytesPeak: 255041536\n" +
            "PercentPrivilegedTime: 0\n" +
            "PercentProcessorTime: 3\n" +
            "PercentUserTime: 0\n" +
            "PoolNonpagedBytes: 19704\n" +
            "PoolPagedBytes: 179368\n" +
            "PriorityBase: 8\n" +
            "PrivateBytes: 250654720\n" +
            "ThreadCount: 20\n" +
            "Timestamp_Object: \n" +
            "Timestamp_PerfTime: \n" +
            "Timestamp_Sys100NS: \n" +
            "VirtualBytes: 3683155968\n" +
            "VirtualBytesPeak: 3683155968\n" +
            "WorkingSet: 40710144\n" +
            "WorkingSetPeak: 45494272\n" +
            "WorkingSetPrivate: 30330880\n";

}
