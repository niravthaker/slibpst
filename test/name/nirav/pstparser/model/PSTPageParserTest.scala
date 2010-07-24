package name.nirav.pstparser.model
import name.nirav.pstparser.model._
import org.junit._
import Assert._

class PSTPageParserTest {
    @Test def pageBitMap{
    	val pm = new AMapPage
    	pm.reserve(10)
    	assertTrue(pm.rgb.get(10))
    	pm.free(10)
   		assertFalse(pm.rgb.get(10))
    }
     
	@Test def pageMapTest{
    	val pm = new PageMap
    	pm.reserve(10)
    	for(i <- 10 to 18)
    		assertTrue(pm.rgb.get(i))
    	pm.free(10)
    	for(i <- 10 to 18)
    		assertFalse(pm.rgb.get(i))
    }
    
    @Test def testDListPage{
    	val entry = new DListPageEntry(0x0EFFF100)
    	assertEquals(0xEFFF, entry.getAMapPageNum)
    	assertEquals(0x0100, entry.getAMapFreeSlots)
    }
   
}