package guiVersion_5;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;

public class SerialPortManager implements SerialPortEventListener{

	//passed from main GUI
	GuiMain window = null;

	//for containing the ports that will be found
	@SuppressWarnings("rawtypes")
	private Enumeration ports = null;
	//map the port names to CommPortIdentifiers
	private HashMap<String, CommPortIdentifier> portMap = new HashMap<String, CommPortIdentifier>();

	//this is the object that contains the opened port
	private CommPortIdentifier selectedPortIdentifier = null;
	private SerialPort serialPort = null;

	//input and output streams for sending and receiving data
	private InputStream input = null;
	private OutputStream output = null;

	//just a boolean flag that i use for enabling
	//and disabling buttons depending on whether the program
	//is connected to a serial port or not
	private boolean bConnected = false;

	//the timeout value for connecting with the port
	final static int TIMEOUT = 2000;

	//some ascii values for for certain things
	final static int SPACE_ASCII = 32;
	final static int DASH_ASCII = 45;
	final static int NEW_LINE_ASCII = 10;
	final static int START_BYTE = 91; // '['
	final static int STOP_BYTE = 93; // ']'
	final static int ASCII_VALUE_OF_ZERO = 48;

	//a string for recording what goes on in the program
	//this string is written to the GUI
	String logText = "";

	boolean dataPacketFlag = false;
	private int byteCounter = 0;
	byte netText[] = new byte[200];
	
	public int numRxD = 0;
	public int numTxD = 0;

	public SerialPortManager(GuiMain window){
		this.window = window;
	}

	public void searchForPorts(){
		ports = CommPortIdentifier.getPortIdentifiers();

		while (ports.hasMoreElements()) {
			CommPortIdentifier currentPort = (CommPortIdentifier) ports.nextElement();
			if (currentPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				window.cboxPorts.addItem(currentPort.getName());
				portMap.put(currentPort.getName(), currentPort);
			}
		}
	}

	public void connect(){
		String selectedPort = (String)window.cboxPorts.getSelectedItem();
		selectedPortIdentifier = (CommPortIdentifier)portMap.get(selectedPort);

		CommPort commPort = null;

		try {
			commPort = selectedPortIdentifier.open("UWSN_ControlPanel", TIMEOUT);
			serialPort = (SerialPort)commPort;
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			setConnected(true);

			logText = selectedPort + " opened successfully!";
			window.logAreaText.setForeground(Color.black);
			window.logAreaText.append(logText + "\n");

			window.keyManager.toggleControls();

		} catch (PortInUseException e) {
			logText = selectedPort + " is in use. (" +e.toString() + ")";

			window.logAreaText.setForeground(Color.red);
			window.logAreaText.append(logText + "\n");
		}
		catch (Exception e) {
			logText = "Failed to open " + selectedPort + "(" + e.toString() + ")";

			window.logAreaText.setForeground(Color.red);
			window.logAreaText.append(logText + "\n");
		}
	}

	public boolean initIOStream(){

		boolean successful = false;

		try {
			input 	= serialPort.getInputStream();
			output 	= serialPort.getOutputStream();

			successful = true;
			return successful;

		} catch (IOException e) {
			logText = "I/O Streams failed to open. (" + e.toString() + ")";
			window.logAreaText.setForeground(Color.red);
			window.logAreaText.append(logText + "\n");
			return successful;
		}
	}

