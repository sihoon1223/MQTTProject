package TestFx.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class SettingController implements Initializable {
	@FXML
	private TextField masterIP;
	@FXML
	private TextField targetIP;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		masterIP.setText(MainApplicationController.masterIP);
		targetIP.setText(MainApplicationController.targetIP);
	}
	
	@FXML
	public void saveAction(ActionEvent action) {
		MainApplicationController.masterIP = masterIP.getText();
		MainApplicationController.targetIP = targetIP.getText();
		
	}

}
