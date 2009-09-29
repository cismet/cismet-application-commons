/*
 * AbstractPlugin2.java
 *
 * Created on 4. März 2009, 16:32
 */
package de.cismet.commons.architecture.plugin;

import Sirius.navigator.plugin.context.PluginContext;
import Sirius.navigator.plugin.interfaces.FloatingPluginUI;
import Sirius.navigator.plugin.interfaces.PluginMethod;
import Sirius.navigator.plugin.interfaces.PluginProperties;
import Sirius.navigator.plugin.interfaces.PluginSupport;
import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.gui.ClipboardWaitDialog;
import de.cismet.cismap.commons.gui.statusbar.StatusBar;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.commons.architecture.broker.AdvancedPluginBroker;
import de.cismet.commons.architecture.broker.BrokerLookup;
import de.cismet.commons.architecture.exception.UnlockingNotSuccessfulException;
import de.cismet.tools.StaticDecimalTools;
import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.ConfigurationManager;
import de.cismet.tools.configuration.NoWriteError;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import java.applet.AppletContext;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.jdom.Element;

/**
 *
 * @author  spuhl
 */
public abstract class AbstractPlugin extends javax.swing.JFrame implements PluginSupport,
        FloatingPluginUI,
        Configurable {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AbstractPlugin.class);
    protected AdvancedPluginBroker broker = null;
    private String pluginConfigurationFile = "defaultPluginConfiguration";
    private static final String PLUGIN_CONFIGURATION_CLASSPATH = "/de/cismet/commons/architecture/configuration/";
    private ClipboardWaitDialog clipboarder;
    private AppletContext appletContext;
    private final PluginContext context;
    private Dimension windowSize = null;
    private Point windowLocation = null;
    private boolean readyToShow = false;
    private ArrayList<JMenuItem> menues = new ArrayList<JMenuItem>();
    /**                                                          
     * @param args the command line arguments
     */
    //ToDo make userdepending
    //ToDo make configurable
    private ConfigurationManager configManager;
    private String brokerName;

    public AbstractPlugin() {
        this(null);
    }

    public AbstractPlugin(final PluginContext context) {
        this.context = context;
        //initLog4J();
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                log.debug("windowClosing(): beende Application");
                log.debug("windowClosing(): Check if there unsaved changes.");
                cleanUp();
                dispose();
                //needed because the frames default closing op must be "do nothing"!
                System.exit(0);
            }
        });
        try {
            if (context != null && context.getEnvironment() != null && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment().getProgressObserver().setProgress(200, "Initialisieren der graphischen Oberfläche...");
            }
            initComponents();
            clipboarder = new ClipboardWaitDialog(this, true);
            if (context != null && context.getEnvironment() != null && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment().getProgressObserver().setProgress(400, "Lade Konfiguration...");
            }

            //ToDo give context to broker --> finer initalization messages map component initialized search components etc
            configurePlugin();
            //broker is conifgured
//            log.info("Start of " + broker.getApplicationName() + " Application");
//            log.debug("is Plugin: " + isPlugin());


            //application will be layouted
            if (context != null && context.getEnvironment() != null && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment().getProgressObserver().setProgress(600, "Layout der graphischen Benutzeroberflächee");
            }
            if (isPlugin()) {
                menues.add(menFile);
                menues.add(menEdit);
                menues.add(menHistory);
                //menues.add(menBookmarks);
                menues.add(menExtras);
                menues.add(menWindow);
                menues.add(menHelp);
            }
            KeyStroke configLoggerKeyStroke = KeyStroke.getKeyStroke('L', InputEvent.CTRL_MASK);
            Action configAction = new AbstractAction() {

                public void actionPerformed(ActionEvent e) {
                    java.awt.EventQueue.invokeLater(new Runnable() {

                        public void run() {
                            Log4JQuickConfig.getSingletonInstance().setVisible(true);
                        }
                    });
                }
            };
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(configLoggerKeyStroke, "CONFIGLOGGING");
            getRootPane().getActionMap().put("CONFIGLOGGING", configAction);
            constructionDone();
            add(broker.getToolbar(), BorderLayout.NORTH);
            //this.pack();
            setWindowSize();
            final Runnable startupComplete = new Runnable() {

                public void run() {
                    try {
                        broker.getLayoutManager().loadUserLayout();
                    } catch (Exception ex) {
                        log.error("Error while loading layout", ex);
                    }
                    setReadyToShow(true);
                    broker.getMappingComponent().unlock();
                    try {
                        if (context != null && context.getEnvironment() != null && context.getEnvironment().isProgressObservable()) {
                            context.getEnvironment().getProgressObserver().setProgress(1000, broker.getAccountName() + " initalisierung abgeschlossen");
                        }
                        if (context != null && context.getEnvironment() != null && context.getEnvironment().isProgressObservable()) {
                            context.getEnvironment().getProgressObserver().setFinished(true);
                        }
                    } catch (InterruptedException ex) {
                        log.error("Plugin was interrupted.", ex);
                    }
                }
            };
            if (EventQueue.isDispatchThread()) {
                startupComplete.run();
            } else {
                EventQueue.invokeLater(startupComplete);
            }
            //ToDo if the application is closed standalone the configuration will be written two times
        } catch (Exception ex) {
            log.fatal("Fatal Error in Abstract Plugin Constructor.", ex);
            System.out.println("Fatal Error in Abstract Plugin Constructor.");
            ex.printStackTrace();
        }
    }

    public boolean isReadyToShow() {
        return readyToShow;
    }

    public void setReadyToShow(boolean readyToShow) {
        this.readyToShow = readyToShow;
    }

    private void configurePlugin() {
        //ToDo Outsource in Broker ??
        configManager = new ConfigurationManager();
        //ToDo set paths
        configManager.setDefaultFileName(getPluginConfigurationFile());
        configManager.setFileName(getPluginConfigurationFile());
        //ToDo configManager initalize --> give xml file knows everything
        configManager.setClassPathFolder(PLUGIN_CONFIGURATION_CLASSPATH);
        configManager.initialiseLocalConfigurationClasspath();
        //configManager.setFolder(PLUGIN_CONFIGURATION_CLASSPATH);
        configManager.addConfigurable(this);
        configManager.configure(this);
    }

    private void constructionDone() {
        broker.pluginConstructionDone();
    }

    public String getBrokerName() {
        return brokerName;
    }

    //public abstract void setPluginConfigurationFile(String pluginConfigurationFile);
    public abstract String getPluginConfigurationFile();
