package view;

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
            if (mode == TreeMode.EDIT)
                return new TagTreeEditCell();
            return new CheckBoxTreeCell<>();
        });

        if (mode == TreeMode.CHECKBOX) {
            setRoot(new TagTreeCheckBoxItem(rootTag));
        } else {
            setEditable(true);
            setRoot(new TagTreeEditItem(rootTag));
        }
    }

    private static class TagTreeEditCell extends TextFieldTreeCell<Tag> {
        TagTreeEditCell() {
            super();

            setConverter(new StringConverter<Tag>() {
                @Override
                public String toString(Tag tag) {
                    return tag.getName();
                }

                @Override
                public Tag fromString(String s) {
                    Tag tag = getTreeItem().getValue();
                    tag.setName(s);
                    return tag;
                }
            });

            ContextMenu menu = new ContextMenu();
            MenuItem edit = new MenuItem("Edit");
            MenuItem delete = new MenuItem("Delete");

            edit.setOnAction(event -> startEdit());

            delete.setOnAction(event -> {
                TreeItem<Tag> current = getTreeItem();
                TreeItem<Tag> parent = getTreeItem().getParent();

                if (parent != null) {
                    parent.getValue().getSubTags().remove(current.getValue());
                    parent.getChildren().remove(current);
                } else {
                    getTreeView().setRoot(null);
                }
                getTreeView().refresh();
            });

            menu.getItems().addAll(edit, delete);

            setContextMenu(menu);
        }
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
