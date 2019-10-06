package performance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/* 총 CPU 사용률, 코어 별 CPU 사용률 등의 정보를 가지는 클래스 */
public class CpuInfo {

	private double totalCpu; // 총 CPU 사용률(%)
	private double lastSum, lastIdle;
	private double lastCoreSum[], lastCoreIdle[];
	private int numberOfCores; // CPU 코어 개수
	private double[] cores; // 코어 별 CPU 사용률(%)

	public CpuInfo() {
		findNumberOfCores(); // CPU 코어 개수 구하기

		totalCpu = 0;
		cores = new double[numberOfCores];
		lastCoreSum = new double[numberOfCores];
		lastCoreIdle = new double[numberOfCores];
		lastSum = lastIdle = 0;
	}

	/* '/proc/cpuinfo' 파일을 읽어 CPU 코어 개수 구하기 */
	private void findNumberOfCores() {
		String inputStr;
		String[] s;

		BufferedReader bufferedReader = null;
		FileReader fileReader = null;

		try {
			fileReader = new FileReader("/proc/cpuinfo");
			bufferedReader = new BufferedReader(fileReader);

			while ((inputStr = bufferedReader.readLine()) != null) {
				if (inputStr.contains("cpu cores")) {
					inputStr = inputStr.replaceAll("\\s", "");
					s = inputStr.split(":");

					this.numberOfCores = Integer.parseInt(s[1]);

					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(bufferedReader != null) try { bufferedReader.close(); } catch (IOException e) { }
			if(fileReader != null) try { fileReader.close(); } catch (IOException e) { }
		}
	}

	/* '/proc/stat' 파일을 읽어 총 CPU 사용률(%)과 코어 별 CPU 사용률(%) 업데이트 */
	public void update() {
		String inputStr;
		String[] s;
		double sum = 0, idle = 0;
		double[] coreSum = new double[numberOfCores];
		double[] coreIdle = new double[numberOfCores];
		int i = -1;

		BufferedReader bufferedReader = null;
		FileReader fileReader = null;

		try {
			fileReader = new FileReader("/proc/stat");
			bufferedReader = new BufferedReader(fileReader);

			while (i < numberOfCores) {
				inputStr = bufferedReader.readLine();
				s = inputStr.split("\\s+");

				if (i == -1) { // 파일의 첫 번째 줄인지 확인. 첫 번째 줄은 총 CPU에 대한 정보를 가짐

					sum = Double.parseDouble(s[1]) + Double.parseDouble(s[2]) + Double.parseDouble(s[3]) + Double.parseDouble(s[4]) + Double.parseDouble(s[5]) + Double.parseDouble(s[6]) + Double.parseDouble(s[7]) + Double.parseDouble(s[8]);
					idle = Double.parseDouble(s[4]) + Double.parseDouble(s[5]);

					totalCpu = 100 * (1.0 - (idle - lastIdle) * 1.0 / (sum - lastSum));
					lastSum = sum;
					lastIdle = idle;

				} else { // 두 번째 줄 부터는 코어 별 CPU에 대한 정보를 가짐

					coreSum[i] = Double.parseDouble(s[1]) + Double.parseDouble(s[2]) + Double.parseDouble(s[3]) + Double.parseDouble(s[4]) + Double.parseDouble(s[5]) + Double.parseDouble(s[6]) + Double.parseDouble(s[7]) + Double.parseDouble(s[8]);
					coreIdle[i] = Double.parseDouble(s[4]) + Double.parseDouble(s[5]);

					cores[i] = 100 * (1.0 - (coreIdle[i] - lastCoreIdle[i]) * 1.0 / (coreSum[i] - lastCoreSum[i]));
					lastCoreSum[i] = coreSum[i];
					lastCoreIdle[i] = coreIdle[i];
				}

				i++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(bufferedReader != null) try { bufferedReader.close(); } catch (IOException e) { }
			if(fileReader != null) try { fileReader.close(); } catch (IOException e) { }
		}
	}

	/* CPU 코어 개수 반환 */
	public int getNumberOfCores() {
		return numberOfCores;
	}

	/* 총 CPU 사용률 반환 */
	public double getTotalCpu() {
		return Math.round(totalCpu*100)/100.0;
	}

	/* 코어 별 CPU 사용률 반환 */
	public String getCores() {
		String s = "";
		int i;

		for (i = 0; i < numberOfCores - 1; i++)
			s += Math.round(cores[i]*100)/100.0 + "/";
		s += Math.round(cores[i]*100)/100.0;

		return s;
	}
}
