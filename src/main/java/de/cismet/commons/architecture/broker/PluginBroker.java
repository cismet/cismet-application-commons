/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.commons.architecture.broker;

import com.vividsolutions.jts.geom.Geometry;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.statusbar.StatusBar;
import de.cismet.commons.architecture.geometrySlot.GeometrySlotInformation;
import de.cismet.commons.architecture.interfaces.ChangeObserver;
import de.cismet.commons.architecture.interfaces.Refreshable;
import de.cismet.commons.architecture.interfaces.Widget;
import de.cismet.tools.configuration.Configurable;
import java.awt.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import org.jdom.Element;

/**
 *
 * @author spuhl
 */
public interface PluginBroker extends ChangeObserver, Configurable {

    Date getDateWithoutTime(Date date);

    void warnIfThreadIsEDT();

    void warnIfThreadIsNotEDT();

    void addWidget(Widget widget);

    void addWidgets(Vector widgets);

    GeometrySlotInformation assignGeometry(Geometry geom);    

    void configure(Element parent);

    void execute(SwingWorker workerThread);

    void fireChangeEvent(Object event);

    String getCurrentValidationErrorMessage();

    MappingComponent getMappingComponent();

    JComponent getParentComponent();

    HashMap<String, Boolean> getPermissions();

    Refreshable getRefreshableByClass(Class<?> refreshClass);

    

    StatusBar getStatusBar();

    boolean isCoreReadOnlyMode();

    void refreshWidgets(Object refreshingObject);

    void resetWidgets();

    void setCoreReadOnlyMode(boolean isCoreReadOnlyMode);

    void setFullReadOnlyMode(boolean isFullReadOnlyMode);

    void setMappingComponent(MappingComponent aMappingComponent);

    void setParentComponent(JComponent parentComponent);

    void setPermissions(HashMap<String, Boolean> permissions);
   
    void setStatusBar(StatusBar statusBar);   

    void setWidgetsEditable(final boolean isEditable);

    boolean validateWidgets();
    
    
}
