package performance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Properties;
import java.sql.*;

/* 컴퓨터 성능 데이터를 수집하여 DB에 저장하는 클래스 */
public class Performance {

	private int interval; // interval(초) 시간 간격으로 DB 업데이트
	private int numberOfRecords; // 지정한 개수 만큼 DB 테이블 레코드 유지

	// 프로세스 id가 [key], 하나의 프로세스에 대한 정보를 가지는 ProcessInfo 클래스가 [value]인 Map
	private ConcurrentHashMap<String, ProcessInfo> processes = new ConcurrentHashMap<>();

	// 프로세스 id가 [key], 프로세스가 실행 중인지 아닌지 판별하는 boolean 변수가 [value]인 Map
	private HashMap<String, Boolean> isAlive;

	private CpuInfo cpuInfo; // 총 CPU 사용률(%), core 별 cpu 사용률(%) 정보를 가지는 클래스
	private MemoryInfo memInfo; // 총 메모리 크기(KB), 사용 중인 메모리 크기(KB) 정보를 가지는 클래스
	private NetworkInfo netInfo; // Network I/O(KB/sec) 정보를 가지는 클래스

	private double lastTotal=0; // '/proc/stat'의 이전 CPU 수치의 합

	private String[] tableList = { "basic_info", "performance", "process" };

	private String driver;
	private String url;
	private String user;
	private String pw;

