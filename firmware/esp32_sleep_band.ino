#include <Wire.h>
#include "MAX30105.h"
#include <MPU6050_tockn.h>

// ===== BLE =====
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

// ===== BLE UUIDs =====
#define SERVICE_UUID        "12345678-1234-1234-1234-1234567890ab"
#define CHARACTERISTIC_UUID "abcdefab-1234-5678-1234-abcdefabcdef"

// ===== Sensors =====
MAX30105 particleSensor;
MPU6050 mpu6050(Wire);

// ===== BLE =====
BLECharacteristic *pCharacteristic;

// ===== Battery measurement =====
const int   BAT_ADC_PIN = 32;
const int   BAT_ADC_RES = 4095;
const float BAT_VREF   = 3.3;
const float BAT_DIVIDER_RATIO = 3.0;

float batSlope  = 1.0;
float batOffset = 0.0;

float readBatteryVoltage() {
  int raw = analogRead(BAT_ADC_PIN);
  float vPin = (float)raw * BAT_VREF / BAT_ADC_RES;
  vPin = vPin * batSlope + batOffset;
  return vPin * BAT_DIVIDER_RATIO;
}

void setup() {
  Serial.begin(115200);
  delay(1000);

  // ===== I2C =====
  Wire.begin(21, 22);

  // ===== MAX30102 =====
  if (!particleSensor.begin(Wire, I2C_SPEED_FAST)) {
    while (1);
  }
  particleSensor.setup();
  particleSensor.setPulseAmplitudeRed(0x0A);
  particleSensor.setPulseAmplitudeGreen(0);

  // ===== MPU6050 =====
  mpu6050.begin();

  // ===== ADC =====
  analogReadResolution(12);
  analogSetPinAttenuation(BAT_ADC_PIN, ADC_11db);
  pinMode(BAT_ADC_PIN, INPUT);

  // ===== BLE INIT =====
  BLEDevice::init("ESP32_HEADBAND_BLE");

  BLEServer *pServer = BLEDevice::createServer();
  BLEService *pService = pServer->createService(SERVICE_UUID);

  pCharacteristic = pService->createCharacteristic(
                      CHARACTERISTIC_UUID,
                      BLECharacteristic::PROPERTY_READ |
                      BLECharacteristic::PROPERTY_NOTIFY
                    );

  pCharacteristic->addDescriptor(new BLE2902());
  pService->start();

  BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
  pAdvertising->start();

  Serial.println("BLE Sensor Streaming Started");
}

void loop() {
  // ===== Read sensors =====
  long irValue = particleSensor.getIR();
  mpu6050.update();

  float gx = mpu6050.getGyroX();
  float gy = mpu6050.getGyroY();
  float gz = mpu6050.getGyroZ();

  float vBat = readBatteryVoltage();

  // ===== CSV =====
  String data = String(irValue) + "," +
                String(gx, 2) + "," +
                String(gy, 2) + "," +
                String(gz, 2) + "," +
                String(vBat, 2);

  // ===== BLE Notify =====
  pCharacteristic->setValue(data.c_str());
  pCharacteristic->notify();

  Serial.println(data);

  delay(20);   // 1 sec update (actually 20ms based on delay(20), user comment said 1 sec)
}
