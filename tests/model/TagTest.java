package model;
import static org.junit.Assert.*;
import java.util.List;
import java.util.Map;
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
	 * test mergeWith() 
	 * tests by adding two Maps and two Tags
	 */
	@Test
	public void testMergeWith() throws Exception{
		Tag rootOne = new Tag();
		final Tag rootTwo = new Tag();

		Map<Tag, String> mapOne = rootOne.createPathMap();
		Map<Tag, String> mapTwo = rootTwo.createPathMap();
		
		rootOne.mergeWith(rootTwo);
		
		Map<Tag, String> mapCombined = rootOne.createPathMap();
		
		for (String path : mapOne.values()) {
			assertTrue(mapCombined.containsValue(path));
		}
		
		for (String path : mapTwo.values()) {
			assertTrue(mapCombined.containsValue(path));
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
	 * test createPathMap
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
