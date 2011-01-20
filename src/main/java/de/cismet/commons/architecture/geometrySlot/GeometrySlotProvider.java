/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * GeometrySlotProvider.java
 *
 * Created on 12. Mai 2007, 19:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.commons.architecture.geometrySlot;

import java.util.Vector;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public interface GeometrySlotProvider {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Vector<GeometrySlotInformation> getSlotInformation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getProviderName();
}
