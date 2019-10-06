import log_reader.LogReader;
import performance.Performance;

public class Main {
	
	/*
		-i <interval> : 지정한 시간 간격으로 DB 업데이트
		-m <minute> : 지정한 시간(분) 동안 DB 테이블 레코드 유지
		-h <hour> : 지정한 시간(시) 동안 DB 테이블 레코드 유지
		-n <number of records> : 지정한 수만큼 DB 테이블 레코드 유지
	*/
	public static void main(String[] args) {

		/* default argument */
		int interval = 3;
		int minute = 0;
		int hour = 0;
		int numberOfRecords = 100;

		/* 전달 받은 argument 처리 */
		for(int i = 0; i < args.length; i++) {
			if(args[i].charAt(0) == '-') {
				if(args[i].length() < 2) {
					System.out.println("inappropriate '"+args[i]+"'");
					return;
				}

				switch (args[i].charAt(1)) {
					case 'i': // interval 설정(second)
						if(args[i].length() < 3) {
							try {
								interval = Integer.parseInt(args[i+1]);
							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println(args[i]+" requires argument");
								return;
							} catch (NumberFormatException e) {
								System.out.println("invalid format '"+args[i+1]+"'");
								return;
							}
						} else {
							String it = args[i].replace("-i","");
							try {
								interval = Integer.parseInt(it);
							} catch (NumberFormatException e) {
								System.out.println("invalid format '"+it+"'");
								return;
							}
						}
						break;
					case 'm': // 레코드 유지 시간 설정(minute)
						if(args[i].length() < 3) {
							try {
								minute = Integer.parseInt(args[i+1]);
							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println(args[i]+" requires argument");
								return;
							} catch (NumberFormatException e) {
								System.out.println("invalid format '"+args[i+1]+"'");
								return;
							}
						} else {
							String m = args[i].replace("-m","");
							try {
								minute = Integer.parseInt(m);
							} catch (NumberFormatException e) {
								System.out.println("invalid format '"+m+"'");
								return;
							}
						}
						break;
					case 'h': // 레코드 유지 시간 설정(hour)
						if(args[i].length() < 3) {
							try {
								hour = Integer.parseInt(args[i+1]);
							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println(args[i]+" requires argument");
								return;
							} catch (NumberFormatException e) {
								System.out.println("invalid format '"+args[i+1]+"'");
								return;
							}
						} else {
							String h = args[i].replace("-h","");
							try {
								hour = Integer.parseInt(h);
							} catch (NumberFormatException e) {
								System.out.println("invalid format '"+h+"'");
								return;
							}
						}
						break;
					case 'n': // 레코드 유지 개수 설정
						if(args[i].length() < 3) {
							try {
								numberOfRecords = Integer.parseInt(args[i+1]);
							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println(args[i]+" requires argument");
								return;
							} catch (NumberFormatException e) {
								System.out.println("invalid format '"+args[i+1]+"'");
								return;
							}
						} else {
							String nr = args[i].replace("-n","");
							try {
								numberOfRecords = Integer.parseInt(nr);
							} catch (NumberFormatException e) {
								System.out.println("invalid format '"+nr+"'");
								return;
							}
						}
						break;
					case '-': // 도움말 출력
						if(args[i].contains("help"))
							printHelp();
						return;
					default:
						System.out.println("unknown option '"+args[i]+"'");
						printHelp();
						return;
				}
			}
		}

		System.out.println("Starting ...");
		System.out.println("interval : " + interval);

		if(minute != 0) {
			numberOfRecords = minute * 60 / interval;
			System.out.println("minute : " + minute);
		}

		if(hour != 0) {
			numberOfRecords = hour * 60 * 60 / interval;
			System.out.println("hour : " + hour);
		}

		System.out.println("numberOfRecords : " + numberOfRecords+"\n");

		//컴퓨터 성능 정보 수집 시작
		Performance p = new Performance(interval, numberOfRecords);
		p.start();

		//트래픽 정보 수집 시작
		LogReader logReader = new LogReader(interval, numberOfRecords);
		Thread t = new Thread(logReader);
		t.start();
	}

	public static void printHelp() {
		System.out.println("Usage: [-i] [-m] [-h] [-n]");
		System.out.println("  -i <interval> : 지정한 시간 간격으로 DB 업데이트");
		System.out.println("  -m <minute> : 지정한 시간(분) 동안 DB 테이블 레코드 유지");
		System.out.println("  -h <hour> : 지정한 시간(시) 동안 DB 테이블 레코드 유지");
		System.out.println("  -n <number of records> : 지정한 수만큼 DB 테이블 레코드 유지");
	}
}
