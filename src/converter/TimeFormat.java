package converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeFormat {

    public String convert(int h, int m) {
        String result = "";
        String prefix = "";
        String suffix = "";
        if(h < 12) {
            prefix = "";
            suffix = " AM";
            result = prefix + h + ":" + m + suffix;
        } else if (h > 12) {
            prefix = "0";
            suffix = " PM";
            h = h - 12;
            result = prefix + h + ":" + m + suffix;
        }
        return result;
    }

    private String timeFormat(DateFormat h, DateFormat m) {
        Calendar cal = Calendar.getInstance();
        return convert(Integer.parseInt(h.format(cal.getTime())), Integer.parseInt(m.format(cal.getTime())));
    }

    private String dateFormat(DateFormat day, DateFormat month, DateFormat year, String type) {
        String result = "";
        Calendar cal = Calendar.getInstance();
        if(type.equals("short")) {
            result = month.format(cal.getTime()) + "/" + day.format(cal.getTime()) + "/" + year.format(cal.getTime());
        } else if(type.equals("long")) {
            result = DayConverter.capsFormat(DayConverter.convertDay(cal.get(Calendar.DAY_OF_WEEK))) + ", " +
            MonthConverter.capsFormat(MonthConverter.convertMonth(cal.get(Calendar.MONTH))) + " " +
            day.format(cal.getTime()) + ", " + year.format(cal.getTime());
        }
        return result;
    }

    public String format(String v) {
        String result = "";
        if(v.equals("short")) {
            DateFormat hour = new SimpleDateFormat("HH");
            DateFormat minute = new SimpleDateFormat("mm");
            DateFormat day = new SimpleDateFormat("dd");
            DateFormat month = new SimpleDateFormat("MM");
            DateFormat year = new SimpleDateFormat("yyyy");

            result = timeFormat(hour, minute) + " " + dateFormat(day, month, year, v);
            /* System.out.println(timeFormat(hour, minute));
            System.out.println(dateFormat(day, month, year, v)); */
        } else if(v.equals("long")) {
            DateFormat hour = new SimpleDateFormat("HH");
            DateFormat minute = new SimpleDateFormat("mm");
            DateFormat day = new SimpleDateFormat("dd");
            DateFormat month = new SimpleDateFormat("MM");
            DateFormat year = new SimpleDateFormat("yyyy");

            result = timeFormat(hour, minute) + " " + dateFormat(day, month, year, v);
            /* System.out.println(timeFormat(hour, minute));
            System.out.println(dateFormat(day, month, year, v)); */
        }
        return result;
    }
}
