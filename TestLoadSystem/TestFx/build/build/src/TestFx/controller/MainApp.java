package TestFx.controller;

import TestFx.mqtt.Setting;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {

	private Stage primaryStage;
	
	@SuppressWarnings("unused")
	private AnchorPane rootLayout;

	public MainApp() throws ClassNotFoundException {
		Setting setting = new Setting();
		DetailViewController.alltopics_k = setting.getTopicList_k();
		DetailViewController.alltopics_e = setting.getTopicList_e();
	}

	@Override 
	public void start(Stage primaryStage) throws Exception { 
        StageStore.stage = primaryStage;

		Parent root = FXMLLoader.load(Class.forName("TestFx.controller.MainApp").getResource("../view/introView.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.show();
		

	}

	public void setRootLayout(AnchorPane rootLayout) {
		this.rootLayout = rootLayout;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
