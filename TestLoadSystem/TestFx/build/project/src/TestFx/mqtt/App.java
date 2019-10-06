package TestFx.mqtt;

import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class App {
	private String broker = "tcp://113.198.85.241:1883"; // test load broker

	private String pubTopic = "startPub"; 
	private String subTopic = "startSub"; 
	private String pubStopTopic = "pubStop"; 
	private String subStopTopic = "subStop"; 
	public String pubListTopic = "pubList"; 
	public String subListTopic = "subList";

	private String clientId;
	private MqttClient client;
	private MemoryPersistence memoryPersistence;

	private String pubfactor = null;
	private String subfactor = null;
	private byte[] topicfactor = null;

	MqttMessage topicMessage = null;
	MqttMessage subMessage;
	MqttMessage pubMessage;

	private String pcName; 
	@SuppressWarnings("unused")
	private String clientType; 
	private String minClientNum; 
	private String maxClientNum;
	private int duration; 
	private int msgRate;
	private int topicNum; 
	private ArrayList<String> topics;

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

		memoryPersistence = new MemoryPersistence();

		connect();

		if (clientType.equals("Subscriber")) { 
			subTopicCommand();
			subCommand(); 
		} else if (clientType.equals("Publisher")) { 
			pubTopicCommand(); 
			pubCommand(); 
		}

	}

	public void connect() {

		try {

			client = new MqttClient(broker, clientId, memoryPersistence);

			MqttConnectOptions connectOptions = new MqttConnectOptions();
			connectOptions.setCleanSession(true);
			client.connect(connectOptions);
			System.out.println(
					"-------------------------------------------CONNECT---------------------------------------------");
			
			
		} catch (MqttException me) {

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

	public void pubTopicCommand() {

		topicfactor = new byte[1024];
		String topic = pcName + "-";
		
		for (int i = 0; i < topics.size(); i++) {
			topic += topics.get(i) + ",";
			topicfactor = topic.getBytes();
		}

		topicMessage = new MqttMessage(topicfactor);
		topicMessage.setQos(2);

		try {
			client.publish(pubListTopic, topicMessage);
			topicfactor = null;

		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void subTopicCommand() {

		topicfactor = new byte[1024];
		String topic = pcName + "-";

		for (int i = 0; i < topics.size(); i++) {
			topic += topics.get(i) + ",";
			topicfactor = topic.getBytes();
		}

		topicMessage = new MqttMessage(topicfactor);
		topicMessage.setQos(2);

		try {
			client.publish(subListTopic, topicMessage);
			topicfactor = null;

		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void subCommand() {

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
