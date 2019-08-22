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

    public void mergeWith(Tag tag) {
        tag.subTags.forEach(subtag -> {
            Tag existing = getSubTagByName(subtag.getName());

            if (existing != null) existing.mergeWith(subtag);
            else subTags.add(subtag);
        });
    }

    public Tag getSubTagByName(String name) {
        return subTags.stream().filter(subtag -> name.equals(subtag.name)).findFirst().orElse(null);
    }

    public boolean hasSubTag(String name) {
        return getSubTagByName(name) != null;
    }

    public Map<Tag, String> createPathMap() {
        if (subTags.isEmpty()) return Collections.singletonMap(this, name);

        Map<Tag, String> children = subTags
                .stream()
                .flatMap(subtag -> subtag.createPathMap().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, e -> name + "\\" + e.getValue()));

        children.put(this, name);

        return children;
    }
}
