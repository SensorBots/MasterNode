package src.guiVersion_6;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.LinkedBlockingQueue;

public class NetworkProtocol implements Runnable{

	//passed from main GUI
	GuiMain window = null;


	private final static int MAX_NUM_PARAMS 	= 11;
	//private final static int MasterNodeId 		= 0;	// '0'
	private final static int NULL 				= -1;
	private final static int timePerInterval 	= 5;
	//private final static int SELF_NETWORK_DATA 	= 1;
	//private final static int SELF_N_CHILD_DATA 	= 2;

	//private static int sendOnce						= 0;

	public static boolean successPcktRxd 			= false;
	public static boolean myChildrenSntDataSuccess 	= false;

	public static int numNodes 			= 2;
	public static int myTimeSlotCounter = 0;
	public static int myTimeSlotMax 	= 2*(numNodes*numNodes);

	public static int myBotId 			= 0;	// '0'
	public static int myParent 			= 0;	// '1'	
	public static int myHasChild		= 0;	// '0'

	public static int mySndFstTime		= -1;	// '1'
	public static int myNumChildren		= 0;
	public static int mySubTreeDone	= 0;

	public static boolean iHaveChildren = false;
	public static boolean IamALeaf		= false;	

	public static final LinkedBlockingQueue<Package> queue = new LinkedBlockingQueue<Package>();

	//	public static int packetBotId 		= 0;
	//	public static int packetParent 		= 0;
	//	public static int packetTimeSlot 	= 0;

	GregorianCalendar gcalendar = new GregorianCalendar();
	private static int hrs = 0;
	private static int min = 0;
	private static int sec = 0;

	public static int[] msgToChildren 	= new int[MAX_NUM_PARAMS]; //{botID, parent, hasChild, hasSenLef, sendFirst, hrs, min, sec};
	public static int[] myChildrenList 	= new int[numNodes];
	public static boolean[] rxdFromChildren	= new boolean[numNodes];
	public static boolean[] myChildSubTree = new boolean[numNodes];

	//FileReadWrite readWriteData = new FileReadWrite();
	Package receivedPackage;

	private int timeResult = -1;

	public NetworkProtocol(GuiMain window){
		this.window = window;
		initialization();
	}

	public void initialization(){

		for (int i = 0; i < numNodes; i++) {
			myChildrenList[i] = -1;
			rxdFromChildren[i] = false;
			myChildSubTree[i] = false;
		}
	}

	public void reflectionTest(){
		int rx = window.serialPortManager.getNumRxD();
		int tx = window.serialPortManager.getNumTxD();
		if (tx != 0) {
			String text = String.valueOf(rx + "/" + tx + " = " + rx/tx);
			window.timeSlotValueLabel.setText(text);
			window.logFile.WriteData("RxD/TxD Master Node Rate:\t" +text+ "\t\n");
		}
	}

