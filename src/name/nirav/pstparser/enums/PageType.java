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
}
