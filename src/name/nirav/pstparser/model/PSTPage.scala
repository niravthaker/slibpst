package name.nirav.pstparser.model


class PageTrailer[A <: AnyVal]{
    var pageType       : Byte  = _
    var pageTypeRepeat : Byte  = _
    var pSign          : Short = _
    var crc            : Int   = _
    var bid            : A       = _
}

trait Trailer[A <: AnyVal]{
    val trailer = new PageTrailer[A]
}

trait BitMap{
    val rgb     = new java.util.BitSet(496 * 8)
    def reserve(pageNum : Int) = rgb set   pageNum
    def free   (pageNum : Int) = rgb clear pageNum
}

class AMapPage[A <: AnyVal] extends Trailer[A] with BitMap

class PageMap[A <: AnyVal] extends AMapPage{
    override def reserve(pageNum : Int) { 
        iterate8(pageNum, super.reserve)
    }
    override def free   (pageNum : Int) {
        iterate8(pageNum, super.free)
    }
    def iterate8(s : Int, f: (Int) => Unit){
        val t = s + 8
        for(i <- s to t) f(i)
    }
}


class DListPageEntry(word: Int){
    val _20BitMask = 0xFFFFF000
    
    def getAMapPageNum   = word &  _20BitMask
    def getAMapFreeSlots = word & ~_20BitMask
    
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
