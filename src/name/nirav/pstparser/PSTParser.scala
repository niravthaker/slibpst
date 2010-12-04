package name.nirav.pstparser
import java.io._
import Implicits._
import Constants._
import name.nirav.pstparser.model._
import name.nirav.pstparser.enums._

class PSTHeaderParser(file: File) {
    val raf     = new RandomAccessFile(file, "r")

    def this(fileName: String){
    	this(new File(fileName))
    }
    
    implicit def HEtoLE(ra: RandomAccessFile) = new RAFImplicit(ra)

    def list = {
        val header = parseHeader
        if(header.version == PSTFormat.ANSI)
            readAnsi(header)
        else
            readUnicode(header)
        
    }
    def readAnsi(header: PSTHeader) {
        val root = header.pstHeaderExtention.asInstanceOf[PSTHeaderExtension[Int]].root
        val bid = root.brefBBT.bid.value
        val ib = root.brefBBT.ib 
        raf.seek(ib)
    }
    def readUnicode(header: PSTHeader) {
        val root = header.pstHeaderExtention.asInstanceOf[PSTHeaderExtension[Long]].root
        val bid = root.brefBBT.bid.value
        val ib = root.brefBBT.ib
        raf.seek(ib)
    }
    def firstAMap = {
    	val header = parseHeader
    	raf seek AMAP_FILE_OFFSET
    	val pageData = new Array[Byte](512)
    	raf.read(pageData)
    	AMapPageParser.parse(pageData, header.version)
    }
    
    def parseHeader() : PSTHeader = {
    	raf seek 0
        var header                 = new PSTHeader
        header.magic               = raf.readIntLE
        header.crc                 = raf.readIntLE
        header.magicClient         = raf.readShortLE
        header.version             = PSTFormat.fromShort(raf.readShortLE)
        header.clientVersion       = raf.readShortLE
        header.platformCreate      = raf.readByte
        header.platformAccess      = raf.readByte
        /*header.reserved1         = */raf.skipBytes(4)
        /*header.reserved2         = */raf.skipBytes(4)

        
        if(header.version == PSTFormat.ANSI ){
            continueAnsiParsing(raf, header)
        }else{
            continueUnicodeParsing(raf, header)
        }
        header
    }
    
    def continueAnsiParsing(raf: RandomAccessFile, header: PSTHeader){
        val aext =  new AnsiPSTHeaderExtension
        header.pstHeaderExtention  = aext
        aext.nextPageBID           = raf.readIntLE
        aext.nextBID               = raf.readIntLE
        header.seedValue           = raf.readIntLE
        header.nids                = Array.tabulate(header.nids.length){nid => new PSTNode(raf.readIntLE)}
        aext.root                  = parseRoot(raf, intReader)
        raf.skipBytes(128) // Deprecated FMap
        raf.skipBytes(128) // Deprecated FPMap
        header.bSentinel      = raf.readByte
        header.bCryptMethod   = raf.readByte
        raf.skipBytes(2) // Ignored, reserved short
        raf.skipBytes(8) // ullReserved
        raf.skipBytes(4) // dwReserved
        raf.skipBytes(3) //3 bytes ignored, rgbReserved2
        raf.skipBytes(1) //ignored bReserved
        raf.skipBytes(32) //Ignored rgbReserved3
        
    }
    
    def continueUnicodeParsing(raf: RandomAccessFile, header: PSTHeader){
        var uext = new UnicodePSTHeaderExtension
        header.pstHeaderExtention  = uext 
        raf.skipBytes(8)//unused padding
        uext.nextPageBID = raf.readLongLE
        uext.nextBID     = raf.readLongLE
        header.seedValue = raf.readIntLE
        header.nids.foreach( nid => new PSTNode(raf.readIntLE) )
        raf.skipBytes(8) // unused space.
        uext.root = parseRoot(raf, longReader)
        raf.skipBytes(4) //ignored aligned bytes
        raf.skipBytes(128) // Deprecated FMap
        raf.skipBytes(128) // Deprecated FPMap
        header.bSentinel      = raf.readByte
        header.bCryptMethod   = raf.readByte
        raf.skipBytes(2) // Ignored, reserved short
        uext.bidNextB  = raf.readLongLE
        uext.crcFull   = raf.readIntLE
        raf.skipBytes(3) //3 bytes ignored, rgbReserved2
        raf.skipBytes(1) //ignored bReserved
        raf.skipBytes(32) //Ignored rgbReserved3
    }
    
    
    def intReader(raf: RandomAccessFile) = raf.readIntLE
    def longReader(raf: RandomAccessFile) = raf.readLongLE
    
    def parseRoot[A <: AnyVal](raf: RandomAccessFile, f : RandomAccessFile => A) : PSTRoot[A] =  {
        raf.skipBytes(4) //ignored reserved space
        val root = new PSTRoot[A]
        root.pstSize   = f(raf)
        root.lastAMap  = f(raf)
        root.aMapFree  = f(raf)
        root.pMapFree  = f(raf)
        root.brefNBT   = parseBRef[A](raf, f)
        root.brefBBT   = parseBRef[A](raf, f)
        root.amapValid = raf.readByte
        raf.skipBytes(1) //Ignored reserved byte
        raf.skipBytes(2) // ignored reserved short
        root
    } 
    
    def parseBRef[A <: AnyVal](raf: RandomAccessFile, f: RandomAccessFile => A) = {
        var bref = new BRef[A]
        bref.bid = new BlockID(f(raf)); bref.ib = f(raf)
        bref
    }

}

class PSTPageParser{
	
}
