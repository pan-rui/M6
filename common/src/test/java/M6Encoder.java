import com.yanguan.device.util.ByteConvertUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 12:17)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class M6Encoder extends MessageToByteEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, String s, ByteBuf byteBuf) throws Exception {
        String[] strs = s.split(",");
        for (int i=0;i<strs.length;i++) {
            String str = strs[i];
            String[] strArry=null;
            if (!str.contains(".")||str.matches("^\\d+\\.\\d+$")) {
                strArry = new String[]{str};                //float 类型
            } else strArry= str.split("\\.");
            for (String ss : strArry) {
                System.out.println("-------"+ss);
                if(ss.matches("[0-9]+")){
                    long digit=Long.parseLong(ss);
                    if(digit<256){
                        byteBuf.writeByte((byte)(digit & 0xFF));
                    }else if(digit<65536){
                        byteBuf.writeBytes(ByteConvertUtil.shortToByteArry((short)digit));
                    }else if(digit<2147483648l){
                        byteBuf.writeBytes(ByteConvertUtil.intToByteArray((int)digit));
                    }else {
                        byteBuf.writeBytes(ByteConvertUtil.longToByteArry(digit));
                    }
                } else if(ss.contains(".")){
                    byteBuf.writeBytes(ByteConvertUtil.floatToByteArray(Float.parseFloat(ss)));
                }else
                    byteBuf.writeBytes(ByteConvertUtil.charArryToByteArry(ss));
            }
        }
    }
}
