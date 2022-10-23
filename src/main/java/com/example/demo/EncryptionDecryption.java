package com.example.demo;

public class EncryptionDecryption {
    public static String encryption(String binaryFileString, int key){
        StringBuilder encryptedStringBuilder = new StringBuilder();
        String str = binaryFileString.substring(binaryFileString.length() - key);
        String str2 = binaryFileString.substring(binaryFileString.length()-2*key, binaryFileString.length()-key);
        String str3 = binaryFileString.substring(0,binaryFileString.length() - 2*key);
        StringBuilder h = new StringBuilder(str);
        h.reverse().toString();
        str=h.toString();
        encryptedStringBuilder.append(str2).append(str3).append(str);
        binaryFileString = encryptedStringBuilder.toString();
        StringBuilder z=new StringBuilder(binaryFileString);
        z.reverse().toString();
        binaryFileString=z.toString();
        return binaryFileString;
    }
}
