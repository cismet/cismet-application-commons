/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.commons.architecture.login;

import Sirius.navigator.connection.Connection;
import Sirius.navigator.connection.ConnectionFactory;
import Sirius.navigator.connection.ConnectionInfo;
import Sirius.navigator.connection.ConnectionSession;
import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.connection.proxy.ConnectionProxy;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.auth.DefaultUserNameStore;
import org.jdesktop.swingx.auth.LoginService;

import org.jdom.Element;

import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.rmi.Remote;

import java.util.HashMap;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

import de.cismet.commons.architecture.broker.AdvancedPluginBroker;
import de.cismet.commons.architecture.broker.BrokerLookup;
import de.cismet.commons.architecture.plugin.AbstractPlugin;

import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.NoWriteError;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */

//ToDo icon and image for login dialog
//ToDo class for registry
public class LoginManager implements Configurable {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger log = org.apache.log4j.Logger.getLogger(LoginManager.class);
    // TODO VERDIS COPY
    private static Image banner = new javax.swing.ImageIcon(LoginManager.class.getResource(
                "/de/cismet/belis/resource/icon/image/login.png")).getImage();

    //~ Instance fields --------------------------------------------------------

    private Image applicationIcon = null;
    private AdvancedPluginBroker broker;
    private AbstractPlugin plugin;

    // ToDo maybe changeable would be cool for different authentication methods!!!
    private LoginManager.WundaAuthentification wa = new LoginManager.WundaAuthentification();
    private String applicationClass = null;
    private boolean isLoginEnabled = true;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void handleLogin() {
        if (broker.isApplicationPlugin()) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Application is Navigator Plugin. Authentication is handled by navigator.");
                }
                final String userString = Sirius.navigator.connection.SessionManager.getSession().getUser().getName()
                            + "@"
                            + Sirius.navigator.connection.SessionManager.getSession()
                            .getUser()
                            .getUserGroup()
                            .getName();
                final String userGroup = Sirius.navigator.connection.SessionManager.getSession()
                            .getUser()
                            .getUserGroup()
                            .getName();
                final String standaloneDomain = Sirius.navigator.connection.SessionManager.getSession()
                            .getUser()
                            .getUserGroup()
                            .getDomain();
                if (log.isDebugEnabled()) {
                    log.debug("userstring: " + userString);
                    log.debug("userGroup: " + userGroup);
                }
                // setUserString(userString);
                broker.setAccountName(userString);
//                log.debug("full qualified username: " + userString + "@" + standaloneDomain);
//                configManager.setCurrentUser(userString + "@" + standaloneDomain);
//                configManager.configure(LagisApp.this);
                final Boolean permission = broker.getPermissions().get(userGroup.toLowerCase());
                if (log.isDebugEnabled()) {
                    log.debug("Permissions Hashmap: " + broker.getPermissions());
                }
                if (log.isDebugEnabled()) {
                    log.debug("Permission: " + permission);
                }
                if ((permission != null) && permission) {
                    if (log.isDebugEnabled()) {
                        log.debug("Authentication successfull user has granted readwrite access");
                    }
                    // TODO strange names
                    broker.setCoreReadOnlyMode(false);
                    broker.setFullReadOnlyMode(false);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Authentication successfull user has granted readonly access");
                    }
                }
                broker.showMainApplication();
                // TODOTest
                // pDMS.setAppletContext(context.getEnvironment().getAppletContext());
                // java.lang.Runtime.getRuntime().addShutdownHook(hook)
            } catch (Throwable t) {
                log.fatal("Error while setting applicaton according to plugin rights. Application will exit", t);
                System.exit(2);
            }
        } else if (isLoginEnabled) {
            if (log.isDebugEnabled()) {
                log.debug("Intialisiere Loginframe");
            }
            //
// Thread t=new Thread(new Runnable(){
// public void run() {
            // JOptionPane.showMessageDialog(null, "lala", "Fehler", JOptionPane.WARNING_MESSAGE);
            final DefaultUserNameStore usernames = new DefaultUserNameStore();
            // ToDo for every
            Preferences appPrefs = null;
            if (getApplicationClass() != null) {
                try {
                    appPrefs = Preferences.userNodeForPackage(Class.forName(getApplicationClass()));
                } catch (ClassNotFoundException ex) {
                    log.error("Problem with Apllication Class using default (LoginManager.class)", ex);
                    appPrefs = Preferences.userNodeForPackage(LoginManager.class);
                }
            } else {
                log.warn(
                    "Attention class name is not set. It is not possible to save properties right in the registry.\nUsing LoginManager class");
                appPrefs = Preferences.userNodeForPackage(LoginManager.class);
            }
            usernames.setPreferences(appPrefs.node("login"));

            final JXLoginPane login = new JXLoginPane(wa, null, usernames) {

                    @Override
                    protected Image createLoginBanner() {
                        return getBannerImage();
                    }
                };

            String u = null;
            try {
                u = usernames.getUserNames()[usernames.getUserNames().length - 1];
            } catch (Exception skip) {
            }
            if (u != null) {
                login.setUserName(u);
            }
            // final JXLoginPanel.JXLoginDialog d=new JXLoginPanel.JXLoginDialog(LagisApp.this,login);
            final JFrame dummy = null;
            final JXLoginPane.JXLoginFrame d = new JXLoginPane.JXLoginFrame(login);
            // final JXLoginPanel.JXLoginDialog d = new JXLoginPanel.JXLoginDialog(dummy,login);

            d.addComponentListener(new ComponentAdapter() {

                    @Override
                    public void componentHidden(final ComponentEvent e) {
                        handleLoginStatus(d.getStatus(), usernames, login);
                    }
                });
            d.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosed(final WindowEvent e) {
                        handleLoginStatus(d.getStatus(), usernames, login);
                    }
                });
            d.setIconImage(applicationIcon);
