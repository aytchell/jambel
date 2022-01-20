package com.jambit.hlerchl.jambel;

import com.jambit.hlerchl.jambel.entity.JambelGreenOnTop;
import com.jambit.hlerchl.jambel.exceptions.JambelException;
import com.jambit.hlerchl.jambel.link.JambelTelnetLink;

public class TestJambel10 {
    // 5 seconds should be enough to connect telnet via intranet
    private static final int CONNECT_TIMEOUT_MSEC = 2000;

    public static void main(String[] args) throws InterruptedException {
        try {
            String jambelHostName = "ampel10.dev.jambit.com";
            final JambelTelnetLink link = new JambelTelnetLink(jambelHostName, 10001);
            link.setConnectTimeout(CONNECT_TIMEOUT_MSEC);
            final Jambel jambel = new JambelGreenOnTop(link);

            Jambel.Status status;

            jambel.testConnection();

            jambel.reset();
            jambel.red().flash();
            status = jambel.status();
            System.out.println("Status (red flash): " + status.toString());
            Thread.sleep(4000);

            jambel.red().off();
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
