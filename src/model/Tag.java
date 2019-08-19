package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Map<Tag, String> createPathMap() {
        if (subTags.isEmpty()) return Collections.singletonMap(this, name);

        return subTags
                .stream()
                .flatMap(subtag -> subtag.createPathMap().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, e -> name + "\\" + e.getValue()));
    }
}