//                    SwingUtilities.invokeLater(new Runnable() {

            login.setPassword("".toCharArray());
            // d.setLocationRelativeTo(LagisApp.this);
            try {
                ((JXPanel)((JXPanel)login.getComponent(1)).getComponent(1)).getComponent(3).requestFocus();
            } catch (Exception skip) {
            }
            d.setIconImage(applicationIcon);
            d.setAlwaysOnTop(true);
            // final JFrame frame = JXLoginPane.showLoginFrame(login);
// JDialog dialog = new JDialog((JFrame)null, "Fehler beim speichern...", true);
// //dialog.setIconImage(((ImageIcon) BelisIcons.icoError16).getImage());
// dialog.add(d);
// dialog.pack();
// //dialog.setLocationRelativeTo(getParentComponent());
// dialog.setVisible(true);

            // login.showLoginDialog(null,login);
            d.setVisible(true);
//        final Thread loginThread = new Thread(new Runnable() {
//
//            public void run() {
//                while (d.isVisible()) {
//                    try {
//                        Thread.currentThread().sleep(100);
//                    } catch (InterruptedException ex) {
//                        log.fatal("Thread wurde interrupted", ex);
//                    }
//                }
//            }
//        });

//        try {
////            EventQueue.
////            d.setVisible(true);
////            loginThread.start();
////            loginThread.join();
//        } catch (InterruptedException ex) {
//            log.fatal("Fehler !", ex);
//        } catch (InvocationTargetException ex){
//            log.fatal("Fehler !", ex);
//        }
            // while(true);
//                        }
            // });

