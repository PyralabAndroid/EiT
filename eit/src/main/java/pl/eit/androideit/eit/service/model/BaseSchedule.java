package pl.eit.androideit.eit.service.model;

import com.google.gson.annotations.SerializedName;

public class BaseSchedule {

    @SerializedName("base_schedule")
    public ScheduleComponent[] baseScheduleComponents;
}
