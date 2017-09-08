package com.github.fabioticconi.roguelite;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.managers.PlayerManager;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.BitVector;
import com.github.fabioticconi.roguelite.behaviours.*;
import com.github.fabioticconi.roguelite.constants.Options;
import com.github.fabioticconi.roguelite.map.EntityGrid;
import com.github.fabioticconi.roguelite.map.Map;
import com.github.fabioticconi.roguelite.systems.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Random;

public class Roguelite extends JFrame implements KeyListener
{
    static final  Logger  log          = LoggerFactory.getLogger(Roguelite.class);
    public static boolean keepRunning  = true;
    private final int     fps          = 25;
    private final long    deltaNanos   = Math.round(1000000000.0d / (double) fps);
    private final float   deltaSeconds = 1.0f / (float) fps;
    private final AsciiPanel        terminal;
    private final World             world;
    private final PlayerInputSystem input;
    private final RenderSystem      render;
    // currently pressed keys
    private final BitVector         pressed;

    public Roguelite() throws IOException
    {
        super();
        terminal = new AsciiPanel(Options.OUTPUT_SIZE_X, Options.OUTPUT_SIZE_Y, AsciiFont.CP437_16x16);
        add(terminal);
        pack();

        pressed = new BitVector(255);

        // Input and render are sort of "binders" between the GUI and the logic.
        // They are both passive: the input system receives raw player commands (when in "play screen")
        // and converts it to artemis "things", then starts a player action. Should be pretty immediate.
        // The render system is called whenever the play screen is active and the map needs to be painted.
        // It needs to be a system for us to be able to leverage the components on the entities, of course.
        input = new PlayerInputSystem();
        render = new RenderSystem();

        final WorldConfiguration config;
        config = new WorldConfiguration();
        // POJOs
        config.register(new Map());
        config.register(new EntityGrid());
        config.register(new Random());
        // passive systems, one-timers, managers etc
        config.setSystem(BootstrapSystem.class); // once
        config.setSystem(PlayerManager.class);
        config.setSystem(GroupSystem.class);
        config.setSystem(WorldSerializationManager.class);
        config.setSystem(input);
        config.setSystem(render);
        // actual game logic
        config.setSystem(new HungerSystem(5f));
        config.setSystem(AISystem.class);
        config.setSystem(MovementSystem.class);
        // ai behaviours (passive)
        config.setSystem(FleeBehaviour.class);
        config.setSystem(GrazeBehaviour.class);
        config.setSystem(ChaseBehaviour.class);
        config.setSystem(FlockBehaviour.class);
        config.setSystem(WanderBehaviour.class);

        world = new World(config);

        addKeyListener(this);
    }

    public static void main(final String[] args) throws IOException
    {
        final Roguelite app = new Roguelite();
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        app.setLocationRelativeTo(null);
        app.setVisible(true);

        app.loop();

        app.dispose();
    }

    public void loop()
    {
        long previousTime = System.nanoTime();
        long currentTime;

        long lag = 0l;
        long elapsed;

        // FIXME: without this the first rendering happens before the first process
        world.process();

        // FIXME: https://github.com/TomGrill/logic-render-game-loop
        // needs to modify that, so that I can divide systems in three groups:
        // input collection/processing, logic, output sending
        // this is because the first and the last will be only processed once,
        // while the logic ones can be re-processed until the lag is gone

        while (keepRunning)
        {
            currentTime = System.nanoTime();
            elapsed = currentTime - previousTime;
            previousTime = currentTime;

            if (elapsed > 250000000L)
            {
                log.info("lagging behind: {} ms", elapsed / 1000000.0f);
                elapsed = 250000000L;
            }

            lag += elapsed;

            input.handleKeys(pressed);

            // we do the actual computation in nanoseconds, using long numbers to avoid sneaky float
            // incorrectness.
            // however, artemis-odb wants a float delta representing seconds, so that's what we give.
            // since we use fixed timestep, this is equivalent
            // FIXME: check if deltaNanos rounding affects the system with certain fps (eg, 60)
            while (lag >= deltaNanos)
            {
                world.setDelta(deltaSeconds);
                world.process();

                lag -= deltaNanos;
            }

            repaint();

            // FIXME: to remove when actual rendering and input processing is implemented
            try
            {
                Thread.sleep(40);
            } catch (final InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void repaint()
    {
        terminal.clear();
        render.display(terminal);
        super.repaint();
    }

    @Override
    public void keyPressed(final KeyEvent e)
    {
        pressed.set(e.getKeyCode());
    }

    @Override
    public void keyReleased(final KeyEvent e)
    {
        // we don't check the capacity because we know the key must have been pressed before
        pressed.unsafeClear(e.getKeyCode());
    }

    @Override
    public void keyTyped(final KeyEvent e)
    {
    }
}
