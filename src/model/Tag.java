package model;

import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

public class Tag {
    private String name;
    private List<Tag> subTags;

    public Tag() {
        this("");
    }

    public Tag(String name) {
        this.subTags = new ArrayList<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Tag> getSubTags() {
        return subTags;
    }
}
