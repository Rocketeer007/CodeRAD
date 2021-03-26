/**
 *  This package contains controllers for CodeRAD applications.
 *  
 */
package com.codename1.rad.controllers;


/**
 *  A utility class that can provide action support for a class.  This is mainly a thin wrapper around {@link EventDispatcher}, but it also includes some static utility methods for event dispatch which are used
 *  for dispatching action events in CodeRAD.
 *  @author shannah
 */
public class ActionSupport {

	public ActionSupport() {
	}

	public void addActionListener(<any> l) {
	}

	public void removeActionListener(<any> l) {
	}

	public void fireActionEvent(ActionEvent evt) {
	}

	public static void addActionListener(Component cmp, ActionListener l) {
	}

	public static void removeActionListener(Component cmp, ActionListener l) {
	}

	/**
	 *  Dispatches an event, first using the source's action support,
	 *  if it implements EventProducer interface.  If event not consumed,
	 *  it will find the nearest ViewController and dispatch the event 
	 *  up the controller chain. Latter also requires that event is ControllerEvent
	 *  @param evt 
	 */
	public static void dispatchEvent(ActionEvent evt) {
	}
}
