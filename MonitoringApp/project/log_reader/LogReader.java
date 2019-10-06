package log_reader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Properties;
import java.sql.*;

/* Mosquitto broker 로그를 읽고 트래픽 데이터를 수집하여 DB에 저장하는 클래스 */
public class LogReader implements Runnable {
	
	private int interval; // interval(초) 시간 간격으로 DB 업데이트
	private int numberOfRecords; // 지정한 개수 만큼 DB 테이블 레코드 유지
	
	// 연결 중인 클라이언트 id를 저장하는 Vector
	private Vector<String> clients = new Vector<String>();

	// 클라이언트 id가 [key], 구독한 토픽과 구독 정보를 가지는 Subscriber 클래스가 [value]인 Map
	private ConcurrentHashMap<String, Subscriber> subscribers = new ConcurrentHashMap<>();

	// 클라이언트 id가 [key], 발행한 토픽과 발행 정보를 가지는 Publisher 클래스가 [value]인 Map
	private ConcurrentHashMap<String, Publisher> publishers = new ConcurrentHashMap<>();

	// 클라이언트 id가 [key], 토픽 정보를 가지는 Topic 클래스가 [value]인 Map
	private ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<>();

	private String minThroughputClientId = ""; // 가장 주고 받은 메시지 크기가 작은 클라이언트 id를 저장하는 변수
	private String maxThroughputClientId = ""; // 가장 주고 받은 메시지 크기가 큰 클라이언트 id를 저장하는 변수

	private Vector<String> topicList = new Vector<>(); // DB의 전체 토픽 목록이 저장된 Vector
	private Vector<String> topicListKor = new Vector<>(); // 토픽의 한글 이름이 저장된 Vector

	private String[] tableList = { "client", "topic", "subscription", "publication", "connection_info" };

	private String driver;
	private String url;
	private String user;
	private String pw;

	public LogReader(int interval, int numberOfRecords) {

		this.interval = interval;
		this.numberOfRecords = numberOfRecords;

		String inputStr;

		BufferedReader bufferedReader = null, bufferedReader2 = null;
		FileReader fileReader = null, fileReader2 = null;

		// 전체 토픽 목록을 받아옴
		try {
			fileReader = new FileReader("topic_list.txt");
			fileReader2 = new FileReader("topic_list_kor.txt");
			bufferedReader = new BufferedReader(fileReader);
			bufferedReader2 = new BufferedReader(fileReader2);

			while((inputStr=bufferedReader.readLine())!=null) 
				topicList.add(inputStr);

			while((inputStr=bufferedReader2.readLine())!=null) 
				topicListKor.add(inputStr);
			
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}  finally {
			if(bufferedReader != null) try { bufferedReader.close(); } catch (IOException e) { }
			if(bufferedReader2 != null) try { bufferedReader2.close(); } catch (IOException e) { }
			if(fileReader != null) try { fileReader.close(); } catch (IOException e) { }
			if(fileReader2 != null) try { fileReader2.close(); } catch (IOException e) { }
		}

		// read db.properties
		readProperties();
	
		// 모든 DB 테이블 비우기
		Connection conn=null;
		PreparedStatement pstmt=null;

		try {
			Class.forName(driver);
			conn=DriverManager.getConnection(url, user, pw);

			pstmt=conn.prepareStatement("DELETE FROM client");
			pstmt.executeUpdate();

			pstmt=conn.prepareStatement("DELETE FROM connection_info");
			pstmt.executeUpdate();

			pstmt=conn.prepareStatement("DELETE FROM subscription");
			pstmt.executeUpdate();

			pstmt=conn.prepareStatement("DELETE FROM publication");
			pstmt.executeUpdate();

			pstmt=conn.prepareStatement("DELETE FROM topic");
			pstmt.executeUpdate();

			// DB에 토픽 저장
			String sql="INSERT INTO topic(topic, topic_kor, message_receiving_count, message_sending_count, accumulated_msg_size) VALUES";

			boolean first=true;

			for (int i = 0; i < topicList.size(); i++) {

				if(!first)
					sql+=", ";

				sql+="(\'"+topicList.get(i)+"\', \'"+topicListKor.get(i)+"\', "+0+", "+0+", "+0+")";

				first=false;
			}

			pstmt=conn.prepareStatement(sql);
			int r=pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("l:1:sql query error");
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			System.out.println("driver error");
		} finally {
			if(pstmt!=null) try { pstmt.close(); } catch (SQLException e) { }
			if(conn!=null) try { conn.close(); } catch (SQLException e) { }
		}
		
		//table_info update
		conn=null;
		pstmt=null;

		try {
			Class.forName(driver);
			conn=DriverManager.getConnection(url, user, pw);

			String sql="INSERT INTO table_info VALUES ";
			boolean first=true;
			int isAccumulated;
			int updateUnit=24;

			for(int i=0; i<tableList.length ;i++) {
				
				if(tableList[i].equals("connection_info"))
					isAccumulated=1;
				else
					isAccumulated=0;
				
				if(!first)
					sql+=", ";

				sql+="(\'"+tableList[i]+"\', "+interval+", "+isAccumulated+", "+updateUnit+")";
				
				first=false;
			}

			sql+=" ON DUPLICATE KEY UPDATE update_interval=VALUES(update_interval)";

			pstmt=conn.prepareStatement(sql);
			pstmt.executeUpdate();

		} catch (ClassNotFoundException e) {
			System.out.println("driver error");
		} catch (SQLException e1) {
			System.out.println("l:2:sql query error");
			e1.printStackTrace();
		} finally {
			if(pstmt!=null) try { pstmt.close(); } catch (SQLException e) { }
			if(conn!=null) try { conn.close(); } catch (SQLException e) { }
		}
	}