//        return pluginConfigurationFile;
//    }  

//    public PluginMethod getMethod(String id) {
//        ////throw new UnsupportedOperationException("Not supported yet.");
//        return null;
//    }
//
//    public Iterator getMethods() {
//        ////throw new UnsupportedOperationException("Not supported yet.");
//        return null;
//    }
//
//    public PluginProperties getProperties() {
//        //throw new UnsupportedOperationException("Not supported yet.");
//        return null;
//    }
//
//    public PluginUI getUI(String id) {
//        //throw new UnsupportedOperationException("Not supported yet.");
//        return null;
//    }
//
//    public Iterator getUIs() {
//        //throw new UnsupportedOperationException("Not supported yet.");
//        return null;
//    }
//
//    public void setActive(boolean active) {
//        //throw new UnsupportedOperationException("Not supported yet.");
//    }
//    public void floatingStarted() {
//        //throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public void floatingStopped() {
//        //throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public Collection getButtons() {
//        //throw new UnsupportedOperationException("Not supported yet.");
//        return null;
//    }
//
//    public Collection getMenus() {
//        //throw new UnsupportedOperationException("Not supported yet.");
//        return null;
//    }
//
//    @Override
    public JComponent getComponent() {
        return panMain;
    }
//
//    @Override

    public void hidden() {
    }

    @Override
    public void moved() {
    }

    @Override
    public void resized() {
    }

    @Override
    public void shown() {
    }

    @Override
    public Collection getMenus() {
        return menues;
    }

    @Override
    public void setActive(boolean active) {
        log.debug("setActive:" + active);
        if (!active) {
            cleanUp();
            dispose();
            //CismapBroker.getInstance().cleanUpSystemRegistry();
        }
    }

    @Override
    public void floatingStarted() {
    }

    @Override
    public void floatingStopped() {
    }

    @Override
    public PluginMethod getMethod(String id) {
        return null;
    }

    @Override
    public Iterator getMethods() {
        LinkedList ll = new LinkedList();
        return ll.iterator();
    }

    @Override
    public PluginProperties getProperties() {
        return null;
    }

    @Override
    public String getId() {
        return broker.getApplicationName();
    }

    public void configure(Element parent) {
        Element prefs = parent.getChild("cismapPluginUIPreferences");
        try {
            log.debug("writing windowSize into Configuration");
            Element window = prefs.getChild("window");
            int windowHeight = window.getAttribute("height").getIntValue();
            int windowWidth = window.getAttribute("width").getIntValue();
            int windowX = window.getAttribute("x").getIntValue();
            int windowY = window.getAttribute("y").getIntValue();
            boolean windowMaximised = window.getAttribute("max").getBooleanValue();
            windowSize = new Dimension(windowWidth, windowHeight);
            windowLocation = new Point(windowX, windowY);
            log.debug("Fenstergröße: Breite " + windowWidth + " Höhe " + windowHeight);
            //TODO why is this not working
            //mapComponent.formComponentResized(null);
            if (windowMaximised) {
                this.setExtendedState(MAXIMIZED_BOTH);
            } else {
            }
            log.debug("Fenstergröße erfolgreich eingelesen");
        } catch (Throwable t) {
            //TODO defaults
            log.error("Fehler beim Laden der Fenstergröße", t);
        }
    }

    public Element getConfiguration() throws NoWriteError {
        Element ret = new Element("cismapPluginUIPreferences");
        Element window = new Element("window");
        int windowHeight = this.getHeight();
        int windowWidth = this.getWidth();
        int windowX = (int) this.getLocation().getX();
        int windowY = (int) this.getLocation().getY();
        boolean windowMaximised = (this.getExtendedState() == MAXIMIZED_BOTH);
        log.debug("Fenstergröße: Breite " + windowWidth + " Höhe " + windowHeight);
        window.setAttribute("height", "" + windowHeight);
        window.setAttribute("width", "" + windowWidth);
        window.setAttribute("x", "" + windowX);
        window.setAttribute("y", "" + windowY);
        window.setAttribute("max", "" + windowMaximised);
        ret.addContent(window);
        return ret;
    }

    public void masterConfigure(Element parent) {
        System.out.println("Master Configure: " + getClass().getName());
        try {
            final Element brokerConf = parent.getChild("BrokerConfiguration");
            try {
                configManager.addConfigurable(BrokerLookup.getInstance());
                configManager.configure(BrokerLookup.getInstance());
                brokerName = brokerConf.getChild("Broker").getChildText("BrokerName");
                broker = BrokerLookup.getInstance().getBrokerForName(brokerName);
                try {
                    final String applicationName = parent.getChild("Configuration").getChildText("ApplicationName");
                    this.setTitle(applicationName);
                    broker.setApplicatioName(applicationName);
                } catch (Exception ex) {
                    broker.setApplicatioName("Kein Name");
                    log.warn("Error while setting application title", ex);
                }
                broker.setParentComponent(panMain);
                broker.setConfigManager(configManager);
                broker.setContext(context);
                configManager.addConfigurable(broker);
                configManager.configure(broker);
            } catch (Exception ex) {
                log.warn("Error while retrieving broker instance", ex);
            }
//            try {
//                albURL = albConfiguration.getChildText("albURL");
//                if (albURL != null) {
//                    albURL = albURL.trim();
//                }
//                log.debug("ALBURL: " + albURL.trim());
//            } catch (Exception ex) {
//                log.warn("Fehler beim lesen der ALB Konfiguration", ex);
//            }
//            try {
//                log.debug("News Url: " + urls.getChildText("onlineHelp"));
//                newsURL = urls.getChildText("news");
//            } catch (Exception ex) {
//                log.warn("Fehler beim lesen der News Url", ex);
//            }
//            try {
//                log.debug("Glassfishhost: " + prefs.getChildText("host"));
//                System.setProperty("org.omg.CORBA.ORBInitialHost", prefs.getChildText("host"));
//            } catch (Exception ex) {
//                log.warn("Fehler beim lesen des Glassfish Hosts", ex);
//            }
//            try {
//                log.debug("Glassfisport: " + prefs.getChildText("orbPort"));
//                System.setProperty("org.omg.CORBA.ORBInitialPort", prefs.getChildText("orbPort"));
//            } catch (Exception ex) {
//                log.warn("Fehler beim lesen des Glassfish Ports", ex);
//            }
//            try{
            //ToDo wrong postion should be in a broker an is only added if != null && enabled or something like that
            try {
                StatusBar statusBar = new StatusBar(broker.getMappingComponent());
                broker.setStatusBar(statusBar);
                broker.getMappingComponent().getFeatureCollection().addFeatureCollectionListener(statusBar);
                CismapBroker.getInstance().addStatusListener(statusBar);
                //panStatusbar.add(statusBar);
                if (broker.isStatusBarEnabled()) {
                    add(statusBar, BorderLayout.SOUTH);
                }
            } catch (Exception ex) {
                log.error("Error whil configuring the statusbar: ", ex);
            }
        } catch (Exception ex) {
            log.error("Fehler beim konfigurieren der Lagis Applikation: ", ex);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panMain = new javax.swing.JPanel();
        mnuBar = new javax.swing.JMenuBar();
        menFile = new javax.swing.JMenu();
        mniSaveLayout = new javax.swing.JMenuItem();
        mniLoadLayout = new javax.swing.JMenuItem();
        mniLockLayout = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        mniClippboard = new javax.swing.JMenuItem();
        mniPrint = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        mniClose = new javax.swing.JMenuItem();
        menEdit = new javax.swing.JMenu();
        mniRefresh = new javax.swing.JMenuItem();
        menHistory = new javax.swing.JMenu();
        mniBack = new javax.swing.JMenuItem();
        mniForward = new javax.swing.JMenuItem();
        mniHome = new javax.swing.JMenuItem();
        sepBeforePos = new javax.swing.JSeparator();
        sepAfterPos = new javax.swing.JSeparator();
        mniHistorySidebar = new javax.swing.JMenuItem();
        menBookmarks = new javax.swing.JMenu();
        mniAddBookmark = new javax.swing.JMenuItem();
        mniBookmarkManager = new javax.swing.JMenuItem();
        mniBookmarkSidebar = new javax.swing.JMenuItem();
        menExtras = new javax.swing.JMenu();
        mniOptions = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JSeparator();
        mniGotoPoint = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JSeparator();
        mniScale = new javax.swing.JMenuItem();
        menWindow = new javax.swing.JMenu();
        jSeparator14 = new javax.swing.JSeparator();
        mniResetWindowLayout = new javax.swing.JMenuItem();
        menHelp = new javax.swing.JMenu();
        mniOnlineHelp = new javax.swing.JMenuItem();
        mniNews = new javax.swing.JMenuItem();
        mniVersions = new javax.swing.JMenuItem();
        mniLisences = new javax.swing.JMenuItem();
        mniAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 600));

        panMain.setLayout(new java.awt.BorderLayout());
        getContentPane().add(panMain, java.awt.BorderLayout.CENTER);

        menFile.setMnemonic('D');
        menFile.setText("Datei");

        mniSaveLayout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mniSaveLayout.setText("Aktuelles Layout speichern");
        mniSaveLayout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSaveLayoutActionPerformed(evt);
            }
        });
        menFile.add(mniSaveLayout);

        mniLoadLayout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        mniLoadLayout.setText("Layout laden");
        mniLoadLayout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniLoadLayoutActionPerformed(evt);
            }
        });
        menFile.add(mniLoadLayout);

        mniLockLayout.setText("Layout sperren");
        mniLockLayout.setEnabled(false);
        mniLockLayout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniLockLayoutActionPerformed(evt);
            }
        });
        menFile.add(mniLockLayout);

        jSeparator8.setEnabled(false);
        menFile.add(jSeparator8);

        mniClippboard.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        mniClippboard.setText("Bild der Karte in die Zwischenablage kopieren");
        mniClippboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniClippboardActionPerformed(evt);
            }
        });
        menFile.add(mniClippboard);

        mniPrint.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        mniPrint.setText("Drucken");
        mniPrint.setEnabled(false);
        menFile.add(mniPrint);

        jSeparator9.setEnabled(false);
        menFile.add(jSeparator9);

        mniClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        mniClose.setText("Beenden");
        mniClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniCloseActionPerformed(evt);
            }
        });
        menFile.add(mniClose);

        mnuBar.add(menFile);

        menEdit.setMnemonic('B');
        menEdit.setText("Bearbeiten");

        mniRefresh.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        mniRefresh.setText("Neu laden");
        mniRefresh.setEnabled(false);
        mniRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniRefreshActionPerformed(evt);
            }
        });
        menEdit.add(mniRefresh);

        mnuBar.add(menEdit);

        menHistory.setMnemonic('C');
        menHistory.setText("Chronik");

        mniBack.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT, java.awt.event.InputEvent.CTRL_MASK));
        mniBack.setText("Zurück");
        mniBack.setEnabled(false);
        mniBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniBackActionPerformed(evt);
            }
        });
        menHistory.add(mniBack);

        mniForward.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT, java.awt.event.InputEvent.CTRL_MASK));
        mniForward.setText("Vor");
        mniForward.setEnabled(false);
        mniForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniForwardActionPerformed(evt);
            }
        });
        menHistory.add(mniForward);

        mniHome.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_HOME, 0));
        mniHome.setText("Home");
        mniHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniHomeActionPerformed(evt);
            }
        });
        menHistory.add(mniHome);

        sepBeforePos.setEnabled(false);
        menHistory.add(sepBeforePos);

        sepAfterPos.setEnabled(false);
        menHistory.add(sepAfterPos);

        mniHistorySidebar.setText("In eigenem Fenster anzeigen");
        mniHistorySidebar.setEnabled(false);
        menHistory.add(mniHistorySidebar);

        mnuBar.add(menHistory);

        menBookmarks.setMnemonic('L');
        menBookmarks.setText("Lesezeichen");

        mniAddBookmark.setText("Lesezeichen hinzufügen");
        mniAddBookmark.setEnabled(false);
        menBookmarks.add(mniAddBookmark);

        mniBookmarkManager.setText("Lesezeichen Manager");
        mniBookmarkManager.setEnabled(false);
        menBookmarks.add(mniBookmarkManager);

        mniBookmarkSidebar.setText("Lesezeichen in eigenem Fenster öffnen");
        mniBookmarkSidebar.setEnabled(false);
        menBookmarks.add(mniBookmarkSidebar);

        mnuBar.add(menBookmarks);

        menExtras.setMnemonic('E');
        menExtras.setText("Extras");

        mniOptions.setText("Optionen");
        mniOptions.setEnabled(false);
        mniOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniOptionsActionPerformed(evt);
            }
        });
        menExtras.add(mniOptions);

        jSeparator12.setEnabled(false);
        menExtras.add(jSeparator12);

        mniGotoPoint.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        mniGotoPoint.setText("Gehe zu ...");
        mniGotoPoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniGotoPointActionPerformed(evt);
            }
        });
        menExtras.add(mniGotoPoint);

        jSeparator13.setEnabled(false);
        menExtras.add(jSeparator13);

        mniScale.setText("Maßstab verändern");
        mniScale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniScaleActionPerformed(evt);
            }
        });
        menExtras.add(mniScale);

        mnuBar.add(menExtras);

        menWindow.setMnemonic('F');
        menWindow.setText("Fenster");

        jSeparator14.setEnabled(false);
        menWindow.add(jSeparator14);

        mniResetWindowLayout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        mniResetWindowLayout.setText("Fensteranordnung zurücksetzen");
        mniResetWindowLayout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniResetWindowLayoutActionPerformed(evt);
            }
        });
        menWindow.add(mniResetWindowLayout);

        mnuBar.add(menWindow);

        menHelp.setMnemonic('H');
        menHelp.setText("Hilfe");

        mniOnlineHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        mniOnlineHelp.setText("Online Hilfe");
        mniOnlineHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniOnlineHelpActionPerformed(evt);
            }
        });
        menHelp.add(mniOnlineHelp);

        mniNews.setText("News");
        mniNews.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniNewsActionPerformed(evt);
            }
        });
        menHelp.add(mniNews);

        mniVersions.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mniVersions.setText("Versionsinformationen");
        mniVersions.setEnabled(false);
        menHelp.add(mniVersions);

        mniLisences.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mniLisences.setText("Lizenzinformationen");
        mniLisences.setEnabled(false);
        menHelp.add(mniLisences);

        mniAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mniAbout.setText("Über LaGIS");
        mniAbout.setEnabled(false);
        menHelp.add(mniAbout);

        mnuBar.add(menHelp);

        setJMenuBar(mnuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void mniSaveLayoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSaveLayoutActionPerformed
    broker.getLayoutManager().saveLayout();
}//GEN-LAST:event_mniSaveLayoutActionPerformed

