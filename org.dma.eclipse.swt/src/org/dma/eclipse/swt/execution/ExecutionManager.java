/*******************************************************************************
 * 2008-2014 Public Domain
 * Contributors
 * Marco Lopes (marcolopes@netc.pt)
 * Paulo Silva (wickay@hotmail.com)
 *******************************************************************************/
package org.dma.eclipse.swt.execution;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.dma.java.utils.Debug;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class ExecutionManager {

	private static final Map<ExecutionDefinition, ExecutionEvent> map=new HashMap();

	/*
	 * Register
	 */
	public static void register(Control control, IAction action) {
		register(control, new int[]{-1}, action);
	}

	public static void register(Control control, int keycode[], IAction action) {
		register(control, keycode, null, action);
	}

	public static void register(Control control, int keycode[], String id, IAction action) {
		register(control, keycode, id, null, action);
	}

	public static void register(Control control, int keycode[], String id, String secondaryId, IAction action) {
		register(control, keycode, id, secondaryId, action, null, null);
	}

	public static void register(Control control, int keycode[], String id, IAction action, IAction responseAction, IAction postresponseAction) {
		register(control, keycode, id, null, action, responseAction, postresponseAction);
	}

	public static void register(Control control, int keycode[], String id, String secondaryId, IAction action, IAction responseAction, IAction postresponseAction) {
		ExecutionDefinition execDefinition=new ExecutionDefinition(id,secondaryId,control);
		ExecutionEvent execEvent=new ExecutionEvent(action,responseAction,postresponseAction,keycode);
		register(execDefinition, execEvent);
	}

	private static void register(ExecutionDefinition execDefinition, final ExecutionEvent execEvent) {

		try{
			if(!map.containsKey(execDefinition)) {

				execDefinition.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent event) {
						for(int keyCode: execEvent.getKeycode()){
							if(keyCode==event.keyCode) {
								Debug.out("EXECUTION");
								execEvent.getExecutionAction().run();
								execEvent.setActionExecuted(true);
								event.doit=false;
							}
						}
					}
				});

				if(execDefinition.getControl() instanceof Combo) {

					execDefinition.addSelectionListener(new Listener() {
						public void handleEvent(Event event) {
							Debug.out("EXECUTION");
							execEvent.getExecutionAction().run();
							execEvent.setActionExecuted(true);
						}
					});

				}

				map.put(execDefinition, execEvent);

				Debug.out(execEvent.getExecutionAction().getId(), map.size());

			}else{
				throw new Exception("EXECUTION ALREADY REGISTERED: "+execDefinition.getId());
			}

		}catch(Exception e){
			e.printStackTrace();
		}

	}



	/*
	 * Unregister
	 */
	public static void unregister(Control control) {

		for(Iterator<ExecutionDefinition> iterator=map.keySet().iterator(); iterator.hasNext();) {

			ExecutionDefinition execDefinition=iterator.next();

			if(execDefinition.getControl().equals(control)){

				ExecutionEvent execEvent=map.get(execDefinition);

				execDefinition.removeListeners();
				iterator.remove();

				Debug.out(execEvent.getExecutionAction().getId(), map.size());

			}

		}

	}



	/*
	 * Executions
	 */
	public static void notifyDependentExecutions(String id, String secondaryId) {

		for(ExecutionDefinition execDefinition: map.keySet()) {

			if(ObjectUtils.equals(id, execDefinition.getId()) &&
				ObjectUtils.equals(secondaryId, execDefinition.getSecondaryId())) {

				ExecutionEvent execEvent=map.get(execDefinition);

				if(execEvent.getResponseAction()!=null && execEvent.isActionExecuted()) {

					execEvent.getResponseAction().run();
					execEvent.setActionExecuted(false);

					if(execEvent.getPostresponseAction()!=null) {
						execEvent.getPostresponseAction().run();
					}

				}

			}

		}

	}


	public static void notifyDependentExecutions(String id) {

		notifyDependentExecutions(id, null);

	}


	private static boolean hasDependentExecutions(String id, String secondaryId) {

		for(ExecutionDefinition execDefinition: map.keySet()) {

			ExecutionEvent execEvent=map.get(execDefinition);

			if(execDefinition!=null && execEvent!=null &&
				execEvent.getPostresponseAction()!=null &&
					ObjectUtils.equals(id, execDefinition.getId()) &&
					ObjectUtils.equals(secondaryId, execDefinition.getSecondaryId()))
				return true;
		}

		return false;

	}


	private static boolean hasDependentExecutions(String id) {

		return hasDependentExecutions(id, null);

	}


}
