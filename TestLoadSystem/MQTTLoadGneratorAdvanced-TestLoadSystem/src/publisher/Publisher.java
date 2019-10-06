package publisher;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import main.Connect;

public class Publisher extends MqttClient {

	private String clientId; // client�� id
	private String topic; // client�� ����(publish)�� �޽����� ����
	private int duration; // client ���� ���� �ð�
	private int msgRate; // client�� 1�ʴ� ����(publish)�� �޼��� ����

	private MqttMessage message;
	private MqttConnectOptions connOpts = new MqttConnectOptions();

	private StringBuilder builder;
	private BufferedWriter writer;

	static private int counter = 0; // publish �� ������ count�Ǵ� ����
	static private int number = 0; // ��ü �������� count�Ǵ� ����

	private String connectTime; // ������ ����� �ð�
	private String disconnectTime; // ������ ���� ������ �ð�
	SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS"); // ������ ����

	// target message server�� ������ �� ����� timer ��ü�� task
	private Timer connectT;
	private TimerTask connectTask;

	// target message server�� message�� ����(publish)�� �� ����� timer ��ü�� task
	private Timer sendT;
	private TimerTask sendTask;

	public static boolean pubIsRunning = false; // ���� publisher���� ����ǰ� �ִ��� �Ǵ��ϴ� ����
	public boolean isRunning = false; // ���� �ش� publisher�� ����ǰ� �ִ��� �Ǵ��ϴ� ����

	private byte[] msgs = new byte[100]; // ���� �޼��� byte �� �迭
	// private int payloadSize; // ���� �޼����� payload size

	public Publisher(String clientId, String serverURI, MemoryPersistence persistence, String topic, int duration,
			int msgRate, BufferedWriter sendWriter) throws MqttException {

		super(serverURI, clientId, persistence);

		this.clientId = clientId;
		this.topic = topic;
		this.duration = duration;
		this.msgRate = msgRate;

		// publish �� �� ����ϱ� ���� writer ��ü
		this.writer = sendWriter;
		this.builder = new StringBuilder();

		connOpts.setCleanSession(true);
		connOpts.setKeepAliveInterval(0);

		System.out.println("my clident id: " + clientId);
		System.out.println("my topic : " + topic);

		connect();
		send(this.topic);
	}

	public void connect() { // target message server�� �����ϴ� �޼���

		try {
			connect(connOpts);

			// ���� �ð� ���
			long t = System.currentTimeMillis(); // ����ð�
			connectTime = dayTime.format(new Date(t));

			pubIsRunning = true; // ���� ����
			isRunning = true;

		} catch (MqttSecurityException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}

		long period = 1000; // 1��

		connectT = new Timer();
		connectTask = new TimerTask() {
			private int count = 0;

			@Override
			public void run() {
				if (Connect.pubStop) {
					stop_disconnect();
					return;
				} else {
					if (count < duration) { // ���ӽð� ����������� count ++
						count++;
					} else { // subscriber�� duration�� ����Ǿ��� ���
						if (!Connect.pubStop) { // master�� Stop ����� ������ �ʾ��� ��쿡��
							time_disconnect(); // �ð� ��� disconnect �޼��� ȣ��
							return;
						}
					}
				}
			}
		};

		connectT.scheduleAtFixedRate(connectTask, 0, period);

	}

