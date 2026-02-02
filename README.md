# sleep-band-android
Overview

This project is a fully offline mobile application for a sleep band that tracks sleep stages using PPG and accelerometer data, generates adaptive noise, and plays it via Bluetooth speakers to improve sleep quality.

No cloud services. No backend. No internet dependency.
All processing, prediction, storage, and audio generation happens on-device.

## High-Level Data Flow
Sleep Band (PPG + Accelerometer)
        ↓ BLE
Mobile App (Android / iOS)
 ├─ Raw Sensor Capture
 ├─ Signal Processing
 ├─ Sleep Stage Prediction (On-device model)
 ├─ Noise Frequency Selection
 ├─ Audio Generation
 ├─ Local Storage
        ↓ Bluetooth
     Speakers

## Key Features

BLE-based connection to sleep band

Real-time PPG + motion capture

On-device sleep stage prediction

Adaptive noise generation (pink / brown / frequency-modulated)

Offline sleep session history

Sleep insights & graphs

No login, no cloud, no data sync

## Supported Platforms
Platform	Status
Android	✅ Primary
iOS	🚧 Planned


## Tech Stack
** Android (Primary) **
Area	Technology
Language	Kotlin
UI	Jetpack Compose
Architecture	MVVM + Clean Architecture
BLE	Android BLE + Nordic BLE Library
Async	Coroutines + Flow
Audio	AudioTrack / SoundPool
Charts	MPAndroidChart
Storage	Room (SQLite)
DI	Hilt
ML	TensorFlow Lite (on-device)

## UI Flow (Aligned with Design)

Welcome / Onboarding

Profile Setup (minimal)

Device Scan & Connect

Session Start

Session Running (noise playback)

Session History

Sleep Insights

Preferences (optional)

## Sleep Processing Pipeline
Raw PPG + Accelerometer
        ↓
Signal Filtering & Feature Extraction
        ↓
Sleep Stage Prediction (30s epochs)
        ↓
Noise Frequency Selection
        ↓
Audio Playback

## Sleep Stages

Awake

Light Sleep

Deep Sleep

REM

## Audio Engine

Fully on-device

Low-latency generation

Noise types:

Pink noise

Brown noise

Frequency-modulated noise

Audio behavior adapts based on sleep stage

## Storage Strategy (Offline Only)
Data	Storage
Sleep sessions	Local DB (Room / CoreData)
Raw sensor data	Optional, limited retention
Model file	App bundle
Audio assets	App internal storage
Preferences	DataStore / UserDefaults

## Development Rules (Important)

❌ No cloud APIs

❌ No background network calls

❌ No BLE logic in UI layer

❌ No heavy ML models

✅ All long-running work off main thread

✅ BLE, audio, and ML are independent modules

## Branching Strategy
main        → Stable / release-ready
develop     → Active development
feature/*   → Individual features


Direct pushes to main are not allowed

All changes go via Pull Requests

Local Setup (Android)

Install Android Studio

Clone the repository

Open project

Sync Gradle

Run on real device (BLE required)

 ## Testing Guidelines

Test BLE stability with real hardware

Run overnight sleep sessions

Validate audio playback timing

Monitor battery consumption

Validate sleep graph correctness
