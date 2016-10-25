package org.jutils.jprocesses.util;

import org.junit.Test;

import java.text.ParseException;
import java.util.Locale;

import static org.junit.Assert.*;

@SuppressWarnings("Since15")
public class ProcessesUtilsTest {
  @Test
  public void getCustomDateFormat() throws Exception {
      assertEquals("10/23/2016 08:30:00", ProcessesUtils.parseUnixLongTimeToFullDate("oct 23 08:30:00 2016"));
      try {
        ProcessesUtils.parseUnixLongTimeToFullDate("23 okt 2016 08:30:00");
        fail();
      } catch (ParseException e) {}
      ProcessesUtils.setCustomDateFormat("dd MMM yyyy HH:mm:ss");
      ProcessesUtils.setCustomLocale(Locale.forLanguageTag("no"));
      assertEquals("10/23/2016 08:30:00", ProcessesUtils.parseUnixLongTimeToFullDate("23 okt 2016 08:30:00"));  
  }

}