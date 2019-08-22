package view;

import java.io.IOException;

import controller.PMController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import model.Entry;

import static util.BindingUtils.makeBinding;

public class EntryPreviewViewController extends HBox {
    private ObjectProperty<Entry> entry = new SimpleObjectProperty<>();

    @FXML // fx:id="tagList"
    private TagList tagList; // Value injected by FXMLLoader

    @FXML // fx:id="title"
    private Label title; // Value injected by FXMLLoader

    @FXML // fx:id="url"
    private Label url; // Value injected by FXMLLoader

    @FXML // fx:id="username"
    private Label username; // Value injected by FXMLLoader

    @FXML // fx:id="validUntil"
    private Label validUntil; // Value injected by FXMLLoader

    public EntryPreviewViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EntryPreviewView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Bind labels to update whenever entry object changes
        title.textProperty().bind(makeBinding(entry, Entry::getTitle, ""));
        url.textProperty().bind(makeBinding(entry, Entry::getUrlString, ""));
        username.textProperty().bind(makeBinding(entry, Entry::getUsername, ""));
        validUntil.textProperty().bind(makeBinding(entry, Entry::getValidUntilString, ""));
        tagList.itemsProperty().bind(makeBinding(entry, Entry::tagsObservable, FXCollections.emptyObservableList()));
    }

    public ObjectProperty<Entry> entryProperty() {
        return entry;
    }

    public void setPmController(PMController pmController) {
        tagList.setPmController(pmController);
    }
}
