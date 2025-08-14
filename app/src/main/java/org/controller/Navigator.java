package org.controller;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Navigator{

	/**
	 *enum che rappresenta le possibili schermate 
	 */

	public enum Screen{MENU,GAME};

	private final JFrame frame;
	private final Map<Screen,JPanel> screens = new HashMap<>();

	public Navigator(JFrame frame) {
		this.frame = frame;
		this.frame.getContentPane().setLayout(new CardLayout());
	}

	public void addScreen(Screen id, JPanel panel){

		screens.put(id,panel);
		frame.getContentPane().add(panel,id.name());
	}
	public void navigate(Screen id){
		CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
		cl.show(frame.getContentPane(), id.name());
	}
}
