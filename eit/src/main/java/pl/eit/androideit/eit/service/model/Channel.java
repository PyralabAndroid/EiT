package pl.eit.androideit.eit.service.model;

public class Channel {
	public String channelName;
	public long channelTimestamp;
    public int isSub;
	
	public Channel(long channelTimestamp, String channelName){
		this.channelTimestamp = channelTimestamp;
		this.channelName = channelName;
        this.isSub = 0;
	}

    public Channel(long channelTimestamp, String channelName, int isSub){
        this.channelTimestamp = channelTimestamp;
        this.channelName = channelName;
        this.isSub = isSub;
    }
}
