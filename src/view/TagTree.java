package view;

import java.util.List;
import java.util.stream.Collectors;

import controller.PMController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import model.Tag;
import factory.WindowFactory;

import java.util.Collections;

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

        try {
            ImageView addCategoryImage = new ImageView(new Image(
                    getClass().getResourceAsStream("/view/resources/add_category_toolbar_icon.png")));
            addCategoryImage.setFitHeight(24);
            addCategoryImage.setFitWidth(24);
            add.setGraphic(addCategoryImage);

            ImageView deleteCategoryImage = new ImageView(new Image(
                    getClass().getResourceAsStream("/view/resources/delete_category_icon.png")));
            deleteCategoryImage.setFitHeight(24);
            deleteCategoryImage.setFitWidth(24);
            remove.setGraphic(deleteCategoryImage);
        } catch( Exception e ) {
            WindowFactory.showError("Kritischer Fehler", "Beim Laden der Programmdaten ist ein Fehler aufgetreten! Vergewissern Sie sich, dass Sie das Programm korrekt installiert haben! Das Programm schließt sich nach dieser Meldung.");
            System.exit(1);
        }

        remove.setOnAction(event -> deleteSelected());
        add.setOnAction(event -> createBelowSelected());

        return outer;
    }

    private void openCreateEntryDialog() {
        if (getSelectedTag() != null) {
            WindowFactory.showCreateModifyEntryView(pmController, Collections.singletonList(getSelectedTag()));
        } else {
        	WindowFactory.showCreateModifyEntryView(pmController);
        }

    }

    public void init(final boolean hasCheckBoxes, PMController controller) {
        pmController = controller;

        setEditable(true);
        setCellFactory(treeView -> new TagTreeCell(hasCheckBoxes));

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
        if (item == null) return;

        TreeItem<Tag> parent = item.getParent();

        if (parent != null) {  // Cannot delete root node
            if (item.getValue() != null) {
                pmController.getTagController().removeTag(parent.getValue(), item.getValue());
            }
        }
    }

    public void editSelected() {
        edit(getSelectedItem());
    }

    public void createBelowSelected() {
        TreeItem<Tag> selected = getSelectedItem();
        TagTreeItem newItem = new TagTreeItem(null);

        if( selected == null )
            selected = getRoot();
        selected.getChildren().add(newItem);

        TreeItem<Tag> finalSelected = selected;
        setOnMouseClicked(event -> {
            if( newItem.getValue() == null )
                finalSelected.getChildren().remove(newItem);
        });
    }

    private ContextMenu createContextMenu(boolean selectorDialog) {
        ContextMenu menu = new ContextMenu();

        MenuItem createTag = new MenuItem("Neues Schlagwort");
        MenuItem createEntry = null;
        MenuItem edit = new MenuItem("Bearbeiten");
        MenuItem delete = new MenuItem("Löschen");

        createTag.setOnAction(event -> createBelowSelected());
        edit.setOnAction(event -> editSelected());
        delete.setOnAction(event -> deleteSelected());


        if( !selectorDialog ) {
            createEntry = new MenuItem("Neuer Eintrag");
            createEntry.setOnAction(event -> openCreateEntryDialog());
            menu.getItems().addAll(createEntry, createTag, new SeparatorMenuItem(), edit, delete);
        } else
            menu.getItems().addAll(createTag, new SeparatorMenuItem(), edit, delete);

        menu.setOnShowing(event -> {
            delete.setDisable(getSelectedItem() == getRoot());
            edit.setDisable(getSelectedItem() == getRoot());
        });

        return menu;
    }

    private static class TagTreeItem extends TreeItem<Tag> implements ListChangeListener<Tag> {
        private boolean checked;

        TagTreeItem(Tag tag) {
            super(tag);

            setExpanded(true);

            if (tag != null) {
                tag.getSubTags().forEach(subtag -> getChildren().add(new TagTreeItem(subtag)));
                tag.subTagsObservable().addListener(this);
            }
        }

        boolean isChecked() {
            return checked;
        }

        void setChecked(boolean checked) {
            this.checked = checked;
        }

        void setCheckedIfAny(List<Tag> tags) {
            if (tags.contains(getValue())) checked = true;

            getChildren().forEach(treeItem -> ((TagTreeItem) treeItem).setCheckedIfAny(tags));
        }

        List<Tag> getSelectedSubTags() {
            List<Tag> selectedSubItems = getChildren().stream()
                                                      .flatMap(treeItem -> ((TagTreeItem) treeItem).getSelectedSubTags()
                                                                                                   .stream())
                                                      .collect(Collectors.toList());

            if (isChecked()) selectedSubItems.add(getValue());

            return selectedSubItems;
        }

        @Override
        public void onChanged(Change<? extends Tag> change) {
            while (change.next()) {
                // If the add was done via this TagTree, the TreeItem already exists!
                outer:
                for (Tag added : change.getAddedSubList()) {
                    for (TreeItem<Tag> item : getChildren()) {
                        if (item.getValue().getName().equals(added.getName())) continue outer;
                    }
                    getChildren().add(new TagTreeItem(added));
                }
                getChildren().removeAll(getChildren().stream()
                                                     .filter(child -> change.getRemoved()
                                                                            .contains(child.getValue()))
                                                     .collect(Collectors.toList()));
            }
        }
    }

    private class TagTreeCell extends TreeCell<Tag> implements ChangeListener<Boolean> {
        private CheckBox checkbox;

        TagTreeCell(boolean hasCheckBox) {
            if (hasCheckBox) {
                checkbox = new CheckBox();
                checkbox.selectedProperty().addListener(this);
            }
            setContextMenu(createContextMenu(hasCheckBox));

            // No comment
            addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent mouseEvent) -> {
                TagTreeItem target;
                if( mouseEvent.getTarget().getClass().getSuperclass() == Text.class) {
                    target = (TagTreeItem) ((TagTreeCell) ((Text) mouseEvent.getTarget()).getParent()).getTreeItem();
                } else if( mouseEvent.getTarget().getClass() == TagTreeCell.class ) {
                    target = (TagTreeItem) ((TagTreeCell) mouseEvent.getTarget()).getTreeItem();
                } else return;

                if( target != getSelectedItem() ) return;
                if ( getSelectedItem() == getRoot() ||
                        (mouseEvent.getClickCount() < 2 && mouseEvent.getButton().equals(MouseButton.PRIMARY)))
                    mouseEvent.consume();
                else if( mouseEvent.getClickCount() >= 2 ) {
                    editSelected();
                    mouseEvent.consume();
                }
            });
        }

        private TextField createEditTextField() {
            TextField wtf = new TextField();

            if (getItem() != null) wtf.setText(getItem().getName());

            final ChangeListener<? super Boolean> focusListener = (observable, oldValue, newValue) -> {
                if (!newValue) finishEdit(wtf.getText());
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
                String text = change.getText();
                if (text.contains("\\") || text.contains(";"))
                    change.setText(text.replace("\\", "﹨").replace(";", ";"));
                return change;
            }));

            return wtf;
        }

        private void finishEdit(String str) {
            if (getTreeItem() == null) return;

            TreeItem<Tag> parentTag = getTreeItem().getParent();

            if (parentTag == null) return;

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
                    getTreeItem().setValue(tag);
                    pmController.getTagController().addTag(parentTag.getValue(), tag);
                } else { // Editing of existing tags
                    tag = getItem();
                    pmController.getTagController().renameTag(tag, str);
                }

                commitEdit(tag);
            }
        }

        private void setToTextField() {
            TextField editTextField = createEditTextField();

            textProperty().unbind();
            setText(null);
            setGraphic(editTextField);
            editTextField.requestFocus();
        }

        @Override
        protected void updateItem(Tag tag, boolean empty) {
            super.updateItem(tag, empty);

            if (empty) {
                textProperty().unbind();
                setText(null);
                setGraphic(null);
            } else {
                if (tag == null || tag.getName().isEmpty()) {
                    setToTextField();
                } else {
                    if (checkbox != null) {
                        checkbox.setSelected(((TagTreeItem) getTreeItem()).isChecked());
                    }

                    textProperty().bind(tag.nameProperty());
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

            textProperty().bind(tag.nameProperty());
            setGraphic(checkbox);
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            Tag tag = getItem();

            if (tag == null || tag.getName().isEmpty()) {
                deleteItem(getTreeItem());
            } else {
                textProperty().bind(tag.nameProperty());
                setGraphic(checkbox);
            }
        }

        @Override
        public void changed(
                ObservableValue<? extends Boolean> observableValue,
                Boolean oldValue,
                Boolean newValue
        ) {
            TagTreeItem item = (TagTreeItem) getTreeItem();

            if (item != null) item.setChecked(newValue);
        }
    }
}
