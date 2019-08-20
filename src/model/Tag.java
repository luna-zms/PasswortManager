package model;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD.ShortClassName")
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

    public boolean hasSubTag(String name) {
        return subTags.stream().anyMatch(subtag -> subtag.name.equals(name));
    }
}
