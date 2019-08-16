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
        setCellFactory(treeview -> {
            switch (mode) {
                case EDIT:
                    return new TagTreeEditCell();
                //case CHECKBOX:
                default:
                    return new CheckBoxTreeCell<>();
            }
        });

        if (mode == TreeMode.CHECKBOX) {
            setRoot(new TagTreeCheckBoxItem(rootTag));
        } else {
            setEditable(true);
            setRoot(new TagTreeEditItem(rootTag));
        }
    }

    private class TagTreeEditCell extends TextFieldTreeCell<Tag> {
        //private Tag tag;

        public TagTreeEditCell() {
            // FUCK YOU JAVA
            super();
            setConverter(new StringConverter<Tag>() {
                @Override
                public String toString(Tag tag) {
                    return tag.getName();
                }

                @Override
                public Tag fromString(String s) {
                    getTreeItem().getValue().setName(s);
                    return getTreeItem().getValue();
                }
            });

            ContextMenu menu = new ContextMenu();
            MenuItem edit = new MenuItem("Edit");
            MenuItem delete = new MenuItem("Delete");

            edit.setOnAction(event -> this.startEdit());

            delete.setOnAction(event -> {
                if (getTreeItem().getParent() != null) {
                    getTreeItem().getParent().getValue().getSubTags().remove(getTreeItem().getValue());
                    getTreeItem().getParent().getChildren().remove(getTreeItem());
                } else {
                    getTreeView().setRoot(null);
                }
                getTreeView().refresh();
            });

            menu.getItems().addAll(edit, delete);

            setContextMenu(menu);
        }
    }

    private class TagTreeCheckBoxItem extends CheckBoxTreeItem<Tag> {
        public TagTreeCheckBoxItem(Tag tag) {
            super(tag);
            setIndependent(true);
            setExpanded(true);

            for (Tag subtag : tag.getSubTags()) {
                this.getChildren().add(new TagTreeCheckBoxItem(subtag));
            }
        }
    }

    private class TagTreeEditItem extends TreeItem<Tag> {
        public TagTreeEditItem(Tag tag) {
            super(tag);
            setExpanded(true);

            for (Tag subtag : tag.getSubTags()) {
                this.getChildren().add(new TagTreeEditItem(subtag));
            }
        }
    }
}
