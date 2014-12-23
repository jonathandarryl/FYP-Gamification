/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Logger;

import java.io.IOException;
import java.util.logging.*;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author chongyangsun
 */
public class MyLogger {
  static private FileHandler fileTxt;
  static private SimpleFormatter formatterTxt;

  static private FileHandler fileHTML;
  static private Formatter formatterHTML;

  static public void setup() throws IOException {

    // get the global logger to configure it
    Logger logger = Logger.getLogger("");

    // suppress the logging output to the console
//    Logger rootLogger = Logger.getLogger("");
//    Handler[] handlers = rootLogger.getHandlers();
//    if (handlers[0] instanceof ConsoleHandler) {
//      rootLogger.removeHandler(handlers[0]);
//    }

    logger.setLevel(Level.INFO);
//    fileTxt = new FileHandler("MerLION.txt");
//    fileHTML = new FileHandler("MerLION.html");
    fileTxt = new FileHandler("%h/MerLIONLog/MerLION-%g-%u.txt");
    fileHTML = new FileHandler("%h/MerLIONLog/MerLION-%g-%u.html");

    // create a TXT formatter
    formatterTxt = new SimpleFormatter();
    fileTxt.setFormatter(formatterTxt);
    logger.addHandler(fileTxt);

    // create an HTML formatter
    formatterHTML = new MyHtmlFormatter();
    fileHTML.setFormatter(formatterHTML);
    logger.addHandler(fileHTML);
  }
}
 
