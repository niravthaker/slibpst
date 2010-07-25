package name.nirav.pstparser

object Utils {
	import java.io.RandomAccessFile;
	def loadFile(str: String) = {
    	val f = Thread.currentThread.getContextClassLoader.getResource(str)
    	new RandomAccessFile(f.getPath,"r")
    }
    
}