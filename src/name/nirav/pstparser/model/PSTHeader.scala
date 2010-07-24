package name.nirav.pstparser.model

import name.nirav.pstparser.enums._


class PSTHeader{
	var magic 			: Int = _ 
	var crc	  			: Int = _
	var magicClient 	: Int = _
	var version 		: PSTFormat  = _ 
	var clientVersion 	: Short = _
	var platformCreate 	: Byte = _
	var platformAccess 	: Byte = _
	var seedValue		: Int = _
	var bSentinel		: Byte = _
	var bCryptMethod	: Byte = _
	var nids 			  	   = new Array[PSTNode](32)
	var pstHeaderExtention: PSTHeaderExtension[_] = _
}


class PSTNode(field: Int){
	def nodeType =  (field & 0xF8000000 )  >> 27 
	
	def nodeId = field & 0x07FFFFFF
	
	override def toString =  "Type: " + nodeType.toHexString + " => Index : " + nodeId.toHexString  
}
class PSTHeaderExtension[A <: AnyVal]{
	var nextPageBID	: A = _
	var nextBID		: A = _
	var root		: PSTRoot[A] = _
}

class UnicodePSTHeaderExtension extends PSTHeaderExtension[Long]{
	var bidNextB : Long = _
	var crcFull  : Int  = _
}

class AnsiPSTHeaderExtension extends PSTHeaderExtension[Int]{
	
}

class PSTRoot[A <: AnyVal]{
	var pstSize 	: A = _
	var lastAMap 	: A = _
	var aMapFree	: A	= _
	var pMapFree	: A = _
	var brefNBT		: BRef[A] = _
	var brefBBT		: BRef[A] = _
	var amapValid	: Byte	  = _
	
	def allAMapsValid : Boolean = amapValid match {
		case 0x01 => return true; 
		case 0x02 => return true;
		case _	  => return false;
	}
	
}


class BRef[A <: AnyVal]{
	var bid : A = _
	var ib	: A  = _
	
	def isInternal : Boolean = bid match{ 
		case a : Int  => (1 & (a >> 30)) == 1
		case a : Long => (1 & (a >> 62)) == 1
		case _ => throw new java.lang.IllegalArgumentException("Unexpected type of bid : " + bid)
	}
	
	def bidIndex : A = { return bid /*TODO: Return appropriate val*/}
	def byteIndex : A = { return ib }
}

