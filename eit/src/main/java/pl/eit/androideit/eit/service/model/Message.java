package pl.eit.androideit.eit.service.model;

public class Message {
	public String message, userName;
	public long channelTimestamp, messageTimestamp;
	
	public Message(String message, long channelTimestamp, long messageTimestamp, String userName){
		this.message = message;
		this.channelTimestamp = channelTimestamp;
		this.userName = userName;
		this.messageTimestamp = messageTimestamp;
	}
	
}
