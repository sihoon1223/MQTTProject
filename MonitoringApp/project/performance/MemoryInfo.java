package performance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/* 총 메모리 크기, 사용 중인 메모리 크기 정보를 가지는 클래스 */
public class MemoryInfo {

	private int totalMemory, usedMemory; // 총 메모리 크기(KB), 사용 중인 메모리 크기(KB)를 저장하는 변수

	public MemoryInfo() {
		findTotalMemory(); // 총 메모리 크기를 구하기
	}

	/* '/proc/meminfo' 파일을 읽어 총 메모리 크기 구하기 */
	private void findTotalMemory() {
		String inputStr;
		String[] s;

		BufferedReader bufferedReader = null;
		FileReader fileReader = null;

		try {
			fileReader = new FileReader("/proc/meminfo");
			bufferedReader = new BufferedReader(fileReader);

			while ((inputStr = bufferedReader.readLine()) != null) {
				if (inputStr.contains("MemTotal")) {
					inputStr = inputStr.replaceAll("\\s|(kB)", "");
					s = inputStr.split(":");

					this.totalMemory = Integer.parseInt(s[1]);

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

	/* '/proc/meminfo' 파일을 읽어 사용 중인 메모리 크기 업데이트 */
	public void update() {
		String inputStr;
		String[] s;
		usedMemory = totalMemory;

		BufferedReader bufferedReader = null;
		FileReader fileReader = null;

		try {
			fileReader = new FileReader("/proc/meminfo");
			bufferedReader = new BufferedReader(fileReader);

			while ((inputStr = bufferedReader.readLine()) != null) {

				if (inputStr.contains("MemFree") || inputStr.contains("Buffers") || inputStr.contains("SReclaimable")) {
					inputStr = inputStr.replaceAll("\\s|(kB)", "");
					s = inputStr.split(":");

					usedMemory -= Integer.parseInt(s[1]);

				} else if (inputStr.matches("Cached.*")) {
					inputStr = inputStr.replaceAll("\\s|(kB)", "");
					s = inputStr.split(":");

					usedMemory -= Integer.parseInt(s[1]);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(bufferedReader != null) try { bufferedReader.close(); } catch (IOException e) { }
			if(fileReader != null) try { fileReader.close(); } catch (IOException e) { }
		}
	}

	/* 총 메모리 크기 반환 */
	public int getTotalMemory() {
		return totalMemory;
	}

	/* 사용 중인 메모리 크기 반환 */
	public int getUsedMemory() {
		return usedMemory;
	}
}
