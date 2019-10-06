package log_reader;

/* 토픽 이름과 토픽에 대해 메시지를 주고 받은 횟수, 누적 메시지 크기 정보를 가지는 클래스 */
public class Topic {

	private String name; // 토픽 이름을 저장하는 변수
	private int totalSendingCount; // 메시지를 보낸 횟수를 저장하는 변수
	private int totalReceivingCount; // 메시지를 받은 횟수를 저장하는 변수
	private int totalThroughput; // 주고 받은 메시지의 크기(byte)를 누적하여 저장하는 변수

	public Topic(String name) {
		this.name = name;

		totalSendingCount = 0;
		totalReceivingCount = 0;
		totalThroughput = 0;
	}

	/* 메시지를 보낸 횟수와 누적 메시지 크기를 증가시킴 */
	public void increaseSendingInfo(int size) {
		totalSendingCount++;
		totalThroughput += size;
	}

	/* 메시지를 받은 횟수와 누적 메시지 크기를 증가시킴 */
	public void increaseReceivingInfo(int size) {
		totalReceivingCount++;
		totalThroughput += size;
	}
	
	/* 토픽 이름을 반환 */
	public String getName() {
		return name;
	}

	/* 메시지 보낸 횟수를 반환 */
	public int getTotalSendingCount() {
		return totalSendingCount;
	}

	/* 메시지 받은 횟수를 반환 */
	public int getTotalReceivingCount() {
		return totalReceivingCount;
	}

	/* 누적 메시지 크기를 반환 */
	public int getTotalThroughput() {
		return totalThroughput;
	}
}
