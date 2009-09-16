/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.commons.architecture.util;

import java.awt.Component;
import java.awt.Container;

/**
 *
 * @author spuhl
 */
public class ArchitectureUtils {
    
    //check commons class look if already there
    public static void enableContainerRecursivley(Container component, boolean isEnabled) {
        if (component != null) {
            component.setEnabled(isEnabled);
            if (component.getComponentCount() > 0) {
                for (Component curComp : component.getComponents()) {
                    if (curComp != null && curComp instanceof Container) {
                        enableContainerRecursivley((Container)curComp, isEnabled);
                    } else {
                        curComp.setEnabled(isEnabled);
                    }
                }
            }
        }
    }

}
