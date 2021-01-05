package main;

import java.awt.Dialog.ModalityType;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Thread responsible to show the Loading Message Dialog.
 * 
 * @author Pedro Nóbrega
 *
 */
public class LoadingMessage extends Thread {

	private JDialog modalDialog;
	
	/**
	 * Construct the Loading Message.
	 * 
	 * @param frame
	 */
	public LoadingMessage(JFrame frame) {
		this.modalDialog = new JDialog(frame, "Loading...", ModalityType.DOCUMENT_MODAL);
		this.modalDialog.setSize(200, 45);
		this.modalDialog.setLocationRelativeTo(frame);
	}
	
	/**
	 * Interrupt the thread and set the dialog invisible.
	 */
	public void deleteMessage() {
		this.interrupt();
		this.modalDialog.setVisible(false);
	}
	
	/**
	 * Set the dialog visible.
	 */
	private void showLoadingMessage() {
		this.modalDialog.setVisible(true);
	}
	
	/**
	 * Start the thread.
	 */
	@Override
	public void run() {
		this.showLoadingMessage();
	}
}
