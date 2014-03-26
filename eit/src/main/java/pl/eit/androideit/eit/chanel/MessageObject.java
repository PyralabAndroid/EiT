package pl.eit.androideit.eit.chanel;

public class MessageObject {
	public String message, userName;
	public int channelId;
	public long messageDate;
	
	public MessageObject(String message, int channelId, long messageDate, String userName){
		this.message = message;
		this.channelId = channelId;
		this.userName = userName;
		this.messageDate = messageDate;
	}
	
}
