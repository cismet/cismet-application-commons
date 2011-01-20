/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * Resettable.java
 *
 * Created on 20. April 2007, 13:19
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
public interface Clearable extends Editable {

    //~ Methods ----------------------------------------------------------------

    /**
     * Methoden getrennt weil die enableMethode später wieder zum enablen benutzt wird.
     */
    void clearComponent();
}
