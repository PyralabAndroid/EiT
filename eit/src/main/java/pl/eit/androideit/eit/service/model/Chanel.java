package pl.eit.androideit.eit.service.model;

public class Chanel {
	public String channelName;
	public long channelTimestamp;
	
	public Chanel(long channelTimestamp, String channelName){
		this.channelTimestamp = channelTimestamp;
		this.channelName = channelName;
	}
}
