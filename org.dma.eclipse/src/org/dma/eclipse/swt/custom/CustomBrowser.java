/*******************************************************************************
 * 2008-2014 Public Domain
 * Contributors
 * Marco Lopes (marcolopes@netc.pt)
 *******************************************************************************/
package org.dma.eclipse.swt.custom;

import org.apache.commons.lang.SystemUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class CustomBrowser extends Browser {

	@Override //subclassing
	protected void checkSubclass() {}

	public static Integer detectStyle(Composite parent) {
		//WINDOWS
		if (SystemUtils.IS_OS_WINDOWS) return SWT.NONE;
		//LINUX e MAC
		for(int style: new int[]{SWT.MOZILLA, SWT.WEBKIT}){
			try{
				System.out.print("STYLE: "+style);
				Browser b=new Browser(parent, style);
				b.dispose();
				return style;

			}catch(SWTError e){
				System.out.println(e);
			}catch(SWTException e){
				System.out.println(e);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Creates a Browser with a new shell as a parent
	 * <p>
	 * A new shell is created with style SWT.NONE and
	 * bounds are set to 0 to make it invisible.
	 */
	public CustomBrowser(Display display) {
		this(new Shell(display,SWT.NONE));
		getShell().setBounds(0, 0, 0, 0);
	}

	/**
	 * Creates a platform dependant browser
	 */
	public CustomBrowser(Composite parent) {
		this(parent, detectStyle(parent));
	}

	/**
	 * Creates a platform dependant browser
	 * <p>
	 * <b>If running on Linux or Mac</b>
	 * one of these should be present in VM parameters:<br>
	 * -Dorg.eclipse.swt.browser.DefaultType=mozilla<br>
	 * -Dorg.eclipse.swt.browser.DefaultType=webkit
	 */
	public CustomBrowser(Composite parent, int style) {
		super(parent, style);
	}

	public boolean setFile(String filename) {
		return setUrl(filename);
	}


}