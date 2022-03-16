package com.idemia.idscreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Utility {
    public static boolean isIrisSensorAvailable() {
        String versionCode = getVersionCode();

        if (versionCode != null && !versionCode.isEmpty()) {
            if (versionCode.charAt(5) == 'I') {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean isNfcAvailable(){
        String versionCode = getVersionCode();

        if(versionCode != null && !versionCode.isEmpty()){
            if (versionCode.charAt(2) == 'M'){
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    private static String getVersionCode(){
        Process ifc = null;
        String versionCode = null;
        try {
            ifc = Runtime.getRuntime().exec("getprop persist.sys.ver.code");
            BufferedReader bis = new BufferedReader(new InputStreamReader(ifc.getInputStream()));
            versionCode = bis.readLine();
//            Log.d("Utility", "Version code: " + versionCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ifc != null) {
            ifc.destroy();
        }

        return versionCode;
    }

    public static boolean isSecureFirmware(){
        String fwVersion = getFirmwareVersion();

        if (fwVersion != null && !fwVersion.isEmpty()){
            if (fwVersion.startsWith("S")){
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private static String getFirmwareVersion(){
        Process ifc = null;
        String fwVersion = null;
        try {
            ifc = Runtime.getRuntime().exec("getprop ro.build.friendlyname");
            BufferedReader bis = new BufferedReader(new InputStreamReader(ifc.getInputStream()));
            fwVersion = bis.readLine();
//            Log.d("Utility", "Firmware version: " + fwVersion);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ifc != null) {
            ifc.destroy();
        }

        return fwVersion;
    }

    public static ArrayList<String> getFeatures() {
        ArrayList<String> featureList = new ArrayList<>();
        featureList.add("Fingerprint Sensor");
        featureList.add("Peripheral Management");
        featureList.add("Camera LED");
        featureList.add("SAM & Smartcard Readers");
        featureList.add("Iris Sensor");
        return featureList;
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static byte[] convertHexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String convertBytesToHex(byte[] bytes) {
        if (bytes == null)  return "";
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }

        return new String(hexChars);
    }
}
