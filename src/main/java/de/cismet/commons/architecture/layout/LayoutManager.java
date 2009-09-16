/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.commons.architecture.layout;

import de.cismet.commons.architecture.broker.AdvancedPluginBroker;
import de.cismet.commons.architecture.broker.BrokerLookup;
import de.cismet.commons.architecture.interfaces.Widget;
import de.cismet.commons.architecture.widget.AbstractWidget;
import de.cismet.tools.CurrentStackTrace;
import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.NoWriteError;
import de.cismet.tools.gui.StaticSwingTools;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import net.infonode.docking.RootWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.mouse.DockingWindowActionMouseButtonListener;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.PropertiesUtil;
import net.infonode.docking.util.StringViewMap;
import net.infonode.gui.componentpainter.AlphaGradientComponentPainter;
import net.infonode.util.Direction;
import org.jdom.Element;

/**
 *
 * @author spuhl
 */
public class LayoutManager implements Configurable {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LayoutManager.class);
    //private AdvancedPluginBroker brokerName;
    private StringViewMap viewMap = new StringViewMap();
    private String layoutFolder;
    private String layoutFileName;
    private String pluginLayoutFileName;
    private InputStream defaultLayout;
    private static final String USER_HOME_DIRECTORY = System.getProperty("user.home");
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private AdvancedPluginBroker broker;
    private RootWindow rootWindow;
    private boolean isInit = true;
    private ArrayList<View> views = new ArrayList<View>();
    private LookAndFeel lookAndFeel = null;

    public void configure(Element parent) {
    }

    public void configureInfoNodeDocking() {
        try {
            createViewsForWidgets();
            rootWindow = DockingUtil.createRootWindow(viewMap, true);
            log.debug("RootWindow created");
            //broker.setRootWindow(rootWindow);
            //InfoNode configuration
            rootWindow.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);
            DockingWindowsTheme theme = new ShapedGradientDockingTheme();
            rootWindow.getRootWindowProperties().addSuperObject(
                    theme.getRootWindowProperties());

            RootWindowProperties titleBarStyleProperties =
                    PropertiesUtil.createTitleBarStyleRootWindowProperties();

            rootWindow.getRootWindowProperties().addSuperObject(
                    titleBarStyleProperties);

            rootWindow.getRootWindowProperties().getDockingWindowProperties().setUndockEnabled(true);
            AlphaGradientComponentPainter x = new AlphaGradientComponentPainter(java.awt.SystemColor.inactiveCaptionText, java.awt.SystemColor.activeCaptionText, java.awt.SystemColor.activeCaptionText, java.awt.SystemColor.inactiveCaptionText);
            rootWindow.getRootWindowProperties().getDragRectangleShapedPanelProperties().setComponentPainter(x);
            broker.setTitleBarComponentpainter(broker.DEFAULT_MODE_COLOR);
            rootWindow.getRootWindowProperties().getTabWindowProperties().getTabbedPanelProperties().setPaintTabAreaShadow(true);
            rootWindow.getRootWindowProperties().getTabWindowProperties().getTabbedPanelProperties().setShadowSize(10);
            rootWindow.getRootWindowProperties().getTabWindowProperties().getTabbedPanelProperties().setShadowStrength(0.8f);
            log.debug("broker: " + broker);
            log.debug("parent: " + broker.getParentComponent());
            log.debug("rootWindow: " + rootWindow);

            broker.getParentComponent().add(rootWindow, BorderLayout.CENTER);
        } catch (Exception ex) {
            log.error("Error while configuring InfoNodeDocking: ", ex);
        }
    }

    private void createViewsForWidgets() {
        log.debug("Create views for widget");
        //final ArrayList<View> createdViews = new ArrayList<View>();

        //return createdViews;
        Vector<Widget> widgets = broker.getWidgets();
        if (widgets != null) {
            log.debug("Widgets count: " + widgets.size());
            for (Widget curWidget : widgets) {
                try {
                    //ToDo proper solution for cast should be in architecture that there are no widgets which are not derived from AbstractWidget
                    View tmpView = new View(curWidget.getWidgetName(), curWidget.getWidgetIcon(), (AbstractWidget) curWidget);
                    tmpView.getCustomTitleBarComponents().addAll(curWidget.getCustomButtons());
                    viewMap.addView(curWidget.getWidgetName(), tmpView);
                    views.add(tmpView);
                    log.debug("Widget: " + curWidget.getWidgetName() + " added to viewMap");
                } catch (Exception ex) {
                    log.error("Error while adding Widget: " + curWidget.getWidgetName(), ex);
                }
            }
        } else {
            log.debug("There are no widgets available");
        }
    }

    //ToDo create default LayoutFile use
    public void doLayoutInfoNodeDefaultFile() {

        if (defaultLayout != null) {
            loadLayout(defaultLayout, true);
        } else {
            doLayoutInfoNodeDefault();
        }
        //aktenzeichenFloatingWindow = rootWindow.createFloatingWindow(new Point(406, 175), new Dimension(300, 613), vAktenzeichenSuche);
//        vDMS.restoreFocus();
//        vKarte.restoreFocus();
    }

    public void doLayoutInfoNodeDefault() {
        rootWindow.setWindow(
                new TabWindow(views.toArray(views.toArray(new View[views.size()]))));
    }

    public void saveLayout() {
        JFileChooser fc = new JFileChooser(layoutFolder);
        fc.setFileFilter(new FileFilter() {

            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".layout");
            }

            public String getDescription() {
                return "Layout";
            }
        });
        fc.setMultiSelectionEnabled(false);
        int state = fc.showSaveDialog(broker.getParentComponent());
        log.debug("state:" + state);
        if (state == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            log.debug("file:" + file);
            String name = file.getAbsolutePath();
            name = name.toLowerCase();
            if (name.endsWith(".layout")) {
                saveLayout(name);
            } else {
                saveLayout(name + ".layout");
            }
        }
    }

    public void saveUserLayout() {
        if (broker.isApplicationPlugin()) {
            log.debug("Speichere PluginLayout nach: " + pluginLayoutFileName);
            saveLayout(pluginLayoutFileName);
        } else {
            log.debug("Speichere StandaloneLayout nach: " + layoutFileName);
            saveLayout(layoutFileName);
        }
    }

    public void saveLayout(String file) {
        broker.setTitleBarComponentpainter(broker.DEFAULT_MODE_COLOR);
        log.debug("Saving Layout.. to " + file);
        File layoutFile = new File(file);
        try {
            if (!layoutFile.exists()) {
                log.debug("Saving Layout.. File does not exit");
                layoutFile.createNewFile();
            } else {
                log.debug("Saving Layout.. File does exit");
            }
            FileOutputStream layoutOutput = new FileOutputStream(layoutFile);
            ObjectOutputStream out = new ObjectOutputStream(layoutOutput);
            rootWindow.write(out);
            out.flush();
            out.close();
            log.debug("Saving Layout.. to " + file + " successfull");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(broker.getParentComponent(), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.saving_layout_failure"), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.message_title"), JOptionPane.INFORMATION_MESSAGE);
            log.error("A failure occured during writing the layout file", ex);
        }
    }

    public void loadUserLayout() {
        if (broker.isApplicationPlugin()) {
            loadLayout(pluginLayoutFileName);
        } else {
            loadLayout(layoutFileName);
        }
    }

    public void loadLayout() {
        JFileChooser fc = new JFileChooser(layoutFolder);
        fc.setFileFilter(new FileFilter() {

            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".layout");
            }

            public String getDescription() {
                return "Layout";
            }
        });
        fc.setMultiSelectionEnabled(false);
        int state = fc.showOpenDialog(broker.getParentComponent());
        if (state == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String name = file.getAbsolutePath();
            name = name.toLowerCase();
            if (name.endsWith(".layout")) {
                loadLayout(name);
            } else {
                //TODO Schwachsinn
                JOptionPane.showMessageDialog(broker.getParentComponent(), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.format_failure_message"), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.message_title"), JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void loadLayout(ObjectInputStream in, boolean defaultFile) {
        log.debug("load layout: ", new CurrentStackTrace());
        try {
            rootWindow.read(in);
            in.close();
            rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);
            rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);
            if (isInit) {
                int count = viewMap.getViewCount();
                for (int i = 0; i < count; i++) {
                    View current = viewMap.getViewAtIndex(i);
                    if (current.isUndocked()) {
                        current.dock();
                    }
                }
            }
            log.debug("Loading Layout successfull");
        } catch (IOException ex) {
            log.warn("loading of default layout failed. Loading programmed layout", ex);
            if (defaultFile) {
                if (isInit) {
                    //ToDo
                    //JOptionPane.showMessageDialog(broker.getParentComponent(), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.loading_layout_failure_message_init"), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.message_title"), JOptionPane.INFORMATION_MESSAGE);
                    log.warn("Loading programmed layout");
                    doLayoutInfoNodeDefault();
                } else {
                    JOptionPane.showMessageDialog(broker.getParentComponent(), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.loading_layout_failure_message"), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.message_title"), JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                log.error("Layout File IO Exception --> loading default Layout", ex);
                if (isInit) {
                    //ToDo
                    //JOptionPane.showMessageDialog(broker.getParentComponent(), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.loading_layout_failure_message_init"), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.message_title"), JOptionPane.INFORMATION_MESSAGE);
                    if (defaultFile) {
                        doLayoutInfoNodeDefault();
                    } else {
                        doLayoutInfoNodeDefaultFile();
                    }

                } else {
                    JOptionPane.showMessageDialog(broker.getParentComponent(), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.loading_layout_failure_message"), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.message_title"), JOptionPane.INFORMATION_MESSAGE);
                }
            }

        }
    }

    public void loadLayout(InputStream inputStream, final boolean defaultFile) {
        log.debug("load layout from classpath: "+inputStream);
        try {
            ObjectInputStream in = new ObjectInputStream(inputStream);
            loadLayout(in, defaultFile);
        } catch (IOException ex) {
            log.warn("load of default layout file failed", ex);
            if (isInit) {
                //ToDo
                //JOptionPane.showMessageDialog(broker.getParentComponent(), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.loading_layout_failure_message_init"), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.message_title"), JOptionPane.INFORMATION_MESSAGE);
                log.warn("loading programmed layout", ex);
                doLayoutInfoNodeDefault();
            } else {
                JOptionPane.showMessageDialog(broker.getParentComponent(), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.loading_layout_failure_message"), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.message_title"), JOptionPane.INFORMATION_MESSAGE);
                doLayoutInfoNodeDefault();
            }
        }
    }

    public void loadLayout(File file, final boolean defaultFile) {
        log.debug("load layout from file: "+file);
        if (file.exists()) {
            try {
                log.debug("Layout File exists");
                FileInputStream layoutInput = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(layoutInput);
                loadLayout(in, defaultFile);
            } catch (IOException ex) {
                log.error("Layout File IO Exception --> loading default Layout", ex);
                if (isInit) {
                    //ToDo
                    //JOptionPane.showMessageDialog(broker.getParentComponent(), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.loading_layout_failure_message_init"), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.message_title"), JOptionPane.INFORMATION_MESSAGE);
                    if (defaultFile) {
                        doLayoutInfoNodeDefault();
                    } else {
                        doLayoutInfoNodeDefaultFile();
                    }

                } else {
                    JOptionPane.showMessageDialog(broker.getParentComponent(), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.loading_layout_failure_message"), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.message_title"), JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            if (isInit) {
                log.warn("Datei exitstiert nicht --> default layout (init)");
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        //UGLY WINNING --> Gefixed durch IDW Version 1.5
                        //setupDefaultLayout();
                        //DeveloperUtil.createWindowLayoutFrame("nach setup1",rootWindow).setVisible(true);
                        if (defaultFile) {
                            doLayoutInfoNodeDefault();
                        } else {
                            doLayoutInfoNodeDefaultFile();
                        }
                        //DeveloperUtil.createWindowLayoutFrame("nach setup2",rootWindow).setVisible(true);
                    }
                });
            } else {
                log.warn("Datei exitstiert nicht)");
                JOptionPane.showMessageDialog(broker.getParentComponent(), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.layout_does_not_exist"), java.util.ResourceBundle.getBundle("de/cismet/cismap/navigatorplugin/Bundle").getString("CismapPlugin.InfoNode.message_title"), JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void loadLayout(String file) {
        log.debug("Load Layout.. from " + file);
        File layoutFile = new File(file);
        loadLayout(layoutFile, false);
    }

    public Element getConfiguration() throws NoWriteError {
        return null;
    }

    public void masterConfigure(final Element parent) {
        final Element layoutConf = parent.getChild("LayoutManager");
        try {
            layoutFolder = layoutConf.getChildText("LayoutFolder");
            layoutFolder = USER_HOME_DIRECTORY + FILE_SEPARATOR + layoutFolder;
            log.info("LayoutFolder: " + layoutFolder);
        } catch (Exception ex) {
            log.warn("Error while loading LayoutFolder", ex);
        }
        try {
            layoutFileName = layoutConf.getChildText("LayoutFileName");
            layoutFileName = layoutFolder + FILE_SEPARATOR + layoutFileName;
            log.info("LayoutFileName: " + layoutFileName);
        } catch (Exception ex) {
            log.warn("Error while loading LayoutFileName", ex);
        }
        try {
            pluginLayoutFileName = layoutConf.getChildText("PluginLayoutFileName");
            pluginLayoutFileName = layoutFolder + FILE_SEPARATOR + pluginLayoutFileName;
            log.info("PluginLayoutFileName: " + pluginLayoutFileName);
        } catch (Exception ex) {
            log.warn("Error while loading PluginLayoutFileName", ex);
        }
        try {
            String defaultLayoutFileFromClasspath = layoutConf.getChildText("DefaultLayout");
            log.debug("defaultLayoutFileFromClasspath: " + defaultLayoutFileFromClasspath);
            defaultLayout = getClass().getResourceAsStream(defaultLayoutFileFromClasspath);
            log.info("defaultLayoutFile: " + defaultLayout);
            log.info("defaultLayoutFile: "+defaultLayout.available());
            int current=0;
            while((current=defaultLayout.read()) != -1){
                log.debug("Layout raw: "+current);
            }
        } catch (Exception ex) {
            log.warn("Error while loading defaultLayoutFile", ex);
            defaultLayout = null;
        }
        try {
            broker = BrokerLookup.getInstance().getBrokerForName(parent.getChild("BrokerConfiguration").getChild("Broker").getChildText("BrokerName"));
        } catch (Exception ex) {
            log.warn("Error while retrieving broker instance", ex);
        }
        try {
//            String lookAndFeelName = layoutConf.getChildText("DefaultLookAndFeel");
//            log.debug("Try to set LookAndFeel: " + lookAndFeelName);
//            Class lookAndFeelClass = Class.forName(lookAndFeelName);            
//            Constructor constructor = lookAndFeelClass.getConstructor();
//            lookAndFeel = (LookAndFeel) constructor.newInstance();            
//            javax.swing.UIManager.setLookAndFeel(lookAndFeel);                
//            log.debug("broker: "+broker);
//            log.debug("broker ParentComponent: "+broker.getParentComponent());
//            log.debug("ParentFrame: "+StaticSwingTools.getParentFrame(broker.getParentComponent()));
//            SwingUtilities.updateComponentTreeUI(StaticSwingTools.getParentFrame(broker.getParentComponent()));            
            throw new UnsupportedOperationException("Not implemented yet.");
        } catch (Exception ex) {
            log.warn("Error while setting LookAndFeel", ex);
        }
    }

    public void addView(View view) {
        viewMap.addView(view);
    }

    public RootWindow getRootWindow() {
        if (rootWindow == null) {
            log.warn("rootWindow == null");
        }
        return rootWindow;
    }

    public void setRootWindow(RootWindow rootWindow) {
        this.rootWindow = rootWindow;
    }
}
