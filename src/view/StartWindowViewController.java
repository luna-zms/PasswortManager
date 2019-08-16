package view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class StartWindowViewController {
	
	@FXML
	private TextField choosePasswordAchivePath;
	
	@FXML
	private HBox customPasswordFieldStartWindow;
	
	//@FXML
	//private CusomPasswordFieldViewController customPasswordFieldStartWindowController;

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
