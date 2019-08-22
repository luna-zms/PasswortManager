package view;

import java.io.IOException;

import controller.PMController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import model.Entry;

public class MainWindowViewController extends BorderPane {
    private PMController pmController;

    @FXML // fx:id="entryPreview"
    private EntryPreviewViewController entryPreview; // Value injected by FXMLLoader

    @FXML // fx:id="entryTable"
    private EntryListViewController entryTable; // Value injected by FXMLLoader

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

        // Bind preview to update when table selection changes
        entryPreview.entryProperty().bind(entryTable.getSelectionModel().selectedItemProperty());
    }

    public void init() {
        tagTree.init(false, pmController.getPasswordManager().getRootTag());
        entryTable.setEntries(pmController.getPasswordManager().entriesObservable());
    }

    public void setPmController(PMController pmController) {
        this.pmController = pmController;
        setPmControllers();
    }

    private void setPmControllers() {
        entryPreview.setPmController(pmController);
        mainWindowToolbar.setPmController(pmController);
    }
}
