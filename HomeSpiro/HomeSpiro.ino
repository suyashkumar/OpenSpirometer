  /*
  HomeSpiro.ino.
  A home lung measurement device.
  @author: Suyash Kumar
  @author: Shana Fielding
  @author: Amy Zhao 
  */
  
  #include <Wire.h>  
  #define spiro 0


  int data[400]={};
  int threshold=8; //0.05 V offset from baseline
  int doneLength = 100; //2 seconds of zeros = done
  

  void setup(){
    // set communiation speed
    Serial.begin(9600);   
    pinMode(spiro, OUTPUT);
    
  }

  
  void loop(){
       
     if (Serial.available()>0){
     char incomingByte;
     incomingByte = Serial.read();
     if (incomingByte == '0'){
       
       //automatic offset calculation
      int Vi_sum=0;
      for (int i=0; i<30; i++){ 
        int Vi = analogRead(spiro); //take input from pin 1
        Vi_sum = Vi_sum+Vi;
      }

      float Vi_avg_float = ((float) Vi_sum)/30;
      int Vi_avg = (int) Vi_avg_float;    
       
      data[400]={};  //reset data array
      measure(Vi_avg);      
      sendData();
    
    }
  }
 
  
}

void measure(int Vi_avg){
    int i=0;
    int zeroCounter = 0;
    boolean breathStarted = false;
    while(i<399){
      int time1 = millis();
      if (zeroCounter>doneLength) break;        //user is done breathing
      int V_in = analogRead(spiro)-Vi_avg;          //auto offset
 
     
     if (V_in>threshold || breathStarted == true){ //only start counting when value is above threshold
        data[i] = V_in;
        breathStarted = true;
        i++;
     }
     
     if (V_in<threshold && breathStarted == true) zeroCounter++;  //count "0"s after breathStarted
     else zeroCounter=0;               //must be consecutive 
     int time2 = millis();
     int timeElapsed = time2-time1;
     delay(20-timeElapsed);                        //50 Hz sample rate = 20 ms period
    // int testTime = millis();
    // int samplePeriod = testTime-time1;
    // Serial.print(samplePeriod);  
    // Serial.print(", ");
  }
}

void sendData(){
   //Serial.print("[");
    for (int i=0; i<400; i++){
      Serial.print(data[i]);
  //    if(i!=399){
        Serial.print(',');
  //    }
    }
  //  Serial.print("]");
    Serial.println();
    
}

