package com.web.utility;
import java.net.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
//import processing.core.*;

public class IPCapture   implements Runnable {
   
  private String urlString, user, pass;
  private byte[] curFrame;
  private boolean frameAvailable;
  private Thread streamReader;
  private HttpURLConnection conn;
  private BufferedInputStream httpIn;
  private ByteArrayOutputStream jpgOut;
  
  public final static String VERSION = "0.1.0";
  
  public IPCapture( String urlString, String user, String pass) {
    super();
      
    this.urlString = urlString;
    this.user = user;
    this.pass = pass;
    this.curFrame = new byte[0];
    this.frameAvailable = false;
    this.streamReader = new Thread(this, "HTTP Stream reader");
  }
  
  public boolean isAvailable() {
    return frameAvailable;
  }
  
  public void start() {
    streamReader.start();
  }
  
  public void stop() {
    try {
      jpgOut.close();
      httpIn.close();
    }
    catch (IOException e) {
      System.err.println("Error closing streams: " + e.getMessage());
    }
    conn.disconnect();
  }
  
  public void dispose() {
    stop();
  }
  
  public void run() {
    URL url;
    Base64Encoder base64 = new Base64Encoder();
    
    try {
      url = new URL(urlString);
    }
    catch (MalformedURLException e) {
      System.err.println("Invalid URL");
      return;
    }
    
    try {
      conn = (HttpURLConnection)url.openConnection();
      conn.setRequestProperty("Authorization", "Basic " + base64.encode(user + ":" + pass));
      httpIn = new BufferedInputStream(conn.getInputStream(), 8192);
    }
    catch (IOException e) {
      System.err.println("Unable to connect: " + e.getMessage());
      return;
    }
    
    int prev = 0;
    int cur = 0;
    
    try {
      while (httpIn != null && (cur = httpIn.read()) >= 0) {
        if (prev == 0xFF && cur == 0xD8) {
          jpgOut = new ByteArrayOutputStream(8192);
          jpgOut.write((byte)prev);
        }
        if (jpgOut != null) {
          jpgOut.write((byte)cur);
        }
        if (prev == 0xFF && cur == 0xD9) {
          synchronized(curFrame) {
            curFrame = jpgOut.toByteArray();
          }
          frameAvailable = true;
          jpgOut.close();
        }
        prev = cur;
      }
    }
    catch (IOException e) {
      System.err.println("I/O Error: " + e.getMessage());
    }
  }
  
  public void read() {
    try {
      ByteArrayInputStream jpgIn = new ByteArrayInputStream(curFrame);
      BufferedImage bufImg = ImageIO.read(jpgIn);
      jpgIn.close();
      int w = bufImg.getWidth();
      int h = bufImg.getHeight();
      /*
      if (w != this.width || h != this.height) {
        this.resize(bufImg.getWidth(),bufImg.getHeight());
      }
      bufImg.getRGB(0, 0, w, h, this.pixels, 0, w);
      this.updatePixels();
      */
      frameAvailable = false;
    }
    catch (IOException e) {
      System.err.println("Error acquiring the frame: " + e.getMessage());
    }
  }
}
