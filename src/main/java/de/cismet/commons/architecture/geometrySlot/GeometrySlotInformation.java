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
 *
 * @author Puhl
 */
public class GeometrySlotInformation {
    //TODO Refactor all method occurrencies
    public static final String SLOT_IDENTIFIER_SEPARATOR=" - ";    
    /**
     * Creates a new instance of GeometrySlotInformation
     */
    
    public GeometrySlotInformation(String providerName,String slotIdentifier,GeometrySlot openSlot,Refreshable refreshable) {
        this.setProviderName(providerName);
        this.setSlotIdentifier(slotIdentifier);
        this.setOpenSlot(openSlot);
        this.setRefreshable(refreshable);
    }
    
    private String providerName;
    private String slotIdentifier;
    private GeometrySlot openSlot;
    private Refreshable refreshable;
    
    public static String getSLOT_IDENTIFIER_SEPARATOR() {
        return SLOT_IDENTIFIER_SEPARATOR;
    }
    
    public String getProviderName() {
        return providerName;
    }
    
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
    
    public String getSlotIdentifier() {
        return slotIdentifier;
    }
    
    public void setSlotIdentifier(String slotIdentifier) {
        this.slotIdentifier = slotIdentifier;
    }
    
    public GeometrySlot getOpenSlot() {
        return openSlot;
    }
    
    public void setOpenSlot(GeometrySlot openSlot) {
        this.openSlot = openSlot;
    }
    
    public String toString() {
        return providerName +SLOT_IDENTIFIER_SEPARATOR+slotIdentifier;
    }

    public Refreshable getRefreshable() {
        return refreshable;
    }

    public void setRefreshable(Refreshable refreshable) {
        this.refreshable = refreshable;
    }
    
}
