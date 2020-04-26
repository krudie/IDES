package main;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import ides.api.core.Hub;
import ides.api.model.supeventset.SupervisoryEventSet;
import ides.api.plugin.Plugin;
import ides.api.plugin.PluginInitException;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.operation.OperationManager;
import ides.api.plugin.presentation.ToolsetManager;
import io.AnnotatedModelPlugin;
import io.fsa.ver2_1.EPSPlugin;
import io.fsa.ver2_1.FSAFileIOPlugin;
import io.fsa.ver2_1.GrailPlugin;
import io.fsa.ver2_1.JPEGPlugin;
import io.fsa.ver2_1.LatexPlugin;
import io.fsa.ver2_1.PNGPlugin;
import io.supeventset.ver3.SupEventSetFileIOPlugin;
import model.supeventset.ver3.SupervisoryEventSetDescriptor;
import operations.fsa.ver2_1.Accessible;
import operations.fsa.ver2_1.Coaccessible;
import operations.fsa.ver2_1.Complement;
import operations.fsa.ver2_1.Concatenation;
import operations.fsa.ver2_1.Containment;
import operations.fsa.ver2_1.ContainsString;
import operations.fsa.ver2_1.ControlMap;
import operations.fsa.ver2_1.Controllable;
import operations.fsa.ver2_1.Equality;
import operations.fsa.ver2_1.InfimalObservable;
import operations.fsa.ver2_1.LocalModular;
import operations.fsa.ver2_1.Meet;
import operations.fsa.ver2_1.Minimize;
import operations.fsa.ver2_1.MultiAgentProductFSA;
import operations.fsa.ver2_1.NFAtoDFA;
import operations.fsa.ver2_1.Nonconflicting;
import operations.fsa.ver2_1.Normal;
import operations.fsa.ver2_1.Observable;
import operations.fsa.ver2_1.PlusClosure;
import operations.fsa.ver2_1.PrefixClosure;
import operations.fsa.ver2_1.ProjectEpsilon;
import operations.fsa.ver2_1.ProjectEventSet;
import operations.fsa.ver2_1.ProjectUnobservable;
import operations.fsa.ver2_1.SelfLoop;
import operations.fsa.ver2_1.SetDifference;
import operations.fsa.ver2_1.StarClosure;
import operations.fsa.ver2_1.SupCon;
import operations.fsa.ver2_1.SupNorm;
import operations.fsa.ver2_1.SupRed;
import operations.fsa.ver2_1.SynchronousProduct;
import operations.fsa.ver2_1.Trim;
import operations.fsa.ver2_1.Union;
import presentation.supeventset.SupEventSetToolset;

/**
 * Deals with the management of plugins.
 * 
 * @author Lenko Grigorov
 */
public class PluginManager {
    // Make the class non-instantiable.
    private PluginManager() {
    }

