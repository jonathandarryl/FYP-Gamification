/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.session;

import java.util.*;

/**
 *
 * @author chongyangsun
 */
public class GeneratePassword {
    /*public static String createPassword() {
        int len=8;
        char[] pwd = new char[len];
        int c = 'A';
        int rand = 0;
        for (int i=0; i < 8; i++) {
            rand = (int)(Math.random() * 3);
            switch(rand) {
            case 0: c = '0' + (int)(Math.random() * 10); break;
            case 1: c = 'a' + (int)(Math.random() * 26); break;
            case 2: c = 'A' + (int)(Math.random() * 26); break;
            }
            pwd[i] = (char)c;
        }
        return new String(pwd);
    }*/
    

    private static final String ALPHA_CAPS  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA   = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUM     = "0123456789";
    private static final String SPL_CHARS   = "!@#$%^&*_=+-/";
    
    private static int minLen = 8;
    private static int maxLen = 12;
    private static int noOfCAPSAlpha = 1;
    private static int noOfDigits = 1;
    private static int noOfSplChars = 1;
 
    public static String createPassword(/*int minLen, int maxLen, int noOfCAPSAlpha, int noOfDigits, int noOfSplChars*/) {
        if(minLen > maxLen)
            throw new IllegalArgumentException("Min. Length > Max. Length!");
        if( (noOfCAPSAlpha + noOfDigits + noOfSplChars) > minLen )
            throw new IllegalArgumentException("Min. Length should be atleast sum of (CAPS, DIGITS, SPL CHARS) Length!");
        Random rnd = new Random();
        int len = rnd.nextInt(maxLen - minLen + 1) + minLen;
        char[] pswd = new char[len];
        int index = 0;
        for (int i = 0; i < noOfCAPSAlpha; i++) {
            index = getNextIndex(rnd, len, pswd);
            pswd[index] = ALPHA_CAPS.charAt(rnd.nextInt(ALPHA_CAPS.length()));
        }
        for (int i = 0; i < noOfDigits; i++) {
            index = getNextIndex(rnd, len, pswd);
            pswd[index] = NUM.charAt(rnd.nextInt(NUM.length()));
        }
        for (int i = 0; i < noOfSplChars; i++) {
            index = getNextIndex(rnd, len, pswd);
            pswd[index] = SPL_CHARS.charAt(rnd.nextInt(SPL_CHARS.length()));
        }
        for(int i = 0; i < len; i++) {
            if(pswd[i] == 0) {
                pswd[i] = ALPHA.charAt(rnd.nextInt(ALPHA.length()));
            }
        }
        return new String(pswd);
    }
 
    private static int getNextIndex(Random rnd, int len, char[] pswd) {
        int index = rnd.nextInt(len);
        while(pswd[index = rnd.nextInt(len)] != 0);
        return index;
    }
}
