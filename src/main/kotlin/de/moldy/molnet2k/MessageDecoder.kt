package de.moldy.molnet2k

import de.moldy.molnet2k.exchange.Message
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.ReplayingDecoder

class MessageDecoder : ReplayingDecoder<Message>() {

//    public static final int MAX_MESSAGE_SIZE = 500_000;

    //    public static final int MAX_MESSAGE_SIZE = 500_000;
    override fun decode(ctx: ChannelHandlerContext?, inBytes: ByteBuf, out: MutableList<Any?>) {
        val nextMassageSize = inBytes.readInt()
        val read = inBytes.readBytes(nextMassageSize)
        val message = Message()
        message.receive(read)
        out.add(message)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        if (cause is DecoderException) {
            cause.printStackTrace()
            ctx.close()
            return
        }
        super.exceptionCaught(ctx, cause)
    }

//    public static class MaximumSizeExceeded extends RuntimeException {
//        private static final long serialVersionUID = -1833615422763957637L;
//
//        public MaximumSizeExceeded(String s) {
//            super(s);
//        }
//    }
}