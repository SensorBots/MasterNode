package guiVersion_2;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class NetworkProtocol implements Runnable{

	//passed from main GUI
	GuiMain window = null;


	private final static int MAX_NUM_PARAMS 	= 10;
	//private final static int MasterNodeId 		= 0;	// '0'
	private final static int NULL 				= -1;
	private final static int timePerInterval 	= 4;
	//private final static int SELF_NETWORK_DATA 	= 1;
	//private final static int SELF_N_CHILD_DATA 	= 2;

	public static boolean successPcktRxd 			= false;
	public static boolean myChildrenSntDataSuccess 	= false;

	public static int numNodes 			= 3;
	public static int myTimeSlotCounter = 0;
	public static int myTimeSlotMax 	= 2*(numNodes*numNodes);

	public static int myBotId 			= 0;	// '0'
	public static int myParent 			= 0;	// '1'	
	public static int myHasChild		= 0;	// '0'
	public static int myHasSeenLeaf		= 0;	// '0'
	public static int mySndFstTime		= 0;	// '1'
	public static int myNumChildren		= 0;

	public static int packetBotId 		= 0;
	public static int packetParent 		= 0;
	public static int packetTimeSlot 	= 0;

	GregorianCalendar gcalendar = new GregorianCalendar();
	private static int hrs = 0;
	private static int min = 0;
	private static int sec = 0;

	public static int[] msgToChildren 	= new int[MAX_NUM_PARAMS]; //{botID, parent, hasChild, hasSenLef, sendFirst, hrs, min, sec};
	public static int[] myChildrenList 	= new int[numNodes];
	public static boolean[] rxdFromChildren	= new boolean[numNodes];


	public NetworkProtocol(GuiMain window){
		this.window = window;
		initialization();
	}

	public void initialization(){

		for (int i = 0; i < numNodes; i++) {
			myChildrenList[i] = -1;
			rxdFromChildren[i] = false;
		}
	}

	public void run(){	
		//int slotCounter = 0;
		int timeResult = -1;

		while (true) {
			//window.logAreaText.append("Entered! " + "\n");
			
		//	if (isSuccessPcktRxd()) {//(successPcktRxd == true) { //myBotId == MasterNodeId || <- not necessary in master
				//window.logAreaText.append("Entered22 " + "\n");

				while (myTimeSlotCounter < myTimeSlotMax ) {//|| myParent == NULL <- not necessary in master
					//slotCounter++;
					//window.logAreaText.append("slotCounter: " + slotCounter + "\n");
					
					while (timeResult != 0) { 
						//do nothing
						timeResult = (int) ((System.currentTimeMillis()/1000) % timePerInterval);
						
						try {
							System.out.println("getSecs: " +(System.currentTimeMillis()/1000)+ " Result: " + timeResult);
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					

					if (myTimeSlotCounter % numNodes != myBotId ) {//|| myParent == NULL <- not necessary in master
						window.logAreaText.append("enter1 \n");
						
						if (isSuccessPcktRxd()) {//(successPcktRxd == true) {
							setSuccessPcktRxd(false); //successPcktRxd = false;
							

							if (myParent == NULL) {
								myParent = packetBotId;
							}  else if (myBotId != packetParent) {//else if (isBotInMyChildrenList(packetBotId) == false) {
								NetworkProtocol.putBotInMyChildrenList(packetBotId);
								NetworkProtocol.setRxdFromChildren(packetBotId);
							}
							myTimeSlotCounter = packetTimeSlot;
						}
					} else {
						if (myParent != NULL) {
							window.serialPortManager.writeData(myBotId, myParent, myTimeSlotCounter);
						}

						if (mySndFstTime == -1) {
							mySndFstTime = myTimeSlotCounter;
						}
					}

					if (myTimeSlotCounter == (mySndFstTime + numNodes) && myHasChild == 0) {
						myHasSeenLeaf = 1;
					}

					if (myHasSeenLeaf == 1 || myChildrenSntDataSuccess == true && (myTimeSlotCounter % numNodes) == myBotId) {
						window.serialPortManager.writeData(myBotId, myParent, myTimeSlotCounter);
					} else if (isSuccessPcktRxd()) {//(successPcktRxd == true) {
						//storeSdCard();
						setSuccessPcktRxd(false); //successPcktRxd = false;

						if (rxdFromAllChildrenTest() == true) {
							myChildrenSntDataSuccess = true;
						}
					}
					myTimeSlotCounter++;
				}
			//}

		}
	}


	public void getTime(){
		//GregorianCalendar gcalendar = new GregorianCalendar();
		hrs = gcalendar.get(Calendar.HOUR_OF_DAY);
		min = gcalendar.get(Calendar.MINUTE);
		sec = gcalendar.get(Calendar.SECOND);
		window.logAreaText.append("Time: " +hrs+ ":" +min+ ":" + sec + "\n");
	}
	
	public int getSecs(){
		return gcalendar.get(Calendar.SECOND);
	}

	private void itoa(int value, char s[])
	{
		int i = s.length; // MAX_NUM_DIGITS = 2

		do {
			s[--i] = (char) (value % 10 + '0');

		} while ( (value /= 10) > 0);

		//window.logAreaText.append("char s: " +String.valueOf(s)+ " numDigs: " +s.length+ "\n");
	}

	private char[] itoa(int value, int length)
	{
		int i = length; // MAX_NUM_DIGITS = 2
		char s[] = new char[length];

		do {
			s[--i] = (char) (value % 10 + '0');

		} while ( (value /= 10) > 0);

		//window.logAreaText.append("char s: " +String.valueOf(s)+ " numDigs: " +s.length+ "\n");
		return s;
	}

	public void createDataPacket(int botID, int parent, int timeSlot){
		getTime();

		char[] tHrs = {'0', '0'}; //Initializes the array so that it always includes a zero in case of one-digit values
		char[] tMin = {'0', '0'}; //new char[2]; //itoa(min);
		char[] tSec = {'0', '0'}; //new char[2]; //itoa(sec);
		char[] tmp  = {'0'};
		char[] tmp2 = {'0', '0'};//, '\0'};

		itoa(timeSlot, tmp2);

		itoa(hrs, tHrs);
		itoa(min, tMin);
		itoa(sec, tSec);		

		tmp = itoa(botID, 1);
		msgToChildren[0] = tmp[0];

		tmp = itoa(parent, 1);
		msgToChildren[1] = tmp[0];

		msgToChildren[2] = tmp2[0];
		msgToChildren[3] = tmp2[1];

		msgToChildren[4] = tHrs[0];
		msgToChildren[5] = tHrs[1]; 
		msgToChildren[6] = tMin[0];
		msgToChildren[7] = tMin[1];
		msgToChildren[8] = tSec[0];
		msgToChildren[9] = tSec[1];
	}

	private static void putBotInMyChildrenList(int botToInsert){
		int i = 0;
		while(i < myChildrenList.length) {
			if (myChildrenList[i] == - 1) {
				myChildrenList[i] = botToInsert;
				myNumChildren++;
				break;
			}
			i++;
		}
	}

	private static void setRxdFromChildren(int botToSet){
		if (rxdFromChildren[botToSet] == false) {
			rxdFromChildren[botToSet] = true;
		}
	}

	private boolean rxdFromAllChildrenTest(){
		int counter = 0;

		for (int i = 0; i < myNumChildren; i++) {
			if (rxdFromChildren[i] == true) {
				counter++;
			}
		}

		if (counter == myNumChildren) {
			return true;
		} else {
			return false;
		}
	}

	public int getMaxNumParams() {
		return MAX_NUM_PARAMS;
	}

	public int[] getMsgToChildren() {
		return msgToChildren;
	}

	public static int getPacketBotId() {
		return packetBotId;
	}

	public static void setPacketBotId(int packetBotId) {
		NetworkProtocol.packetBotId = packetBotId;
	}

	public static int getPacketParent() {
		return packetParent;
	}

	public static void setPacketParent(int packetParent) {
		NetworkProtocol.packetParent = packetParent;
	}

	public static int getPacketTimeSlot() {
		return packetTimeSlot;
	}

	public static void setPacketTimeSlot(int packetTimeSlot) {
		NetworkProtocol.packetTimeSlot = packetTimeSlot;
	}

	public static boolean isSuccessPcktRxd() {
		return successPcktRxd;
	}

	public static void setSuccessPcktRxd(boolean successPcktRxd) {
		NetworkProtocol.successPcktRxd = successPcktRxd;
	}

}
