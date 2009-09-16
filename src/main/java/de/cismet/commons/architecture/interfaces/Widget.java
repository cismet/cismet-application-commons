/*
 * Widget.java
 *
 * Created on 1. Mai 2007, 09:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.commons.architecture.interfaces;

import de.cismet.commons.architecture.validation.Validatable;
import de.cismet.tools.configuration.Configurable;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.JComponent;

/**
 *
 * @author Puhl
 */
    public interface Widget extends ObjectChangeListener,Clearable,Editable,Validatable,Refreshable,EntitySaver,Configurable,PropertyChangeListener {    
    
    public String getWidgetName();
    public void setWidgetName(final String widgetName);
    public Icon getWidgetIcon();
    public void setWidgetIcon(final String iconName);    
    public boolean isCoreWidget();
    public void setIsCoreWidget(final boolean isCoreWidget);
    public boolean isReadOnlyWidget();
    public void setReadOnlyWidget(final boolean isReadOnlyWidget);
    public void updateUIPropertyChange();
    //ToDo should be in seperate interface or in a deeper layer like Layoutet Widget
    public ArrayList<JComponent> getCustomButtons();
}
