package com.jambit.hlerchl.jambel;

import com.jambit.hlerchl.jambel.exceptions.JambelException;
import lombok.Value;

/**
 * The definitive interface for all things a "jambel" can do.
 * <p>
 * A "jambel" is a piece of hardware created by employees of jambit GmbH
 * with a functionality similar to (and beyond) a small traffic light
 * ("Ampel" in german).
 * <p>
 * The "jambel" is attached via ethernet and can be controlled by using
 * the telnet protocol. There are three lights (red, yellow and green) which
 * can be separately controlled to be on, off, blinking or flashing.
 */
public interface Jambel {
    /**
     * Reset all three lights.
     * <p>
     * This command will cause the jambel to turn all three lights off and
     * set the blink frequencies (used for the next call to
     * {@link JambelModule#blink()}) back to factory settings.
     *
     * @throws JambelException thrown in case the jambel couldn't be reached
     *      or behaved unexpectedly
     */
    void reset() throws JambelException;

    /**
     * Fetch the version string from the jambel.
     * <p>
     * The version string is set by the manufacturer of the built-in
     * board which converts telnet commands to RS-232 commands (which
     * then control the lights).
     * <p>
     * This version string is neither used nor understood by this library.
     *
     * @return the version string returned by the jambel
     * @throws JambelException thrown in case the jambel couldn't be reached
     *      or behaved unexpectedly
     */
    String version() throws JambelException;

    /**
     * Set defaults for the blink times (duration of "on" and "off" phases).
     * <p>
     * It's possible to control the blink behaviour of a jambel.
     * By using this method a caller can define the default values for
     * how long a light should be turned on and how long it should be
     * turned off.
     * <p>
     * This setting will be used for all three lights except if there's a
     * "light specific override" (see {@link JambelModule#setBlinkTimes}).
     *
     * @param msecOn milliseconds to keep the light on
     * @param msecOff milliseconds to keep the light off
     * @throws JambelException thrown in case the jambel couldn't be reached
     *      or behaved unexpectedly
     */
    void setDefaultBlinkTimes(int msecOn, int msecOff) throws JambelException;

    /**
     * Fetch the current status of the three lights.
     * <p>
     * Used to query the status of the three lights. It's not possible to
     * fetch the blink times; the jambel informs only about the three active
     * modes.
     *
     * @return a pojo with three enums describing the current state of the lights
     * @throws JambelException thrown in case the jambel couldn't be reached
     *      or behaved unexpectedly
     */
    Status status() throws JambelException;


    /**
     * Test connectivity to the jambel.
     * <p>
     * This command is a no-op. If things work as expected, then nothing
     * happens. If the jambel can't be reached or is somehow broken the
     * call will throw an exception.
     *
     * @throws JambelException thrown in case the jambel couldn't be reached
     *      or behaved unexpectedly
     */
    // throws on connection error; no other effect
    void testConnection() throws JambelException;

    /**
     * Access the control panel of the green light.
     * <p>
     * Switching a specific light is done by another instance. This method
     * returns the appropriate class instance so a caller can control the
     * green light.
     *
     * @return a class instance to control the green light
     */
    JambelModule green();

    /**
     * Access the control panel of the yellow light.
     * <p>
     * Switching a specific light is done by another instance. This method
     * returns the appropriate class instance so a caller can control the
     * yellow light.
     *
     * @return a class instance to control the yellow light
     */
    JambelModule yellow();

    /**
     * Access the control panel of the red light.
     * <p>
     * Switching a specific light is done by another instance. This method
     * returns the appropriate class instance so a caller can control the
     * red light.
     *
     * @return a class instance to control the red light
     */
    JambelModule red();

    /**
     * A pojo which contains the current status of the three lights
     */
    @Value
    class Status {
        LightStatus red;
        LightStatus yellow;
        LightStatus green;
    }

    /**
     * Enum describing the modes a jambel light can be in
     */
    enum LightStatus {
        /**
         * The light is turned off.
         */
        OFF,

        /**
         * The light is turned on and shines continuously.
         */
        ON,

        /**
         * The light blinks. The durations of the "on" and "off" phases
         * can differ. These can be controlled via {@link Jambel#setDefaultBlinkTimes}.
         */
        BLINK,

        /**
         * The light flashes "nervously" in bursts.
         */
        FLASH,

        /**
         * Same as {@code BLINK} but "on" and "off" phases are inverted.
         * By using this mode it's easy to have two lights blinking
         * alternately.
         */
        BLINK_INVERSE
    }
}
