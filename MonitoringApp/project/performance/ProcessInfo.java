package performance;

/* 하나의 프로세스에 대한 정보를 가지는 ProcessInfo 클래스 */
public class ProcessInfo {
	
	private String processId; // 프로세스 id를 저장하는 변수
	private double pcpu; // 프로세스의 CPU 사용률을 저장하는 변수
	private double pmem; // 프로세스가 사용 중인 메모리를 저장하는 변수
	private int utimeAndStime=-1; // 사용자 모드와 커널 모드에서 프로세스가 스케줄 된 시간을 저장하는 변수
	private String command; // 프로세스 command 이름을 저장하는 변수
	
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public double getPcpu() {
		return pcpu;
	}
	public void setPcpu(double pcpu) {
		this.pcpu = pcpu;
	}
	public double getPmem() {
		return pmem;
	}
	public void setPmem(double pmem) {
		this.pmem = pmem;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public int getUtimeAndStime() {
		return utimeAndStime;
	}
	public void setUtimeAndStime(int utimeAndStime) {
		this.utimeAndStime=utimeAndStime;
	}
}
