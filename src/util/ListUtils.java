package util;

import java.util.List;

import model.Tag;

public class ListUtils {
	
	public static <A> boolean compare (List<A> listA, List<A> listB) {
		// Maths, bitch
		return listA.containsAll(listB) && listB.containsAll(listA);
	}
	
    public static boolean tagsEquals(List<Tag> l1, List<Tag> l2)  {
        outer: for (Tag t1: l1) {
            for(Tag t2: l2) {
                if(ListUtils.tagEquals(t1, t2))
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
