package testPackage;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GuiTest_1 extends JFrame {
	private static final long serialVersionUID = -6790235809345665514L;

	int timePerCycle			= 0;
	JLabel mainTitle			= new JLabel("Under Water Multihop Network");    
	JLabel portTitle			= new JLabel("Select Comm Port");
	JComboBox cboxPorts			= new JComboBox();
	JButton connectBtn			= new JButton("Connect");
    JButton disconnectBtn		= new JButton("Disconnect");
	JLabel numRobLabel			= new JLabel("Number of Robots");
	JTextField numRobText		= new JTextField();
	JLabel timePerCycleLabel	= new JLabel("Time/Cycle: ");
	JLabel cycleIndexLabel 		= new JLabel("Current Comm Cycle");
	JLabel dataPacketLabel 		= new JLabel("Enter Data Packet");
	JTextField dataPacketText 	= new JTextField(20);
	JButton startBtn			= new JButton("START");
	JLabel logAreaTitle			= new JLabel("Logging Area");
	JTextArea logAreaText 		= new JTextArea(10, 60);
	JScrollPane logAreaPane		= new JScrollPane(logAreaText);
	JLabel nullLabel			= new JLabel(" " + timePerCycle);
	
	
	public GuiTest_1(){
		
		
		super("UW Multihop Network Sensor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mainTitle.setFont(new java.awt.Font("Tahoma", 1, 14));
		mainTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		
		portTitle.setFont(new java.awt.Font("Tahoma", 1, 14));
		portTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		
		logAreaTitle.setFont(new java.awt.Font("Tahoma", 1, 14));
		logAreaTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

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
		controlPanel.add(startBtn);
		//controlPanel.add(nullLabel);
		
		buildConstraints(constraints, 0, 0, 1, 1, 100, 100);
		gridBagLayout.setConstraints(mainTitle, constraints);
		contentPane.add(mainTitle);
			
		/*buildConstraints(constraints, 0, 1, 1, 1, 100, 100);
		gridBagLayout.setConstraints(portTitle, constraints);
		contentPane.add(portTitle);*/
		
		buildConstraints(constraints, 3, 1, 1, 1, 100, 100);
		gridBagLayout.setConstraints(logAreaTitle, constraints);
		contentPane.add(logAreaTitle);
		
		buildConstraints(constraints, 0, 2, 2, 2, 100, 100);
		gridBagLayout.setConstraints(controlPanel, constraints);
		contentPane.add(controlPanel);
		
		buildConstraints(constraints, 3, 3, 1, 1, 100, 100);
		gridBagLayout.setConstraints(logAreaPane, constraints);
		contentPane.add(logAreaPane);
		
		/*buildConstraints(constraints, 0, 2, 1, 1, 100, 100);
		gridBagLayout.setConstraints(cboxPorts, constraints);
		contentPane.add(cboxPorts);
		
		buildConstraints(constraints, 1, 2, 1, 1, 100, 100);
		gridBagLayout.setConstraints(connectBtn, constraints);
		contentPane.add(connectBtn);
		
		buildConstraints(constraints, 2, 2, 1, 1, 100, 100);
		gridBagLayout.setConstraints(disconnectBtn, constraints);
		contentPane.add(disconnectBtn);
		
		buildConstraints(constraints, 3, 3, 1, 1, 100, 100);
		gridBagLayout.setConstraints(logAreaPane, constraints);
		contentPane.add(logAreaPane);
		
		buildConstraints(constraints, 0, 3, 1, 1, 100, 100);
		gridBagLayout.setConstraints(numRobLabel, constraints);
		contentPane.add(numRobLabel);
		
		buildConstraints(constraints, 1, 3, 1, 1, 100, 100);
		gridBagLayout.setConstraints(numRobText, constraints);
		contentPane.add(numRobText);
		
		buildConstraints(constraints, 2, 3, 1, 1, 100, 100);
		gridBagLayout.setConstraints(timePerCycleLabel, constraints);
		contentPane.add(timePerCycleLabel);
				
		buildConstraints(constraints, 0, 4, 1, 1, 100, 100);
		gridBagLayout.setConstraints(dataPacketLabel, constraints);
		contentPane.add(dataPacketLabel);
		
		buildConstraints(constraints, 1, 4, 1, 1, 100, 100);
		gridBagLayout.setConstraints(dataPacketText, constraints);
		contentPane.add(dataPacketText);
		
		buildConstraints(constraints, 0, 5, 1, 1, 100, 100);
		gridBagLayout.setConstraints(startBtn, constraints);
		contentPane.add(startBtn);*/
		
		/*
		//JPanel north	= new JPanel();
		JPanel south	= new JPanel();
		JPanel sWest	= new JPanel();
		JPanel sEast	= new JPanel();
		JPanel sWNorth	= new JPanel();
		JPanel sWSouth	= new JPanel();
		
		south.setLayout(new BorderLayout());
		sWest.setLayout(new BorderLayout());
		sEast.setLayout(new GridLayout(2, 1));
		sWNorth.setLayout(new GridLayout(1, 1, 0, 20));
		sWSouth.setLayout(new GridLayout(4, 3, 5, 5));
		
		contentPane.add("North", mainTitle);
		contentPane.add("South", south);
		
		south.add("West", sWest);
		south.add("East", sEast);
		
		sWest.add("North", sWNorth);
		sWest.add("South", sWSouth);
		
		sWNorth.add(portTitle);
		sWSouth.add(cboxPorts);
		sWSouth.add(connectBtn);	
		sWSouth.add(disconnectBtn);
		sWSouth.add(numRobLabel);
		sWSouth.add(numRobText);
		sWSouth.add(timePerCycleLabel);
		sWSouth.add(dataPacketLabel);
		sWSouth.add(dataPacketText);
		sWSouth.add(startBtn);
		
		sEast.add(logAreaTitle);
		sEast.add(logAreaText);*/
			
		/*
		JPanel westPane = new JPanel();
		westPane.setLayout(new GridLayout(5, 1));
		
		JPanel westSubPane1 = new JPanel();
		westSubPane1.setLayout(new GridLayout(1, 3));
		
		JPanel westSubPane2 = new JPanel();
		westSubPane2.setLayout(new GridLayout(1, 2));
		
		JPanel eastPane = new JPanel();
		eastPane.setLayout(new GridLayout(2, 1));
		
		contentPane.add("North", mainTitle);
		contentPane.add("West", westPane);
		contentPane.add("East", eastPane);
		
		westPane.add(portTitle);
		
		westPane.add(westSubPane1);
		westSubPane1.add(numRobLabel);
		westSubPane1.add(numRobText);
		westSubPane1.add(timePerCycleLabel);
		
		westPane.add(cycleIndexLabel);
		
		westPane.add(westSubPane2);
		westSubPane2.add(dataPacketLabel);
		westSubPane2.add(dataPacketText);
		
		westPane.add(startBtn);
		
		eastPane.add(logAreaTitle);
		eastPane.add(logAreaText);	*/	
		
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		setContentPane(contentPane);
		
		/*GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(mainTitle)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(portTitle)
												.addComponent(cboxPorts)
												.addComponent(numRobLabel)
												.addComponent(dataPacketLabel)
												.addComponent(startBtn))
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(connectBtn)
												.addComponent(numRobText)
												.addComponent(dataPacketText))																				
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(disconnectBtn)
												.addComponent(timePerCycleLabel)))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(logAreaTitle)
										.addComponent(logAreaText))
						)
				)
		);
		
		layout.linkSize(SwingConstants.HORIZONTAL, cboxPorts, connectBtn, disconnectBtn);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(mainTitle)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(portTitle)
								.addComponent(logAreaTitle)))
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)		
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(cboxPorts)
										.addComponent(connectBtn)
										.addComponent(disconnectBtn)
										.addComponent(logAreaText))))												
									.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(numRobLabel)
												.addComponent(numRobText)
												.addComponent(timePerCycleLabel))
									.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(dataPacketLabel)
												.addComponent(dataPacketText))
						.addComponent(startBtn)
		);
		
		
		setTitle("UW Multihop Network Sensor");
		pack();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);*/
		
		pack();
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new GuiTest_1().setVisible(true);
			}
		});*/
		new GuiTest_1().setVisible(true);
	}
	
	void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy){
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}

}
