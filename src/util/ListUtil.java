package util;

import java.util.List;

import model.Tag;

public class ListUtil {

	public static <A> boolean compare (List<A> listA, List<A> listB) {
		// Maths, bitch
		return listA.containsAll(listB) && listB.containsAll(listA);
	}

    public static boolean tagsEquals(List<Tag> listA, List<Tag> listB)  {
        outer: for (Tag t1: listA) {
            for(Tag t2: listB) {
                if(ListUtil.tagEquals(t1, t2))
                    continue outer;
            }
            return false;
        }
        return true;
    }

    public static boolean tagEquals(Tag tag1, Tag tag2) {
        if(tag1 == tag2) return true;
        return tag1.getName().equals(tag2.getName()) && tagsEquals(tag1.getSubTags(), tag2.getSubTags());
    }
}
