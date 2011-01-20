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
package de.cismet.commons.architecture.exception;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public class UnlockingNotSuccessfulException extends Exception {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new UnlockingNotSuccessfulException object.
     *
     * @param  message  DOCUMENT ME!
     */
    public UnlockingNotSuccessfulException(final String message) {
        super(message);
    }
}
