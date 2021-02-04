package com.example.serialportapplication.util;


import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android_serialport_api.SerialPort;

public class SerialPortHelper {
    private volatile static SerialPortHelper instance;

    public static SerialPortHelper getInstance() {
        if (instance == null) {
            synchronized (SerialPortHelper.class) {
                if (instance == null) {
                    instance = new SerialPortHelper();
                }
            }
        }
        return instance;
    }

    private SerialPort mSerialPort;

    public SerialPortHelper init(String devicePathname, int baudrate, int flags) {
        if (mSerialPort == null)
            mSerialPort = SerialPortUtil.createSerialPort(devicePathname, baudrate, flags);
        isReciveThreadIntercept = false;
        startReciveThread();
        startSendThread();
        return instance;
    }

    private Thread reciveThread;
    private boolean isReciveThreadIntercept;

    private void startReciveThread() {
        if (reciveThread == null) {
            reciveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!isReciveThreadIntercept) {
                            byte[] buffer = new byte[1024];
                            if (mSerialPort == null)
                                break;
                            int size = mSerialPort.getInputStream().read(buffer);
                            byte[] readBytes = new byte[size];
                            System.arraycopy(buffer, 0, readBytes, 0, size);
                            onReceiveDataListener.OnReceive(new String(readBytes, "GBK"));
                            onReceiveDataListener.OnReceive(readBytes);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            reciveThread.start();
        }
    }


    private ExecutorService sendThreadExecutor;

    private void startSendThread() {
        if (sendThreadExecutor == null)
            sendThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public void sendData(final String data) {
        sendThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                // 写入数据
                try {
                    byte[] bytes = data.getBytes();
                    if(mSerialPort==null)
                        return;
                    OutputStream out = mSerialPort.getOutputStream();
                    if (out == null)
                        return;
                    out.write(bytes);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void sendData(final byte[] data) {
        sendThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                OutputStream out = mSerialPort.getOutputStream();
                // 写入数据
                try {
                    if (out == null)
                        return;
                    out.write(data);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void destroy() {
        if (sendThreadExecutor != null && !sendThreadExecutor.isShutdown()) {
            // 关闭线程池
            sendThreadExecutor.shutdown();
        }
        isReciveThreadIntercept = true;
        if (mSerialPort != null) {
            try {
                if (mSerialPort.getInputStream() != null)
                    mSerialPort.getInputStream().close();
                if (mSerialPort.getOutputStream() != null)
                    mSerialPort.getOutputStream().close();
                mSerialPort.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        instance = null;
    }

    public void setOnReceiveDataListener(OnReceiveDataListener onReceiveDataListener) {
        this.onReceiveDataListener = onReceiveDataListener;
    }

    private OnReceiveDataListener onReceiveDataListener = new OnReceiveDataListener() {
        @Override
        public void OnReceive(String data) {
            Log.i("OnReceiveDataListener-----------", "OnReceive:  String-data=" + data);
        }

        @Override
        public void OnReceive(byte[] data) {
            Log.i("OnReceiveDataListener-----------", "OnReceive: byte[]-data.length=" + data.length);

        }
    };

    public interface OnReceiveDataListener {
        void OnReceive(String data);

        void OnReceive(byte[] data);
    }
}