	public void run(){

		while (window.serialPortManager.getConnectionStatus() == true) {

			reflectionTest();

			while (myTimeSlotCounter < myTimeSlotMax ) {//|| myParent == NULL <- not necessary in master

				while (timeResult != 0) { 
					//do nothing
					timeResult = (int) ((System.currentTimeMillis()/1000) %timePerInterval);

					try {
						System.out.println("Result: " + timeResult);
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						System.out.println(window.networkThread.getName() + " 's status2 is: " + window.networkThread.getState());
						//e.printStackTrace();						
					}
				}
                System.out.println("Debugging, slotCounter is "+ myTimeSlotCounter);
				if ((myTimeSlotCounter % numNodes) != myBotId ) {//|| myParent == NULL <- not necessary in master

					if (isSuccessPcktRxd()) {//(successPcktRxd == true) {
						setSuccessPcktRxd(false); //successPcktRxd = false;

						/*if (isChildrenSubTreeDone() == true) {
							mySubTreeDone = 1;
							sendOnce = 1;
						}*/					

						try {
							System.out.println("DEBUG!!!!!!!!!!!!!!!!receiving data now, ");
							receivedPackage = queue.take();

							if (myParent == NULL) {
								myParent = receivedPackage.botID;
							}  else if (myBotId == receivedPackage.packetParent) {//else if (isBotInMyChildrenList(packetBotId) == false) {
								putBotInMyChildrenList(receivedPackage.botID);
								//setRxdFromChildren(receivedPackage.botID);
								iHaveChildren = true;

								if (receivedPackage.packetSubTree == 1) {
									setDoneChildSubTreeList(receivedPackage.botID);
								}
							}
							myTimeSlotCounter = receivedPackage.packetTimeSlot;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} else {
					if (myParent != NULL ) { //&& sendOnce <= 1
						window.serialPortManager.sendData(myBotId, myParent, myTimeSlotCounter, mySubTreeDone);
						System.out.println("myParent != NULL GOT IN" );

						if (mySndFstTime == -1) {
							mySndFstTime = myTimeSlotCounter;
						}

						//sendOnce = 2;
					}
				}

				if (myTimeSlotCounter == (mySndFstTime + numNodes) && iHaveChildren == false) { //>=
					IamALeaf = true;
				}

				if ((IamALeaf == true || myChildrenSntDataSuccess == true) && (myTimeSlotCounter % numNodes) == myBotId) { // && sendOnce > 1
					window.serialPortManager.sendData(myBotId, myParent, myTimeSlotCounter, mySubTreeDone);
					//System.out.println("long condition GOT IN" );

				} else{

					if (isSuccessPcktRxd()) {

						//Package receivedPackage;

						try {
							receivedPackage = queue.take();
							window.logFile.WriteData(receivedPackage);

						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						setRxdFromChildren(receivedPackage.botID);
						setSuccessPcktRxd(false); //successPcktRxd = false;

						if (rxdFromAllChildrenTest() == true) {
							myChildrenSntDataSuccess = true;
						}
					}
				}
				myTimeSlotCounter++;
			}
			System.out.println("myTimeSlotCounter: " +myTimeSlotCounter + " mySndFstTime: " +mySndFstTime + " myHasChild: "+ myHasChild + " myHasSeenLeaf: "+ IamALeaf);
			myTimeSlotCounter = 0;
			timeResult = -1;
		}
		//}
	}


	public void getTime(){
		//GregorianCalendar gcalendar = new GregorianCalendar();
		hrs = gcalendar.get(Calendar.HOUR_OF_DAY);
		min = gcalendar.get(Calendar.MINUTE);
		sec = gcalendar.get(Calendar.SECOND);
		//window.logAreaText.append("Time: " +hrs+ ":" +min+ ":" + sec + "\n");
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

	public void createDataPacket(int botID, int parent, int timeSlot, int packetSubTree){
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

		tmp = itoa(botID, 1);		//botID
		msgToChildren[0] = tmp[0];

		tmp = itoa(parent, 1);		//parent
		msgToChildren[1] = tmp[0];

		msgToChildren[2] = tmp2[0];	//timeSlot 0
		msgToChildren[3] = tmp2[1]; //timeSlot 1

		tmp = itoa(packetSubTree, 1);
		msgToChildren[4] = tmp[0];  //packetSubTree

		msgToChildren[5] = tHrs[0];
		msgToChildren[6] = tHrs[1]; 
		msgToChildren[7] = tMin[0];
		msgToChildren[8] = tMin[1];
		msgToChildren[9] = tSec[0];
		msgToChildren[10] = tSec[1];
	}

	private static void putBotInMyChildrenList(int botToInsert){
		int i = 0;
		while(i < myChildrenList.length) {
			if (myChildrenList[i] == -1) {
				myChildrenList[i] = botToInsert;
				myNumChildren++;

				System.out.println("New children @ pos. " + i + " : " +myChildrenList[i]);

				break;
			}
			i++;
		}
	}

	private static void setDoneChildSubTreeList(int botToSet){
		if (myChildSubTree[botToSet] == false) {
			myChildSubTree[botToSet] = true;
		}
	}

	/*private static boolean isChildrenSubTreeDone(){
		for (int i = 0; i < myChildSubTree.length; i++) {
			if (myChildSubTree[i] == false) {
				return false;
			}
		}
		return true;
	}*/

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

	public static boolean isSuccessPcktRxd() {
		return successPcktRxd;
	}

	public static void setSuccessPcktRxd(boolean successPcktRxd) {
		NetworkProtocol.successPcktRxd = successPcktRxd;
	}

}
