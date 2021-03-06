package engine.server.network;

import engine.Platform;
import engine.server.event.NetworkDisconnectedEvent;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.packet.Packet;
import engine.server.network.packet.PacketDisconnect;
import engine.util.Side;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import javax.annotation.Nullable;

public class NetworkHandler extends SimpleChannelInboundHandler<Packet> {

    private Channel channel;
    //which is THIS handler located
    private final Side instanceSide;
    private ConnectionStatus status;

    public NetworkHandler(Side side){
        instanceSide = side;
        status = ConnectionStatus.HANDSHAKE;
    }

    public Side getSide() {
        return instanceSide;
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    public void setStatus(ConnectionStatus status) {
        this.status = status;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        closeChannel();
    }

    public void closeChannel() {
        closeChannel("");
    }

    public void closeChannel(String reason){
        if(this.channel != null && this.channel.isOpen()){
            this.channel.close().awaitUninterruptibly();
            Platform.getEngine().getEventBus().post(new NetworkDisconnectedEvent(reason));
        }
    }

    public boolean isLocal() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        Platform.getEngine().getEventBus().post(new PacketReceivedEvent(this, packet));
    }

    private boolean exceptionMet = false;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof TimeoutException){
            closeChannel("Connection timed out");
        } else {
            Platform.getLogger().warn("exception thrown in connection", cause);
            if (!exceptionMet) {
                exceptionMet = true;
                sendPacket(new PacketDisconnect(cause.getMessage()), future -> closeChannel(cause.getMessage()));
            } else {
                Platform.getLogger().warn("this exception is an double failure");
                closeChannel(cause.getMessage());
            }
        }
    }

    public boolean isChannelOpen() {
        return channel != null && channel.isOpen();
    }

    // This method will not send packet immediately
    public void pendPacket(Packet packet) {
        pendPacket(packet, null);
    }

    public void pendPacket(Packet packet, @Nullable GenericFutureListener<Future<? super Void>> future) {
        if (channel != null) {

            var channelFuture = channel.write(packet);
            if (future != null) {
                channelFuture.addListener(future);
            }
            channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    public void sendPendingPackets() {
        if (channel != null) {
            channel.flush();
        }
    }

    public void sendPacket(Packet packet) {
        pendPacket(packet);
        sendPendingPackets();
    }

    public void sendPacket(Packet packet, @Nullable GenericFutureListener<Future<? super Void>> future) {
        pendPacket(packet, future);
        sendPendingPackets();
    }

}
