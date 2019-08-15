package view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class MainWindowToolbarController extends GridPane {
	
	@FXML
	private Button addEntryToolbar;
	
	@FXML
	private Button addCategoryToolbar;
	
	@FXML
	private Button saveDatabaseToolbar;
	
	@FXML
	private Button openDatabaseToolbar;
	
	@FXML
	private Button setMasterPasswordToolbar;
	
	@FXML
	private Button importDatabaseToolbar;
	
	@FXML
	private Button exportDatabaseToolbar;
	
	@FXML
	private Button searchButtonToolbar;
	
	public MainWindowToolbarController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainWindowToolbar.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		Image addEntryImage = new Image(getClass().getResourceAsStream("add_category_toolbar_icon.png"));
		
		addEntryToolbar.setGraphic(new ImageView(addEntryImage));
	}	
}
