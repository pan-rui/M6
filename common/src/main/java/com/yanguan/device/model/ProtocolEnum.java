package com.yanguan.device.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-26 18:43)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public enum ProtocolEnum {
    SCHEDU(0,new String[]{"backup-1","company-2","imei-8","base1-2","base2-2","base3-2","base4-2","verify-2"},new int[]{2,2,1,1,1,1,2,1,1,1,1,2,1,1,1,1,2,1,1,1,1,2,1,1,1,1,2,1,1,1,1,2,2}),
    SCHEDU_SINGLE(1,new String[]{"backup-1","company-2","imei-8","base1-2","base2-2","base3-2","base4-2","sType-1","verify-2"},new int[]{2,2,1,1,1,1,2,1,2}),
    UP_DevAct(2,new String[]{"imei-8","~softVer...-5","~softVer..-3","~softVer.-2","softVer-1","~iccid-20","verify-2"},new int[]{2,8,205,203,202,1,220,1,2}),
    Take_DevID(7,new String[]{"pName-2","sVer-2","imei-8","~softVer...-5","~softVer..-3","~softVer.-2","softVer-1","~iccid-20","verify-2"},new int[]{2,2,2,4,2}),
    Active_Card(9, new String[]{"pName-2", "sVer-2", "devId-4", "~iccid-20", "verify-2"}, new int[]{2, 2, 2, 4, 1, 2}),
    App_Cmd0(20, new String[]{"pName-2", "sVer-2", "devId-4","cmdType-1","resultCode-1","verify-2"}, new int[]{2, 2, 2, 4, 1,  1, 2}),
    App_Cmd1(21, new String[]{"pName-2", "sVer-2", "devId-4","cmdType-1",  "p1-1","resultCode-1","verify-2"}, new int[]{2, 2, 2, 4, 1, 1, 1, 2}),
    App_Cmd9(29, new String[]{"pName-2", "sVer-2", "devId-4",  "p1-1","`lon-4","`lat-4","resultCode-1","verify-2"}, new int[]{2, 2, 2, 4, 1, 1, 1,104,104, 2}),
    Status_Update(3, new String[]{"pName-2","sVer-2","devId-4","sport-1","voltage-1","defence-1","power-1","brake-1","verify-2"}, new int[]{}),
    Real_Update1(51, new String[]{"pName-2","sVer-2","devId-4","sateSize-1","`lon1-4","`lat1-4","time1-4","verify-2"}, new int[]{}),
    Real_Update2(52, new String[]{"pName-2","sVer-2","devId-4","sateSize-1","`lon1-4","`lat1-4","time1-4","`lon2-4","`lat2-4","time2-4","verify-2"}, new int[]{}),
    Real_Update3(53, new String[]{"pName-2","sVer-2","devId-4","sateSize-1","`lon1-4","`lat1-4","time-4","`lon2-4","`lat2-4","time2-4","`lon3-4","`lat3-4","time3-4","verify-2"}, new int[]{}),
    Real_Update4(54, new String[]{"pName-2","sVer-2","devId-4","sateSize-1","`lon1-4","`lat1-4","time-4","`lon2-4","`lat2-4","time2-4","`lon3-4","`lat3-4","time3-4","`lon4-4","`lat4-4","time4-4","verify-2"}, new int[]{}),
    Real_Update5(55, new String[]{"pName-2","sVer-2","devId-4","sateSize-1","`lon1-4","`lat1-4","time-4","`lon2-4","`lat2-4","time2-4","`lon3-4","`lat3-4","time3-4","`lon4-4","`lat4-4","time4-4","`lon5-4","`lat5-4","time5-4","verify-2"}, new int[]{}),
    HeartBeat(16, new String[]{"devId-4", "gpsSignal-1", "verify-2"}, new int[]{2,1, 2});
    private Map<String, Integer> receiveMap;
    private int[] sendDataLeng;
    private int fieldLength;
    private int type;

/*    private ProtocolEnum(Map<String, Integer> bodyMap) {
        this.bodyMap=bodyMap;
        this.fieldLength = bodyMap.size();
    }*/

    private ProtocolEnum(int type,String[] receives,int[] sendDataLeng) {
        this.fieldLength=receives.length;
        this.type=type;
        receiveMap = new LinkedHashMap<>(fieldLength);
        for (String field : receives) {
            String[] strArry = field.split("-");
            receiveMap.put(strArry[0], Integer.parseInt(strArry[1]));
        }
        this.sendDataLeng=sendDataLeng;
/*        sendMap = new LinkedHashMap<>();
        for (String send : sends) {
            String[] strArry = send.split("-");
            sendMap.put(strArry[0], Integer.parseInt(strArry[1]));
        }*/
    }


    public Map<String, Integer> getReceiveMap() {
        return receiveMap;
    }

/*    public Map<String, Integer> getSendMap() {
        return sendMap;
    }*/

    public int[] getSendDataLeng() {
        return sendDataLeng;
    }

    public int getFieldLength() {
        return fieldLength;
    }

    public int getType() {
        return type;
    }

    public int getLengthForField(String fieldStr) {
        return receiveMap.get(fieldStr);
    }

    public static ProtocolEnum valueOfType(int typ) {
        for (ProtocolEnum protocol : values()) {
            if (protocol.type == typ) {
                return protocol;
            }
        }
        return null;
    }
}
