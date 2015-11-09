  /*
  HomeSpiro.ino.
  A home lung measurement device.
  @author: Suyash Kumar
  @author: Shana Fielding
  @author: Amy Zhao 
  */
  
  #include <Wire.h>  
  #include "Max3421e.h"
  #include "Usbhost.h"
  #include "AndroidAccessory.h"
  
  #define spiro 1

  AndroidAccessory acc("Manufacturer",
  		"Model",
  		"Description",
  		"1.0",
  		"http://yoursite.com",
                  "0000000012345678");
  
  float Vi[10];
  float Vi_avg;
  byte buffer[400];
  int threshold = 10; //change this (0-255)
  int doneLength = 10; //change this (num samples of 0 = done)

  void setup(){
    // set communiation speed
    Serial.begin(115200);
   
    Serial.print("\r\nStart");
    boolean out = acc.isConnected();
    Serial.println(out, DEC);
    acc.powerOn();
    
    //set pins
    
    float Vi_sum=0;
    //automatic offset calculation
    for (int i=0; i<10; i++){ 
      Vi[i] = analogRead(spiro); //take input from pin 1
      Vi_sum = Vi_sum+Vi[i];
    }

    Vi_avg = Vi_sum/10;
    
    
  }

  
  void loop(){
       
   byte msg[1];
    
   int len;
   len = acc.read(msg, sizeof(msg), 1);
   if (len>0){
      Serial.println(msg[0]);
      if (msg[0]==1) {
        byte buffer[400];  //reset buffer for new FVC/FEV measurement
        measure();
        sendData();
      }     
   }


   
  
}

void measure(){
    int i=0;
    int zeroCounter = 0;
    while(i<399){
      
      if (zeroCounter>doneLength) break;        //user is done breathing
      int V_in = analogRead(1)-Vi_avg;          //auto offset
      buffer[i] = (byte) V_in;
    //voltage = V_in*5/1024
    
    //exhale only--don't need?
//    if(V_in1<0){ //negative volume
//    }
//  
//   if(V_in1>=0){ //positive volume
//   }

     if (V_in<threshold) zeroCounter++;    //count "0"s
     else zeroCounter=0;               //must be consecutive    
     delay(20);                        //50 Hz sample rate = 20 ms period
     i++;
    }
}

void sendData(){
   if (acc.isConnected()){   
       acc.write(buffer, sizeof(buffer));
   } 
}
