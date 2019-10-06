package subscriber;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

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

	public static ArrayList<Subscriber> subClientList; // subscriber 클라이언트 리스트
	public static ArrayList<Integer> subListIndex; // subscriber 클라이언트의 index를 저장하는 리스트

	private ArrayList<String> topics = null; // master로부터 받아온 토픽리스트

	private BufferedWriter writer; // 파일 기록을 위한 writer

	public Instance(String broker, String clientId, String factors, ArrayList<String> topics) {

		this.serverURI = broker;
		this.clientId = clientId;
		this.persistence = new MemoryPersistence();
		this.minClientNum = Integer.parseInt(factors.split(",")[1]);
		this.maxClientNum = Integer.parseInt(factors.split(",")[2]);
		this.duration = Integer.parseInt(factors.split(",")[3]);
		this.topics = (ArrayList<String>) topics;

		/*try {
			// Subscriber들의 서버 연결과 연결해제를 기록하는 파일 상단 factor 요소들 기록(subLog.csv)
			writer = Files.newBufferedWriter(Paths.get("subLog.csv"), Charset.forName("EUC-KR"),
					StandardOpenOption.CREATE);
			writer.write("Publisher 이름, connect 시간(ms), disconnect 시간(ms), 지속시간(ms), 생성된 순서\n");
			writer.close();

			// Subscriber들이 subscribe(receive)할 때마다 기록할 factor 요소 종류들 기록 (receiveLog.csv)
			writer = Files.newBufferedWriter(Paths.get("receiveLog.csv"), Charset.forName("EUC-KR"),
					StandardOpenOption.CREATE);
			writer.write(
					"Publisher 이름, Publish 시간(ms), Publish Topic, Subscriber 이름, Subscribe 시간(ms), Subscribe Topic, 도착순서\n");
			writer.close();
			writer = Files.newBufferedWriter(Paths.get("receiveLog.csv"), Charset.forName("EUC-KR"),
					StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}*/

		System.out.println(
				"-------------------------------------------SETTING-----------------------------------------------");

	}

	@Override
	public void run() {

		int clientNum = maxClientNum - minClientNum + 1; // 총 생성될 클라이언트 개수
		int i = 0; // 클라이언트 index 변수

		subListIndex = new ArrayList<Integer>(clientNum);
		subClientList = new ArrayList<Subscriber>(clientNum);

		while (true) {

			if (subClientList.size() < clientNum) { // 생성되어야할 클라이언트 개수가 생성되지 않았다면
				try {
					for (i = minClientNum; i <= maxClientNum; i++) {

						if (!subListIndex.contains(i)) { // 혹시 listIndex에 i가 저장되어 있지 않으면!

							if (Connect.subStop) { // stop 명령이 날라온 즉시 subClientList의 모든 객체의 stop_disconnect 호출

								Instance.subClientList.clear(); // subClientList 전체 비우기
								Instance.subListIndex.clear(); // subListIndex 전체 비우기

								return; // while 무한루프 종료
							}

							double randomValue = Math.random();
							int intValue = (int) (randomValue * 3) + 3; // (3초~5초)주기로 생성됨
							//int intValue = (int) (randomValue * 6) + 5; // (5초~10초)주기로 생성됨
							//int intValue = (int) (randomValue * 6) + 10; // (10초~15초)주기로 생성됨
							long intervalPeriod = intValue * 1000; // 클라이언트 생성할 주기 (랜덤값)

							// client 생성후 clientList에 삽입, listIndex 또한 삽입
							subClientList.add(new Subscriber(clientId + i, serverURI, persistence, topics, duration, writer));
							subListIndex.add(i);

							System.out.println("period: " + intValue + " (sec)");
							System.out.println("add in list: " + clientId + i + "( " + subClientList + " )");
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
					if (Connect.subStop) {

						Instance.subClientList.clear();
						Instance.subListIndex.clear();

						return;
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
