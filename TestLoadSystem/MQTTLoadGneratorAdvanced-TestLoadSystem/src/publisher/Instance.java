package publisher;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import main.Connect;

public class Instance extends Thread {

	private String serverURI; // target message server의 ip주소
	private MemoryPersistence persistence = new MemoryPersistence();

	private String clientId;
	private int minClientNum; // 최소 클라이언트 개수
	private int maxClientNum; // 최대 클라이언트 개수
	private int duration; // client 지속 시간
	private int msgRate; // 1초에 보낼 메세지 개수
	private int topicNum; // 토픽당 하위에 생길 하위토픽 개수

	private ArrayList<String> topics = null; // master로부터 받아온 토픽리스트
	static ArrayList<Publisher> pubClientList; // publisher 클라이언트 리스트
	static ArrayList<Integer> pubListIndex; // publisher 클라이언트의 index를 저장하는 리스트
	private HashMap<String, Integer> pubTopicIndex = null; // 상위 토픽 밑의 하위 토픽 index를 저장하는 map

	BufferedWriter writer; // 파일 기록을 위한 writer

	public Instance(String broker, String clientId, String factors, ArrayList<String> topics) {

		this.serverURI = broker;
		this.clientId = clientId;
		this.minClientNum = Integer.parseInt(factors.split(",")[1]);
		this.maxClientNum = Integer.parseInt(factors.split(",")[2]);
		this.duration = Integer.parseInt(factors.split(",")[3]);
		this.msgRate = Integer.parseInt(factors.split(",")[4]);
		this.topicNum = Integer.parseInt(factors.split(",")[5]);
		this.topics = topics;

		try {
			// Publisher들의 서버 연결과 연결해제를 기록하는 파일 상단 factor 요소들 기록(pubLog.csv)
			writer = Files.newBufferedWriter(Paths.get("pubLog.csv"), Charset.forName("EUC-KR"),
					StandardOpenOption.CREATE);
			writer.write("Publisher 이름, connect 시간(ms), disconnect 시간(ms), 지속시간(ms), 생성된 순서\n");
			writer.close();
			
			// Publisher들이 publish(send)할 때마다 기록할 factor 요소 종류들 기록 (sendLog.csv)
			writer = Files.newBufferedWriter(Paths.get("sendLog.csv"), Charset.forName("EUC-KR"),
					StandardOpenOption.CREATE);
			writer.write("Publisher 이름, Publish 시간(ms), Publish Topic, 보낸 순서\n");
			writer.close();
			writer = Files.newBufferedWriter(Paths.get("sendLog.csv"), Charset.forName("EUC-KR"),
					StandardOpenOption.APPEND);

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(
				"-------------------------------------------SETTING----------------------------------------------");

	}

	@Override
	public void run() {

		int clientNum = maxClientNum - minClientNum + 1; // 총 생성될 클라이언트 개수
		int i = 0; // 클라이언트 index 변수
		int index = 0; // 하위 토픽 index 변수

		pubListIndex = new ArrayList<Integer>(clientNum);
		pubTopicIndex = new HashMap<>(topics.size());
		pubClientList = new ArrayList<Publisher>(clientNum);

		while (true) {

			if (pubClientList.size() < clientNum) { // 생성되어야할 클라이언트 개수가 생성되지 않았다면
				try {
					for (i = minClientNum; i <= maxClientNum; i++) {

						if (!pubListIndex.contains(i)) { // 혹시 listIndex에 i가 저장되어 있지 않으면!

							if (Connect.pubStop) { // stop 명령이 날라온 즉시 pubClientList의 모든 객체의 stop_disconnect 호출

								Instance.pubClientList.clear(); // pubClientList 전체 비우기
								Instance.pubListIndex.clear(); // pubListIndex 전체 비우기

								return; // while 무한루프 종료
							}

							double randomValue = Math.random();

							// int intValue = (int) (randomValue * 3) + 3; // (3초~5초)주기로 생성됨
							// int intValue = (int) (randomValue * 6) + 10; // (10초~15초)주기로 생성됨
							int intValue = (int) (randomValue * 6) + 5; // (5초~10초)주기로 생성됨

							long intervalPeriod = intValue * 1000; // 클라이언트 생성할 주기 (랜덤값)

							String mainTopic = selectTopic(topics) + "/"; // 상위 topic선택

							index = 0;

							if (!pubTopicIndex.keySet().contains(mainTopic)) { // 상위 topic이 topicIndex에 없으면
								pubTopicIndex.put(mainTopic, index); // 집어넣고
							} else { // 있으면
								index = pubTopicIndex.get(mainTopic); // index를 가져와서 증가시키고
								index++;

								if (index > topicNum) { // 하위토픽 제한보다 index가 커질경우 다시 0으로 초기화해주기
									index = 0;
								}

								pubTopicIndex.put(mainTopic, index); // 다시 집어넣어주기
							}

							// client 생성후 clientList에 삽입, listIndex 또한 삽입
							pubClientList.add(new Publisher(clientId + i, serverURI, persistence, mainTopic + index,
									duration, msgRate, writer));
							pubListIndex.add(i);

							System.out.println("period: " + intValue + " (sec)");
							System.out.println(
									"------------------------------------------------------------------------------------------------");

							sleep(intervalPeriod); // 해당 주기만큼 기다리기
							break;
						}
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (MqttException e) {
					e.printStackTrace();
				}
			} else { // 대기
				try {
					sleep(10);
					if (Connect.pubStop) {
						Instance.pubClientList.clear();
						Instance.pubListIndex.clear();
						return;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}

	public String selectTopic(List<String> topics) { // 상위 토픽 랜덤으로 선택해서 반환하는 메서드
		int topicSize = topics.size();
		double randomValue = Math.random();
		int intValue = (int) (randomValue * topicSize) + 0; // (0~토픽리스트 사이즈)

		String mySelectTopic = topics.get(intValue);
		return mySelectTopic;
	}

}
