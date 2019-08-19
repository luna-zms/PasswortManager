package view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class StartWindowViewController extends GridPane {
	
	@FXML
	private TextField choosePasswordAchivePath;
	
	@FXML
	private CustomPasswordFieldViewController customPasswordField;

	public StartWindowViewController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StartWindowView.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
