package org.jutils.jprocesses;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.jutils.jprocesses.model.ProcessInfo;

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
        List<ProcessInfo> processesList = JProcesses.getProcessList();

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
        List<ProcessInfo> processesList = JProcesses.getProcessList("java");

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
        System.out.println("===============End test getProcessList by name============");
    }
    
    /**
     * Test of getProcessList method by name, of class JProcesses.
     */
    @Test
    public void testChangePriority() {
        System.out.println("===============Testing changePriority============");
        boolean ok = JProcesses.changePriority(2689, 5);
        assertTrue(ok);
        
        ProcessInfo process = JProcesses.getProcess(2689);
        assertTrue("5".equals(process.getPriority()));

        ok = JProcesses.changePriority(2689, 7);
        assertTrue(ok);
        
        process = JProcesses.getProcess(2689);
        assertTrue("7".equals(process.getPriority()));

        System.out.println("===============End test changePriority============");
    }
}