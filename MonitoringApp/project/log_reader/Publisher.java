package log_reader;

import java.util.Vector;

/* publisher의 id와 발행 정보를 가지는 클래스 */
public class Publisher {

	private String clientId; // publisher(클라이언트)의 id를 저장하는 변수
	private Vector<Publication> publications; // publisher가 발행한 토픽 이름과 메시지를 보낸 횟수 정보를 저장하는 클래스의 Vector
	private int throughput; // publisher가 보낸 메시지 크기(byte)를 누적하여 저장하는 변수
	private int numberOfMessages; // publisher가 보낸 메시지 개수를 누적하여 저장하는 변수

	public Publisher(String clientId) {
		this.clientId = clientId;
		publications = new Vector<Publication>();
		throughput = 0;
		numberOfMessages = 0;
	}

	/* 발행하는 토픽을 바탕으로 Publication을 생성하여 publications에 저장하고 누적 메시지 크기를 증가시킴 */
	public void publish(String topic, int messageSize) {
		int index = getPublicationIndex(topic);
		Publication p;

		if (index == -1) { //이미 publicaitons에 저장되어 있는 토픽인지 확인
			p = new Publication(topic);
			p.addSendingCount();
			publications.add(p);
		} else { 
			p = publications.get(index);
			p.addSendingCount();
			publications.set(index, p);
		}

		throughput += messageSize; //누적 메시지 크기(byte)를 증가시킴
		numberOfMessages += 1; //누적 메시지 개수를 증가시킴
	}

	/* publications에 이미 저장 된 토픽인지 확인하고 인덱스를 반환 */
	public int getPublicationIndex(String topic) { 
		for (int i = 0; i < publications.size(); i++)
			if (topic.equals(publications.get(i).getTopic()))
				return i;
		return -1;
	}
	
	/* publications의 내용을 지움 */
	public void clearPub() {
		publications.clear();
	}

	/* 누적 된 메시지 크기를 반환 */
	public int getThroughput() {
		return throughput;
	}

	/* 누적 된 메시지 개수를 반환 */
	public int getNumberOfMessages() {
		return numberOfMessages;
	}

	/* publisher(클라이언트)의 id를 반환 */
	public String getClientId() {
		return clientId;
	}

	/* publications를 반환 */
	public Vector<Publication> getPublications() {
		return publications;
	}

	/* publisher가 발행한 토픽 목록을 출력 */
	public void printPublication() {
		for (Publication p : publications)
			System.out.println("publisher id (" + clientId + ") topic (" + p.getTopic() + ")");
	}
}
