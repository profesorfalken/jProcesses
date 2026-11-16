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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jutils.jprocesses.model.ProcessInfo;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UnixProcessesServiceTest {

    private static final String PS_OUTPUT
            = "    PID RUSER      VSZ   RSS %CPU STARTED                     TIME  NI COMMAND\n"
            + "    123 javier   10000  2000  1.5 Thu Nov 12 19:41:24 2026 00:00:05   0 myproc\n"
            + "    456 root      2048  1024  0.0 Thu Nov 12 19:41:24 2026 00:00:00   0 other proc\n";

    private static final String FULL_COMMAND_OUTPUT
            = "    PID COMMAND\n"
            + "    123 /usr/bin/myproc --flag\n"
            + "    456 /usr/bin/other proc\n";

    private Locale originalLocale;

    @Before
    public void setUp() {
        originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }

    @After
    public void tearDown() {
        Locale.setDefault(originalLocale);
    }

    @Test
    public void parsesValidProcessLines() {
        TestService service = new TestService(PS_OUTPUT, FULL_COMMAND_OUTPUT);

        List<ProcessInfo> processes = service.getList();

        assertEquals(2, processes.size());

        ProcessInfo first = processes.get(0);
        assertEquals("123", first.getPid());
        assertEquals("javier", first.getUser());
        assertEquals("10000", first.getVirtualMemory());
        assertEquals("2000", first.getPhysicalMemory());
        assertEquals("1.5", first.getCpuUsage());
        assertEquals("19:41:24", first.getStartTime());
        assertEquals("00:00:05", first.getTime());
        assertEquals("0", first.getPriority());
        assertEquals("myproc", first.getName());
        assertEquals("/usr/bin/myproc --flag", first.getCommand());
        assertEquals("11/12/2026 19:41:24",
                first.getExtraData().get("start_datetime"));

        //Names containing spaces must be kept as a single field
        assertEquals("other proc", processes.get(1).getName());
    }

    @Test
    public void emptyAndUnexpectedOutputIsIgnored() {
        TestService service = new TestService(PS_OUTPUT, FULL_COMMAND_OUTPUT);

        assertTrue(service.parseList("").isEmpty());
        assertTrue(service.parseList("\n\n").isEmpty());
        assertTrue(service.parseList("    PID RUSER\n").isEmpty());
        assertTrue(service.parseList("error: process ID out of range\n").isEmpty());
        assertTrue(service.parseList("123 javier 10000\n").isEmpty());
    }

    @Test
    public void invalidPidsAreRejected() {
        TestService service = new TestService(PS_OUTPUT, FULL_COMMAND_OUTPUT);

        try {
            service.getProcess(0);
        } catch (IllegalArgumentException expected) {
            // expected
        }

        try {
            service.killProcess(-1);
            throw new AssertionError("killProcess must reject negative PIDs");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        try {
            service.killProcessGracefully(0);
            throw new AssertionError("killProcessGracefully must reject PID 0");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        try {
            service.changePriority(-5, 5);
            throw new AssertionError("changePriority must reject negative PIDs");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    public void filtersByNameWithoutKeepingState() {
        TestService service = new TestService(PS_OUTPUT, FULL_COMMAND_OUTPUT);

        assertEquals(1, service.getList("myproc").size());
        //A filtered call must not affect later unfiltered calls
        assertEquals(2, service.getList().size());
        assertTrue(service.getList("unexisting").isEmpty());
    }

    @Test
    public void getsSingleProcessInformation() {
        TestService service = new TestService(PS_OUTPUT, FULL_COMMAND_OUTPUT);

        ProcessInfo process = service.getProcess(123);
        assertNotNull(process);
        assertEquals("123", process.getPid());
        assertEquals("myproc", process.getName());

        Map<String, String> extraData = process.getExtraData();
        assertNotNull(extraData);
        assertEquals("11/12/2026 19:41:24", extraData.get("start_datetime"));
    }

    @Test
    public void killAndPriorityUseExitCode() {
        TestService service = new TestService(PS_OUTPUT, FULL_COMMAND_OUTPUT);

        assertTrue(service.killProcess(123).isSuccess());
        assertTrue(service.killProcessGracefully(123).isSuccess());
        assertTrue(service.changePriority(123, 5).isSuccess());
    }

    @Test
    public void missingProcessReturnsNull() {
        TestService service = new TestService("    PID RUSER\n",
                "    PID COMMAND\n");
        assertNull(service.getProcess(999));
    }

    private static final class TestService extends UnixProcessesService {

        private final String psOutput;
        private final String fullCommandOutput;

        private TestService(String psOutput, String fullCommandOutput) {
            this.psOutput = psOutput;
            this.fullCommandOutput = fullCommandOutput;
        }

        @Override
        protected String executeCommand(String... command) {
            for (String part : command) {
                if ("pid,command".equals(part)) {
                    return fullCommandOutput;
                }
            }
            return psOutput;
        }

        @Override
        protected int executeCommandAndGetCode(String... command) {
            return 0;
        }
    }
}
