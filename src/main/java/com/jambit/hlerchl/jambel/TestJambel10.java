package com.jambit.hlerchl.jambel;

import com.jambit.hlerchl.jambel.entity.JambelGreenOnTop;
import com.jambit.hlerchl.jambel.exceptions.JambelException;
import com.jambit.hlerchl.jambel.link.JambelTelnetLink;

public class TestJambel10 {
    // 2 seconds should be enough to connect telnet via intranet
    private static final int CONNECT_TIMEOUT_MSEC = 2000;

    public static void main(String[] args) throws InterruptedException {
        try {
            final String hostName = "ampel10.dev.jambit.com";
            final int port = JambelFactory.DEFAULT_PORT;

            final JambelTelnetLink link = new JambelTelnetLink(hostName, port);
            link.setConnectTimeout(CONNECT_TIMEOUT_MSEC);
            final Jambel jambel = new JambelGreenOnTop(link);

            Jambel.Status status;

            jambel.testConnection();
            final String jambelVersion = jambel.version();
            System.out.println("jambel version: " + jambelVersion);

            jambel.reset();
            jambel.red().flash();
            status = jambel.status();
            System.out.println("Status (red flash): " + status.toString());
            Thread.sleep(4000);

            jambel.red().off();
            jambel.setDefaultBlinkTimes(100, 400);
            jambel.yellow().blink();
            jambel.green().blinkInverse();
            status = jambel.status();
            System.out.println("Status (green blink inverse): " + status.toString());
            Thread.sleep(4000);

            jambel.green().off();
            jambel.green().setBlinkTimes(400, 200);
            jambel.yellow().blink();
            status = jambel.status();
            System.out.println("Status (yellow blink): " + status.toString());
            Thread.sleep(4000);

            jambel.reset();
            jambel.testConnection();
        } catch (JambelException e) {
            e.printStackTrace();
        }
    }
}
