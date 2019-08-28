package view;

import controller.PMController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import model.Tag;
import util.WindowFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TagTree extends TreeView<Tag> {
    private PMController pmController;

    public BorderPane createPaneWithButtons() {
        BorderPane outer = new BorderPane();

        FlowPane inner = new FlowPane(Orientation.HORIZONTAL);
        inner.setAlignment(Pos.CENTER);

        Button remove = new Button();
        Button add = new Button();

        remove.setTooltip(new Tooltip("Ausgewähltes Schlagwort löschen"));
        add.setTooltip(new Tooltip("Neues Schlagwort"));

        inner.setMaxHeight(34);
        inner.setVgap(5);
        inner.setHgap(5);
        inner.setPadding(new Insets(5, 5, 5, 5));

        inner.getChildren().addAll(remove, add);

        outer.setCenter(this);
        outer.setBottom(inner);

        ImageView addCategoryimage = new ImageView(new Image(
            getClass().getResourceAsStream("/view/resources/add_category_toolbar_icon.png")));
        addCategoryimage.setFitHeight(24);
        addCategoryimage.setFitWidth(24);
        add.setGraphic(addCategoryimage);

        ImageView deleteCategoryimage = new ImageView(new Image(
            getClass().getResourceAsStream("/view/resources/delete_category_icon.png")));
        deleteCategoryimage.setFitHeight(24);
        deleteCategoryimage.setFitWidth(24);
        remove.setGraphic(deleteCategoryimage);

        remove.setOnMouseClicked(event -> deleteSelected());
        add.setOnMouseClicked(event -> createBelowSelected());

        return outer;
    }

    private void openCreateEntryDialog() {
        CreateModifyEntryViewController dialogController = new CreateModifyEntryViewController();
        dialogController.setPmController(pmController);
        dialogController.init();
        if (getSelectedTag() != null) {
            dialogController.setCheckedTags(Collections.singletonList(getSelectedTag()));
        }

        WindowFactory.showDialog("Eintrag erstellen", dialogController);
    }

    public void init(final boolean hasCheckBoxes, PMController controller) {
        pmController = controller;

        setCellFactory(treeView -> new TagTreeCell(hasCheckBoxes));

        setEditable(true);
        setRoot(new TagTreeItem(controller.getPasswordManager().getRootTag()));
    }

    private TreeItem<Tag> getSelectedItem() {
        return getSelectionModel().getSelectedItem();
    }

    public List<Tag> getCheckedTags() {
        return ((TagTreeItem) getRoot()).getSelectedSubTags();
    }

    public void setCheckedTags(List<Tag> tags) {
        ((TagTreeItem) getRoot()).setCheckedIfAny(tags);
    }

    public Tag getSelectedTag() {
        return getSelectedItem() == null
               ? null
               : getSelectedItem().getValue();
    }

    public void deleteSelected() {
        deleteItem(getSelectedItem());
    }

    private void deleteItem(TreeItem<Tag> item) {
        if (item == null) {
            return;
        }

        TreeItem<Tag> parent = item.getParent();

        if (parent != null) {  // Cannot delete root node
            if (item.getValue() != null) {
                pmController.getTagController().removeTag(parent.getValue(), item.getValue());
            }
            parent.getChildren().remove(item);
        }
    }

    public void editSelected() {
        edit(getSelectedItem());
    }

    public void createBelowSelected() {
        TreeItem<Tag> selected = getSelectedItem();
        TagTreeItem newItem = new TagTreeItem(null);

        selected.getChildren().add(newItem);
    }

    private ContextMenu createContextMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem createTag = new MenuItem("Neues Schlagwort");
        MenuItem createEntry = new MenuItem("Neuer Eintrag");
        MenuItem edit = new MenuItem("Bearbeiten");
        MenuItem delete = new MenuItem("Löschen");

        createTag.setOnAction(event -> createBelowSelected());
        createEntry.setOnAction(event -> openCreateEntryDialog());
        edit.setOnAction(event -> editSelected());
        delete.setOnAction(event -> deleteSelected());

        menu.getItems().addAll(createEntry, createTag, new SeparatorMenuItem(), edit, delete);

        menu.setOnShowing(event -> delete.setDisable(getSelectedItem() == getRoot()));

        return menu;
    }

    private static class TagTreeItem extends TreeItem<Tag> {
        private boolean checked;

        TagTreeItem(Tag tag) {
            super(tag);

            setExpanded(true);

            if (tag != null) {
                tag.getSubTags().forEach(subtag -> getChildren().add(new TagTreeItem(subtag)));
            }
        }

        boolean isChecked() {
            return checked;
        }

        void setChecked(boolean checked) {
            this.checked = checked;
        }

        void setCheckedIfAny(List<Tag> tags) {
            if (tags.contains(getValue())) {
                checked = true;
            }

            getChildren().forEach(treeItem -> ((TagTreeItem) treeItem).setCheckedIfAny(tags));
        }

        List<Tag> getSelectedSubTags() {
            List<Tag> selectedSubItems = getChildren()
                .stream()
                .flatMap(treeItem -> ((TagTreeItem) treeItem).getSelectedSubTags().stream())
                .collect(Collectors.toList());

            if (isChecked()) {
                selectedSubItems.add(getValue());
            }

            return selectedSubItems;
        }
    }

    private class TagTreeCell extends TreeCell<Tag> implements ChangeListener<Boolean> {
        private CheckBox checkbox;

        TagTreeCell(boolean hasCheckBox) {
            if (hasCheckBox) {
                checkbox = new CheckBox();
                checkbox.selectedProperty().addListener(this);
            }
            setContextMenu(createContextMenu());
        }

        private TextField createEditTextField() {
            TextField wtf = new TextField();

            if (getItem() != null) {
                wtf.setText(getItem().getName());
            }

            final ChangeListener<? super Boolean> focusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                    finishEdit(wtf.getText());
                }
            };

            wtf.focusedProperty().addListener(focusListener);
            wtf.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
                switch (keyEvent.getCode()) {
                    case ENTER:
                        keyEvent.consume();

                        wtf.focusedProperty().removeListener(focusListener);
                        finishEdit(wtf.getText());
                        break;
                    case ESCAPE:
                        keyEvent.consume();

                        cancelEdit();
                        break;
                    default:
                        break;
                }
            });

            wtf.setTextFormatter(new TextFormatter<>(change -> {
                String text = change.getControlNewText();
                if (text.contains("\\") || text.contains(";")) return null;
                return change;
            }));

            return wtf;
        }

        private void finishEdit(String str) {
            if (getTreeItem() == null) {
                return;
            }

            TreeItem<Tag> parentTag = getTreeItem().getParent();

            if (parentTag == null) {
                return;
            }

            if (str.isEmpty()) {
                cancelEdit();
            } else if (parentTag.getValue().hasSubTag(str)) {
                if (getItem() == null || !getItem().getName().equals(str)) {
                    Alert errorAlert = new Alert(Alert.AlertType.WARNING);
                    errorAlert.setTitle("Dupliziertes Schlagwort");
                    errorAlert.setHeaderText("Schlagwort existiert bereits");
                    errorAlert.setContentText(
                        "Ein Schlagwort mit dem Namen '" + str + "' existiert bereits. \n Wähle einen anderen Namen");
                    errorAlert.showAndWait();
                }

                cancelEdit();
            } else {
                Tag tag;

                if (getItem() == null) { // newly created tags
                    tag = new Tag(str);
                    pmController.getTagController().addTag(parentTag.getValue(), tag);
                    getTreeItem().setValue(tag);
                } else { // Editing of existing tags
                    tag = getItem();
                    pmController.getTagController().renameTag(tag, str);
                }

                commitEdit(tag);
            }
        }

        private void setToTextField() {
            TextField editTextField = createEditTextField();

            setText(null);
            setGraphic(editTextField);
            editTextField.requestFocus();
        }

        @Override
        protected void updateItem(Tag tag, boolean empty) {
            super.updateItem(tag, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (tag == null || tag.getName().isEmpty()) {
                    setToTextField();
                } else {
                    if (checkbox != null) checkbox.setSelected(((TagTreeItem) getTreeItem()).isChecked());

                    setText(tag.getName());
                    setGraphic(checkbox);
                }
            }
        }

        @Override
        public void startEdit() {
            super.startEdit();

            setToTextField();
        }

        @Override
        public void commitEdit(Tag tag) {
            super.commitEdit(tag);

            setText(tag.getName());
            setGraphic(checkbox);
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            Tag tag = getItem();

            if (tag == null || tag.getName().isEmpty()) {
                deleteItem(getTreeItem());
            } else {
                setText(tag.getName());
                setGraphic(checkbox);
            }
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            TagTreeItem item = (TagTreeItem) getTreeItem();

            if (item != null) {
                item.setChecked(newValue);
            }
        }
    }
}
