package TestFx.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import TestFx.mqtt.App;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;


public class Device {
	private String pcName;
	public boolean isSaved = false; 
	public boolean isStart = false; 

	public App app;
	
	private StringProperty clientType;
	private StringProperty createOption;
	private StringProperty payloadSize;
	private IntegerProperty duration;
	private IntegerProperty msgRate;
	private IntegerProperty minClientNum;
	private IntegerProperty maxClientNum;

	private final ListProperty<String> topics;
	private final IntegerProperty topicNum;

	private Button startBtn;
	private Button stopBtn;
	private ComboBox<String> typeCbx;
	private ComboBox<String> optionCbx;
	private ComboBox<String> payloadCbx;
	private TextField msgRateTf;
	private TextField topicNumTf;
	private TextField durationTf;
	private TextField minTf;
	private TextField maxTf;

	public String factor = null;

	public static boolean existSub = false;

	public static ArrayList<Integer> clientNumIndex = new ArrayList<Integer>();

	public Device() {
		this(null);
		isSaved = false;
	}

	public void saved() {
		isSaved = true;
	}

	public Device(String pcName) {

		this.pcName = pcName;
		ObservableList<String> observableList = FXCollections.observableArrayList(Arrays.asList("Hansung"));

		this.clientType = new SimpleStringProperty("Publisher");
		this.createOption = new SimpleStringProperty("Random");
		this.payloadSize = new SimpleStringProperty("Random");
		this.duration = new SimpleIntegerProperty(200);
		this.msgRate = new SimpleIntegerProperty(1);
		this.minClientNum = new SimpleIntegerProperty(1);
		this.maxClientNum = new SimpleIntegerProperty(10);
		this.topics = new SimpleListProperty<String>(observableList);
		this.topicNum = new SimpleIntegerProperty(10);
	}

	public String getPcName() {
		return pcName;
	}

	public void setPcName(String pcName) {
		this.pcName = pcName;
	}

	public String getCreateOption() {
		return createOption.get();
	}

	public void setCreateOption(String createOption) {
		this.createOption.set(createOption);
	}

	public String getPayloadSize() {
		return payloadSize.get();
	}

	public void setPayloadSize(String payloadSize) {
		this.payloadSize.set(payloadSize);
	}

	public String getClientType() {
		return clientType.get();
	}

	public void setClientType(String clientType) {
		this.clientType.set(clientType);
	}

	public int getDuration() {
		return duration.get();
	}

	public void setDuration(int duration) {
		this.duration.set(duration);
	}

	public int getMsgRate() {
		return msgRate.get();
	}

	public void setMsgRate(int msgRate) {
		this.msgRate.set(msgRate);
	}

	public int getMinClientNum() {
		return minClientNum.get();
	}

	public void setMinClientNum(int minClientNum) {
		this.minClientNum.set(minClientNum);
	}

	public int getMaxClientNum() {
		return maxClientNum.get();
	}

	public void setMaxClientNum(int maxClientNum) {
		this.maxClientNum.set(maxClientNum);
	}

	public int getTopicNum() {
		return topicNum.get();
	}

	public void setTopicNum(int topicNum) {
		this.topicNum.set(topicNum);
	}

	public ListProperty<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics.set((ObservableList<String>) topics);
	}

	public StringProperty clientTypeProperty() {
		return clientType;
	}

	public StringProperty createOptionProperty() {
		return createOption;
	}

	public StringProperty payloadSizeProperty() {
		return payloadSize;
	}

	public IntegerProperty durationProperty() {
		return duration;
	}

	public IntegerProperty msgRateProperty() {
		return msgRate;
	}

	public IntegerProperty minClientNumProperty() {
		return minClientNum;
	}

	public IntegerProperty maxClientNumProperty() {
		return maxClientNum;
	}

	public IntegerProperty topicNumProperty() {
		return topicNum;
	}

	public ListProperty<String> topicsProperty() {
		return topics;
	}

	public Button getStartBtn() {
		return startBtn;
	}

	public void setStartBtn(Button startBtn) {
		this.startBtn = startBtn;
	}

	public Button getStopBtn() {
		return stopBtn;
	}

	public void setStopBtn(Button stopBtn) {
		this.stopBtn = stopBtn;
	}

	public ComboBox<String> getTypeCbx() {
		return typeCbx;
	}

	public void setTypeCbx(ComboBox<String> typeCbx) {
		this.typeCbx = typeCbx;
	}

	public ComboBox<String> getOptionCbx() {
		return optionCbx;
	}

	public void setOptionCbx(ComboBox<String> optionCbx) {
		this.optionCbx = optionCbx;
	}

	public ComboBox<String> getPayloadCbx() {
		return payloadCbx;
	}

	public void setPayloadCbx(ComboBox<String> payloadCbx) {
		this.payloadCbx = payloadCbx;
	}

	public TextField getMsgRateTf() {
		return msgRateTf;
	}

	public void setMsgRateTf(TextField msgRateTf) {
		this.msgRateTf = msgRateTf;
	}

	public TextField getTopicNumTf() {
		return topicNumTf;
	}

	public void setTopicNumTf(TextField topicNumTf) {
		this.topicNumTf = topicNumTf;
	}

	public TextField getMinTf() {
		return minTf;
	}

	public void setMinTf(TextField minTf) {
		this.minTf = minTf;
	}

	public TextField getMaxTf() {
		return maxTf;
	}

	public void setMaxTf(TextField maxTf) {
		this.maxTf = maxTf;
	}

	public TextField getDurationTf() {
		return durationTf;
	}

	public void setDurationTf(TextField durationTf) {
		this.durationTf = durationTf;
	}

}
