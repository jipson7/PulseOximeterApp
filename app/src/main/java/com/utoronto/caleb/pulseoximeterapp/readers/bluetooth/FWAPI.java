package com.utoronto.caleb.pulseoximeterapp.readers.bluetooth;

import android.util.Log;

import com.utoronto.caleb.pulseoximeterapp.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FWAPI {

    private static final String TAG = BLEDeviceReader.TAG;

    //Enum
    //target device
    public static final byte NRF                    = 0;
    public static final byte NIM                    = 1;
    public static final byte I2C                    = 2;
    public static final byte SENS_OPT               = 3;
    public static final byte SENS_ACC               = 4;

    //target device read/write parameter
    public static final byte WRITEPARAM             = 0;
    public static final byte READPARAM              = 1;

    //messages
    public static final byte MSG_NRF_FWVER          = 0;
    public static final byte MSG_NIM_FWVER          = 1;
    public static final byte MSG_ENABLE_SENSORS     = 2;
    public static final byte MSG_ENABLE_FLASHLOG    = 3;
    public static final byte MSG_REGVAL             = 4;
    public static final byte MSG_NRF_CONFIG_VLED    = 5;
    public static final byte MSG_NRF_CONFIG_VDD     = 6;
    public static final byte MSG_NRF_RTCPRESCALE    = 7;
    public static final byte MSG_NRF_USEACC         = 8;
    public static final byte MSG_NIM_ISBUSY         = 9;
    public static final byte MSG_NIM_ISFULL         = 10;
    public static final byte MSG_NRF_CONFIG_VDDLDO  = 11;
    public static final byte MSG_NRF_CONFIG_BBST    = 12;
    public static final byte MSG_NRF_BATTERY        = 13;
    public static final byte MSG_NRF_NUMPD          = 14;
    public static final byte MSG_NRF_CONNPARAMS     = 15;
    public static final byte MSG_NRF_CONFIG_PMIC    = 16;
    public static final byte MSG_NRF_DACCAL         = 17;
    public static final byte MSG_NIM_ENABLE_FCLK    = 18;

    //NRF specific commands
    public static final byte NRF_FWVER              = 0;
    public static final byte NRF_ENABLE_SENSORS     = 1;
    public static final byte NRF_CONFIG_VLED        = 2;
    public static final byte NRF_CONFIG_VDD         = 3;
    public static final byte NRF_RTCPRESCALE        = 4;
    public static final byte NRF_USEACC             = 5;
    public static final byte NRF_CONFIG_VDDLDO      = 6;
    public static final byte NRF_CONFIG_BBST        = 7;
    public static final byte NRF_BATTERY            = 8;
    public static final byte NRF_NUMPD              = 9;
    public static final byte NRF_CONNPARAMS         = 10;
    public static final byte NRF_CONFIG_PMIC        = 11;
    public static final byte NRF_DACCAL             = 12;

    //NIM specific commands
    public static final byte NIM_FWVER              = 0;
    public static final byte NIM_ENABLE_FLASHLOG    = 1;
    public static final byte NIM_FLASHLOG_WRITE     = 2;
    public static final byte NIM_ISBUSY             = 3;
    public static final byte NIM_ISFULL             = 4;
    public static final byte NIM_ENABLE_FCLK        = 5;

    //I2C/SENS_OPT/SENS_ACC commands
    public static final byte READREG                = 0;
    public static final byte RMW                    = 1;

    //flashlog error codes
    public static final byte FLASH_NO_ERROR         = 0;
    public static final byte FLASH_BUSY_ERROR       = 1;
    public static final byte FLASH_FULL_ERROR       = 2;
    public static final byte FLASH_READY            = 3;

    //notification packet codes
    public static final byte NOTI_SNSR0             = 0;
    public static final byte NOTI_SNSR1             = 1;
    public static final byte NOTI_SNSR2             = 2;
    public static final byte NOTI_PERIODIC          = 3;
    public static final byte NOTI_FLASHLOG             = 4;

    public static final byte STOP                   = 0;
    public static final byte START                  = 1;

    public static final byte BUCKBOOST              = 0;
    public static final byte LDO                    = 1;


    public static byte[] setNRF_FWVER()
    {
        byte[] configData = new byte[2];
        configData[0] = NRF;
        configData[1] = NRF_FWVER;
        return configData;
    }

    public static String getNRF_FWVER(byte[] packet)
    {
        String NRF_FWVER_String = "";
        if ( packet.length > 6 )
        {
            NRF_FWVER_String += packet[1]+"."+packet[2]+" "+
                    (((packet[3] & 0xff )<< 8) + (packet[4] & 0xff))+"-"+
                    String.format("%02d", (packet[5]&0xff))+"-"+ String.format("%02d", (packet[6]&0xff));
        } else {
            NRF_FWVER_String += "No info";
        }
        return NRF_FWVER_String;
    }

    public static byte[] setNIM_FWVER()
    {
        byte[] configData = new byte[2];
        configData[0] = NIM;
        configData[1] = NIM_FWVER;
        return configData;
    }

    public static String getNIM_FWVER(byte[] packet)
    {
        String NIM_FWVER_String = "";
        if ( packet.length > 6 )
        {
            NIM_FWVER_String += packet[1]+"."+packet[2]+" "+
                    (((packet[3] & 0xff )<< 8) + (packet[4] & 0xff))+"-"+
                    String.format("%02d", (packet[5]&0xff))+"-"+ String.format("%02d", (packet[6]&0xff));
        } else {
            NIM_FWVER_String += "No info";
        }
        return NIM_FWVER_String;
    }

    public static byte[] setNRF_ENABLE_SENSORS(byte value)
    {
        byte[] configData = new byte[4];
        configData[0] = NRF;
        configData[1] = NRF_ENABLE_SENSORS;
        configData[2] = WRITEPARAM;
        configData[3] = value;
        return configData;
    }

    public static byte[] setNRF_ENABLE_SENSORS()
    {
        byte[] configData = new byte[3];
        configData[0] = NRF;
        configData[1] = NRF_ENABLE_SENSORS;
        configData[2] = READPARAM;
        return configData;
    }

    public static byte getNRF_ENABLE_SENSORS(byte[] packet)
    {
        return packet[1];
    }

    public static byte[] setNIM_ENABLE_FLASHLOG(byte value)
    {
        byte[] configData = new byte[12];
        configData[0] = NIM;
        configData[1] = NIM_ENABLE_FLASHLOG;
        configData[2] = WRITEPARAM;
        configData[3] = value;

        String timeStamp = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss").format(Calendar.getInstance().getTime());
        String[] timeArray = timeStamp.split(",");
        //Log.d(TAG, "Timeformat:"+timeArray[0].substring(0,2)+","+timeArray[0].substring(2,4));
        //configData[4] = (byte)Integer.parseInt(timeArray[0].substring(0,2));
        //configData[5] = (byte)Integer.parseInt(timeArray[0].substring(2,4));
        int year = Integer.parseInt(timeArray[0]);
        configData[4] = (byte)((year >> 8)&0xff);
        configData[5] = (byte)(year & 0xff);
        configData[6] = (byte) Integer.parseInt(timeArray[1]);
        configData[7] = (byte) Integer.parseInt(timeArray[2]);
        configData[8] = (byte) Integer.parseInt(timeArray[3]);
        configData[9] = (byte) Integer.parseInt(timeArray[4]);
        configData[10] = (byte) Integer.parseInt(timeArray[5]);
        configData[11] = 0;
        Log.d(TAG, "Test Normal");
        return configData;
    }

    public static byte[] setNIM_ENABLE_FLASHLOG()
    {
        byte[] configData = new byte[3];
        configData[0] = NIM;
        configData[1] = NIM_ENABLE_FLASHLOG;
        configData[2] = READPARAM;
        return configData;
    }

    public static byte getNIM_ENABLE_FLASHLOG(byte[] packet)
    {
        return packet[1];
    }

    public static byte[] setNRF_CONFIG_VLED(byte value)
    {
        byte[] configData = new byte[4];
        configData[0] = NRF;
        configData[1] = NRF_CONFIG_VLED;
        configData[2] = WRITEPARAM;
        configData[3] = value;
        return configData;
    }

    public static byte[] setNRF_CONFIG_VLED()
    {
        byte[] configData = new byte[3];
        configData[0] = NRF;
        configData[1] = NRF_CONFIG_VLED;
        configData[2] = READPARAM;
        return configData;
    }

    public static byte getNRF_CONFIG_VLED(byte[] packet)
    {
        //Log.d(TAG, "VLED:"+packet[1]);
        return packet[1];
    }

    public static byte[] setNRF_CONFIG_VDD(byte value)
    {
        byte[] configData = new byte[4];
        configData[0] = NRF;
        configData[1] = NRF_CONFIG_VDD;
        configData[2] = WRITEPARAM;
        configData[3] = value;
        return configData;
    }

    public static byte[] setNRF_CONFIG_VDD()
    {
        byte[] configData = new byte[3];
        configData[0] = NRF;
        configData[1] = NRF_CONFIG_VDD;
        configData[2] = READPARAM;
        return configData;
    }

    public static byte getNRF_CONFIG_VDD(byte[] packet)
    {
        //Log.d(TAG, "VDD:"+packet[1]);
        return packet[1];
    }

    public static byte[] setNRF_RTCPRESCALE(byte value)
    {
        byte[] configData = new byte[4];
        configData[0] = NRF;
        configData[1] = NRF_RTCPRESCALE;
        configData[2] = WRITEPARAM;
        configData[3] = value;
        return configData;
    }

    public static byte[] setNRF_RTCPRESCALE()
    {
        byte[] configData = new byte[3];
        configData[0] = NRF;
        configData[1] = NRF_RTCPRESCALE;
        configData[2] = READPARAM;
        return configData;
    }

    public static byte getNRF_RTCPRESCALE(byte[] packet)
    {
        return packet[1];
    }

    public static byte[] setNRF_USEACC(byte value)
    {
        byte[] configData = new byte[4];
        configData[0] = NRF;
        configData[1] = NRF_USEACC;
        configData[2] = WRITEPARAM;
        configData[3] = value;
        return configData;
    }

    public static byte[] setNRF_USEACC()
    {
        byte[] configData = new byte[3];
        configData[0] = NRF;
        configData[1] = NRF_USEACC;
        configData[2] = READPARAM;
        return configData;
    }

    public static byte getNRF_USEACC(byte[] packet)
    {
        //Log.d(TAG, "VDD:"+packet[1]);
        return packet[1];
    }

    public static byte[] setNRF_CONFIG_VDDLDO(byte value)
    {
        byte[] configData = new byte[4];
        configData[0] = NRF;
        configData[1] = NRF_CONFIG_VDDLDO;
        configData[2] = WRITEPARAM;
        configData[3] = value;
        return configData;
    }

    public static byte[] setNRF_CONFIG_VDDLDO()
    {
        byte[] configData = new byte[3];
        configData[0] = NRF;
        configData[1] = NRF_CONFIG_VDDLDO;
        configData[2] = READPARAM;
        return configData;
    }

    public static byte getNRF_CONFIG_VDDLDO(byte[] packet)
    {
        //Log.d(TAG, "VDD:"+packet[1]);
        return packet[1];
    }

    public static byte[] setNRF_CONFIG_BBST(byte valueBBstIset, byte valueBBstVset)
    {
        byte[] configData = new byte[5];
        configData[0] = NRF;
        configData[1] = NRF_CONFIG_BBST;
        configData[2] = WRITEPARAM;
        configData[3] = valueBBstIset;
        configData[4] = valueBBstVset;
        return configData;
    }

    public static byte[] setNRF_CONFIG_BBST()
    {
        byte[] configData = new byte[3];
        configData[0] = NRF;
        configData[1] = NRF_CONFIG_BBST;
        configData[2] = READPARAM;
        return configData;
    }

    public static byte[] setNRF_CONNPARAMS(byte value)
    {
        byte[] configData = new byte[4];
        configData[0] = NRF;
        configData[1] = NRF_CONNPARAMS;
        configData[2] = WRITEPARAM;
        configData[3] = value;
        return configData;
    }

    public static byte[] setNRF_CONNPARAMS()
    {
        byte[] configData = new byte[3];
        configData[0] = NRF;
        configData[1] = NRF_CONNPARAMS;
        configData[2] = READPARAM;
        return configData;
    }

    public static byte getNRF_CONNPARAMS(byte[] packet)
    {
        //Log.d(TAG, "VDD:"+packet[1]);
        return packet[1];
    }

    public static byte[] setNRF_BATTERY()
    {
        byte[] configData = new byte[2];
        configData[0] = NRF;
        configData[1] = NRF_BATTERY;
        return configData;
    }

    public static byte[] setNRF_NUMPD()
    {
        byte[] configData = new byte[2];
        configData[0] = NRF;
        configData[1] = NRF_NUMPD;
        return configData;
    }

    public static byte[] setNIM_ISBUSY()
    {
        byte[] configData = new byte[2];
        configData[0] = NIM;
        configData[1] = NIM_ISBUSY;
        return configData;
    }

    public static byte[] setNIM_ISFULL()
    {
        byte[] configData = new byte[2];
        configData[0] = NIM;
        configData[1] = NIM_ISFULL;
        return configData;
    }

    public static byte[] setI2C_READREG(byte registerAddr, byte len, byte slaveAddr)
    {
        byte[] configData = new byte[5];
        configData[0] = I2C;
        configData[1] = READREG;
        configData[2] = registerAddr;
        configData[3] = len;
        configData[4] = slaveAddr;
        return configData;
    }

    public static byte[] getI2C_READREG(byte[] packet, byte len)
    {
        byte[] resultData = new byte[len];
        for ( int i = 0; i < len; i++ )
        {
            resultData[i] = packet[4-i];
        }
        return resultData;
    }

    public static byte[] setI2C_RMW(byte registerAddr, byte stopBit, byte startBit, byte writeVal, byte slaveAddr)
    {
        byte[] configData = new byte[7];
        configData[0] = I2C;
        configData[1] = RMW;
        configData[2] = registerAddr;
        configData[3] = stopBit;
        configData[4] = startBit;
        configData[5] = writeVal;
        configData[6] = slaveAddr;
        return configData;
    }

    public static byte[] setSENS_OPT_READREG(byte registerAddr, byte len)
    {
        byte[] configData = new byte[4];
        configData[0] = SENS_OPT;
        configData[1] = READREG;
        configData[2] = registerAddr;
        configData[3] = len;
        return configData;
    }

    public static byte[] getSENS_OPT_READREG(byte[] packet, byte len)
    {
        byte[] resultData = new byte[len];
        for ( int i = 0; i < len; i++ )
        {
            resultData[i] = packet[4-i];
        }
        return resultData;
    }

    public static byte[] setSENS_OPT_RMW(byte registerAddr, byte stopBit, byte startBit, byte writeVal)
    {
        byte[] configData = new byte[6];
        configData[0] = SENS_OPT;
        configData[1] = RMW;
        configData[2] = registerAddr;
        configData[3] = stopBit;
        configData[4] = startBit;
        configData[5] = writeVal;
        return configData;
    }

    public static byte[] setSENS_ACC_READREG(byte registerAddr, byte len)
    {
        byte[] configData = new byte[4];
        configData[0] = SENS_ACC;
        configData[1] = READREG;
        configData[2] = registerAddr;
        configData[3] = len;
        return configData;
    }

    public static byte[] getSENS_ACC_READREG(byte[] packet, byte len)
    {
        byte[] resultData = new byte[len];
        for ( int i = 0; i < len; i++ )
        {
            resultData[i] = packet[4-i];
        }
        return resultData;
    }

    public static byte[] setSENS_ACC_RMW(byte registerAddr, byte stopBit, byte startBit, byte writeVal)
    {
        byte[] configData = new byte[6];
        configData[0] = SENS_ACC;
        configData[1] = RMW;
        configData[2] = registerAddr;
        configData[3] = stopBit;
        configData[4] = startBit;
        configData[5] = writeVal;
        return configData;
    }


    public static byte[] setNIM_FLASHLOG_WRITE(byte[] value)
    {
        byte[] configData = new byte[20];
        configData[0] = NIM;
        configData[1] = NIM_FLASHLOG_WRITE;
        for (int i = 0; i < value.length; i++)
        {
            configData[2+i] = value[i];
        }

        return configData;
    }

    public static byte[] setNRF_CONFIG_PMIC(int len, byte[] value, byte opcode)
    {
        byte[] configData = new byte[5+len];
        configData[0] = NRF;
        configData[1] = NRF_CONFIG_PMIC;
        configData[2] = WRITEPARAM;
        configData[3] = (byte)len;
        int i = 0;
        for ( i = 0; i < len; i++) {
            configData[4+i] = value[i];
        }
        configData[4+i] = opcode;
        return configData;
    }

    public static byte[] setNRF_CONFIG_PMIC(int len, byte opcode)
    {
        byte[] configData = new byte[5];
        configData[0] = NRF;
        configData[1] = NRF_CONFIG_PMIC;
        configData[2] = READPARAM;
        configData[3] = (byte)len;
        configData[4] = opcode;
        return configData;
    }

    public static byte[] setNRF_DACCAL()
    {
        byte[] configData = new byte[3];
        configData[0] = NRF;
        configData[1] = NRF_DACCAL;
        configData[2] = READPARAM;
        return configData;
    }

    public static byte getNRF_DACCAL(byte[] packet)
    {
        //Log.d(TAG, "VDD:"+packet[1]);
        return packet[2]; //return status
    }

    public static byte[] setNIM_ENABLE_FCLK(byte value)
    {
        byte[] configData = new byte[4];
        configData[0] = NIM;
        configData[1] = NIM_ENABLE_FCLK;
        configData[2] = WRITEPARAM;
        configData[3] = value;
        return configData;
    }

    public static byte[] setNIM_ENABLE_FCLK()
    {
        byte[] configData = new byte[3];
        configData[0] = NIM;
        configData[1] = NIM_ENABLE_FCLK;
        configData[2] = READPARAM;
        return configData;
    }

    public static byte getNIM_ENABLE_FCLK(byte[] packet)
    {
        return packet[1];
    }

}
