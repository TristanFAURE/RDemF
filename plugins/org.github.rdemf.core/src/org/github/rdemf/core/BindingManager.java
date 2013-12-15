package org.github.rdemf.core;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.InjectorFactory;

import com.google.common.base.Supplier;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SortedSetMultimap;

@SuppressWarnings({ "rawtypes", "restriction" })
public class BindingManager {

        private static String EXT_POINT = "binding";

        @SuppressWarnings("unchecked")
        public static void registerBindings() {
                Map<Class, Collection<PriorityClass>> map = new HashMap<Class, Collection<PriorityClass>>();
                Supplier<SortedSet<PriorityClass>> factory = new Supplier<SortedSet<PriorityClass>>() {

                        @Override
                        public SortedSet<PriorityClass> get() {
                                SortedSet<PriorityClass> sorted = new TreeSet<PriorityClass>(new Comparator<PriorityClass>() {
                                                        @Override
                                                        public int compare(PriorityClass o1, PriorityClass o2) {
                                                                int result = new Integer(o1.priority).compareTo(o2.priority);
                                                                if (result == 0 && !o1.equals(o2)) {
                                                                        result = -1;
                                                                }
                                                                return result;
                                                        }
                                                });
                                return sorted;
                        }
                };
                SortedSetMultimap<Class, PriorityClass> multi = Multimaps.newSortedSetMultimap(map, factory);
                IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(Activator.PLUGIN_ID, EXT_POINT);
                for(IConfigurationElement e : elements) {
                        String theInterface = e.getAttribute("interface");
                        String theImpl = e.getAttribute("impl");
                        String name = e.getAttribute("name");
                        Class theInterfaceC = loadClass(e, theInterface);
                        Class theImplC = loadClass(e, theImpl);
                        int priority = 0;
                        String prio = e.getAttribute("priority");
                        if(prio != null && prio.length() > 0) {
                                try {
                                        priority = Integer.parseInt(prio);
                                } catch (NumberFormatException ex) {
                                }
                        }
                        if(theInterfaceC != null && theImplC != null && theInterfaceC.isAssignableFrom(theImplC)) {
                                PriorityClass priorityClass = new PriorityClass();
                                priorityClass.aClass = theImplC;
                                priorityClass.priority = priority;
                                priorityClass.name = name;
                                multi.put(theInterfaceC, priorityClass);
                        }
                }
                for(Class c : multi.keys()) {
                        PriorityClass last = null;
                        // for each priority class if name is present a binding is created with the given name
                        for(PriorityClass pc : multi.get(c)) {
                                if(pc.name != null && pc.name.length() > 0) {
                                        InjectorFactory.getDefault().addBinding(c).implementedBy(pc.aClass).named(pc.name);
                                } 
                                last = pc;
                        }
                        if(last != null) {
                                // the binding by default is made by a class with no name
                                InjectorFactory.getDefault().addBinding(c).implementedBy(last.aClass);
                        }
                }

        }

        private static Class loadClass(IConfigurationElement e, String theClass) {
                try {
                        return Platform.getBundle(e.getContributor().getName()).loadClass(theClass);
                } catch (InvalidRegistryObjectException e1) {
                        e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                        e1.printStackTrace();
                }
                return null;
        }

        private static class PriorityClass {

                public String name;

                public Class aClass;

                public int priority;
        }

}