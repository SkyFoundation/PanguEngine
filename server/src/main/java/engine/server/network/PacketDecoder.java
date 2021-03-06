package engine.server.network;

import engine.Platform;
import engine.server.network.packet.Packet;
import engine.server.network.packet.PacketProvider;
import engine.server.network.packet.UnrecognizedPacketException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() != 0){
            var wrapper = new PacketBuf(in);
            var id = wrapper.readVarInt();
            Packet packet = Platform.getEngine().getRegistryManager().getRegistry(PacketProvider.class).orElseThrow().getValue(id).create();
            if(packet == null){
                throw new UnrecognizedPacketException("Unknown packet id: " + id);
            }
            else{
                packet.read(wrapper);
                if(wrapper.readableBytes() > 0){
                    throw new IOException(String.format("Packet #%d (%s) left out %d bytes while reading buffer!", id, packet.getClass().getSimpleName(), wrapper.readableBytes()));
                }
                else{
                    out.add(packet);
                }
            }
        }
    }
}