	public void send(String topic) { // �޼����� ����(publish) �ϴ� �޼���

		// payloadSize = selectPayloadSize(); // ���� payload ����� ����
		// msgs = makePayload(payloadSize); // payload ũ�⸸ŭ�� ����� ������ �迭�� ����

		sendT = new Timer();
		sendTask = new TimerTask() {

			@Override
			public void run() {
				for (int j = 0; j < msgRate; j++) { // 1�ʸ��� (msgRate)���� �޼����� ����

					try {

						long t = System.currentTimeMillis(); // ���� �ð�
						String pubTime = dayTime.format(new Date(t));

						String msgText = clientId + "," + pubTime + "," + topic;

						try {
							System.arraycopy(msgText.getBytes(), 0, msgs, 0, msgText.length()); // �޼��� �迭�� msgText�� ��ü����
						} catch (ArrayIndexOutOfBoundsException ae) {
							System.out.println("warning");
							ae.printStackTrace();
						}

						message = new MqttMessage(msgs);
						message.setQos(2);

						// send �� ������ �����͸� ���Ͽ� ��� (sendLog.csv)
						builder.append(clientId).append(",").append(pubTime).append(",").append(topic).append(",")
								.append(counter);
						try {
							writer.write(builder.append("\n").toString());
							writer.flush();
							builder.setLength(0);
							counter++;
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (!Connect.pubStop && isRunning) {
							publish(topic, message); // �޼��� ������
						}

					} catch (MqttException e) {
						e.printStackTrace();
					}
				}

			}
		};
		long period = 1000;
		long delay = 0;
		sendT.scheduleAtFixedRate(sendTask, delay, period); // 1�ʸ��� ����
	}

	public void stop_disconnect() { // stop ����� �޾��� ����� disconnect ���� �޼���

		pubIsRunning = false;
		isRunning = false;

		// ��� �۾� ����//
		sendTask.cancel();
		sendT.cancel();
		sendT.purge();
		connectTask.cancel();
		connectT.cancel();
		connectT.purge();

		// ���� ���� �ð� ���
		long t = System.currentTimeMillis(); // ���� ���� �ð�
		disconnectTime = dayTime.format(new Date(t));

		// ���Ͽ� publisher ����,���� �ð� ���
		recordPublisher();

		try {
			disconnect(); // target message server�� client�� disconnect
		} catch (MqttException e) {
			e.printStackTrace();
		}

	}

	public void time_disconnect() { // duration �ð��� ����Ǿ��� ����� disconnect ���� �޼���

		isRunning = false;

		for (int i = Instance.pubClientList.size() - 1; i >= 0; i--) {
			// pubClientList���� clientId(�ڱ��ڽ��� id)�� ã��
			if (Instance.pubClientList.get(i).getClientId().equals(clientId)) {

				// System.out.println("clientList remove! ( " + Instance.pubClientList.get(i) +
				// " )");
				System.out.println("remove in list: " + clientId + "( " + Instance.pubClientList.get(i) + " )");
				System.out.println("time over : " + clientId + " is disconnect");
				System.out.println(
						"------------------------------------------------------------------------------------------------");

				// ��� �۾� ����//
				sendTask.cancel();
				sendT.cancel();
				sendT.purge();
				connectTask.cancel();
				connectT.cancel();
				connectT.purge();

				// list�� �ʱ�ȭ���ֱ�
				Instance.pubClientList.remove(i);
				Instance.pubListIndex.remove(i);

				// ���� ���� �ð� ���
				long t = System.currentTimeMillis(); // ���� ���� �ð�
				disconnectTime = dayTime.format(new Date(t));

				// ���Ͽ� publisher ����,���� �ð� ���
				recordPublisher();

				try {
					disconnect(); // target message server�� client�� disconnect
				} catch (MqttException e) {
					e.printStackTrace();
				}
				break;
			}

		}
	}

	public int selectPayloadSize() { // payload ������ �������� 1�� �̱� (20~30 byte ���� �� )

		double randomValue = Math.random();
		int intValue = (int) (randomValue * 10) + 20; // 20~30 byte
		return intValue;
	}

	public byte[] makePayload(int size) { // payload ũ�⸸ŭ�� ����� ������ �迭 �����
		byte sizeArray[] = new byte[size];
		return sizeArray;
	}

	public void recordPublisher() { // disconnect �� �� ���Ͽ� ����ϴ� �޼���

		try {
			writer = Files.newBufferedWriter(Paths.get("pubLog.csv"), Charset.forName("EUC-KR"),
					StandardOpenOption.APPEND);

			// Publisher �̸�, connect �ð�(ms), disconnect �ð�(ms), ���ӽð�(ms), ������ ����
			builder.append(clientId).append(",").append(connectTime).append(",").append(disconnectTime).append(",")
					.append(duration).append(",").append(number);

			writer.write(builder.append("\n").toString());
			writer.flush();
			builder.setLength(0);

			number++;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
