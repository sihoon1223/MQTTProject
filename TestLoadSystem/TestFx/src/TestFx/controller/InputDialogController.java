package TestFx.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class InputDialogController implements Initializable {
	@FXML
	TextField masterIP;
	@FXML
	TextField targetIP;
	@FXML
	AnchorPane rootPane;
	/*
	 * @FXML Button submitBtn;
	 * 
	 * @FXML Button cancelBtn;
	 */

	private Stage dialogStage;
	private boolean okClicked = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	// 창 드래그
	public void registerStage(Stage stage) {
		EffectUtilities.makeDraggable(stage, rootPane);
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	@FXML
	private void handleOk() {
		if (isInputValid()) {
			MainApplicationController.masterIP = masterIP.getText();
			MainApplicationController.targetIP = targetIP.getText();
			okClicked = true;
			dialogStage.close();
		}
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	private boolean isInputValid() {
		String errorMessage = "";

		if (masterIP.getText() == null || masterIP.getText().length() == 0 ) {
			errorMessage += "Please enter master broker's ip address.\n";
		}
		if (targetIP.getText() == null || targetIP.getText().length() == 0) {
			errorMessage += "Please enter target broker's ip address.\n";
		}

		if (errorMessage.length() == 0) {
			return true;
		} else {
			// 오류 메시지를 보여준다.
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
			alert.setTitle("Warning!");
			alert.setHeaderText("Empty IP address.");
			alert.setContentText(errorMessage);
			alert.showAndWait();

			return false;
		}
	}
}
