package com.jambit.hlerchl.jambel.entity;

import com.jambit.hlerchl.jambel.Jambel;
import com.jambit.hlerchl.jambel.exceptions.JambelConnectException;
import com.jambit.hlerchl.jambel.exceptions.JambelException;
import com.jambit.hlerchl.jambel.exceptions.JambelIoException;
import com.jambit.hlerchl.jambel.exceptions.JambelResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RawJambelTest {

    private JambelCommLink mockedLink;
    private Jambel fixture;

    @BeforeEach
    void setupCommLinkMock() throws JambelException {
        mockedLink = Mockito.mock(JambelCommLink.class);
        Mockito.doThrow(new JambelIoException("Wrong command sent"))
            .when(mockedLink).sendCommand(Mockito.anyString());
         fixture = new RawJambel(mockedLink, 1, 2, 3);
    }

    @Test
    void reset() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("reset");

        fixture.reset();
    }

    @Test
    void setDefaultBlinkTimes() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("blink_time_on=600");
        Mockito.doReturn("OK").when(mockedLink).sendCommand("blink_time_off=300");

        fixture.setDefaultBlinkTimes(600, 300);
    }

    @Test
    void getVersionOfJambel() throws JambelException {
        Mockito.doReturn("Version 1.0 of da funky light")
            .when(mockedLink).sendCommand("version");

        final String version = fixture.version();
        assertEquals(version, "Version 1.0 of da funky light");
    }

    @Test
    void turnGreenOn() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=3,on");

        fixture.green().on();
    }

    @Test
    void turnYellowOn() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=2,on");

        fixture.yellow().on();
    }

    @Test
    void turnRedOn() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=1,on");

        fixture.red().on();
    }

    @Test
    void turnGreen30MSecOn() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=3,30");

        fixture.green().on(30);
    }

    @Test
    void turnYellow60MSecOn() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=2,60");

        fixture.yellow().on(60);
    }

    @Test
    void turnRed90MSecOn() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=1,90");

        fixture.red().on(90);
    }

    @Test
    void turnRedOff() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=1,off");

        fixture.red().off();
    }

    @Test
    void letRedBlink() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=1,blink");

        fixture.red().blink();
    }

    @Test
    void letRedFlash() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=1,flash");

        fixture.red().flash();
    }

    @Test
    void letRedBlinkInverse() throws JambelException {
        // [sic!] the telnet command really spells "invers"
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=1,blink_invers");

        fixture.red().blinkInverse();
    }

    @Test
    void setBlinkTimersForSingleModule() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("blink_time=1,800,400");

        fixture.red().setBlinkTimes(800, 400);
    }

    @Test
    void inverseModuleOrderWorks() throws JambelException {
        final Jambel inverseFixture = new RawJambel(mockedLink, 3, 2, 1);
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=3,on");

        inverseFixture.red().on();
    }

    @Test
    void throwsOnUnusualAnswer() throws JambelException {
        Mockito.doReturn("OKiDoki").when(mockedLink).sendCommand("set=1,blink");
        Mockito.doReturn("").when(mockedLink).sendCommand("version");

        assertThrows(JambelResponseException.class, () -> fixture.red().blink());
        assertThrows(JambelResponseException.class, () -> fixture.version());
    }

    @Test
    void throwsOnNoAnswer() throws JambelException {
        Mockito.doReturn(null).when(mockedLink).sendCommand("set=1,on");
        Mockito.doReturn(null).when(mockedLink).sendCommand("version");

        assertThrows(JambelResponseException.class, () -> fixture.red().on());
        assertThrows(JambelResponseException.class, () -> fixture.version());
    }

    @Test
    void connectExceptionIsForwarded() throws JambelException {
        Mockito.doThrow(new JambelConnectException("no connection"))
            .when(mockedLink).sendCommand("set=1,blink");

        assertThrows(JambelConnectException.class, () -> fixture.red().blink());
    }

    @Test
    void ioExceptionIsForwarded() throws JambelException {
        Mockito.doThrow(new JambelIoException("broken link"))
            .when(mockedLink).sendCommand("set=1,blink");

        assertThrows(JambelIoException.class, () -> fixture.red().blink());
    }

    @Test
    void statusBrokenResponsePrefix() throws JambelException {
        Mockito.doReturn("stats=1,0,2,0,0").when(mockedLink).sendCommand("status");

        assertThrows(JambelResponseException.class, () -> fixture.status());
    }

    @Test
    void statusUnknownValueInResponse() throws JambelException {
        Mockito.doReturn("status=1,8,2,0,0").when(mockedLink).sendCommand("status");

        assertThrows(JambelResponseException.class, () -> fixture.status());
    }

    @Test
    void statusUnknownStructureOfResponse() throws JambelException {
        Mockito.doReturn("status=1, 1, 2, 0, 0").when(mockedLink).sendCommand("status");

        assertThrows(JambelResponseException.class, () -> fixture.status());
    }
}