	/* read db.properties */
	public void readProperties() {
		Properties props = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream("db.properties");

			props.load(is);

		} catch(IOException e) {
			e.printStackTrace();
			return;
		} finally {
			if(is != null) try { is.close(); } catch (IOException e) { }
		}

		driver = props.getProperty("jdbc.driver");
		url = props.getProperty("jdbc.url");
		user = props.getProperty("jdbc.username");
		pw = props.getProperty("jdbc.password");
	}
	
	@Override
	public void run() {

		startTimer(); // 타이머 시작
		read(); // Mosquitto broker 로그 읽기 시작
	}
	
	/* Timer, TimerTask를 이용하여 주기적으로 DB 업데이트 */
	public void startTimer() {
		System.out.println("LogReader started at " + getCurrentTime());

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {

				//System.out.println("LogReader " + getCurrentTime());
				print();

				// 트래픽 데이터 관련 DB 테이블 업데이트
				updateClientTable();
				updateConnectionInfoTable();
				updateSubscriptionTable();
				updatePublicationTable();
				updateTopicTable();
			}
		};
		timer.schedule(task, 0, interval * 1000);
	}

	/* client 테이블 업데이트 */
	public void updateClientTable() {

		String clientId="";
		Subscriber s;
		Publisher p;

		if(clients.size()==0)
			return;

		Connection conn=null;
		PreparedStatement pstmt=null;

		try {
			Class.forName(driver);
			conn=DriverManager.getConnection(url, user, pw);

			String sql="INSERT INTO client(id, number_of_messages) VALUES";
			boolean first=true;

			// INSERT 쿼리 문에 연결 중인 클라이언트 id 추가
			for (int i = 0; i < clients.size(); i++) {
				clientId = clients.get(i);

				if(!first)
					sql+=", ";

				sql+="(\'"+clientId+"\', ";

				// 1) subscriber 2) publisher 3) None 인 경우를 구별
				if((s=subscribers.get(clientId))!=null)
					sql+=s.getNumberOfMessages()+")";
				else if((p=publishers.get(clientId))!=null)
					sql+=p.getNumberOfMessages()+")";
				else
					sql+=0+")";
				
				first=false;
			}
			
			sql+=" ON DUPLICATE KEY UPDATE number_of_messages=VALUES(number_of_messages)"; // 이미 레코드에 존재하는 프로세스 id이면 UPDATE

			// client 테이블에 클라이언트에 대한 정보를 삽입
			pstmt=conn.prepareStatement(sql);
			int r=pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("sql create query error");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("driver error");
		} finally {
			if(pstmt!=null) try { pstmt.close(); } catch (SQLException e) { }
			if(conn!=null) try { conn.close(); } catch (SQLException e) { }
		}
	}

	/* connection_info 테이블 업데이트 */
	public void updateConnectionInfoTable() {

		/* ----- 트래픽 데이터 수집 ----- */
		int numberOfCurrentConnections=0; // 현재 연결된 클라이언트 수를 저장하는 변수
		int max = Integer.MIN_VALUE; // Integer의 최대값을 저장하는 변수
		int min = Integer.MAX_VALUE; // Integer의 최소값을 저장하는 변수
		String resentClientId=""; // 최근 연결된 클라이언트 id를 저장하는 변수
		String oldClientId=""; // 가장 오래 연결된 클라이언트 id를 저장하는 변수

		maxThroughputClientId = "";
		minThroughputClientId = "";

		if(clients.size() != 0) {
			numberOfCurrentConnections = clients.size();
			resentClientId = clients.lastElement();
			oldClientId = clients.firstElement();
		}

		// subscriber 중 주고 받은 메시지 크기가 가장 크거나 가장 작은 클라이언트 id 찾기 
		for (Subscriber s : subscribers.values()) {
			if (max < s.getThroughput() && clients.contains(s.getClientId())) {
				maxThroughputClientId = s.getClientId();
				max = s.getThroughput();
			}
			if (min > s.getThroughput() && clients.contains(s.getClientId())) {
				minThroughputClientId = s.getClientId();
				min = s.getThroughput();
			}
		}

		// publisher 중 주고 받은 메시지 크기가 가장 크거나 가장 작은 클라이언트 id 찾기 
		for (Publisher p : publishers.values()) {
			if (max < p.getThroughput() && clients.contains(p.getClientId())) {
				maxThroughputClientId = p.getClientId();
				max = p.getThroughput();
			}
			if (min > p.getThroughput() && clients.contains(p.getClientId())) {
				minThroughputClientId = p.getClientId();
				min = p.getThroughput();
			}
		}


		/* ----- 트래픽 데이터 DB에 저장 ----- */
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		try {
			Class.forName(driver);
			conn=DriverManager.getConnection(url, user, pw);

			// connection_info 테이블의 레코드 개수를 알아내기 위한 쿼리 실행
			pstmt=conn.prepareStatement("SELECT COUNT(*) AS count FROM connection_info");

			rs=pstmt.executeQuery();
			rs.next();

			// 현재 connection_info 테이블의 레코드 개수가 numberOfRecords 값보다 크면 가장 오래된 레코드 삭제
			if(rs.getInt("count") >= numberOfRecords) {

				pstmt=conn.prepareStatement("DELETE FROM connection_info ORDER BY date ASC LIMIT 1");

				int r=pstmt.executeUpdate();
			}

			// connection_info 테이블에 트래픽 데이터와 현재 시간을 삽입
			pstmt=conn.prepareStatement("INSERT INTO connection_info(number_of_current_connections, recent_client_id, old_client_id, client_id_of_minimum_msg, client_id_of_maximum_msg, date) VALUES(?, ?, ?, ?, ?, ?)");

			pstmt.setInt(1, numberOfCurrentConnections);
			pstmt.setString(2, resentClientId);
			pstmt.setString(3, oldClientId);
			pstmt.setString(4, minThroughputClientId);
			pstmt.setString(5, maxThroughputClientId);
			pstmt.setString(6, getCurrentTime());

			int r=pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("l:3:sql query error");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("driver error");
		} finally {
			if(rs!=null) try { rs.close(); } catch (SQLException e) { }
			if(pstmt!=null) try { pstmt.close(); } catch (SQLException e) { }
			if(conn!=null) try { conn.close(); } catch (SQLException e) { }
		}
		
	}

	/* subscription 테이블 업데이트 */
	public void updateSubscriptionTable() {
		Vector<Subscription> subscriptions;

		if(subscribers.size()==0)
			return;
		
		Connection conn=null;
		PreparedStatement pstmt=null;

		try {
			Class.forName(driver);
			conn=DriverManager.getConnection(url, user, pw);

			// subscription 테이블에 subscriber(클라이언트) id와 토픽 이름을 삽입
			pstmt=conn.prepareStatement("INSERT INTO subscription(client_id, subscription_topic, subscription_topic_kor) select ?,?,? from dual where not exists ( select * from subscription where client_id=? and subscription_topic=? and subscription_topic_kor=?)");

			for (Subscriber s : subscribers.values()) {
				subscriptions=s.getSubscriptions();

				for(Subscription sub : subscriptions) {

					pstmt.setString(1, s.getClientId());
					pstmt.setString(2, sub.getTopic());
					pstmt.setString(3, sub.getTopicKor());

					pstmt.setString(4, s.getClientId());
					pstmt.setString(5, sub.getTopic());
					pstmt.setString(6, sub.getTopicKor());

					pstmt.addBatch(); // 쿼리를 batch에 담기
					pstmt.clearParameters(); // paramter 초기화
				}
			}

		} catch (SQLException e) {
			System.out.println("sql update subscription query error");
		} catch (ClassNotFoundException e) {
			System.out.println("driver error");
		} finally {
			if(pstmt!=null)
				try {
					pstmt.executeBatch(); // batch에 담은 쿼리 내용을 한번에 실행
					pstmt.clearBatch(); // batch 초기화
					pstmt.close();
				} catch (SQLException e) { }

			if(conn!=null) try { conn.close(); } catch (SQLException e) { }
		}
	}

	/* publication 테이블 업데이트 */
	public void updatePublicationTable() {
		Vector<Publication> publications;

		if(publishers.size()==0)
			return;

		Connection conn=null;
		PreparedStatement pstmt=null;

		try {
			Class.forName(driver);
			conn=DriverManager.getConnection(url, user, pw);

			// publication 테이블에 publisher(클라이언트) id와 토픽 이름을 삽입
			pstmt=conn.prepareStatement("INSERT INTO publication(client_id, publication_topic) select ?,? from dual where not exists ( select * from publication where client_id=? and publication_topic=?)");

			for (Publisher p : publishers.values()) {
				publications=p.getPublications();

				for(Publication pub : publications) {

					pstmt.setString(1, p.getClientId());
					pstmt.setString(2, pub.getTopic());

					pstmt.setString(3, p.getClientId());
					pstmt.setString(4, pub.getTopic());

					pstmt.addBatch(); // 쿼리를 batch에 담기
					pstmt.clearParameters(); // paramter 초기화
				}
			}

		} catch (SQLException e) {
			System.out.println("sql update publication query error");
		} catch (ClassNotFoundException e1) {
			System.out.println("driver error");
		} finally {
			if(pstmt!=null)
				try {
					pstmt.executeBatch(); // batch에 담은 쿼리 내용을 한번에 실행
					pstmt.clearBatch(); // batch 초기화
					pstmt.close();
				} catch (SQLException e) { }

			if(conn!=null) try { conn.close(); } catch (SQLException e) { }
		}
	}

	/* topic 테이블 업데이트 */
	public void updateTopicTable() {
		Connection conn=null;
		PreparedStatement pstmt=null;

		try {
			Class.forName(driver);
			conn=DriverManager.getConnection(url, user, pw);

			// topic 테이블의 메시지 송수신 횟수, 누적 메시지 크기 업데이트
			pstmt=conn.prepareStatement("UPDATE topic SET message_receiving_count=?, message_sending_count=?, accumulated_msg_size=? WHERE topic=?");

			for (Topic t : topics.values()) {

				pstmt.setInt(1, t.getTotalReceivingCount());
				pstmt.setInt(2, t.getTotalSendingCount());
				pstmt.setInt(3, t.getTotalThroughput());
				pstmt.setString(4, t.getName());

				pstmt.addBatch(); // 쿼리를 batch에 담기
				pstmt.clearParameters(); // paramter 초기화
			}

		} catch (SQLException e) {
			System.out.println("l:4:sql query error");
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			System.out.println("driver error");
		} finally {
			if(pstmt!=null)
				try {
					pstmt.executeBatch(); // batch에 담은 쿼리 내용을 한번에 실행
					pstmt.clearBatch(); // batch 초기화
					pstmt.close();
				} catch (SQLException e) { }

			if(conn!=null) try { conn.close(); } catch (SQLException e) { }
		}
	}

	/* client 테이블 레코드 삭제 */
	public void deleteFromClientTable(String clientId) {
		Connection conn=null;
		PreparedStatement pstmt=null;

		try {
			Class.forName(driver);
			conn=DriverManager.getConnection(url, user, pw);

			//연결 해제 된 클라이언트 정보를 client 테이블에서 삭제
			pstmt=conn.prepareStatement("DELETE FROM client where id=?");

			pstmt.setString(1, clientId);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("sql delete query error(client id : "+clientId);
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			System.out.println("driver error");
		} finally {
			if(pstmt!=null) try { pstmt.close(); } catch (SQLException e) { }
			if(conn!=null) try { conn.close(); } catch (SQLException e) { }
		}
	}

	/* Mosquitto broker 로그를 읽고 트래픽 데이터를 수집 */
	public void read() { 

		InputStreamReader inputStreamReader = new InputStreamReader(System.in);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader); 

		String inputStr;
		String[] words;
		boolean isContainTopic = false;

		String clientId = "", topic;
		int messageSize;

		try {
			while (true) {
			//try {
				inputStr = null;
				if ((inputStr = bufferedReader.readLine()) != null) {

					if (inputStr.contains(MessageType.CONNECT)) { /* 클라이언트가 연결 되었을 때 */

						words = inputStr.split(" ");
						clientId = words[4]; // 연결 된 클라이언트 id를 저장하는 변수

						if (!clients.contains(clientId))
							clients.add(clientId); // clients에 클라이언트 id 추가

					} else if (inputStr.contains(MessageType.DISCONNECT)) { /* 클라이언트가 연결 해제 되었을 때 */

						words = inputStr.split(" ");
						clientId = words[4]; // 연결 해제된 클라이언트 id를 저장하는 변수

						clients.remove(clientId); // clients에서 클라이언트 id 제거

						if (subscribers.containsKey(clientId))
							subscribers.get(clientId).clearSub(); // subscriber이면 subscriber의 구독 정보 초기화
						if (publishers.containsKey(clientId))
							publishers.get(clientId).clearPub(); // publisher이면 publisher의 발행 정보 초기화

						deleteFromClientTable(clientId); // client 테이블에서 클라이언트 id를 삭제

					} else if (inputStr.contains(MessageType.SOCKET_ERROR)) { /* 소켓 에러가 일어나 강제 연결 해제 되었을 때 */

						words = inputStr.split(" ");
						clientId = words[5].replace(",", ""); // 연결 해제된 클라이언트 id를 저장하는 변수

						clients.remove(clientId); // clients에서 클라이언트 id 제거

						if (subscribers.containsKey(clientId))
							subscribers.get(clientId).clearSub(); // subscriber이면 subscriber의 구독 정보 초기화
						if (publishers.containsKey(clientId))
							publishers.get(clientId).clearPub(); // publisher이면 publisher의 발행 정보 초기화

						deleteFromClientTable(clientId); // client 테이블에서 클라이언트 id를 삭제

					} else if (inputStr.contains(MessageType.SUBSCRIBE) || isContainTopic) { /* 클라이언트가 토픽을 구독(subscribe)할 때 */

						if (isContainTopic) { 
							words = inputStr.split("\\s+");
							topic = words[1]; // 구독하는 토픽 이름을 저장하는 변수

							Subscriber s;

							// subscribers에 이미 있는 클라이언트 id인지 확인
							if (!subscribers.containsKey(clientId))
								s = new Subscriber(clientId);
							else
								s = subscribers.get(clientId);

							// 구독하는 토픽에 '#'이 포함되면 하위 토픽 모두 구독하도록 설정
							if (topic.contains("#")) {
								String pre=topic.replace("/#","");
								String t, tKor;

								// topic list와 관련 없는 토픽인지 확인
								if(!topicList.contains(pre)) {
									isContainTopic = false;
									continue;
								}

								for(int i=0;i<topicList.size();i++) { 
									t=topicList.get(i);
									tKor=topicListKor.get(i);

									if(t.contains(pre)) {
										s.subscribe(t, tKor); // 하위 토픽이면 구독
					
										// 사용 중인 토픽 목록(topics)에 하위 토픽을 추가
										if(!topics.containsKey(t))
											topics.put(t, new Topic(t));
									}
								}

								s.useSharp(); // 토픽에 '#'이 포함 됨을 명시

							} else {

								// topic list와 관련 없는 토픽인지 확인
								if(!topicList.contains(topic)) {
									isContainTopic = false;
									continue;
								}

								String topicKor="";

								for(int i=0;i<topicList.size();i++)
									if(topicList.get(i).equals(topic))
										topicKor=topicListKor.get(i);

								// 해당 토픽을 구독
								s.subscribe(topic, topicKor);

								// 사용 중인 토픽 목록(topics)에 해당 토픽을 추가
								if (!topics.containsKey(topic))
									topics.put(topic, new Topic(topic));
							}

							subscribers.put(clientId, s); // subscriber를 subscribers에 추가

							isContainTopic = false;

						} else {
							words = inputStr.split(" ");
							clientId = words[4]; // subscriber(클라이언트) id를 저장하는 변수

							isContainTopic = true; // 구독하는 토픽 이름이 써 있는 다음 줄을 필수적으로 읽어야 하기 때문에 관련 변수의 값을 변환시킴
						}

					} else if (inputStr.contains(MessageType.RECEIVED_PUBLISH)) { /* publisher가 메시지를 보냈을 때 */

						words = inputStr.split(" ");
						clientId = words[4]; // publisher(클라이언트) id를 저장하는 변수
						topic = words[9].replaceAll("\'|,", ""); // 발행하는 토픽 이름을 저장하는 변수
						messageSize = Integer.parseInt(words[11].replace("(", "")); // 보낸 메시지 크기를 저장하는 변수


						// 하위 토픽이 아니라 아예 list와 관련없는 토픽일 경우 따로 처리
						// '/A/B/1' ---> '/A/B'로 만들어 '/A/B' 토픽에 대한 정보를 업데이트

						if(!topicList.contains(topic)) {
							words = topic.split("/");

							try {
								int n = Integer.parseInt(words[words.length-1]);
							} catch (NumberFormatException e) { System.out.println("received publish"); continue; }

							topic="";

							for(int i=1;i<words.length-1;i++)
								topic+="/"+words[i];
						}


						Publisher p;

						// publishers에 이미 있는 클라이언트 id인지 확인
						if (!publishers.containsKey(clientId))
							p = new Publisher(clientId);
						else
							p = publishers.get(clientId);

						p.publish(topic, messageSize); // 해당 토픽으로 메시지를 발행함
						publishers.put(clientId, p); // publisher를 publishers에 추가


						Topic t = topics.get(topic);
						if (t == null)
							t = new Topic(topic);
						t.increaseSendingInfo(messageSize); // 토픽에 대한 누적 메시지 크기를 증가시킴

						topics.put(topic, t); // topics를 업데이트

					} else if (inputStr.contains(MessageType.SENDING_PUBLISH)) { /* subscriber에게 메시지를 보냈을 때 */

						words = inputStr.split(" ");
						clientId = words[4]; // subscriber(클라이언트) id를 저장하는 변수
						topic = words[9].replaceAll("\'|,", ""); // 구독한 토픽 이름을 저장하는 변수
						messageSize = Integer.parseInt(words[11].replace("(", "")); // 받은 메시지 크기를 저장하는 변수


						// 하위 토픽이 아니라 아예 list와 관련없는 토픽일 경우 따로 처리
						// '/A/B/1' ---> '/A/B'로 만들어 '/A/B' 토픽에 대한 정보를 업데이트
						if(!topicList.contains(topic)) {
							words = topic.split("/");

							try {
								int n = Integer.parseInt(words[words.length-1]);
							} catch (NumberFormatException e) { System.out.println("sending publish"); continue; }

							topic="";

							for(int i=1;i<words.length-1;i++)
								topic+="/"+words[i];
						}


						Subscriber s = (Subscriber) subscribers.get(clientId);
						s.receivedMessage(topic, messageSize); // 구독 중인 토픽에 대한 정보를 업데이트

						subscribers.replace(clientId, s); // subscribers를 업데이트


						Topic t = topics.get(topic);
						t.increaseReceivingInfo(messageSize); // 토픽에 대한 누적 메시지 크기를 증가시킴
						topics.put(topic, t); // topics를 업데이트
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(bufferedReader != null) try { bufferedReader.close(); } catch (IOException e) { }
			if(inputStreamReader != null) try { inputStreamReader.close(); } catch (IOException e) { }
		}
	}

	/* 현재 시간 구하기 */
	public String getCurrentTime() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return sdf.format(d);
	}
	
	/* 트래픽 데이터 간략하게 출력 */
	public void print() {
		System.out.println("-------------------LogReader---------------------");
		System.out.println("current time : " + getCurrentTime());
		System.out.println("number of clients : " + clients.size());

		if(clients.size() != 0) {
			System.out.println("recent client id : " + clients.lastElement());
			System.out.println("old client id : " + clients.firstElement());
		}

		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;
		maxThroughputClientId = "";
		minThroughputClientId = "";

		for (Subscriber s : subscribers.values()) {
			if (max < s.getThroughput() && clients.contains(s.getClientId())) {
				maxThroughputClientId = s.getClientId();
				max = s.getThroughput();
			}
			if (min > s.getThroughput() && clients.contains(s.getClientId())) {
				minThroughputClientId = s.getClientId();
				min = s.getThroughput();
			}
		}

		for (Publisher p : publishers.values()) {
			if (max < p.getThroughput() && clients.contains(p.getClientId())) {
				maxThroughputClientId = p.getClientId();
				max = p.getThroughput();
			}
			if (min > p.getThroughput() && clients.contains(p.getClientId())) {
				minThroughputClientId = p.getClientId();
				min = p.getThroughput();
			}
		}

		if(!minThroughputClientId.equals("")) 
			System.out.println("min throughput client id : " + minThroughputClientId + " (" + min + " bytes)"); 

		if(!maxThroughputClientId.equals(""))
			System.out.println("max throughput client id : " + maxThroughputClientId + " (" + max + " bytes)"); 


		System.out.println("\nsubscriber list");

		for (Subscriber s : subscribers.values())
			s.printSubscription(); 

		System.out.println("-------------------------------------------------");
	}
}
