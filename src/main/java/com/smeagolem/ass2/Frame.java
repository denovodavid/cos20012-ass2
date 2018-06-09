package com.smeagolem.ass2;

import java.util.Arrays;

public class Frame
{
    char startFlag = STX; // 2 bytes
    byte frameNo = 0; // 1 byte
    char[] dataBlock = new char[50]; // 100 bytes
    byte fcs = 0; // 1 byte
    char endFlag = ETX; // 2 bytes

    static final char SOH = (char) 1;
    static final char STX = (char) 2;
    static final char ETX = (char) 3;
    static final char EOT = (char) 4;
    static final char ACK = (char) 6;
    static final char NAK = (char) 21;

    public Frame() {
        Arrays.fill(dataBlock, ETX);
    }

    public Frame(byte frameNo, char[] dataBlock)
    {
        this.frameNo = frameNo;
        this.dataBlock = Arrays.copyOfRange(dataBlock, 0, 50);
        for (int i = 49; i > dataBlock.length - 1; --i) {
            this.dataBlock[i] = ETX;
        }
        fcs = calulateChecksum();
        // System.out.println("FrameDataLength: " + dataBlock.length);
    }

    public static Frame parse (String frameString)
    {
        Frame frame = new Frame();
        char[] frameArray = frameString.toCharArray();
        try {
            if (frameArray.length != 54) {
                throw new IllegalArgumentException("String incorrect length");
            }
            frame.startFlag = frameArray[0];
            frame.frameNo = (byte) frameArray[1];
            frame.dataBlock = Arrays.copyOfRange(frameArray, 2, 52);
            frame.fcs = (byte) frameArray[52];
            frame.endFlag = frameArray[53];
        } catch (Exception e) {
            System.err.println("ERROR: Failed to parse string as frame.");
            System.err.println(e);
        }
        return frame;
    }

    public byte calulateChecksum ()
    {
        int total = startFlag;
        for (char character : getData().toCharArray()) {
            total += character;
        }
        byte checksum = (byte) ((total + endFlag) % 128);
        // Can't have checksum of 10 because writeString uses line-breaks >:(
        if (checksum == (byte) 10) {
            checksum = (byte) 11;
        }
        return checksum;
    }

    public String getData() {
        return String.valueOf(dataBlock).replace(String.valueOf(ETX), "");
        // return dataBlock.toString();
    }

    public boolean isAck(byte frameNo, byte fcs) {
        // System.out.println("AckLength: " + getData().length());
        return this.frameNo == frameNo &&
                getData().length() == 2 &&
                getData().charAt(0) == ACK &&
                getData().charAt(1) == (char) fcs;
            // getData().equals(String.valueOf(new char[]{ ACK, (char) fcs }));
    }

    public boolean isCorrupt() {
        return fcs != calulateChecksum();
    }

    public String toString ()
    {
        // System.out.println("dataBlock.length: " + dataBlock.length);
        StringBuilder sb = new StringBuilder(54);
        sb.append(startFlag);
        sb.append((char) frameNo);
        sb.append(dataBlock);
        for (int i = 49; i > dataBlock.length - 1; --i) {
            sb.append(ETX);
        }
        sb.append((char) fcs);
        sb.append(endFlag);
        return sb.toString();
    }

    public boolean equals(Frame frame) {
        return startFlag == frame.startFlag &&
            frameNo == frame.frameNo &&
            fcs == frame.fcs &&
            endFlag == frame.endFlag &&
            Arrays.equals(dataBlock, frame.dataBlock);
    }
}