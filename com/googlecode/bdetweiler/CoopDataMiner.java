package com.googlecode.bdetweiler;

/* ********************CDM*****************************************************
 * File name:       cdm.java                                                  *
 * Author:          Brian Detweiler                                           *
 * Description:     This app retrieves information from the Registry and      *
 *                  presents it as a                                          *
 *                  text file to the user. It can also send the information   *
 *                  to a MySQL database (or any other DB with some mods).     *
 *                  Currently, the app finds the following:                   *
 *                                                                            *
 *                  LEGEND:                                                   *
 *                     @   == NOT DONE OR STARTED.                            *
 *                     *** == DONE                                            *
 *                     !   == TEMPORARILY WORKING. WILL NEED FIXING LATER.    *
 *                     ?   == IMPLEMENTED BUT NOT WORKING. NEEDS TO BE FIXED. *
 *                                                                            *
 *                  *** OS Version                      @   Workgroup         *
 *                  *** OS Type                         *** Domain            *
 *                  ?   Windows Serial Number           *** Hard Disk 1 Type  *
 *                  !   MAC Address                     *** SCSI Disk 1 Type  *
 *                  *** IP Address                      @   Hard Disk 1 Size  *
 *                  *** Host Name                       !   CD/DVD Drive 1    *
 *                  !   IEEE 1394                       @   CD/DVD Drive 2    *
 *                  *** Keyboard                        @   Floppy Drive 1    *
 *                  *** Mouse                           @   Floppy Drive 2    *
 *                  *** Modem                           @   Monitor           *
 *                  @   Ethernet Adapter                @   Com Port Devices  *
 *                  @   PCMCIA                          @   Parallel Devices  *
 *                  *** Processor Type                  @   Sound Card        *
 *                  *** Processor Speed                 @   USB Devices       *
 *                  *** Physical Memory                 @   Office Version    *
 *                  @   MISC Software Info              @   Office S/N        *
 *                  *** System BIOS Version             @   Display Adapter   *
 *****************************************************************************/



import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CoopDataMiner
{
    @SuppressWarnings("deprecation")
	public static void main(String[] args)
    {
        JFrame frame = new CDMFrame();
        // Catch all close attempts and exit gracefully.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Start the App
        frame.show();
    }
}

class CDMFrame extends JFrame
{

	private static final long serialVersionUID = 1L;

	public CDMFrame()
    {

        setTitle("CDM - The Coop Data Miner");
        // Set the dimensions of the window relative to the user's resolution
        Toolkit tk  = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int width   = 600;
        int height  = 550;
        setBounds((d.width - width) / 2, (d.height - height) / 2, 600, 550);
        setResizable(true);

        Container contentPane = getContentPane();
        contentPane.add(new CDMPanel());
   }
}

class CDMPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JButton     gatherInfoBTN;
    private JButton     exitBTN;
    private Checkbox    sendInfoCB;
    private JScrollPane scrollPane;
    private JTextArea   infoTA;

    public CDMPanel()
    {
/* ******************** PANELS ********************************/
        JPanel infoPanel      = new JPanel();
        JPanel checkBoxPanel  = new JPanel();
        JPanel buttonPanel    = new JPanel();
        JPanel controlPanel   = new JPanel();

        infoPanel.setLayout(    new FlowLayout(FlowLayout.CENTER));

        checkBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        buttonPanel.setLayout(  new FlowLayout(FlowLayout.LEFT));

        controlPanel.setLayout( new BorderLayout());

/* ******************** INFO PANEL ****************************/
        infoTA     = new JTextArea(4, 20);
        infoTA.setLineWrap(false);
        infoTA.setEditable(false);
        scrollPane = new JScrollPane(infoTA);

/* ******************** CHECK BOXEN ***************************/


        sendInfoCB = new Checkbox("Send to Database", true); // selected = true
        //sendInfoCB.addActionListener(this);


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

/* ************************ Gather Info Button **************************/

        else if(source == gatherInfoBTN)
        {
            // Put this into a static class method

            infoTA.setText("Personal Directory: "
                + RegQuery.getCurrentUserPersonalFolderPath()
                + "\n");
            infoTA.append("CPU NAME: "
                + RegQuery.getCPUName()
                + "\n");
            infoTA.append("CPU Speed: "
                + RegQuery.getCPUSpeed()
                + " MHz"
                + "\n");
            infoTA.append("Physical Memory: "
                + RegQuery.getMem()
                + " MB"
                + "\n");
            infoTA.append("MAC Address: "
                + RegQuery.getMAC()
                + "\n");
            infoTA.append("IP Address: "
                + RegQuery.getIP()
                + "\n");
            infoTA.append("Mouse: "
                + RegQuery.getMouse()
                + "\n");
            infoTA.append("Keyboard: "
                + RegQuery.getKeyboard()
                + "\n");
            infoTA.append("OS Name: "
                + RegQuery.getOSName()
                + "\n");
System.out.println(RegQuery.getOSName());
            infoTA.append("Product ID: "
                + RegQuery.getProductID()
                + "\n");
            infoTA.append("Registered Owner: "
                + RegQuery.getOwner()
                + "\n");
            infoTA.append("Windows Activation Key: "
                + RegQuery.getProductKey()
                + "\n");
            infoTA.append("Service Pack Number: "
                + RegQuery.getServicePack()
                + "\n");
            infoTA.append("Computer Name: "
                + RegQuery.getComputerName()
                + "\n");
            infoTA.append("Default User Name: "
                + RegQuery.getUserName()
                + "\n");
            infoTA.append("BIOS Version: "
                + RegQuery.getBios()
                + "\n");
            infoTA.append("Modem: "
                + RegQuery.getModem()
                + "\n");
            infoTA.append("Hard Disk 1: "
                + RegQuery.getHDD()
                + "\n");
            infoTA.append("SCSI Disk 1: "
                + RegQuery.getSDD1()
                + "\n");
            infoTA.append("SCSI Disk 2: "
                + RegQuery.getSDD2()
                + "\n");
            infoTA.append("Parallel Port: "
                + RegQuery.getLPT()
                + "\n");
            infoTA.append("Com1 Port: "
                + RegQuery.getCOM1()
                + "\n");
            infoTA.append("Com3 Port: "
                + RegQuery.getCOM3()
                + "\n");
            infoTA.append("1394 Firewire: "
                + RegQuery.get1394()
                + "\n");

            // If they checked the "Send to Database" button
            if(sendInfoCB.getState())
            {
                /* This is a little routine that connects to the PHP end, as 
                 * opposed to the MySQL backend. This will allow PHP to do more 
                 * housekeeping and keep things generally safer and more secure 
                 * all around.
                 *
                 * Otherwise, MySQL would have to allow outside connections
                 * and with the password being contained within this, that has
                 * many potential flaws.
                 *
                 */
                String data = "";
                try 
                {
                    // Just need to do more of these and we've got it.
                    data  = URLEncoder.encode("Enter", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode("Enter", "UTF-8");

                    data += URLEncoder.encode("cpu_type", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(RegQuery.getCPUName(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("cpu_speed", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(RegQuery.getCPUSpeed(), "UTF-8");
                    /*
                    if(RegQuery.getMAC().equals(""))
                    {
                        data += "&" 
                             + URLEncoder.encode("mac", "UTF-8") 
                             + "=" 
                             + URLEncoder.encode(RegQuery.getMAC(), "UTF-8");
                    }
                    else
                    {
                        data += "&"
                             + URLEncoder.encode("mac", "UTF-8")
                             + "="
                             + URLEncoder.encode("null", "UTF-8");
                    }
                    */
                    data += "&" 
                          + URLEncoder.encode("ip", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(RegQuery.getIP(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("mouse", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(RegQuery.getMouse(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("keyboard", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(RegQuery.getKeyboard(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("os", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(RegQuery.getOSName(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("os_sn", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(RegQuery.getProductKey(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("owner", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(RegQuery.getOwner(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("hostname", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(RegQuery.getComputerName(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("modem", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(RegQuery.getModem(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("hd0", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(RegQuery.getHDD(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("lpt1", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(RegQuery.getLPT(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("com1", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(RegQuery.getCOM1(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("ieee_1394", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(RegQuery.get1394(), "UTF-8");
                    // Send data
                    URL                url  = new URL("http://192.168.0.41:80/cdm_data_entry.php");
                    URLConnection      conn = url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    OutputStreamWriter wr   = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();
    
                    // Get the response
                    BufferedReader     rd   = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    @SuppressWarnings("unused")
					String             line = "";
                    while((line = rd.readLine()) != null)
                    {
                        // Process line...
                    }
                    wr.close();
                    rd.close();
                } 
                catch(Exception ex) 
                {
                }
            }
        }
    }
}

