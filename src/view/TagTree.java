package view;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;
import model.Tag;

public class TagTree extends TreeView<Tag> {
    public enum TreeMode {
        EDIT,
        CHECKBOX
    }

    public void init(final TreeMode mode, Tag rootTag) {
        setCellFactory(treeView -> {
            if (mode == TreeMode.EDIT) {
                TextFieldTreeCell<Tag> c = new TextFieldTreeCell<>();
                c.setConverter(converterFor(c));
                c.setContextMenu(createContextMenu());
                return c;
            }
            else {
                CheckBoxTreeCell<Tag> c = new CheckBoxTreeCell<>();
                c.setContextMenu(createContextMenu());
                return c;
            }
        });

        if (mode == TreeMode.CHECKBOX) {
            setRoot(new TagTreeCheckBoxItem(rootTag));
        } else {
            setEditable(true);
            setRoot(new TagTreeEditItem(rootTag));
        }
    }

    private TreeItem<Tag> getSelectedItem() {
        return getSelectionModel().getSelectedItem();
    }

    public Tag getSelected() {
        return getSelectedItem().getValue();
    }

    public void deleteSelected() {
        TreeItem<Tag> selected = getSelectedItem();
        TreeItem<Tag> parent = selected.getParent();

        if (parent != null) {  // Cannot delete root node
            // TODO: Once we do Controllers, this needs to be moved there (to some removeTag(Tag tag) method that also goes through all entries
            parent.getValue().getSubTags().remove(selected.getValue());
            parent.getChildren().remove(selected);
        }

        refresh();
    }

    public void editSelected() {
        this.edit(getSelectedItem());
    }

    private ContextMenu createContextMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem edit = new MenuItem("Bearbeiten");
        MenuItem delete = new MenuItem("Löschen");

        edit.setOnAction(event -> editSelected());
        delete.setOnAction(event -> deleteSelected());

        menu.getItems().addAll(edit, delete);

        return menu;
    }

    private StringConverter<Tag> converterFor(final TreeCell<Tag> cell) {
        return new StringConverter<Tag>() {
            @Override
            public String toString(Tag tag) {
                return tag.getName();
            }

            @Override
            public Tag fromString(String str) {
                Tag tag = cell.getTreeItem().getValue();
                tag.setName(str);
                return tag;
            }
        };
    }

    private static class TagTreeCheckBoxItem extends CheckBoxTreeItem<Tag> {
        TagTreeCheckBoxItem(Tag tag) {
            super(tag);

            setIndependent(true);
            setExpanded(true);

            tag.getSubTags().forEach(subtag -> getChildren().add(new TagTreeCheckBoxItem(subtag)));
        }
    }

    private static class TagTreeEditItem extends TreeItem<Tag> {
        TagTreeEditItem(Tag tag) {
            super(tag);

            setExpanded(true);

            tag.getSubTags().forEach(subtag -> getChildren().add(new TagTreeEditItem(subtag)));
        }
    }
}
