package view;

import controller.PMController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainWindowViewController extends BorderPane {
    private PMController pmController;

    @FXML // fx:id="entryNamePreview"
    private Label entryNamePreview; // Value injected by FXMLLoader

    @FXML // fx:id="usernamePreview"
    private Label usernamePreview; // Value injected by FXMLLoader

    @FXML // fx:id="urlPreview"
    private Label urlPreview; // Value injected by FXMLLoader

    @FXML // fx:id="validUntilPreview"
    private Label validUntilPreview; // Value injected by FXMLLoader

    @FXML // fx:id="tagListPreview"
    private ListView<?> tagListPreview; // Value injected by FXMLLoader

    @FXML // fx:id="tagTree"
    private TagTree tagTree; // Value injected by FXMLLoader

    @FXML // fx:id="mainWindowToolbar"
    private MainWindowToolbarViewController mainWindowToolbar;

    public MainWindowViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainWindowView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPmController(PMController pmController) {
        this.pmController = pmController;

        tagTree.init(false, pmController);

        mainWindowToolbar.setPmController(pmController);
    }
}
