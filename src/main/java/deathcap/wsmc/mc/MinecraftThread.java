package deathcap.wsmc.mc;

import deathcap.wsmc.mc.ping.PingResponse;
import deathcap.wsmc.mc.ping.PingStatus;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MinecraftThread extends Thread {

    public final String host;
    public String taggedHost;
    public final int port;
    public final String username;
    public final ChannelHandlerContext websocket;
    public ClientHandler clientHandler;
    public boolean loggingIn = true;
    public String pingResponseText;

    public MinecraftThread(String host, int port, String pingResponseText, String username, ChannelHandlerContext websocket) {
        this.host = host;
        this.port = port;
        this.pingResponseText = pingResponseText;

        this.taggedHost = host;
        boolean forge = false;
        PingResponse pingResponse = PingStatus.parse(pingResponseText);
        if (pingResponse != null && pingResponse.modinfo != null) {
            String type = pingResponse.modinfo.type;
            if ("FML".equals(type)) {
                forge = true;
                System.out.println("Forge support enabled");
            }
        }
        if (forge) this.taggedHost += "\0FML\0";

        this.username = username;
        this.websocket = websocket;
    }

    @Override
    public void run() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        System.out.println("Connecting to "+host+":"+port+" as "+username);

        try {
            this.clientHandler = new ClientHandler(this);
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(this.clientHandler);

            ChannelFuture f = b.connect(host, port).sync();

            f.channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
