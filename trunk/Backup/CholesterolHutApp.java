/* ********************CholesterolHut*********************************************************
 * File name:		CDM.java                                                                 *
 * Author:			Brian Detweiler                                                          *
 *	Description: 	This app retrieves information from the Registry and presents it as a    * 
 *	                text file to the user. It can also send the information to a MySQL       *
 *	                database (or any other DB with some mods). Currently, the app finds      *
 *	                the following:                                                           *
 *	                
 *	                *** OS Version                      *   Workgroup
 *	                *** OS Type                         *** Domain
 *	                *?? Windows Serial Number           *   Hard Disk 1 Size/Type
 *	                *?? MAC Address                     *   Hard Disk 2 Size/Type
 *	                *?? IP Address                      *   Display Adapter
 *	                *** Host Name                       *   CD/DVD Drive 1
 *	                *   IEEE 1394                       *   CD/DVD Drive 2
 *	                *   Keyboard                        *   Floppy Drive 1
 *	                *** Mouse                           *   Floppy Drive 2
 *	                *   Modem                           *   Monitor
 *	                *   Ethernet Adapter                *   Com Port Devices
 *	                *   PCMCIA                          *   Parallel Devices
 *	                *** Processor Type                  *   Sound Card
 *	                *** Processor Speed                 *   USB Devices
 *	                *   Physical Memory                 *   Office Version
 *	                *   MISC Software Info              *   Office Serial Number 
 *	                *?? System BIOS Version
 *********************************************************************************************/	

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.text.*;
import java.lang.*;
import java.util.*;

public class CDM
{
   public static void main(String[] args)
   {
      JFrame frame = new CholesterolHutFrame();
		// This will catch all "unorthodox" close attempts and exit gracefully.
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Start the App
      frame.show();
   }
}

class CholesterolHutFrame extends JFrame
{
   public CholesterolHutFrame()
   {
			
      setTitle("CDM - The Coop Data Miner");
		// Set the dimensions of the window relative to the user's resolution
		Toolkit tk  = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		int width   = 600;
		int height  = 550;
      setBounds((d.width - width)/2, (d.height - height)/2, 600, 550);
		setResizable(true);	

		Container contentPane = getContentPane();
		contentPane.add(new CholesterolHutPanel());
   }
}

class CholesterolHutPanel extends JPanel implements ActionListener
{
	private JButton     gatherInfoBTN;
	private JButton     exitBTN;
	private JCheckBox   sendInfoCB;
    private JScrollPane scrollPane;
    private JTextArea   infoTA;

	public CholesterolHutPanel()
	{	
/* ******************** PANELS ********************************/
        JPanel infoPanel      = new JPanel();
		JPanel checkBoxPanel  = new JPanel();
		JPanel buttonPanel 	  = new JPanel();
        JPanel controlPanel   = new JPanel(); 

        infoPanel.setLayout(    new FlowLayout(FlowLayout.CENTER));

		checkBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		buttonPanel.setLayout(	new FlowLayout(FlowLayout.LEFT)); 

        controlPanel.setLayout( new BorderLayout());

/* ******************** INFO PANEL ****************************/
        infoTA     = new JTextArea(4, 20);
        infoTA.setLineWrap(false);
        infoTA.setEditable(false);
        scrollPane = new JScrollPane(infoTA);

/* ******************** CHECK BOXEN ***************************/

        
		sendInfoCB = new JCheckBox("Send to Database", true); // selected = true
		sendInfoCB.addActionListener(this);
        

		checkBoxPanel.add(sendInfoCB);
		
/* ******************** BUTTONS *******************************/
		gatherInfoBTN = new JButton("Gather Info");
		exitBTN       = new JButton("Exit"); 
		gatherInfoBTN.addActionListener(this);
		exitBTN.addActionListener(this);

		buttonPanel.add(gatherInfoBTN);
		buttonPanel.add(exitBTN);

        controlPanel.add(buttonPanel, BorderLayout.CENTER);
        controlPanel.add(checkBoxPanel, BorderLayout.SOUTH);

/* ******************* FINISH PANEL *****************************/
		setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
	}

/* ******************* ACTION LISTENER **************************/
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		// The Exit button will exit gracefully
		if(source == exitBTN)
		{
			System.exit(0);
		}
		/*
		 * The sendInfoBTN will total up the order,
		 * print it out in the text box fields, and
		 * reset the check boxes.  
		 */
		else if(source == gatherInfoBTN)
		{
            RegQuery query = new RegQuery();
            infoTA.setText("Personal Directory: "     + query.getCurrentUserPersonalFolderPath() + "\n");
            infoTA.append("CPU NAME: "                + query.getCPUName()                       + "\n");
            infoTA.append("CPU Speed: "               + query.getCPUSpeed()                      + " MHz\n");
            infoTA.append("Physical Memory: "         + query.getMem()                           + " MB\n");
            infoTA.append("Mouse: "                   + query.getMouse()                         + "\n");
            infoTA.append("OS Name: "                 + query.getOSName()                        + "\n");
            infoTA.append("OS Serial Number: "        + query.getSN()                            + "\n"); 
            infoTA.append("Registered Owner: "        + query.getOwner()                         + "\n");
            infoTA.append("Windows Activation Key: "  + query.getProductID()                     + "\n");
            infoTA.append("Service Pack Number: "     + query.getServicePack()                   + "\n");
            infoTA.append("Default Domain Name: "     + query.getDomainName()                    + "\n");
            infoTA.append("Default User Name: "       + query.getUserName()                      + "\n");
            infoTA.append("BIOS Version: "            + query.getBios()                          + "\n");
		}
		/*
		 * When a topping is selected, I thought it would be nice
		 * if the user could see how much their current selection 
		 * would cost them before tax. So the subtotal is added up 
		 * and printed in the subTotal textbox each time they click
		 * on a topping.
		 */
		else if(source == sendInfoCB)
		{
		}
	}
}

