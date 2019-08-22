package view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import controller.PMController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import model.Entry;
import model.Tag;

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

        // TODO: Fetch from model
        Tag rootTag = new Tag("Root Tag");
        tagTree.init(true, rootTag);

        // TODO: Fetch from model
        List<Entry> entries = new ArrayList<>();
        Entry entry = new Entry("Hello there", "asdf");
        entry.setUsername("userSample");

        URL url = null;
        try {
            url = new URL("http://example.com");
        } catch (MalformedURLException e) {
        }

        entry.setUrl(url);
        entry.setValidUntil(LocalDateTime.MAX);

        entries.add(entry);
        entryTable.setEntries(FXCollections.observableList(entries));
    }

    public void setPmController(PMController pmController) {
        this.pmController = pmController;

        mainWindowToolbar.setPmController(pmController);
    }
}
