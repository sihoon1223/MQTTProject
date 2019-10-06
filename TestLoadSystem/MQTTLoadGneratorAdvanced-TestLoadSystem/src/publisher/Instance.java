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

	private String serverURI; // target message server�� ip�ּ�
	private MemoryPersistence persistence = new MemoryPersistence();

	private String clientId;
	private int minClientNum; // �ּ� Ŭ���̾�Ʈ ����
	private int maxClientNum; // �ִ� Ŭ���̾�Ʈ ����
	private int duration; // client ���� �ð�
	private int msgRate; // 1�ʿ� ���� �޼��� ����
	private int topicNum; // ���ȴ� ������ ���� �������� ����

	private ArrayList<String> topics = null; // master�κ��� �޾ƿ� ���ȸ���Ʈ
	static ArrayList<Publisher> pubClientList; // publisher Ŭ���̾�Ʈ ����Ʈ
	static ArrayList<Integer> pubListIndex; // publisher Ŭ���̾�Ʈ�� index�� �����ϴ� ����Ʈ
	private HashMap<String, Integer> pubTopicIndex = null; // ���� ���� ���� ���� ���� index�� �����ϴ� map

	BufferedWriter writer; // ���� ����� ���� writer

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
			// Publisher���� ���� ����� ���������� ����ϴ� ���� ��� factor ��ҵ� ���(pubLog.csv)
			writer = Files.newBufferedWriter(Paths.get("pubLog.csv"), Charset.forName("EUC-KR"),
					StandardOpenOption.CREATE);
			writer.write("Publisher �̸�, connect �ð�(ms), disconnect �ð�(ms), ���ӽð�(ms), ������ ����\n");
			writer.close();
			
			// Publisher���� publish(send)�� ������ ����� factor ��� ������ ��� (sendLog.csv)
			writer = Files.newBufferedWriter(Paths.get("sendLog.csv"), Charset.forName("EUC-KR"),
					StandardOpenOption.CREATE);
			writer.write("Publisher �̸�, Publish �ð�(ms), Publish Topic, ���� ����\n");
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

		int clientNum = maxClientNum - minClientNum + 1; // �� ������ Ŭ���̾�Ʈ ����
		int i = 0; // Ŭ���̾�Ʈ index ����
		int index = 0; // ���� ���� index ����

		pubListIndex = new ArrayList<Integer>(clientNum);
		pubTopicIndex = new HashMap<>(topics.size());
		pubClientList = new ArrayList<Publisher>(clientNum);

		while (true) {

			if (pubClientList.size() < clientNum) { // �����Ǿ���� Ŭ���̾�Ʈ ������ �������� �ʾҴٸ�
				try {
					for (i = minClientNum; i <= maxClientNum; i++) {

						if (!pubListIndex.contains(i)) { // Ȥ�� listIndex�� i�� ����Ǿ� ���� ������!

							if (Connect.pubStop) { // stop ����� ����� ��� pubClientList�� ��� ��ü�� stop_disconnect ȣ��

								Instance.pubClientList.clear(); // pubClientList ��ü ����
								Instance.pubListIndex.clear(); // pubListIndex ��ü ����

								return; // while ���ѷ��� ����
							}

							double randomValue = Math.random();

							// int intValue = (int) (randomValue * 3) + 3; // (3��~5��)�ֱ�� ������
							// int intValue = (int) (randomValue * 6) + 10; // (10��~15��)�ֱ�� ������
							int intValue = (int) (randomValue * 6) + 5; // (5��~10��)�ֱ�� ������

							long intervalPeriod = intValue * 1000; // Ŭ���̾�Ʈ ������ �ֱ� (������)

							String mainTopic = selectTopic(topics) + "/"; // ���� topic����

							index = 0;

							if (!pubTopicIndex.keySet().contains(mainTopic)) { // ���� topic�� topicIndex�� ������
								pubTopicIndex.put(mainTopic, index); // ����ְ�
							} else { // ������
								index = pubTopicIndex.get(mainTopic); // index�� �����ͼ� ������Ű��
								index++;

								if (index > topicNum) { // �������� ���Ѻ��� index�� Ŀ����� �ٽ� 0���� �ʱ�ȭ���ֱ�
									index = 0;
								}

								pubTopicIndex.put(mainTopic, index); // �ٽ� ����־��ֱ�
							}

							// client ������ clientList�� ����, listIndex ���� ����
							pubClientList.add(new Publisher(clientId + i, serverURI, persistence, mainTopic + index,
									duration, msgRate, writer));
							pubListIndex.add(i);

							System.out.println("period: " + intValue + " (sec)");
							System.out.println(
									"------------------------------------------------------------------------------------------------");

							sleep(intervalPeriod); // �ش� �ֱ⸸ŭ ��ٸ���
							break;
						}
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (MqttException e) {
					e.printStackTrace();
				}
			} else { // ���
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

	public String selectTopic(List<String> topics) { // ���� ���� �������� �����ؼ� ��ȯ�ϴ� �޼���
		int topicSize = topics.size();
		double randomValue = Math.random();
		int intValue = (int) (randomValue * topicSize) + 0; // (0~���ȸ���Ʈ ������)

		String mySelectTopic = topics.get(intValue);
		return mySelectTopic;
	}

}
