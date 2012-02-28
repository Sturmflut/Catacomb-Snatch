package com.mojang.mojam;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import com.mojang.mojam.gui.Font;
import com.mojang.mojam.screen.Screen;

/**
 * Chat functionality for multiplayer mode
 */
public class Chat implements KeyListener {
	/** Constants */
	private static final int MAX_MESSAGES = 10;
	private static final int MAX_MESSAGE_LENGTH = 35;
	private static final int TICKS_PER_MESSAGE = 60 * 4;

	private ArrayList<String> messages = new ArrayList<String>();
	private int displayedMessage = -1;
	private int displayTicks = 0;
	private boolean open = false;
	private String currentMessage = "";
	private String waitingMessage = null;

	/**
	 * Clear all messages
	 */
	public void clear() {
		messages.clear();
	}

	/**
	 * Check if the chat is open
	 * 
	 * @return True if open, false if not
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * Open the chat
	 */
	public void open() {
		open = true;
	}

	/**
	 * Add a message
	 * 
	 * @param message Message
	 */
	public void addMessage(String message) {
		if (messages.size() == MAX_MESSAGES) {
			messages.remove(MAX_MESSAGES - 1);
		}
		messages.add(0, message);
		if (displayedMessage + 1 < MAX_MESSAGES) {
			displayedMessage += 1;
		}
	}

	/**
	 * Retrieve a waiting message
	 * 
	 * @return Message on success, null if no message available
	 */
	public String getWaitingMessage() {
		String msg = waitingMessage;
		waitingMessage = null;
		return msg;
	}

	/**
	 * Handle animations
	 */
	public void tick() {
		if (displayedMessage > -1) {
			displayTicks++;
			if (displayTicks == TICKS_PER_MESSAGE) {
				displayTicks = 0;
				displayedMessage -= 1;
			}
		}
	}

	/**
	 * Render the messages onto the given screen
	 * 
	 * @param screen Screen
	 */
	public void render(Screen screen) {
		int xOffset = 5;
		int yOffset = 312;
		if (open) {
			Font.defaultFont().draw(screen, currentMessage + "-", xOffset, yOffset);
			for (int i = 0; i < messages.size(); i++) {
				Font.defaultFont().draw(screen, messages.get(i), xOffset, (yOffset -= 8));
			}
		} else {
			for (int i = 0; i <= displayedMessage; i++) {
				Font.defaultFont().draw(screen, messages.get(i), xOffset, (yOffset -= 8));
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (open) {
			if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE && currentMessage.length() > 0) {
				currentMessage = currentMessage.substring(0, currentMessage.length() - 1);
			} else {
				if (currentMessage.length() < MAX_MESSAGE_LENGTH) {
					currentMessage += e.getKeyChar();
				}
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {
		if (open) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				open = false;
				currentMessage = "";
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				open = false;
				if (!currentMessage.equals("")) {
					waitingMessage = currentMessage;
				}
				currentMessage = "";
			}
		}
	}

}
