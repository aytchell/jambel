package com.jambit.hlerchl.jambel;

import com.jambit.hlerchl.jambel.entity.JambelCommandCompiler;
import com.jambit.hlerchl.jambel.entity.JambelGreenOnTop;
import com.jambit.hlerchl.jambel.entity.JambelRedOnTop;
import com.jambit.hlerchl.jambel.exceptions.JambelCompileException;

/**
 * A factory class with static methods to create {@link Jambel} instances.
 */
public class JambelFactory {
    /**
     * Default TCP port number of a jambel
     */
    public static final int DEFAULT_PORT = 10001;

    /**
     * Create a new {@link Jambel} instance with default network timeouts.
     * <p>
     * For a detailed description see {@link #build(String, int, boolean, int)}.
     *
     * @see JambelFactory#build(String, int, boolean, int)
     * @param hostAddress network host address or name of the jambel
     * @param port port number of the jambel (see {@link JambelFactory#DEFAULT_PORT}).
     * @param redOnTop does this jambel have the red light on the top?
     * @return a {@link Jambel} instance which can be used to control the jambel
     */
    public static Jambel build(String hostAddress, int port, boolean redOnTop) {
        if (redOnTop) {
            return JambelRedOnTop.build(hostAddress, port);
        } else {
            return JambelGreenOnTop.build(hostAddress, port);
        }
    }

    /**
     * Create a new {@link Jambel} instance.
     * <p>
     * Static factory method for creating a {@link Jambel} instance. Note that
     * there are two kinds of jambels:
     * <ul>
     *     <li>those with a red light on the top and yellow and green below and</li>
     *     <li>those with a green light on the top and yellow and red below</li>
     * </ul>
     * To configure the {@link Jambel} instance correctly you have to give this
     * information here. Later, when using the instance both versions behave equal.
     * Note that if this parameter is wrong, the commands for the red light will
     * control the green light and vice versa.
     * <p>
     * Since a jambel is controlled via telnet (over TCP) you also have to give
     * the host name or address. DNS names as well as IPv4 addresses are accepted.
     * Using an IPv6 address as parameter has never been tested (it might also be
     * possible that the jambel doesn't support IPv6 at all).
     * <p>
     * And finally it is possible to configure the TCP timeout for establishing
     * a new connection (in milliseconds). The implemented mode of operation is
     * that for each single command the lib is opening a telnet connection,
     * sends the correct command (depending on the called method) and closes
     * the connection again. This might look a bit inefficient, but it prevents
     * us from handling broken connections and limitations on the jambel side
     * (it seems like there's an embedded IP-stack running which allows only
     * one or two parallel telnet connections ... and in general is a bit
     * brittle).
     * <p>
     * Note also that the IP stack on the jambel is a bit slow so when sending
     * a burst of commands, some of them might fail. To handle this, the lib
     * automatically re-tries a failed command up to three times. This behaviour
     * will increase the "observed timeout" in case of enduring connectivity
     * problems.
     *
     * @see JambelFactory#build(String, int, boolean)
     * @param hostAddress network host address or name of the jambel
     * @param port port number of the jambel (see {@link JambelFactory#DEFAULT_PORT}).
     * @param redOnTop does this jambel have the red light on the top?
     * @param timeoutMilliSeconds timeout in milliseconds when establishing a connection
     *                            to the jambel
     * @return a {@link Jambel} instance which can be used to control the jambel
     */
    public static Jambel build(String hostAddress, int port, boolean redOnTop,
                               int timeoutMilliSeconds) {
        if (redOnTop) {
            return JambelRedOnTop.build(hostAddress, port, timeoutMilliSeconds);
        } else {
            return JambelGreenOnTop.build(hostAddress, port, timeoutMilliSeconds);
        }
    }

    /**
     * Compile a jambel command string to a closure.
     * <p>
     * This jambel library contains a kind of "command line" for jambels.
     * This is implemented via the command pattern.
     * <p>
     * There is a well-defined command set understood by this static method.
     * Given such a command, the method will create a closure object
     * which can then (at a later point in time) be executed without knowing
     * anything about {@link Jambel}'s API or capabilities or the content
     * of the given string.
     * <p>
     * The commands understood by this method are:
     * <ul>
     *      <li>"reset" : calls {@link Jambel#reset()}</li>
     *      <li>"test_connection" : calls {@link Jambel#testConnection()}</li>
     *      <li>"set_blink_times &lt;msec_on&gt; &lt;msec_off&gt;" : expects
     *          two integer values as parameters denoting the number of
     *          milliseconds to stay on and off. Calls
     *          {@link Jambel#setDefaultBlinkTimes(int, int)}</li>
     *      <li>"red ..." or "yellow ..." or "green ..." : control a specific
     *          light of the jambel. Expects at least one more parameter:
     *          <ul>
     *              <li>"... on" : Turn the light on. See {@link JambelModule#on()}</li>
     *              <li>"... off" : Turn the light off. See {@link JambelModule#off()}</li>
     *              <li>"... blink" : Lets the light blink. See {@link JambelModule#blink()}</li>
     *              <li>"... flash" : Lets the light flash. See {@link JambelModule#flash()}</li>
     *              <li>"... blink_inverse" : Lets the light blink inverse.
     *                  See {@link JambelModule#blinkInverse()} ()}</li>
     *              <li>"... set_on_off_times &lt;msec_on&gt; &lt;msec_off&gt;" : Sets
     *                  the blink timings for this specific light. Expects
     *                  two integer values as parameters denoting the number of
     *                  milliseconds to stay on and off.
     *                  See {@link JambelModule#setBlinkTimes}</li>
     *          </ul>
     * </ul>
     *
     * @param jambel the Jambel which the command should control
     * @param command a correct jambel command string (see detailed
     *                method description)
     * @return a closure which can be use like a lambda without knowing
     *          anything about jambel's API or internals
     * @throws JambelCompileException thrown in case of a malformed command
     *          string
     */
    public static JambelCommand compileCommand(Jambel jambel, String command)
        throws JambelCompileException {
        return JambelCommandCompiler.compile(jambel, command);
    }
}
