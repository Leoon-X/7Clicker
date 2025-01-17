package dev.leon.testclicker;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import dev.leon.testclicker.gui.Gui;
import dev.leon.testclicker.listener.Bind;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import dev.leon.testclicker.listener.OnClick;

public class AutoClicker {

	public static Robot robot;
	public static Point mousePos;
	public static Gui gui = new Gui();

	public static boolean toggled = false;
	public static boolean activated = false;
	public static boolean skipNext = false;
	public static boolean blockHit = false;

	private static int delay = -1;
	public static long lastTime = 0;
	public static int minCPS = 8;
	public static int maxCPS = 12;
	public static int button = 1;

	public static String[] toggleKey = { "", "" };
	public static int toggleMouseButton = 3;

	public static void main(String[] args) {
		LogManager.getLogManager().reset();
		Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);

		try {
			robot = new Robot();
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeMouseListener(new OnClick());
			GlobalScreen.addNativeKeyListener(new Bind());
		} catch (NativeHookException | AWTException e) {
			e.printStackTrace();
		}

		try {
			while (true) {
				Thread.sleep(1);
				Random random = new Random();
				if (delay == -1)
					delay = random.nextInt((1003 / minCPS) - (1004 / maxCPS) + 1) + (1004 / maxCPS);

				if (activated && toggled && !gui.focused) {
					if (System.currentTimeMillis() - lastTime >= delay) {
						click();
						lastTime = System.currentTimeMillis();
						delay = random.nextInt((1003 / minCPS) - (1004 / maxCPS) + 1) + (1004 / maxCPS);
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void click() {
		skipNext = true;
		robot.mousePress((button == 1) ? 16 : 4);
		robot.mouseRelease((button == 1) ? 16 : 4);

		if (blockHit) {
			robot.mousePress((button == 1) ? 4 : 16);
			robot.mouseRelease((button == 1) ? 4 : 16);
		}
	}

	public static void toggle() {
		if (AutoClicker.toggled) {
			AutoClicker.toggled = false;
			AutoClicker.gui.powerButton
					.setIcon(new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/on_icon.png")));
		} else {
			AutoClicker.toggled = true;
			AutoClicker.gui.powerButton.setIcon(
					new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/off_icon.png")));
		}

		AutoClicker.activated = false;
		AutoClicker.skipNext = false;
		AutoClicker.blockHit = false;
	}
}
