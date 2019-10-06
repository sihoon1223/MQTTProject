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

	public String pcNum; // 내 pc 번호
	public String orderPC; // 명령을 받은 pc 번호
	public int qos = 2;

	public String masterClientId;
	public String pubClientId;
	public String subClientId;

	public MqttClient client;

	public String masterbroker = "tcp://113.198.85.241:1883"; // test load broker
	public String targetbroker = "tcp://113.198.85.153:1883"; // target message broker

	// 토픽들//
	public String pubListTopic = "pubList"; // pub 토픽리스트를 받아올 토픽
	public String subListTopic = "subList"; // pub 토픽리스트를 받아올 토픽
	public String pubTopic = "startPub"; // pub개시를 알리는 토픽
	public String subTopic = "startSub"; // sub개시를 알리는 토픽
	private String pubStopTopic = "pubStop"; // pub stop 토픽
	private String subStopTopic = "subStop"; // sub stop 토픽

	public static boolean subStop = false; // publish,subscribe 종료 판단 변수
	public static boolean pubStop = false;

	public ArrayList<String> pubtopics = new ArrayList<String>(); // 내가 pub할 토픽들의 리스트
	public ArrayList<String> subtopics = new ArrayList<String>(); // 내가 sub할 토픽들의 리스트

	public String factors = null;
	
//	public MyThread thread; //gpio led 연결

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

			// 토픽들 구독//
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

			if (topic.equals(pubStopTopic)) { // publisher 들의 publish를 전부 다 stop, disconnect 명령

				//Main.gpio.shutdown();
				
				orderPC = message.toString().split("/")[0];


				if (pcNum.equals(orderPC)) { // 자신의 컴퓨터 번호와 일치한다면
					
					/*thread.interrupt();
					Main.led_pin.low();
					*/
					long t = System.currentTimeMillis(); // 연결 해제 시간
					SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS"); // 데이터 포맷
					String disconnectTime = dayTime.format(new Date(t));
					
					System.out.println("Stop to publish (orderPC: " + orderPC + ", myPC: " + pcNum + ")" + disconnectTime);
					
					if (Publisher.pubIsRunning) { // 현재 publisher들이 구동중이라면
						pubStop = true;

						System.out.println(
								"-------------------------------------------STOP------------------------------------------------");
					}

				}
			} else if (topic.equals(subStopTopic)) { // subscriber 들의 subscribe를 전부 다 stop, disconnect 명령
				
				orderPC = message.toString().split("/")[0];

				if (pcNum.equals(orderPC)) { // 자신의 컴퓨터 번호와 일치한다면

					/*thread.interrupt();
					Main.led_pin.low();
					*/
					long t = System.currentTimeMillis(); // 연결 해제 시간
					SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS"); // 데이터 포맷
					String disconnectTime = dayTime.format(new Date(t));
					
					System.out.println("Stop to subscribe (orderPC: " + orderPC + ", myPC: " + pcNum + ")" +disconnectTime);

					if (Subscriber.subIsRunning) { // 현재 subscriber들이 구동중이라면
						subStop = true;

						System.out.println(
								"--------------------------------------------STOP------------------------------------------------");
					}
				}

			} else if (topic.equals(pubListTopic)) { // topic list 관련 토픽이 오면 topics에 담기

				orderPC = message.toString().split("-")[0];

				if (pcNum.equals(orderPC)) {
					
					System.out.println("Publisher's topic list (orderPC: " + orderPC + ", myPC: " + pcNum + ")");

					if (pubtopics != null) { // pubtopics 초기화 해주기 (비우기)
						pubtopics.clear();
					}

					String msg = message.toString().split("-")[1];

					for (int i = 0; i < msg.split(",").length; i++) {
						String str = msg.split(",")[i];
						pubtopics.add(str);
						System.out.println("- " + str);
					}
				}

			} else if (topic.equals(subListTopic)) { // topic list 관련 토픽이 오면 topics에 담기

				orderPC = message.toString().split("-")[0];

				if (pcNum.equals(orderPC)) {
					System.out.println("Subscriber's topic list (orderPC: " + orderPC + ", myPC: " + pcNum + ")");

					if (subtopics != null) { // subtopics 초기화 해주기 (비우기)
						subtopics.clear();
					}

					String msg = message.toString().split("-")[1];

					for (int i = 0; i < msg.split(",").length; i++) {
						String str = msg.split(",")[i];
						subtopics.add(str);
						System.out.println("- " + str);
					}
				}

			} else if (topic.equals(pubTopic)) { // pub개시를 명령하는 토픽이 왔을 경우

				factors = message.toString();
				orderPC = factors.split(",")[0]; // master에서 보낸 pc확인 변수

				
				// 만약 내 컴퓨터 번호가 개시를 시작하는 컴퓨터 번호와 같다면
				if (pcNum.equals(orderPC)) {

//					thread = new MyThread();
//					thread.start();
					System.out.println("factors: " + factors);

					pubStop = false; //pubStop 초기화해주기

					publisher.Instance instance = new publisher.Instance(targetbroker, pubClientId, factors, pubtopics);
					instance.start(); // Instance는 thread 상속

				}

			} else if (topic.equals(subTopic)) { // sub개시를 명령하는 토픽이 왔을 경우

				factors = message.toString();
				orderPC = factors.split(",")[0]; // master에서 보낸 pc확인 변수
				
				System.out.println("팩터: "+factors);
			
				// 만약 내 컴퓨터 번호가 개시를 시작하는 컴퓨터 번호와 같다면
				if (pcNum.equals(orderPC)) {
//					thread = new MyThread();
//					thread.start();
//					
					System.out.println("factors: " + factors);

					subStop = false; //subStop 초기화해주기

					subscriber.Instance instance = new subscriber.Instance(targetbroker, subClientId, factors, subtopics);
					
					instance.start(); // Instance는 thread 상속

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
