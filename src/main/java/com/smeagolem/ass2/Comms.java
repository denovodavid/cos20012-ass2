package com.smeagolem.ass2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Comms
{

    boolean isServer;
    String serverHostName;
    int portNumber;
    int outOfError;
    long baudRate;
    long lastTransferTime;

    final double PROB_ERROR = 0.1;

    ServerSocket serverSocket;
    Socket socket;
    InputStream inStream;
    OutputStream outStream;

    public Comms (String serverHostName, int portNumber, boolean isServer, int baudRate, int outOfError) throws IOException
    {
        this.serverHostName = serverHostName;
        this.portNumber = portNumber;
        this.isServer = isServer;
        this.baudRate = baudRate;
        this.outOfError= outOfError;

        if (isServer)
        {
            serverSocket = new ServerSocket (portNumber);
        }
        else
        {
        }
    }

    public boolean connect ()
    {
        try
        {
            if (isServer)
            {
                serverSocket.setSoTimeout (0);
                socket = serverSocket.accept ();
            }
            else
            {
                socket = new Socket (serverHostName, portNumber);
            }
            inStream = socket.getInputStream ();
            outStream = socket.getOutputStream ();
        }
        catch (SocketException e)
        {
            return false;
        }
        catch (IOException e)
        {
            return false;
        }

        lastTransferTime = System.currentTimeMillis ();
        return true;
    }

    protected boolean timeForNextTransfer ()
    {
        if ((baudRate > 0) &&
            (System.currentTimeMillis () - lastTransferTime < (1000 / baudRate / 9) ))
            return false;
        else
            return true;
    }

    public boolean available () throws IOException
    {
        if (! timeForNextTransfer ())
            return false;
        else
            return (inStream.available () > 0);
    }

    public int read () throws IOException
    {
        int received;

        // while (! timeForNextTransfer ()) { }
        while (inStream.available () <= 0) { }

        lastTransferTime = System.currentTimeMillis ();
        received = inStream.read ();

        return received;
    }
    public int unrelRead () throws IOException
    {
        int received;

        while (! timeForNextTransfer ()) { }

        lastTransferTime = System.currentTimeMillis ();
        received = inStream.read ();
        if ( (int)(Math.random()*outOfError) == 1 &&  received !='q')
            {
               return 35;// # means dropped char
            }
            else
            {
               return received;
            }
    }

   public StringBuffer readString() throws IOException
    {
            // int i = 0;
            int inInt;
            StringBuffer stringRead = new StringBuffer(255) ;
            do
            {
                inInt=read();
                stringRead.append((char)inInt);
                // i++;
             } while ( inInt != 10) ;
             System.out.println("stringRead.length: " + stringRead.length());
             return stringRead;
     }


    public void write (int value ) throws IOException
    {
        //   while (! timeForNextTransfer ()) { }
        if ( (int)(Math.random()*outOfError) == 1 )
            value = 35;
        outStream.write (value);
        lastTransferTime = System.currentTimeMillis ();
    }

   public void writeString(String stringvalue) throws IOException
    {
        if ( Math.random() < PROB_ERROR )
        {}
        else
        {
            // System.out.println("StringLength: " + stringvalue.length());
            for (int i = 0; i < stringvalue.length(); i++)
            {
                write(stringvalue.charAt(i));
                // System.out.println("CHAR " + i + ": " + (int) stringvalue.charAt(i));
            }
            write('\n');
            outStream.flush();
        }
    }

    public void close() throws IOException
    {
        outStream.close();
    }

}

