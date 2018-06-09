package com.smeagolem.ass2;

// Keyboard - School of IT, Swinburne University, Keyboard class definition

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Keyboard methods to read numbers and strings from standard input (usually
 * KEYBOARD) with retry when invalid input.  The class has methods which read
 * things from the keyboard terminated by Enter key. Most of the methods of
 * this class have two versions, one with a prompt parameter and the other
 * without.  The latter versions have a default prompt, except for
 * readString().
 * <p>(version 1.7) limits number of retries, readString terminates on -1.
 * <br>(version 1.8) modifies readString() so that it handles CRLF ('\r''\n')
 *               for JDK 1.1.4 - abo - 25 November 1997
 * <br>(version 1.9) modified readDate() and readWord() to remove the calls to
 *               JDK 1.02 deprecated methods for JDK 1.1 code
 *               and also for VisualCafe 2.0 Bug in flush() - 27 Jan 1998-abo
 * <br>(version 2.0) javadoc style - 8 November 1999 - abo
 *
 * @
 * PUBLIC FEATURES:
 *   // Methods:
 *   public static String readString()
 *   public static String readString(String prompt)
 *   // Read a string from the console, the string is terminated by a newline
 *   // Returns the input string (without the newline).  No prompt unless
 *   // supplied.
 *
 *   public static int    readInt()
 *   public static int    readInt(String prompt)
 *   public static long   readLong()
 *   public static long   readLong(String prompt)
 *   public static float  readFloat()
 *   public static float  readFloat(String prompt)
 *   public static double readDouble()
 *   public static double readDouble(String prompt)
 *   // Reads a number from the console, the input is terminated by a newline.
 *   // If the input is not a valid representation of the number's type
 *   // it display's an error message and retries, re-prompting if applicable.
 *   // Returns a numeric value of the indicated type. After 5 tries returns 0.
 *   // Adds space to prompt if supplied, effective default prompt is ": ".
 *
 *   public static Date readDate()
 *   public static Date readDate(String prompt)
 *   // Reads a date from the console, the input is terminated by a newline.
 *   // If the input is not a valid date representation as defined in the
 *   // java.text.DateFormat class it display's an error message, re-prompting if
 *   // applicable and retries.  After 5 tries it returns the current date.
 *   // Adds space to prompt if supplied, effective default prompt is ": ".
 *   // Returns a Date object.
 *
 *   public static void pause()
 *   public static void pause(String prompt)
 *   // Pauses execution and waits for the user to press <ENTER> or NEWLINE ('\n')
 *   // If not supplied as a parameter the default prompt is
 *   //           "Press Enter to continue......."
 *
 *   public static String readWord()
 *   // Returns a 'word' from the console. A word is any group of characters
 *   // terminated by whitespace (one or more ' ', '\t', '\n').  No prompt.
 *   public static void flush()
 *   // Ignore the rest of the current line, ie. flush the input stream
 *   // discarding any contents up to '\n' inclusive.  Does nothing if
 *   // '\n' is last char read.  Only useful after readWord().
 *
 * MODIFIED:
 *  version 1.7, 5 May 1997. retry limit. rka
 *  version 1.8, 25 November 1997. CFLF. abo
 *  version 1.9, 27 January 1998. JDK 1.1. abo
 * @version 2.0, 8 November 1999. javadoc. abo
 * @author Ewen Vowels
 * @author Rob Allen
 * @author Annette Oppenheim. School of IT, Swinburne University of Technology
 */


public class Keyboard
{
   /**
    * maintains last char read - used by readWord, flush, pause
    */
   private static int lastChar = 0;
   /**
    * internal value of end of line character
    */
   private static int endOfLine = 10;
   /**
    * internal representation of end of file
    */
   private static int endOfFile = -1;

