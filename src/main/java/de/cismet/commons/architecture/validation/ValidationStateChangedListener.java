/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * ValidationStateChangedListener.java
 *
 * Created on 1. Februar 2005, 16:02
 */
package de.cismet.commons.architecture.validation;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public interface ValidationStateChangedListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * better validationEvent.
     *
     * @param  validatedObject  DOCUMENT ME!
     */
    void validationStateChanged(Object validatedObject);
}