private void mniLoadLayoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniLoadLayoutActionPerformed
    broker.getLayoutManager().loadLayout();
}//GEN-LAST:event_mniLoadLayoutActionPerformed

private void mniLockLayoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniLockLayoutActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_mniLockLayoutActionPerformed

private void mniClippboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniClippboardActionPerformed
    Thread t = new Thread(new Runnable() {

        public void run() {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    clipboarder.setLocationRelativeTo(AbstractPlugin.this);
                    clipboarder.setVisible(true);
                }
            });
            ImageSelection imgSel = new ImageSelection(broker.getMappingComponent().getImage());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    clipboarder.dispose();
                }
            });

        }
    });
    t.start();
}//GEN-LAST:event_mniClippboardActionPerformed

private void mniCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCloseActionPerformed
    this.dispose();
}//GEN-LAST:event_mniCloseActionPerformed

//ToDo
private void mniRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniRefreshActionPerformed
//LagisBroker.getInstance().reloadFlurstueck();
}//GEN-LAST:event_mniRefreshActionPerformed

private void mniBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniBackActionPerformed
    if (broker.getMappingComponent() != null && broker.getMappingComponent().isBackPossible()) {
        broker.getMappingComponent().back(true);
    }
}//GEN-LAST:event_mniBackActionPerformed

