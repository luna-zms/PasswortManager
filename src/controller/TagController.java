package controller;

import model.Entry;
import model.Tag;

/**
 * @author sopr017
 */
public class TagController {

    protected PMController pmController;

    /**
     * Adds the given child to the parent as a subTag.
     *
     * @param parent The tag that child is supposed to be added to.
     * @param child  The tag that is supposed to be added.
     */
    public void addTag(Tag parent, Tag child) {
        if (child == null) {
            throw new NullPointerException();
        }
        parent.getSubTags().add(child);
    }

    /**
     * Removes the given child from subTagList in parent and from every Entry.
     *
     * @param parent The tag where child is supposed to be removed from.
     * @param child  The tag that is supposed to be removed.
     */
    public void removeTag(Tag parent, Tag child) {
        if (child == null) {
            throw new NullPointerException();
        }
        parent.getSubTags().remove(child);
        for (Entry entry : pmController.getPasswordManager().getEntries()) {
            entry.getTags().remove(child);
        }
    }

    /**
     * Changes the name of the given Tag.
     *
     * @param tag    The tag that is supposed to be renamed.
     * @param rename The new name that is supposed to be set.
     */
    public void renameTag(Tag tag, String rename) {
        if (rename == null) {
            throw new NullPointerException();
        }
        tag.setName(rename);
    }

    public void setPMController(PMController controller) {
        this.pmController = controller;
    }

}
