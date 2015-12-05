/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jutils.jprocesses.jni;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author javier
 */
public class ProcessesDataCollectorTest {
    
    public ProcessesDataCollectorTest() {
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
     * Test of getProcessesData method, of class ProcessesDataCollector.
     */
    @Test
    public void testGetProcessesData() {
        ProcessesDataCollector instance = ProcessesDataCollector.INSTANCE;
        String result = instance.getProcessesData();        
        
        assertTrue(result != null);
        
        System.out.println("Processes data output: \n" + result);
    }

    /**
     * Test of killProcess method, of class ProcessesDataCollector.
     */
    @Test
    public void testKillProcess() {
        //Not tested
    }
    
}
