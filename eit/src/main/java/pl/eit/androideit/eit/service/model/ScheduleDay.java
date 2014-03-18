package pl.eit.androideit.eit.service.model;

import com.google.gson.annotations.SerializedName;

public class ScheduleDay {

    public String name;
    @SerializedName("start_time")
    public String startTime;
    @SerializedName("end_time")
    public String endTime;
    public String place;
    public String type;
    public String week;

}
