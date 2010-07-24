package name.nirav.pstparser.utils

object BitOps {
	
	def bitAt(a : Int, pos : Int) : Boolean = {
		val b = (1 << pos)
		val c = (a & b)
		return c != 0
	}
}