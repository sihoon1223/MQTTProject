package log_reader;

/* subscriber가 구독한 토픽의 이름과 메시지를 받은 횟수 정보를 가지는 클래스 */
public class Subscription {
	
	private String topic; // 구독한 토픽 이름을 저장하는 변수
	private String topicKor;
	private int receivingCount; // 메시지를 받은 횟수를 저장하는 변수

	public Subscription(String topic, String topicKor) {
		this.topic=topic;
		this.topicKor=topicKor;
		
		receivingCount=0;
	}
	
	/* 토픽 이름을 반환 */
	public String getTopic() {
		return topic;
	}

	public String getTopicKor() {
		return topicKor;
	}
	
	/* 메시지를 받은 횟수를 반환 */
	public int getReceivingCount() {
		return receivingCount;
	}
	
	/* 메시지를 받은 횟수를 증가시킴 */
	public void addReceivingCount() {
		receivingCount++;
	}
}
