/* ***************** RegQuery.java **************************************
 * File:        RegQuery.java                                           *
 * Author:      Brian Detweiler                                         *
 * Company:     Aurora Cooperative                                      *
 * Description: This does all the dirty work for the Coop Data Miner.   *
 *              It queries the registry and does whatever else it needs *
 *              to do to get the required data.                         *
 ***********************************************************************/

import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class RegQuery
{

    private static final String MEM_QUERY        = "mem";
    private static final String IP_CONFIG_QUERY  = "ipconfig /all";
    private static final String REGQUERY_UTIL    = "reg query ";
    private static final String REGSTR_TOKEN     = "REG_SZ";
    private static final String REGDWORD_TOKEN   = "REG_DWORD";
    private static final String REGBINARY_TOKEN  = "REG_BINARY";
    private static final String REGMULTISZ_TOKEN = "REG_MULTI_SZ";

    private static final String PERSONAL_FOLDER_CMD     = REGQUERY_UTIL +
        "\"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v Personal";
    private static final String CPU_SPEED_CMD           = REGQUERY_UTIL +
        "\"HKLM\\HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\" /v ~MHz";
    private static final String CPU_NAME_CMD            = REGQUERY_UTIL +
        "\"HKLM\\HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\" /v ProcessorNameString";
    private static final String MOUSE_NAME_CMD          = REGQUERY_UTIL +
        "\"HKLM\\HARDWARE\\DESCRIPTION\\System\\"                       +
        "MultifunctionAdapter\\5\\PointerController\\0\\PointerPeripheral\\0\" /v Identifier";
    // This may not be found in the same place on another system.
    private static final String KEYBOARD_CMD            = REGQUERY_UTIL +
        "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\WOW\\boot.description\" /v keyboard.typ";
    //private static final String COMPUTER_NAME_CMD       = REGQUERY_UTIL +
        //"\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon\" /v DefaultDomainName";
    private static final String COMPUTER_NAME_CMD       = REGQUERY_UTIL +
        "\"HKLM\\SYSTEM\\ControlSet001\\Control\\ComputerName\\ActiveComputerName\" /v ComputerName";
    private static final String DEFAULT_USER_NAME_CMD   = REGQUERY_UTIL +
        "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon\" /v DefaultUserName";
    private static final String OS_NAME_CMD             = REGQUERY_UTIL +
        "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\" /v ProductName";
    private static final String PRODUCT_ID_CMD          = REGQUERY_UTIL +
        "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\" /v ProductId";
    private static final String OWNER_CMD               = REGQUERY_UTIL +
        "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\" /v RegisteredOwner";
    private static final String SERVICE_PACK_CMD        = REGQUERY_UTIL +
        "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\" /v CSDVersion";
    private static final String INSTALL_DATE_CMD        = REGQUERY_UTIL +
        "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\" /v InstallDate";      // DWord
    private static final String PRODUCT_KEY_CMD         = REGQUERY_UTIL +
        "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\" /v DigitalProductId"; // DWord
    private static final String BIOS_CMD                = REGQUERY_UTIL +
        "\"HKLM\\HARDWARE\\DESCRIPTION\\System\" /v SystemBiosVersion";
    private static final String MODEM_CMD               = REGQUERY_UTIL +
        "\"HKLM\\SYSTEM\\ControlSet001\\Services\\winachsf\" /v FriendlyName";
    private static final String HDD_CMD                 = REGQUERY_UTIL +
        "\"HKEY_LOCAL_MACHINE\\SYSTEM\\ControlSet001\\Services\\Disk\\Enum\" /v 0";
    private static final String SDD1_CMD                = REGQUERY_UTIL +
        "\"HKLM\\HARDWARE\\DEVICEMAP\\Scsi\\Scsi Port 0\\Scsi Bus 0\\Target Id 0\\Logical Unit Id 0\" /v Identifier";
    private static final String SDD2_CMD                = REGQUERY_UTIL +
        "\"HKLM\\HARDWARE\\DEVICEMAP\\Scsi\\Scsi Port 1\\Scsi Bus 0\\Target Id 0\\Logical Unit Id 0\" /v Identifier";
    private static final String LPT_CMD                 = REGQUERY_UTIL +
        "\"HKLM\\HARDWARE\\DEVICEMAP\\PARALLEL PORTS\" /v \\Device\\Parallel0";
    private static final String COM1_CMD                = REGQUERY_UTIL +
        "\"HKEY_LOCAL_MACHINE\\HARDWARE\\DEVICEMAP\\SERIALCOMM\" /v \\Device\\Serial0";
    private static final String COM3_CMD                = REGQUERY_UTIL +
        "\"HKEY_LOCAL_MACHINE\\HARDWARE\\DEVICEMAP\\SERIALCOMM\" /v Winachsf0";
    private static final String FIREWIRE_CMD            = REGQUERY_UTIL +
        "\"HKLM\\SYSTEM\\ControlSet002\\Services\\NIC1394\" /v DisplayName";
    //  HKLM\SOFTWARE\Microsoft\Windows\CurrentVersion\Reinstall\0000 holds PCI information. // NOT SURE ABOUT THIS
    //HKLM\HARDWARE\DEVICEMAP\Scsi\Scsi Port 0\Scsi Bus 0\Target Id 0\Logical Unit Id 0 /v Identifier regsz
    //private static final String SERVICE_PACK_CMD        = REGQUERY_UTIL +
    //    "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\NetworkCards\\###" /v Description";

    /* Name:        queryRegistry(String cmd, String token)
     * Input:       String, String
     * Output:      None
     * Return:      
     * Notes:       Query the registry
     */
    public static String queryRegistry(String cmd, String token)
    {
        /* Trying to make one big place to add reg queries.
         * Remember? Single point of failure. Right here. The rest
         * will either just return the string, or will parse the string
         * then return it.
         */
        try
        {
            Process      process = Runtime.getRuntime().exec(cmd);
            StreamReader reader  = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();
            int    p      = result.indexOf(token);

            if (p == -1)
            {
                return null;
            }
            //System.out.println(result.substring(p + token.length()).trim());
            return result.substring(p + token.length()).trim();
        }
        catch (Exception e)
        {
            System.out.println("Query of Registry failed.");
            return null;
        }
    }

    /* Name:        getCurrentUserPersonalFolderPath()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Returns the current user's Personal Folder $PATH
     *              e.g. "C:\Documents and Settings\%user%\My Documents"
     */
    public static String getCurrentUserPersonalFolderPath()
    {
        String rval = "null";
        if(queryRegistry(PERSONAL_FOLDER_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(PERSONAL_FOLDER_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    /* Name:        getCPUName()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Returns the CPU name as a String. 
     *              e.g. "mobile Athlon(tm) XP2200+"
     */
    public static String getCPUName()
    {
        String rval = "null";
        if(queryRegistry(CPU_NAME_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(CPU_NAME_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    /* Name:        getCPUSpeed()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Returns the CPU speed as a String in MHz.
     *              e.g. "1788 MHz"
     */
    public static String getCPUSpeed()
    {
        String temp = queryRegistry(CPU_SPEED_CMD, REGDWORD_TOKEN);
        try
        {
            // CPU speed in Mhz (minus 1) in HEX notation, convert it to DEC
            String rval = "null";
            if(queryRegistry(CPU_NAME_CMD, REGSTR_TOKEN) != null)
            {
                rval = Integer.toString((Integer.parseInt(temp.substring("0x".length()), 16) + 1));
            }
            return rval;
        }
        catch (Exception e)
        {
            return "null";
        }
    }

    /* Name:        getMAC()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Returns the MAC address of the ethernet card
     *              e.g. "00-0B-CD-53-B6-94"
     */
    public static String getMAC()
    {
        try
        {
            // Note: Will have to find for multiple Network Interfaces.
            // Need to find what uses this.

            // private final static boolean windowsIsMacAddress(String macAddressCandidate) {
            Process process     = Runtime.getRuntime().exec(IP_CONFIG_QUERY);
            StreamReader reader = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result       = reader.getResult();
            String rval         = result;

            Pattern macPattern  = Pattern.compile(
                            "[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0 -9a-fA-F]{2}-[0-9a-fA-F]{2}");
            Matcher m           = macPattern.matcher(result);
            // if(m.matches())
            // {
                rval = m.group();
            // }

            // result              = result.substring(510, 528);
            return rval;
        }
        catch(Exception e)
        {
            return "null";
        }
    }

    /* Name:        getIP()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Returns the IP address of the ethernet card
     *              e.g. "192.168.0.106"
     */
    public static String getIP()
    {
            /* This will need a device to actually be up. 
             * Otherwise, it grabs the 127.0.0.1 address. For our purposes,
             * this will not be a problem since they will need an active
             * connection to send info to the database.
             */ 
        String rval = "null";
        try
        {
            InetAddress local = InetAddress.getLocalHost();
            rval              = "" + local.getHostAddress();
        }
        catch(UnknownHostException uhe)
        {
            rval              = "null";
        }
            
        return rval;
    }

    /* Name:        getMem()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Returns the Physical Memory of the machine
     *              e.g. "655360 MB"
     */
    public static String getMem()
    {
        try
        {
            // Process process     = Runtime.getRuntime().exec(MEM_QUERY);
            // StreamReader reader = new StreamReader(process.getInputStream());

            // reader.start();
            // process.waitFor();
            // reader.join();

            /* Executes "mem" in a shell */
            // String rval = reader.getResult();
            /* Strips out just the total conventional memory */
            // rval        = result.substring(8, 14);
            // return rval;
            return "null";
        }
        catch(Exception e)
        {
            return "null";
        }
    }

    /* Name:        getMouse()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the mouse attached to the machine using a Reg Query
     *              e.g. "MICROSOFT PS2 MOUSE"
     */
    public static String getMouse()
    {
        String rval = "null";
        if(queryRegistry(MOUSE_NAME_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(MOUSE_NAME_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    /* Name:        getMouse()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the mouse attached to the machine using a Reg Query
     *              e.g. "MICROSOFT PS2 MOUSE"
     */
    public static String getKeyboard()
    {
        String rval = "null";
        if(queryRegistry(KEYBOARD_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(KEYBOARD_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    /* Name:        getOSName()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the Operating System name
     *              e.g. "Microsoft Windows XP"
     */
    public static String getOSName()
    {
        String rval = "null";
        if(queryRegistry(OS_NAME_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(OS_NAME_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    /* Name:        getProductID()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the product ID string
     *              e.g. "55277-OEM-0046542-62264"
     */
    public static String getProductID()
    {
        String rval = "null";
        if(queryRegistry(PRODUCT_ID_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(PRODUCT_ID_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    /* Name:        getOwner()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the Registered Owner
     *              e.g. "Brian Detweiler"
     */
    public static String getOwner()
    {
        String rval = "null";
        if(queryRegistry(OWNER_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(OWNER_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    /* Name:        getServicePack()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the Windows Service Pack number of the OS
     *              e.g. "Service Pack 1"
     */
    public static String getServicePack()
    {
        String rval = "null";
        if(queryRegistry(SERVICE_PACK_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(SERVICE_PACK_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    /* Name:        getProductKey()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the Product Key of the OS. 
     *              This is a tough one. Microsoft encrypts the product key with  
     *              something like a 24-bit XOR.
     */
    public static String getProductKey()
    {
        String       fullPID       = "";
        String       encodedKey    = "";
        if(queryRegistry(PRODUCT_KEY_CMD, REGBINARY_TOKEN) != null)
        {
            fullPID = queryRegistry(PRODUCT_KEY_CMD, REGBINARY_TOKEN);
        }
       
        // This is what we have to work with:
        encodedKey                     = fullPID.substring(104, 134);
System.out.println(encodedKey);
        //################### CONSTANTS ###################
        // This little guy holds all the possible characters in the Product Key
        final char[] digits       = {'B', 'C', 'D', 'F', 'G', 'H', 'J', 'K', 
                                   // 0    1    2    3    4    5    6    7 
                                     'M', 'P', 'Q', 'R', 'T', 'V', 'W', 'X',
                                   // 8    9    10   11   12   13   14   15 
                                     'Y', '2', '3', '4', '6', '7', '8', '9'};
                                   // 16   17   18   19   20   21   22   23
        // D_LEN is the length of the decoded product key
        final int   D_LEN         = 29;
        // S_LEN is the length of the encoded product key in Bytes - a total of 
        // 30 in chars (remember, 0 through 15 is a total of 16).
        final int   S_LEN         = 15;

        /* The longs were originally of type 'cardinal' in Pascal, which
         * is a fancy term for unsigned int. Java doesn't do unsigned.
         * My solution? Promote and Pray.
         *
         * A note from http://mindprod.com/jgloss/unsigned.html :
         * "In Java bytes, shorts, ints and longs are all considered signed. 
         * The only unsigned type is the 16-bit char. To use the sign bit as 
         * an additional data bit you have to promote to the next bigger data 
         * type with sign extension then mask off the high order bits.
         * To get the effect of a 32-bit unsigned:
         * ---------------------------
         * int i;                    |
         * // ...                    |
         * long l = i & 0xffffffffL; |
         * ---------------------------
         */
        int[]       hexDigitalPID = new int[D_LEN];
        char[]      des           = new char[D_LEN + 1];
        int         i             = 0;
        int         n             = 0;
        int         tmp           = 0;
        int         hn            = 0; // 0xffffffffL & (long)tmp;
        int         value         = 0; // 0xffffffffL & (long)tmp;

        String      rval          = "";
        
        for(i = 0; i <= 14; ++i)
        {
                hexDigitalPID[i] = Integer.decode("0x" + encodedKey.substring(i * 2, (i * 2) + 2)).intValue();
        }
        for(i = 0; i <= 14; ++i)
        {
            System.out.println("hexDigitalPID[" + i + "] = " + hexDigitalPID[i]);
        }
//########################## BEGIN MAIN ALGORITHM ###############################
        for(i = D_LEN - 1; i >= 0; --i)
        {
            if(((i + 1) % 6) == 0)
            {
                des[i] = '-';
            }
            else
            {
                hn     = 0;
                for(n = S_LEN - 1; n >= 0; --n)
                {
                    hn               = ((hn << 8) + hexDigitalPID[n]);
                    hexDigitalPID[n] = (hn        / 24);
                    hn               = (hn        % 24);
                }
                // Now use hn to get the ascii value from the list
                des[i] = digits[hn]; 
            }
        }
//########################## END MAIN ALGORITHM ###############################
        des[D_LEN] = '\n';
        for(i = 0; des[i] != '\n' ; ++i)
        {
            rval += des[i];
        }
        return rval;
    }



    /* Name:        getComputerName()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the NetBIOS name of the computer
     *              e.g. "Brian's Laptop"
     */
    public static String getComputerName()
    {
        String rval = "null";
        if(queryRegistry(COMPUTER_NAME_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(COMPUTER_NAME_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    /* Name:        getUserName()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the User Name of current logged on user
     *              e.g. "Brian"
     */
    public static String getUserName()
    {
        String rval = "null";
        if(queryRegistry(DEFAULT_USER_NAME_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(DEFAULT_USER_NAME_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    /* Name:        getBios()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the BIOS of the computer
     *              e.g. "PTLTD  - 6040000\0PhoenixBIOS 4.0 Release 6.0" +
     *                   "\0Ver 1.00PARTTBL\0\0"
     */
    public static String getBios()
    {
        String rval = "null";
        if(queryRegistry(BIOS_CMD, REGMULTISZ_TOKEN) != null)
        {
            rval = queryRegistry(BIOS_CMD, REGMULTISZ_TOKEN);
        }
        return rval;
    }

    /* Name:        getModem()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the Modem device of the machine
     *              e.g. "Conexant 56K ACLink Modem"
     */
    public static String getModem()
    {
        String rval = "null";
        if(queryRegistry(MODEM_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(MODEM_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    /* Name:        getHDD()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the hard disk device 
     *              e.g. "IDE\DiskIC25N040ATCS04-0________________________" +
     *                   "CA4OA71A\5&37cd7add&0&0.0.0"
     */
    public static String getHDD()
    {
        String rval = "null";
        if(queryRegistry(HDD_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(HDD_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    // TODO: May need a getHDD1() and getHDD2() methods

    /* Name:        getSDD1()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the first SCSI device
     *              e.g. "IC25N040ATCS04-0"
     */
    public static String getSDD1()
    {
        String rval = "null";
        if(queryRegistry(SDD1_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(SDD1_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    /* Name:        getSDD2()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the second SCSI device
     *              e.g. "TOSHIBA DVD-ROM SD-R2312"
     */
    public static String getSDD2()
    {
        String rval = "null";
        if(queryRegistry(SDD2_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(SDD2_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    /* Name:        getServicePack()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the LPT device
     *              e.g. "\DosDevices\LPT1"
     */
    public static String getLPT()
    {
        String rval = "null";
        if(queryRegistry(LPT_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(LPT_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    /* Name:        getCOM1()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the COM1 device
     *              e.g. "COM1"
     */
    public static String getCOM1()
    {
        String rval = "null";
        if(queryRegistry(COM1_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(COM1_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    /* Name:        getCOM3()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the COM3 device
     *              e.g. "COM3"
     */
    public static String getCOM3()
    {
        String rval = "null";
        if(queryRegistry(COM3_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(COM3_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    /* Name:        get1394()
     * Input:       None
     * Output:      None
     * Return:      String
     * Notes:       Get the firewire device
     *              e.g. "1394 Net Driver"
     */
    public static String get1394()
    {
        String rval = "null";
        if(queryRegistry(FIREWIRE_CMD, REGSTR_TOKEN) != null)
        {
            rval = queryRegistry(FIREWIRE_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    // END OF "GET" METHODS

    /* Class Name:        class StreamReader()
     * Notes:             Reads the registry.
     */
    static class StreamReader extends Thread
    {
        private InputStream is;
        private StringWriter sw;

        StreamReader(InputStream is)
        {
            this.is = is;
            sw = new StringWriter();
        }

        public void run()
        {
            try
            {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);
            }
            catch (IOException e)
            {;;;} // do nothing
        }

        String getResult()
        {
            return sw.toString();
        }
    }
}
