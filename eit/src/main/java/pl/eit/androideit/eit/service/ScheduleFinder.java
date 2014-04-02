package pl.eit.androideit.eit.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.eit.androideit.eit.content.AppPreferences;
import pl.eit.androideit.eit.schedule_fragment.ScheduleItem;
import pl.eit.androideit.eit.service.model.BaseSchedule;
import pl.eit.androideit.eit.service.model.Info;
import pl.eit.androideit.eit.service.model.Schedule;
import pl.eit.androideit.eit.service.model.ScheduleComponent;
import pl.eit.androideit.eit.service.model.ScheduleDay;

public class ScheduleFinder {

    private BaseSchedule mBaseSchedule;
    private AppPreferences mAppPreferences;
    private int mDayId;


    private boolean EVEN_WEEK;

    private Calendar mCalendar;

    private String mTimeFormat = "%1$s - %2$s";

    public ScheduleFinder(Context context, BaseSchedule baseSchedule, int dayId) {
        mBaseSchedule = baseSchedule;
        mAppPreferences = new AppPreferences(context);
        mDayId = dayId;

        mCalendar = Calendar.getInstance();
        EVEN_WEEK = mCalendar.get(Calendar.WEEK_OF_YEAR) % 2 == 0;
    }

    public void updateBaseSchedule(BaseSchedule baseSchedule) {
        mBaseSchedule = baseSchedule;
    }

    public List<ScheduleItem> getScheduleList() {
        final ScheduleDay[] scheduleDay = getScheduleForSpecifDay();
        if (scheduleDay == null) {
            return null;
        }
        return makeScheduleList(scheduleDay);
    }

    private List<ScheduleItem> makeScheduleList(ScheduleDay[] scheduleDayTab) {
        ArrayList<ScheduleItem> list = new ArrayList<ScheduleItem>();

        for (ScheduleDay scheduleDay : scheduleDayTab) {
            if (isCorrectWeek(scheduleDay.week)) {
                list.add(new ScheduleItem(scheduleDay.name, scheduleDay.place,
                        getTime(scheduleDay.startTime, scheduleDay.endTime), getType(scheduleDay.type)));
            }
        }
        return list;
    }

    private boolean isCorrectWeek(String week) {
        if (week.equals("always")) {
            return true;
        } else if (week.equals("above") && EVEN_WEEK) {
            return true;
        } else if (week.equals("below") && !EVEN_WEEK) {
            return true;
        } else {
            return false;
        }
    }

    private String getType(String type) {
        if (type.equals("wyk")) {
            return "Wykład";
        } else if (type.equals("cw")) {
            return "Ćwiczenia";
        } else if (type.equals("lab")) {
            return "Laboratoria";
        } else {
            return "";
        }
    }

    private String getTime(String startTime, String endTime) {
        return String.format(mTimeFormat, startTime, endTime);
    }

    private ScheduleDay[] getScheduleForSpecifDay() {
        final Schedule schedule = findRightSchedule(mBaseSchedule.baseScheduleComponents);
        if (schedule == null) {
            return null;
        }
        switch (mDayId) {
            case 1:
                return schedule.monday;
            case 2:
                return schedule.tuesday;
            case 3:
                return schedule.wednesday;
            case 4:
                return schedule.thursday;
            case 5:
                return schedule.friday;
            default:
                return null;
        }
    }

    private Schedule findRightSchedule(ScheduleComponent[] baseScheduleComponents) {
        for (ScheduleComponent scheduleComponent : baseScheduleComponents) {
            if (isRightSchedule(scheduleComponent.info)) {
                return scheduleComponent.schedule;
            }
        }
        return null;
    }

    // TODO: add check from dialog change
    private boolean isRightSchedule(Info info) {
        if (info.year.equals(mAppPreferences.getYear())) {
            if (info.group.equals(mAppPreferences.getGroup())) {
                if (info.site.equals(mAppPreferences.getSide())) {
                    return true;
                }
            }
        }
        return false;
    }
}
