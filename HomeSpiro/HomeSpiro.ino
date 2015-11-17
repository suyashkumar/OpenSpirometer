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
  
  #define spiro 0

  AndroidAccessory acc("Manufacturer",
  		"Model",
  		"Description",
  		"1.0",
  		"http://yoursite.com",
                  "0000000012345678");
  
  float Vi_avg_float;
  int Vi_avg;
  int data[400]={};
  int threshold=10; //0.05 V offset from baseline
  int doneLength = 100; //2 seconds of zeros = done
  

  void setup(){
    // set communiation speed
    Serial.begin(9600);
   
    //Serial.print("\r\nStart");
    //boolean out = acc.isConnected();
    //Serial.println(out, DEC);
    //acc.powerOn();
    
    //set pins
    
    int Vi_sum=0;
    pinMode(spiro, OUTPUT);
    //automatic offset calculation
    for (int i=0; i<30; i++){ 
      int Vi = analogRead(spiro); //take input from pin 1
      Vi_sum = Vi_sum+Vi;
    }

    Vi_avg_float = ((float) Vi_sum)/30;
    Vi_avg = (int) Vi_avg_float;
    
  }

  
  void loop(){
       
   if (Serial.available()>0){
     char incomingByte;
     incomingByte = Serial.read();
    if (incomingByte == '0'){
      int data[400]={};
      measure();      
      sendData();
    
    }
   }
    
// //  int len;
// //  len = acc.read(msg, sizeof(msg), 1);
// //  if (len>0){
//      //Serial.println(msg[0]);
//      if (msg[0]==1) {
//        byte data[400];  //reset data for new FVC/FEV measurement
//        measure();
//        sendData();
//      }     
// //  }


   
  
}

void measure(){
    int i=0;
    int zeroCounter = 0;
    boolean breathStarted = false;
    while(i<399){
      
      if (zeroCounter>doneLength) break;        //user is done breathing
      int V_in = analogRead(spiro)-Vi_avg;          //auto offset
      

    //voltage = V_in*5/1023
    
//    exhale only--don't need?
//    if(V_in1<0){ //negative volume
//    }
//  
//   if(V_in1>=0){ //positive volume
//   }

   
     
     if (V_in>threshold || breathStarted == true){ //only start counting when value is above threshold
        data[i] = V_in;
        breathStarted = true;
        i++;
     }
     
     if (V_in<threshold && breathStarted == true) zeroCounter++;  //count "0"s after breathStarted
     else zeroCounter=0;               //must be consecutive 
     
     delay(20);                        //50 Hz sample rate = 20 ms period
    }
}

void sendData(){
//   if (acc.isConnected()){   
//       acc.write(data, sizeof(data));
//   } 
    for (int i=0; i<400; i++){
      Serial.print(data[i]); 
    }
    Serial.println();
    
}
