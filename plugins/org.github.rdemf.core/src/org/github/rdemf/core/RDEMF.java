package org.github.rdemf.core;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.IInjector;
import org.eclipse.e4.core.di.InjectionException;
import org.eclipse.e4.core.di.InjectorFactory;
import org.eclipse.e4.core.internal.contexts.ContextObjectSupplier;
import org.eclipse.e4.core.services.contributions.IContributionFactory;

@SuppressWarnings("restriction")
public class RDEMF {

	static {
		BindingManager.registerBindings();
	}
	
	public static <T> T make(Class<T> theInterface) {
		return make(theInterface, null);
	}

	/**
	 * Injects data into an object, using the eclipse context.
	 * 
	 * @param object
	 *            the object to perform injection on
	 */
	private static void doInject(Object object) {
		int cpt = 0;
		// FIX ME
		while (!Platform.isRunning()) {
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (cpt >= 5) {
				break;
			}
			cpt++;
		}
		inject(object, null);
	}

	/**
	 * Injects data into an object, using a specified context.
	 * 
	 * @param object
	 *            the object to perform injection on
	 */
	public static void inject(Object object, IEclipseContext context) {
		InjectorFactory.getDefault().inject(
				object,
				ContextObjectSupplier.getObjectSupplier(context,
						InjectorFactory.getDefault()));
	}

	/**
	 * Retrieves an instance of a given class. This instance will be injected
	 * with data found in the given context.
	 * 
	 * @param theInterface
	 * @param context
	 * @return
	 */
	public static <T> T make(Class<T> theInterface, IEclipseContext context) {
		if (context != null){
			return ContextInjectionFactory.make(theInterface, context);
		}
		else {
			return InjectorFactory.getDefault().make(theInterface, null);
		}
	}

	/**
	 * Call the annotated method on an object, injecting the parameters from the
	 * workbench contect or the given values.
	 * <p>
	 * If no matching method is found on the class, null will be returned.
	 * </p>
	 * 
	 * @param object
	 *            the object on which the method should be called
	 * @param qualifier
	 *            the annotation tagging method to be called
	 * @param parameters
	 *            that will be retrievable thanks to the annotation "Named"
	 * @return the return value of the method call, might be <code>null</code>
	 * @throws InjectionException
	 *             if an exception occurred while performing this operation
	 */
	public static Object invoke(Object object,
			Class<? extends Annotation> qualifier,
			Map<String, Object> stringParameters,
			Map<Class<?>, Object> classParameters) {
		return invoke(object, qualifier, null, stringParameters,
				classParameters);
	}

	/**
	 * Call the annotated method on an object, injecting the parameters from the
	 * context or the given values.
	 * <p>
	 * If no matching method is found on the class, null will be returned.
	 * </p>
	 * 
	 * @param object
	 *            the object on which the method should be called
	 * @param qualifier
	 *            the annotation tagging method to be called
	 * @param context
	 *            the eclipse context from which some of the values should be
	 *            returned.
	 * @param stringParameters
	 *            that will be retrievable thanks to the annotation "Named"
	 * @return the return value of the method call, might be <code>null</code>
	 * @throws InjectionException
	 *             if an exception occurred while performing this operation
	 */
	@SuppressWarnings("unchecked")
	public static Object invoke(Object object,
			Class<? extends Annotation> qualifier, IEclipseContext context,
			Map<String, Object> stringParameters,
			Map<Class<?>, Object> classParameters) {
		IEclipseContext temporaryContext = EclipseContextFactory.create();
		if (stringParameters != null) {
			for (Entry<String, Object> entry : stringParameters.entrySet()) {
				temporaryContext.set(entry.getKey(), entry.getValue());
			}
		}
		if (classParameters != null) {
			for (Entry<Class<?>, Object> entry : classParameters.entrySet()) {
				@SuppressWarnings("rawtypes")
				Class key = entry.getKey();
				Object value = entry.getValue();
				if (key.isInstance(value)) {
					temporaryContext.set(key, value);
				}
			}
		}
		IInjector defaultInjector = InjectorFactory.getDefault();
		return defaultInjector.invoke(object, qualifier, null,
				ContextObjectSupplier.getObjectSupplier(context,
						defaultInjector), ContextObjectSupplier
						.getObjectSupplier(temporaryContext, defaultInjector));
	}

	/**
	 * Creates an object from a bundleclass uri, and injects it with values
	 * present in the given context.
	 * <p>
	 * The format of the uriString should be
	 * "bundleclass://plugin_id/class_qualified_name".
	 * </p>
	 */
	public static Object createInstance(String uriString,
			IEclipseContext context) {
		IContributionFactory contributionFactory = context
				.get(IContributionFactory.class);
		return contributionFactory.create(uriString, context);
	}

	/**
	 * Creates an object from a bundleclass uri, and injects it with values
	 * present in the workbench context.
	 * <p>
	 * The format of the uriString should be
	 * "bundleclass://plugin_id/class_qualified_name".
	 * </p>
	 */
	public static Object createInstance(String uriString) {
		return createInstance(uriString, null);
	}

	public static void inject(Object... objs) {
		for (Object o : objs) {
			doInject(o);
		}
	}
}
