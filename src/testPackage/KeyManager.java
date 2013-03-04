package testPackage;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.KeyStroke;


public class KeyManager {
	
	GuiTest_2 window = null;
	
	private int randomValue = 0;
	
	//private static char connect = 'c';
	//private static char disconnect = 'd';
	private static char startConnect = 's';
	
	public KeyManager(GuiTest_2 window){
		this.window = window;
	}

	public void bindKeys() {
		window.connectBtn.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(startConnect), "startNetwork");
		window.connectBtn.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(Character.toUpperCase(startConnect)), "startNetwork");
		window.connectBtn.getActionMap().put("startNetwork", startNetwork);
	}
	
	public void toggleControls(){
		if (window.serialPortManager.getConnected()) {
			
			window.cboxPorts.setEnabled(false);
			window.connectBtn.setEnabled(false);
			
			window.disconnectBtn.setEnabled(true);
			window.sendBtn.setEnabled(true);
		}
		else {
			window.cboxPorts.setEnabled(true);
			window.connectBtn.setEnabled(true);
			
			window.disconnectBtn.setEnabled(false);
			window.sendBtn.setEnabled(false);
		}
	}
	
	Action startNetwork = new AbstractAction() {
		private static final long serialVersionUID = -5773234292283175453L;

		public void actionPerformed(ActionEvent arg0) {
			randomValue = doSmthing(window.timePerCycle);
			updateLabels();
		}
	};
	
	public int doSmthing(int someValue){
		return someValue++;
	}
	
	public void updateLabels(){
		window.nullLabel.setText(String.valueOf(randomValue));
		
		//window.communicator.writeData(randomValue);
	}
	
}
