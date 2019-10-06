package TestFx.mqtt;

import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import TestFx.controller.MainApplicationController;
import TestFx.model.Device;

public class App {
	private String broker = "tcp://"+MainApplicationController.masterIP+":1883"; // test load broker

	// 여러 토픽들//
	private String pubTopic = "startPub"; // publisher 생성을 지시하는 토픽
	private String subTopic = "startSub"; // subscriber 생성을 지시하는 토픽
	private String pubStopTopic = "pubStop"; // publish stop 명령 지시하는 토픽
	private String subStopTopic = "subStop"; // subscribe stop 명령 지시하는 토픽
	public String pubListTopic = "pubList"; // pub 토픽리스트를 받아올 토픽
	public String subListTopic = "subList"; // pub 토픽리스트를 받아올 토픽

	private String clientId;
	private MqttClient client;
	private MemoryPersistence memoryPersistence;

	// 토픽에 담길 메세지들//
	private String pubfactor = null;
	private String subfactor = null;
	private byte[] topicfactor = null;

	// 메세지들//
	MqttMessage topicMessage = null;
	MqttMessage subMessage;
	MqttMessage pubMessage;

	// factor들
	private String pcName; // 명령을 받을 pc번호
	@SuppressWarnings("unused")
	private String clientType; // client type (publisher or subsrciber)
	private String minClientNum; // 생성될 클라이언트 번호 범위의 최솟값
	private String maxClientNum; // 생성될 클라이언트 번호 범위의 최댓값
	private int duration; // 클라이언트가 유지할 지속 시간
	private int msgRate; // publisher일 경우 1초 (1ms) 당 publish할 메세지 개수
	private int topicNum; // publisher일 경우 하위토픽의 개수
	private ArrayList<String> topics; // 선택한 토픽리스트
	
	public boolean isConnect = false;

	public App(String pcName, String clientType, String min, String max, String duration, String msgRate,
			String topicNum, ArrayList<String> topics) {

		this.pcName = pcName;
		this.clientType = clientType;
		this.minClientNum = min;
		this.maxClientNum = max;
		this.duration = Integer.parseInt(duration);
		this.msgRate = Integer.parseInt(msgRate);
		this.topicNum = Integer.parseInt(topicNum);
		this.topics = topics;

		clientId = pcName + ":master";
		System.out.println("App;pcName:"+pcName);

		memoryPersistence = new MemoryPersistence();

		connect();
		
		System.out.println(isConnect);
		if (clientType.equals("Subscriber")) { // client type이 subscriber일 경우
			Device.existSub = true;
			subTopicCommand(); // 선택된 토픽들을 보내고
			subCommand(); // subscriber 생성 지시 관련 메서드를 호출
		} else if (clientType.equals("Publisher")) { // client type이 publisher일 경우
			pubTopicCommand(); // 선택된 토픽들을 보내고
			pubCommand(); // publihser 생성 지시 관련 메서드를 호출
		}

	}

	// test load broker에 연결
	public void connect() {

		try {

			client = new MqttClient(broker, clientId, memoryPersistence);

			MqttConnectOptions connectOptions = new MqttConnectOptions();
			connectOptions.setCleanSession(true);
			client.connect(connectOptions);
			System.out.println(
					"-------------------------------------------CONNECT---------------------------------------------");
			isConnect = true;
			
		} catch (MqttException me) {
			
			isConnect = false;

			System.out.print("The connection failed (");
			System.out.print("reason : " + me.getReasonCode() + ",");
			System.out.print("msg : " + me.getMessage() + ",");
			System.out.print("loc : " + me.getLocalizedMessage() + ",");
			System.out.print("cause : " + me.getCause() + ",");
			System.out.print("excep : " + me + ")");
			me.printStackTrace();
			System.out.println("The connection failed.");
		}

	}

	// 선택한 토픽을 담아 pubTopic으로 토픽종류들을 전송
	// message형태 ex) "3-/Hansung/IT,/Hansung"
	public void pubTopicCommand() {

		// 전체 토픽 경우의 수 중 선택한 토픽의 경우만 담기
		topicfactor = new byte[1024];
		String topic = pcName + "-";
		
		System.out.println("토픽들"+topics);
		for (int i = 0; i < topics.size(); i++) {
			topic += topics.get(i) + ",";
			topicfactor = topic.getBytes();
		}

		// 토픽리스트 메세지 먼저 전송
		topicMessage = new MqttMessage(topicfactor);
		topicMessage.setQos(2);

		try {
			client.publish(pubListTopic, topicMessage);
			// topic 보내고 비우기
			topicfactor = null;

		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	// 선택한 토픽을 담아 subTopic으로 토픽종류들을 전송
	// message형태 ex) "3-/Hansung/IT,/Hansung"
	public void subTopicCommand() {

		// 전체 토픽 경우의 수 중 선택한 토픽의 경우만 담기
		topicfactor = new byte[1024];
		String topic = pcName + "-";

		for (int i = 0; i < topics.size(); i++) {
			topic += topics.get(i) + ",";
			topicfactor = topic.getBytes();
		}

		// 토픽리스트 메세지 먼저 전송
		topicMessage = new MqttMessage(topicfactor);
		topicMessage.setQos(2);

		try {
			client.publish(subListTopic, topicMessage);
			// topic 보내고 비우기
			topicfactor = null;

		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	// subscriber 생성을 지시하라는 message를 보냄
	public void subCommand() {

		System.out.println("민 :"+minClientNum +",맥:"+maxClientNum);
		subfactor = pcName + "," + minClientNum + "," + maxClientNum + "," + duration;
		subMessage = new MqttMessage(subfactor.getBytes());
		subMessage.setQos(2);

		try {

			client.publish(subTopic, subMessage);

		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}

	}

	// publisher 생성을 지시하라는 message를 보냄
	public void pubCommand() {

		pubfactor = pcName + "," + minClientNum + "," + maxClientNum + "," + duration + "," + msgRate + ","
				+ topicNum;
		pubMessage = new MqttMessage(pubfactor.getBytes());
		pubMessage.setQos(2);

		try {
			
			client.publish(pubTopic, pubMessage);
			
		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}

	}

	// publisher stop 명령 지시
	public void pubStopCommand() {

		String stopStr = pcName + "/pubStop";
		MqttMessage stopMsg = new MqttMessage(stopStr.getBytes());

		try {
			client.publish(pubStopTopic, stopMsg);
		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}

		System.out.println(
				"---------------------------------------------STOP----------------------------------------------");

	}

	// subscriber stop 명령 지시
	public void subStopCommand() {

		String stopStr = pcName + "/subStop";
		MqttMessage stopMsg = new MqttMessage(stopStr.getBytes());

		try {
			client.publish(subStopTopic, stopMsg);
		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}

		System.out.println(
				"---------------------------------------------STOP----------------------------------------------");

	}

}
