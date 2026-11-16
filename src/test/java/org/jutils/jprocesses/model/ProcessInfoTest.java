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
package org.jutils.jprocesses.model;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ProcessInfoTest {

    @Test
    public void equalsAndHashCodeUseAllFields() {
        ProcessInfo first = process("100", "java", "javier", "10", "20", "1.5", "8");
        ProcessInfo second = process("100", "java", "javier", "10", "20", "1.5", "8");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setCpuUsage("2.5");
        assertFalse(first.equals(second));

        second = process("100", "java", "javier", "10", "20", "1.5", "8");
        second.setUser("other");
        assertFalse(first.equals(second));

        second = process("100", "java", "javier", "10", "20", "1.5", "8");
        second.setVirtualMemory("11");
        assertFalse(first.equals(second));
    }

    @Test
    public void isSerializable() throws Exception {
        ProcessInfo original = process("100", "java", "javier", "10", "20", "1.5", "8");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(original);
        output.close();

        ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()));
        ProcessInfo copy = (ProcessInfo) input.readObject();
        input.close();

        assertEquals(original, copy);
    }

    private static ProcessInfo process(String pid, String name, String user,
            String virtualMemory, String physicalMemory, String cpuUsage, String priority) {
        return new ProcessInfo(pid, "00:00:01", name, user, virtualMemory,
                physicalMemory, cpuUsage, "10:00:00", priority, name);
    }
}