	public void initListener(){

		try {
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);

		} catch (TooManyListenersException e) {
			logText = "Too many listeners. (" + e.toString() + ")";
			window.logAreaText.setForeground(Color.red);
			window.logAreaText.append(logText + "\n");
		}		
	}

	public void disconnect(){
		try {
			serialPort.removeEventListener();
			serialPort.close();
			input.close();
			output.close();

			setConnected(false);
			window.keyManager.toggleControls();

			logText = "Disconnected.";
			window.logAreaText.setForeground(Color.red);
			window.logAreaText.append(logText + "\n");


		} catch (Exception e) {
			logText = "Failed to close " + serialPort.getName() + "(" + e.toString() + ")";
			window.logAreaText.setForeground(Color.red);
			window.logAreaText.append(logText + "\n");
			//e.printStackTrace();
		}
	}

	final public boolean getConnectionStatus()
	{
		return bConnected;
	}

	public void setConnected(boolean bConnected)
	{
		this.bConnected = bConnected;
	}

	public void serialEvent(SerialPortEvent spe) {

		int[] msgToChildren = NetworkProtocol.msgToChildren;
		if (spe.getEventType() == SerialPortEvent.DATA_AVAILABLE) {//If one byte of data came in


			try {
				byte charVal = (byte)input.read();

				if (charVal == START_BYTE && dataPacketFlag == false) { //Stores start byte
					dataPacketFlag = true;

					netText[byteCounter++] = charVal;
					
					window.logAreaText.append("RxD-");
					logText = new String(new byte[] {charVal});
					window.logAreaText.append(logText);
				}

				else if (charVal != STOP_BYTE && dataPacketFlag == true) { //Stores actual data
					logText = new String(new byte[] {charVal});
					window.logAreaText.append(logText);

					netText[byteCounter++] = charVal;					
				}

				else if (charVal == STOP_BYTE && dataPacketFlag == true) { //Stores stop byte
					dataPacketFlag = false;

					if (netText[1] != msgToChildren[0]) { //botID parent...						
						parseChildMsg(byteCounter);	
						printChildMsg(byteCounter);
						NetworkProtocol.setSuccessPcktRxd(true);
						numRxD++;
					} 

					logText = new String(new byte[] {charVal});
					window.logAreaText.append(logText);
					window.logAreaText.append("\n");

					byteCounter = 0;
				}


			} catch (Exception e) {
				logText = "Failed to read data." + "(" + e.toString() + ")";
				window.logAreaText.setForeground(Color.red);
				window.logAreaText.append(logText + "\n");
				System.err.println(e.toString());
			}
		}
	}

	public void sendData(int botID, int parent, int timeSlot, int packetSubTree){

		window.networkProtocol.createDataPacket(botID, parent, timeSlot, packetSubTree);
		int[] msgToChildren = NetworkProtocol.msgToChildren;

		try {
			output.write(START_BYTE);
			System.out.print("Data Sent: [");
			
			window.logAreaText.append("TxD-");
			logText = new String(new byte[] {START_BYTE});
			window.logAreaText.append(logText);

			for (int i = 0; i < msgToChildren.length; i++) {
				output.write(msgToChildren[i]);
				window.logAreaText.append(new String(new byte[] {(byte) msgToChildren[i]}));
				System.out.print(new String(new byte[] {(byte) msgToChildren[i]}));
			}

			output.write(STOP_BYTE); 
			output.flush();
			numTxD++;
			
			logText = new String(new byte[] {STOP_BYTE});
			window.logAreaText.append(logText + "\n");
			
			System.out.print(new String(new byte[] {STOP_BYTE}) + "\n");

		} catch (Exception e) {
			logText = "Failed to write data. (" + e.toString() + ")";
			window.logAreaText.setForeground(Color.red);
			window.logAreaText.append(logText + "\n");
			//e.printStackTrace();
		}
	}


	public void parseChildMsg(int byteCounter){
		int cnter = 0;
		int[] tmp = new int[2];
		int packetBotID = -1;
		int packetParent = -1;
		int packetTimeSlot = -1;
		int packetSubTree = -1;

		for (int i = 1; i < byteCounter; i++) {

			if (i == 1) {
				packetBotID = Integer.parseInt(new String(new byte[] {netText[i]})); //(int) netText[i] & 0xff;
			} else if (i == 2) {
				packetParent = (int) netText[i] & 0xff;
			} else if (i == 3 || i == 4) {
				tmp[cnter++] = Integer.parseInt(new String(new byte[] {netText[i]})); 

				if (cnter == 2) {
					packetTimeSlot = tmp[0]*10 + tmp[1];
					window.timeSlotValueLabel.setText(NetworkProtocol.myTimeSlotCounter + " / " + packetTimeSlot);
					cnter = 0;
				}
			} else if (i == 5) {
				packetSubTree = (int) netText[i] & 0xff;
			}
		}
		NetworkProtocol.queue.add(new Package(packetBotID, packetParent, packetTimeSlot, packetSubTree));
	}

	public void printChildMsg(int byteCounter){


		for (int i = 1; i < byteCounter; i++) {

			if (i == 1 || i == 2 || i== 5 || i == 12) {				
				window.netAreaText.append(new String(new byte[] {netText[i]}) + " ");
			}

			if (i == 3) {
				window.netAreaText.append(new String(new byte[] {netText[i]}));
			}
			else if (i == 4) {
				window.netAreaText.append(new String(new byte[] {netText[i]}) + "-");
			}

			else if (i >= 6 && i <= 11 ) {
				if (i % 2 == 0) {//if even			
					window.netAreaText.append(new String(new byte[] {netText[i]}) + ": "); 
				} else {
					window.netAreaText.append(new String(new byte[] {netText[i]}));
				}
			}

			else if (i >= 13) {
				if ((i - 13 )% 9 == 0) {
					window.netAreaText.append(new String(new byte[] {netText[i]}) + "-");
				} else {
					if (i % 2 == 0) {					
						window.netAreaText.append(new String(new byte[] {netText[i]}) + "::"); 
					} else {
						window.netAreaText.append(new String(new byte[] {netText[i]}));
						//text.concat(new String(new byte[] {netText[i]}));
					}
				}
			}
		}
		window.netAreaText.append("\n");
		window.logFile.WriteData(window.netAreaText.getText());
	}

	public int getNumRxD() {
		return numRxD;
	}

	public int getNumTxD() {
		return numTxD;
	}


}
