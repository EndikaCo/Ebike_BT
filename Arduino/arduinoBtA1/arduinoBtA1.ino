

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

const int algo = A1;

const int batAmpIn = A2;

const int buzzer = A3;



//Voltimetro


String strvoltage;

String strAmps;

String strSpeed;

String verific= "asdas";

int i;


//millis

unsigned long currentMillis = 0;

unsigned long previousMillis = 0;

unsigned long previousMillis2 = 0;



//SENSOR HALL

const float perimetro = 2.02;// 2*pi*(radio/100)

volatile int contador;//contador de vueltas de rueda

unsigned long iniciovuelta, finvuelta;

float distanciaHoy = 0, recorridoAyer, tiempo, recorrido;


void setup()

{
    Serial.begin(9600);

    BT1.begin(9600);

    pinMode(pas, OUTPUT); // pas

    pinMode(throttle, OUTPUT); // throttle

    pinMode(cruising, OUTPUT); // cruising

    pinMode(reverse, OUTPUT); // reverse

    pinMode(power1, OUTPUT); // power

    pinMode(buzzer, OUTPUT); //

    pinMode(back_light, OUTPUT); //

    pinMode(front_light, OUTPUT); //

    pinMode(batVoltIn, INPUT); //

    pinMode(batAmpIn, INPUT); //

    pinMode(algo, INPUT); //

    // hall
    contador = 0;
    // recorridoAyer = EEPROM.read(2);
    recorrido = recorridoAyer;

    // attachInterrupt(digitalPinToInterrupt(pas), hall, CHANGE);//interrupcion sensor hall

}



void loop() {

    currentMillis = millis();

    //velocidad();

    if (BT1.available()) {

        recibido(BT1.read());

    }

    if (currentMillis - previousMillis >= 100) { //loop 0.1s

        bateria();

        sensorconsumo();

        velocidad();

        previousMillis = currentMillis;

    }


    if (currentMillis - previousMillis2 >= 500) { //loop 0.5s

        serialView();

        blutus();

        previousMillis2 = currentMillis;

    }

}

void bateria() {

    const double r1 = 1000000;

    const double r2 = 72900;

    int readRaw = analogRead(A0);

    double vIn = (readRaw * 5) / 1023.0;

    // Voltage = map(vIn, fromLow, fromHigh, toLow, toHigh)

    double Voltage = vIn / (r2 / (r1 + r2));

    char str_temp[7];

    dtostrf(Voltage, 2, 2, str_temp);
    strvoltage = String(str_temp);  // cast it to string from char 

}

void velocidad() {

    const double r1 = 1000000;

    const double r2 = 72900;

    int readRaw = analogRead(A1);

    double vIn = (readRaw * 5) / 1023.0;

    // Voltage = map(vIn, fromLow, fromHigh, toLow, toHigh)

    double velosidad = vIn / (r2 / (r1 + r2));

    char str_temp[7];

    dtostrf(velosidad, 2, 2, str_temp);
    strSpeed = String(str_temp);  // cast it to string from char 
}


void sensorconsumo() {

    // Amperimetro

    int mVperAmp = 60; // See Scale Factors Below

    int ACSoffset = 2500; // See offsets below

    int RawValue = 0;

    RawValue = analogRead(batAmpIn);

    int vIn = (RawValue / 1023.0) * 5000; // Gets you mV

    double Amps = ((vIn - ACSoffset) / mVperAmp);

    char str_temp[7];

    dtostrf(Amps, 2, 2, str_temp);
    strAmps = String(str_temp);  // cast it to string from char 
}


void blutus() {


    BT1.print("v" + strvoltage + ";");

    BT1.print("a" + strAmps + ";");

    BT1.print("s" + strSpeed + ";");

}

void serialView() {

    Serial.print("v:");
    Serial.println(strvoltage);
     Serial.print("a:");
    Serial.println(strAmps);
     Serial.print("s:");
    Serial.println(strSpeed);
}


void pitido() {

    analogWrite(buzzer, 500);

    delay(100);

    analogWrite(buzzer, 0);

}


void recibido(char dat) {

    switch (dat)

    {

    case 'd':

        digitalWrite(reverse, HIGH);//DI}
        pitido();
        break;

    case 'r':

        digitalWrite(reverse, LOW);//DI}
        pitido();
        break;

    case 'i':

        digitalWrite(pas, LOW);//DI}
        digitalWrite(throttle, LOW);//DI}
        pitido();
        break;

    case 'o':

        digitalWrite(pas, HIGH);//DI}
        digitalWrite(throttle, LOW);//DI}
        pitido();
        break;

    case 'p':

        digitalWrite(throttle, HIGH);//DI}
        digitalWrite(pas, LOW);//DI}
        pitido();
        break;

    case 'a':

        digitalWrite(power1, HIGH);//DI}
        pitido();
        break;

    case 'b':

        digitalWrite(power1, LOW);//DI}
        pitido();
        break;

    case 'l':

        digitalWrite(back_light, LOW);//DI}
        digitalWrite(front_light, LOW);//DI}
        pitido();
        break;

    case 'k':

        digitalWrite(back_light, HIGH);//DI}
        digitalWrite(front_light, LOW);//DI}
        pitido();
        break;

    case 'j':
        digitalWrite(back_light, HIGH);//DI}
        digitalWrite(front_light, HIGH);//DI}
        pitido();
        break;


    default:

        Serial.print("eneo");
        break;

    }

}
/*

  LISTA COMANDOS - versión VERSION:3.0-20170609

  38400br LAS 2

  AT                                <-- Prueba comunicación comandos AT

  AT+NAME:nombre       <-- Cambia el nombre

  AT+PSWD:"1234"       <-- Modifica pin de emparejamiento en este caso 1234

  AT+UART:9600,0,0    <-- Selecciona el baudRate, en este caso el 4 que es 9600



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


  Saber modo de Conexión: AT+CMODE (0 Especifico 1 General)

  A cualquier dispositivo: AT+CMODE=1

  saber direccion mac al que se empareja AT+BIND

*/
