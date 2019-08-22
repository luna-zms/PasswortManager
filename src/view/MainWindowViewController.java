package view;

import java.io.IOException;

import controller.PMController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import util.BindingUtils;

public class MainWindowViewController extends BorderPane {
    private PMController pmController;

    @FXML // fx:id="entryList"
    private EntryListViewController entryList; // Value injected by FXMLLoader

    @FXML // fx:id="entryPreview"
    private EntryPreviewViewController entryPreview; // Value injected by FXMLLoader

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
        entryPreview.entryProperty().bind(entryList.getSelectionModel().selectedItemProperty());

        // Filter entries by selected tag
        entryList.filterProperty().bind(BindingUtils.makeBinding(
                tagTree.getSelectionModel().selectedItemProperty(),
                item -> (entry -> entry.getTags().contains(item.getValue())),
                entry -> true  // Accept all by default
        ));
    }

    public void init() {
        tagTree.init(false, pmController.getPasswordManager().getRootTag());
        entryList.setEntries(pmController.getPasswordManager().entriesObservable());
    }

    public void setPmController(PMController pmController) {
        this.pmController = pmController;
        setPmControllers();
    }

    private void setPmControllers() {
        entryList.setPmController(pmController);
        entryPreview.setPmController(pmController);
        mainWindowToolbar.setPmController(pmController);
    }
}
