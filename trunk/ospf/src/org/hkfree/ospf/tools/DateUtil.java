package org.hkfree.ospf.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Třída pro operace s datumy
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class DateUtil {

    /**
     * Vrací příznak, zda se jedná o stejný den
     * @param date1
     * @param date2
     * @return boolean
     */
    public static boolean isItSameDay(Date date1, Date date2) {
	Calendar calendar1 = Calendar.getInstance();
	Calendar calendar2 = Calendar.getInstance();
	calendar1.setTime(date1);
	calendar2.setTime(date2);
	if (calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)
		&& calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
		&& calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)) {
	    return true;
	}
	return false;
    }


    /**
     * Metoda získá datum ze stringu dle předaného formátu
     * @param format formát v jakém je zadáno datum
     * @param dateString datum ve stringu
     * @return datum jako Date
     * @throws ParseException při chybném rozparsování stringu
     */
    public static Date parseDate(String format, String dateString) throws ParseException {
	SimpleDateFormat sdf = new SimpleDateFormat(format);
	return sdf.parse(dateString);
    }
}
