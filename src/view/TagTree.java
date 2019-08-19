package view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import model.Tag;

public class TagTree extends TreeView<Tag> {
    public void init(final boolean hasCheckBoxes, Tag rootTag) {
        setCellFactory(treeView -> new TagTreeCell(hasCheckBoxes));

        setEditable(true);
        setRoot(new TagTreeItem(rootTag));
    }

    private TreeItem<Tag> getSelectedItem() {
        return getSelectionModel().getSelectedItem();
    }

    public Tag getSelected() {
        return getSelectedItem().getValue();
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
        this.edit(getSelectedItem());
    }

    public void createBelowSelected() {
        TreeItem<Tag> selected = getSelectedItem();
        Tag newTag = new Tag();
        TagTreeItem newItem = new TagTreeItem(newTag);

        selected.getChildren().add(newItem);
        selected.getValue().getSubTags().add(newTag);
    }

    private ContextMenu createContextMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem create = new MenuItem("Neu");
        MenuItem edit = new MenuItem("Bearbeiten");
        MenuItem delete = new MenuItem("Löschen");

        create.setOnAction(event -> createBelowSelected());
        edit.setOnAction(event -> editSelected());
        delete.setOnAction(event -> deleteSelected());

        menu.getItems().addAll(create, new SeparatorMenuItem(), edit, delete);

        menu.setOnShowing(event -> delete.setDisable(getSelectedItem() == getRoot()));

        return menu;
    }

    private static class TagTreeItem extends TreeItem<Tag> {
        private boolean checked;

        TagTreeItem(Tag tag) {
            super(tag);

            setExpanded(true);

            tag.getSubTags().forEach(subtag -> getChildren().add(new TagTreeItem(subtag)));
        }

        boolean isChecked() {
            return checked;
        }

        void setChecked(boolean checked) {
            this.checked = checked;
        }
    }

    private class TagTreeCell extends TreeCell<Tag> implements ChangeListener<Boolean> {
        private CheckBox checkbox;

        TagTreeCell(boolean hasCheckBox) {
            if (hasCheckBox)
                checkbox = new CheckBox();
            setContextMenu(createContextMenu());
        }

        private TextField createEditTextField() {
            TextField wtf = new TextField(getItem().getName());

            wtf.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
                switch (keyEvent.getCode()) {
                    case ENTER:
                        finishEdit(wtf.getText());
                        break;
                    case ESCAPE:
                        cancelEdit();
                        break;
                    default:
                        break;
                }
                keyEvent.consume();
            });

            wtf.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue)
                    finishEdit(wtf.getText());
            });

            return wtf;
        }

        private void finishEdit(String str) {
            if (getItem() == null)
                return;

            boolean isDuplicate = getTreeItem().getParent() != null && getTreeItem().getParent().getValue().hasSubTag(str);

            // TODO: maybe show a warning/info message when isDuplicate is true

            if (str.isEmpty() || isDuplicate) {
                cancelEdit();
            } else {
                Tag tag = getItem();
                tag.setName(str);

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
                if (tag.getName().isEmpty()) {
                    setToTextField();
                } else {
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

            if (tag.getName().isEmpty()) {
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
