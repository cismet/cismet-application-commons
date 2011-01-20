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
package de.cismet.commons.architecture.util;

import java.awt.Component;
import java.awt.Container;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public class ArchitectureUtils {

    //~ Methods ----------------------------------------------------------------

    /**
     * check commons class look if already there.
     *
     * @param  component  DOCUMENT ME!
     * @param  isEnabled  DOCUMENT ME!
     */
    public static void enableContainerRecursivley(final Container component, final boolean isEnabled) {
        if (component != null) {
            component.setEnabled(isEnabled);
            if (component.getComponentCount() > 0) {
                for (final Component curComp : component.getComponents()) {
                    if ((curComp != null) && (curComp instanceof Container)) {
                        enableContainerRecursivley((Container)curComp, isEnabled);
                    } else {
                        curComp.setEnabled(isEnabled);
                    }
                }
            }
        }
    }
}
