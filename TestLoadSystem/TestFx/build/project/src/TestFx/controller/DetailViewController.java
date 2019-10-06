package TestFx.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import TestFx.model.Device;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DetailViewController implements Initializable {
	@FXML
	public AnchorPane rootPane;
	@FXML
	public Label pcName;
	@FXML
	public Button resetBtn;
	@FXML
	public Button startBtn;
	@FXML
	public Button stopBtn;
	@FXML
	public ComboBox<String> typeCbx;
	@FXML
	public ComboBox<String> optionCbx;
	@FXML
	public ComboBox<String> payloadCbx;
	@FXML
	public TextField msgRateTf;
	@FXML
	public TextField topicNumTf;
	@FXML
	public TextField durationTf;
	@FXML
	public TextField minTf;
	@FXML
	public TextField maxTf;
	@FXML
	public Label pcName2;
	@FXML
	public ListView<String> availableList;
	@FXML
	public ListView<String> selectedList;
	@FXML
	private Button a_selectBtn;
	@FXML
	private Button deselectBtn;
	@FXML
	private Button s_selectBtn;
	@FXML
	private Button saveBtn;
	@FXML
	private Button leftToRightBtn;
	@FXML
	private Button rightToLeftBtn;

	private String name;

	// public static ArrayList<String> list;
	private ObservableList<String> selected;
	private ObservableList<String> available;

	private MainApplicationController mainApplicationController;

	// 비즈니스 로직과 연결 //
	public TestFx.mqtt.App app;
	public String errorMessage = "";
	public String rate = "0";
	public String topicNum = "0";
	public String factor = null;
	public String duration = "0";
	public int min = 0;
	public int max = 0;
	public static ArrayList<String> alltopics_k = new ArrayList<>(); // 전체 한글리스트
	public static ArrayList<String> alltopics_e = new ArrayList<>(); // 전체 영어리스트
	public ArrayList<String> topics_k = new ArrayList<>(); // 선택된 한글리스트
	public ArrayList<String> topics_e = new ArrayList<>(); // 선택된 영어리스트

	// Stage를 따로 class로 만들어 Static 변수로 선언하여 공용으로 사용한다.
	private Stage stage = StageStore.stage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		available = FXCollections.observableArrayList();
		selected = FXCollections.observableArrayList();

		for (int i = 0; i < alltopics_k.size(); i++) {
			available.add(alltopics_k.get(i));
		}
		availableList.setItems(available);
		selectedList.setItems(selected);

		availableList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		selectedList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		stopBtn.setDisable(true);
		topicNumTf.setDisable(true);

		typeCbx.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(@SuppressWarnings("rawtypes") ObservableValue ov, String oldValue, String newValue) {
				String type = "Publisher";
				if (typeCbx.getSelectionModel().getSelectedItem() != null) {
					if (newValue.equals(type)) {
						topicNumTf.setDisable(false);
					} else {
						topicNumTf.setText("");
						topicNumTf.setDisable(true);
					}

				}
			}
		});

	}

	@FXML
	public void startAction(ActionEvent action) {
		String pcNumber = "";
		System.out.println("호출");
		if (isTfInputValid() && isCbxInputValid() && topicListValid()) { // Validation 통과하면
			name = pcName.getText();
			pcNumber = pcName.getText().split(" ")[1]; // 이름 저장
			String type = typeCbx.getSelectionModel().getSelectedItem();

			for (int i = 0; i < MainApplicationController.deviceData.size(); i++) {
				if (name.equals(MainApplicationController.deviceData.get(i).getPcName())) {

					if (Device.existSub && type.equals("Subscriber")) { // 한개의 subscriber가 이미 작동중이고 그 후에 또 subscriber
																		// 선택시
						errorMessage = "이미 한개의 subscriber가 작동중입니다.";

						Alert alert = new Alert(AlertType.ERROR);
						Stage curStage = stage;
						alert.initOwner(curStage);
						alert.setTitle("Warning! ");
						alert.setHeaderText("블라블라");
						alert.setContentText(errorMessage);

						alert.showAndWait();
					} else {
						MainApplicationController.deviceData.get(i).isStart = true; // 나중에 바꾸기

						duration = durationTf.getText();

						if (type.equals("Publisher")) { // publisher면

							min = Integer.parseInt(minTf.getText());
							max = Integer.parseInt(maxTf.getText());

							for (int j = min; j <= max; j++) {
								Device.clientNumIndex.add(j);
							}

							rate = msgRateTf.getText();
							topicNum = topicNumTf.getText();

						} else { // subscriber면

							rate = "0";
							topicNum = "0";
							Device.existSub = true;
						}

						// factor에 담기
						factor = pcNumber + "," + type + "," + String.valueOf(min) + "," + String.valueOf(max) + ","
								+ duration + "," + rate + "," + topicNum;

						System.out.println("factors: " + factor);
						System.out.println("selected topiclist");

						/*
						 * for(int m = 0 ; m< selected.size();i++) { topics_k.add(selected.get(i)); //
						 * 선택된 한글리스트 }
						 */
						for (String topic : selected) { // 선택된 한글리스트
							topics_k.add(topic);
							System.out.println(topic);
						}

						for (int k = 0; k < topics_k.size(); k++) {

							// 전체한글리스트에서 선택된 값 인덱스 저장
							int index = alltopics_k.indexOf(topics_k.get(k));
							// 전체영어리스트에서 인덱스로 값 찾기
							String topic = alltopics_e.get(index);

							System.out.println("- " + topic);

							topics_e.add(topic); // topics_e: factor로 보낼 영어리스트
						}
					}
					app = new TestFx.mqtt.App(pcNumber, type, String.valueOf(min), String.valueOf(max), duration, rate,
							topicNum, topics_e);
					// start 버튼 누를시 다른 필드들 수정 못하게, 데이터 저장//
					saveData();
					// start , stop 버튼 색 바꾸기
					MainApplicationController.deviceData.get(i).getStartBtn().setDisable(true);
					MainApplicationController.deviceData.get(i).getStopBtn().setDisable(false);

					// edit 못하게 막기
					typeCbx.setDisable(true);
					typeCbx.setOpacity(1.0);
					optionCbx.setDisable(true);
					optionCbx.setOpacity(1.0);
					payloadCbx.setDisable(true);
					payloadCbx.setOpacity(1.0);
					topicNumTf.setDisable(true);
					topicNumTf.setOpacity(1.0);
					msgRateTf.setDisable(true);
					msgRateTf.setOpacity(1.0);
					durationTf.setDisable(true);
					durationTf.setOpacity(1.0);
					minTf.setDisable(true);
					minTf.setOpacity(1.0);
					maxTf.setDisable(true);
					maxTf.setOpacity(1.0);
					selectedList.setDisable(true);
					selectedList.setOpacity(1.0);
					availableList.setDisable(true);
					availableList.setOpacity(1.0);
					resetBtn.setDisable(true);
					resetBtn.setOpacity(1.0);
					leftToRightBtn.setDisable(true);
					leftToRightBtn.setOpacity(1.0);
					rightToLeftBtn.setDisable(true);
					rightToLeftBtn.setOpacity(1.0);

				}
				if (mainApplicationController.pcList.getItems().get(i)
						.equals(MainApplicationController.deviceData.get(i).getPcName())) {
					mainApplicationController.pcList.refresh();
				}
			}
		}
	}

	@FXML
	public void stopAction(ActionEvent action) {
		topics_e.clear(); // 선택된 영어리스트 비우기
		topics_k.clear();
		for (int i = 0; i < MainApplicationController.deviceData.size(); i++) {
			name = pcName.getText();
			String type = typeCbx.getSelectionModel().getSelectedItem();
			if (name.equals(MainApplicationController.deviceData.get(i).getPcName())) {
				int min = Integer.parseInt(minTf.getText());
				int max = Integer.parseInt(maxTf.getText());

				for (int j = min; j <= max; j++) {
					Device.clientNumIndex.remove(Integer.valueOf(j)); // 선택된 clientIndex에서 해당 index 지우기
				}

				if (type.equals("Subscriber")) { // subscriber이면
					Device.existSub = false;
					msgRateTf.setEditable(false);
					topicNumTf.setEditable(false);
					MainApplicationController.deviceData.get(i).app.subStopCommand(); // substop
				} else { // publisher이면
					MainApplicationController.deviceData.get(i).app.pubStopCommand(); // pubstop
				}

				MainApplicationController.deviceData.get(i).isStart = false; // 나중에 바꾸기

				// start , stop 버튼 색 바꾸기
				MainApplicationController.deviceData.get(i).getStartBtn().setDisable(false);
				MainApplicationController.deviceData.get(i).getStopBtn().setDisable(true);

				// edit 할 수 있게 바꾸기
				typeCbx.setDisable(false);
				typeCbx.setOpacity(1.0);
				optionCbx.setDisable(false);
				optionCbx.setOpacity(1.0);
				payloadCbx.setDisable(false);
				payloadCbx.setOpacity(1.0);
				topicNumTf.setDisable(false);
				topicNumTf.setOpacity(1.0);
				msgRateTf.setDisable(false);
				msgRateTf.setOpacity(1.0);
				durationTf.setDisable(false);
				durationTf.setOpacity(1.0);
				minTf.setDisable(false);
				minTf.setOpacity(1.0);
				maxTf.setDisable(false);
				maxTf.setOpacity(1.0);
				selectedList.setDisable(false);
				selectedList.setOpacity(1.0);
				availableList.setDisable(false);
				availableList.setOpacity(1.0);
				resetBtn.setDisable(false);
				resetBtn.setOpacity(1.0);
				leftToRightBtn.setDisable(false);
				leftToRightBtn.setOpacity(1.0);
				rightToLeftBtn.setDisable(false);
				rightToLeftBtn.setOpacity(1.0);

			}
			if (mainApplicationController.pcList.getItems().get(i)
					.equals(MainApplicationController.deviceData.get(i).getPcName())) {
				mainApplicationController.pcList.refresh();
			}
		}

	}

	@FXML
	public void resetAction(ActionEvent action) {
		msgRateTf.setText("");
		durationTf.setText("");
		minTf.setText("");
		maxTf.setText("");

		topicNumTf.setText("");
		topicNumTf.setDisable(true);

		typeCbx.getSelectionModel().clearSelection();
		optionCbx.getSelectionModel().clearSelection();
		payloadCbx.getSelectionModel().clearSelection();
	}

	@FXML
	public void leftToRightAction(ActionEvent action) {
		ObservableList<String> aList = availableList.getSelectionModel().getSelectedItems();
		ArrayList<String> items = new ArrayList<>();

		for (String item : aList) {
			items.add(item);
			// System.out.println(item);
		}

		if (aList != null) {

			for (int i = 0; i < items.size(); i++) {
				available.remove(items.get(i));

				if (selected.size() == 0) {
					selected.add(items.get(i));
				} else {
					int item_index = alltopics_k.indexOf(items.get(i));
					int insert_index = 0;

					for (int j = 0; j < selected.size(); j++) {
						insert_index = j;

						if (alltopics_k.indexOf(selected.get(j)) > item_index) {
							break;
						} else if (j == selected.size() - 1) {
							insert_index = selected.size();
						}
					}

					selected.add(insert_index, items.get(i));
				}
			}
		}
	}

	@FXML
	public void rightToLeftAction(ActionEvent action) {
		ObservableList<String> sList = selectedList.getSelectionModel().getSelectedItems();
		ArrayList<String> items = new ArrayList<>();

		for (String item : sList) {
			items.add(item);
			System.out.println(item);
		}

		if (sList != null) {
			for (int i = 0; i < items.size(); i++) {

				selected.remove(items.get(i));

				if (available.size() == 0) {
					available.add(items.get(i));
				} else {
					int item_index = alltopics_k.indexOf(items.get(i));
					int insert_index = 0;

					for (int j = 0; j < available.size(); j++) {
						insert_index = j;

						if (alltopics_k.indexOf(available.get(j)) > item_index) {
							break;
						} else if (j == available.size() - 1) {
							insert_index = available.size();
						}
					}

					available.add(insert_index, items.get(i));
				}
			}
		}
	}

	@FXML
	public void deselectedAllAction(ActionEvent action) {

		availableList.getSelectionModel().clearSelection();
	}

	@FXML
	public void selectedAllAction(ActionEvent action) {

		if (action.getTarget().equals(a_selectBtn)) {
			availableList.getSelectionModel().selectAll();
		} else {
			selectedList.getSelectionModel().selectAll();
		}
	}

	public boolean isTfInputValid() {
		String errorMessage = "";

		if (msgRateTf.getText() == null || msgRateTf.getText().length() == 0) {
			errorMessage += "No valid msg rate!\n";
		} else {
			try {
				Integer.parseInt(msgRateTf.getText());
			} catch (NumberFormatException e) {
				errorMessage += "No valid msg rate (must be an integer)!\n";
			}
		}
		if (durationTf.getText() == null || durationTf.getText().length() == 0) {
			errorMessage += "No valid duration!\n";
		} else {
			try {
				Integer.parseInt(durationTf.getText());
			} catch (NumberFormatException e) {
				errorMessage += "No valid duration (must be an integer)!\n";
			}
		}

		if (minTf.getText() == null || minTf.getText().length() == 0) {
			errorMessage += "No valid minimal create range!\n";
		} else {
			try {

				Integer.parseInt(minTf.getText());
			} catch (NumberFormatException e) {
				errorMessage += "No valid minimal create range (must be an integer)!\n";
			}
		}

		if (maxTf.getText() == null || maxTf.getText().length() == 0) {
			errorMessage += "No valid maximum create range!\n";
		} else {
			try {
				Integer.parseInt(maxTf.getText());
			} catch (NumberFormatException e) {
				errorMessage += "No valid maximum create range (must be an integer)!\n";
			}

			if (minTf.getText() != null && minTf.getText().length() != 0) {
				if (Integer.parseInt(maxTf.getText()) < Integer.parseInt(minTf.getText())) {
					errorMessage += "Invalid range (minimum number cannot be greater than maximum) !";
				}
			}
		}

		if (errorMessage.length() == 0) {
			return true;
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			// Stage curStage = (Stage) optionCbx.getScene().getWindow();
			Stage curStage = stage;
			alert.initOwner(curStage);
			alert.setTitle("Invalid Fields");
			alert.setHeaderText("Please correct invalid fields");
			alert.setContentText(errorMessage);

			alert.showAndWait();

			return false;
		}

	}

	public boolean isCbxInputValid() {
		String errorMessage = "";

		String clientType = typeCbx.getSelectionModel().getSelectedItem();
		String createOption = optionCbx.getSelectionModel().getSelectedItem();
		String payloadSize = payloadCbx.getSelectionModel().getSelectedItem();

		if (clientType == null) {
			errorMessage += "Please select client type!\n";
		} else {

			if (clientType.equals("Publisher")) {
				if (topicNumTf.getText() == null || topicNumTf.getText().length() == 0) {
					errorMessage += "No valid topic number!\n";
				} else {
					try {
						Integer.parseInt(topicNumTf.getText());
					} catch (NumberFormatException e) {
						errorMessage += "No valid topic number (must be an integer)!\n";
					}
				}
			}
		}

		if (createOption == null) {
			errorMessage += "Please select create option!\n";
		}
		if (payloadSize == null) {
			errorMessage += "Please select payload size!\n";
		}

		if (errorMessage.length() == 0) {
			return true;
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			Stage curStage = (Stage) optionCbx.getScene().getWindow();
			alert.initOwner(curStage);
			alert.setTitle("Empty options");
			alert.setHeaderText("Please select options");
			alert.setContentText(errorMessage);

			alert.showAndWait();

			return false;
		}
	}

	public boolean topicListValid() {
		String errorMessage = "";

		if (selected.size() == 0) {
			errorMessage += "Please select topics!!\n";
		}

		if (errorMessage.length() == 0) {
			return true;
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			Stage curStage = (Stage) startBtn.getScene().getWindow();
			alert.initOwner(curStage);
			alert.setTitle("Empty topics");
			alert.setHeaderText("Please select topics");
			alert.setContentText(errorMessage);

			alert.showAndWait();

			return false;

		}
	}

	// publisher의 clientNum 배열 인덱스 검사
	public boolean publisherIndexValid() {
		boolean result = true;
		int minNum = Integer.parseInt(minTf.getText());
		int maxNum = Integer.parseInt(maxTf.getText());
		String type = typeCbx.getSelectionModel().getSelectedItem();
		for (int i = minNum; i <= maxNum; i++) {
			if (type.equals("Publisher")) {
				if (Device.clientNumIndex.contains(i)) {
					Alert alert = new Alert(AlertType.ERROR);
					Stage curStage = (Stage) startBtn.getScene().getWindow();
					alert.initOwner(curStage);
					alert.setTitle("WARNING");
					alert.setHeaderText("블라블라~");
					alert.setContentText("배열 인덱스 이상");

					alert.showAndWait();
					result = false;
					break;
				}

			}
		}
		return result;
	}

	public void showDetailData(Device device) {
		if (device != null) {
			msgRateTf.setText(String.valueOf(device.getMsgRate()));
			durationTf.setText(String.valueOf(device.getDuration()));
			minTf.setText(String.valueOf(device.getMinClientNum()));
			maxTf.setText(String.valueOf(device.getMaxClientNum()));
			topicNumTf.setText(String.valueOf(device.getTopicNum()));
			typeCbx.setValue(device.getClientType());
			optionCbx.setValue(device.getCreateOption());
			payloadCbx.setValue(device.getPayloadSize());

			selected.clear();
			for (int i = 0; i < device.getTopics().size(); i++) {
				selected.add(device.getTopics().get(i));
				available.remove(device.getTopics().get(i));
			}

			if (device.isStart) {
				startBtn.setDisable(true);
				stopBtn.setDisable(false);
			} else {
				startBtn.setDisable(false);
				stopBtn.setDisable(true);
			}

			/*
			 * System.out.println("----------------------------------------------------");
			 * System.out.println("DetailViewController - showDetailData");
			 * System.out.println("pcName + " + device.getPcName());
			 * System.out.println("clienttype + " + device.getClientType());
			 * System.out.println("createoption + " + device.getCreateOption());
			 * System.out.println("duration + " + device.getDuration());
			 * System.out.println("max + " + device.getMaxClientNum());
			 * System.out.println("min + " + device.getMinClientNum());
			 * System.out.println("msg + " + device.getMsgRate());
			 * System.out.println("payload + " + device.getPayloadSize());
			 * System.out.println("topics + " + device.getTopics());
			 * System.out.println("----------------------------------------------------");
			 */

		} else {
			clearData();
		}

		if (device.isStart) {
			// edit 못하게 막기
			typeCbx.setDisable(true);
			typeCbx.setOpacity(1.0);
			optionCbx.setDisable(true);
			optionCbx.setOpacity(1.0);
			payloadCbx.setDisable(true);
			payloadCbx.setOpacity(1.0);
			topicNumTf.setDisable(true);
			topicNumTf.setOpacity(1.0);
			msgRateTf.setDisable(true);
			msgRateTf.setOpacity(1.0);
			durationTf.setDisable(true);
			durationTf.setOpacity(1.0);
			minTf.setDisable(true);
			minTf.setOpacity(1.0);
			maxTf.setDisable(true);
			maxTf.setOpacity(1.0);
			selectedList.setDisable(true);
			selectedList.setOpacity(1.0);
			availableList.setDisable(true);
			availableList.setOpacity(1.0);
			resetBtn.setDisable(true);
			resetBtn.setOpacity(1.0);
			leftToRightBtn.setDisable(true);
			leftToRightBtn.setOpacity(1.0);
			rightToLeftBtn.setDisable(true);
			rightToLeftBtn.setOpacity(1.0);

		}
	}

	public void clearData() {
		msgRateTf.setText("");
		durationTf.setText("");
		minTf.setText("");
		maxTf.setText("");

		topicNumTf.setText("");
		topicNumTf.setDisable(true);

		typeCbx.getSelectionModel().clearSelection();
		optionCbx.getSelectionModel().clearSelection();
		payloadCbx.getSelectionModel().clearSelection();

	}

	public void saveData() {

		for (int i = 0; i < MainApplicationController.deviceData.size(); i++) {
			if (name.equals(MainApplicationController.deviceData.get(i).getPcName())) {

				MainApplicationController.deviceData.get(i)
						.setClientType(typeCbx.getSelectionModel().getSelectedItem());
				MainApplicationController.deviceData.get(i)
						.setCreateOption(optionCbx.getSelectionModel().getSelectedItem());
				MainApplicationController.deviceData.get(i)
						.setPayloadSize(payloadCbx.getSelectionModel().getSelectedItem());

				MainApplicationController.deviceData.get(i).setDuration(Integer.parseInt(durationTf.getText()));
				MainApplicationController.deviceData.get(i).setMsgRate(Integer.parseInt(msgRateTf.getText()));
				MainApplicationController.deviceData.get(i).setMinClientNum(Integer.parseInt(minTf.getText()));
				MainApplicationController.deviceData.get(i).setMaxClientNum(Integer.parseInt(maxTf.getText()));

				if (MainApplicationController.deviceData.get(i).getClientType().equals("Publisher")) {
					MainApplicationController.deviceData.get(i).setTopicNum(Integer.parseInt(topicNumTf.getText()));
				}

				MainApplicationController.deviceData.get(i).setTopics(selected);

				MainApplicationController.deviceData.get(i).saved();

				// MainApplicationController.deviceData.get(i).isStart = true;

				MainApplicationController.deviceData.get(i).setStartBtn(startBtn);
				MainApplicationController.deviceData.get(i).setStopBtn(stopBtn);
				MainApplicationController.deviceData.get(i).setDurationTf(durationTf);
				MainApplicationController.deviceData.get(i).setMaxTf(maxTf);
				MainApplicationController.deviceData.get(i).setMinTf(minTf);
				MainApplicationController.deviceData.get(i).setMsgRateTf(msgRateTf);
				MainApplicationController.deviceData.get(i).setTopicNumTf(topicNumTf);
				MainApplicationController.deviceData.get(i).setOptionCbx(optionCbx);
				MainApplicationController.deviceData.get(i).setPayloadCbx(payloadCbx);
				MainApplicationController.deviceData.get(i).setTypeCbx(typeCbx);

				MainApplicationController.deviceData.get(i).app = app;
			}

			/*
			 * System.out.println("----------------------------------------------------");
			 * System.out.println("DatailViewController - deviceData(" + i + ")");
			 * System.out.println("pcName + " +
			 * MainApplicationController.deviceData.get(i).getPcName());
			 * System.out.println("clienttype + " +
			 * MainApplicationController.deviceData.get(i).getClientType());
			 * System.out.println("createoption + " +
			 * MainApplicationController.deviceData.get(i).getCreateOption());
			 * System.out.println("duration + " +
			 * MainApplicationController.deviceData.get(i).getDuration());
			 * System.out.println("max + " +
			 * MainApplicationController.deviceData.get(i).getMaxClientNum());
			 * System.out.println("min + " +
			 * MainApplicationController.deviceData.get(i).getMinClientNum());
			 * System.out.println("msg + " +
			 * MainApplicationController.deviceData.get(i).getMsgRate());
			 * System.out.println("payload + " +
			 * MainApplicationController.deviceData.get(i).getPayloadSize());
			 * System.out.println("----------------------------------------------------");
			 */
		}

	}

	public void setPcName(String name) {
		this.pcName.setText(name);
		this.pcName2.setText(name);
	}

	public String getName() {
		return name;
	}

	public void setMainApplicationController(MainApplicationController controller) {
		this.mainApplicationController = controller;
	}

}
