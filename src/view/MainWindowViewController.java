package view;

import controller.PMController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

import model.Tag;
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
    }
    private Tag getRootTag() {
        return pmController.getPasswordManager().getRootTag();
    }
    public void init() {
        // Bind preview to update when table selection changes
        entryPreview.entryProperty().bind(entryList.getSelectionModel().selectedItemProperty());
        entryList.tagProperty()
                 .bind(BindingUtils.makeBinding(tagTree.getSelectionModel().selectedItemProperty(),
                                                TreeItem::getValue, getRootTag()));
        mainWindowToolbar.setOnSearchRefreshAction((filter, searchEverywhere) -> {
            if (!searchEverywhere) {
                filter = filter.and(entry -> entry.getTags().contains(getRootTag()));
            }
            entryList.filterOnce(filter);
        });

        tagTree.init(false, pmController);
        // Prevent limbo state of no tag being selected
        tagTree.getSelectionModel().selectFirst();

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