//     }
//    });
//    t.setPriority(Thread.NORM_PRIORITY);
//    t.start();
        } else {
            log.info("Login is disabled. Attention writing is possible.");
            // ToDo maybe should be also configurable
            broker.setCoreReadOnlyMode(false);
            broker.setFullReadOnlyMode(false);
            broker.showMainApplication();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Image getBannerImage() {
        return banner;
    }
    // TODO VERDIS COPY
    // obsolete because for failed logins --> only for saving the username

    /**
     * DOCUMENT ME!
     *
     * @param  status     DOCUMENT ME!
     * @param  usernames  DOCUMENT ME!
     * @param  login      DOCUMENT ME!
     */
    private void handleLoginStatus(final JXLoginPane.Status status,
            final DefaultUserNameStore usernames,
            final JXLoginPane login) {
        if (status == JXLoginPane.Status.SUCCEEDED) {
            // Damit wird sichergestellt, dass dieser als erstes vorgeschlagen wird
            usernames.removeUserName(login.getUserName());
            usernames.saveUserNames();
            usernames.addUserName((login.getUserName()));
            usernames.saveUserNames();
            if (log.isDebugEnabled()) {
                // Added for RM Plugin functionalty 22.07.2007 Sebastian Puhl
                log.debug("Login erfolgreich");
            }
            // broker.getParentFrame().setVisible(true);
            broker.showMainApplication();
            // ToDo start application
            // new LagisApp();
            // loginWasSuccessful = true;
        } else {
            // Should never gets executed
            log.warn("Login fehlgeschlagen");
            System.exit(0);
        }
    }

    @Override
    public void configure(final Element parent) {
    }

    @Override
    public Element getConfiguration() throws NoWriteError {
        return null;
    }

    @Override
    public void masterConfigure(final Element parent) {
        try {
            final String isLoginEnabled = parent.getChild("Login").getChildText("IsLoginEnabled");
            if (isLoginEnabled.equals("false")) {
                this.isLoginEnabled = false;
            }
        } catch (Exception ex) {
            log.error("Error while checking if login is enabled. Setting to default: " + isLoginEnabled, ex);
        }
        try {
            broker = BrokerLookup.getInstance()
                        .getBrokerForName(parent.getChild("BrokerConfiguration").getChild("Broker").getChildText(
                                    "BrokerName"));
        } catch (Exception ex) {
            log.warn("Error while retrieving broker instance", ex);
            shutDownApplication();
        }
        try {
            final Element userPermissions = parent.getChild("Login").getChild("Permissions");
            final HashMap<String, Boolean> permissions = new HashMap<String, Boolean>();
            final List<Element> xmlPermissions = userPermissions.getChildren();
            for (final Element currentPermission : xmlPermissions) {
                try {
                    final String isReadWriteAllowedString = currentPermission.getChildText("ReadWrite");
                    boolean isReadWriteAllowed = false;
                    if (isReadWriteAllowedString != null) {
                        if (isReadWriteAllowedString.equals("true")) {
                            isReadWriteAllowed = true;
                        }
                    }
                    final String userGroup = currentPermission.getChildText("UserGroup");
                    final String userDomain = currentPermission.getChildText("UserDomain");
                    final String permissionString = userGroup + "@" + userDomain;
                    log.info("Permissions für: login=*@" + permissionString + " readWriteAllowed=" + isReadWriteAllowed
                                + "(boolean)/" + isReadWriteAllowedString + "(String)");
                    if (permissionString != null) {
                        permissions.put(permissionString.toLowerCase(), isReadWriteAllowed);
                    }
                } catch (Exception ex) {
                    log.warn("Error while reading user right can't authenticate", ex);
                }
            }
            broker.setPermissions(permissions);
        } catch (Exception ex) {
            log.fatal("Error while reading userrights can't authenticate (Permissions)", ex);
            broker.setPermissions(new HashMap<String, Boolean>());
            // TODO wenigstens den Nutzer benachrichtigen sonst ist es zu hard oder nur lesen modus --> besser!!!
            System.exit(2);
        }
        try {
            broker.getConfigManager().addConfigurable(wa);
            broker.getConfigManager().configure(wa);
        } catch (Exception ex) {
            log.error("Error while configuring authentication method", ex);
            shutDownApplication();
        }
        try {
            this.setApplicationClass(parent.getChild("Configuration").getChildText("ApplicationClass"));
        } catch (Exception ex) {
            log.warn("Error while setting application class", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  applicationClass  DOCUMENT ME!
     */
    private void setApplicationClass(final String applicationClass) {
        this.applicationClass = applicationClass;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getApplicationClass() {
        return applicationClass;
    }

    /**
     * DOCUMENT ME!
     */
    private void shutDownApplication() {
        if (log.isDebugEnabled()) {
            log.debug("Shutting down Application");
        }
        System.exit(2);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class WundaAuthentification extends LoginService implements Configurable {

        //~ Static fields/initializers -----------------------------------------

        // TODO steht auch so in VERDIS schlecht für ÄNDERUNGEN !!!!!
        public static final String CONNECTION_CLASS = "Sirius.navigator.connection.RMIConnection";
        public static final String CONNECTION_PROXY_CLASS =
            "Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler";
        // private String standaloneDomain;
        private static String standaloneDomain;

        //~ Instance fields ----------------------------------------------------

        private final Logger log = org.apache.log4j.Logger.getLogger(WundaAuthentification.class);
        private String callserverhost;
        private String userString;
        private AdvancedPluginBroker broker;
        // private String userDependingConfigurationFile;
        // private UserDependingConfigurationManager configManager;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new WundaAuthentification object.
         */
        public WundaAuthentification() {
            try {
//                configManager = new UserDependingConfigurationManager();
//                log.info("Laden der Lagis Konfiguration");
//                log.debug("Name des Lagis Server Konfigurationsfiles: " + LAGIS_CONFIGURATION_FILE);
//                configManager.setDefaultFileName(LAGIS_CONFIGURATION_FILE);
//                configManager.setFileName(LOCAL_LAGIS_CONFIGURATION_FILE);
//
//                //            if (!plugin) {
//                //                configManager.setFileName("configuration.xml");
//                //
//                //            } else {
//                //                configManager.setFileName("configurationPlugin.xml");
//                //                configManager.addConfigurable(metaSearch);
//                //            }
//                configManager.setClassPathFolder(LAGIS_CONFIGURATION_CLASSPATH);
//                configManager.setFolder(LAGIS_LOCAL_CONFIGURATION_FOLDER);
//                configManager.addConfigurable(this);
//                configManager.addConfigurable(LagisBroker.getInstance());
//                configManager.configure(this);
//                configManager.configure(LagisBroker.getInstance());
            } catch (Exception ex) {
                log.fatal("Fehler bei der Konfiguration des ConfigurationManagers (LoginFrame)", ex);
            }
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean authenticate(final String name, final char[] password, final String server) throws Exception {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Authentication for :" + name);
                }

                System.setProperty("sun.rmi.transport.connectionTimeout", "15");
                final String user = name.split("@")[0];
                final String group = name.split("@")[1];

                broker.setAccountName(name);
                final String callServerURL = "rmi://" + callserverhost + "/callServer";
                if (log.isDebugEnabled()) {
                    log.debug("callServerUrl:" + callServerURL);
                }
                final String domain = standaloneDomain;
                userString = name;
                if (log.isDebugEnabled()) {
                    log.debug("full qualified username: " + userString + "@" + standaloneDomain);
                }
                final Remote r = null;

                final Connection connection = ConnectionFactory.getFactory()
                            .createConnection(CONNECTION_CLASS, callServerURL);
                ConnectionSession session = null;
                ConnectionProxy proxy = null;
                final ConnectionInfo connectionInfo = new ConnectionInfo();
                connectionInfo.setCallserverURL(callServerURL);
                connectionInfo.setPassword(new String(password));
                connectionInfo.setUserDomain(domain);
                connectionInfo.setUsergroup(group);
                connectionInfo.setUsergroupDomain(domain);
                connectionInfo.setUsername(user);

                session = ConnectionFactory.getFactory().createSession(connection, connectionInfo, true);
                proxy = ConnectionFactory.getFactory().createProxy(CONNECTION_PROXY_CLASS, session);
                // proxy = ConnectionFactory.getFactory().createProxy(CONNECTION_CLASS,CONNECTION_PROXY_CLASS,
                // connectionInfo,false);
                SessionManager.init(proxy);
                final String tester = (group + "@" + domain).toLowerCase();
                if (log.isDebugEnabled()) {
                    log.debug("authentication: tester = " + tester);
                    log.debug("authentication: name = " + name);
                    log.debug("authentication: RM Plugin key = " + name + "@" + domain);
                    // setUserString(name); TODO update Configuration depending on username --> formaly after the
                    // handlelogin method --> test if its work!!!!
                }

//                configManager.setCurrentUser(userString + "@" + standaloneDomain);
//                //zweimal wegen userdepending konfiguration
//                configManager.configure(this);
                final Boolean permission = broker.getPermissions().get(tester);
                if (log.isDebugEnabled()) {
                    log.debug("Permissions Hashmap: " + broker.getPermissions());
                }
                if (log.isDebugEnabled()) {
                    log.debug("Permission: " + permission);
                }
                if ((permission != null) && permission) {
                    if (log.isDebugEnabled()) {
                        log.debug("Authentication successfull user has granted readwrite access");
                    }
                    broker.setCoreReadOnlyMode(false);
                    broker.setFullReadOnlyMode(false);
                    // loginWasSuccessful = true;
                    return true;
                } else if (permission != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Authentication successfull user has granted readonly access");
                    }
                    // loginWasSuccessful = true;
                    return true;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("authentication else false: no permission available");
                    }
                    // loginWasSuccessful = false;
                    return false;
                }
//                if (prefs.getRwGroups().contains(tester)) {
//                    //Main.this.readonly=false;
//                    setUserString(name);
//                    //log.debug("RMPlugin: wird initialisiert (VerdisStandalone)");
//                    //log.debug("RMPlugin: Mainframe "+Main.this);
//                    //log.debug("RMPlugin: PrimaryPort "+prefs.getPrimaryPort());
//                    //log.debug("RMPlugin: SecondaryPort "+prefs.getSecondaryPort());
//                    //log.debug("RMPlugin: Username "+(name+"@"+prefs.getStandaloneDomainname()));
//                    //log.debug("RMPlugin: RegistryPath "+prefs.getRmRegistryServerPath());
//                    //rmPlugin = new RMPlugin(Main.this,prefs.getPrimaryPort(),prefs.getSecondaryPort(),prefs.getRmRegistryServerPath(),name+"@"+prefs.getStandaloneDomainname());
//                    //log.debug("RMPlugin: erfolgreich initialisiert (VerdisStandalone)");
//                    return true;
//                } else if (prefs.getUsergroups().contains(tester)) {
//                    //Main.this.readonly=true;
//                    setUserString(name);
//                    //rmPlugin = new RMPlugin(Main.this,prefs.getPrimaryPort(),prefs.getSecondaryPort(),prefs.getRmRegistryServerPath(),name+"@"+prefs.getStandaloneDomainname());
//                    return true;
//                } else {
//                    log.debug("authentication else false");
//                    return false;
//                }
            } catch (Throwable t) {
                log.error("Fehler beim Anmelden", t);
                return false;
            }
        }

        @Override
        public void configure(final Element parent) {
        }

        @Override
        public Element getConfiguration() throws NoWriteError {
            return null;
        }

        @Override
        public void masterConfigure(final Element parent) {
            try {
                final Element login = parent.getChild("Login").getChild("Standalone");
                // Element userDep = parent.getChild("userDependingConfigurationProperties");
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("Userdomain: " + login.getAttribute("userdomainname").getValue());
                    }
                    standaloneDomain = login.getAttribute("userdomainname").getValue();
                } catch (Exception ex) {
                    log.fatal("Error while reading userdomain can't authenticate", ex);
                    System.exit(2);
                    // TODO wenigstens den Nutzer benachrichtigen sonst ist es zu hard oder nur lesen modus -->
                    // besser!!!
                }
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("Callserverhost: " + login.getAttribute("callserverhost").getValue());
                    }
                    callserverhost = login.getAttribute("callserverhost").getValue();
                } catch (Exception ex) {
                    log.fatal("Error while reading callserverhost can't authenticate", ex);
                    System.exit(2);
                    // TODO wenigstens den Nutzer benachrichtigen sonst ist es zu hard oder nur lesen modus -->
                    // besser!!!
                }
//            try {
//                userDependingConfigurationFile = userDep.getChildText("file");
//                userDependingConfigurationClasspathfolder = userDep.getChildText("classpathfolder");
//                log.debug("UserDependingConfiguration: file=" + userDependingConfigurationFile + " classpathfolder=" + userDependingConfigurationClasspathfolder);
//                configManager.setUserDependingConfigurationClasspath(userDependingConfigurationClasspathfolder);
//                configManager.setUserDependingConfigurationFile(userDependingConfigurationFile);
//            } catch (Exception ex) {
//                log.warn("Fehler beim lesen des userconfigurationfiles", ex);
//            }
                try {
                    broker = BrokerLookup.getInstance()
                                .getBrokerForName(parent.getChild("BrokerConfiguration").getChild("Broker")
                                        .getChildText("BrokerName"));
                } catch (Exception ex) {
                    log.fatal("Error while retrieving broker instance can't autenticate", ex);
                    System.exit(2);
                    // TODO wenigstens den Nutzer benachrichtigen sonst ist es zu hard oder nur lesen modus -->
                    // besser!!!
                }
            } catch (Exception ex) {
                log.fatal("Error while configuring LoginManager", ex);
                System.exit(2);
            }
        }
    }
}
