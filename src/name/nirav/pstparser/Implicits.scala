package name.nirav.pstparser

object Implicits {
	class RAFImplicit(raf: java.io.RandomAccessFile){
		def readShortLE() : Short  = java.lang.Short.reverseBytes(raf.readShort)
		def readIntLE()   : Int    = java.lang.Integer.reverseBytes(raf.readInt)
		def readLongLE()  : Long   = java.lang.Long.reverseBytes(raf.readLong)
	}

}