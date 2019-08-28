package view;

import controller.PMController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

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
        setLeft(tagTree.createPaneWithButtons());
        //System.out.println(this.getLeft());
        widthProperty().addListener((observable, oldWidth, newWidth) ->
                ((BorderPane) getLeft()).setMaxWidth( newWidth.doubleValue() <= 1000 ? newWidth.doubleValue()/5 : 200 )
        );
    }

    private Tag getRootTag() {
        return pmController.getPasswordManager().getRootTag();
    }

    public void init(Consumer<Path> showOpenDialog) {
        // Init subcomponents
        entryList.init();
        entryPreview.init(getRootTag());
        mainWindowToolbar.setOpenDatabaseFileAction(showOpenDialog);

        // Bind preview to update when table selection changes
        entryPreview.entryProperty().bind(entryList.getSelectionModel().selectedItemProperty());
        entryList.tagProperty()
                 .bind(BindingUtils.makeBinding(tagTree.getSelectionModel().selectedItemProperty(),
                                                TreeItem::getValue, getRootTag()));
        mainWindowToolbar.setOnSearchRefreshAction((filter, searchEverywhere) -> {
            if (!searchEverywhere) {
                filter = filter.and(entry -> entry.getTags().contains(entryList.tagProperty().getValue()));
            }
            entryList.filterOnce(filter);
        });
        mainWindowToolbar.setOnTreeViewRefresh(() -> tagTree.init(false, pmController));

        tagTree.init(false, pmController);
        // Prevent limbo state of no tag being selected
        tagTree.getSelectionModel().selectFirst();
    }

    public void setPmController(PMController pmController) {
        this.pmController = pmController;
        setPmControllers();
    }

    private void setPmControllers() {
        entryList.setPmController(pmController);
        mainWindowToolbar.setPmController(pmController);
    }
}
