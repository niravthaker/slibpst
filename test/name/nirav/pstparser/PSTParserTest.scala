package name.nirav.pstparser
import name.nirav.pstparser.model._
import name.nirav.pstparser.enums._
import org.junit._
import Assert._


class PSTHeaderParserTest {
    @Test def testParseHeader(){
        var p = new PSTHeaderParser("/media/Development_/NiravNameSpace/Profession/CSAM/archive/archive.pst")
        val header = p.parseHeader
        assertNotNull(header)
        assertEquals(Integer.valueOf("4E444221",16), header.magic)
        assertEquals(Integer.valueOf("4D53",16), header.magicClient)
        assertEquals(PSTFormat.ANSI, header.version)
        assertEquals(19, header.clientVersion )
        assertEquals(0x01, header.platformCreate)
        assertEquals(0x01, header.platformAccess)
        assertNotNull(header.pstHeaderExtention.nextPageBID)
        assertNotNull(header.pstHeaderExtention.nextBID)
        assertNotNull(header.seedValue)
        assertEquals(32,header.nids.length)
        assertEquals(0x80, header.bSentinel & 0xff) //to work around java, convert to unsigned int
        assertTrue(List(0x00, 0x01, 0x02).contains(header.bCryptMethod))
        var root = header.pstHeaderExtention.root.asInstanceOf[PSTRoot[Int]]
        assertEquals(46744576, root.pstSize) 
        assertNotNull(root.aMapFree)
        assertNotNull(root.pMapFree)
        assertTrue(root.allAMapsValid)
        checkBRef(root.brefBBT)
        checkBRef(root.brefNBT)
    }
    
    def checkBRef[A <: AnyVal](bref: BRef[A]){
        assertNotNull(bref)
        assertNotNull(bref.bid)
        assertNotNull(bref.ib)
    }
    
}