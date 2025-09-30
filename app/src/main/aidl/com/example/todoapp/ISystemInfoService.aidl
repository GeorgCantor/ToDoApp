package com.example.todoapp;

import com.example.todoapp.ISystemCallback;
import com.example.todoapp.SystemInfo;
import com.example.todoapp.StorageInfo;

interface ISystemInfoService {
    SystemInfo getSystemInfo();
    StorageInfo getStorageInfo();
    int getBatteryLevel();
    long getAvailableMemory();
    void registerCallback(ISystemCallback callback);
    void unregisterCallback(ISystemCallback callback);
    void startMonitoring();
    void stopMonitoring();
}