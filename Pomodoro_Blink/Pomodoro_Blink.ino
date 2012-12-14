/* Woosung's Pomodoro Project */
#include <SoftwareSerial.h>

#define TIME_25MIN  49    //1
#define TIME_5MIN   50     //2
#define TIME_30MIN  51    //3

#define TIMER_CANCELED 'C'    
#define TIMER_IS_UP    'U'    

void startPomodoroFromSerial(int value);
void startPomodoroFromTest(int value);

const int led[5] = {3, 4, 5, 6, 7};   //3~7을 5개의 LED로 잡았다. 
const int ledBT = 2;                  //BlueTooth 옆의 LED 
const int timerBtn = 10;              //타이머의 중지 버튼
SoftwareSerial BTSerial(8, 9);        // BlueTooth module HC-06 TX,RX  

int i = 0;

// the setup routine runs once when you press reset:
void setup() 
{                
  // initialize the digital pin as an output.
  for(i=0; i<5; i++){
      pinMode(led[i], OUTPUT);     
  }
  pinMode(ledBT, OUTPUT);
  pinMode(timerBtn, INPUT);
  
  //Set the rate for the Serial and BlueTooth module
  Serial.begin(9600);
  delay(100);
  BTSerial.begin(9600);
}

// the loop routine runs over and over again forever:
void loop() 
{
  char toSend = 0;
 
  for(i=0; i<5; i++){
    digitalWrite(led[i], HIGH);    // HIGH = LED 끈다. 
  }  
  digitalWrite(ledBT, LOW);  
  delay(10);
  
  //read from BlueTooth module
  if (BTSerial.available())
  {
    toSend = (char)BTSerial.read();    
    switch(toSend)
    {
      case TIME_25MIN : 
        //Serial.println("25");
        startPomodoroFromSerial(25);
        break;
      case TIME_5MIN : 
        //Serial.println("5");
        startPomodoroFromSerial(5);        
        break;
      case TIME_30MIN : 
        //Serial.println("30");
        startPomodoroFromSerial(30);        
        break;
      default : 
        Serial.println("another value");  
    }
    
    //LED Blink
    digitalWrite(ledBT, HIGH); 
    delay(5);
    digitalWrite(ledBT, LOW);
    delay(5);    

  } else {
    //대기 모드
    digitalWrite(led[0], LOW);
    delay(500);
    digitalWrite(led[0], HIGH);
    delay(500);
  }
  
  //Read from usb serial to bluetooth
  if(Serial.available())
  {
    char toSend = (char)Serial.read();
    BTSerial.print(TIMER_CANCELED);
    digitalWrite(ledBT, HIGH);    
    delay(5);
  }
}

void startPomodoroFromSerial(int value)
{
  int minute = value / 5;
  int LEDcount = 0;
  int time = 0;
  int j = 0;
  char btnState = 0;
  
  Serial.print("I received ");
  Serial.println(value);

  //LED 켜고, 
  for(j=0; j<minute; j++)
  {
    digitalWrite(led[j], LOW);   // turn the LED on (HIGH is the voltage level)
  }
  LEDcount = j-1;  //가장 상위 LED index 값
  
  while(time < value)
  {
    btnState = digitalRead(timerBtn); 
    //send canceled message to android phone :) 
    if(btnState == HIGH)
    {
      Serial.println("Timer is canceled. ");
      BTSerial.print(TIMER_CANCELED);   //only 1 time.
      digitalWrite(ledBT, HIGH);    
      delay(5);
      break;
    }
    delay(1000);   //wait 1sec 
    time ++;
    
    //5sec 단위로 LED 하나씩 꺼준다.
    if(time%5 == 0) {
      digitalWrite(led[LEDcount--], HIGH); 
    }
  }
  
  // 정상 종료
  if(btnState != HIGH) {
    Serial.println("Time's Up !! ");
    BTSerial.print(TIMER_IS_UP);   //only 1 time.
    digitalWrite(ledBT, HIGH);    
    delay(5);    
  }
  
  //끝났다는 표시로 LED 를 두 번 전체적으로 깜박인다 
  for(i=0; i<2; i++)
  {
    delay(500);   //500ms 
    //LED 전체 켜고, 
    for(j=0; j<5; j++)
    {
      digitalWrite(led[j], LOW); 
    }
    delay(500);   //500ms
  
    //LED 전체 끄고, 
    for(j=0; j<5; j++)
    {
      digitalWrite(led[j], HIGH);
    }
  }  
}
