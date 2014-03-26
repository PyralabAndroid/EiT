package pl.eit.androideit.eit.service.model;

public class Message {
	public String message, userName;
	public int channelId;
	public long messageDate;
	
	public Message(String message, int channelId, long messageDate, String userName){
		this.message = message;
		this.channelId = channelId;
		this.userName = userName;
		this.messageDate = messageDate;
	}
	
}
