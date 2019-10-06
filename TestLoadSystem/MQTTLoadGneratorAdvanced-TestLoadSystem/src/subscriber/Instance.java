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

	private String serverURI; // target message server�� ip�ּ�
	private MemoryPersistence persistence = new MemoryPersistence();

	private String clientId;
	private int minClientNum; // �ּ� Ŭ���̾�Ʈ ����
	private int maxClientNum; // �ִ� Ŭ���̾�Ʈ ����
	private int duration; // client ���� �ð�

	public static ArrayList<Subscriber> subClientList; // subscriber Ŭ���̾�Ʈ ����Ʈ
	public static ArrayList<Integer> subListIndex; // subscriber Ŭ���̾�Ʈ�� index�� �����ϴ� ����Ʈ

	private ArrayList<String> topics = null; // master�κ��� �޾ƿ� ���ȸ���Ʈ

	private BufferedWriter writer; // ���� ����� ���� writer

	public Instance(String broker, String clientId, String factors, ArrayList<String> topics) {

		this.serverURI = broker;
		this.clientId = clientId;
		this.persistence = new MemoryPersistence();
		this.minClientNum = Integer.parseInt(factors.split(",")[1]);
		this.maxClientNum = Integer.parseInt(factors.split(",")[2]);
		this.duration = Integer.parseInt(factors.split(",")[3]);
		this.topics = (ArrayList<String>) topics;

		/*try {
			// Subscriber���� ���� ����� ���������� ����ϴ� ���� ��� factor ��ҵ� ���(subLog.csv)
			writer = Files.newBufferedWriter(Paths.get("subLog.csv"), Charset.forName("EUC-KR"),
					StandardOpenOption.CREATE);
			writer.write("Publisher �̸�, connect �ð�(ms), disconnect �ð�(ms), ���ӽð�(ms), ������ ����\n");
			writer.close();

			// Subscriber���� subscribe(receive)�� ������ ����� factor ��� ������ ��� (receiveLog.csv)
			writer = Files.newBufferedWriter(Paths.get("receiveLog.csv"), Charset.forName("EUC-KR"),
					StandardOpenOption.CREATE);
			writer.write(
					"Publisher �̸�, Publish �ð�(ms), Publish Topic, Subscriber �̸�, Subscribe �ð�(ms), Subscribe Topic, ��������\n");
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

		int clientNum = maxClientNum - minClientNum + 1; // �� ������ Ŭ���̾�Ʈ ����
		int i = 0; // Ŭ���̾�Ʈ index ����

		subListIndex = new ArrayList<Integer>(clientNum);
		subClientList = new ArrayList<Subscriber>(clientNum);

		while (true) {

			if (subClientList.size() < clientNum) { // �����Ǿ���� Ŭ���̾�Ʈ ������ �������� �ʾҴٸ�
				try {
					for (i = minClientNum; i <= maxClientNum; i++) {

						if (!subListIndex.contains(i)) { // Ȥ�� listIndex�� i�� ����Ǿ� ���� ������!

							if (Connect.subStop) { // stop ����� ����� ��� subClientList�� ��� ��ü�� stop_disconnect ȣ��

								Instance.subClientList.clear(); // subClientList ��ü ����
								Instance.subListIndex.clear(); // subListIndex ��ü ����

								return; // while ���ѷ��� ����
							}

							double randomValue = Math.random();
							int intValue = (int) (randomValue * 3) + 3; // (3��~5��)�ֱ�� ������
							//int intValue = (int) (randomValue * 6) + 5; // (5��~10��)�ֱ�� ������
							//int intValue = (int) (randomValue * 6) + 10; // (10��~15��)�ֱ�� ������
							long intervalPeriod = intValue * 1000; // Ŭ���̾�Ʈ ������ �ֱ� (������)

							// client ������ clientList�� ����, listIndex ���� ����
							subClientList.add(new Subscriber(clientId + i, serverURI, persistence, topics, duration, writer));
							subListIndex.add(i);

							System.out.println("period: " + intValue + " (sec)");
							System.out.println("add in list: " + clientId + i + "( " + subClientList + " )");
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
