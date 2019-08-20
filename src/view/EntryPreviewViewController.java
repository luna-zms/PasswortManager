package view;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import model.Entry;
import model.Tag;

public class EntryPreviewViewController extends HBox {
    private ObjectProperty<Entry> entry = new SimpleObjectProperty<>();

    @FXML // fx:id="tagList"
    private ListView<Tag> tagList; // Value injected by FXMLLoader

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
        title.textProperty().bind(Bindings.createStringBinding(
                () -> entry.get() != null ? entry.get().getTitle() : "",
                entry
        ));
        url.textProperty().bind(Bindings.createStringBinding(
                () -> entry.get() != null ? entry.get().getUrlString() : "",
                entry
        ));
        username.textProperty().bind(Bindings.createStringBinding(
                () -> entry.get() != null ? entry.get().getUsername() : "",
                entry
        ));
        validUntil.textProperty().bind(Bindings.createStringBinding(
                () -> entry.get() != null ? entry.get().getValidUntilString() : "",
                entry
        ));
    }

    public ObjectProperty<Entry> entryProperty() {
        return entry;
    }
}
