package name.nirav.pstparser.enums;

public enum PSTFormat {
	ANSI, UNICODE;
	public static PSTFormat fromShort(short i) {
		switch (i) {
		case 0x000E:
		case 0x000F:
			return PSTFormat.ANSI;
		case 0x0015:
		case 0x0017:
			return PSTFormat.UNICODE;
		default:
			throw new java.lang.IllegalArgumentException("Illegal PST format : " + i);
		}
	}
}
