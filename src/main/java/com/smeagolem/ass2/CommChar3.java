package com.smeagolem.ass2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommChar3
{

  static void serverApp(Comms comms) throws Exception
  {
    // This server App acknowledges and outputs if it has certain files.
    // String sendString;
    String receiveString;
    boolean quit = false;
    do
    {
      // Get message
      // receiveString = comms.readString().toString();
      // if (receiveString.isEmpty()) continue;
      ReliableDataTransfer rdt = new ReliableDataTransfer(comms);
      System.out.println("\nWaiting for requests...");
      receiveString = rdt.readData(false);

      if (receiveString == "q") {
        quit = true;
        continue;
      }

      // Send ACK
      // comms.writeString(String.valueOf(Frame.ACK));
      // rdt.writeString(String.valueOf(Frame.ACK));

      // Check if file exists and send message
      File file = new File(receiveString.trim());
      System.out.println("Checking \"" + receiveString.trim() + "\"");
      if (file.isFile())
      {
        // comms.writeString(Frame.SOH + "I have the file! Sending it through now...");
        rdt.writeData(Frame.SOH + "I have the file! Sending it through now...");
        FileReader fileStream = new FileReader(file);
        int c;
        int i = 0;
        // int frameNo = 0;
        char[] buffer = new char[50];
        while ((c = fileStream.read()) != -1) {
          if (i == 50 || c == '\n')
          {
            System.out.println(buffer);
            // String frameString = (new Frame((byte) frameNo, buffer)).toString();
            // System.out.println("Frame " + frameNo + ": " + frameString);
            rdt.writeData(String.valueOf(buffer));
            // do {
              // TODO: implement RDT class that wraps readString and writeString
              // with ACK/NAK confirmation and retransmitting.
              // Use Frame for all communication and parsing.
              // comms.writeString(frameString);
            // } while (!comms.readString().toString().equals(String.valueOf(Frame.ACK)));
            // frameNo = frameNo == 0 ? 1 : 0;
            buffer = new char[50];
            i = 0;
            if (c == '\n') continue;
          }
          buffer[i] = (char) c;
          i++;
        }
        fileStream.close();
        // comms.writeString(String.valueOf(Frame.EOT));
        rdt.writeData(String.valueOf(Frame.EOT));
      }
      else
      {
        // comms.writeString("File not found!");
        rdt.writeData("File not found!");
        rdt.writeData(String.valueOf(Frame.EOT));
      }
    }
    while(!quit);
  }

  static void clientApp(Comms comms) throws Exception
  {
    // This client App asks the server if it has certain files.
    String sendString;
    boolean quit = false;
    do
    {
      ReliableDataTransfer rdt = new ReliableDataTransfer(comms);
      // Get input and send
      System.out.print("\nDo you have file?\n");
      sendString = System.console().readLine();
      // comms.writeString(sendString);

      // don't wait for ack because clientApp never fails to writeString()
      //
      rdt.writeData(sendString, false);
      if (sendString.equals("q")) {
        quit = true;
        continue;
      }

      // Check ACK
      // if (comms.readString().toString().equals(String.valueOf(Frame.ACK) + "\n")) {
      //   System.out.print("Acknowledged\n");
      // }
      // if (rdt.readFrame().dataBlock[0] == Frame.ACK) {
      //   System.out.print("Acknowledged\n");
      // }

      int state = 0;
      // 0 = waiting for SOH
      // 1 = receiving file data
      // 2 = EOT
      while (state != 2) {
        String resData = rdt.readData();
        if (state == 0 && resData.charAt(0) == Frame.SOH) {
          state = 1;
        } else if (resData.equals(String.valueOf(Frame.EOT))) {
          state = 2;
        } else if (state == 1) {
          // TODO: save to file
          System.out.println("File data: " + resData);
        }
      }
    }
    while (!quit);
  }

  public static void start() throws Exception
  {
    Comms comms;
    boolean isServer;  // Server or Client?
    String serverName; // Host name of the server to connect to
    int outOfError;     // Error on transfer per character is 1 : outOfError
    // Create an InputStreamReader for reading characters from byte stream System.in
    InputStreamReader inStreamReader = new InputStreamReader (System.in);
    // Create a BufferedReader for reading entire lines of text from the InputStreamReader
    BufferedReader inputReader = new BufferedReader (inStreamReader);
    // Ask the user if this is the server
    System.out.print ("Is this the server (yes/no)? ");
    if (inputReader.readLine ().equalsIgnoreCase ("yes"))
    {
      isServer = true;
    }
    else
    {
      isServer = false;
    }
    // Ask the user for the host name of the server
    if (isServer )
    {
      System.out.println ("The error rate is 1:N characters. N=0 will produce no errors, 2=50%, 10=10%, 20=5%");
      outOfError = Keyboard.readInt("Please enter N: ");
      System.out.println("\nWaiting for connection with client...");
      serverName = " ";
    }
    else
    {
      System.out.print ("Please enter the host name of server: ");
      serverName = inputReader.readLine ();
      outOfError = 0;
    }
    // Create communications object
    try
    {
      comms = new Comms (serverName, 2569, isServer, 9600, outOfError);
    }
    catch (IOException e)
    {
      System.out.println ("An I/O error occurred.");
      comms = null;
    }
    // Establish connection
    // * If this is the server then comms.connect () waits for a client to
    //   connect.
  // * If this is the client then comms.connect () tries to connect to the
  //   server.
  if ((comms != null) && (comms.connect ()))
  {
    System.out.println ("Connected.");
    if (comms.isServer)
    {
      serverApp(comms);
    }else
    {
      clientApp(comms);
    }
    System.out.print("Transfer complete!");
  }
  else
  {
    System.out.println ("Connection Failed.");
  }

  }
}

