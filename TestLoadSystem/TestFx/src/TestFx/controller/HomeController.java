package TestFx.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class HomeController implements Initializable {
	@FXML
	Label masterIP;
	@FXML
	Label targetIP;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setIP(MainApplicationController.masterIP, MainApplicationController.targetIP);
	}
	
	public void setIP (String master , String target) {
		masterIP.setText(master);
		targetIP.setText(target);
	}
}
