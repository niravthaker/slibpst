package name.nirav.pstparser.utils
import org.junit._
import Assert._
import name.nirav.pstparser.utils.BitOps._

class BitOpsTest {
	@Test def testBitAt(){
		assertEquals(true, bitAt(0x02, 1))
		assertEquals(true, bitAt(0x01, 0))
		assertEquals(false, bitAt(0x01, 1))
	}
}