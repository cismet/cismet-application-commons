/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * GeometrySlotInformation.java
 *
 * Created on 12. Mai 2007, 19:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.commons.architecture.geometrySlot;

import de.cismet.commons.architecture.interfaces.Refreshable;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class GeometrySlotInformation {

    //~ Static fields/initializers ---------------------------------------------

    // TODO Refactor all method occurrencies
    public static final String SLOT_IDENTIFIER_SEPARATOR = " - ";

    //~ Instance fields --------------------------------------------------------

    private String providerName;
    private String slotIdentifier;
    private GeometrySlot openSlot;
    private Refreshable refreshable;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of GeometrySlotInformation.
     *
     * @param  providerName    DOCUMENT ME!
     * @param  slotIdentifier  DOCUMENT ME!
     * @param  openSlot        DOCUMENT ME!
     * @param  refreshable     DOCUMENT ME!
     */

    public GeometrySlotInformation(final String providerName,
            final String slotIdentifier,
            final GeometrySlot openSlot,
            final Refreshable refreshable) {
        this.setProviderName(providerName);
        this.setSlotIdentifier(slotIdentifier);
        this.setOpenSlot(openSlot);
        this.setRefreshable(refreshable);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getSLOT_IDENTIFIER_SEPARATOR() {
        return SLOT_IDENTIFIER_SEPARATOR;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  providerName  DOCUMENT ME!
     */
    public void setProviderName(final String providerName) {
        this.providerName = providerName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSlotIdentifier() {
        return slotIdentifier;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  slotIdentifier  DOCUMENT ME!
     */
    public void setSlotIdentifier(final String slotIdentifier) {
        this.slotIdentifier = slotIdentifier;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public GeometrySlot getOpenSlot() {
        return openSlot;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  openSlot  DOCUMENT ME!
     */
    public void setOpenSlot(final GeometrySlot openSlot) {
        this.openSlot = openSlot;
    }

    @Override
    public String toString() {
        return providerName + SLOT_IDENTIFIER_SEPARATOR + slotIdentifier;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Refreshable getRefreshable() {
        return refreshable;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  refreshable  DOCUMENT ME!
     */
    public void setRefreshable(final Refreshable refreshable) {
        this.refreshable = refreshable;
    }
}
