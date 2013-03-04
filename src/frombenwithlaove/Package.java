package frombenwithlaove;

public class Package {

	public final int botID;
	public final int packetParent;
	public final int packetTimeSlot;
	
	public Package(int botID, int packetParent, int packetTimeSlot) {
		this.botID = botID;
		this.packetParent = packetParent;
		this.packetTimeSlot = packetTimeSlot;
	}
}
