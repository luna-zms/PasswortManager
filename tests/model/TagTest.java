package model;
import static org.junit.Assert.*;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

/**
 * class to test Tag
 * @author sopr016
 *
 */
public class TagTest {
	
	private Tag rootTag,tagAa,tagAb,tagAc,tagBa,tagBb,tagBc,tagCa,tagCb,tagCc;
	private List<Tag> subTags;
	
	/**
	 * Sets up the Test object before each test run.
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
	 * 
	 */
	@Test
	public void testHasSubTag(){
		assertEquals(false, rootTag.hasSubTag(""));
		assertEquals(true, rootTag.hasSubTag("TagAa"));
		assertEquals(false, tagAa.hasSubTag(" "));
		assertTrue(tagAb.hasSubTag("TagBb"));
		assertFalse(rootTag.hasSubTag("TagCc"));
		assertFalse(tagCa.hasSubTag(" "));
		assertTrue(tagAb.hasSubTag("TagCb"));
	}
	/**
	 * 
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
	
	@Test
	public void testEquals(){
		
	}
}
