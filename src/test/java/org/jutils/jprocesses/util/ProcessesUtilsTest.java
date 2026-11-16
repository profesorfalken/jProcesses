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
package org.jutils.jprocesses.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProcessesUtilsTest {

    private Locale originalLocale;

    @Before
    public void setUp() {
        originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }

    @After
    public void tearDown() {
        Locale.setDefault(originalLocale);
        ProcessesUtils.setCustomDateFormat(null);
        ProcessesUtils.setCustomLocale(null);
    }

    @Test
    public void parsesEnglishLongDate() throws Exception {
        assertEquals("10/23/2016 08:30:00",
                ProcessesUtils.parseUnixLongTimeToFullDate("oct 23 08:30:00 2016"));
    }

    @Test
    public void rejectsUnknownMonthWithoutCustomFormat() throws Exception {
        try {
            ProcessesUtils.parseUnixLongTimeToFullDate("23 okt 2016 08:30:00");
            fail("The date should not be parseable with the English locale");
        } catch (ParseException expected) {
            // expected
        }
    }

    @Test
    public void parsesCustomLocaleAndFormat() throws Exception {
        Locale norwegian = Locale.forLanguageTag("no");
        //Use the exact abbreviation known by the running JDK (CLDR data changes
        //between JDK versions: "okt" vs "okt.")
        String october = new SimpleDateFormat("MMM", norwegian)
                .getDateFormatSymbols().getShortMonths()[9];

        ProcessesUtils.setCustomDateFormat("dd MMM yyyy HH:mm:ss");
        ProcessesUtils.setCustomLocale(norwegian);

        assertEquals("10/23/2016 08:30:00",
                ProcessesUtils.parseUnixLongTimeToFullDate("23 " + october + " 2016 08:30:00"));
    }

    @Test
    public void parsesWindowsDates() {
        assertEquals("13:58:15",
                ProcessesUtils.parseWindowsDateTimeToSimpleTime("20160514135815.444566+180"));
        assertEquals("05/14/2016 13:58:15",
                ProcessesUtils.parseWindowsDateTimeToFullDate("20160514135815.444566+180"));
    }

    @Test
    public void invalidWindowsDatesAreReturnedUnchanged() {
        assertEquals("", ProcessesUtils.parseWindowsDateTimeToSimpleTime(""));
        assertEquals("short", ProcessesUtils.parseWindowsDateTimeToSimpleTime("short"));
        assertEquals("short", ProcessesUtils.parseWindowsDateTimeToFullDate("short"));
        assertEquals(null, ProcessesUtils.parseWindowsDateTimeToSimpleTime(null));
    }

    @Test
    public void emptyUnixDateIsRejected() throws Exception {
        try {
            ProcessesUtils.parseUnixLongTimeToFullDate("");
            fail("An empty date must be rejected");
        } catch (ParseException expected) {
            // expected
        }
    }
}
