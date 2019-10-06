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

	private String clientId; // client의 id
	private String topic; // client가 발행(publish)할 메시지의 토픽
	private int duration; // client 연결 지속 시간
	private int msgRate; // client가 1초당 발행(publish)할 메세지 개수

	private MqttMessage message;
	private MqttConnectOptions connOpts = new MqttConnectOptions();

	private StringBuilder builder;
	private BufferedWriter writer;

	static private int counter = 0; // publish 할 때마다 count되는 변수
	static private int number = 0; // 객체 생성때에 count되는 변수

	private String connectTime; // 서버에 연결된 시간
	private String disconnectTime; // 서버에 연결 해제된 시간
	SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS"); // 데이터 포맷

	// target message server에 연결할 때 사용할 timer 객체와 task
	private Timer connectT;
	private TimerTask connectTask;

	// target message server에 message를 발행(publish)할 때 사용할 timer 객체와 task
	private Timer sendT;
	private TimerTask sendTask;

	public static boolean pubIsRunning = false; // 현재 publisher들이 연결되고 있는지 판단하는 변수
	public boolean isRunning = false; // 현재 해당 publisher가 연결되고 있는지 판단하는 변수

	private byte[] msgs = new byte[100]; // 보낼 메세지 byte 형 배열
	// private int payloadSize; // 보낼 메세지의 payload size

	public Publisher(String clientId, String serverURI, MemoryPersistence persistence, String topic, int duration,
			int msgRate, BufferedWriter sendWriter) throws MqttException {

		super(serverURI, clientId, persistence);

		this.clientId = clientId;
		this.topic = topic;
		this.duration = duration;
		this.msgRate = msgRate;

		// publish 할 때 기록하기 위한 writer 객체
		this.writer = sendWriter;
		this.builder = new StringBuilder();

		connOpts.setCleanSession(true);
		connOpts.setKeepAliveInterval(0);

		System.out.println("my clident id: " + clientId);
		System.out.println("my topic : " + topic);

		connect();
		send(this.topic);
	}

	public void connect() { // target message server에 연결하는 메서드

		try {
			connect(connOpts);

			// 연결 시간 기록
			long t = System.currentTimeMillis(); // 연결시간
			connectTime = dayTime.format(new Date(t));

			pubIsRunning = true; // 연결 성공
			isRunning = true;

		} catch (MqttSecurityException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}

		long period = 1000; // 1초

		connectT = new Timer();
		connectTask = new TimerTask() {
			private int count = 0;

			@Override
			public void run() {
				if (Connect.pubStop) {
					stop_disconnect();
					return;
				} else {
					if (count < duration) { // 지속시간 경과전까지는 count ++
						count++;
					} else { // subscriber가 duration이 경과되었을 경우
						if (!Connect.pubStop) { // master가 Stop 명령을 내리지 않았을 경우에만
							time_disconnect(); // 시간 경과 disconnect 메서드 호출
							return;
						}
					}
				}
			}
		};

		connectT.scheduleAtFixedRate(connectTask, 0, period);

	}

	public void send(String topic) { // 메세지를 발행(publish) 하는 메서드

		// payloadSize = selectPayloadSize(); // 랜덤 payload 사이즈를 저장
		// msgs = makePayload(payloadSize); // payload 크기만큼의 사이즈를 가지는 배열을 생성

		sendT = new Timer();
		sendTask = new TimerTask() {

			@Override
			public void run() {
				for (int j = 0; j < msgRate; j++) { // 1초마다 (msgRate)개의 메세지를 발행

					try {

						long t = System.currentTimeMillis(); // 보낸 시간
						String pubTime = dayTime.format(new Date(t));

						String msgText = clientId + "," + pubTime + "," + topic;

						try {
							System.arraycopy(msgText.getBytes(), 0, msgs, 0, msgText.length()); // 메세지 배열에 msgText를 전체복사
						} catch (ArrayIndexOutOfBoundsException ae) {
							System.out.println("warning");
							ae.printStackTrace();
						}

						message = new MqttMessage(msgs);
						message.setQos(2);

						// send 할 때마다 데이터를 파일에 기록 (sendLog.csv)
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
							publish(topic, message); // 메세지 보내기
						}

					} catch (MqttException e) {
						e.printStackTrace();
					}
				}

			}
		};
		long period = 1000;
		long delay = 0;
		sendT.scheduleAtFixedRate(sendTask, delay, period); // 1초마다 동작
	}

	public void stop_disconnect() { // stop 명령을 받았을 경우의 disconnect 관련 메서드

		pubIsRunning = false;
		isRunning = false;

		// 모든 작업 중지//
		sendTask.cancel();
		sendT.cancel();
		sendT.purge();
		connectTask.cancel();
		connectT.cancel();
		connectT.purge();

		// 연결 해제 시간 기록
		long t = System.currentTimeMillis(); // 연결 해제 시간
		disconnectTime = dayTime.format(new Date(t));

		// 파일에 publisher 연결,해제 시간 기록
		recordPublisher();

		try {
			disconnect(); // target message server에 client를 disconnect
		} catch (MqttException e) {
			e.printStackTrace();
		}

	}

	public void time_disconnect() { // duration 시간이 경과되었을 경우의 disconnect 관련 메서드

		isRunning = false;

		for (int i = Instance.pubClientList.size() - 1; i >= 0; i--) {
			// pubClientList에서 clientId(자기자신의 id)을 찾아
			if (Instance.pubClientList.get(i).getClientId().equals(clientId)) {

				// System.out.println("clientList remove! ( " + Instance.pubClientList.get(i) +
				// " )");
				System.out.println("remove in list: " + clientId + "( " + Instance.pubClientList.get(i) + " )");
				System.out.println("time over : " + clientId + " is disconnect");
				System.out.println(
						"------------------------------------------------------------------------------------------------");

				// 모든 작업 중지//
				sendTask.cancel();
				sendT.cancel();
				sendT.purge();
				connectTask.cancel();
				connectT.cancel();
				connectT.purge();

				// list들 초기화해주기
				Instance.pubClientList.remove(i);
				Instance.pubListIndex.remove(i);

				// 연결 해제 시간 기록
				long t = System.currentTimeMillis(); // 연결 해제 시간
				disconnectTime = dayTime.format(new Date(t));

				// 파일에 publisher 연결,해제 시간 기록
				recordPublisher();

				try {
					disconnect(); // target message server에 client를 disconnect
				} catch (MqttException e) {
					e.printStackTrace();
				}
				break;
			}

		}
	}

	public int selectPayloadSize() { // payload 사이즈 랜덤으로 1개 뽑기 (20~30 byte 범위 내 )

		double randomValue = Math.random();
		int intValue = (int) (randomValue * 10) + 20; // 20~30 byte
		return intValue;
	}

	public byte[] makePayload(int size) { // payload 크기만큼의 사이즈를 가지는 배열 만들기
		byte sizeArray[] = new byte[size];
		return sizeArray;
	}

	public void recordPublisher() { // disconnect 한 후 파일에 기록하는 메서드

		try {
			writer = Files.newBufferedWriter(Paths.get("pubLog.csv"), Charset.forName("EUC-KR"),
					StandardOpenOption.APPEND);

			// Publisher 이름, connect 시간(ms), disconnect 시간(ms), 지속시간(ms), 생성된 순서
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
