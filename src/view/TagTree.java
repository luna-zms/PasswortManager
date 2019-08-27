package view;

import controller.PMController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.Tag;
import util.WindowFactory;

import java.util.List;
import java.util.stream.Collectors;

public class TagTree extends TreeView<Tag> {
    private PMController pmController;

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
        return getSelectedItem() == null ? null : getSelectedItem().getValue();
    }

    public void deleteSelected() {
        deleteItem(getSelectedItem());
    }

    private void deleteItem(TreeItem<Tag> item) {
        if (item == null)
            return;

        TreeItem<Tag> parent = item.getParent();

        if (parent != null) {  // Cannot delete root node
            // TODO: Once we do Controllers, this needs to be moved there (to some removeTag(Tag tag) method that also goes through all entries
            parent.getValue().getSubTags().remove(item.getValue());
            parent.getChildren().remove(item);
        } // TODO: maybe display some warning?
    }

    public void editSelected() {
        edit(getSelectedItem());
    }

    public void createBelowSelected() {
        TreeItem<Tag> selected = getSelectedItem();
        //Tag newTag = new Tag("");
        TagTreeItem newItem = new TagTreeItem(null);

        // TODO: delegate to controller
        selected.getChildren().add(newItem);
        //selected.getValue().getSubTags().add(newTag);
    }

    private ContextMenu createContextMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem createTag = new MenuItem("Neues Schlagwort");
        MenuItem createEntry = new MenuItem("Neuer Eintrag");
        MenuItem edit = new MenuItem("Bearbeiten");
        MenuItem delete = new MenuItem("Löschen");

        createTag.setOnAction(event -> createBelowSelected());
        createEntry.setOnAction(event -> {
            CreateModifyEntryViewController dialogController = new CreateModifyEntryViewController();
            dialogController.setPmController(pmController);
            Stage stage = WindowFactory.createStage("Eintrag erstellen");
            stage.show();
            stage.setScene(WindowFactory.createScene(dialogController));
            dialogController.init();
        });
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

            if (tag != null)
                tag.getSubTags().forEach(subtag -> getChildren().add(new TagTreeItem(subtag)));
        }

        boolean isChecked() {
            return checked;
        }

        void setChecked(boolean checked) {
            this.checked = checked;
        }

        void setCheckedIfAny(List<Tag> tags) {
            if (tags.contains(getValue()))
                checked = true;

            getChildren().forEach(treeItem -> ((TagTreeItem) treeItem).setCheckedIfAny(tags));
        }

        List<Tag> getSelectedSubTags() {
            List<Tag> selectedSubItems = getChildren()
                    .stream()
                    .flatMap(treeItem -> ((TagTreeItem) treeItem).getSelectedSubTags().stream())
                    .collect(Collectors.toList());

            if (isChecked())
                selectedSubItems.add(getValue());

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

            if (getItem() != null)
                wtf.setText(getItem().getName());

            final ChangeListener<? super Boolean> focusListener = (observable, oldValue, newValue) -> {
                if (!newValue)
                    finishEdit(wtf.getText());
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

            return wtf;
        }

        private void finishEdit(String str) {
            TreeItem<Tag> parentTag = getTreeItem().getParent();

            if (parentTag == null)
                return;

            // TODO: maybe show a warning/info message when isDuplicate is true

            if (str.isEmpty() || parentTag.getValue().hasSubTag(str)) {
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

            if (item != null)
                item.setChecked(newValue);
        }
    }
}
