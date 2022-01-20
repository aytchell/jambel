package com.jambit.hlerchl.jambel.entity;

import com.jambit.hlerchl.jambel.Jambel;
import com.jambit.hlerchl.jambel.JambelCommLink;
import com.jambit.hlerchl.jambel.exceptions.JambelException;
import com.jambit.hlerchl.jambel.exceptions.JambelIoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JambelGreenOnTopTest {

    private JambelCommLink mockedLink;
    private Jambel fixture;

    @BeforeEach
    void setupCommLinkMock() throws JambelException {
        mockedLink = Mockito.mock(JambelCommLink.class);
        Mockito.doThrow(new JambelIoException("Wrong command sent"))
            .when(mockedLink).sendCommand(Mockito.anyString());
        fixture = new JambelGreenOnTop(mockedLink);
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
    void statusOnOffBlink() throws JambelException {
        Mockito.doReturn("status=1,0,2,0,0").when(mockedLink).sendCommand("status");

        Jambel.Status status = fixture.status();
        assertEquals(Jambel.LightStatus.ON, status.getRed());
        assertEquals(Jambel.LightStatus.OFF, status.getYellow());
        assertEquals(Jambel.LightStatus.BLINK, status.getGreen());
    }

    @Test
    void statusOffFlashBlinkInverse() throws JambelException {
        Mockito.doReturn("status=0,3,4,0,0").when(mockedLink).sendCommand("status");

        Jambel.Status status = fixture.status();
        assertEquals(Jambel.LightStatus.OFF, status.getRed());
        assertEquals(Jambel.LightStatus.FLASH, status.getYellow());
        assertEquals(Jambel.LightStatus.BLINK_INVERSE, status.getGreen());
    }
}
