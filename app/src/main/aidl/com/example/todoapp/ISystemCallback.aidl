package com.example.todoapp;

interface ISystemCallback {
    void onBatteryChanged(int level);
    void onStorageLow();
    void onMemoryLow();
}