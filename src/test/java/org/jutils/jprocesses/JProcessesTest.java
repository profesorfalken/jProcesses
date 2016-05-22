package org.jutils.jprocesses;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jutils.jprocesses.model.ProcessInfo;
import org.jutils.jprocesses.model.WindowsPriority;
import org.jutils.jprocesses.util.OSDetector;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author javier
 */
public class JProcessesTest {

    public JProcessesTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getProcessList method, of class JProcesses.
     */
    @Test
    public void testGetProcessList() {
        System.out.println("===============Testing getProcessList============");
        List<ProcessInfo> processesList = JProcesses.get().fastMode().listProcesses();

        assertTrue(processesList != null && processesList.size() > 0);

        for (final ProcessInfo processInfo : processesList) {
            System.out.println("Process PID: " + processInfo.getPid());
            System.out.println("Process Name: " + processInfo.getName());
            System.out.println("Process Time: " + processInfo.getTime());
            System.out.println("User: " + processInfo.getUser());
            System.out.println("Virtual Memory: " + processInfo.getVirtualMemory());
            System.out.println("Physical Memory: " + processInfo.getPhysicalMemory());
            System.out.println("CPU usage: " + processInfo.getCpuUsage());
            System.out.println("Start Time: " + processInfo.getStartTime());
            System.out.println("Start DateTime: "
                    + processInfo.getExtraData().get("start_datetime"));
            System.out.println("Priority: " + processInfo.getPriority());
            System.out.println("Full command: " + processInfo.getCommand());
            System.out.println("------------------");
        }
        System.out.println("===============End test getProcessList============");
    }

    /**
     * Test of getProcessList method by name, of class JProcesses.
     */
    @Test
    public void testGetProcessListByName() {
        System.out.println("===============Testing getProcessList by name============");
        //JProcesses.fastMode = true;
        String processToSearch = "java";
        if (OSDetector.isWindows()) {
            processToSearch += ".exe";
        }

        List<ProcessInfo> processesList = JProcesses.getProcessList(processToSearch);

        assertTrue(processesList != null && processesList.size() > 0);

        for (final ProcessInfo processInfo : processesList) {
            System.out.println("Process PID: " + processInfo.getPid());
            System.out.println("Process Name: " + processInfo.getName());
            System.out.println("Process Time: " + processInfo.getTime());
            System.out.println("User: " + processInfo.getUser());
            System.out.println("Virtual Memory: " + processInfo.getVirtualMemory());
            System.out.println("Physical Memory: " + processInfo.getPhysicalMemory());
            System.out.println("CPU usage: " + processInfo.getCpuUsage());
            System.out.println("Start Time: " + processInfo.getStartTime());
            System.out.println("Priority: " + processInfo.getPriority());
            System.out.println("Command: " + processInfo.getCommand());
            System.out.println("------------------");
        }

        //Compare list with a manually retrieved list
        List<ProcessInfo> processesListFull = JProcesses.getProcessList();
        List<ProcessInfo> processesListFound = new ArrayList<ProcessInfo>();
        for (final ProcessInfo process : processesListFull) {
            if (processToSearch.equals(process.getName())) {
                processesListFound.add(process);
            }
        }

        assertTrue("Manually list differs from search founded " 
                + processesListFound.size() + " instead of " + processesList.size(), 
                processesList.size() == processesListFound.size()
        );
        
        System.out.println("===============End test getProcessList by name============");
    }

    /**
     * Test of getProcessList method by name, of class JProcesses.
     */
    //@Test
    public void testKill() {
        System.out.println("===============Testing killProcess============");
        boolean success = JProcesses.killProcess(3844).isSuccess();

        System.out.println("===============End test killProcess============");
    }

    /**
     * Test of getProcessList method by name, of class JProcesses.
     */
    //@Test
    public void testChangePriority() {
        System.out.println("===============Testing changePriority============");
        boolean ok = JProcesses.changePriority(3260, WindowsPriority.HIGH).isSuccess();
        assertTrue(ok);

        ProcessInfo process = JProcesses.getProcess(3260);
        assertTrue(String.valueOf(13).equals(process.getPriority()));

        ok = JProcesses.changePriority(3260, WindowsPriority.NORMAL).isSuccess();
        assertTrue(ok);

        process = JProcesses.getProcess(3260);
        assertTrue(String.valueOf(8).equals(process.getPriority()));

        System.out.println("===============End test changePriority============");
    }
}
