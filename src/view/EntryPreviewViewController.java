package view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import model.Entry;
import model.Tag;
import util.DateFormatUtil;

import static util.BindingUtil.makeBinding;

public class EntryPreviewViewController extends HBox {
    private ObjectProperty<Entry> entry = new SimpleObjectProperty<>();
    private ObservableValue<Map<Tag, String>> pathMap = new SimpleObjectProperty<>(new HashMap<>());

    @FXML // fx:id="tagList"
    private TagList tagList; // Value injected by FXMLLoader

    @FXML // fx:id="title"
    private TextField title; // Value injected by FXMLLoader

    @FXML // fx:id="url"
    private TextField url; // Value injected by FXMLLoader

    @FXML // fx:id="username"
    private TextField username; // Value injected by FXMLLoader

    @FXML // fx:id="validUntil"
    private TextField validUntil; // Value injected by FXMLLoader

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
        validUntil.textProperty().bind(makeBinding(entry, e -> DateFormatUtil.formatDate(e.getValidUntil()), ""));

        tagList.itemsProperty().bind(makeBinding(entry, e -> e.tagsObservable().sorted((t1, t2) -> {
            if( pathMap.getValue().get(t1) != null && pathMap.getValue().get(t2) != null )
                    return pathMap.getValue().get(t1).toLowerCase().compareTo(pathMap.getValue().get(t2).toLowerCase());
            return 0;
        }), FXCollections.emptyObservableList()));
    }

    public ObjectProperty<Entry> entryProperty() {
        return entry;
    }

    public void init(Tag rootTag) {
        pathMap = Bindings.createObjectBinding(
                rootTag::createPathMap,
                rootTag.nameProperty(),
                rootTag.subTagsObservable()
        );
        tagList.setPathMap(pathMap);
    }
}
