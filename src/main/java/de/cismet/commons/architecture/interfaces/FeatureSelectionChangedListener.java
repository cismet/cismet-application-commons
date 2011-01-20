/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FeatureSelectionChangedListener.java
 *
 * Created on 1. Juli 2007, 16:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.commons.architecture.interfaces;

import java.util.Collection;

import javax.swing.event.ChangeListener;

import de.cismet.cismap.commons.features.Feature;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public interface FeatureSelectionChangedListener extends ChangeListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  features  DOCUMENT ME!
     */
    void featureSelectionChanged(Collection<Feature> features);
}
