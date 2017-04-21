package deathcap.wsmc;

import deathcap.wsmc.mc.PacketFilter;
import deathcap.wsmc.web.WebThread;

public class Main {
    public static void main(String[] args)
    {
        String wsAddress = args.length > 0 ? args[0] : "";
        int wsPort = args.length > 1 ? Integer.parseInt(args[1]) : 8000;
        String mcAddress = args.length > 2 ? args[2] : "localhost";
        int mcPort = args.length > 3 ? Integer.parseInt(args[3]) : 58888;

        System.out.println("WS("+wsAddress+":"+wsPort+") <--> MC("+mcAddress+":"+mcPort+")");
        WebThread webThread = new WebThread(wsAddress, wsPort, mcAddress, mcPort, null, new PacketFilter(), true);
        webThread.start();
    }
}
