package com.jambit.hlerchl.jambel;

import com.jambit.hlerchl.jambel.entity.Jambel;
import com.jambit.hlerchl.jambel.entity.JambelGreenOnTop;
import com.jambit.hlerchl.jambel.exceptions.JambelException;
import com.jambit.hlerchl.jambel.link.JambelTelnetLink;

public class TestJambel10 {
    // 5 seconds should be enough to connect telnet via intranet
    private static final int CONNECT_TIMEOUT_MSEC = 5000;

    public static void main(String[] args) throws InterruptedException {
        try {
            final JambelTelnetLink link = new JambelTelnetLink("ampel10.dev.jambit.com", 10001);
            link.setConnectTimeout(CONNECT_TIMEOUT_MSEC);
            final Jambel jambel = new JambelGreenOnTop(link);

            jambel.reset();
            jambel.red().on();
            jambel.yellow().blink();
            Thread.sleep(4000);

            jambel.reset();
            jambel.green().on();
            Thread.sleep(4000);

            jambel.green().setBlinkTimes(400, 200);
            jambel.green().blink();
            jambel.yellow().blinkInvers();
            Thread.sleep(4000);

            jambel.reset();
        } catch (JambelException e) {
            e.printStackTrace();
        }
    }
}
