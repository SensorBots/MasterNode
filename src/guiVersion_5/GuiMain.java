/**
 * 
 */
package src.guiVersion_5;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author andres
 *
 */
public class GuiMain extends JFrame implements ActionListener /*, FocusListener*/{

	private static final long serialVersionUID = 2918932934632365316L;

	SerialPortManager serialPortManager = null;
	KeyManager keyManager = null;
	NetworkProtocol networkProtocol = null;
	Thread networkThread = null;
	
	FileReadWrite logFile = null;


	int timePerCycle			= 0;
	JLabel mainTitle			= new JLabel("Under Water Multihop Network");    
	JLabel portTitle			= new JLabel("Select Comm Port -->");
	JComboBox cboxPorts			= new JComboBox();
	JButton connectBtn			= new JButton("Connect");
	JButton disconnectBtn		= new JButton("Disconnect");
	JLabel numChildLabel		= new JLabel("Number of Children");
	JTextField numChildText		= new JTextField(2);
	JLabel timeSlotLabel		= new JLabel("RxD/TxD: "); //"Current Time Slot: "
	JLabel cycleIndexLabel 		= new JLabel("Current Comm Cycle");
	JLabel dataPacketLabel 		= new JLabel("Test Data Packet"); //"Enter Data Packet"
	//JTextField dataPacketText 	= new JTextField(25);
	JButton dataPacketButton	= new JButton("0,0,0,0");
	JButton sendBtn				= new JButton("SEND MSG");
	JLabel logAreaTitle			= new JLabel("Logging Area");
	JTextArea logAreaText 		= new JTextArea(16, 60);
	JScrollPane logAreaPane		= new JScrollPane(logAreaText);
	JLabel timeSlotValueLabel	= new JLabel("0");
	JLabel netAreaTitle			= new JLabel("botID-prnt-timeSlot-SubTree-hh-mm-ss-numChildren-botID::OX::PH::TP::AB");
	JTextArea netAreaText		= new JTextArea(5, 40);
	JScrollPane netAreaPane		= new JScrollPane(netAreaText);

	public GuiMain(){
		Gui_Init();
		createObjects();
		serialPortManager.searchForPorts();
		keyManager.toggleControls();
		//keyManager.bindKeys();		
	}

	public void Gui_Init(){

		setTitle("UW Multihop Network Sensor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainTitle.setFont(new java.awt.Font("Tahoma", 1, 14));
		mainTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

		portTitle.setFont(new java.awt.Font("Tahoma", 1, 14));
		portTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

		logAreaTitle.setFont(new java.awt.Font("Tahoma", 1, 14));
		logAreaTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		
		netAreaTitle.setFont(new java.awt.Font("Tahoma", 1, 12));
		netAreaTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		
		numChildText.setText("1");
		//dataPacketText.setText("0,0,0,0"); //botId, parent, timeSlot, subtreeDone
		
		connectBtn.addActionListener(this);
		disconnectBtn.addActionListener(this);
		sendBtn.addActionListener(this);
		//dataPacketText.addActionListener(this);		
		//dataPacketText.addFocusListener(this);
		dataPacketButton.addActionListener(this);

		GridBagLayout gridBagLayout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel contentPane = new JPanel();
		contentPane.setLayout(gridBagLayout);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(6, 2, 0, 0));

		buildConstraints(constraints, 1, 0, 2, 1, 100, 100); //1,1
		gridBagLayout.setConstraints(mainTitle, constraints);
		contentPane.add(mainTitle);

		controlPanel.add(portTitle);
		controlPanel.add(cboxPorts);
		controlPanel.add(connectBtn);
		controlPanel.add(disconnectBtn);
		controlPanel.add(numChildLabel);
		controlPanel.add(numChildText);
		controlPanel.add(timeSlotLabel);
		controlPanel.add(timeSlotValueLabel);
		controlPanel.add(dataPacketLabel);
		//controlPanel.add(dataPacketText);
		controlPanel.add(dataPacketButton);
		controlPanel.add(sendBtn);
		
		buildConstraints(constraints, 2, 1, 2, 1, 100, 100); 
		gridBagLayout.setConstraints(logAreaTitle, constraints);
		contentPane.add(logAreaTitle);

		buildConstraints(constraints, 0, 2, 2, 2, 100, 100); 
		gridBagLayout.setConstraints(controlPanel, constraints);
		contentPane.add(controlPanel);
		
		buildConstraints(constraints, 2, 2, 2, 4, 100, 100); 
		gridBagLayout.setConstraints(logAreaPane, constraints);
		contentPane.add(logAreaPane);
		
		buildConstraints(constraints, 0, 4, 2, 1, 100, 100);
		gridBagLayout.setConstraints(netAreaTitle, constraints);
		contentPane.add(netAreaTitle);
		
		buildConstraints(constraints, 0, 5, 2, 1, 100, 100); 
		gridBagLayout.setConstraints(netAreaPane, constraints);
		contentPane.add(netAreaPane);

		//constraints.fill = GridBagConstraints.BOTH;
		//constraints.anchor = GridBagConstraints.CENTER;
		setContentPane(contentPane);

		pack();
	}

	public void createObjects(){
		serialPortManager = new SerialPortManager(this);
		keyManager = new KeyManager(this);
		networkProtocol = new NetworkProtocol(this);
		networkThread = new Thread(networkProtocol, "networkThread");
		logFile = new FileReadWrite(this);
	}

	void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy){
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}
	
	@Override
	/*public void focusGained(FocusEvent evt) {
		if (evt.getSource() == dataPacketText) {
			dataPacketText.setText("");
		}	
	}

	@Override
	public void focusLost(FocusEvent evt) {
		// do nothing
	}*/

	public void actionPerformed(ActionEvent evt){
		
		if (evt.getSource() == connectBtn) {
			serialPortManager.connect();
			if (serialPortManager.getConnectionStatus() == true)
			{
				if (serialPortManager.initIOStream() == true)
				{
					serialPortManager.initListener();
				}
			}
			
			//logFile.WriteHeader();
			
		} else if (evt.getSource() == disconnectBtn) {
			resetTimePerCycle();
			serialPortManager.disconnect();
			//networkThread.interrupt();
			System.out.println(networkThread.getName() + " 's status is: " + networkThread.getState());
			
		} /*else if (evt.getSource() == dataPacketText) {
			logAreaText.append("Default packet value changed" + "\n");
			
		}*/ else if (evt.getSource() == sendBtn) {
			//timePerCycle++;
			//timeSlotValueLabel.setText(String.valueOf(timePerCycle));
					
			if (!networkThread.isAlive() && !networkThread.isInterrupted()) {
				networkThread.start();				
			}
 			//serialPortManager.sendData(0, 0, 0, 0);
			
		} else if (evt.getSource() == dataPacketButton) {
			String packetText = dataPacketButton.getText();
			StringTokenizer token = new StringTokenizer(packetText, ",");
			int botId 			= Integer.parseInt(token.nextToken());
			int parent 			= Integer.parseInt(token.nextToken());
			int timeSlot 		= Integer.parseInt(token.nextToken());
			int packetSubTree 	= Integer.parseInt(token.nextToken());
			
			serialPortManager.sendData(botId, parent, timeSlot, packetSubTree);
			System.out.println(networkThread.getName() + " 's status is: " + networkThread.getState());
		}
		
	}

	private void resetTimePerCycle(){
		timePerCycle = 0;
		timeSlotValueLabel.setText(String.valueOf(timePerCycle));
	}

	public static void main(String[] args) {
		GuiMain gui = new GuiMain();
		gui.setVisible(true);
		//gui.setResizable(false);
	}	

}
