package com.jambit.hlerchl.jambel;

import com.jambit.hlerchl.jambel.exceptions.JambelException;

/**
 * Interface for controlling a specific light on a jambel
 * <p>
 * @see Jambel
 */
public interface JambelModule {
    /**
     * Turns the light on.
     * <p>
     * @see Jambel#status()
     * @see Jambel.LightStatus#ON
     * @throws JambelException thrown in case the jambel couldn't be reached
     *      or behaved unexpectedly
     */
    void on() throws JambelException;

    /**
     * Turns the light off.
     * <p>
     * @see Jambel#status()
     * @see Jambel.LightStatus#OFF
     * @throws JambelException thrown in case the jambel couldn't be reached
     *      or behaved unexpectedly
     */
    void off() throws JambelException;

    /**
     * Switches the light into blink mode.
     * <p>
     * The light will blink. The times (in milliseconds) how long the
     * light will be on and off can be set either by calling
     * {@link Jambel#setDefaultBlinkTimes} (default values for all
     * three lights) or by calling {@link JambelModule#setBlinkTimes}
     * (times specific for this light).
     *
     * @see Jambel#status()
     * @see Jambel.LightStatus#BLINK
     * @throws JambelException thrown in case the jambel couldn't be reached
     *      or behaved unexpectedly
     */
    void blink() throws JambelException;

    /**
     * Switches the light into "inverse blink mode".
     * <p>
     * The "inverse blink mode" is very similar to the "blink mode". The only
     * difference is, that the configured durations for the 'on' and 'off'
     * phases are replaced.
     * <p>
     * By using this mode it's easy to have two lights blinking
     * alternately.
     *
     * @see Jambel#status()
     * @see Jambel.LightStatus#BLINK_INVERSE
     * @throws JambelException thrown in case the jambel couldn't be reached
     *      or behaved unexpectedly
     */
    void blinkInverse() throws JambelException;

    /**
     * Switches the light into flash mode.
     * <p>
     * The light will flash "nervously" in bursts.
     *
     * @see Jambel#status()
     * @see Jambel.LightStatus#FLASH
     * @throws JambelException thrown in case the jambel couldn't be reached
     *      or behaved unexpectedly
     */
    void flash() throws JambelException;

    /**
     * Set blink timings specific to this light.
     * <p>
     * The on and off times are used, when the light is blinking.
     * The values set via this method override those values set via
     * {@link Jambel#setDefaultBlinkTimes}.
     *
     * @param msecOn time in milliseconds how long a blinking light will
     *               be turned <b>on</b>
     * @param msecOff time in milliseconds how long a blinking light will
     *                be turned <b>off</b>
     * @throws JambelException thrown in case the jambel couldn't be reached
     *      or behaved unexpectedly
     */
    void setBlinkTimes(int msecOn, int msecOff) throws JambelException;
}
