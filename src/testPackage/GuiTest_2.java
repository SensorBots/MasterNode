/**
 * 
 */
package testPackage;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
public class GuiTest_2 extends JFrame implements ActionListener, FocusListener{

	private static final long serialVersionUID = 2918932934632365316L;

	SerialPortManager serialPortManager = null;
	KeyManager keyManager = null;
	NetworkProtocol networkProtocol = null;

	int timePerCycle			= 0;
	JLabel mainTitle			= new JLabel("Under Water Multihop Network");    
	JLabel portTitle			= new JLabel("Select Comm Port");
	JComboBox cboxPorts			= new JComboBox();
	JButton connectBtn			= new JButton("Connect");
	JButton disconnectBtn		= new JButton("Disconnect");
	JLabel numRobLabel			= new JLabel("Number of Robots");
	JTextField numRobText		= new JTextField(1);
	JLabel timePerCycleLabel	= new JLabel("Time/Cycle: ");
	JLabel cycleIndexLabel 		= new JLabel("Current Comm Cycle");
	JLabel dataPacketLabel 		= new JLabel("Enter Data Packet");
	JTextField dataPacketText 	= new JTextField(25);
	JButton sendBtn			= new JButton("SEND MSG");
	JLabel logAreaTitle			= new JLabel("Logging Area");
	JTextArea logAreaText 		= new JTextArea(20, 60);
	JScrollPane logAreaPane		= new JScrollPane(logAreaText);
	JLabel nullLabel			= new JLabel("0");
	JTextArea netAreaText		= new JTextArea(10, 100);

	public GuiTest_2(){
		Gui_Init();
		createObjects();
		serialPortManager.searchForPorts();
		keyManager.toggleControls();
		keyManager.bindKeys();		
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

		/*connectBtn.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				connectBtnActionPerformed(e);
			}
		});

		disconnectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disconnectBtnActionPerformed(e);
			}
		});

		sendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startBtnActionPerformed(e);
			}
		});

		dataPacketText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

			}
		});*/
		
		numRobText.setText("1");
		dataPacketText.setText("0,0,0,0,0");
		
		connectBtn.addActionListener(this);
		disconnectBtn.addActionListener(this);
		sendBtn.addActionListener(this);
		dataPacketText.addActionListener(this);
		
		dataPacketText.addFocusListener(this);
		

		GridBagLayout gridBagLayout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel contentPane = new JPanel();
		contentPane.setLayout(gridBagLayout);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(6, 2, 0, 0));

		controlPanel.add(portTitle);
		controlPanel.add(cboxPorts);
		controlPanel.add(connectBtn);
		controlPanel.add(disconnectBtn);
		controlPanel.add(numRobLabel);
		controlPanel.add(numRobText);
		controlPanel.add(timePerCycleLabel);
		controlPanel.add(nullLabel);
		controlPanel.add(dataPacketLabel);
		controlPanel.add(dataPacketText);
		controlPanel.add(sendBtn);

		buildConstraints(constraints, 0, 0, 1, 1, 100, 100);
		gridBagLayout.setConstraints(mainTitle, constraints);
		contentPane.add(mainTitle);

		buildConstraints(constraints, 3, 1, 1, 1, 100, 100);
		gridBagLayout.setConstraints(logAreaTitle, constraints);
		contentPane.add(logAreaTitle);

		buildConstraints(constraints, 0, 2, 2, 2, 100, 100);
		gridBagLayout.setConstraints(controlPanel, constraints);
		contentPane.add(controlPanel);

		buildConstraints(constraints, 3, 3, 1, 1, 100, 100);
		gridBagLayout.setConstraints(logAreaPane, constraints);
		contentPane.add(logAreaPane);

		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		setContentPane(contentPane);

		pack();
		

	}

	public void createObjects(){
		serialPortManager = new SerialPortManager(this);
		keyManager = new KeyManager(this);
		networkProtocol = new NetworkProtocol(this);
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
	public void focusGained(FocusEvent evt) {
		if (evt.getSource() == dataPacketText) {
			dataPacketText.setText("");
		}	
	}

	@Override
	public void focusLost(FocusEvent evt) {
		// do nothing
	}

	public void actionPerformed(ActionEvent evt){
		
		if (evt.getSource() == connectBtn) {
			serialPortManager.connect();
			if (serialPortManager.getConnected() == true)
			{
				if (serialPortManager.initIOStream() == true)
				{
					serialPortManager.initListener();
				}
			}
			
		} else if (evt.getSource() == disconnectBtn) {
			resetTimePerCycle();
			serialPortManager.disconnect();
			
		} else if (evt.getSource() == dataPacketText) {
			logAreaText.append("Default packet value changed" + "\n");
			
		} else if (evt.getSource() == sendBtn) {
			timePerCycle++;
			nullLabel.setText(String.valueOf(timePerCycle));
			
			String packetText = dataPacketText.getText();
			StringTokenizer token = new StringTokenizer(packetText, ",");
			int botId 		= Integer.parseInt(token.nextToken());
			int parent 		= Integer.parseInt(token.nextToken());
			int hasChild	= Integer.parseInt(token.nextToken());
			int hasSenLef	= Integer.parseInt(token.nextToken());
			int sendFirst	= Integer.parseInt(token.nextToken());
			
			serialPortManager.writeData(botId, parent, hasChild, hasSenLef, sendFirst);
		}
	}

	/*private void waitSeconds(int xSeconds){		
		long time0, time1;

		time0 = System.currentTimeMillis();
		do{
			time1 = System.currentTimeMillis();
		}
		while ((time1 - time0) < (xSeconds * 1000));
	}*/

	private void resetTimePerCycle(){
		timePerCycle = 0;
		nullLabel.setText(String.valueOf(timePerCycle));
	}

	public static void main(String[] args) {
		GuiTest_2 gui = new GuiTest_2();
		gui.setVisible(true);
		gui.setResizable(false);
		/*new GuiTest_2().setVisible(true);
		new GuiTest_2().setResizable(false);*/
	}

	

}