   /**
    * Read a string from the console, the string is terminated by a newline.
    * No prompt.
    *
    * @return the input string (without the newline).
    */
   public static String readString()
   {
      int     ch = 0;
      String  r = "";
      boolean done = false;

      while (!done)
      {
         try
         {
            ch = System.in.read();
            if (ch < 0 || (char)ch == '\n')
               done = true;
            // 'r' test added on 25/11/97 for JDK 1.1.4
            // readString() now works - abo
            else if ( (char) ch != '\r')
               r = r + (char) ch;
         }
         catch(java.io.IOException e)
         {
            done = true;
            return null;
         }
      }
      lastChar = ch;
      return r;
   }

   /**
    * Read a string from the console, the string is terminated by a newline.
    * readString() with prompting.
    *
    * @param prompt the prompt
    * @return the input string (without the newline).
    */
   public static String readString(String prompt)
   {
      System.out.print(prompt + " ");
      System.out.flush();
      return readString();
   }

   /**
    * Reads an integer from the console, the input is terminated by a newline.
    * If the input is not a valid integer representation it displays an
    * error message, re-prompts and retries.
    * readInt() with default ":" prompt
    *
    * @return an integer
    */
   public static int readInt()
   {
      return readInt(":");
   }

   /**
    * Reads an integer from the console, the input is terminated by a newline.
    * If the input is not a valid integer representation it displays an
    * error message, re-prompts and retries.
    *
    * @param prompt the prompt
    * @return an integer
    */
    public static int readInt(String prompt)
    {
      int count = 5;
      while (count > 0)
      {
         System.out.print(prompt + " ");
         System.out.flush();
         try
         {
            return Integer.valueOf(readString().trim()).intValue();
         }
         catch(NumberFormatException e)
         {
            System.out.println ("Not an integer! Please try again.");
         }
         --count;
      }
      System.out.println ("Too many retries -- treated as zero.");
      return 0;
   }

   /**
    * Reads a long integer from the console, the input is terminated by a newline.
    * If the input is not a valid integer representation it displays an
    * error message, re-prompts and retries.
    * readLong() with default ":" prompt
    *
    * @return a long integer
    */
   public static long readLong()
   {
      return readLong(":");
   }

   /**
    * Reads a long integer from the console, the input is terminated by a newline.
    * If the input is not a valid integer representation it displays an
    * error message, re-prompts and retries.
    *
    * @param prompt the prompt
    * @return a long integer
    */
   public static long readLong(String prompt)
   {
      int count = 5;
      while (count > 0)
      {
         System.out.print(prompt + " ");
         System.out.flush();
         try
         {
            return Long.valueOf(readString().trim()).longValue();
         }
         catch(NumberFormatException e)
         {
            System.out.println ("Not an integer! Please try again.");
         }
         --count;
      }
      System.out.println ("Too many retries -- treated as zero.");
      return 0L;
   }

   /**
    * Reads a floating point number from the console,
    * the input is terminated by a newline.
    * If the input is not a valid floating point representation it
    * displays an error message, re-prompts and retries.
    * readFloat() with default ":" prompt
    *
    * @return a floating point number
    */
   public static float readFloat()
   {
      return readFloat(":");
   }

   /**
    * Reads a floating point number from the console,
    * the input is terminated by a newline.
    * If the input is not a valid floating point representation it
    * displays an error message, re-prompts and retries.
    *
    * @param prompt the prompt
    * @return a floating point number
    */
   public static float readFloat(String prompt)
   {
      int count = 5;
      while (count > 0)
      {
         System.out.print(prompt + " ");
         System.out.flush();
         try
         {
            return Float.valueOf
                (readString().trim()).floatValue();
         }
         catch(NumberFormatException e)
         {
            System.out.println ("Not a floating point number! Please try again.");
         }
         --count;
      }
      System.out.println ("Too many retries -- treated as zero.");
      return 0.0F;
   }

   /**
    * Reads a double precision floating point number from the console,
    * the input is terminated by a newline.
    * If the input is not a valid double representation it displays an
    * error message, re-prompts and retries
    * readDouble() with default ":" prompt
    *
    * @return a double precision floating point number
    */
   public static double readDouble()
   {
      return readDouble(":");
   }

