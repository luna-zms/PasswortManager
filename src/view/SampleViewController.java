package view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

public class SampleViewController extends BorderPane{
	
	public SampleViewController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SampleView.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
