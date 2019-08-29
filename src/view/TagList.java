package view;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import model.Tag;
import util.BindingUtil;

public class TagList extends ListView<Tag> {
    private ObservableValue<Map<Tag, String>> pathMap = new SimpleObjectProperty<>(new HashMap<>());

    public TagList() {
        setCellFactory(view -> new TagListCell());
        setFocusTraversable(false);  // Make non-selectable as it's read-only anyway
    }

    public void setPathMap(ObservableValue<Map<Tag, String>> pathMap) {
        this.pathMap = pathMap;
    }

    private class TagListCell extends ListCell<Tag> {
        @Override
        protected void updateItem(Tag tag, boolean empty) {
            super.updateItem(tag, empty);

            if (empty || tag == null) {
                textProperty().unbind();
                setText(null);
            }
            // TODO: Find out if this is necessary or if updateItem also gets triggered when the ObservableList is updated
            else {
                textProperty().bind(BindingUtil.makeBinding(pathMap, map -> map.get(tag), ""));
            }
        }
    }
}
