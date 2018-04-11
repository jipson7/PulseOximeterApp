package com.utoronto.caleb.pulseoximeterapp.readers.bluetooth;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class BLEDataParser {
/*
    private static final int NUMBER_PHOTODETECTORS = 1; // Which sensor are we parsing.
    private static final String TAG = "BLE_DEVICE_PARSING";
    private boolean accl_enabled = true;

    void parseNRF52_DATA_NOTIFICATION(byte[] packet)
    {
        int[] packet_int = new int[20];
        for ( int i = 0; i < packet.length; i++)
            packet_int[i] = packet[i] & 0xFF;

        if ( packet[1] == FWAPI.NOTI_PERIODIC )
        {
            parseNRF52_DATA_NOTIFICATION_MAX86140_PERIODIC(packet_int);
        } else if ( packet[1] == FWAPI.NOTI_FLASHLOG )
        {
            parseNRF52_DATA_NOTIFICATION_MAX86140_ERROR(packet_int);
        } else if ( NUMBER_PHOTODETECTORS == 1 )
        {
            if ( accl_enabled ) {
                switch (channelCnt)
                {
                    case 1:
                        parseNRF52_DATA_NOTIFICATION_MAX86140_ACC_CH1(packet_int);
                        break;
                    case 2:
                        parseNRF52_DATA_NOTIFICATION_MAX86140_ACC_CH2(packet_int);
                        break;
                    case 3:
                        parseNRF52_DATA_NOTIFICATION_MAX86140_ACC_CH3(packet_int);
                        break;
                    case 4:
                        parseNRF52_DATA_NOTIFICATION_MAX86140_ACC_CH4(packet_int);
                        break;
                    case 5:
                        parseNRF52_DATA_NOTIFICATION_MAX86140_ACC_CH5(packet_int);
                        break;
                    case 6:
                        parseNRF52_DATA_NOTIFICATION_MAX86140_ACC_CH6(packet_int);
                        break;
                }
            } else {
                switch (channelCnt)
                {
                    case 1:
                        parseNRF52_DATA_NOTIFICATION_MAX86140_CH1(packet_int);
                        break;
                    case 2:
                        parseNRF52_DATA_NOTIFICATION_MAX86140_CH2(packet_int);
                        break;
                    case 3:
                        parseNRF52_DATA_NOTIFICATION_MAX86140_CH3(packet_int);
                        break;
                    case 4:
                        parseNRF52_DATA_NOTIFICATION_MAX86140_CH4(packet_int);
                        break;
                    case 5:
                        parseNRF52_DATA_NOTIFICATION_MAX86140_CH5(packet_int);
                        break;
                    case 6:
                        parseNRF52_DATA_NOTIFICATION_MAX86140_CH6(packet_int);
                        break;
                }
            }

        } else if ( NUMBER_PHOTODETECTORS == 2 ) {
            if ( accl_enabled ) {
                switch (channelCnt)
                {
                    case 1:
                        parseNRF52_DATA_NOTIFICATION_MAX86141_ACC_CH1(packet_int);
                        break;
                    case 2:
                        parseNRF52_DATA_NOTIFICATION_MAX86141_ACC_CH2(packet_int);
                        break;
                    case 3:
                        parseNRF52_DATA_NOTIFICATION_MAX86141_ACC_CH3(packet_int);
                        break;
                    case 4:
                        parseNRF52_DATA_NOTIFICATION_MAX86141_ACC_CH4(packet_int);
                        break;
                    case 5:
                        parseNRF52_DATA_NOTIFICATION_MAX86141_ACC_CH5(packet_int);
                        break;
                    case 6:
                        parseNRF52_DATA_NOTIFICATION_MAX86141_ACC_CH6(packet_int);
                        break;
                }
            } else {
                switch (channelCnt)
                {
                    case 1:
                        parseNRF52_DATA_NOTIFICATION_MAX86141_CH1(packet_int);
                        break;
                    case 2:
                        parseNRF52_DATA_NOTIFICATION_MAX86141_CH2(packet_int);
                        break;
                    case 3:
                        parseNRF52_DATA_NOTIFICATION_MAX86141_CH3(packet_int);
                        break;
                    case 4:
                        parseNRF52_DATA_NOTIFICATION_MAX86141_CH4(packet_int);
                        break;
                    case 5:
                        parseNRF52_DATA_NOTIFICATION_MAX86141_CH5(packet_int);
                        break;
                    case 6:
                        parseNRF52_DATA_NOTIFICATION_MAX86141_CH6(packet_int);
                        break;
                }
            }
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86140_PERIODIC(int[] packet)
    {
        Log.d(TAG, "PARSE PERIODIC NOTIFICATION");
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86140_ERROR(int[] packet)
    {
        Log.d(TAG, "parseNRF52_DATA_NOTIFICATION_MAX86140_ERROR:"+packet[2]+","+packet[3]+","+packet[4]+
                ","+packet[5]);
        switch(packet[2])
        {
            case FWAPI.FLASH_BUSY_ERROR:
                Toast.makeText(this, "FLASH_BUSY_ERROR", Toast.LENGTH_LONG).show();
                if ( dlgFlashLoggingAlert != null && dlgFlashLoggingAlert.isShowing() )
                    dlgFlashLoggingAlert.dismiss();
                stopSensor();
                break;
            case FWAPI.FLASH_FULL_ERROR:
                Toast.makeText(this, "FLASH_FULL_ERROR", Toast.LENGTH_LONG).show();
                if ( isStartRunning ) {
                    stopSensor();
                }
                break;
            case FWAPI.FLASH_READY:
                //Toast.makeText(this, "FLASH_LOG_READY", Toast.LENGTH_LONG).show();
                if ( isStartRunning ) {
                    stopSensor();
                    showAutoDismissDialog2("Notification", "Please download logging file before starting again");
                }
                else
                    startSensor();
                break;
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86140_CH1(int[] packet)
    {
        int data = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[5], packet[6], packet[7], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[11], packet[12], packet[13], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[17], packet[18], packet[19], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86140_CH2(int[] packet)
    {
        int data = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[5], packet[6], packet[7], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[11], packet[12], packet[13], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[17], packet[18], packet[19], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86140_CH3(int[] packet)
    {
        int data = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[5], packet[6], packet[7], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0
            data = makeCHData(packet[11], packet[12], packet[13], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
            data = makeCHData(packet[17], packet[18], packet[19], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86140_CH4(int[] packet)
    {
        int data = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[5], packet[6], packet[7], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0
            data = makeCHData(packet[11], packet[12], packet[13], FragmentPPG.CH3);
            fppg.addData(FragmentPPG.CH3, data);//ch0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[17], packet[18], packet[19], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
        } else if ( packet[1] == FWAPI.NOTI_SNSR1 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0
            data = makeCHData(packet[5], packet[6], packet[7], FragmentPPG.CH3);
            fppg.addData(FragmentPPG.CH3, data);//ch0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[11], packet[12], packet[13], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0
            data = makeCHData(packet[17], packet[18], packet[19], FragmentPPG.CH3);
            fppg.addData(FragmentPPG.CH3, data);//ch0
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86140_CH5(int[] packet)
    {
        int data = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[5], packet[6], packet[7], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0
            data = makeCHData(packet[11], packet[12], packet[13], FragmentPPG.CH3);
            fppg.addData(FragmentPPG.CH3, data);//ch0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH4);
            fppg.addData(FragmentPPG.CH4, data);//ch0
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86140_CH6(int[] packet)
    {
        int data = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[5], packet[6], packet[7], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0
            data = makeCHData(packet[11], packet[12], packet[13], FragmentPPG.CH3);
            fppg.addData(FragmentPPG.CH3, data);//ch0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH4);
            fppg.addData(FragmentPPG.CH4, data);//ch0
            data = makeCHData(packet[17], packet[18], packet[19], FragmentPPG.CH5);
            fppg.addData(FragmentPPG.CH5, data);//ch0
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86140_ACC_CH1(int[] packet)
    {
        int data = 0;
        double adata = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        FragmentAccel facc = (FragmentAccel)subFragment[3];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[5], packet[6], packet[7], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            adata = makeACCData(packet[8], packet[9]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[10], packet[11]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[12], packet[13]);
            facc.addData(FragmentAccel.ACCZ, adata);
            adata = makeACCData(packet[14], packet[15]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[16], packet[17]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[18], packet[19]);
            facc.addData(FragmentAccel.ACCZ, adata);
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86140_ACC_CH2(int[] packet)
    {
        int data = 0;
        double adata = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        FragmentAccel facc = (FragmentAccel)subFragment[3];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[5], packet[6], packet[7], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[11], packet[12], packet[13], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[17], packet[18], packet[19], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
        } else if ( packet[1] == FWAPI.NOTI_SNSR1 ) {
            adata = makeACCData(packet[2], packet[3]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[4], packet[5]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[6], packet[7]);
            facc.addData(FragmentAccel.ACCZ, adata);
            adata = makeACCData(packet[8], packet[9]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[10], packet[11]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[12], packet[13]);
            facc.addData(FragmentAccel.ACCZ, adata);
            adata = makeACCData(packet[14], packet[15]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[16], packet[17]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[18], packet[19]);
            facc.addData(FragmentAccel.ACCZ, adata);
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86140_ACC_CH3(int[] packet)
    {
        int data = 0;
        double adata = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        FragmentAccel facc = (FragmentAccel)subFragment[3];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[5], packet[6], packet[7], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0
            data = makeCHData(packet[11], packet[12], packet[13], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
            data = makeCHData(packet[17], packet[18], packet[19], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0
        } else if ( packet[1] == FWAPI.NOTI_SNSR1 ) {
            adata = makeACCData(packet[2], packet[3]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[4], packet[5]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[6], packet[7]);
            facc.addData(FragmentAccel.ACCZ, adata);
            adata = makeACCData(packet[8], packet[9]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[10], packet[11]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[12], packet[13]);
            facc.addData(FragmentAccel.ACCZ, adata);
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86140_ACC_CH4(int[] packet)
    {
        int data = 0;
        double adata = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        FragmentAccel facc = (FragmentAccel)subFragment[3];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[5], packet[6], packet[7], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0
            data = makeCHData(packet[11], packet[12], packet[13], FragmentPPG.CH3);
            fppg.addData(FragmentPPG.CH3, data);//ch0
            adata = makeACCData(packet[14], packet[15]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[16], packet[17]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[18], packet[19]);
            facc.addData(FragmentAccel.ACCZ, adata);
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86140_ACC_CH5(int[] packet)
    {
        int data = 0;
        double adata = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        FragmentAccel facc = (FragmentAccel)subFragment[3];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[5], packet[6], packet[7], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0
            data = makeCHData(packet[11], packet[12], packet[13], FragmentPPG.CH3);
            fppg.addData(FragmentPPG.CH3, data);//ch0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH4);
            fppg.addData(FragmentPPG.CH4, data);//ch0
        } else if ( packet[1] == FWAPI.NOTI_SNSR1 ) {
            adata = makeACCData(packet[2], packet[3]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[4], packet[5]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[6], packet[7]);
            facc.addData(FragmentAccel.ACCZ, adata);
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86140_ACC_CH6(int[] packet)
    {
        int data = 0;
        double adata = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        FragmentAccel facc = (FragmentAccel)subFragment[3];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0
            data = makeCHData(packet[5], packet[6], packet[7], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0
            data = makeCHData(packet[11], packet[12], packet[13], FragmentPPG.CH3);
            fppg.addData(FragmentPPG.CH3, data);//ch0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH4);
            fppg.addData(FragmentPPG.CH4, data);//ch0
            data = makeCHData(packet[17], packet[18], packet[19], FragmentPPG.CH5);
            fppg.addData(FragmentPPG.CH5, data);//ch0
        } else if ( packet[1] == FWAPI.NOTI_SNSR1 ) {
            adata = makeACCData(packet[2], packet[3]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[4], packet[5]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[6], packet[7]);
            facc.addData(FragmentAccel.ACCZ, adata);
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86141_CH1(int[] packet)
    {
        int data = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86141_CH2(int[] packet)
    {
        int data = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0

            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0,pd0

            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
        } else if ( packet[1] == FWAPI.NOTI_SNSR1 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0,pd0

            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0

            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0,pd0
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86141_CH3(int[] packet)
    {
        int data = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0,pd0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0,pd0
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86141_CH4(int[] packet)
    {
        int data = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0,pd0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0,pd0
        } else if ( packet[1] == FWAPI.NOTI_SNSR1 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH3);
            fppg.addData(FragmentPPG.CH3, data);//ch0,pd0
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86141_CH5(int[] packet)
    {
        int data = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0,pd0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0,pd0
        } else if ( packet[1] == FWAPI.NOTI_SNSR1 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH3);
            fppg.addData(FragmentPPG.CH3, data);//ch0,pd0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH4);
            fppg.addData(FragmentPPG.CH4, data);//ch0,pd0
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86141_CH6(int[] packet)
    {
        int data = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0,pd0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0,pd0
        } else if ( packet[1] == FWAPI.NOTI_SNSR1 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH3);
            fppg.addData(FragmentPPG.CH3, data);//ch0,pd0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH4);
            fppg.addData(FragmentPPG.CH4, data);//ch0,pd0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH5);
            fppg.addData(FragmentPPG.CH5, data);//ch0,pd0
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86141_ACC_CH1(int[] packet)
    {
        int data = 0;
        double adata = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        FragmentAccel facc = (FragmentAccel)subFragment[3];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
            //ch[FragmentPPG.CH0] = data;
            //if ( logFile != null) logFile.writeToFile(ch[FragmentPPG.CH0]);
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
            //ch[FragmentPPG.CH0] = data;
            //if ( logFile != null) logFile.writeToFile(ch[FragmentPPG.CH0]);
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
            //ch[FragmentPPG.CH0] = data;
            //if ( logFile != null) logFile.writeToFile(ch[FragmentPPG.CH0]);
        } else if ( packet[1] == FWAPI.NOTI_SNSR1 ) {
            adata = makeACCData(packet[2], packet[3]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[4], packet[5]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[6], packet[7]);
            facc.addData(FragmentAccel.ACCZ, adata);
            adata = makeACCData(packet[8], packet[9]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[10], packet[11]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[12], packet[13]);
            facc.addData(FragmentAccel.ACCZ, adata);
            adata = makeACCData(packet[14], packet[15]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[16], packet[17]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[18], packet[19]);
            facc.addData(FragmentAccel.ACCZ, adata);

        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86141_ACC_CH2(int[] packet)
    {
        int data = 0;
        double adata = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        FragmentAccel facc = (FragmentAccel)subFragment[3];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
            //ch[FragmentPPG.CH0] = data;
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0,pd0
            //ch[FragmentPPG.CH1] = data;
            //if ( logFile != null) logFile.writeToFile(ch[FragmentPPG.CH0],ch[FragmentPPG.CH1]);
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
            //ch[FragmentPPG.CH0] = data;
        } else if ( packet[1] == FWAPI.NOTI_SNSR1 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0,pd0
            //ch[FragmentPPG.CH1] = data;
            //if ( logFile != null) logFile.writeToFile(ch[FragmentPPG.CH0],ch[FragmentPPG.CH1]);
            adata = makeACCData(packet[8], packet[9]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[10], packet[11]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[12], packet[13]);
            facc.addData(FragmentAccel.ACCZ, adata);
            adata = makeACCData(packet[14], packet[15]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[16], packet[17]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[18], packet[19]);
            facc.addData(FragmentAccel.ACCZ, adata);
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86141_ACC_CH3(int[] packet)
    {
        int data = 0;
        double adata = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        FragmentAccel facc = (FragmentAccel)subFragment[3];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0,pd0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0,pd0
            //if ( logFile != null) logFile.writeToFile(makeCHData(packet[2], packet[3], packet[4]),
            //        makeCHData(packet[8], packet[9], packet[10]), makeCHData(packet[14], packet[15], packet[16]));
        } else if ( packet[1] == FWAPI.NOTI_SNSR1 ) {
            adata = makeACCData(packet[2], packet[3]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[4], packet[5]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[6], packet[7]);
            facc.addData(FragmentAccel.ACCZ, adata);
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86141_ACC_CH4(int[] packet)
    {
        int data = 0;
        double adata = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        FragmentAccel facc = (FragmentAccel)subFragment[3];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0,pd0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0,pd0
        } else if ( packet[1] == FWAPI.NOTI_SNSR1 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH3);
            fppg.addData(FragmentPPG.CH3, data);//ch0,pd0
            adata = makeACCData(packet[8], packet[9]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[10], packet[11]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[12], packet[13]);
            facc.addData(FragmentAccel.ACCZ, adata);
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86141_ACC_CH5(int[] packet)
    {
        int data = 0;
        double adata = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        FragmentAccel facc = (FragmentAccel)subFragment[3];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0,pd0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0,pd0
        } else if ( packet[1] == FWAPI.NOTI_SNSR1 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH3);
            fppg.addData(FragmentPPG.CH3, data);//ch0,pd0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH4);
            fppg.addData(FragmentPPG.CH4, data);//ch0,pd0
            adata = makeACCData(packet[14], packet[15]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[16], packet[17]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[18], packet[19]);
            facc.addData(FragmentAccel.ACCZ, adata);
        }
    }

    void parseNRF52_DATA_NOTIFICATION_MAX86141_ACC_CH6(int[] packet)
    {
        int data = 0;
        double adata = 0;
        FragmentPPG fppg = (FragmentPPG)subFragment[2];
        FragmentAccel facc = (FragmentAccel)subFragment[3];
        if ( packet[1] == FWAPI.NOTI_SNSR0 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH0);
            fppg.addData(FragmentPPG.CH0, data);//ch0,pd0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH1);
            fppg.addData(FragmentPPG.CH1, data);//ch0,pd0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH2);
            fppg.addData(FragmentPPG.CH2, data);//ch0,pd0
        } else if ( packet[1] == FWAPI.NOTI_SNSR1 ) {
            data = makeCHData(packet[2], packet[3], packet[4], FragmentPPG.CH3);
            fppg.addData(FragmentPPG.CH3, data);//ch0,pd0
            data = makeCHData(packet[8], packet[9], packet[10], FragmentPPG.CH4);
            fppg.addData(FragmentPPG.CH4, data);//ch0,pd0
            data = makeCHData(packet[14], packet[15], packet[16], FragmentPPG.CH5);
            fppg.addData(FragmentPPG.CH5, data);//ch0,pd0
        } else if ( packet[1] == FWAPI.NOTI_SNSR2 ) {
            adata = makeACCData(packet[2], packet[3]);
            facc.addData(FragmentAccel.ACCX, adata);
            adata = makeACCData(packet[4], packet[5]);
            facc.addData(FragmentAccel.ACCY, adata);
            adata = makeACCData(packet[6], packet[7]);
            facc.addData(FragmentAccel.ACCZ, adata);
        }
    }*/
}
