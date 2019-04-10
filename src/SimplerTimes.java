import java.util.Calendar;
import java.util.Date;

/**
 * A utility to simplify working with the Calendar.
 */
public class SimplerTimes{

  Calendar calendar;

  public SimplerTimes() {
     calendar = Calendar.getInstance();
  }

  public int get(int n) {
    return calendar.get(n);
  }

  public static String difference(int elapsed) {
    int minutes = elapsed / 60;
    int seconds = elapsed % 60;
    StringBuffer buf = new StringBuffer(20);
    buf.append(minutes);
    buf.append(" min ");
    buf.append(60 - seconds);
    buf.append(" sec");
    return buf.toString();
  }

  public static String timeOfday(int hour, int min, String ampm) {
    StringBuffer buf = new StringBuffer(10);
    buf.append(hour);
    buf.append(min < 10 ? ":0" : ":");
    buf.append(min);
    if (ampm.length() > 0) {
      buf.append(" ");
      buf.append(ampm);
    }
    return buf.toString();
  }

  public static String timeOfday(int hour, int min) {
    return timeOfday(hour, min, "");
  }

  public String timeOfday(boolean withAmPm) {
    return SimplerTimes.timeOfday(hour(), Calendar.MINUTE, (withAmPm) ? ampm() : "");
  }

  public String ampm() {
    return (calendar.get(Calendar.AM_PM) == Calendar.AM) ? "am" : "pm";
  }

  public int hour() {
    int hr =  get(Calendar.HOUR);
    return (hr < 1) ? hr + 12: hr;
  }


}
