package name.nirav.pstparser

object Utils {
	import java.io.{RandomAccessFile, File}
	
	def getResource(str: String) = Thread.currentThread.getContextClassLoader.getResource(str)

	def loadRandomAccessFile(str: String) = new RandomAccessFile(getResource(str).getPath,"r")
	
	def loadFile(str: String) = new File(getResource(str).getFile)

	def hexDump(){
	    
	    
	}
    
}