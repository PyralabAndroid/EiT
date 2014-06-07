package pl.eit.androideit.eit.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import pl.eit.androideit.eit.content.AppConst;

public class TimeCalculateHelper {

    private static final long MINUTE = 60000;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;

    private static final String NOW = "teraz";
    private static final String MIN_AGO = " min. temu";
    private static final String HOUR_AGO = " godz. temu";
    private static final String DAY_AGO = " dni temu";

    public static String getTimeDifferenceString(long timeInMillis) {
        SimpleDateFormat format = new SimpleDateFormat(AppConst.MESSAGE_TIME_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        long timeDifference = Calendar.getInstance().getTimeInMillis() - timeInMillis;

        if (timeDifference < MINUTE) {
            return NOW;
        } else if (timeDifference < HOUR) {
            return String.valueOf(timeDifference / MINUTE) + MIN_AGO;
        } else if (timeDifference < DAY) {
            return String.valueOf(timeDifference / HOUR) + HOUR_AGO;
        } else {
            return format.format(calendar.getTime());
        }
    }
}
