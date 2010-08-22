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


import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.text.*;
import java.lang.*;
import java.util.*;

// URL stuff here
import java.net.*;
import java.io.*;

public class cdm
{
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

            RegQuery query = new RegQuery();
            infoTA.setText("Personal Directory: "
                + query.getCurrentUserPersonalFolderPath()
                + "\n");
            infoTA.append("CPU NAME: "
                + query.getCPUName()
                + "\n");
            infoTA.append("CPU Speed: "
                + query.getCPUSpeed()
                + " MHz"
                + "\n");
            infoTA.append("Physical Memory: "
                + query.getMem()
                + " MB"
                + "\n");
            infoTA.append("MAC Address: "
                + query.getMAC()
                + "\n");
            infoTA.append("IP Address: "
                + query.getIP()
                + "\n");
            infoTA.append("Mouse: "
                + query.getMouse()
                + "\n");
            infoTA.append("Keyboard: "
                + query.getKeyboard()
                + "\n");
            infoTA.append("OS Name: "
                + query.getOSName()
                + "\n");
System.out.println(query.getOSName());
            infoTA.append("Product ID: "
                + query.getProductID()
                + "\n");
            infoTA.append("Registered Owner: "
                + query.getOwner()
                + "\n");
            infoTA.append("Windows Activation Key: "
                + query.getProductKey()
                + "\n");
            infoTA.append("Service Pack Number: "
                + query.getServicePack()
                + "\n");
            infoTA.append("Computer Name: "
                + query.getComputerName()
                + "\n");
            infoTA.append("Default User Name: "
                + query.getUserName()
                + "\n");
            infoTA.append("BIOS Version: "
                + query.getBios()
                + "\n");
            infoTA.append("Modem: "
                + query.getModem()
                + "\n");
            infoTA.append("Hard Disk 1: "
                + query.getHDD()
                + "\n");
            infoTA.append("SCSI Disk 1: "
                + query.getSDD1()
                + "\n");
            infoTA.append("SCSI Disk 2: "
                + query.getSDD2()
                + "\n");
            infoTA.append("Parallel Port: "
                + query.getLPT()
                + "\n");
            infoTA.append("Com1 Port: "
                + query.getCOM1()
                + "\n");
            infoTA.append("Com3 Port: "
                + query.getCOM3()
                + "\n");
            infoTA.append("1394 Firewire: "
                + query.get1394()
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
                          + URLEncoder.encode(query.getCPUName(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("cpu_speed", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(query.getCPUSpeed(), "UTF-8");
                    /*
                    if(query.getMAC().equals(""))
                    {
                        data += "&" 
                             + URLEncoder.encode("mac", "UTF-8") 
                             + "=" 
                             + URLEncoder.encode(query.getMAC(), "UTF-8");
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
                          + URLEncoder.encode(query.getIP(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("mouse", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(query.getMouse(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("keyboard", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(query.getKeyboard(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("os", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(query.getOSName(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("os_sn", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(query.getProductKey(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("owner", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(query.getOwner(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("hostname", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(query.getComputerName(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("modem", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(query.getModem(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("hd0", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(query.getHDD(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("lpt1", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(query.getLPT(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("com1", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(query.getCOM1(), "UTF-8");
                    data += "&" 
                          + URLEncoder.encode("ieee_1394", "UTF-8") 
                          + "=" 
                          + URLEncoder.encode(query.get1394(), "UTF-8");
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

