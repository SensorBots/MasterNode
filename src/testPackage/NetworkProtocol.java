package testPackage;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class NetworkProtocol {
	
	//passed from main GUI
	GuiTest_2 window = null;
	
	private final static int MAX_NUM_PARAMS = 11;

	final static int botID 		= 48;	// '0'
	final static int parent 	= 49;	// '1'	
	final static int hasChild	= 48;	// '0'
	final static int hasSenLef	= 48;	// '0'
	final static int sendFirst	= 49;	// '1'
	
	GregorianCalendar gcalendar = new GregorianCalendar();
	private static int hrs = 0;
	private static int min = 0;
	private static int sec = 0;

	private static int[] msgToChildren = new int[MAX_NUM_PARAMS]; //{botID, parent, hasChild, hasSenLef, sendFirst, hrs, min, sec};
	
	public NetworkProtocol(GuiTest_2 window){
		this.window = window;
	}
	
	public void getTime(){
		GregorianCalendar gcalendar = new GregorianCalendar();
		hrs = gcalendar.get(Calendar.HOUR_OF_DAY);
		min = gcalendar.get(Calendar.MINUTE);
		sec = gcalendar.get(Calendar.SECOND);
		window.logAreaText.append("Time: " +hrs+ ":" +min+ ":" + sec + "\n");
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
    
    
	public void createDataPacket(){
		getTime();
		
		char[] tHrs = {'0', '0'}; //Initializes the array so that it always includes a zero in case of one-digit values
		char[] tMin = {'0', '0'}; //new char[2]; //itoa(min);
		char[] tSec = {'0', '0'}; //new char[2]; //itoa(sec);
		
		itoa(hrs, tHrs);
		itoa(min, tMin);
		itoa(sec, tSec);
		
		msgToChildren[0] = botID; 
		msgToChildren[1] = parent; 
		msgToChildren[2] = hasChild; 
		msgToChildren[3] = hasSenLef; 
		msgToChildren[4] = sendFirst; 
		msgToChildren[5] = tHrs[0];
		msgToChildren[6] = tHrs[1]; 
		msgToChildren[7] = tMin[0];
		msgToChildren[8] = tMin[1];
		msgToChildren[9] = tSec[0];
		msgToChildren[10]= tSec[1];
	}
	
	public void createDataPacket(int botID, int parent, int hasChild, int hasSenLef, int sendFirst){
		getTime();
		
		char[] tHrs = {'0', '0'}; //Initializes the array so that it always includes a zero in case of one-digit values
		char[] tMin = {'0', '0'}; //new char[2]; //itoa(min);
		char[] tSec = {'0', '0'}; //new char[2]; //itoa(sec);
		char[] tmp = {'0'};
		
		itoa(hrs, tHrs);
		itoa(min, tMin);
		itoa(sec, tSec);		
		
		tmp = itoa(botID, 1);
		msgToChildren[0] = tmp[0];//botID; 
		
		tmp = itoa(parent, 1);
		msgToChildren[1] = tmp[0];
		
		tmp = itoa(hasChild, 1);
		msgToChildren[2] = tmp[0];
		
		tmp = itoa(hasSenLef, 1);
		msgToChildren[3] = tmp[0]; 
		
		tmp = itoa(sendFirst, 1);
		msgToChildren[4] = tmp[0]; 
		msgToChildren[5] = tHrs[0];
		msgToChildren[6] = tHrs[1]; 
		msgToChildren[7] = tMin[0];
		msgToChildren[8] = tMin[1];
		msgToChildren[9] = tSec[0];
		msgToChildren[10]= tSec[1];
	}
	
	/*public void genPacket(){
		getTime();
		createDataPacket();
	}
	
	public void genPacket(int botID, int parent, int hasChild, int hasSenLef, int sendFirst){
		getTime();
		createDataPacket(botID, parent, hasChild, hasSenLef, sendFirst);
	}*/

	public int getMaxNumParams() {
		return MAX_NUM_PARAMS;
	}

	public int[] getMsgToChildren() {
		return msgToChildren;
	}
}
