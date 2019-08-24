package model;
import static org.junit.Assert.*;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
public class TagTest {
	
	private Tag rootTag,tagAa,tagAb,tagAc,tagBa,tagBb,tagBc;
	private List<Tag> subTags;
	
	@Before
	public void setUp(){
		rootTag = new Tag("Tag");
		tagAa =new Tag("TagAa");
		tagAb =new Tag("TagAb");
		tagAc =new Tag("TagAc");
		tagBa =new Tag("TagBa");
		tagBb =new Tag("TagBb");
		tagBc =new Tag("TagBc");
		subTags= rootTag.getSubTags();
		subTags.add(tagAa);
		subTags.add(tagAb);
		subTags.add(tagAc);
		subTags =tagAb.getSubTags();
		subTags.add(tagBa);
		subTags.add(tagBb);
		subTags.add(tagBc);
	}
	
	@Test
	public void testHasSubTag(){
		assertEquals(false, rootTag.hasSubTag(""));
		assertEquals(true, rootTag.hasSubTag("TagAa"));
		assertEquals(false, tagAa.hasSubTag(" "));
		assertTrue(tagAb.hasSubTag("TagBb"));
		assertFalse(rootTag.hasSubTag("TagBc"));
	}
	
	@Test
	public void testcCreatePathMap(){
		Map<Tag,String> map=rootTag.createPathMap();
		assertEquals("Tag\\TagAa", map.get(tagAa));
		assertTrue(map.get(tagBc).equals("Tag\\TagAb\\TagBc"));
		assertFalse(map.get(tagBb).equals("TagAa\\TagBb"));
		Map<Tag, String> mapA=tagAb.createPathMap();
		assertEquals("TagAb\\TagBa", mapA.get(tagBa));
		assertTrue(mapA.get(tagBc).equals("TagAb\\TagBc"));
	}
	
}
