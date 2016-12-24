package com.yanguan.device.util;

import java.nio.ByteBuffer;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2016-11-26 17:09)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class ByteConvertUtil {
    //byte 数组与 int(4字节) 的相互转换
    public static int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static float byteArrayToFloat(byte[] b) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.put(b);
        buffer.flip();
        return buffer.getFloat();
    }

    public static byte[] floatToByteArray(float a) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putFloat(a);
        return buffer.array();
    }

    public static short byteArryToShort(byte[] b) {
        return (short) (b[1]&0xFF|(b[0] &0xFF)<<8);
    }

    public static byte[] shortToByteArry(int a) {
        return new byte[]{
                (byte) ((a >> 8) & 0xFF), (byte) (a & 0xFF)
        };
    }

    public static long byteArryToLong(byte[] b) {
/*        String prefixB=Integer.toBinaryString(b[7] &0xFF|  (b[6] &0xFF)<<8|    (b[5] &0xFF) <<16|    (b[4]&0xFF) <<24);
        String suffixB = Integer.toBinaryString((b[3] & 0xFF) << 32 | (b[2] & 0xFF) << 40 | (b[1] & 0xFF) << 48 | (b[0] & 0xFF) << 56);
        for(int i=32-suffixB.length();i>0;i--){
            suffixB=0+suffixB;
        }
        for(int i=32-prefixB.length();i>0;i--){
            prefixB=0+prefixB;
        }
        return Long.parseLong( suffixB+prefixB, 2);*/
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(b);
        buffer.flip();
        return buffer.getLong();
    }

    public static byte[] longToByteArry(long a) {
        return new byte[]{
                (byte)((a>>56)&0xFF),(byte)((a>>48)&0xFF),(byte)((a>>40)&0xFF),(byte)((a>>32)&0xFF),(byte)((a>>24)&0xFF),(byte)((a>>16)&0xFF),(byte)((a>>8)&0xFF),(byte)(a&0xFF)
        };
    }

    public static String byteArryToChars(byte[] bytes) {
        char[] chars = new char[bytes.length];
        for (int i=0;i<bytes.length;i++) {
            chars[i] = (char) bytes[i];
        }
        return new String(chars);
    }

    public static byte[] charArryToByteArry(String str) {
        byte[] b=new byte[str.length()];
        for(int i=0;i<str.length();i++){
            b[i] = (byte)str.charAt(i);
        }
        return b;
    }
    public static Object byteArryToObj(byte[] bytes) {
        switch (bytes.length) {
            case 1:
                return bytes[0] &0xFF;
            case 2:
                return byteArryToShort(bytes);
            case 4:
                return byteArrayToInt(bytes);
            case 8:
                return byteArryToLong(bytes);
        }
        return "";
    }

    public static byte[] objToByteArry(String str,int length) {
        switch (length) {
            case 1:
                return new byte[]{(byte) (Integer.parseInt(str)&0xFF)};
            case 2:
                return shortToByteArry(Integer.parseInt(str));
            case 4:
                return intToByteArray(Integer.parseInt(str));
            case 8:
                return longToByteArry(Long.parseLong(str));
        }
        return new byte[0];
    }
}
