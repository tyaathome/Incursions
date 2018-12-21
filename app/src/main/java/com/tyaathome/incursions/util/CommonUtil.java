package com.tyaathome.incursions.util;

import java.util.Calendar;

/**
 * Created by tyaathome on 2018/12/20.
 */
public class CommonUtil {

    public static Calendar copyCalendar(Calendar raw) {
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(raw.getTimeInMillis());
        return result;
    }

}
