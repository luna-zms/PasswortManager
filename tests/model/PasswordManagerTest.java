package model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PasswordManagerTest {
	
	private PasswordManager test;
	
	@Before
	public void setup() {
		test = new PasswordManager();
	}
	
	private boolean tagExistsInTree(Tag current, Tag toFind) {
		if (current == toFind) {
			return true;
		} else {
			return current.getSubTags().stream().anyMatch(t -> tagExistsInTree(t, toFind));
		}
	}
	
	/**
	 * Tests unification of tag references in entries
	 */
	@Test
	public void testMergeWith() {
		Tag rootOne = new Tag("rootOne");
		Tag rootTwo = new Tag("rootOne");
		
		Tag subOne = new Tag("subOne");
		Tag subTwo = new Tag("subOne");
		Tag subsubOne = new Tag("subsubOne");
		Tag subsubTwo = new Tag("subsubOne");
		
		rootOne.getSubTags().add(subOne);
		subOne.getSubTags().add(subsubOne);
		
		rootTwo.getSubTags().add(subTwo);
		subTwo.getSubTags().add(subsubTwo);
		
		List<Entry> entriesOne = new ArrayList<Entry>();
		Entry a = new Entry("a", "");
		a.getTags().add(rootOne);
		Entry b = new Entry("b", "");
		b.getTags().add(subsubOne);
		entriesOne.add(a);
		entriesOne.add(b);
		
		List<Entry> entriesTwo = new ArrayList<Entry>();
		Entry c = new Entry("c", "");
		c.getTags().add(rootTwo);
		Entry d = new Entry("d", "");
		d.getTags().add(subsubTwo);
		entriesTwo.add(c);
		entriesTwo.add(d);
		
		test.setRootTag(rootOne);
		test.setEntries(entriesOne);
		test.mergeWith(entriesTwo, rootTwo);
		
		for (Entry e : test.getEntries()) {
			System.out.println(e);
			for (Tag t : e.getTags()) {
				System.out.println(t);
				assertTrue(tagExistsInTree(test.getRootTag(), t));
			}
		}
	}
}
