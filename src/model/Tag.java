package model;

import java.util.*;
import java.util.stream.Collectors;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * this class is about the setting of the tag
 * @author sopr016
 */
@SuppressWarnings("PMD.ShortClassName")
public class Tag {
    private StringProperty name = new SimpleStringProperty();
    private ObservableList<Tag> subTags = FXCollections.observableArrayList(
            t -> new Observable[] { t.nameProperty(), t.subTagsObservable() }
    );

    public Tag() {
        this("");
    }
    /**
     * constructor sets the minimal required attributes name
     * and the subTags
     * @param name set tagname = "name"
     */
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
     * All subtags of the given tag will be check ,if it already exists in the current one.
     * if so,we merge recursively with the existing ones,otherwise we reinsert the Tag.
     * @param tag the given tag
     */
    public void mergeWith(Tag tag) {
        tag.subTags.forEach(subtag -> {
            Tag existing = getSubTagByName(subtag.getName());

            if (existing != null) existing.mergeWith(subtag);
            else subTags.add(subtag);
        });
    }

   /**
    * this method will return a tag,that has the correspond subtag's name
    * @param name 
    * @return Tag with Name name
    */
    public Tag getSubTagByName(String name) {
        return subTags.stream().filter(subtag -> name.equals(subtag.getName())).findFirst().orElse(null);
    }

    /**
     * this method is to check,if the tag with the correspond name has subtag or not
     * @param name 
     * @return boolean indicates if there is a subtag with Name name
     */
    public boolean hasSubTag(String name) {
        return getSubTagByName(name) != null;
    }

    /**
     * this method will create a path
     * @return Map<Tag ,String> which by a given Tag returns its path
     */
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
    public String toString() {
        return "Tag{" +
                "name='" + name.getValue() + '\'' +
                ", subTags=" + subTags +
                '}';
    }
}
