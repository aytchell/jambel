package com.jambit.hlerchl.jambel.entity;

import com.jambit.hlerchl.jambel.Jambel;
import com.jambit.hlerchl.jambel.JambelModule;
import com.jambit.hlerchl.jambel.exceptions.JambelCompileException;
import com.jambit.hlerchl.jambel.exceptions.JambelException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JambelCommandCompilerTest {

    private final Jambel mockedJambel = Mockito.mock(Jambel.class);
    private final JambelModule mockedRed = Mockito.mock(JambelModule.class);
    private final JambelModule mockedYellow = Mockito.mock(JambelModule.class);
    private final JambelModule mockedGreen = Mockito.mock(JambelModule.class);

    @Test
    void nullAsCommandThrows() {
        assertThrows(JambelCompileException.class,
            () -> JambelCommandCompiler.compile(mockedJambel, null));
    }

    @Test
    void emptyCommandThrows() {
        assertThrows(JambelCompileException.class,
            () -> JambelCommandCompiler.compile(mockedJambel, "   "));
    }

    @Test
    void test() throws JambelException {
        JambelCommandCompiler.compile(mockedJambel, " test_connection ").execute();
        Mockito.verify(mockedJambel).testConnection();
        Mockito.verifyNoMoreInteractions(mockedJambel);
    }

    @Test
    void reset() throws JambelException {
        JambelCommandCompiler.compile(mockedJambel, " reset ").execute();
        Mockito.verify(mockedJambel).reset();
        Mockito.verifyNoMoreInteractions(mockedJambel);
    }

    @Test
    void setDefaultBlinkTimesWrongParamCountThrows() {
        assertThrows(JambelCompileException.class,
            () -> JambelCommandCompiler.compile(mockedJambel, "set_blink_times    "));
        assertThrows(JambelCompileException.class,
            () -> JambelCommandCompiler.compile(mockedJambel, "set_blink_times 100"));
        assertThrows(JambelCompileException.class,
            () -> JambelCommandCompiler.compile(mockedJambel, "set_blink_times 100 120 140"));
    }

    @Test
    void setDefaultBlinkTimesWrongParamTypeThrows() {
        assertThrows(JambelCompileException.class,
            () -> JambelCommandCompiler.compile(mockedJambel, "set_blink_times 100 3a4"));
    }

    @Test
    void setDefaultBlinkTimes() throws JambelException {
        JambelCommandCompiler.compile(mockedJambel, "set_blink_times 100 120").execute();

        Mockito.verify(mockedJambel).setDefaultBlinkTimes(100, 120);
        Mockito.verifyNoMoreInteractions(mockedJambel);
    }

    @Test
    void turnGreenOn() throws JambelException {
        Mockito.doReturn(mockedGreen).when(mockedJambel).green();

        JambelCommandCompiler.compile(mockedJambel, "green on").execute();

        Mockito.verify(mockedJambel).green();
        Mockito.verify(mockedGreen).on();
        Mockito.verifyNoMoreInteractions(mockedJambel);
        Mockito.verifyNoMoreInteractions(mockedGreen);
    }

    @Test
    void turnYellowOn() throws JambelException {
        Mockito.doReturn(mockedYellow).when(mockedJambel).yellow();

        JambelCommandCompiler.compile(mockedJambel, "yellow on").execute();

        Mockito.verify(mockedJambel).yellow();
        Mockito.verify(mockedYellow).on();
        Mockito.verifyNoMoreInteractions(mockedJambel);
        Mockito.verifyNoMoreInteractions(mockedYellow);
    }

    @Test
    void turnRedOn() throws JambelException {
        Mockito.doReturn(mockedRed).when(mockedJambel).red();

        JambelCommandCompiler.compile(mockedJambel, "red on").execute();

        Mockito.verify(mockedJambel).red();
        Mockito.verify(mockedRed).on();
        Mockito.verifyNoMoreInteractions(mockedJambel);
        Mockito.verifyNoMoreInteractions(mockedRed);
    }

    @Test
    void turnGreenSomeMSecOn() throws JambelException {
        Mockito.doReturn(mockedGreen).when(mockedJambel).green();

        JambelCommandCompiler.compile(mockedJambel, "green on_for 60").execute();

        Mockito.verify(mockedJambel).green();
        Mockito.verify(mockedGreen).on(60);
        Mockito.verifyNoMoreInteractions(mockedJambel);
        Mockito.verifyNoMoreInteractions(mockedGreen);
    }

    @Test
    void turnYellowSomeMSecOn() throws JambelException {
        Mockito.doReturn(mockedYellow).when(mockedJambel).yellow();

        JambelCommandCompiler.compile(mockedJambel, "yellow on_for 120").execute();

        Mockito.verify(mockedJambel).yellow();
        Mockito.verify(mockedYellow).on(120);
        Mockito.verifyNoMoreInteractions(mockedJambel);
        Mockito.verifyNoMoreInteractions(mockedYellow);
    }

    @Test
    void turnRedSomeMSecOn() throws JambelException {
        Mockito.doReturn(mockedRed).when(mockedJambel).red();

        JambelCommandCompiler.compile(mockedJambel, "red on_for 42").execute();

        Mockito.verify(mockedJambel).red();
        Mockito.verify(mockedRed).on(42);
        Mockito.verifyNoMoreInteractions(mockedJambel);
        Mockito.verifyNoMoreInteractions(mockedRed);
    }

    @Test
    void turnRedOff() throws JambelException {
        Mockito.doReturn(mockedRed).when(mockedJambel).red();

        JambelCommandCompiler.compile(mockedJambel, "red off").execute();

        Mockito.verify(mockedJambel).red();
        Mockito.verify(mockedRed).off();
        Mockito.verifyNoMoreInteractions(mockedJambel);
        Mockito.verifyNoMoreInteractions(mockedRed);
    }

    @Test
    void letRedBlink() throws JambelException {
        Mockito.doReturn(mockedRed).when(mockedJambel).red();

        JambelCommandCompiler.compile(mockedJambel, "red blink").execute();

        Mockito.verify(mockedJambel).red();
        Mockito.verify(mockedRed).blink();
        Mockito.verifyNoMoreInteractions(mockedJambel);
        Mockito.verifyNoMoreInteractions(mockedRed);
    }

    @Test
    void letRedFlash() throws JambelException {
        Mockito.doReturn(mockedGreen).when(mockedJambel).green();

        JambelCommandCompiler.compile(mockedJambel, "green flash").execute();

        Mockito.verify(mockedJambel).green();
        Mockito.verify(mockedGreen).flash();
        Mockito.verifyNoMoreInteractions(mockedJambel);
        Mockito.verifyNoMoreInteractions(mockedGreen);
    }

    @Test
    void letRedBlinkInverse() throws JambelException {
        Mockito.doReturn(mockedYellow).when(mockedJambel).yellow();

        JambelCommandCompiler.compile(mockedJambel, "yellow blink_inverse").execute();

        Mockito.verify(mockedJambel).yellow();
        Mockito.verify(mockedYellow).blinkInverse();
        Mockito.verifyNoMoreInteractions(mockedJambel);
        Mockito.verifyNoMoreInteractions(mockedYellow);
    }

    @Test
    void setBlinkTimersForSingleModule() throws JambelException {
        Mockito.doReturn(mockedYellow).when(mockedJambel).yellow();

        JambelCommandCompiler.compile(mockedJambel, "yellow set_on_off_times 600 300").execute();

        Mockito.verify(mockedJambel).yellow();
        Mockito.verify(mockedYellow).setBlinkTimes(600, 300);
        Mockito.verifyNoMoreInteractions(mockedJambel);
        Mockito.verifyNoMoreInteractions(mockedYellow);
    }

    @Test
    void setAllToBlinkInverseOffOn() throws JambelException {
        JambelCommandCompiler.compile(mockedJambel,
            "set_ryg blink_inverse off on").execute();

        Mockito.verify(mockedJambel).setAllLights(
            Jambel.LightStatus.BLINK_INVERSE, Jambel.LightStatus.OFF, Jambel.LightStatus.ON);
        Mockito.verifyNoMoreInteractions(mockedJambel);
    }

    @Test
    void setAllToFlashBlinkInverseOff() throws JambelException {
        JambelCommandCompiler.compile(mockedJambel,
            "set_ryg flash blink_inverse off").execute();

        Mockito.verify(mockedJambel).setAllLights(
            Jambel.LightStatus.FLASH, Jambel.LightStatus.BLINK_INVERSE, Jambel.LightStatus.OFF);
        Mockito.verifyNoMoreInteractions(mockedJambel);
    }

    @Test
    void setAllToOnBlinkFlash() throws JambelException {
        JambelCommandCompiler.compile(mockedJambel, "set_ryg on blink flash").execute();

        Mockito.verify(mockedJambel).setAllLights(
            Jambel.LightStatus.ON, Jambel.LightStatus.BLINK, Jambel.LightStatus.FLASH);
        Mockito.verifyNoMoreInteractions(mockedJambel);
    }
}
