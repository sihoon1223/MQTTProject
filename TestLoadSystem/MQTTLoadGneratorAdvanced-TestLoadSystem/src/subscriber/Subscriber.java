package subscriber;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import main.Connect;

public class Subscriber extends MqttClient implements MqttCallback {

	private String clientId;
	private int duration;
	private ArrayList<String> topicList;
	private String myTopic = null;

	private StringBuilder builder;
	private BufferedWriter writer;

	static private int counter = 0; // subscribe �� ������ count�Ǵ� ����
	static private int number = 0; // ��ü �������� count�Ǵ� ����

	private String connectTime; // ������ ����� �ð�
	private String disconnectTime; // ������ ���� ������ �ð�
	SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS"); // ������ ����

	private MqttConnectOptions connOpts = new MqttConnectOptions();

	// target message server�� ������ �� ����� timer ��ü�� task
	private Timer connectT;
	private TimerTask connectTask;

	public static boolean subIsRunning = false; // ���� subscriber�� ����ǰ� �ִ��� �Ǵ��ϴ� ����

	public Subscriber(String clientId, String serverURI, MemoryPersistence persistence, ArrayList<String> topicList,
			int duration, BufferedWriter receiveWriter) throws MqttException {

		super(serverURI, clientId, persistence);

		this.clientId = clientId;
		this.topicList = topicList;
		this.duration = duration;

		// subscribe �� �� ����ϱ� ���� writer ��ü
		this.writer = receiveWriter;
		this.builder = new StringBuilder();

		connOpts.setCleanSession(true);
		connOpts.setKeepAliveInterval(0);

		this.setCallback(this);

		System.out.println("my clident id: " + clientId);
		connect();
	}

	public void connect() { // target message server�� �����ϴ� �޼���

		try {
			connect(connOpts);

			// ���� �ð� ���
			long t = System.currentTimeMillis(); // ����ð�
			connectTime = dayTime.format(new Date(t));

			subIsRunning = true;
			topic_subscribe(); // 1���� topic ����

		} catch (MqttException e) {
			e.printStackTrace();
		}

		long period = 1000; // 1��

		connectT = new Timer();
		connectTask = new TimerTask() {
			private int count = 0; // 1�ʸ��� count��

			@Override
			public void run() {

				if (Connect.subStop) {
					stop_disconnect();
					return;
				} else {
					if (count < duration) { // ���ӽð� ����������� count ++
						count++;

					} else { // subscriber�� duration�� ����Ǿ��� ���
						if (!Connect.subStop) { // master�� Stop ����� ������ �ʾ��� ��쿡��
							time_disconnect(); // �ð� ��� disconnect �޼��� ȣ��
						}
					}

				}
			}
		};

		connectT.scheduleAtFixedRate(connectTask, 0, period);
	}

	public void topic_subscribe() { // topic ���� �޼���

		String mainTopic = selectTopic(topicList); // ���ȸ���Ʈ �߿� �������� �ϳ� �̱�
		myTopic = mainTopic + "/#"; // �̰� ���ϵ� ī�� �ޱ�

		try {
			subscribe(myTopic, 2); // �����ϱ�
		} catch (MqttException e) {
			e.printStackTrace();
		}

		System.out.println("my topic : " + myTopic);

	}

	public String selectTopic(ArrayList<String> topics) { // topics �迭���� random���� 1���� ���� �̱�
		int topicSize = topics.size();

		double randomValue = Math.random();
		int intValue = (int) (randomValue * topicSize) + 0; // �ִ� topic���� �ּڰ� 0

		String mySelectTopic = topics.get(intValue);
		return mySelectTopic;
	}

	public String getClientId() {
		return clientId;
	}

	public void stop_disconnect() { // stop ����� �޾��� ����� disconnect ���� �޼���

		subIsRunning = false;

		// ��� �۾� ����//
		connectTask.cancel();
		connectT.cancel();
		connectT.purge();
		
		/*// ���� ���� �ð� ���
		long t = System.currentTimeMillis(); // ���� ���� �ð�
		disconnectTime = dayTime.format(new Date(t));

		// ���Ͽ� subscriber ����,���� �ð� ���
		recordSubscriber();
*/
		try {
			disconnect(); // target message server�� client�� disconnect
		} catch (MqttException e) {
			e.printStackTrace();
		}

	}

	public void time_disconnect() { // duration �ð��� ����Ǿ��� ����� disconnect ���� �޼���

		for (int i = Instance.subClientList.size() - 1; i >= 0; i--) {
			// pubClientList���� clientId(�ڱ��ڽ��� id)�� ã��
			if (Instance.subClientList.get(i).getClientId().equals(clientId)) {

				System.out.println("remove in list: " + clientId + "( " + Instance.subClientList.get(i) + " )");
				System.out.println("time over : " + clientId + " is disconnect");
				System.out.println(
						"------------------------------------------------------------------------------------------------");

				// ��� �۾� ����//
				connectTask.cancel();
				connectT.cancel();
				connectT.purge();

				// list�� �ʱ�ȭ���ֱ�
				Instance.subClientList.remove(i);
				Instance.subListIndex.remove(i);

				/*// ���� ���� �ð� ���
				long t = System.currentTimeMillis(); // ���� ���� �ð�
				disconnectTime = dayTime.format(new Date(t));

				// ���Ͽ� subscriber ����,���� �ð� ���
				recordSubscriber();
				*/
				try {
					disconnect(); // target message server�� client�� disconnect
				} catch (MqttException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	/*public void recordSubscriber() { // disconnect �� �� ���Ͽ� ����ϴ� �޼���

		try {
			writer = Files.newBufferedWriter(Paths.get("subLog.csv"), Charset.forName("EUC-KR"),
					StandardOpenOption.APPEND);

			// Subscriber �̸�, connect �ð�(ms), disconnect �ð�(ms), ���ӽð�(ms), ������ ����
			builder.append(clientId).append(",").append(connectTime).append(",").append(disconnectTime).append(",")
					.append(duration).append(",").append(number);

			writer.write(builder.append("\n").toString());
			writer.flush();
			builder.setLength(0);

			number++;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	@Override
	public void connectionLost(Throwable cause) {
		System.out.println("Connection lost!");
		System.out.println("msg " + cause.getMessage());
		System.out.println("loc " + cause.getLocalizedMessage());
		System.out.println("cause " + cause.getCause());
		System.out.println("excep " + cause);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {

		long t = System.currentTimeMillis(); // ���� �ð�
		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		String subTime = dayTime.format(new Date(t));

		String msg = message.toString();

		/*builder.append(msg).append(",").append(clientId).append(",").append(subTime).append(",").append(myTopic)
				.append(",").append(counter);
		writer.write(builder.append("\n").toString());
		writer.flush();
		builder.setLength(0);
		counter++;*/

	}
}
