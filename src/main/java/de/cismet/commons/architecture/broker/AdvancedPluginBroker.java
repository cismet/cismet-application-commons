/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.commons.architecture.broker;

import Sirius.navigator.plugin.context.PluginContext;
import de.cismet.cismap.commons.gui.statusbar.StatusBar;
import de.cismet.commons.architecture.interfaces.Widget;
import de.cismet.commons.architecture.layout.LayoutManager;
import de.cismet.commons.architecture.login.LoginManager;
import de.cismet.commons.server.entity.GeoBaseEntity;
import de.cismet.tools.configuration.ConfigurationManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import net.infonode.docking.RootWindow;
import net.infonode.gui.componentpainter.GradientComponentPainter;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;
import org.jdom.Element;

//Layout,Configuration,Toolbar,Decoration
/**
 *
 * @author spuhl
 */
//ToDo write Interface and replace all usage of the implementation with the interfaces same with BasicBroker
// Statusbar
public class AdvancedPluginBroker extends BasicPluginBroker implements AdvancedPluginBrokerInt {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AdvancedPluginBroker.class);
    private final LayoutManager layoutManager = new LayoutManager();
    private final LoginManager loginManager = new LoginManager();
    private ConfigurationManager configManager;
    private JToolBar toolbar;
    private PluginContext context;
    private String applicationName;
    private StatusBar statusbar;
    private boolean statusBarEnabled=true;

    protected AdvancedPluginBroker() {
        super();
        System.out.println("Constructor: " + AdvancedPluginBroker.class.getName());
        initToolbar();
    }
    private String account;

    public String getAccountName() {
        if (account == null) {
            log.fatal("Benutzername unvollständig: " + account);
        }
        return account;
    }

    public void setAccountName(String aAccount) {
        account = aAccount;
    }

    @Override
    public void masterConfigure(Element parent) {
        try{
        super.masterConfigure(parent);
        System.out.println("masterConfigure: " + AdvancedPluginBroker.class.getName());        
        configManager.addConfigurable(layoutManager);
        configManager.addConfigurable(loginManager);                
        configManager.configure(layoutManager);
        configManager.configure(loginManager);
        try {
            loginManager.handleLogin();
        } catch (Exception ex) {
            log.error("Fehler beim Loginframe", ex);
            System.exit(2);
        }
        for (Widget widget : getWidgets()) {
            configManager.addConfigurable(widget);
            configManager.configure(widget);
        }
        customizeApplication();
        }catch(Exception ex){
            log.error("Error while master configure: ",ex);
        }
    }

    public ConfigurationManager getConfigManager() {
        return configManager;
    }

    public void setApplicatioName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setConfigManager(ConfigurationManager configManager) {
        this.configManager = configManager;
    }

    public LayoutManager getLayoutManager() {
        return layoutManager;
    }

    //if there is more need --> Listener
    public void pluginConstructionDone() {
        try {

            final Runnable layoutRun = new Runnable() {

                public void run() {
                    layoutManager.configureInfoNodeDocking();
                    layoutManager.doLayoutInfoNodeDefaultFile();
                }
            };
            if (EventQueue.isDispatchThread()) {
                layoutRun.run();
            } else {
                EventQueue.invokeLater(layoutRun);
            }
        } catch (Exception ex) {
            log.error("Error while setting layout: ", ex);
        }
    //getMappingComponent().unlock();
    }

    public RootWindow getRootWindow() {
        return layoutManager.getRootWindow();
    }

    public void setContext(PluginContext context) {
        this.context = context;
    }

    public PluginContext getContext() {
        return context;
    }

    public boolean isApplicationPlugin() {
        return context != null;
    }

    public void setTitleBarComponentpainter(Color color) {
        getRootWindow().getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new GradientComponentPainter(color, new Color(236, 233, 216), color, new Color(236, 233, 216)));
    }

    public void setTitleBarComponentpainter(Color left, Color right) {
        getRootWindow().getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new GradientComponentPainter(left, right, left, right));
    }    //ToDo outsource in Toolbarmanager
    protected JButton btnSwitchInEditmode = new javax.swing.JButton();
    protected JButton btnDiscardChanges = new javax.swing.JButton();
    protected JButton btnAcceptChanges = new javax.swing.JButton();
    protected JButton cmdPrint = new javax.swing.JButton();
    protected JButton btnReload = new javax.swing.JButton();

    public JButton getBtnAcceptChanges() {
        return btnAcceptChanges;
    }

    public void addEditControl(JButton editControl) {
        if (editControl != null) {
            editControls.add(editControl);
        }
    }

    public void removeEditControl(JButton editControl) {
        if (editControl != null) {
            editControls.remove(editControl);
        }
    }

    public void setBtnAcceptChanges(JButton btnAcceptChanges) {
        this.btnAcceptChanges = btnAcceptChanges;
    }

    public JButton getBtnDiscardChanges() {
        return btnDiscardChanges;
    }

    public void setBtnDiscardChanges(JButton btnDiscardChanges) {
        this.btnDiscardChanges = btnDiscardChanges;
    }

    public JButton getBtnReloadFlurstueck() {
        return btnReload;
    }

    public void setBtnReloadFlurstueck(JButton btnReloadFlurstueck) {
        this.btnReload = btnReloadFlurstueck;
    }

    public JButton getBtnSwitchInEditmode() {
        return btnSwitchInEditmode;
    }

    public void setBtnSwitchInEditmode(JButton btnSwitchInEditmode) {
        this.btnSwitchInEditmode = btnSwitchInEditmode;
    }

    public JButton getCmdPrint() {
        return cmdPrint;
    }

    public void setCmdPrint(JButton cmdPrint) {
        this.cmdPrint = cmdPrint;
    }
    //Todo outsource in panel, the advantage is that it is easier to edit --> you can use the gui builder
    //And don't know if it is good to have fix item in the toolbar

    private void initToolbar() {
        try {
            java.awt.GridBagConstraints gridBagConstraints;
            toolbar = new javax.swing.JToolBar();

            JPanel editPan = new JPanel(new GridBagLayout());
            getToolbar().setRollover(true);
            getToolbar().setMinimumSize(new java.awt.Dimension(496, 33));
            final ArrayList<JButton> editButtons = getEditButtons();            
            for (int i = 0; i < editButtons.size(); i++) {
                JButton curButton = editButtons.get(i);
                gridBagConstraints = new java.awt.GridBagConstraints();
                if (!(i == 0)) {
                    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
                } else {
                    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
                }
                editPan.add(curButton, gridBagConstraints);
                //getToolbar().add(curButton);
            }
            editPan.setOpaque(false);
            getToolbar().add(editPan);
            addSeparatorToToolbar();

            cmdPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/commons/architecture/resource/icon/toolbar/frameprint.png"))); // NOI18N

            cmdPrint.setToolTipText("Drucken");
            cmdPrint.setBorderPainted(false);
            cmdPrint.setFocusable(false);
            cmdPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            cmdPrint.setPreferredSize(new java.awt.Dimension(23, 23));
            cmdPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            cmdPrint.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    String oldMode = getMappingComponent().getInteractionMode();
                    log.debug("oldInteractionMode:" + oldMode);
                    //Enumeration en = cmdGroupPrimaryInteractionMode.getElements();
                    //togInvisible.setSelected(true);
                    getMappingComponent().showPrintingSettingsDialog(oldMode);
                }
            });
            getToolbar().add(cmdPrint);

