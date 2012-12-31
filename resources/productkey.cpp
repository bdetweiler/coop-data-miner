// To decode the product key, I first translated the Pascal code to C++
// as I was more familiar with that language and just learning Java at the time
// The benefit is, you now also have a C++ version should you need it! -- BRD
#include<iostream>
using namespace std;

char *getProductKey(char*); // Prototype

int main(void)
{
    char *encodedKey = "F0C77C37E5F9A0F5C5BADBE0E8C702";
    char       *decodedKey = "\n";

    decodedKey = getProductKey(encodedKey);
    cout << decodedKey;
    return 0;

}

char *getProductKey(char *encoded_key)
{
    char *fullPID    = "\n";
    
    //################### CONSTANTS ###################
    unsigned int START_OFFSET  = 34;
    unsigned int END_OFFSET    = START_OFFSET + 15; 

    // This little guy holds all the possible characters in the Product Key
    char[] digits        = {'B', 'C', 'D', 'F', 'G', 'H', 'J', 'K', 
                          // 0    1    2    3    4    5    6    7 
                            'M', 'P', 'Q', 'R', 'T', 'V', 'W', 'X',
                          // 8    9    10   11   12   13   14   15 
                            'Y', '2', '3', '4', '6', '7', '8', '9'};
                          // 16   17   18   19   20   21   22   23
    // D_LEN is the length of the decoded product key
    int    D_LEN         = 29;
    // S_LEN is the length of the encoded product key in Bytes - a total of 
    // 30 in chars (remember, 0 through 15 is a total of 16).
    int    S_LEN         = 15;

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
    char[D_LEN]     hexDigitalPID;
    char[D_LEN + 1] des;
    char            *tmpchr;
    int             i             = 0;
    int             n             = 0;
    int             tmp           = 0;
    unsigned int    hn            = 0; // 0xffffffffL & (long)tmp;
    unsigned int    value         = 0; // 0xffffffffL & (long)tmp;

    char           *rval;
    
    int            a              = 1;
    for(i = 0; encodedKey[i] != '\n'; i += 2)
    {
        if(encodedKey[i + 2] != '\n')
        {
            hexDigitalPID[i]     = encodedKey[i];
            hexDigitalPID[i + 1] = encodedKey[i + 1];
                    // Integer.decode("0x" + encodedKey.substring(i, i + 2)).intValue();
        }
// System.out.println(hexDigitalPID[i]);
// System.out.println("After copying value number " + i);
    }
    hexDigitalPID[i + 2] = '\n';
    for(i = D_LEN - 1; i >= 0; --i)
    {
        if(((i + 1) % 6) == 0)
        {
            des[i] = '-';
// System.out.println("if 6 then inserting - ");
        }
        else
        {
// System.out.println("hn = " + hn);
            // int value = 0;
            for(n = S_LEN - 1; n > 0; --n)
            {
value
System.out.println("S_LEN = " + S_LEN);
// System.out.println("Main algorithm");
                //################### MAIN ALGORITHM ###################
// System.out.println("Before Shift = " + Long.toBinaryString(value));
                    
System.out.println("hn = "                + Integer.toBinaryString(hn));
System.out.println("Hex Digital PID = "   + Integer.toBinaryString(hexDigitalPID[n]));
System.out.println("hn shifted left 8 = " + Integer.toBinaryString(hn << 8));
                // value            = (int)((hn << 8) + hexDigitalPID[n]);
                hn            = ((hn << 8) + hexDigitalPID[n]); 
System.out.println("hn shifted left 8 and ORd with hexDigPID = " + Integer.toBinaryString(value));

// System.out.println("Hex Digital PID = " + Long.toBinaryString(hexDigitalPID[n]));
// System.out.println("After  Shift = " + Long.toBinaryString(value));
                // hexDigitalPID[n] = (int)(value     / 24);
                hexDigitalPID[n] = (hn     / 24);
// System.out.println("hexDigitalPID = " + hexDigitalPID[n]);
                // hn               = (int )(value    % 24);
                hn               = (hn    % 24);
                // value               = (int )(value    % 24);
// System.out.println("hn = " + hn);
            }
            // Now use hn to get the ascii value from the list
            // Have to convert the long to an int to use as the index for des[]
            // Long duckDong = new Long(hn);
// System.out.println(digits[duckDong.intValue()] + " = " + duckDong);
            des[i] = digits[hn]; // duckDong.intValue()];
        }
    }
    des[D_LEN] = '\n';
    for(i = 0; des[i] != '\n' ; ++i)
    {
        rval += des[i];
    }
    return rval;
}
