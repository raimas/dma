/*******************************************************************************
 * 2008-2013 Public Domain
 * Contributors
 * Marco Lopes (marcolopes@netc.pt)
 *******************************************************************************/
package org.dma.eclipse.core.progress;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.dma.java.utils.Debug;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ProgressSupport extends LinkedHashMap<IProgressAction, String> {

	private static final long serialVersionUID = 1L;

	private final String title;
	private IProgressAction action;
	private int work;

	public ProgressSupport(String title){
		this.title=title;
	}


	public void add(Class klass) {

		Assert.isTrue(IProgressAction.class.isAssignableFrom(klass));

		try{
			IProgressAction action=((Class<IProgressAction>)klass).newInstance();
			add(action, action.getClass().getName());

		} catch (Exception e){
			e.printStackTrace();
		}

	}


	public void add(Class klass, String taskName) {

		try{
			add(((Class<IProgressAction>)klass).newInstance(), taskName);

		} catch (Exception e){
			e.printStackTrace();
		}

	}


	public void add(IProgressAction action, String taskName) {

		put(action, taskName);
		work=100/size();

	}


	public boolean run() throws Exception {

		final Error error=new Error();

		try{
			new ProgressMonitorDialog(null).run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor) throws InterruptedException {

					monitor.beginTask(title, 100);

					// execute the tasks
					for(Iterator<IProgressAction> iterator=keySet().iterator();
						iterator.hasNext() && error.getCause()==null;){

						action=iterator.next();

						//task name
						String taskName=get(action);
						Debug.out("TASK", taskName);
						monitor.subTask(taskName);

						try{
							action.run();
						}catch(Exception e){
							throw new RuntimeException(e);
						}
						if (monitor.isCanceled()){
							throw new InterruptedException();
						}
						monitor.worked(work);

					}

					monitor.done();

				}
			});

			return error.getCause()==null;

		}catch (InterruptedException e){
			Debug.out("InterruptedException");
			return false;
		}

	}


	public String getActionClass() {
		return action.getClass().getCanonicalName();
	}


	public void debug() {
		for(String value: values()){
			System.out.println(value);
		}
	}


}