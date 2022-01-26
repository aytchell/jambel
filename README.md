# Jambel lib

This library can be used to control a "jambel".
A "jambel" is a piece of hardware created by employees of
[jambit GmbH](https://www.jambit.com)
with a functionality similar to (and beyond) a small traffic light
("Ampel" in german).

The "jambel" is attached via ethernet and can be controlled by using
the telnet protocol. There are three lights (red, yellow and green) which
can be separately controlled to be on, off, blinking or flashing.

Due to historical reasons there are two kinds of jambel devices:
 * those with a **red** light on the top (yellow, green below) and
 * those with a **green** light on the top (yellow, red below) and

## Introduction

The starting point for controlling a jambel is the `JambelFactory`:

```java
import com.jambit.hlerchl.jambel.JambelFactory;
import com.jambit.hlerchl.jambel.Jambel;

final Jambel jambel = JambelFactory.build(
    "jambel.example.com", JambelFactory.DEFAULT_PORT, true);
```

To create a `Jambel` you need the network host name of the device,
a TCP port number and a boolean telling whether red is the topmost light
(or not).

## Maven

In case you'd like to directly start, here is the maven pointer:

```xml
    <dependency>
        <groupId>com.jambit.hlerchl</groupId>
        <artifactId>jambel</artifactId>
        <version>1.3.0</version>
    <dependency>
```

The jar is not deployed to mavencentral (since this lib is quite jambit
specific). You can either `mvn install` it locally or fetch it from
[jambit's Nexus](https://confluence.jambit.com/display/POSI/Nexus+Artefakt-Server).

jars with sources and javadoc included are also available.

## Acting operations

As already told, the jambel is a small device akin to a traffic light. So you
can turn the three lights on and off:

```java
    // turning all three lights on ...
    jambel.red().on();
    jambel.yellow().on();
    jambel.green().on();

    /// ... and off again
    jambel.red().off();
    jambel.yellow().off();
    jambel.green().off();
```

As you can see, there is no "business logic". Lights can be switched on
and off independently.

And then it is possible to let the lights blink or flash.
The jambel (and the library) allows a caller to set the durations
for the 'on' and 'off' phases when blinking.
These durations can be set globally (for all three lights) as well as
individually for each light. If both settings are set, the individual
timings are chosen.

```java
    // set the default durations how long each blink phase should take
    int msecOn = 100;
    int msecOff = 300;
    jambel.setDefaultBlinkTimes(msecOn, msecOff);

    int msecYellowOn = 200;
    int msecYellowOff = 400;
    jambel.yellow().setBlinkTimes(msecYellowOn, msecYellowOff);

    // let the red light blink with (100, 300)
    jambel.red().blink();

    // let the yellow light blink with (200, 400)
    jambel.yellow().blink();

    // flashing produces three "lightbursts" followed by a
    // short 'off' phase; this looks way more 'nervous' than
    // blinking. Flashing has fixed timings that can't be customized
    jambel.green.flash();

    // let the yellow light blink with (300, 100); basically the
    // 'on' and 'off' phases are inverted. Not only the durations
    // are swapped but the blink pattern is synchronized with
    // the red light (which uses the default durations to "blink"
    // instead of "blinkInverse") so that whenever red is turned on,
    // then yellow is turned off (and vice versa).
    jambel.green().blinkInverse();
```

## Housekeeping and diagnostic operations

There are some 'housekeeping methods': First, a caller can
test the connection to the device which acts as a "ping" on
protocol level.

```java
    // no-op if things are fine; exception if connection fails
    jambel.testConnection();
```

Then it is possible to reset the jambel. This will turn
off all three lights and reset the blink frequencies to factory default settings.

```java
    // turn off lights; reset to factory settings
    jambel.reset();
```

And it is possible to fetch the version string of the jambel.
This string is directly taken from the eth-to-rs232 board and is not
specific to the jambel functionality.

```java
    // fetch the version string from the jambel
    final String version = jambel.version();
```

For getting some feedback from the jambel (and maybe sync states from time to time)
it is possible to fetch the current status of the three lights.

```java
    final Jambel.Status status = jambel.status();

    if (status.getRed() == Jambel.LightStatus.BLINK) {
        // is blinking ok for you?
    }

    // of course getYellow() and getGreen() are also available
```

## Usage via command pattern

The library provides a possibility to read jambel commands encoded as
strings. These strings are then 'compiled' to instances of this interface

```java
public interface JambelCommand {
    void execute() throws JambelException;
}
```

Then you can hand over these commands to a "command processor" which
executes them without knowing anything about jambel commands.
Instances of this interface can be re-used, so you don't have to
compile the strings repeatedly.

For compiling jambel command strings see

```java
    public static JambelCommand JambelFactory.compileCommand(
            Jambel jambel, String command);
```

## Error handling

Accessing a network attached device can cause several problems.
In case one of the above-mentioned methods fail it will throw a
`JambelException`. Possible error sources are:
 * network failure (host not found, connection refused, ...)
 * unexpected response
 * io error (failure to actually send the command)
 * and when compiling command strings: invalid command given

The detail message of the exception will contain a
human-readable description.