private void mniForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniForwardActionPerformed
    if (broker.getMappingComponent() != null && broker.getMappingComponent().isForwardPossible()) {
        broker.getMappingComponent().forward(true);
    }
}//GEN-LAST:event_mniForwardActionPerformed

private void mniHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniHomeActionPerformed
    if (broker.getMappingComponent() != null) {
        broker.getMappingComponent().gotoInitialBoundingBox();
    }
}//GEN-LAST:event_mniHomeActionPerformed

private void mniOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniOptionsActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_mniOptionsActionPerformed

private void mniGotoPointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniGotoPointActionPerformed
    BoundingBox c = broker.getMappingComponent().getCurrentBoundingBox();
    double x = (c.getX1() + c.getX2()) / 2;
    double y = (c.getY1() + c.getY2()) / 2;
    String s = JOptionPane.showInputDialog(this, "Zentriere auf folgendem Punkt: x,y", StaticDecimalTools.round(x) + "," + StaticDecimalTools.round(y));
    try {
        String[] sa = s.split(",");
        Double gotoX = new Double(sa[0]);
        Double gotoY = new Double(sa[1]);
        BoundingBox bb = new BoundingBox(gotoX, gotoY, gotoX, gotoY);
        broker.getMappingComponent().gotoBoundingBox(bb, true, false, broker.getMappingComponent().getAnimationDuration());
    } catch (Exception skip) {
    }
}//GEN-LAST:event_mniGotoPointActionPerformed

