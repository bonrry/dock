
#include <Max3421e.h>
#include <Usb.h>
// FOR FREEDUINO BOARD :
// #include <FHB.h>
// FOR ADK MEGA BOARD  :
#include <AndroidAccessory.h>

#define  LED_ERR        8 /* digi */

#define  LED_WARM       6
#define  LED_COLD       5

#define  ACT_LED        'L'
#define  ACT_MOV        'M'
#define  ACT_DBG        'D'
#define  DBG_SENSOR     's'


AndroidAccessory acc("telethonSommeil",
"telethonSommeil",
"telethonSommeil",
"1.0",
"http://telethon.bemyapp.com/",
"0000000012345678");

void setup();
void loop();
void sendMovementMessagesToAndroid();
int CMD_DELIMITER = 255;
boolean sensor_realtime_feedback = false; // ON: cold light will reflect movement sensor level. 

void init_leds()
{
  pinMode(LED_ERR, OUTPUT);

  pinMode(LED_WARM, OUTPUT);
  pinMode(LED_COLD, OUTPUT);
}

void error_led()
{
  digitalWrite(LED_ERR, 1);
  Serial.println("ERROR");
}

void clear_error()
{
  digitalWrite(LED_ERR, 0);
}

void setup()
{
  Serial1.begin(9600);
  Serial.begin(115200);
  Serial.println("\nStart");
  init_leds();
  Serial.println("pre-power");
  acc.powerOn();
  Serial.println("post-power");
}

void loop()
{
  if (acc.isConnected()) {
    // SEND MESSAGES TO ANDROID
    consumeSerial(true);

    byte msg[3];
    int len = acc.read(msg, sizeof(msg), 1);

    // READ MESSAGES FROM ANDROID
    clear_error();
    if (len > 0) {
      if (msg[0] == ACT_LED) {
        if (len == 3) {
          if (msg[1] == 0x0) {
            analogWrite(LED_WARM, msg[2]);
            //Serial.print("LED_WARM "); 
            //Serial.print(msg[2], DEC);
          } 
          else if (msg[1] == 0x1) {
            analogWrite(LED_COLD, msg[2]);
            //Serial.print("LED_COLD "); 
            //Serial.print(msg[2], DEC);
          } 
          else {
            error_led();
          }
          //Serial.println("done");
        }
      } else if (msg[0] == ACT_DBG) {
        if (len == 3) {
          if (msg[1] == DBG_SENSOR) {
            if (msg[2] == 0) {
              sensor_realtime_feedback = false;
              analogWrite(LED_WARM, 0);
              analogWrite(LED_COLD, 0);
            } else {
              sensor_realtime_feedback = true;
              Serial.println("Sensor RT ON");
            }
          }
          //Serial.println("done");
        }
      }
    }
  } 
  else {
    // FIXME: shall we bufferize messages from sensor when phone is not plugged ?
    // SEND MESSAGES TO ANDROID
    consumeSerial(false);
    analogWrite(LED_WARM, 0);
    analogWrite(LED_COLD, 0);
    // Flush all, data are corrupted...
    cbuf_fluch();
    sensor_realtime_feedback = false;
  }
  delay(10);
}

#define BUF_SIZE 250
int m_type = 0;
int m_len = 0;
byte m_buf[BUF_SIZE];

// On ADK, we use Serial1 to talk to the XBEE
void consumeSerial(boolean isConnected) {
  int b;
  while(Serial1.available() > 0) {
    b = Serial1.read();
    //Serial.print(b, HEX);
    if (b == CMD_DELIMITER) {
      if (isConnected)
        sendMovementMessageToAndroid();
    } 
    else {
      cbuf_put(b);
    }
  }
}

void cbuf_fluch();
int toto = 0;
void sendMovementMessageToAndroid() {
  while(cbuf_available()) {
    int offset = 0;
    m_len = 0;
    m_type = cbuf_get();
    if (m_type == ACT_MOV) {
      m_buf[offset++] = ACT_MOV;
      if (cbuf_available()) {
        m_len = cbuf_get();
      }
      m_buf[offset++] = m_len;
      while (((offset - 2) < m_len) && (offset < BUF_SIZE)) {
        if (cbuf_available())
          m_buf[offset++] = cbuf_get();
        else {
          m_len = 0;
          break;
        }
      }
      if (m_len > 0) {
        acc.write(m_buf, m_len + 2);
        if (sensor_realtime_feedback) {
          //if (toto++ == 42)
          {
            analogWrite(LED_COLD, m_buf[2]);
            toto = 0;
            Serial.print("M sent ");
            Serial.println(m_buf[2]);
          }
        }
      }
    } 
    else {
      Serial.println("flush...");
      // Flush all, data are corrupted...
      cbuf_fluch();
    }
  }
}

#define CIRCULAR_BUF_SIZE 250
byte cbuf[CIRCULAR_BUF_SIZE];
int readIdx = 0;
int writeIdx = 0;

byte cbuf_put(byte b) {
  if (writeIdx+1 != readIdx) {
    cbuf[writeIdx++] = b;
    if (writeIdx == CIRCULAR_BUF_SIZE) {
      writeIdx = 0;
    }
  }
}

byte cbuf_available() {
  if (readIdx != writeIdx) {
    return true;
  }
  return false;
}

byte cbuf_get() {
  byte res = 0; 
  if (readIdx != writeIdx) {
    res = cbuf[readIdx++];
    if (readIdx == CIRCULAR_BUF_SIZE) {
      readIdx = 0;
    }
  }
  return res;
}

void cbuf_fluch() {
  int readIdx = 0;
  int writeIdx = 0;
}