    @Override
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }

    protected static Set<Plugin> loadedPlugins = new TreeSet<Plugin>(new Comparator<Plugin>() {
        public int compare(Plugin o1, Plugin o2) {
            return o1.getName().compareTo(o2.getName());
        }
    });

    protected static void initInternalPlugins() {
        // Register FSA operations
        OperationManager.instance().register(new Meet());
        OperationManager.instance().register(new SynchronousProduct());
        OperationManager.instance().register(new Accessible());
        OperationManager.instance().register(new Coaccessible());
        OperationManager.instance().register(new Trim());
        OperationManager.instance().register(new PrefixClosure());
        OperationManager.instance().register(new Controllable());
        OperationManager.instance().register(new SupCon());
        OperationManager.instance().register(new Containment());
        OperationManager.instance().register(new Nonconflicting());
        OperationManager.instance().register(new ControlMap());
        OperationManager.instance().register(new LocalModular());
        OperationManager.instance().register(new SupRed());
        OperationManager.instance().register(new MultiAgentProductFSA());
        OperationManager.instance().register(new SelfLoop());
        OperationManager.instance().register(new Minimize());
        OperationManager.instance().register(new SetDifference());
        OperationManager.instance().register(new Complement());
        OperationManager.instance().register(new Union());
        OperationManager.instance().register(new Concatenation());
        OperationManager.instance().register(new ProjectEpsilon());
        OperationManager.instance().register(new ProjectUnobservable());
        OperationManager.instance().register(new ProjectEventSet());
        OperationManager.instance().register(new Equality());
        OperationManager.instance().register(new NFAtoDFA());
        OperationManager.instance().register(new Observable());
        OperationManager.instance().register(new Normal());
        OperationManager.instance().register(new SupNorm());
        OperationManager.instance().register(new StarClosure());
        OperationManager.instance().register(new PlusClosure());
        OperationManager.instance().register(new InfimalObservable());
        OperationManager.instance().register(new ContainsString());

        // Input/Output plugins:
        new FSAFileIOPlugin().initialize();
        new AnnotatedModelPlugin().initialize();
        new SupEventSetFileIOPlugin().initialize();

        // Import/Export plugins:
        new GrailPlugin().initialize();
        new EPSPlugin().initialize();
        new LatexPlugin().initialize();
        new PNGPlugin().initialize();
        new JPEGPlugin().initialize();

        // Model plugins:
        ModelManager.instance().registerModel(new SupervisoryEventSetDescriptor());

        // Toolset plugins:
        ToolsetManager.instance().registerToolset(SupervisoryEventSet.class, new SupEventSetToolset());

    }

    public static void init() {
        initInternalPlugins();

        Vector<URL> resources = new Vector<URL>();
        Vector<String> pluginClasses = new Vector<String>();
        Map<String, String> failedResources = new TreeMap<String, String>();

        // dynamically adding plugin resources
        File localLocation = new File((String) System.getProperties().get("user.dir"));
        File baseLocation = null;
        try {
            baseLocation = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (baseLocation.isFile()) {
                baseLocation = baseLocation.getParentFile();
            }
        } catch (URISyntaxException e) {
            Hub.getNoticeManager().postErrorTemporary(Hub.string("errorLoadingPluginsShort"),
                    Hub.string("errorLoadingPlugins") + " " + Hub.string("errorLoadingPluginsNoAccess"));
        }
        if (localLocation.equals(baseLocation)) {
            baseLocation = null;
        }
        Vector<File> libs = new Vector<File>();
        Vector<File> plugins = new Vector<File>();
        if (baseLocation != null) {
            File baseLib = new File(baseLocation.getAbsolutePath() + File.separator + "lib");
            File basePlugins = new File(baseLocation.getAbsolutePath() + File.separator + "plugins");
            if (!baseLib.exists() || !basePlugins.exists() || !baseLib.isDirectory() || !basePlugins.isDirectory()) {
                Hub.getNoticeManager().postErrorTemporary(Hub.string("errorLoadingPluginsShort"),
                        Hub.string("errorLoadingPlugins") + " " + Hub.string("errorLoadingPluginsNoAccess") + " ("
                                + baseLib.getAbsolutePath() + "; " + basePlugins.getAbsolutePath() + ")");
            } else {
                libs.addAll(Arrays.asList(baseLib.listFiles()));
                plugins.addAll(Arrays.asList(basePlugins.listFiles()));
            }
        }
        File localLib = new File(localLocation.getAbsolutePath() + File.separator + "lib");
        File localPlugins = new File(localLocation.getAbsolutePath() + File.separator + "plugins");
        if (!localLib.exists() || !localPlugins.exists() || !localLib.isDirectory() || !localPlugins.isDirectory()) {
            Hub.getNoticeManager().postErrorTemporary(Hub.string("errorLoadingPluginsShort"),
                    Hub.string("errorLoadingPlugins") + " " + Hub.string("errorLoadingPluginsNoAccess") + " ("
                            + localLib.getAbsolutePath() + "; " + localPlugins.getAbsolutePath() + ")");
        } else {
            libs.addAll(Arrays.asList(localLib.listFiles()));
            plugins.addAll(Arrays.asList(localPlugins.listFiles()));
        }
        for (File f : plugins) {
            try {
                resources.add(f.toURI().toURL());
                String className = f.getName();
                if (className.endsWith(".jar")) {
                    className = className.substring(0, className.length() - 4);
                }
                pluginClasses.add(className);
            } catch (MalformedURLException e) {
                failedResources.put(f.getAbsolutePath(), "");
            }
        }
        for (File f : libs) {
            try {
                resources.add(f.toURI().toURL());
            } catch (MalformedURLException e) {
                failedResources.put(f.getAbsolutePath(), "");
            }
        }

        // load plugins
        URLClassLoader loader = new URLClassLoader(resources.toArray(new URL[] {}));
        for (String className : pluginClasses) {
            try {
                Class<?> pluginClass = loader.loadClass(className);
                if (Arrays.asList(pluginClass.getInterfaces()).contains(Plugin.class)) {
                    Constructor<?>[] constructors = pluginClass.getConstructors();
                    Constructor<?> constructor = null;
                    for (Constructor<?> c : constructors) {
                        if (Arrays.asList(c.getModifiers()).contains(Modifier.PUBLIC)
                                && c.getParameterTypes().length == 0) {
                            constructor = c;
                            break;
                        }
                    }
                    if (constructor == null) {
                        failedResources.put(className, Hub.string("errorLoadingPluginsNoConstructor"));
                    } else {
                        Plugin plugin = Plugin.class.cast(constructor.newInstance(new Object[] {}));
                        plugin.initialize();
                        loadedPlugins.add(plugin);
                    }
                }
            } catch (ClassNotFoundException e) {
                failedResources.put(className, Hub.string("errorLoadingPluginsNoClass"));
            } catch (InvocationTargetException e) {
                failedResources.put(className, Hub.string("errorLoadingPluginsInstantiation"));
            } catch (IllegalAccessException e) {
                failedResources.put(className, Hub.string("errorLoadingPluginsInstantiation"));
            } catch (InstantiationException e) {
                failedResources.put(className, Hub.string("errorLoadingPluginsInstantiation"));
            } catch (PluginInitException e) {
                failedResources.put(className, e.getMessage());
            }
        }

        if (!failedResources.isEmpty()) {
            String message = "";
            for (String key : failedResources.keySet()) {
                message += key + ("".equals(failedResources.get(key)) ? "" : " [" + failedResources.get(key) + "]")
                        + "; ";
            }
            Hub.getNoticeManager().postErrorTemporary(Hub.string("errorLoadingPluginsShort"),
                    Hub.string("errorLoadingPlugins") + " " + message);
        }
    }

    public static void cleanup() {
        for (Plugin plugin : loadedPlugins) {
            plugin.unload();
        }
    }

    public static Collection<Plugin> getLoadedPlugins() {
        return loadedPlugins;
    }
}
