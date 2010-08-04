package name.nirav.pstparser.model
import name.nirav.pstparser.model._
import name.nirav.pstparser.enums._
import name.nirav.pstparser._
import org.junit._
import Assert._
import java.io._


class PSTPageParserTest {
    @Test def pageBitMap{
    	val pm = new AMapPage
    	pm.reserve(10)
    	assertFalse(pm.isFree(10))
    	pm.free(10)
   		assertTrue(pm.isFree(10))
    }
     
	@Test def pageMapTest{
    	val pm = new PageMap
    	pm.reserve(10)
    	(10 to 18) foreach { i=> assertTrue(pm.rgb.get(i)) }
    	assertFalse(pm.isFree(10))
    	pm.free(10)
    	(10 to 18) foreach { i => assertFalse(pm.rgb.get(i))}
    	assertTrue(pm.isFree(10))
    }
    
    @Test def testDListPage{
    	val entry = new DListPageEntry(0x0EFFF100)
    	assertEquals(0xEFFF, entry.getAMapPageNum)
    	assertEquals(0x0100, entry.getAMapFreeSlots)
    }
    
    @Test def testAMapPageParse{
    	val raf = Utils.loadRandomAccessFile("archive.pst")
    	val bytes = new Array[Byte](512)
    	raf.seek(Constants.AMAP_FILE_OFFSET)
    	raf.read(bytes)
    	val page = AMapPageParser.parse(bytes, PSTFormat.ANSI)
    	assertEquals(PageType.AllocationMap, page.trailer.pageType)
    	assertEquals(PageType.AllocationMap, page.trailer.pageTypeRepeat)
    	assertNotNull(page.trailer.pSign)
    	assertNotNull(page.trailer.crc)
    	assertNotNull(page.trailer.bid)

    }
   
}