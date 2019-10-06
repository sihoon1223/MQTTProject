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

	// ���� ���ȵ�//
	private String pubTopic = "startPub"; // publisher ������ �����ϴ� ����
	private String subTopic = "startSub"; // subscriber ������ �����ϴ� ����
	private String pubStopTopic = "pubStop"; // publish stop ��� �����ϴ� ����
	private String subStopTopic = "subStop"; // subscribe stop ��� �����ϴ� ����
	public String pubListTopic = "pubList"; // pub ���ȸ���Ʈ�� �޾ƿ� ����
	public String subListTopic = "subList"; // pub ���ȸ���Ʈ�� �޾ƿ� ����

	private String clientId;
	private MqttClient client;
	private MemoryPersistence memoryPersistence;

	// ���ȿ� ��� �޼�����//
	private String pubfactor = null;
	private String subfactor = null;
	private byte[] topicfactor = null;

	// �޼�����//
	MqttMessage topicMessage = null;
	MqttMessage subMessage;
	MqttMessage pubMessage;

	// factor��
	private String pcName; // ����� ���� pc��ȣ
	@SuppressWarnings("unused")
	private String clientType; // client type (publisher or subsrciber)
	private String minClientNum; // ������ Ŭ���̾�Ʈ ��ȣ ������ �ּڰ�
	private String maxClientNum; // ������ Ŭ���̾�Ʈ ��ȣ ������ �ִ�
	private int duration; // Ŭ���̾�Ʈ�� ������ ���� �ð�
	private int msgRate; // publisher�� ��� 1�� (1ms) �� publish�� �޼��� ����
	private int topicNum; // publisher�� ��� ���������� ����
	private ArrayList<String> topics; // ������ ���ȸ���Ʈ
	
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
		if (clientType.equals("Subscriber")) { // client type�� subscriber�� ���
			Device.existSub = true;
			subTopicCommand(); // ���õ� ���ȵ��� ������
			subCommand(); // subscriber ���� ���� ���� �޼��带 ȣ��
		} else if (clientType.equals("Publisher")) { // client type�� publisher�� ���
			pubTopicCommand(); // ���õ� ���ȵ��� ������
			pubCommand(); // publihser ���� ���� ���� �޼��带 ȣ��
		}

	}

	// test load broker�� ����
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

	// ������ ������ ��� pubTopic���� ������������ ����
	// message���� ex) "3-/Hansung/IT,/Hansung"
	public void pubTopicCommand() {

		// ��ü ���� ����� �� �� ������ ������ ��츸 ���
		topicfactor = new byte[1024];
		String topic = pcName + "-";
		
		System.out.println("���ȵ�"+topics);
		for (int i = 0; i < topics.size(); i++) {
			topic += topics.get(i) + ",";
			topicfactor = topic.getBytes();
		}

		// ���ȸ���Ʈ �޼��� ���� ����
		topicMessage = new MqttMessage(topicfactor);
		topicMessage.setQos(2);

		try {
			client.publish(pubListTopic, topicMessage);
			// topic ������ ����
			topicfactor = null;

		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	// ������ ������ ��� subTopic���� ������������ ����
	// message���� ex) "3-/Hansung/IT,/Hansung"
	public void subTopicCommand() {

		// ��ü ���� ����� �� �� ������ ������ ��츸 ���
		topicfactor = new byte[1024];
		String topic = pcName + "-";

		for (int i = 0; i < topics.size(); i++) {
			topic += topics.get(i) + ",";
			topicfactor = topic.getBytes();
		}

		// ���ȸ���Ʈ �޼��� ���� ����
		topicMessage = new MqttMessage(topicfactor);
		topicMessage.setQos(2);

		try {
			client.publish(subListTopic, topicMessage);
			// topic ������ ����
			topicfactor = null;

		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	// subscriber ������ �����϶�� message�� ����
	public void subCommand() {

		System.out.println("�� :"+minClientNum +",��:"+maxClientNum);
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

	// publisher ������ �����϶�� message�� ����
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

	// publisher stop ��� ����
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

	// subscriber stop ��� ����
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