private void mniScaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniScaleActionPerformed
    String s = JOptionPane.showInputDialog(this, "Maßstab_manuell_auswählen", ((int) broker.getMappingComponent().getScaleDenominator()) + "");
    try {
        Integer i = new Integer(s);
        broker.getMappingComponent().gotoBoundingBoxWithHistory(broker.getMappingComponent().getBoundingBoxFromScale(i));
    } catch (Exception skip) {
    }
}//GEN-LAST:event_mniScaleActionPerformed

private void mniResetWindowLayoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniResetWindowLayoutActionPerformed
    broker.getLayoutManager().doLayoutInfoNodeDefaultFile();
}//GEN-LAST:event_mniResetWindowLayoutActionPerformed

private void mniOnlineHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniOnlineHelpActionPerformed
    //openUrlInExternalBrowser(onlineHelpURL);
}//GEN-LAST:event_mniOnlineHelpActionPerformed

private void mniNewsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniNewsActionPerformed
    //openUrlInExternalBrowser(newsURL);
}//GEN-LAST:event_mniNewsActionPerformed

//ToDo outsource ?? and configurable
    private void openUrlInExternalBrowser(String url) {
        try {
            if (appletContext == null) {
                de.cismet.tools.BrowserLauncher.openURL(url);
            } else {
                java.net.URL u = new java.net.URL(url);
                appletContext.showDocument(u, "cismetBrowser");
            }
        } catch (Exception e) {
            log.warn(java.util.ResourceBundle.getBundle("de/cismet/cismap/commons/GuiBundle").getString("FeatureInfoDisplay.log.Fehler_beim_Oeffnen_von") + url + java.util.ResourceBundle.getBundle("de/cismet/cismap/commons/GuiBundle").getString("FeatureInfoDisplay.log.Neuer_Versuch"), e);
            //Nochmal zur Sicherheit mit dem BrowserLauncher probieren
            try {
                de.cismet.tools.BrowserLauncher.openURL(url);
            } catch (Exception e2) {
                log.warn(java.util.ResourceBundle.getBundle("de/cismet/cismap/commons/GuiBundle").getString("FeatureInfoDisplay.log.Auch_das_2te_Mal_ging_schief.Fehler_beim_Oeffnen_von") + url + java.util.ResourceBundle.getBundle("de/cismet/cismap/commons/GuiBundle").getString("FeatureInfoDisplay.log.Letzter_Versuch"), e2);
                try {
                    de.cismet.tools.BrowserLauncher.openURL("file://" + url);
                } catch (Exception e3) {
                    log.error(java.util.ResourceBundle.getBundle("de/cismet/cismap/commons/GuiBundle").getString("FeatureInfoDisplay.log.Auch_das_3te_Mal_ging_schief.Fehler_beim_Oeffnen_von:file://") + url, e3);
                }
            }
        }
    }
