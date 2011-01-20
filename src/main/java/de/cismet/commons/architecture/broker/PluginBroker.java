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
package de.cismet.commons.architecture.broker;

import com.vividsolutions.jts.geom.Geometry;

import org.jdom.Element;

import java.awt.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.SwingWorker;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.statusbar.StatusBar;

import de.cismet.commons.architecture.geometrySlot.GeometrySlotInformation;
import de.cismet.commons.architecture.interfaces.ChangeObserver;
import de.cismet.commons.architecture.interfaces.Refreshable;
import de.cismet.commons.architecture.interfaces.Widget;

import de.cismet.tools.configuration.Configurable;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public interface PluginBroker extends ChangeObserver, Configurable {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   date  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getDateWithoutTime(Date date);

    /**
     * DOCUMENT ME!
     */
    void warnIfThreadIsEDT();

    /**
     * DOCUMENT ME!
     */
    void warnIfThreadIsNotEDT();

    /**
     * DOCUMENT ME!
     *
     * @param  widget  DOCUMENT ME!
     */
    void addWidget(Widget widget);

    /**
     * DOCUMENT ME!
     *
     * @param  widgets  DOCUMENT ME!
     */
    void addWidgets(Vector widgets);

    /**
     * DOCUMENT ME!
     *
     * @param   geom  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    GeometrySlotInformation assignGeometry(Geometry geom);
    @Override
    void configure(Element parent);

    /**
     * DOCUMENT ME!
     *
     * @param  workerThread  DOCUMENT ME!
     */
    void execute(SwingWorker workerThread);

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    void fireChangeEvent(Object event);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getCurrentValidationErrorMessage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    MappingComponent getMappingComponent();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    JComponent getParentComponent();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    HashMap<String, Boolean> getPermissions();

    /**
     * DOCUMENT ME!
     *
     * @param   refreshClass  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Refreshable getRefreshableByClass(Class<?> refreshClass);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    StatusBar getStatusBar();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isCoreReadOnlyMode();

    /**
     * DOCUMENT ME!
     *
     * @param  refreshingObject  DOCUMENT ME!
     */
    void refreshWidgets(Object refreshingObject);

    /**
     * DOCUMENT ME!
     */
    void resetWidgets();

    /**
     * DOCUMENT ME!
     *
     * @param  isCoreReadOnlyMode  DOCUMENT ME!
     */
    void setCoreReadOnlyMode(boolean isCoreReadOnlyMode);

    /**
     * DOCUMENT ME!
     *
     * @param  isFullReadOnlyMode  DOCUMENT ME!
     */
    void setFullReadOnlyMode(boolean isFullReadOnlyMode);

    /**
     * DOCUMENT ME!
     *
     * @param  aMappingComponent  DOCUMENT ME!
     */
    void setMappingComponent(MappingComponent aMappingComponent);

    /**
     * DOCUMENT ME!
     *
     * @param  parentComponent  DOCUMENT ME!
     */
    void setParentComponent(JComponent parentComponent);

    /**
     * DOCUMENT ME!
     *
     * @param  permissions  DOCUMENT ME!
     */
    void setPermissions(HashMap<String, Boolean> permissions);

    /**
     * DOCUMENT ME!
     *
     * @param  statusBar  DOCUMENT ME!
     */
    void setStatusBar(StatusBar statusBar);

    /**
     * DOCUMENT ME!
     *
     * @param  isEditable  DOCUMENT ME!
     */
    void setWidgetsEditable(final boolean isEditable);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean validateWidgets();
}
