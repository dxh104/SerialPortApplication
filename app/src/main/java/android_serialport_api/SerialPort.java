/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android_serialport_api;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class SerialPort {

    private static final String TAG = "SerialPort---->";
    private int dataBits = 8;//数据位,默认8；可选值为5~8
    private int parity = 0;//校验位；0:无校验位(NONE，默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
    private int stopBits = 1;//停止位，默认1；1:1位停止位；2:2位停止位
    private int flowCon = 0;//流控位，默认0；0:不使用流控 1:硬件流控 2:软件流控
    /*
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {

        /* Check access permission */
		// 检查是否获取了指定串口的读写权限
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
				// 如果没有获取指定串口的读写权限，则通过挂在到linux的方式修改串口的权限为可读写
				Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }

        mFd = open(device.getAbsolutePath(), baudrate, dataBits, parity, stopBits, flowCon, flags);
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    public SerialPort(File device, int baudrate, int dataBits, int parity, int stopBits, int flowCon, int flags) throws SecurityException, IOException {

        /* Check access permission */
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }

        mFd = open(device.getAbsolutePath(), baudrate, dataBits, parity, stopBits, flowCon, flags);
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    // Getters and setters
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    /**
     * @param path     串口文件地址
     * @param baudrate 波特率
     * @param dataBits 数据位,默认8；可选值为5~8
     * @param parity   校验位；0:无校验位(NONE，默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
     * @param stopBits 停止位，默认1；1:1位停止位；2:2位停止位
     * @param flowCon  流控位，默认0；0:不使用流控 1:硬件流控 2:软件流控
     * @param flags  O_RDWR  读写方式打开 | O_NOCTTY  不允许进程管理串口 | O_NDELAY   非阻塞
     * @return
     */
    // JNI
    private native static FileDescriptor open(String path, int baudrate, int dataBits, int parity, int stopBits, int flowCon, int flags);

    public native void close();

    static {
        System.loadLibrary("serial_port");
    }
}
