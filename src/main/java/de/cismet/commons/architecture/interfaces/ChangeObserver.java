/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.commons.architecture.interfaces;

/**
 *
 * @author spuhl
 */
public interface ChangeObserver {
    
    void fireChangeFinished(ObjectChangeListener changeListener);
    
}
