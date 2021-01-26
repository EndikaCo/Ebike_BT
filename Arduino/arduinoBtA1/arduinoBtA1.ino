/*
Name:    Ebici.ino
Author:  Endika Correia
*/

#include <SoftwareSerial.h>

SoftwareSerial BT1(10, 11); // RX | TX

const int hall_sensor = 3;

const int power2 = 4;

const int power1 = 5;

const int reverse = 6;

const int cruising = 7;

const int throttle = 8;

const int pas = 9;

const int back_light = 12;

const int front_light = 13;

const int batVoltIn = A0;

const int batAmpIn = A2;

const int horn = A3;


//lights

bool blinkBackLight=LOW;


//Strings to send via bluetooth

String strvoltage;

String strAmps;

String strSpeed;


//millis

unsigned long currentMillis = 0;

unsigned long previousMillis = 0;

unsigned long previousMillis2 = 0;


//hall sensor

const float perimetre = 2.02;// 2*pi*(radio/100)

volatile int spinCount; // wheel spin counter

unsigned long startSpin, stopSpin;

float distanceToday = 0, distanceYesterday, spinTime, distance;


void setup(){

    BT1.begin(9600);  // start bluetooth baud rate

    pinMode(pas, OUTPUT); // pas

    pinMode(throttle, OUTPUT); // throttle

    pinMode(cruising, OUTPUT); // cruising

    pinMode(reverse, OUTPUT); // reverse

    pinMode(power1, OUTPUT); // power

    pinMode(horn, OUTPUT); //horn

    digitalWrite(horn, HIGH);//OFF (Reversed relay)

    pinMode(back_light, OUTPUT); //

    pinMode(front_light, OUTPUT); //

    digitalWrite(front_light, HIGH); //OFF (Reversed relay)

    pinMode(batVoltIn, INPUT); //

    pinMode(batAmpIn, INPUT); //

    // hall
    ///contador = 0;
    // recorridoAyer = EEPROM.read(2);
   // recorrido = recorridoAyer;
    // attachInterrupt(digitalPinToInterrupt(pas), hall, CHANGE);//interrupcion sensor hall
}



void loop() {

    currentMillis = millis();

    if (BT1.available()) {

        Receive(BT1.read());

    }

    if (currentMillis - previousMillis >= 200) { //loop 0.1s

        BatteryMeasure();

        AmperesSensor();

        SpeedHall();

        previousMillis = currentMillis;

    }


    if (currentMillis - previousMillis2 >= 500) { //loop 0.5s

        BluetoothSend();

        BlinkBackOn();

        previousMillis2 = currentMillis;

    }

}

void BatteryMeasure() {

    const double r1 = 1000000;

    const double r2 = 74800;

    int readRaw = analogRead(batVoltIn);

    double vIn = (readRaw * 4.9) / 1023.0; 

    double Voltage = vIn / (r2 / (r1 + r2)); // voltage divider 

    char str_temp[7]; 

    dtostrf(Voltage, 2, 2, str_temp);

    strvoltage = String(str_temp);  // cast it to string from char 

}

void SpeedHall() {

    const double r1 = 1000000;

    const double r2 = 74500;//7290

    int readRaw = analogRead(hall_sensor);

    double vIn = (readRaw * 5) / 1023.0;

    // Voltage = map(vIn, fromLow, fromHigh, toLow, toHigh)

    double speed = vIn / (r2 / (r1 + r2));

    char str_temp[7];

    dtostrf(speed, 2, 2, str_temp);
    strSpeed = String(str_temp);  // cast it to string from char 
}


void AmperesSensor() {

    // Amperimetre

    int mVperAmp = 60; 

    int ACSoffset = 2500; 

    int RawValue = 0;

    RawValue = analogRead(batAmpIn);

    int vIn = (RawValue / 1023.0) * 5000; // Gets you mV

    double Amps = ((vIn - ACSoffset) / mVperAmp);

    char str_temp[7];

    dtostrf(Amps, 2, 2, str_temp);
    strAmps = String(str_temp);  // cast it to string from char 
}


void BluetoothSend() {

    BT1.print("xxx");
    BT1.print("v" + strvoltage + ";");
    BT1.print("a" + strAmps + ";");
   // BT1.print("s" + strSpeed + ";");
    // BT1.print("b" + verificCode + ";");
    BT1.print("xxx");
}


void StartHorn(int t) {

    digitalWrite(horn, LOW);//on

    delay(t);

    digitalWrite(horn, HIGH);//off

}

void BlinkBackOn() {

    if (blinkBackLight == HIGH) {

        if (digitalRead(back_light) == HIGH) {
            digitalWrite(back_light, LOW);
        }
        else digitalWrite(back_light, HIGH);
    }
}


void Receive(char dat) {
    switch (dat) {

    case 'd':

        digitalWrite(reverse, HIGH);//DI}
        StartHorn(10);
        break;

    case 'r':

        digitalWrite(reverse, LOW);//DI}
        StartHorn(10);
        break;

    case 'i':

        digitalWrite(pas, LOW);//DI}
        digitalWrite(throttle, LOW);//DI}
        StartHorn(10);
        break;

    case 'o':

        digitalWrite(pas, HIGH);//DI}
        digitalWrite(throttle, LOW);//DI}
        StartHorn(10);
        break;

    case 'p':

        digitalWrite(throttle, HIGH);//DI}
        digitalWrite(pas, LOW);//DI}
        StartHorn(10);
        break;

    case 'a':

        digitalWrite(power1, HIGH);//DI}
        StartHorn(10);
        break;

    case 'b':

        digitalWrite(power1, LOW);//DI}
        StartHorn(10);
        break;
    case 'l':

        digitalWrite(back_light, LOW);
        digitalWrite(front_light, HIGH);//OFF
        StartHorn(10);
        break;

    case 'k':

        digitalWrite(back_light, HIGH);
        digitalWrite(front_light, HIGH);//OFF
        StartHorn(10);
        break;

    case 'j':
        digitalWrite(back_light, HIGH);
        digitalWrite(front_light, LOW);//ON
        StartHorn(10);
        break;

    case 'x':

        blinkBackLight = HIGH;
        break;

    case 'z':

        blinkBackLight = LOW;
        break;

    case 'y':

        StartHorn(200);
        break;

    default:

        break;

    }
}

/*
 HC-05 VERSION:3.0-20170609

  38400 baud rate

  AT+NAME:name        <-- change name

  AT+PSWD:"1234"      <-- change password

  AT+UART:9600,0,0    <-- change baud rate

  AT+CMODE=1          <-- change modes


  Most useful AT commands are

  AT : Ceck the connection.

  AT+NAME : See default name

  AT+ADDR : see default address

  AT+VERSION : See version

  AT+UART : See baudrate

  AT+ROLE: See role of bt module(1=master/0=slave)

  AT+RESET : Reset and exit AT mode

  AT+ORGL : Restore factory settings

  AT+PSWD: see default password

  AT+CMODE : connection mode(0 Especific 1 General) 

  AT+BIND : paired device address 

*/