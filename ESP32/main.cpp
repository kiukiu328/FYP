#include <Arduino.h>
#include <WiFi.h>
#include <HTTPClient.h>

const int voltagePin = 32;
float val;

const char *ssid = "kiu";
const char *password = "aa301301";
String serverName = "http://192.168.1.107/setVoltage";
unsigned long lastTime = 0;
unsigned long timerDelay = 5000;

void setup()
{
  Serial.begin(9600);
  WiFi.begin(ssid, password);
  pinMode(2, OUTPUT);
  WiFi.begin(ssid, password);
  Serial.println("Connecting");
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());

  Serial.println("Timer set to 5 seconds (timerDelay variable), it will take 5 seconds before publishing the first reading.");
}

void loop()
{
  // Send an HTTP POST request every 10 minutes
  if ((millis() - lastTime) > timerDelay)
  {

    // Check WiFi connection status
    if (WiFi.status() == WL_CONNECTED)
    {
      HTTPClient http;
      val = analogRead(voltagePin);
      val = (val / 1024) * 5;
      Serial.println(val);
      String serverPath = serverName + "?voltage=" + val;

      // Your Domain name with URL path or IP address with path
      http.begin(serverPath.c_str());

      // Send HTTP GET request
      int httpResponseCode = http.GET();

      if (httpResponseCode > 0)
      {
        Serial.print("HTTP Response code: ");
        Serial.println(httpResponseCode);
        String payload = http.getString();
        Serial.println(payload);
      }
      else
      {
        Serial.print("Error code: ");
        Serial.println(httpResponseCode);
      }
      // Free resources
      http.end();
    }
    else
    {
      Serial.println("WiFi Disconnected");
    }
    lastTime = millis();
  }
}