   /**
    * Reads a double precision floating point number from the console,
    * the input is terminated by a newline.
    * If the input is not a valid double representation it displays an
    * error message, re-prompts and retries
    *
    * @param prompt the prompt
    * @return a double precision floating point number
    */
    public static double readDouble(String prompt)
    {
      int count = 5;
      while (count > 0)
      {
         System.out.print(prompt + " ");
         System.out.flush();
         try
         {
            return Double.valueOf
               (readString().trim()).doubleValue();
         }
         catch(NumberFormatException e)
         {
            System.out.println ("Not a floating point number! Please try again.");
         }
         --count;
      }
      System.out.println ("Too many retries -- treated as zero.");
      return 0.0;
   }

   /**
    * Reads a date from the console, the input is terminated by a newline.
    * If the input is not a valid date representation as defined in the
    * java.text.DateFormat class it displays an error message, re-prompts
    * and retries.
    * readDate() with default ":" prompt
    *
    * @return a date
    */
   public static Date readDate()
   {
      return readDate(":");
   }

   /**
    * Reads a date from the console, the input is terminated by a newline.
    * If the input is not a valid date representation as defined in the
    * java.text.DateFormat class it displays an error message, re-prompts
    * and retries.
    *
    * @param prompt the prompt
    * @return a date
    */
   public static Date readDate(String prompt)
   {
      Date result;
      int count = 5;
      while (count > 0)
      {
         System.out.print(prompt + " ");
         System.out.flush();
         try
         {
            // replaced JDK 1.02 return new Date(readString()) with
            // following line - abo 8/12/97
            result = DateFormat.getDateInstance().parse(readString());
            return result;
         }
         // replaced JDK 1.02 catch(IllegalArgumentException e)
         // abo - 8/12/97
         catch(ParseException e)
         {
            System.out.println("Not a valid date format! Please try again.");
         }
         --count;
      }
      System.out.println ("Too many retries -- treated as today.");
      return new Date();
   }

   /**
    * Pauses execution and waits for the user to press <ENTER> or NEWLINE ('\n')
    * Displays default prompt 'Press Enter to continue......' before pausing.
    */
   public static void pause()
   {
      pause("\nPress Enter to continue.......");
   }

   /**
    * Pauses execution and waits for the user to press <ENTER> or NEWLINE ('\n')
    * Displays prompt before pausing.
    *
    * @param prompt the prompt
    */
   public static void pause(String prompt)
   // (Bug fixes for use with file input. ABO 29/4/97, RKA 5/5/97)
   {
      System.out.print(prompt + " ");
      System.out.flush();
      try
      {
          lastChar = System.in.read();
  	      // System.out.println("now" + lastChar);  // abo debug
      }
      catch(java.io.IOException e)
      {
      }
      flush();
   }

   /**
    * Read a 'word' from the console. A word is any group of characters terminated
    * by whitespace. Strips leading mutiple white spaces.
    *
    * @return a String - the 'word' entered
    */
   public static String readWord()
   // Note that this code does not correctly handle ^Z as DOS EOF
   // ABO - 20/3/97
   {
      int     ch = 0;
      String  r = "";
      boolean done = false;
      boolean haveSpace = true;

      while (!done)
      {
         try
         {
            ch = System.in.read();
            // replaced isSpace() with isWhitspace() for JDK 1.1
            // abo - 8/12/97
            haveSpace = ch < 0 || Character.isWhitespace((char)ch);
            if (!haveSpace)
                r = r + (char) ch;
            else if (r.length() != 0 || ch < 0)  // (v1.7) ch < 0
                done = true;
   	        // System.out.println("ch=" + ch);   // rka debug
         }
         catch(java.io.IOException e)
         {
            done = true;
   	        System.out.println("IOException");  // rka debug
   	        break;
         }
      }
      lastChar = ch;
      return r;
   }

   /**
    * Flush the input stream up to first '\n' unless '\n' just read.
    */
   public static void flush()
   // New version (1.6) rka
   // New Version (1.9) abo for {} around  String ignore = readString()
      {
           if (lastChar != endOfLine && lastChar != endOfFile)
           {
	            String ignore = readString();
	       }
	  }

    }  // end Keyboard