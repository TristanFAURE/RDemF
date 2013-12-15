package org.github.rdemf.rdf2ecore.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.INullSelectionListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.github.rdemf.core.RDEMF;
import org.github.rdemf.rdf2ecore.IRDF2Ecore;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class OpenRDFHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public OpenRDFHandler() {
	}
	
	IRDF2Ecore rdf2ecore = RDEMF.make(IRDF2Ecore.class);

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		InputDialog d = new InputDialog(HandlerUtil.getActiveShell(event), "RDF URL", "Please enter URL", "", null);
		if (d.open() == InputDialog.OK){
			String result = d.getValue();
		}
		
		return null;
	}
}
