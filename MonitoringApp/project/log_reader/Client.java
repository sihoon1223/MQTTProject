package log_reader;

/* 클라이언트 이름과 누적된 메시지 크기에 대한 정보를 가지는 클래스 */
public class Client {

	private String clientId; // 클라이언트 id를 저장하는 변수
	private int throughput; // 주고 받은 메시지 크기(byte)를 누적하여 저장하는 변수

	public Client(String clientId) {
		this.clientId = clientId;
		throughput = 0;
	}

	/* 클라이언트 id를 반환 */
	public String getClientId() {
		return clientId;
	}

	/* 누적된 메시지 크기를 반환 */
	public int getThroughput() {
		return throughput;
	}

	/* 누적된 메시지 크기를 증가시킴 */
	public void increaseThroughput(int messageSize) {
		throughput += messageSize;
	}
}
