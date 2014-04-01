package pl.eit.androideit.eit.service.model;

public class Chanel {
	public String channelName;
	public long channelTimestamp;
    public int isSub;
	
	public Chanel(long channelTimestamp, String channelName){
		this.channelTimestamp = channelTimestamp;
		this.channelName = channelName;
        this.isSub = 0;
	}

    public Chanel(long channelTimestamp, String channelName, int isSub){
        this.channelTimestamp = channelTimestamp;
        this.channelName = channelName;
        this.isSub = isSub;
    }
}
