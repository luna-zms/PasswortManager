package model;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("PMD.ShortClassName")
/**
 * this class is about the setting of the tag
 * @author sopr016
 */
public class Tag {
    private String name;
    private List<Tag> subTags;
    /**
     * default constructor
     * set tagname=""
     */
    public Tag() {
        this("");
    }
    /**
     * constructor sets the minimal required attributes name
     * and the subTags
     * @param name set tagname = "name"
     */
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
        return subTags.stream().filter(subtag -> name.equals(subtag.name)).findFirst().orElse(null);
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
        if (subTags.isEmpty()) return Collections.singletonMap(this, name);

        Map<Tag, String> children = subTags
                .stream()
                .flatMap(subtag -> subtag.createPathMap().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> name + "\\" + entry.getValue()));

        children.put(this, name);

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
