package TestFx.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import TestFx.model.Device;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;

public class MainApplicationController implements Initializable {

	@FXML
	private StackPane rootPane;
	@FXML
	private AnchorPane rootLayout;
	@FXML
	public ListView<String> pcList;
	@FXML
	private Button addBtn;
	@FXML
	private Button removeBtn;
	@FXML
	private Button exitBtn;
	@FXML
	private Button homeBtn;
	@FXML
	private Button infoBtn;
	@FXML
	private AnchorPane mainPane;

	public static String masterIP = null;
	public static String targetIP = null;

	@SuppressWarnings("unused")
	private MainApp mainApp;
	private ObservableList<String> listItems;
	private ArrayList<String> test = new ArrayList<>(
			Arrays.asList("PC 0", "PC 1", "PC 2", "PC 3", "PC 4", "PC 5", "PC 6", "PC 7", "PC 8", "PC 9"));

	public static ObservableList<Device> deviceData = FXCollections.observableArrayList();

	private DetailViewController detailViewController;
	private MainApplicationController mainApplicationController = this;

	// Stage를 따로 class로 만들어 Static 변수로 선언하여 공용으로 사용한다.
	private Stage stage = StageStore.stage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// 메인창 지정
		rootLayout = (AnchorPane) stage.getScene().getRoot();

		// 리스트 배치

		listItems = FXCollections.observableArrayList();
		deviceData.clear();
		deviceData.add(new Device("PC 0"));
		deviceData.add(new Device("PC 1"));
		deviceData.add(new Device("PC 2"));

		for (int i = 0; i < deviceData.size(); i++) {
			listItems.add(deviceData.get(i).getPcName());
		}