//        btnReload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/commons/architecture/resource/icon/toolbar/reload.gif"))); // NOI18N
//
//        btnReload.setToolTipText("Flurstück neu laden");
//        btnReload.setBorderPainted(false);
//        btnReload.setFocusable(false);
//        btnReload.setPreferredSize(new java.awt.Dimension(23, 23));
//        btnReload.addActionListener(new java.awt.event.ActionListener() {
//
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                //btnReloadFlurstueckActionPerformed(evt);
//            }
//        });
//        getToolbar().add(btnReload);
            addSeparatorToToolbar();
        } catch (Exception ex) {
            System.out.println("Exception while initializing toolbar");
            ex.printStackTrace();
            log.error("Exception while initializing toolbar.", ex);
        }
    }

    protected ArrayList<JButton> getEditButtons() {        
        final ArrayList<JButton> editButtons = new ArrayList<JButton>();
        btnSwitchInEditmode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/commons/architecture/resource/icon/toolbar/editmode.png"))); // NOI18N
        //btnSwitchInEditmode.setEnabled(false);
        btnSwitchInEditmode.setToolTipText("Editormodus");
        btnSwitchInEditmode.setBorderPainted(false);
        btnSwitchInEditmode.setFocusable(false);
        btnSwitchInEditmode.setPreferredSize(new java.awt.Dimension(23, 23));
        btnSwitchInEditmode.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                log.debug("Versuche in Editiermodus zu wechseln: ");
                try {
                    switchEditMode();
                    for (JButton curControl : editControls) {
                        curControl.setEnabled(false);
                    }
                    btnAcceptChanges.setEnabled(true);
                    btnDiscardChanges.setEnabled(true);
                    setTitleBarComponentpainter(EDIT_MODE_COLOR);//

                } catch (Exception ex) {
                    log.debug("Fehler beim anlegen der Sperre", ex);
                }


                getMappingComponent().setReadOnly(false);

                log.debug("ist im Editiermodus: " + isInEditMode());
            }
        });
        editControls.add(btnSwitchInEditmode);

        editButtons.add(btnSwitchInEditmode);
        btnDiscardChanges.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/commons/architecture/resource/icon/toolbar/cancel.png"))); // NOI18N

        btnDiscardChanges.setToolTipText("Änderungen Abbrechen");
        btnDiscardChanges.setBorderPainted(false);
        btnDiscardChanges.setFocusable(false);
        btnDiscardChanges.setEnabled(false);
        btnDiscardChanges.setPreferredSize(new java.awt.Dimension(23, 23));
        btnDiscardChanges.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (isInEditMode()) {
                    log.debug("Versuche aus Editiermodus heraus zu wechseln: ");
                     //ToDo make generic
                    int answer = JOptionPane.showConfirmDialog(getParentComponent(), "Wollen Sie die gemachten Änderungen verwerfen?", "Belis Änderungen", JOptionPane.YES_NO_OPTION);
                    if (answer == JOptionPane.NO_OPTION) {
                        return;
                    }

                    try {
                        fireCancelStarted();
                        execute(new SaveCancelWorker(SaveCancelWorker.CANCEL_MODE));
                    //((DefaultFeatureCollection)LagisBroker.getInstance().getMappingComponent().getFeatureCollection()).setAllFeaturesEditable(false);
                    //TODO TEST IT!!!!
                    //TODO EDT
                    } catch (Exception ex) {
                        log.debug("Fehler beim lösen der Sperre", ex);

                    }

                    //btnOpenWizard.setEnabled(true);
                    //LagisBroker.getInstance().reloadFlurstueck();
                    log.debug("ist im Editiermodus: " + isInEditMode());
                }
            }
        });
        editButtons.add(btnDiscardChanges);
        btnAcceptChanges.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/commons/architecture/resource/icon/toolbar/ok.png"))); // NOI18N

        btnAcceptChanges.setToolTipText("Änderungen annehmen");
        btnAcceptChanges.setBorderPainted(false);
        btnAcceptChanges.setFocusable(false);
        btnAcceptChanges.setEnabled(false);
        btnAcceptChanges.setPreferredSize(new java.awt.Dimension(23, 23));
        btnAcceptChanges.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    if (isInEditMode()) {
                        log.debug("Versuche aus Editiermodus heraus zu wechseln: ");
                        boolean isValid = validateWidgets();
                        if (isValid) {
                            log.debug("Alle Änderungen sind valide: " + isValid);
                            //ToDo make generic
                            int answer = JOptionPane.showConfirmDialog(getParentComponent(), "Wollen Sie die gemachten Änderungen speichern?", "Belis Änderungen", JOptionPane.YES_NO_OPTION);
                            if (answer == JOptionPane.YES_OPTION) {
                                //LagisBroker.getInstance().saveCurrentFlurstueck();
                                fireSaveStarted();
                                execute(new SaveCancelWorker(SaveCancelWorker.SAVE_MODE));
                            } else {
                                return;
                            }
                        } else {
                            String reason = getCurrentValidationErrorMessage();
                            log.debug("Es kann nicht gespeichert werden, da nicht alle Komponenten valide sind. Grund:\n" + reason);
                            JOptionPane.showMessageDialog(getParentComponent(), "Änderungen können nur gespeichert werden, wenn alle Inhalte korrekt sind:\n\n" + reason + "\n\nBitte berichtigen Sie die Inhalte oder machen Sie die jeweiligen Änderungen rückgängig.", "Fehler", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    log.debug("ist im Editiermodus: " + isInEditMode());
                } catch (Exception ex) {
                    log.error("Fehler beim akzeptieren von Änderungen: ", ex);
                    showSaveErrorDialog();
                }
            }
        });
        editButtons.add(btnAcceptChanges);
        return editButtons;
    }

    protected void fireSaveStarted() {
    }

    protected void fireSaveFinished() {
    }

    protected void fireCancelStarted() {
    }

    protected void fireCancelFinished() {
    }

    class SaveCancelWorker extends SwingWorker<Runnable, Void> {

        public final static int SAVE_MODE = 0;
        public final static int CANCEL_MODE = 1;
        private int mode;

        SaveCancelWorker(int mode) {
            this.mode = mode;
        }

        protected Runnable doInBackground() throws Exception {
            if (mode == SAVE_MODE) {
                return save();
            } else if (mode == CANCEL_MODE) {
                return AdvancedPluginBroker.this.cancel();
            } else {
                log.warn("Mode is unkown.");
                return null;
            }

        }

        protected void done() {
            if (isCancelled()) {
                log.warn("SaveUpdateWorker is canceled --> nothing to do in method done()");
                return;
            }
            try {
                log.debug("done");
                Runnable resultRun = get();
                if (resultRun != null) {
                    log.debug("result runnable != null will be executed");
                    resultRun.run();
                } else {
                    log.debug("result runnable == null. Can't execute");
                }
                getMappingComponent().setReadOnly(true);
                switchEditMode();
                log.debug("enable buttons");
                if (!isFullReadOnlyMode()) {
                    for (JButton curControl : editControls) {
                        curControl.setEnabled(true);
                    }
                }
                btnAcceptChanges.setEnabled(false);
                btnDiscardChanges.setEnabled(false);
                setTitleBarComponentpainter(DEFAULT_MODE_COLOR);
                if (mode == SAVE_MODE) {
                    fireSaveFinished();
                } else if (mode == CANCEL_MODE) {
                    fireCancelFinished();

                }
            } catch (Exception ex) {
                log.error("Failure during saving/refresh results", ex);
                if (mode == SAVE_MODE) {
                    saveFailed();
                    fireSaveFinished();
                } else if (mode == CANCEL_MODE) {
                    cancelFailed();
                    fireCancelFinished();
                }
                return;
            }
        }
    }

    public JToolBar getToolbar() {
        return toolbar;
    }

    public void addSeparatorToToolbar() {
        getToolbar().add(createToolBarSeperator());
    }

    public JSeparator createToolBarSeperator() {
        final JSeparator tmpSeperator = new JSeparator();
        tmpSeperator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        tmpSeperator.setMaximumSize(new java.awt.Dimension(2, 32767));
        tmpSeperator.setMinimumSize(new java.awt.Dimension(2, 25));
        tmpSeperator.setPreferredSize(new java.awt.Dimension(2, 23));
        return tmpSeperator;
    }

    //ToDo!!! use Backgroundworker
    public Runnable save() throws Exception {
        return null;
    }

    protected void saveFailed() {
        showSaveErrorDialog();
    }

    protected Runnable cancel() throws Exception {
        return null;
    }

    protected void cancelFailed() {
        //Todo cancel failed
    }

    public JXTreeTable decorateWithAlternateHighlighting(final JXTreeTable ttable) {
        ttable.addHighlighter(ALTERNATE_ROW_HIGHLIGHTER);
        return ttable;
    }

    public JXTreeTable decorateWithNoGeometryHighlighter(final JXTreeTable ttable) {

        final HighlightPredicate noGeometryPredicate = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer, ComponentAdapter componentAdapter) {
                //int displayedIndex = componentAdapter.row;
                //nt modelIndex = ((JXTreeTable) ttable).getFilters().convertRowIndexToModel(displayedIndex);                 
                try {
                    Object userObj = ((AbstractMutableTreeTableNode) ttable.getPathForRow(componentAdapter.row).getLastPathComponent()).getUserObject();
                    if (userObj != null && userObj instanceof GeoBaseEntity) {
                        return ((GeoBaseEntity) userObj).getGeometry() == null;
                    }
                } catch (Exception ex) {
                    log.error("Exception in Highlighter: ", ex);
                }
                return false;
            //ReBe r = model.get//tableModel.getReBeAtRow(modelIndex);
            //return r != null && r.getGeometry() == null;
            }
        };
        final Highlighter noGeometryHighlighter = new ColorHighlighter(noGeometryPredicate, this.gray, null);
        //((JXTable) tReBe).setHighlighters(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER,noGeometryHighlighter);
        ttable.addHighlighter(noGeometryHighlighter);
        return ttable;
    }

    //ToDo is overwritten in the client should be changed !!!
    protected void showSaveErrorDialog() {
    }

    private void customizeApplication() {
        customizeApplicationToolbar();
    }

    public void customizeApplicationToolbar() {
    }

    public StatusBar getStatusbar() {
        return statusbar;
    }

    public void setStatusbar(StatusBar statusbar) {
        this.statusbar = statusbar;
    }

    public boolean isStatusBarEnabled() {
        return statusBarEnabled;
    }

    public void setStatusBarEnabled(boolean isStatusBarEnabled) {
        this.statusBarEnabled = isStatusBarEnabled;
    }

}
