package model;

import java.util.*;
import java.util.stream.Collectors;

import controller.PMController;
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

    public Tag(String name) {
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("tag name is null or empty");
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

    /**
     * a.mergeWith(b) merges tag tree b into tag tree a.
     * @param tag root of the tag tree to be merged into this tree.
     * @return a Map<Tag, Tag>. An entry (s, t) in this
     * map means that s from tree b and t from tree a are duplicates, and
     * s has been merged into t.
     */
    public Map<Tag, Tag> mergeWith(Tag tag) {
    	Map<Tag, Tag> unify = new HashMap<>();
        mergeWith(tag, unify);
        return unify;
    }
    
    private void mergeWith(Tag tag, Map<Tag, Tag> unify) {
    	tag.subTags.forEach(subtag -> {
    		Tag existing = getSubTagByName(subtag.getName());
    		
    		if (existing != null) {
    			existing.mergeWith(subtag, unify);
    			unify.put(subtag, existing);
    		} else {
    			subTags.add(subtag);
    		}
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
        return name.getValue().equals(tag.name.getValue()) &&
                subTags.equals(tag.subTags);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name.getValue() + '\'' +
                ", subTags=" + subTags +
                '}';
    }
}
