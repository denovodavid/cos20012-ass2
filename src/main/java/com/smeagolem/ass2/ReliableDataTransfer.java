package com.smeagolem.ass2;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * ReliableDataTransfer
 */
public class ReliableDataTransfer {

  private final Comms comms;
  private byte frameNo = 0;
  private byte lastReadFrameNo = -1;
  private final ExecutorService executor;
  private final Callable<String> readStringTask;

  public ReliableDataTransfer(final Comms comms) {
    this.comms = comms;
    executor = Executors.newCachedThreadPool();
    readStringTask = new Callable<String>() {
      public String call() throws Exception {
        System.out.println("Read String!");
        return comms.readString().toString();
      }
    };
  }

  public Frame readFrame(boolean sendAck) throws Exception {
    Frame frame;
    while (true) {
      String frameString = comms.readString().toString();

      // System.out.println("ReadFrame: " + frameString);

      // for (int i = 0, n = frameString.length(); i < n; i++) {
      //   System.out.println("Char " + i + ": " + (int) frameString.charAt(i));
      // }

      // remove line end
      frameString = frameString.substring(0, frameString.length() - 1);

      // System.out.println("ReadFrameLn: " + frameString);

      // return parsed frame
      frame = Frame.parse(frameString);

      // System.out.println("FRAMEWORK: " + frameString.equals(frame.toString()));
      // System.out.println("FCS: " + frame.fcs);
      // System.out.println("CHK: " + frame.calulateChecksum());
      // System.out.println("isCorrupt: " + frame.isCorrupt());
      // String testy = String.valueOf(frame.dataBlock);
      // for (int i = 0, n = testy.length(); i < n; i++) {
      //   System.out.println("Char " + i + ": " + (int) testy.charAt(i));
      // }

      // System.out.println(frame);

      // System.out.println(frame.getData());

      if (frame.isCorrupt()) System.out.println("CORRUPT");

      if (sendAck)
        sendAck(frame);



      if (frame.frameNo != lastReadFrameNo && !frame.isCorrupt())
        break;
    }

    lastReadFrameNo = frame.frameNo;
    return frame;
  }

  public void sendAck(Frame frame) throws Exception {
    char[] data = { Frame.ACK, (char) frame.calulateChecksum() };
    Frame ack = new Frame(frame.frameNo, data);
    String frameString = ack.toString();
    // System.out.println("isAck: " + ack.isAck(frame.frameNo, frame.fcs));
    // System.out.println("isCorrupt: " + ack.isCorrupt());
    // System.out.println("Send Ack: " + frame.frameNo);
    // for (int i = 0, n = frameString.length(); i < n; i++) {
    //   System.out.println("AckChar " + i + ": " + (int) frameString.charAt(i));
    // }
    comms.writeString(frameString);
  }

  public String readData() throws Exception {
    return readData(true);
  }

  public String readData(boolean sendAck) throws Exception {
    return readFrame(sendAck).getData();
  }

  // public Frame writeFrame(Frame frame) throws Exception {
  //   Frame res;

  //   System.out.println("Data: " + frame.getData());

  //   // Frame frame = new Frame(frameNo, data.toCharArray());
  //   String frameString = frame.toString();

  //   System.out.println("Frame: " + frameString);

  //   // https://stackoverflow.com/questions/1164301/how-do-i-call-some-blocking-method-with-a-timeout-in-java
  //   ExecutorService executor = Executors.newCachedThreadPool();
  //   Callable<Frame> getAckTask = new Callable<Frame>() {
  //     public Frame call() throws Exception {
  //       return readFrame(false);
  //     }
  //   };
  //   while (true) {
  //     comms.writeString(frameString);
  //     System.out.println("Sent and waiting for response " + frameNo + "...");
  //     // Frame thing = readFrame(false);
  //     // System.out.println("ACK: " + (int) thing.getData().charAt(0));
  //     // System.out.println("isAck: " + thing.isAck(frameNo));
  //     // System.out.println("isCorrupt: " + thing.isCorrupt());
  //     // break;
  //     Future<Frame> futureAck = executor.submit(getAckTask);
  //     try {
  //       res = futureAck.get(4, TimeUnit.SECONDS);
  //       break;
  //     } catch (Exception e) {
  //       // continue;
  //     } finally {
  //       futureAck.cancel(true); // may or may not desire this
  //     }
  //   }
  //   frameNo = frameNo == 0 ? (byte) 1 : 0;
  //   return res;
  // }

  public void writeData(String data) throws Exception {
    writeData(data, true);
  }

  public void writeData(String data, boolean reqAck) throws Exception {
    // write string
    // comms.writeString(frame.toString());
    // wait for ACK
    // String ack = comms.readString().toString();
    // if NAK or timout, send again

    // System.out.println(data);

    // remove any line breaks, because it messes up comms >:(
    data = data.replace('\n', ' ');

    // System.out.println("Data: " + data);

    Frame frame = new Frame(frameNo, data.toCharArray());
    String frameString = frame.toString();

    // System.out.println("Frame: " + frameString);

    // System.out.println("FCS: " + frame.fcs);
    // System.out.println("CHK: " + frame.calulateChecksum());

    // String testy = String.valueOf(frame.dataBlock);
    // for (int i = 0, n = testy.length(); i < n; i++) {
    //   System.out.println("Char " + i + ": " + (int) testy.charAt(i));
    // }

    if (reqAck) {
      myLoop: while (true) {
        comms.writeString(frameString);
        System.out.println("Sent and waiting for response " + frameNo + "...");

        long endTime = System.currentTimeMillis() + 50;
        while (true) {
          if (comms.available()) {
            String ackString = comms.readString().toString();
            // System.out.println("ACK READ!");

            // System.out.println("ReadFrame: " + frameString);

            // for (int i = 0, n = ackString.length(); i < n; i++) {
            //   System.out.println("AckChar " + i + ": " + (int) ackString.charAt(i));
            // }

            // remove line end
            ackString = ackString.substring(0, ackString.length() - 1);

            // System.out.println("ReadFrameLn: " + frameString);

            // return parsed frame
            Frame ack = Frame.parse(ackString);
            // System.out.println("isAck: " + ack.isAck(frameNo, frame.fcs));
            // System.out.println("isCorrupt: " + ack.isCorrupt());
            if (ack.isAck(frameNo, frame.fcs) && !ack.isCorrupt()) {
              // System.out.println("CONFIRMED ACK");
              break myLoop;
            }
          }
          if (System.currentTimeMillis() > endTime) {
            System.out.println("TIMEOUT");
            break;
          }
        }
      }
      frameNo = frameNo == 0 ? (byte) 1 : 0;
    } else {
      comms.writeString(frameString);
    }
  }
}