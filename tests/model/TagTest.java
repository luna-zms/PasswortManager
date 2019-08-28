package model;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

/**
 * this class is to test class Tag
 * @author sopr016
 *
 */
public class TagTest {
	
	private Tag rootTag,tagAa,tagAb,tagAc,tagBa,tagBb,tagBc,tagCa,tagCb,tagCc;
	private List<Tag> subTags;
	
	/**
	 * Sets up the Test object before each test run. so there are a set of tags and subtags 
	 * that can be directly used in the later testmethods.
	 */
	@Before
	public void setUp(){
		rootTag = new Tag("Tag");
		tagAa =new Tag("TagAa");
		tagAb =new Tag("TagAb");
		tagAc =new Tag("TagAc");
		tagBa =new Tag("TagBa");
		tagBb =new Tag("TagBb");
		tagBc =new Tag("TagBc");
		tagCa = new Tag("TagCa");
		tagCb = new Tag("TagCb");
		tagCc = new Tag("TagCc");
		subTags= rootTag.getSubTags();
		subTags.add(tagAa);
		subTags.add(tagAb);
		subTags.add(tagAc);
		subTags =tagAb.getSubTags();
		subTags.add(tagBa);
		subTags.add(tagBb);
		subTags.add(tagBc);
		subTags = tagBc.getSubTags();
		subTags.add(tagCa);
		subTags.add(tagCb);
		subTags.add(tagCc);
	}
	/**
	 * test mergeWith() by adding two rootTags with subtags
	 * tests if these two rootTags can be keerect merged or not
	 */

	@Test
	public void testMergeWith() throws Exception{
		Tag rootOne = new Tag("tagOne");
		Tag rootTwo = new Tag("tagTwo");
		tagAa =new Tag("TagAa");
		tagAb =new Tag("TagAb");
	    tagAc =new Tag("TagAc");
	    tagBa =new Tag("TagBa");
	    tagBb =new Tag("TagBb");
		tagBc =new Tag("TagBc");
		subTags = rootOne.getSubTags();
		subTags.add(tagAa);
		subTags.add(tagAb);
		subTags.add(tagAc);
		subTags = rootTwo.getSubTags();
		subTags.add(tagAa);
		subTags.add(tagBa);
		subTags.add(tagBb);
		subTags.add(tagBc);
			
		Map<Tag, String> mapOne = rootOne.createPathMap();
		mapOne.replaceAll((Tag tag, String str) -> Arrays.stream(str.split("\\\\")).skip(1).collect(Collectors.joining("")));
		Map<Tag, String> mapTwo = rootTwo.createPathMap();
		mapTwo.replaceAll((Tag tag, String str) -> Arrays.stream(str.split("\\\\")).skip(1).collect(Collectors.joining("")));
	
		rootOne.mergeWith(rootTwo);
		
		Map<Tag, String> mapCombinedOne = rootOne.createPathMap();
		mapCombinedOne.replaceAll((Tag tag, String str) -> Arrays.stream(str.split("\\\\")).skip(1).collect(Collectors.joining("")));
		
		for (String path : mapOne.values()) {
			assertTrue(mapCombinedOne.containsValue(path));
		}
		
		for (Map.Entry<Tag, String> entry : mapTwo.entrySet()) {
			if (entry.getKey() != rootTwo) {
				assertTrue(mapCombinedOne.containsValue(entry.getValue()));
			}
		}  
	}
	/**
	 * test getSubTagByName()
	 * tests whether the tag has a subtag ,that with the correspond name
	 */
	@Test
	public void testGetSubTagByName(){
		assertEquals(tagBb, tagAb.getSubTagByName("TagBb"));
		assertEquals(tagCc, tagBc.getSubTagByName("TagCc"));
		assertEquals(tagAb, rootTag.getSubTagByName("TagAb"));
		assertTrue(tagBc.equals(tagAb.getSubTagByName("TagBc")));
		assertFalse(tagCb.equals(tagBc.getSubTagByName("TagCc")));
	}
	/**
	 * test hasSubTag()
	 * tests whether a tag has Subtag  or not
	 */
	@Test
	public void testHasSubTag(){
		assertTrue(tagAb.hasSubTag("TagBa"));
		assertFalse(tagAa.hasSubTag(" "));
		assertTrue(tagBc.hasSubTag("TagCa"));
		assertFalse(tagAc.hasSubTag(" "));
		assertFalse(tagAb.hasSubTag("TagAc"));
		assertFalse(tagCc.hasSubTag(" "));
		
	}
	/**
	 * test createPathMap()
	 * tests whether the Path in correspond map is true or not
	 */
	@Test
	public void testcCreatePathMap(){
		Map<Tag,String> map=rootTag.createPathMap();
		assertEquals("Tag\\TagAa", map.get(tagAa));
		assertTrue(map.get(tagBc).equals("Tag\\TagAb\\TagBc"));
		assertFalse(map.get(tagBb).equals("TagAa\\TagBb"));
		Map<Tag, String> mapA=tagAb.createPathMap();
		assertEquals("TagAb\\TagBa", mapA.get(tagBa));
		assertTrue(mapA.get(tagBc).equals("TagAb\\TagBc"));
		assertFalse(mapA.get(tagBb).equals("Tag\\TagBb\\TagBc"));
		Map<Tag, String>mapB = tagBc.createPathMap();
		assertFalse(mapB.get(tagCc).equals("Tag\\TagAb\\TagBc"));
	}
	
}
