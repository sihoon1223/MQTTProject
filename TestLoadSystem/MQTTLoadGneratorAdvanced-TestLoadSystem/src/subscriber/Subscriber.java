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

	static private int counter = 0; // subscribe 할 때마다 count되는 변수
	static private int number = 0; // 객체 생성때에 count되는 변수

	private String connectTime; // 서버에 연결된 시간
	private String disconnectTime; // 서버에 연결 해제된 시간
	SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS"); // 데이터 포맷

	private MqttConnectOptions connOpts = new MqttConnectOptions();

	// target message server에 연결할 때 사용할 timer 객체와 task
	private Timer connectT;
	private TimerTask connectTask;

	public static boolean subIsRunning = false; // 현재 subscriber가 연결되고 있는지 판단하는 변수

	public Subscriber(String clientId, String serverURI, MemoryPersistence persistence, ArrayList<String> topicList,
			int duration, BufferedWriter receiveWriter) throws MqttException {

		super(serverURI, clientId, persistence);

		this.clientId = clientId;
		this.topicList = topicList;
		this.duration = duration;

		// subscribe 할 때 기록하기 위한 writer 객체
		this.writer = receiveWriter;
		this.builder = new StringBuilder();

		connOpts.setCleanSession(true);
		connOpts.setKeepAliveInterval(0);

		this.setCallback(this);

		System.out.println("my clident id: " + clientId);
		connect();
	}

	public void connect() { // target message server에 연결하는 메서드

		try {
			connect(connOpts);

			// 연결 시간 기록
			long t = System.currentTimeMillis(); // 연결시간
			connectTime = dayTime.format(new Date(t));

			subIsRunning = true;
			topic_subscribe(); // 1개의 topic 구독

		} catch (MqttException e) {
			e.printStackTrace();
		}

		long period = 1000; // 1초

		connectT = new Timer();
		connectTask = new TimerTask() {
			private int count = 0; // 1초마다 count됨

			@Override
			public void run() {

				if (Connect.subStop) {
					stop_disconnect();
					return;
				} else {
					if (count < duration) { // 지속시간 경과전까지는 count ++
						count++;

					} else { // subscriber가 duration이 경과되었을 경우
						if (!Connect.subStop) { // master가 Stop 명령을 내리지 않았을 경우에만
							time_disconnect(); // 시간 경과 disconnect 메서드 호출
						}
					}

				}
			}
		};

		connectT.scheduleAtFixedRate(connectTask, 0, period);
	}

	public void topic_subscribe() { // topic 구독 메서드

		String mainTopic = selectTopic(topicList); // 토픽리스트 중에 랜덤으로 하나 뽑기
		myTopic = mainTopic + "/#"; // 뽑고 와일드 카드 달기

		try {
			subscribe(myTopic, 2); // 구독하기
		} catch (MqttException e) {
			e.printStackTrace();
		}

		System.out.println("my topic : " + myTopic);

	}

	public String selectTopic(ArrayList<String> topics) { // topics 배열에서 random으로 1개의 토픽 뽑기
		int topicSize = topics.size();

		double randomValue = Math.random();
		int intValue = (int) (randomValue * topicSize) + 0; // 최댓값 topic개수 최솟값 0

		String mySelectTopic = topics.get(intValue);
		return mySelectTopic;
	}

	public String getClientId() {
		return clientId;
	}

	public void stop_disconnect() { // stop 명령을 받았을 경우의 disconnect 관련 메서드

		subIsRunning = false;

		// 모든 작업 중지//
		connectTask.cancel();
		connectT.cancel();
		connectT.purge();
		
		/*// 연결 해제 시간 기록
		long t = System.currentTimeMillis(); // 연결 해제 시간
		disconnectTime = dayTime.format(new Date(t));

		// 파일에 subscriber 연결,해제 시간 기록
		recordSubscriber();
*/
		try {
			disconnect(); // target message server에 client를 disconnect
		} catch (MqttException e) {
			e.printStackTrace();
		}

	}

	public void time_disconnect() { // duration 시간이 경과되었을 경우의 disconnect 관련 메서드

		for (int i = Instance.subClientList.size() - 1; i >= 0; i--) {
			// pubClientList에서 clientId(자기자신의 id)을 찾아
			if (Instance.subClientList.get(i).getClientId().equals(clientId)) {

				System.out.println("remove in list: " + clientId + "( " + Instance.subClientList.get(i) + " )");
				System.out.println("time over : " + clientId + " is disconnect");
				System.out.println(
						"------------------------------------------------------------------------------------------------");

				// 모든 작업 중지//
				connectTask.cancel();
				connectT.cancel();
				connectT.purge();

				// list들 초기화해주기
				Instance.subClientList.remove(i);
				Instance.subListIndex.remove(i);

				/*// 연결 해제 시간 기록
				long t = System.currentTimeMillis(); // 연결 해제 시간
				disconnectTime = dayTime.format(new Date(t));

				// 파일에 subscriber 연결,해제 시간 기록
				recordSubscriber();
				*/
				try {
					disconnect(); // target message server에 client를 disconnect
				} catch (MqttException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	/*public void recordSubscriber() { // disconnect 한 후 파일에 기록하는 메서드

		try {
			writer = Files.newBufferedWriter(Paths.get("subLog.csv"), Charset.forName("EUC-KR"),
					StandardOpenOption.APPEND);

			// Subscriber 이름, connect 시간(ms), disconnect 시간(ms), 지속시간(ms), 생성된 순서
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

		long t = System.currentTimeMillis(); // 받은 시간
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
