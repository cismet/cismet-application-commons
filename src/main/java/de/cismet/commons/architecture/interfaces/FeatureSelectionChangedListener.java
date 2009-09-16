/*
 * FeatureSelectionChangedListener.java
 *
 * Created on 1. Juli 2007, 16:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.commons.architecture.interfaces;

import de.cismet.cismap.commons.features.Feature;
import java.util.Collection;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Sebastian Puhl
 */
public interface FeatureSelectionChangedListener extends ChangeListener {
    void featureSelectionChanged(Collection<Feature> features);
}
