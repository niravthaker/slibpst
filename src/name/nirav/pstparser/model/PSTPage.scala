package name.nirav.pstparser.model

import name.nirav.pstparser.enums._


class BlockID[A <: AnyVal] (bid: A){
	
	def isInternal : Boolean = bid match{ 
        case a : Int  => (1 & (a >> 30)) == 1
        case a : Long => (1 & (a >> 62)) == 1
        case _        => throw new java.lang.IllegalArgumentException("Unexpected type of bid : " + bid)
    }
	
	def value = bid
    
	
}
class PageTrailer[A <: AnyVal]{
    var pageType       : PageType  = _
    var pageTypeRepeat : PageType  = _
    var pSign          : Short     = _
    var crc            : Int       = _
    var bid            : BlockID[A]= _
}

trait Trailer[A <: AnyVal]{
    val trailer = new PageTrailer[A]
}

trait BitMap{
    val rgb     = new java.util.BitSet(496 * 8)
    def reserve(pageNum : Int) =   rgb set   pageNum
    def free   (pageNum : Int) =   rgb clear pageNum
    def isFree (pageNum : Int) = !(rgb get   pageNum)
}

class AMapPage[A <: AnyVal] extends Trailer[A] with BitMap{
	
}
object AMapPageParser{
	import java.nio._
	
	def parse(bytes : Array[Byte], format : PSTFormat)  = {
		val bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
		format match {
			case PSTFormat.ANSI    => parseANSIPage(bb)
			case PSTFormat.UNICODE => parseUnicodePage(bb)
		}
	}
	
	def parseUnicodePage(bb : ByteBuffer) = {
		val amap = new AMapPage[Long]
        basicPageParse(bb, amap)
		amap.trailer.bid = new BlockID(bb.getLong)
		amap
	}
	
	def parseANSIPage(bb : ByteBuffer) = {
		bb.getInt // Unused padding
		val amap = new AMapPage[Int]
        basicPageParse(bb, amap)
		amap.trailer.bid = new BlockID(bb.getInt)
		amap
	}
	
	def basicPageParse[A <: AnyVal](bb : ByteBuffer, amap : AMapPage[A]) = {
		for(i <- 0 to 495){ // 496 bytes
			stripBits(amap, i, bb.get)
		}
		amap.trailer.pageType 		 = PageType.fromByte(bb.get)
		amap.trailer.pageTypeRepeat  = PageType.fromByte(bb.get)
		amap.trailer.pSign           = bb.getShort
		amap.trailer.crc             = bb.getInt
	}
	
	def stripBits[A <: AnyVal](amap: AMapPage[A], index: Int, value : Byte) = {
		val lsbMask = 0x0001
		var intVal = value * 8;
		val to     = index + 8
		for(i <- index to to){
			val firstBit = (intVal & lsbMask) == 0x1
			amap.rgb.set(i, firstBit)
			intVal = intVal >>> 1
		}
	}
}

class PageMap[A <: AnyVal] extends AMapPage{
    override def reserve(pageNum : Int) { 
        iterate8(pageNum, super.reserve)
    }
    override def free(pageNum : Int) {
        iterate8(pageNum, super.free)
    }
    override def isFree(pageNum : Int) = {
    	var i = true;
    	iterate8(pageNum, (j) => i &= super.isFree(j))
    	i
    }
    
    def iterate8(s : Int, f: (Int) => Unit){
        for(i <- s to  s + 8) f(i)
    }
}


class DListPageEntry(word: Int){
    val _20BitMask = 0xFFFFF000
    
    def getAMapPageNum   = (word &  _20BitMask) >>> 12
    def getAMapFreeSlots =  word & ~_20BitMask
    
}

class DensityListPage[A <: AnyVal] {
    var flags        : Byte = _
    var numEntries   : Byte = _
    var currentPage  : Int  = _
    var pageTrailer  : PageTrailer[A] = _
}

class UnicodeDensityListPage[Long] extends DensityListPage{
    val entries  = new Array[DListPageEntry](119)  //476 bytes    
}

class ANSIDensityListPage[Int] extends DensityListPage{
    val entries  = new Array[DListPageEntry](120) //480 bytes
}

class FreeMapPage[A <: AnyVal] extends AMapPage

class ANSIBTreePage extends Trailer[Int]

class UnicodeBTreePage extends Trailer[Long]

class BTEntry[A <: AnyVal]{
    var btKey : A = _
    var BRef  : A = _
}
class BBTEntry[A <: AnyVal]{
    var BRef       : A     = _
    var size       : Short = _
    var refCount   : Short = _
}
class NBTEntry[A <: AnyVal]{
    var nid        : A   = _
    var bidData    : A   = _
    var bidSub     : A   = _
    var nidParent  : Int = _
    
    def hasSubnode = bidSub != 0
}

class BlockTrailer[A <: AnyVal]{
	var size     : Short      = _
	var bSign    : Short      = _
	var crc      : Int        = _
	var bid      : BlockID[A] = _
}

class LeafBlockEntry[A <: AnyVal] {
    var nid     : A          = _
    var bidData : BlockID[A] = _
    var bidChild: BlockID[A] = _
}

class XBlock[ A <:  AnyVal] {
    var blockType       : Byte  = _
    var indirectionLevel: Byte  = _
    var entryCount      : Short = _
    var byteCount       : Int   = _
    var entries         : Array[A] = _
}

class HID(hid : Int){
    def hidType         = hid & 0xF8000000
    def hidIndex        = hid & 0x07FF0000
    def hidBlockIndex   = hid & 0x0000FFFF
}

class HeapNodeHeader{
    var pageMapByteOffset : Short = _
    var signature         : Byte  = _
    var clientSign        : Byte  = _
    var hidUserRoot       : Int   = _
    var rgbFillLevel      : Int   = _
}

class HeapNodeBitMapHeader{
    var hnPageMapByteOffset : Short = _
    var rgbFillLevel                = new Array[Byte](64)
}

class HeapNodePageMap{
    var allocationCount  : Short = _
    var freeCount        : Short = _
    var allocationTabl   : Array[Int] = _
}

class BTreeHeapHeader{
    var bType           : Byte = _
    var sizeKey         : Byte = _
    var sizeData        : Byte = _
    var indexDepth      : Byte = _
    var hid             : HID  = _
    
}