package log_reader;

import java.util.Vector;

/* subscriber의 id와 구독 정보를 가지는 클래스 */
public class Subscriber {

	private String clientId; // subscriber(클라이언트)의 id를 저장하는 변수
	private Vector<Subscription> subscriptions; // subscriber가 구독한 토픽 이름과 메시지를 보낸 횟수 정보를 저장하는 클래스의 Vector
	private int throughput; // subscriber가 받은 메시지 크기(byte)를 누적하여 저장하는 변수
	private int numberOfMessages; // subscriber가 받은 메시지 개수를 누적하여 저장하는 변수
	private boolean isExistSharp; // 구독한 토픽에 '#'이 포함되어 있는지를 판별하는 변수

	public Subscriber(String clientId) {
		this.clientId = clientId;
		subscriptions = new Vector<Subscription>();
		throughput = 0;
		numberOfMessages = 0;
		isExistSharp = false;
	}

	/* 구독하는 토픽을 바탕으로 Subscription을 생성하여 subscriptions에 저장 */
	public void subscribe(String topic, String topicKor) {
		int index = getSubscriptionIndex(topic);
		if (index == -1) {
			Subscription s = new Subscription(topic, topicKor);
			subscriptions.add(s);
		}
	}

	/* subscriptions에 이미 저장 된 토픽인지 확인하고 인덱스를 반환 */
	public int getSubscriptionIndex(String topic) {
		for (int i = 0; i < subscriptions.size(); i++)
			if (topic.equals(subscriptions.get(i).getTopic()))
				return i;
		return -1;
	}

	/* 구독 중인 토픽으로 메시지를 받으면 토픽에 대하여 메시지 받은 횟수를 증가시키고, 누적 메시지 크기를 증가시킴 */
	public void receivedMessage(String topic, int messageSize) {
		if(isExistSharp) { // '#'이 포함된 토픽을 구독 중인지 확인
			for(int i=0;i<subscriptions.size();i++)
				if(topic.contains(subscriptions.get(i).getTopic())) // 모든 하위 토픽에 대하여 메시지 받은 횟수를 증가시킴
					subscriptions.get(i).addReceivingCount();

		} else {

			int index = getSubscriptionIndex(topic);

			Subscription s = subscriptions.get(index);
			s.addReceivingCount();
			subscriptions.set(index, s);
		}

		throughput += messageSize; //누적 메시지 크기(byte)를 증가시킴
		numberOfMessages += 1; //누적 메시지 개수를 증가시킴
	}
	
	/* subscriptions의 내용을 지움 */
	public void clearSub() {
		subscriptions.clear();
		isExistSharp = false;
	}

	/* 누적 된 메시지 크기를 반환 */
	public int getThroughput() {
		return throughput;
	}

	/* 누적 된 메시지 개수를 반환 */
	public int getNumberOfMessages() {
		return numberOfMessages;
	}

	/* subscriber(클라이언트)의 id를 반환 */
	public String getClientId() {
		return clientId;
	}

	/* subscriptions를 반환 */
	public Vector<Subscription> getSubscriptions() {
		return subscriptions;
	}

	/* subscriber가 구독한 토픽 목록을 출력 */
	public void printSubscription() {
		for (Subscription s : subscriptions)
			System.out.println("subscriber id (" + clientId + ") topic (" + s.getTopic() + ")");
	}

	/* '#'을 포함한 토픽을 구독 함을 명시 */
	public void useSharp() {
		isExistSharp = true;
	}
}
