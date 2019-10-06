package TestFx.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import TestFx.model.Device;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
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

	@SuppressWarnings("unused")
	private MainApp mainApp;
	private ObservableList<String> listItems;
	private ArrayList<String> test = new ArrayList<>(
			Arrays.asList("PC 0", "PC 1", "PC 2", "PC 3", "PC 4", "PC 5", "PC 6", "PC 7", "PC 8", "PC 9"));

	public static ObservableList<Device> deviceData = FXCollections.observableArrayList();

	private DetailViewController detailViewController;
	private MainApplicationController mainApplicationController = this;

	private Stage stage = StageStore.stage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		rootLayout = (AnchorPane) stage.getScene().getRoot();

		rootPane.setOpacity(0);
		makeFadeIn();


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

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("../view/homeView.fxml"));
		AnchorPane homeView;
		try {
			homeView = (AnchorPane) loader.load();
			mainPane.getChildren().add(homeView);
		} catch (IOException e) {
			e.printStackTrace();
		}

		pcList.setStyle("-fx-padding: 0px ; -fx-background-insets:0 0 0 0 0,0,0");
		pcList.setItems(listItems);
		pcList.getStylesheets().add("TestFx/view/myStyle.css");
	}

	public ObservableList<Device> getDeviceData() {
		return deviceData;
	}

	private void makeFadeIn() {
		FadeTransition fadeTransition = new FadeTransition();
		fadeTransition.setDuration(Duration.millis(3000));
		fadeTransition.setNode(rootPane);
		fadeTransition.setFromValue(0);
		fadeTransition.setToValue(1);

		fadeTransition.play();
	}

	public void registerStage(Stage stage) {
		EffectUtilities.makeDraggable(stage, rootPane);
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		mainApp.setRootLayout(rootLayout);
	}

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

	@FXML
	public void deleteAction(ActionEvent action) {

		int selectedIndex = pcList.getSelectionModel().getSelectedIndex();
		Stage curStage = (Stage) pcList.getScene().getWindow();
		if (pcList.getItems().size() > 1) {
			if (selectedIndex >= 0) { 
				listItems.remove(selectedIndex);
				deviceData.remove(selectedIndex);


				// selectedIndex = selectedIndex -1;

			} else {
				Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(curStage);
				alert.setTitle("~~");
				alert.setHeaderText("~~");
				alert.setContentText("Please select a device in the list.");

				alert.showAndWait();

			}
		} else if (pcList.getItems().size() == 1) { 
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(curStage);
			alert.setTitle("~~");
			alert.setHeaderText("~~");
			alert.setContentText("~~");

			alert.showAndWait();

		}
	}


	@FXML
	public void infoAction(ActionEvent action) {

	}


	@FXML
	public void homeAction(ActionEvent action) throws ClassNotFoundException {

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Class.forName("TestFx.controller.MainApplicationController").getResource("../view/homeView.fxml"));
		AnchorPane homeView;
 
		try {
			homeView = (AnchorPane) loader.load();
			mainPane.getChildren().clear();
			mainPane.getChildren().add(homeView);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void closeAction(ActionEvent action) {
		System.exit(0);
	}

	public class MyEventHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent t) {
			String name = pcList.getSelectionModel().getSelectedItem();

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

			DetailViewController controller = loader.getController();
			controller.setMainApplicationController(mainApplicationController);
			detailViewController = controller;
			controller.setPcName(name);

			for (int i = 0; i < deviceData.size(); i++) {
				if (name.equals(deviceData.get(i).getPcName())) {
					if (deviceData.contains(deviceData.get(i))) {
						if (deviceData.get(i).isSaved) {
							controller.clearData();
							controller.showDetailData(deviceData.get(i));
						}
					} else {
						controller.clearData();
					}
				}


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
	}

	public void setDetailViewController(DetailViewController controller) {
		this.detailViewController = controller;
	}

	// pc����Ʈ �ȿ� �� ListCell Ŭ����
	public class XCell extends ListCell<String> {
		String lastItem;

		HBox hbox = new HBox();

		AnchorPane pane1 = new AnchorPane();
		Label pcName = new Label();

		AnchorPane pane2 = new AnchorPane();
		public Button startBtn = new Button();

		AnchorPane pane3 = new AnchorPane();
		Button stopBtn = new Button();

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
			} else {
				lastItem = item;
				pcName.setText(item != null ? item : "<null>");
				setGraphic(hbox);

				for (int i = 0; i < deviceData.size(); i++) {
					if (pcName.getText().equals(deviceData.get(i).getPcName())) {
						if (deviceData.get(i).isStart) {
							
							/*File imageFile = new File("resources/pcList_start_button_gray.png");
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
							stopBtn.setPrefSize(26, 26);*/

						} else {

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
