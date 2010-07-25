package name.nirav.pstparser.enums;

public enum PageType {
	BlockBTree(0x80), 
	NodeBTree(0x81), 
	FreeMap(0x82), 
	PageMap(0x83), 
	AllocationMap(0x84), 
	FreePageMap(0x85), 
	DensityList(0x86);
	
	private final int id;
	PageType(int id){
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public static PageType fromByte(byte b){
		int ub = b & 0xff;
		for (PageType t : values()) {
	        if(t.id == ub)
	        	return t;
        }
		throw new IllegalArgumentException("No such page type: " + b);
	}
}
