package log_reader;

/* Mosquitto broker 로그 메시지를 구분하는 문자열 키워드를 가지는 클래스 */
public class MessageType {

	public static final String CONNECT = "CONNACK"; // 클라이언트가 연결 되었을 때
	public static final String DISCONNECT = "DISCONNECT"; // 클라이언트가 연결 해제 되었을 때
	public static final String SOCKET_ERROR = "disconnecting"; // 소켓 에러가 일어나 강제 연결 해제 되었을 때
	public static final String SUBSCRIBE = "SUBSCRIBE"; // 클라이언트가 토픽을 구독(subscribe)할 때
	public static final String RECEIVED_PUBLISH = "Received PUBLISH"; // publisher가 메시지를 보냈을 때
	public static final String SENDING_PUBLISH = "Sending PUBLISH"; // subscriber에게 메시지를 보냈을 때
}
