package com.yanguan.device.decoder;

import com.yanguan.device.model.ProtocolEnum;
import com.yanguan.device.util.ByteConvertUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 12:07)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class M6Decoder extends ByteToMessageDecoder {
    private static Logger logger = Logger.getLogger(M6Decoder.class);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int length = byteBuf.readableBytes();
        logger.info("byteBuf长度\t" + length);
/*        for (int i = 0; i < length; i++) {
            byte by = byteBuf.readByte();
            System.out.println("================" + i + "===============");
            System.out.println(by);
        }*/
        if (length < 8) {
            byteBuf.readerIndex(byteBuf.readerIndex() + length);
            return;
        }
        byte[] iType = new byte[2];
        byteBuf.readBytes(iType, 0, 2);
        final Map<String, Object> data = new LinkedHashMap();
        short itype = ByteConvertUtil.byteArryToShort(iType);
        ProtocolEnum protocol = ProtocolEnum.valueOfType(itype);
//        StringBuffer sb = new StringBuffer(String.valueOf(itype));
        data.put("iType", itype);
        boolean[] join = new boolean[]{false};
        if (protocol == null) return;
//        Map<String,Integer> receiveMap=protocol.getReceiveMap().keySet()
        protocol.getReceiveMap().forEach((k, v) -> {
            String ke = k.replaceAll("[~`]{1}", "");
            if (join[0]) {
                byte[] bytes = new byte[v];
                byteBuf.readBytes(bytes);
                if (k.startsWith("~")) {
                    data.put(ke, data.get(ke + ".") + "." + ByteConvertUtil.byteArryToChars(bytes));
                } else {
                    data.put(ke, data.get(ke + ".") + "." + ByteConvertUtil.byteArryToObj(bytes));
                }
                join[0] = false;
            } else {
                byte[] bytes = new byte[v];
                byteBuf.readBytes(bytes);
                if (k.startsWith("~")) {
                    data.put(ke, ByteConvertUtil.byteArryToChars(bytes));
                } else if (k.startsWith("`")) {
                    data.put(ke, ByteConvertUtil.byteArrayToFloat(bytes));
                } else {
                    data.put(ke, ByteConvertUtil.byteArryToObj(bytes));
                }
            }
            if (k.endsWith("."))
                join[0] = true;
        });
        list.add(data);
    }
}
