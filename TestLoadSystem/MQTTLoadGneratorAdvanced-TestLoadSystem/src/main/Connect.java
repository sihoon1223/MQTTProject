package main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import publisher.*;
import subscriber.*;

public class Connect {

	public String pcNum; // �� pc ��ȣ
	public String orderPC; // ����� ���� pc ��ȣ
	public int qos = 2;

	public String masterClientId;
	public String pubClientId;
	public String subClientId;

	public MqttClient client;

	public String masterbroker = "tcp://113.198.85.241:1883"; // test load broker
	public String targetbroker = "tcp://113.198.85.153:1883"; // target message broker

	// ���ȵ�//
	public String pubListTopic = "pubList"; // pub ���ȸ���Ʈ�� �޾ƿ� ����
	public String subListTopic = "subList"; // pub ���ȸ���Ʈ�� �޾ƿ� ����
	public String pubTopic = "startPub"; // pub���ø� �˸��� ����
	public String subTopic = "startSub"; // sub���ø� �˸��� ����
	private String pubStopTopic = "pubStop"; // pub stop ����
	private String subStopTopic = "subStop"; // sub stop ����

	public static boolean subStop = false; // publish,subscribe ���� �Ǵ� ����
	public static boolean pubStop = false;

	public ArrayList<String> pubtopics = new ArrayList<String>(); // ���� pub�� ���ȵ��� ����Ʈ
	public ArrayList<String> subtopics = new ArrayList<String>(); // ���� sub�� ���ȵ��� ����Ʈ

	public String factors = null;
	
//	public MyThread thread; //gpio led ����

	public Connect(String broker, String pcNum) {
		this.masterbroker = broker;
		this.pcNum = pcNum;
		masterClientId = pcNum + ":init";
		pubClientId = pcNum + ":publisher";
		subClientId = pcNum + ":subscriber";

		MemoryPersistence persistence = new MemoryPersistence();

		try {

			client = new MqttClient(masterbroker, masterClientId, persistence);

			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			connOpts.setKeepAliveInterval(0);
			client.connect(connOpts);

			System.out.println("Test Load Broker: " + masterbroker + "");
			System.out.println(
					"-------------------------------------------CONNECT----------------------------------------------");

			// ���ȵ� ����//
			client.subscribe(pubListTopic, qos);
			client.subscribe(subListTopic, qos);
			client.subscribe(pubTopic, qos);
			client.subscribe(subTopic, qos);
			client.subscribe(subStopTopic, qos);
			client.subscribe(pubStopTopic, qos);

			client.setCallback(new runCallBack());

		} catch (MqttException me) {

			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);

			me.printStackTrace();
		}

	}

	class runCallBack implements MqttCallback {

		@Override
		public void connectionLost(Throwable cause) {
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {

			if (topic.equals(pubStopTopic)) { // publisher ���� publish�� ���� �� stop, disconnect ���

				//Main.gpio.shutdown();
				
				orderPC = message.toString().split("/")[0];


				if (pcNum.equals(orderPC)) { // �ڽ��� ��ǻ�� ��ȣ�� ��ġ�Ѵٸ�
					
					/*thread.interrupt();
					Main.led_pin.low();
					*/
					long t = System.currentTimeMillis(); // ���� ���� �ð�
					SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS"); // ������ ����
					String disconnectTime = dayTime.format(new Date(t));
					
					System.out.println("Stop to publish (orderPC: " + orderPC + ", myPC: " + pcNum + ")" + disconnectTime);
					
					if (Publisher.pubIsRunning) { // ���� publisher���� �������̶��
						pubStop = true;

						System.out.println(
								"-------------------------------------------STOP------------------------------------------------");
					}

				}
			} else if (topic.equals(subStopTopic)) { // subscriber ���� subscribe�� ���� �� stop, disconnect ���
				
				orderPC = message.toString().split("/")[0];

				if (pcNum.equals(orderPC)) { // �ڽ��� ��ǻ�� ��ȣ�� ��ġ�Ѵٸ�

					/*thread.interrupt();
					Main.led_pin.low();
					*/
					long t = System.currentTimeMillis(); // ���� ���� �ð�
					SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS"); // ������ ����
					String disconnectTime = dayTime.format(new Date(t));
					
					System.out.println("Stop to subscribe (orderPC: " + orderPC + ", myPC: " + pcNum + ")" +disconnectTime);

					if (Subscriber.subIsRunning) { // ���� subscriber���� �������̶��
						subStop = true;

						System.out.println(
								"--------------------------------------------STOP------------------------------------------------");
					}
				}

			} else if (topic.equals(pubListTopic)) { // topic list ���� ������ ���� topics�� ���

				orderPC = message.toString().split("-")[0];

				if (pcNum.equals(orderPC)) {
					
					System.out.println("Publisher's topic list (orderPC: " + orderPC + ", myPC: " + pcNum + ")");

					if (pubtopics != null) { // pubtopics �ʱ�ȭ ���ֱ� (����)
						pubtopics.clear();
					}

					String msg = message.toString().split("-")[1];

					for (int i = 0; i < msg.split(",").length; i++) {
						String str = msg.split(",")[i];
						pubtopics.add(str);
						System.out.println("- " + str);
					}
				}

			} else if (topic.equals(subListTopic)) { // topic list ���� ������ ���� topics�� ���

				orderPC = message.toString().split("-")[0];

				if (pcNum.equals(orderPC)) {
					System.out.println("Subscriber's topic list (orderPC: " + orderPC + ", myPC: " + pcNum + ")");

					if (subtopics != null) { // subtopics �ʱ�ȭ ���ֱ� (����)
						subtopics.clear();
					}

					String msg = message.toString().split("-")[1];

					for (int i = 0; i < msg.split(",").length; i++) {
						String str = msg.split(",")[i];
						subtopics.add(str);
						System.out.println("- " + str);
					}
				}

			} else if (topic.equals(pubTopic)) { // pub���ø� ����ϴ� ������ ���� ���

				factors = message.toString();
				orderPC = factors.split(",")[0]; // master���� ���� pcȮ�� ����

				
				// ���� �� ��ǻ�� ��ȣ�� ���ø� �����ϴ� ��ǻ�� ��ȣ�� ���ٸ�
				if (pcNum.equals(orderPC)) {

//					thread = new MyThread();
//					thread.start();
					System.out.println("factors: " + factors);

					pubStop = false; //pubStop �ʱ�ȭ���ֱ�

					publisher.Instance instance = new publisher.Instance(targetbroker, pubClientId, factors, pubtopics);
					instance.start(); // Instance�� thread ���

				}

			} else if (topic.equals(subTopic)) { // sub���ø� ����ϴ� ������ ���� ���

				factors = message.toString();
				orderPC = factors.split(",")[0]; // master���� ���� pcȮ�� ����
				
				System.out.println("����: "+factors);
			
				// ���� �� ��ǻ�� ��ȣ�� ���ø� �����ϴ� ��ǻ�� ��ȣ�� ���ٸ�
				if (pcNum.equals(orderPC)) {
//					thread = new MyThread();
//					thread.start();
//					
					System.out.println("factors: " + factors);

					subStop = false; //subStop �ʱ�ȭ���ֱ�

					subscriber.Instance instance = new subscriber.Instance(targetbroker, subClientId, factors, subtopics);
					
					instance.start(); // Instance�� thread ���

				}
			}
		}
	}
	
	/*class MyThread extends Thread{
		public void run() {
			try {
				while(true) {
					Main.led_pin.high();
					Thread.sleep(750);
					Main.led_pin.low();
					Thread.sleep(750);
					
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}*/
}