//    //ToDo remove
//    private static void initLog4J() {
//        try {
//            //System.out.println();
//            //PropertyConfigurator.configure(ClassLoader.getSystemResource("de/cismet/lagis/ressource/log4j/log4j.properties"));
//            PropertyConfigurator.configure(AbstractPlugin.class.getResource("/de/cismet/commons/architecture/configuration/log4j.properties"));
//            log.info("Log4J System erfolgreich konfiguriert");
//        } catch (Exception ex) {
//            System.err.println("Fehler bei Log4J Initialisierung");
//            ex.printStackTrace();
//        }
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JMenu menBookmarks;
    private javax.swing.JMenu menEdit;
    private javax.swing.JMenu menExtras;
    private javax.swing.JMenu menFile;
    private javax.swing.JMenu menHelp;
    private javax.swing.JMenu menHistory;
    private javax.swing.JMenu menWindow;
    private javax.swing.JMenuItem mniAbout;
    private javax.swing.JMenuItem mniAddBookmark;
    private javax.swing.JMenuItem mniBack;
    private javax.swing.JMenuItem mniBookmarkManager;
    private javax.swing.JMenuItem mniBookmarkSidebar;
    private javax.swing.JMenuItem mniClippboard;
    private javax.swing.JMenuItem mniClose;
    private javax.swing.JMenuItem mniForward;
    private javax.swing.JMenuItem mniGotoPoint;
    private javax.swing.JMenuItem mniHistorySidebar;
    private javax.swing.JMenuItem mniHome;
    private javax.swing.JMenuItem mniLisences;
    private javax.swing.JMenuItem mniLoadLayout;
    private javax.swing.JMenuItem mniLockLayout;
    private javax.swing.JMenuItem mniNews;
    private javax.swing.JMenuItem mniOnlineHelp;
    private javax.swing.JMenuItem mniOptions;
    private javax.swing.JMenuItem mniPrint;
    private javax.swing.JMenuItem mniRefresh;
    private javax.swing.JMenuItem mniResetWindowLayout;
    private javax.swing.JMenuItem mniSaveLayout;
    private javax.swing.JMenuItem mniScale;
    private javax.swing.JMenuItem mniVersions;
    private javax.swing.JMenuBar mnuBar;
    private javax.swing.JPanel panMain;
    private javax.swing.JSeparator sepAfterPos;
    private javax.swing.JSeparator sepBeforePos;
    // End of variables declaration//GEN-END:variables

    // best place ?? 
    class ImageSelection implements Transferable {

        private Image image;

        public ImageSelection(Image image) {
            this.image = image;
        }

        // Returns supported flavors
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        // Returns true if flavor is supported
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        // Returns image
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!DataFlavor.imageFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }
    }

    public AdvancedPluginBroker getBroker() {
        return broker;
    }

    public void setBroker(AdvancedPluginBroker broker) {
        this.broker = broker;
    }

    @Override
    public void dispose() {
        setVisible(false);
        log.info("Dispose(): Application gets shutted down");
        broker.getLayoutManager().saveUserLayout();
        //TODO
        //configurationManager.writeConfiguration();
        //CismapBroker.getInstance().writePropertyFile();
        //CismapBroker.getInstance().cleanUpSystemRegistry();
        super.dispose();
        System.exit(0);
    }
    //ToDo english

    protected void cleanUp() {
        if (broker.isInEditMode()) {
            try {
                log.debug("Application is in Editmode --> ask user if he wants to save");
                //ToDo make generic
                int answer = JOptionPane.showConfirmDialog(this, "Wollen Sie die gemachten Änderungen speichern", "Belis Änderungen", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    boolean isValid = broker.validateWidgets();
                    if (isValid) {
                        //TODO Progressbar
                        log.debug("All changes are valid" + isValid);
                        //ToDo!!! use Backgroundworker
                        broker.save();
                        log.debug("Änderungen wurden gespeichert");
                    } else {
                        log.warn("not all changes are valid --> can't save");
//ToDo wrong place --> there must be a check if before everything is shutdown because if there unvalid changes the user has not the possiblite to change them
                        //                        String reason = broker.getCurrentValidationErrorMessage();
//                        log.debug("Flurstueck kann nicht gespeichert werden, da nicht alle Komponenten valide sind. Grund:\n" + reason);
//                        answer = JOptionPane.showConfirmDialog(broker.getParentComponent(), "Änderungen können nur gespeichert werden, wenn alle Inhalte korrekt sind:\n\n" + reason + "\n\nBitte berichtigen Sie die Inhalte oder machen Sie die jeweiligen Änderungen rückgängig.\n\n Möchten Sie die Applikation beenden ohne zu speichern ? ", "Fehler", JOptionPane.YES_NO_OPTION);                        
//                        if (answer == JOptionPane.YES_OPTION) {
//                            log.debug("Beende ohne Änderungen zu speichern");
//                        } else{
//                            log.debug("Beende ohne Änderungen zu speichern");
//                        }
                        //JOptionPane.Inf(this, "Es traten Fehler beim abspeichern des Flurstuecks auf", "Fehler", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                //TODO saveCurrentFlurstueck wirft keine Exception, prüfen an welchen Stellen die Methode benutzt wird und sicherstellen das keine Probleme durch eine geworfene Exception auftreten
                log.debug("Es ist ein Fehler wärend dem abspeichern des Flurstuecks aufgetreten", ex);
                JOptionPane.showMessageDialog(this, "Es traten Fehler beim abspeichern des Flurstuecks auf", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
            while (broker.isInEditMode()) {

                try {
                    //TODO Progressbar & !!! Regeneriert sich nicht nach einem Server neustart
                    broker.releaseLock();
                    log.debug("Sperre konnte erfolgreich gelöst werden");
                    break;
                } catch (UnlockingNotSuccessfulException ex) {
                    Logger.getLogger(AbstractPlugin.class.getName()).log(Level.SEVERE, null, ex);
                    //ToDo make generic
                    int answer = JOptionPane.showConfirmDialog(this, "Sperre konnte nicht entfernt werden. Möchten Sie es erneut probieren?", "Belis Änderungen", JOptionPane.YES_NO_OPTION);
                    if (answer == JOptionPane.NO_OPTION) {
                        break;
                    }
                }
            }
        }
        configManager.writeConfiguration();
    }

    private boolean isPlugin() {
        return context != null;
    }

    private void setWindowSize() {
        if (windowSize != null && windowLocation != null) {
            this.setSize(windowSize);
            this.setLocation(windowLocation);
        } else {
            this.pack();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (isPlugin()) {
            log.debug("Plugin setVisible ignored: " + visible);
        } else {
            log.debug("No Plugin super.setVisible: " + visible);
            super.setVisible(visible);
        }
    }
}
