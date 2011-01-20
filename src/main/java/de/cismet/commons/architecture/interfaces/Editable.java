/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * Editable.java
 *
 * Created on 1. Mai 2007, 09:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.commons.architecture.interfaces;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public interface Editable {

    //~ Methods ----------------------------------------------------------------

    /**
     * ToDo rename if a component is editable dosen't mean automatic that it is a widget.
     *
     * @param  isEditable  DOCUMENT ME!
     */
    void setWidgetEditable(final boolean isEditable);
}
