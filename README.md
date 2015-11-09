# coop-data-miner
Automatically exported from code.google.com/p/coop-data-miner

Written for a farming cooperative back in 2005, I needed a way to inventory all the Windows PCs on the network. The most significant piece of this puzzle was decoding the Windows product key. At the time, the only known implementation was written in Pascal, so I translated it to Java.

The Java piece runs on each individual PC, and "phones home" to a RESTful webservice (not included).

This is for historical purposes, and for anyone who needs an example of mining the Windows Registry and decoding the product key for Windows XP and earlier. Not tested on newer versions.
