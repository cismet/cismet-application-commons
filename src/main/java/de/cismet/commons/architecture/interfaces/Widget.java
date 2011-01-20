/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * Widget.java
 *
 * Created on 1. Mai 2007, 09:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.commons.architecture.interfaces;

import java.beans.PropertyChangeListener;

import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JComponent;

import de.cismet.commons.architecture.validation.Validatable;

import de.cismet.tools.configuration.Configurable;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public interface Widget extends ObjectChangeListener,
    Clearable,
    Editable,
    Validatable,
    Refreshable,
    EntitySaver,
    Configurable,
    PropertyChangeListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getWidgetName();
    /**
     * DOCUMENT ME!
     *
     * @param  widgetName  DOCUMENT ME!
     */
    void setWidgetName(final String widgetName);
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Icon getWidgetIcon();
    /**
     * DOCUMENT ME!
     *
     * @param  iconName  DOCUMENT ME!
     */
    void setWidgetIcon(final String iconName);
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isCoreWidget();
    /**
     * DOCUMENT ME!
     *
     * @param  isCoreWidget  DOCUMENT ME!
     */
    void setIsCoreWidget(final boolean isCoreWidget);
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isReadOnlyWidget();
    /**
     * DOCUMENT ME!
     *
     * @param  isReadOnlyWidget  DOCUMENT ME!
     */
    void setReadOnlyWidget(final boolean isReadOnlyWidget);
    /**
     * DOCUMENT ME!
     */
    void updateUIPropertyChange();
    /**
     * ToDo should be in seperate interface or in a deeper layer like Layoutet Widget.
     *
     * @return  DOCUMENT ME!
     */
    ArrayList<JComponent> getCustomButtons();
}
