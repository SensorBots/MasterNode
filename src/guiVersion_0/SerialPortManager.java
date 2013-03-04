package guiVersion_0;

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
			//serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

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
		}
	}

	final public boolean getConnected()
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

					if (netText[1] != msgToChildren[0]) { //botID parent myHasChild...						
						/*for (int i = 0; i < byteCounter; i++) {
							window.logAreaText.append(new String(new byte[] { netText[i] }));
						}
						window.logAreaText.append(new String(new byte[] { STOP_BYTE }) + "\n");*/
						
						parseChildMsg(byteCounter);
						
						/*for (int i = 1; i < byteCounter; i++) {							
							window.netAreaText.setForeground(Color.blue);
							window.netAreaText.append(new String(new byte[] {netText[i]}) + "\t");	
						}
						window.netAreaText.append("\n");*/						
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
			}	
		}

	}

	public void writeData(){
		window.networkProtocol.createDataPacket();
		int[] msgToChildren = window.networkProtocol.getMsgToChildren();

		try {

			output.write(START_BYTE);

			for (int i = 0; i < msgToChildren.length; i++) {
				output.write(msgToChildren[i]);
			}

			output.write(STOP_BYTE);
			output.flush();

		} catch (Exception e) {
			logText = "Failed to write data. (" + e.toString() + ")";
			window.logAreaText.setForeground(Color.red);
			window.logAreaText.append(logText + "\n");
		}

		window.logAreaText.append(new String(new byte[] {START_BYTE}));
		for (int i = 0; i < msgToChildren.length; i++) {
			window.logAreaText.append(Integer.toString(msgToChildren[i]));				
		}
		window.logAreaText.append(new String(new byte[] {STOP_BYTE}) + "\n");
	}

	public void writeData(int botID, int parent, int timeSlot){

		window.networkProtocol.createDataPacket(botID, parent, timeSlot);
		int[] msgToChildren = NetworkProtocol.msgToChildren;

		try {
			output.write(START_BYTE);

			for (int i = 0; i < msgToChildren.length; i++) {
				output.write(msgToChildren[i]);
			}

			output.write(STOP_BYTE); //ronald@adastrarocket.com; 
			output.flush();

		} catch (Exception e) {
			logText = "Failed to write data. (" + e.toString() + ")";
			window.logAreaText.setForeground(Color.red);
			window.logAreaText.append(logText + "\n");
		}

		window.logAreaText.append(new String(new byte[] {START_BYTE}));
		for (int i = 0; i < msgToChildren.length; i++) {
			//window.logAreaText.append(Integer.toString(msgToChildren[i]));  //Shows the decimal values correspondent to each byte sent
			window.logAreaText.append(new String(new byte[] {(byte) msgToChildren[i]}));
		}
		window.logAreaText.append(new String(new byte[] {STOP_BYTE}) + "\n");

	}

	
	public void parseChildMsg(int byteCounter){
		for (int i = 1; i < byteCounter; i++) {
			if (i == 1 || i == 2 || i == 11) {
				//System.out.println(i + " " + byteCounter);
				window.netAreaText.append(new String(new byte[] {netText[i]}) + " ");
			}
			else if (i >= 3 && i <= 10 ) {
				if (i == 3) {
					window.netAreaText.append(new String(new byte[] {netText[i]}));
				}
				else if (i == 4) {
					window.netAreaText.append(new String(new byte[] {netText[i]}) + "-");
				} else {
					if (i % 2 == 0) {//if even			
						window.netAreaText.append(new String(new byte[] {netText[i]}) + ": "); 
					} else {
						window.netAreaText.append(new String(new byte[] {netText[i]}));
					}
				}
				
			}
			
			else if (i >= 12) {
				if ((i - 12 )% 9 == 0) {
					window.netAreaText.append(new String(new byte[] {netText[i]}) + "-");
				} else {
					if (i % 2 == 0) {					
						window.netAreaText.append(new String(new byte[] {netText[i]}) + "::"); 
					} else {
						window.netAreaText.append(new String(new byte[] {netText[i]}));
					}
				}
			}
			
			/*else if (i >= 12 && i <= 28 ) {
				if (i % 2 == 0) {					
					window.netAreaText.append(new String(new byte[] {netText[i]}));
				} else {
					window.netAreaText.append(new String(new byte[] {netText[i]}) + ": ");
				}
			}
			
			
			else if (i == 3) {
				window.netAreaText.append(new String(new byte[] {netText[i]}) + ": ");
			}
			else if (i == 4) {
				window.netAreaText.append(new String(new byte[] {netText[i]}));
			}
			else if (i == 5) {
				window.netAreaText.append(new String(new byte[] {netText[i]}) + ": ");
			}
			else if (i == 6) {
				window.netAreaText.append(new String(new byte[] {netText[i]}));
			}
			else if (i == 7) {
				window.netAreaText.append(new String(new byte[] {netText[i]}) + " ");
			}
			else if (i == 8 || i == 9) {
				window.netAreaText.append(new String(new byte[] {netText[i]}) + "-");
			}
			else {
				window.netAreaText.append(new String(new byte[] {netText[i]}));
			}*/			
		}
		
		
		
		/*for (int i = 1; i < byteCounter; i++) {							
			window.netAreaText.setForeground(Color.blue);			
			window.netAreaText.append(new String(new byte[] {netText[i]}) + "\t");	
		}*/
		window.netAreaText.append("\n");
	}
	

	/**
	 * Convert the given integer number to its ASCII equivalent in a character array.
	 * 
	 * @param number given integer number
	 * @return a character array representing ASCII form of the number
	 *//*
    private  char[] itoa(int number) {
        boolean negative = false;
        if(number < 0) {
            negative = true;
            number = 0 - number;
        }

        if(number >= 0 && number <= 9) {
            char temp = (char) (ASCII_VALUE_OF_ZERO + number);
            if(!negative) {
                return new char[] { temp };
            }

            return new char[] { '-', temp };
        }

        // define an array of which can hold 12 characters
        // the max integer is 10 digits long - 1 for negative character
        char[] digits = new char[12];

        // now let's divide the number by 10 and keep adding the remainder
        int digitPosition = 0;

        do {
            int remainder = number % 10;
            number = number / 10;

            digits[digitPosition++] = (char) (ASCII_VALUE_OF_ZERO + remainder);
        } while(number > 0);

        window.logAreaText.append(" numDigs: " +digitPosition+ "\n");

        //if (digitPosition == 1) {
        	digits[digitPosition++] = ASCII_VALUE_OF_ZERO;
        //}

        // add negative sign if needed
        if(negative) {
            digits[digitPosition++] = '-';
        } 

        // now reverse the array
        for(int i = 0; i < digitPosition / 2; i++) {
            char temp = digits[i];
            digits[i] = digits[digitPosition - i - 1];
            digits[digitPosition - i - 1] = temp;
        }

        window.logAreaText.append("char s: " +String.valueOf(digits)+ " numDigs: " +digitPosition+ "\n");
        return digits;
    }*/

}