		pcList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> param) {
				XCell cell = new XCell();
				cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new MyEventHandler());
				return cell;
			}
		});

		// 처음 시작할때 mainPane은 homeView를 띄움
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("../view/homeView.fxml"));
		AnchorPane homeView;
		try {
			homeView = (AnchorPane) loader.load();
			mainPane.getChildren().add(homeView);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 스타일 지정
		pcList.setStyle("-fx-padding: 0px ; -fx-background-insets:0 0 0 0 0,0,0");
		pcList.setItems(listItems);
		pcList.getStylesheets().add("TestFx/view/myStyle.css");

		// 페이드인 애니메이션
		rootPane.setOpacity(0);
		makeFadeIn();
	}

	public ObservableList<Device> getDeviceData() {
		return deviceData;
	}

	// 페이드인 애니메이션
	public void makeFadeIn() {
		FadeTransition fadeTransition = new FadeTransition();
		fadeTransition.setDuration(Duration.millis(3000));
		fadeTransition.setNode(rootPane);
		fadeTransition.setFromValue(0);
		fadeTransition.setToValue(1);
		fadeTransition.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						try {
							boolean okClicked = showInputDialog();
							if (okClicked) {

								URL fxmlPath = new File("src/TestFx/view/homeView.fxml").toURL();
								FXMLLoader loader = new FXMLLoader();
								AnchorPane homeView = loader.load(fxmlPath);
								mainPane.getChildren().clear();
								mainPane.getChildren().add(homeView);

								/*
								 * homeController.setIP(MainApplicationController.masterIP,
								 * MainApplicationController.targetIP); AnchorPane pane =
								 * FXMLLoader.load(getClass().getClassLoader().getResource(
								 * "../view/homeView.fxml")); mainPane.getChildren().clear();
								 * mainPane.getChildren().add(pane);
								 */
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		});
		fadeTransition.play();
	}

	public boolean showInputDialog() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("../view/inputDialogView.fxml"));
		AnchorPane dialog = (AnchorPane) loader.load();

		Stage dialogStage = new Stage();
		dialogStage.initStyle(StageStyle.TRANSPARENT);
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(stage);

		InputDialogController controller = loader.getController();
		controller.setDialogStage(dialogStage);

		Scene scene = new Scene(dialog);
		dialogStage.setScene(scene);
		dialogStage.showAndWait();

		return controller.isOkClicked();

	}

	// 창 드래그
	public void registerStage(Stage stage) {
		EffectUtilities.makeDraggable(stage, rootPane);
	}

	// 메인 앱 참조
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		mainApp.setRootLayout(rootLayout);
	}

	// pc리스트에서 pc 추가할 때 액션
	@FXML
	public void addAction(ActionEvent action) {

		for (int i = 0; i < test.size(); i++) {
			if (!pcList.getItems().contains(test.get(i))) {
				pcList.getItems().add(i, test.get(i));
				deviceData.add(new Device(test.get(i)));
				break;
			}
		}
	}

	// pc리스트에서 pc 삭제할 때 액션
	@FXML
	public void deleteAction(ActionEvent action) {

		int selectedIndex = pcList.getSelectionModel().getSelectedIndex();
		Stage curStage = (Stage) pcList.getScene().getWindow(); // 현재 윈도우 가져오기
		if (pcList.getItems().size() > 1) { // 리스트에 1개 보다 많이 아이템 있을경우
			if (selectedIndex >= 0) { // 선택한 아이템이 있으면
				// mainPane.getChildren().clear();
				listItems.remove(selectedIndex);
				deviceData.remove(selectedIndex);

				// 삭제 전 list의 -1 번째 item으로 list 선택하는 로직
				changeListIndex();

			} else { // 아무것도 선택하지 않았다면
				Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(curStage);
				alert.setTitle("알림");
				alert.setHeaderText("삭제할 PC를 선택하세요");
				alert.setContentText("Please select a device in the list.");

				alert.showAndWait();

			}
		} else if (pcList.getItems().size() == 1) { // 리스트에 아이템이 1개만 있으면
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(curStage);
			alert.setTitle("알림");
			alert.setHeaderText("리스트에 최소 1개의 pc가 있어야합니다");
			alert.setContentText("블라블라.");

			alert.showAndWait();

		}
	}

	// pcList index를 받아와 pcName에 맞게 detailview 띄우기
	public void changeListIndex() {
		String name = pcList.getSelectionModel().getSelectedItem();

		// 선택된 pc의 view가 나오게끔하기 위해 우선 detailView를 로드
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("../view/detailView.fxml"));
		AnchorPane detailView;

		try {
			detailView = (AnchorPane) loader.load();
			mainPane.getChildren().clear();
			mainPane.getChildren().add(detailView);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 컨트롤러 가져오기
		DetailViewController controller = loader.getController();
		controller.setMainApplicationController(mainApplicationController);
		detailViewController = controller;
		// datailView의 선택된 pc 이름을 label에 띄우게끔 설정
		controller.setPcName(name);

		for (int i = 0; i < deviceData.size(); i++) {
			// 이름이 같고
			if (name.equals(deviceData.get(i).getPcName())) {
				// 리스트에 이미 있는 pc일 경우
				if (deviceData.contains(deviceData.get(i))) {
					// save 된 상태면
					if (deviceData.get(i).isSaved) {
						// 초기화해주고 저장된 데이터 바인딩
						controller.clearData();
						controller.showDetailData(deviceData.get(i));
					}
					// 리스트에 없던 pc면 (즉 추가된거면)
				} else {
					controller.clearData();
				}
			}

		}
	}

	// top bar에서 info 버튼 누를 때 액션

	@FXML
	public void infoAction(ActionEvent action) {

	}

	@FXML
	public void settingAction(ActionEvent action) {

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("../view/settingView.fxml"));
		AnchorPane settingView;

		try {
			settingView = (AnchorPane) loader.load();

			mainPane.getChildren().clear();
			mainPane.getChildren().add(settingView);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// top bar에서 home 버튼 누를 때 액션

	@FXML
	public void homeAction(ActionEvent action) {

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("../view/homeView.fxml"));
		AnchorPane homeView;

		try {
			homeView = (AnchorPane) loader.load();
			mainPane.getChildren().clear();
			mainPane.getChildren().add(homeView);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// top bar에서 close 버튼 누를 때 액션
	@FXML
	public void closeAction(ActionEvent action) {
		System.exit(0);
	}

	// pc리스트의 pc를 클릭했을 때 관련 이벤트 클래스
	public class MyEventHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent t) {
			changeListIndex();

			// 한번 초기화하고 저장된 정보 띄우기

			/*
			 * System.out.println("----------------------------------------------------");
			 * System.out.println("MainApplication - deviceData(" + i + ")");
			 * System.out.println("pcName + " + deviceData.get(i).getPcName());
			 * System.out.println("clienttype + " + deviceData.get(i).getClientType());
			 * System.out.println("createoption + " + deviceData.get(i).getCreateOption());
			 * System.out.println("duration + " + deviceData.get(i).getDuration());
			 * System.out.println("max + " + deviceData.get(i).getMaxClientNum());
			 * System.out.println("min + " + deviceData.get(i).getMinClientNum());
			 * System.out.println("msg + " + deviceData.get(i).getMsgRate());
			 * System.out.println("payload + " + deviceData.get(i).getPayloadSize());
			 * System.out.println("topics + " + deviceData.get(i).getTopics());
			 * System.out.println("----------------------------------------------------");
			 */

		
		}

	}

	public void setDetailViewController(DetailViewController controller) {
		this.detailViewController = controller;
	}

	public Tooltip setToolTip(Device device) {
		Tooltip tooltip = new Tooltip("");
		String topics = "";
		
		for(int i=0;i<device.getTopics().size();i++) {
			topics= topics+"\n\t"+device.getTopics().get(i);
		}
		
		if (device.getClientType().equals("Publisher")) {
			
			
			tooltip.setText(
					"Status: Connecting\n\n-------------------------------------------\n\t\t\t\tSetting\n-------------------------------------------\n\tDevice name: \t" + device.getPcName()
					+ "\n\tClient type: \t" + device.getClientType()
					+ "\n\tMsg Rage: \t"+ device.getMsgRate()
					+ "\n\tTopic num: \t" + device.getTopicNum()
					+ "\n\tCreate Option: \t" + device.getCreateOption()
					+ "\n\tPayload size: \t" + device.getPayloadSize()
					+ "\n\tHolding time: \t" + device.getDuration()
					+ "\n\tCreate Range: \t" + device.getMinClientNum() + " ~ " + device.getMaxClientNum()
					+ "\n-------------------------------------------\n\t\t\t\tTopics\n-------------------------------------------"
					+ topics
					);
			tooltip.setGraphic(new ImageView("file:iconmonstr-basketball-1-16.png"));
		} else {
			tooltip.setText(
					"Status: Connecting\n\n-------------------------------------------\n\t\t\t\tSetting\n-------------------------------------------\n\tDevice name: \t" + device.getPcName()
					+ "\n" + "Client Type: " + device.getClientType()
					+ "\nCreate Option: " + device.getCreateOption()
					+ "\nPayload size: " + device.getPayloadSize()
					+ "\nHolding time: " + device.getDuration()
					+ "\nCreate Range: "+ device.getMinClientNum() + " ~ " + device.getMaxClientNum()
					+"\n-------------------------------------------\n\t\t\t\tTopics\n-------------------------------------------"
					+ topics
					);

		}
		return tooltip;
	}

	// pc리스트 안에 들어갈 ListCell 클래스
	public class XCell extends ListCell<String> {
		String lastItem;

		HBox hbox = new HBox();

		AnchorPane pane1 = new AnchorPane();
		Label pcName = new Label();

		AnchorPane pane2 = new AnchorPane();
		public Button startBtn = new Button();

		AnchorPane pane3 = new AnchorPane();
		Button stopBtn = new Button();

		private Tooltip tooltip;

		public XCell() {
			super();

			File imageFile = new File("resources/pcList_start_button_white.png");
			Image startBtnImg = new Image(imageFile.toURI().toString());
			ImageView startBtnImgView = new ImageView(startBtnImg);
			startBtnImgView.setFitWidth(23);
			startBtnImgView.setFitHeight(18);

			File imageFile2 = new File("resources/pcList_stop_button_gray.png");
			Image stopBtnImg = new Image(imageFile2.toURI().toString());
			ImageView stopBtnImgView = new ImageView(stopBtnImg);
			stopBtnImgView.setFitWidth(23);
			stopBtnImgView.setFitHeight(18);

			hbox.setPrefSize(200, 34);

			pane1.setPrefWidth(114);
			pane1.setPrefHeight(42);
			pcName.setLayoutX(50);
			pcName.setLayoutY(5);
			pcName.setPrefWidth(65.0);
			pcName.setPrefHeight(26.0);
			pcName.getStylesheets().add("TestFx/view/myStyle.css");
			pcName.getStyleClass().clear();
			pcName.getStyleClass().add("myriad-20-semibold");
			pane1.getChildren().add(pcName);

			pane2.setPrefWidth(30);
			pane2.setPrefHeight(42);
			startBtn.setGraphic(startBtnImgView);
			startBtn.setLayoutX(0);
			startBtn.setLayoutY(5);
			startBtn.setPrefSize(26, 26);
			startBtn.getStylesheets().add("TestFx/view/myStyle.css");
			startBtn.getStyleClass().clear();
			startBtn.getStyleClass().add("start-button-white");
			pane2.getChildren().add(startBtn);

			pane3.setPrefWidth(30);
			pane3.setPrefHeight(42);
			stopBtn.setGraphic(stopBtnImgView);
			stopBtn.setLayoutX(5);
			stopBtn.setLayoutY(5);
			stopBtn.setPrefSize(26, 26);
			stopBtn.getStylesheets().add("TestFx/view/myStyle.css");
			stopBtn.getStyleClass().clear();
			stopBtn.getStyleClass().add("stop-button-white");
			stopBtn.setDisable(true);
			pane3.getChildren().add(stopBtn);

			hbox.getChildren().addAll(pane1, pane2, pane3);

			startBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					// detailView의 startBtn 동작하게 설정
					detailViewController.startBtn.fire();
				}
			});

			stopBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					detailViewController.stopBtn.fire();
				}
			});

		}

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			setText(null); // No text in label of super class
			if (empty) {
				lastItem = null;
				setGraphic(null);
				setTooltip(null);
			} else {
				lastItem = item;
				pcName.setText(item != null ? item : "<null>");
				setGraphic(hbox);
				
				// refresh 했을 때 해당 pc가 connect 돼있으면 조건 적용
				for (int i = 0; i < deviceData.size(); i++) {
					if (pcName.getText().equals(deviceData.get(i).getPcName())) {
						if (deviceData.get(i).isStart) {
							setStyle("-fx-background-color:green;-fx-opacity:0.7");
							tooltip = setToolTip(deviceData.get(i));
							setTooltip(tooltip);
							tooltip.setStyle(
									"-fx-background-color: lightblue;-fx-text-fill:black; -fx-opacity: 0.8;-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.5) , 10, 0.0 , 0 , 3 );");
							
							/*
							 * File test = new File("resources/pcList_start_button_white.png"); Image tt =
							 * new Image(test.toURI().toString()); ImageView ttt = new ImageView(tt);
							 * tooltip.setGraphic(ttt); tooltip.setGraphicTextGap(100);
							 */

							File imageFile = new File("resources/pcList_start_button_gray.png");
							Image startBtnImg = new Image(imageFile.toURI().toString());
							ImageView startBtnImgView = new ImageView(startBtnImg);
							startBtn.setGraphic(startBtnImgView);
							startBtnImgView.setFitWidth(23);
							startBtnImgView.setFitHeight(18);
							startBtn.setDisable(true);
							startBtn.setPrefSize(26, 26);

							File imageFile2 = new File("resources/pcList_stop_button_white.png");
							Image stopBtnImg = new Image(imageFile2.toURI().toString());
							ImageView stopBtnImgView = new ImageView(stopBtnImg);
							stopBtnImgView.setFitWidth(23);
							stopBtnImgView.setFitHeight(18);
							stopBtn.setGraphic(stopBtnImgView);
							stopBtn.setDisable(false);
							stopBtn.setPrefSize(26, 26);

						} else {
							setStyle("-fx-background-color: #2f5597");
							File imageFile = new File("resources/pcList_start_button_white.png");
							Image startBtnImg = new Image(imageFile.toURI().toString());
							ImageView startBtnImgView = new ImageView(startBtnImg);
							startBtn.setGraphic(startBtnImgView);
							startBtnImgView.setFitWidth(23);
							startBtnImgView.setFitHeight(18);
							startBtn.setDisable(false);
							startBtn.setPrefSize(26, 26);

							File imageFile2 = new File("resources/pcList_stop_button_gray.png");
							Image stopBtnImg = new Image(imageFile2.toURI().toString());
							ImageView stopBtnImgView = new ImageView(stopBtnImg);
							stopBtnImgView.setFitWidth(23);
							stopBtnImgView.setFitHeight(18);
							stopBtn.setGraphic(stopBtnImgView);
							stopBtn.setDisable(true);
							stopBtn.setPrefSize(26, 26);

						}
					}
				}
			}
		}
	}
}
