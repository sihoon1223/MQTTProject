package TestFx.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class IntroController implements Initializable {

	@FXML
	private StackPane rootPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		makeFadeOut();
	}

	private void makeFadeOut() {
		FadeTransition fadeTransition = new FadeTransition();
		fadeTransition.setDuration(Duration.millis(3000));
		fadeTransition.setNode(rootPane);
		fadeTransition.setFromValue(1);
		fadeTransition.setToValue(0);
		fadeTransition.setOnFinished(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				loadNextScene();

			}
		});
		fadeTransition.play();
	}

	public void loadNextScene() {
		
//			Parent nextView;
//			nextView = (StackPane) FXMLLoader.load(getClass().getResource("../view/rootLayout.fxml"));
//			Scene newScene = new Scene(nextView);
//			Stage curStage = (Stage) rootPane.getScene().getWindow();
//			curStage.setScene(newScene);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/rootLayout.fxml"));
		StackPane rootLayout = new StackPane();

		try {
			rootLayout = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Scene newScene = new Scene(rootLayout);
		//newScene.getStylesheets().add(getClass().getResource("../view/myStyle.css").toExternalForm());
		Stage curStage = (Stage) rootPane.getScene().getWindow();
		curStage.setScene(newScene);

		MainApplicationController controller = loader.getController();

		try {
			controller.registerStage(curStage); //드래그 모드
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
