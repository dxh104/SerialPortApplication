package com.example.serialportapplication.util;

import java.io.File;
import java.io.IOException;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

public class SerialPortUtil {
    //----获取/dev下的文件
    public static String[] getAllDevicesPath() {
        SerialPortFinder mSerialPortFinder = new SerialPortFinder();
        // 得到所有设备文件地址的数组
        // 实际上该操作并不需要，这里只是示例打印出所有的设备信息
        String[] entryValues = mSerialPortFinder.getAllDevicesPath();
        return entryValues;
    }

    //设备文件地址，波特率,方式打开
    public static SerialPort createSerialPort(String devicePathname, int baudrate, int flags) {
        SerialPort mSerialPort = null;
        try {
            // 打开/dev/ttyUSB0路径设备的串口
//            mSerialPort = new SerialPort(new File("/dev/ttyUSB0"), 9600, 0);
//            SerialPort mSerialPort = new SerialPort(new File("/dev/ttyS1"), 9600, 0);
            mSerialPort = new SerialPort(new File(devicePathname), baudrate, flags);
        } catch (IOException e) {
            System.out.println("找不到该设备文件");
        }
        return mSerialPort;
    }

    public static SerialPort createSerialPort(String devicePathname, int baudrate, int dataBits, int parity, int stopBits, int flowCon, int flags) {
        SerialPort mSerialPort = null;
        try {
            mSerialPort = new SerialPort(new File(devicePathname), baudrate, dataBits, parity, stopBits, flowCon, flags);
        } catch (IOException e) {
            System.out.println("找不到该设备文件");
        }
        return mSerialPort;
    }




}
