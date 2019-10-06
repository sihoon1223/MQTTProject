package log_reader;

/* publisher가 발행한 토픽의 이름과 메시지를 보낸 횟수 정보를 가지는 클래스 */
public class Publication {
	
	private String topic; // 발행한 토픽 이름을 저장하는 변수
	private int sendingCount; // 메시지를 보낸 횟수를 저장하는 변수

	public Publication(String topic) {
		this.topic=topic;
		
		sendingCount=0;
	}
	
	/* 토픽 이름을 반환 */
	public String getTopic() {
		return topic;
	}
	
	/* 메시지를 보낸 횟수를 반환 */
	public int getSendingCount() {
		return sendingCount;
	}
	
	/* 메시지를 보낸 횟수를 증가시킴 */
	public void addSendingCount() {
		sendingCount++;
	}
}
