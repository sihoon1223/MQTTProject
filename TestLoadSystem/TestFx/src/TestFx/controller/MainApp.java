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

	public MainApp() {
		// 파일을 읽어와 토픽리스트를 구성하기
		Setting setting = new Setting();
		DetailViewController.alltopics_k = setting.getTopicList_k();
		DetailViewController.alltopics_e = setting.getTopicList_e();
	}

	@Override
	public void start(Stage primaryStage) throws Exception { // main 메서드에서 애플리케이션이 실행되면 자동으로 호출
		// Stage를 따로 class로 만들어 Static 변수로 선언하여 공용으로 사용한다.
		StageStore.stage = primaryStage;

		Parent root = FXMLLoader.load(getClass().getResource("../view/introView.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.show();

	}

	public void setRootLayout(AnchorPane rootLayout) {
		this.rootLayout = rootLayout;
	}

	public Stage getPrimaryStage() { // 메인 스테이지를 반환한다.
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