	public Performance(int interval, int numberOfRecords) {

		this.interval = interval;
		this.numberOfRecords = numberOfRecords;

		processes = new ConcurrentHashMap<>();

		isAlive = new HashMap<>();

		cpuInfo = new CpuInfo();
		memInfo = new MemoryInfo();
		netInfo = new NetworkInfo();
		netInfo.setInterval(interval);

		// read db.properties
		readProperties();

		// basic_info 테이블 업데이트
		setBasicInfoTable();

		// 모든 DB 테이블 비우기
		Connection conn=null;
		PreparedStatement pstmt=null;

		try {
			Class.forName(driver);
			conn=DriverManager.getConnection(url, user, pw);

			pstmt=conn.prepareStatement("DELETE FROM performance");
			pstmt.executeUpdate();

			pstmt=conn.prepareStatement("DELETE FROM process");
			pstmt.executeUpdate();

		} catch (ClassNotFoundException e) {
			System.out.println("driver error");
			e.printStackTrace();
		} catch (SQLException e1) {
			System.out.println("p:1:sql query error");
			e1.printStackTrace();
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
				
				if(tableList[i].equals("performance"))
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
			System.out.println("p:2:sql query error");
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

	/* Timer, TimerTask를 이용하여 주기적으로 DB 업데이트 */
	public void start() {
		System.out.println("Performance started at " + getCurrentTime());

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {

				//System.out.println("Performance " + getCurrentTime());
				print();

				// 컴퓨터 성능 데이터 업데이트
				update();

				// 컴퓨터 성능 데이터 관련 DB 테이블 업데이트
				updatePerformanceTable();
				updateProcessTable();
			}
		};
		timer.schedule(task, 0, interval * 1000);
	}

	/* basic_info 테이블 업데이트 */
	public void setBasicInfoTable() {

		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		try {
			Class.forName(driver);
			conn=DriverManager.getConnection(url, user, pw);

			// basic_info 테이블의 레코드 개수를 알아내기 위한 쿼리 실행
			pstmt=conn.prepareStatement("SELECT COUNT(*) AS count FROM basic_info");

			rs=pstmt.executeQuery();
			rs.next();

			// 이미 레코드가 존재하면 UPDATE, 존재하지 않으면 INSERT 쿼리 실행 
			if(rs.getInt("count") > 0) {
				pstmt=conn.prepareStatement("UPDATE basic_info SET total_memory=?, number_of_cores=?");
				pstmt.setInt(1, memInfo.getTotalMemory());
				pstmt.setInt(2, cpuInfo.getNumberOfCores());

				int r=pstmt.executeUpdate();
			} else {
				pstmt=conn.prepareStatement("INSERT INTO basic_info(total_memory, number_of_cores) VALUES(?, ?)");
				pstmt.setInt(1, memInfo.getTotalMemory());
				pstmt.setInt(2, cpuInfo.getNumberOfCores());

				int r=pstmt.executeUpdate();
			}

		} catch (SQLException e) {
			System.out.println("p:3:sql query error");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("driver error");
		} finally {
			if(rs!=null) try { rs.close(); } catch (SQLException e) { }
			if(pstmt!=null) try { pstmt.close(); } catch (SQLException e) { }
			if(conn!=null) try { conn.close(); } catch (SQLException e) { }
		}
	}

	/* performance 테이블 업데이트 */
	public void updatePerformanceTable() {

		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		try {
			Class.forName(driver);
			conn=DriverManager.getConnection(url, user, pw);

			// performance 테이블의 레코드 개수를 알아내기 위한 쿼리 실행
			pstmt=conn.prepareStatement("SELECT COUNT(*) AS count FROM performance");

			rs=pstmt.executeQuery();
			rs.next();

			// 현재 performance 테이블의 레코드 개수가 numberOfRecords 값보다 크면 가장 오래된 레코드 삭제
			if(rs.getInt("count") >= numberOfRecords) {

				pstmt=conn.prepareStatement("DELETE FROM performance ORDER BY date ASC LIMIT 1");

				int r=pstmt.executeUpdate();
			}

			// performance 테이블에 성능 데이터와 현재 시간을 삽입
			pstmt=conn.prepareStatement("INSERT INTO performance(cpu_util, cores_util, memory_usage, network_in, network_out, date) VALUES(?, ?, ?, ?, ?, ?)");
			pstmt.setDouble(1, cpuInfo.getTotalCpu());
			pstmt.setString(2, cpuInfo.getCores());
			pstmt.setInt(3, memInfo.getUsedMemory());
			pstmt.setDouble(4, netInfo.getRx());
			pstmt.setDouble(5, netInfo.getTx());
			pstmt.setString(6, getCurrentTime());

			int r=pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("p:4:sql query error");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("driver error");
		} finally {
			if(rs!=null) try { rs.close(); } catch (SQLException e) { }
			if(pstmt!=null) try { pstmt.close(); } catch (SQLException e) { }
			if(conn!=null) try { conn.close(); } catch (SQLException e) { }
		}
	}

	/* process 테이블 업데이트 */
	public void updateProcessTable() {

		String sql;

		if(!isAlive.isEmpty()) {

			Connection conn=null;
			PreparedStatement pstmt=null;

			try {				
				Class.forName(driver);
				conn=DriverManager.getConnection(url, user, pw);

				sql="DELETE FROM process WHERE pid IN (";
				boolean first=true;
				boolean isUsed=false; // 실행 종료 된 프로세스가 하나라도 있으면 true로 변환

				Iterator<Map.Entry<String, Boolean>> iter = isAlive.entrySet().iterator();
				while(iter.hasNext()) {
					Map.Entry<String, Boolean> entry = iter.next();
					
					if(!entry.getValue()) { // false value ---> dead process
						if(!first)
							sql+=", ";

						sql+="\'"+entry.getKey()+"\'"; // DELETE 쿼리 문에 종료 된 프로세스 id 추가

						processes.remove(entry.getKey()); // 종료 된 프로세스 id(key)를 processes에서 삭제

						iter.remove(); // 종료 된 프로세스 id(key)를 isAlive에서 삭제

						first=false;
						isUsed=true;
					}
				}

				sql+=")";

				// 종료 된 프로세스가 있으면 process 테이블에서 삭제
				if(isUsed) {
					pstmt=conn.prepareStatement(sql);
					int r=pstmt.executeUpdate();
				}

			} catch (SQLException e) {
				System.out.println("sql process delete query error");
			} catch (ClassNotFoundException e) {
				System.out.println("driver error");
			} finally {
				if(pstmt!=null) try { pstmt.close(); } catch (SQLException e) { }
				if(conn!=null) try { conn.close(); } catch (SQLException e) { }
			}
		}

		ProcessInfo proInfo;

		if(processes.size()==0)
			return;

		sql="INSERT INTO process(pid, cpu_util, memory_util, command) VALUES";

		Connection conn=null;
		PreparedStatement pstmt=null;

		try {
			Class.forName(driver);
			conn=DriverManager.getConnection(url, user, pw);

			boolean first=true;
			boolean isEmptyMap=isAlive.isEmpty();
		
			// INSERT 쿼리 문에 실행 중인 프로세스 id 추가
			for(Map.Entry<String, ProcessInfo> entry : processes.entrySet()) {

				proInfo=entry.getValue();

				// isAlive의 value를 false로 변환
				isAlive.put(proInfo.getProcessId(), false);

				// dead process ---> INSERT X
				if(!isEmptyMap && !isAlive.containsKey(proInfo.getProcessId()))
					continue;

				if(!first)
					sql+=", ";

				sql+="(\'"+proInfo.getProcessId()+"\', "+proInfo.getPcpu()+", "+proInfo.getPmem()+", \'"+proInfo.getCommand()+"\')";
				
				first=false;
			}

			sql+=" ON DUPLICATE KEY UPDATE cpu_util=VALUES(cpu_util), memory_util=VALUES(memory_util)"; // 이미 레코드에 존재하는 프로세스 id이면 UPDATE

			// process 테이블에 프로세스에 대한 정보를 삽입
			pstmt=conn.prepareStatement(sql);
			int r=pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("p:5:sql query error");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("driver error");
		} finally {
			if(pstmt!=null) try { pstmt.close(); } catch (SQLException e) { }
			if(conn!=null) try { conn.close(); } catch (SQLException e) { }
		}
	}

	/* 모든 컴퓨터 성능 데이터 업데이트 */
	public void update() {

		cpuInfo.update();
		memInfo.update();
		netInfo.update();
		
		executePs();
	}

	/* ps 명령어를 실행하여 프로세스 id 목록을 알아내고 각 프로세스에 대한 성능 정보를 수집 */
	public void executePs() {
		String inputStr;
		String[] s;
		double total;
		java.lang.Process p;

		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null; 

		try {
			// '/proc/stat'의 현재 CPU 수치의 합을 계산
			total = calculateTotal();

			// ps 명령 실행
			p = Runtime.getRuntime().exec("ps -eo pid,pmem,comm");

			inputStreamReader = new InputStreamReader(p.getInputStream()); 
			bufferedReader = new BufferedReader(inputStreamReader);

			inputStr = bufferedReader.readLine();

			while ((inputStr = bufferedReader.readLine()) != null) { // ps 명령을 실행하여 출력되는 결과를 한 줄씩 읽기
				s = inputStr.trim().split("\\s+");

				ProcessInfo proInfo;
				int currentUtimeAndStime;

	 			if(processes.containsKey(s[0])) { // processes에 이미 저장되어 있는 프로세스 id인지 확인
					proInfo=processes.get(s[0]);

					// '/proc/[프로세스 id]/stat' 파일을 읽어 현재 utime + stime 를 구하기
					currentUtimeAndStime = calculateUtimeAndStime(s[0]);

					// 프로세스의 cpu 사용률을 계산하여 proInfo에 저장
					proInfo.setPcpu(cpuInfo.getNumberOfCores() * 100 * (currentUtimeAndStime - proInfo.getUtimeAndStime()) * 1.0 / (total - lastTotal));
					proInfo.setUtimeAndStime(currentUtimeAndStime);
				}
				else {
					proInfo = new ProcessInfo();

					// '/proc/[프로세스 id]/stat' 파일을 읽어 현재 utime + stime 를 구하기
					currentUtimeAndStime = calculateUtimeAndStime(s[0]);

					proInfo.setProcessId(s[0]);
					proInfo.setCommand(s[2]);
					proInfo.setPcpu(0.0);
					proInfo.setUtimeAndStime(currentUtimeAndStime);
				}

				proInfo.setPmem(Double.parseDouble(s[1]));

				if(!proInfo.getCommand().equals("ps")) {
					processes.put(s[0], proInfo); // 프로세스 id를 key로, proInfo 클래스를 value로 하여 processes에 저장
					if(isAlive.containsKey(s[0]))
						isAlive.replace(s[0], true); // 현재 실행 중인 프로세스이므로 isAlive의 value를 true로 변환(true ---> 살아있는 상태)
				}
			}

			lastTotal=total;

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(bufferedReader != null) try { bufferedReader.close(); } catch (IOException e) { }
			if(inputStreamReader != null) try { inputStreamReader.close(); } catch (IOException e) { }
		}
	}

	/* '/proc/stat' 파일을 읽어 현재 CPU 수치의 합을 계산 */
	public double calculateTotal() {

		String inputStr;
		String[] s;
		double total=0;

		BufferedReader bufferedReader = null;
		FileReader fileReader = null;

		try {
			fileReader = new FileReader("/proc/stat");
			bufferedReader = new BufferedReader(fileReader);

			inputStr=bufferedReader.readLine();
			s=inputStr.trim().split("\\s+");

			total = Double.parseDouble(s[1]) + Double.parseDouble(s[2]) + Double.parseDouble(s[3]) + Double.parseDouble(s[4]) + Double.parseDouble(s[5]) + Double.parseDouble(s[6]) + Double.parseDouble(s[7]) + Double.parseDouble(s[8]);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(bufferedReader != null) try { bufferedReader.close(); } catch (IOException e) { }
			if(fileReader != null) try { fileReader.close(); } catch (IOException e) { }
		}

		return total;
	}
	
	/* '/proc/[프로세스 id]/stat' 파일을 읽어 현재 utime + stime 를 계산 */
	public int calculateUtimeAndStime(String processId) {

		String inputStr;
		String[] s;
		int utime=0, stime=0;

		BufferedReader bufferedReader = null;
		FileReader fileReader = null;

		try {
			fileReader = new FileReader("/proc/"+processId+"/stat");
			bufferedReader = new BufferedReader(fileReader);

			inputStr=bufferedReader.readLine();
			s=inputStr.trim().split("\\s+");

			utime = Integer.parseInt(s[13]); // 사용자 모드에서 프로세스가 스케줄 된 시간. clock ticks 단위
			stime = Integer.parseInt(s[14]); // 커널 모드에서 프로세스가 스케줄 된 시간. clock ticks 단위

		} catch (IOException e) {
			//ps는 실행되고 바로 죽음
		} finally {
			if(bufferedReader != null) try { bufferedReader.close(); } catch (IOException e) { }
			if(fileReader != null) try { fileReader.close(); } catch (IOException e) { }
		}

		return utime+stime;
	}

	/* 현재 시간 구하기 */
	public String getCurrentTime() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		return sdf.format(d);
	}

	/* 성능 데이터 간략하게 출력 */
	public void print() {
		System.out.println("------------------Performance--------------------");
		System.out.println("current time : " + getCurrentTime());
		System.out.println("total cpu percentage : " + cpuInfo.getTotalCpu() + "%");
		System.out.println("total memory : " + memInfo.getTotalMemory() + "KB " + "used memory : " + memInfo.getUsedMemory() + "KB");
		System.out.println("network in : " + netInfo.getRx() + "kb/s " + "network out : " + netInfo.getTx() + "kb/s");
		System.out.println("-------------------------------------------------");
	}

}
