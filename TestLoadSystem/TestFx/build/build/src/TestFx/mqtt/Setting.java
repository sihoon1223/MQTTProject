package TestFx.mqtt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Setting {

	public ArrayList<String> topicList_k = null;
	public ArrayList<String> topicList_e = null;

	public Setting() throws ClassNotFoundException {
		readTopicList();
	}
	
	public ArrayList<String> getTopicList_k() {
		return topicList_k;
	}

	public void setTopicList_k(ArrayList<String> topicList_k) {
		this.topicList_k = topicList_k;
	}

	public ArrayList<String> getTopicList_e() {
		return topicList_e;
	}

	public void setTopicList_e(ArrayList<String> topicList_e) {
		this.topicList_e = topicList_e;
	}

	@SuppressWarnings("resource")
	public void readTopicList() throws ClassNotFoundException {

		//String filePath = Setting.class.getResource("").getPath();
		String filePath = Class.forName("TestFx.mqtt.Setting").getResource("").getPath();
		System.out.println(
				"------------------------------------------------------------------------------------------------");

		String topic = "";
		topicList_k = new ArrayList<>();
		topicList_e = new ArrayList<>();

		try {
			File file1 = new File(filePath + "topicList(korean).txt");
			File file2 = new File(filePath + "topicList(english).txt");
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file1), "UTF-8"));

			while ((topic = bufReader.readLine()) != null) {
				topicList_k.add(topic);
			}

			bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file2), "UTF-8"));

			while ((topic = bufReader.readLine()) != null) {
				topicList_e.add(topic);
			}
			bufReader.close();
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}


		/* 
		 * devices[j].topicJlist.setListData(topicList_k.toArray());
		 * devices[j].alltopics_e = topicList_e; devices[j].alltopics_k = topicList_k;
		 * devices[j].topicJlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		 * this.add(devices[j].listScroll);
		 */
	}

}
