package performance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/* Network I/O 정보를 가지는 클래스 */
public class NetworkInfo {

	private int interval; // interval(초) 시간 간격으로 DB 업데이트

	private double rx; // 초 당 받은 바이트 크기(Network Input, KB/sec)를 저장하는 변수
	private double tx; // 초 당 보낸 바이트 크기(Network Output, KB/sec)를 저장하는 변수
	private long lastRx, lastTx; // 각각 받은 바이트 크기, 보낸 바이트 크기를 저장하는 변수
	private String interfaceName; // 네트워크 인터페이스 이름을 저장하는 변수

	public NetworkInfo() {
		findInterfaceName(); // 네트워크 인터페이스 이름 찾기
		init(); // lastRx, lastTx를 초기화
	}

	/* interval 설정 */
	public void setInterval(int interval) {
		this.interval = interval;
	}

	/* ifconfig 명령을 실행하여 네트워크 인터페이스 이름 찾기 */
	public void findInterfaceName() {
		String inputStr;
		String[] s;
		java.lang.Process p;

		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;

		try {
			p = Runtime.getRuntime().exec("ifconfig");

			inputStreamReader = new InputStreamReader(p.getInputStream());
			bufferedReader = new BufferedReader(inputStreamReader);

			inputStr = bufferedReader.readLine();
			s = inputStr.split("\\s+");

			interfaceName = s[0].replace(":", "");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(bufferedReader != null) try { bufferedReader.close(); } catch (IOException e) { }
			if(inputStreamReader != null) try { inputStreamReader.close(); } catch (IOException e) { }
		}
	}

	/* '/proc/net/dev' 파일을 읽어 lastRx, lastTx를 초기화 */
	public void init() {
		String inputStr = "";
		String[] s;

		BufferedReader bufferedReader = null;
		FileReader fileReader = null;

		try {
			fileReader = new FileReader("/proc/net/dev");
			bufferedReader = new BufferedReader(fileReader);

			while ((inputStr = bufferedReader.readLine()) != null)
				if (inputStr.contains(interfaceName))
					break;

			s = inputStr.trim().split("\\s+");

			lastRx = Long.parseLong(s[1]);
			lastTx = Long.parseLong(s[9]);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(bufferedReader != null) try { bufferedReader.close(); } catch (IOException e) { }
			if(fileReader != null) try { fileReader.close(); } catch (IOException e) { }
		}
	}

	/* '/proc/net/dev' 파일을 읽어 rx, tx 계산 */
	public void update() {
		String inputStr = "";
		String[] s;
		long r, t;

		BufferedReader bufferedReader = null;
		FileReader fileReader = null;

		try {
			fileReader = new FileReader("/proc/net/dev");
			bufferedReader = new BufferedReader(fileReader);

			while ((inputStr = bufferedReader.readLine()) != null)
				if (inputStr.contains(interfaceName))
					break;

			s = inputStr.trim().split("\\s+");

			r = Long.parseLong(s[1]);
			t = Long.parseLong(s[9]);

			rx = (r - lastRx) * 1.0 / 1024 / interval;
			tx = (t - lastTx) * 1.0 / 1024 / interval;
			
			lastRx = r;
			lastTx = t;

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(bufferedReader != null) try { bufferedReader.close(); } catch (IOException e) { }
			if(fileReader != null) try { fileReader.close(); } catch (IOException e) { }
		}
	}

	/* 초 당 받은 바이트 크기(Network Input)를 반환 */
	public double getRx() {
		return Math.round(rx*100)/100.0;
	}

	/* 초 당 보낸 바이트 크기(Network Output)를 반환 */
	public double getTx() {
		return Math.round(tx*100)/100.0;
	}
}
