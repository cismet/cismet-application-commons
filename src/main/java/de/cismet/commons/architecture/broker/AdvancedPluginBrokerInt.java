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

import net.infonode.docking.RootWindow;

import java.awt.Color;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public interface AdvancedPluginBrokerInt extends PluginBroker {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  color  DOCUMENT ME!
     */
    void setTitleBarComponentpainter(Color color);

    /**
     * DOCUMENT ME!
     *
     * @param  left   DOCUMENT ME!
     * @param  right  DOCUMENT ME!
     */
    void setTitleBarComponentpainter(Color left, Color right);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    RootWindow getRootWindow();
}
