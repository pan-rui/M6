import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2017-01-11 15:00)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class AgpsClient {
    public static byte[] hexStr2ByteArray(String hexString) {
        if (StringUtils.isEmpty(hexString))
            throw new IllegalArgumentException("this hexString must not be empty");

        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {
            //因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            //将hex 转换成byte   "&" 操作为了防止负数的自动扩展
            // hex转换成byte 其实只占用了4位，然后把高位进行右移四位
            // 然后“|”操作  低四位 就能得到 两个 16进制数转换成一个byte.
            //
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }
    public static void main(String[] args) throws IOException {
        Socket client=new Socket("192.168.199.198",10008);
        OutputStream out = client.getOutputStream();
        InputStream in = client.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        long sendTime=System.currentTimeMillis();
        out.write(hexStr2ByteArray("00 14 00 0e 00 00 01 cc 00 00 00 00 00 00 25 fc 00 00 0f 2a 00 00".replace(" ","")));
        out.flush();
        byte[] buf = new byte[4096];
        int len=0,offset=0;
        while ((len = in.read(buf)) > 0)
            bos.write(buf,0,len);
        bos.flush();
        byte[] totalData=bos.toByteArray();
        in.close();
        out.close();
        System.out.println(totalData.length);
    }
}
