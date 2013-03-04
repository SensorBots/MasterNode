package src.guiVersion_6;

public class Package {

	public final int botID;
	public final int packetParent;
	public final int packetTimeSlot;
	public final int packetSubTree;
	
	public Package(int botID, int packetParent, int packetTimeSlot, int packetSubTree) {
		this.botID = botID;
		this.packetParent = packetParent;
		this.packetTimeSlot = packetTimeSlot;
		this.packetSubTree = packetSubTree;
		
		System.out.println("Package Class -> botID: " +this.botID+ " pcktParent: " +this.packetParent+ 
				" pcktTimeSlot: " + this.packetTimeSlot + " pcktSubTree: " + this.packetSubTree);
	}
}
