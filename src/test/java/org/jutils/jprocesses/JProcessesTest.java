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
package org.jutils.jprocesses;

import org.junit.Ignore;
import org.junit.Test;
import org.jutils.jprocesses.model.ProcessInfo;
import org.jutils.jprocesses.model.WindowsPriority;

import java.lang.management.ManagementFactory;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author javier
 */
public class JProcessesTest {

    @Test
    public void testGetProcessList() {
        List<ProcessInfo> processesList = JProcesses.get().fastMode().listProcesses();

        assertNotNull(processesList);
        assertFalse(processesList.isEmpty());

        for (final ProcessInfo processInfo : processesList) {
            assertNotNull(processInfo.getPid());
        }
    }

    @Test
    public void testGetProcessListByName() {
        int selfPid = getSelfPid();
        ProcessInfo self = JProcesses.getProcess(selfPid);
        assertNotNull("The current JVM process must be visible", self);

        List<ProcessInfo> processesList = JProcesses.getProcessList(self.getName());

        assertNotNull(processesList);
        assertFalse(processesList.isEmpty());

        boolean found = false;
        for (final ProcessInfo process : processesList) {
            assertEquals(self.getName(), process.getName());
            if (String.valueOf(selfPid).equals(process.getPid())) {
                found = true;
            }
        }
        assertTrue("The current JVM process must be found by name", found);
    }

    @Test
    public void testGetProcessByPid() {
        int selfPid = getSelfPid();

        ProcessInfo process = JProcesses.getProcess(selfPid);

        assertNotNull(process);
        assertEquals(String.valueOf(selfPid), process.getPid());
    }

    @Test
    public void testGetProcessByPidFastMode() {
        int selfPid = getSelfPid();

        ProcessInfo process = JProcesses.getProcess(selfPid, true);

        assertNotNull(process);
        assertEquals(String.valueOf(selfPid), process.getPid());
    }

    @Test
    public void testInvalidPidIsRejected() {
        assertPidRejected(0);
        assertPidRejected(-1);
    }

    @Test
    @Ignore("Destructive: requires a live PID and, usually, elevated rights")
    public void testKill() {
        boolean success = JProcesses.killProcess(3844).isSuccess();
        assertTrue(success);
    }

    @Test
    @Ignore("Destructive and platform dependent: uses a hardcoded Windows PID")
    public void testChangePriority() {
        boolean ok = JProcesses.changePriority(3260, WindowsPriority.HIGH).isSuccess();
        assertTrue(ok);

        ProcessInfo process = JProcesses.getProcess(3260);
        assertTrue(String.valueOf(13).equals(process.getPriority()));

        ok = JProcesses.changePriority(3260, WindowsPriority.NORMAL).isSuccess();
        assertTrue(ok);

        process = JProcesses.getProcess(3260);
        assertTrue(String.valueOf(8).equals(process.getPriority()));
    }

    private void assertPidRejected(int pid) {
        try {
            JProcesses.getProcess(pid);
            throw new AssertionError("getProcess must reject PID " + pid);
        } catch (IllegalArgumentException expected) {
            // expected
        }
        try {
            JProcesses.killProcess(pid);
            throw new AssertionError("killProcess must reject PID " + pid);
        } catch (IllegalArgumentException expected) {
            // expected
        }
        try {
            JProcesses.killProcessGracefully(pid);
            throw new AssertionError("killProcessGracefully must reject PID " + pid);
        } catch (IllegalArgumentException expected) {
            // expected
        }
        try {
            JProcesses.changePriority(pid, 5);
            throw new AssertionError("changePriority must reject PID " + pid);
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    private static int getSelfPid() {
        String runtimeName = ManagementFactory.getRuntimeMXBean().getName();
        return Integer.parseInt(runtimeName.substring(0, runtimeName.indexOf('@')));
    }
}
