/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.commons.architecture.broker;

import java.awt.Color;
import net.infonode.docking.RootWindow;

/**
 *
 * @author spuhl
 */
public interface AdvancedPluginBrokerInt extends PluginBroker {
    
    void setTitleBarComponentpainter(Color color);

    void setTitleBarComponentpainter(Color left, Color right);        
    
    RootWindow getRootWindow();
}
