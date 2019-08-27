package model;

import java.util.*;
import java.util.stream.Collectors;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@SuppressWarnings("PMD.ShortClassName")
public class Tag {
    private StringProperty name = new SimpleStringProperty();
    private ObservableList<Tag> subTags = FXCollections.observableArrayList(
            t -> new Observable[] { t.nameProperty(), t.subTagsObservable() }
    );

    public Tag() {
        this("");
    }

    public Tag(String name) {
        this.name.setValue(name);
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public List<Tag> getSubTags() {
        return subTags;
    }

    public ObservableList<Tag> subTagsObservable() {
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
        return subTags.stream().filter(subtag -> name.equals(subtag.getName())).findFirst().orElse(null);
    }

    public boolean hasSubTag(String name) {
        return getSubTagByName(name) != null;
    }

    public Map<Tag, String> createPathMap() {
        if (subTags.isEmpty()) return Collections.singletonMap(this, getName());

        Map<Tag, String> children = subTags
                .stream()
                .flatMap(subtag -> subtag.createPathMap().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> getName() + "\\" + entry.getValue()));

        children.put(this, getName());

        return children;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tag tag = (Tag) obj;
        return name.equals(tag.name) &&
                subTags.equals(tag.subTags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, subTags);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                ", subTags=" + subTags +
                '}';
    }
}
