/*
 * AbstractWidget.java
 *
 * Created on 20. November 2007, 09:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.commons.architecture.widget;


import de.cismet.commons.architecture.broker.AdvancedPluginBroker;
import de.cismet.commons.architecture.interfaces.Widget;
import de.cismet.commons.architecture.validation.ValidationStateChangedListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.BindingGroup;

/**
 *
 * @author Sebastian Puhl
 */
public abstract class AbstractWidget extends JPanel implements Widget {
    
    public abstract BindingGroup getBindingGroup();

    public abstract void setBindingGroup(BindingGroup bindingGroup);

    //Idea isEditable property
    
//    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
//
//    /**
//     * Add PropertyChangeListener.
//     *
//     * @param listener
//     */
//    public void addPropertyChangeListener(PropertyChangeListener listener) {
//        
//        propertyChangeSupport.addPropertyChangeListener(listener);
//    }
//
//    /**
//     * Remove PropertyChangeListener.
//     *
//     * @param listener
//     */
//    public void removePropertyChangeListener(PropertyChangeListener listener) {
//        propertyChangeSupport.removePropertyChangeListener(listener);
//    }
    
    protected AdvancedPluginBroker broker;

    public AdvancedPluginBroker getBroker() {
        return broker;
    }

    public void setBroker(AdvancedPluginBroker broker) {
        this.broker = broker;
    }
    
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private final ArrayList<ValidationStateChangedListener> validationListeners = new ArrayList<ValidationStateChangedListener>();
    //Widget properties
    private UpdateWorker currentWorker;
    protected String widgetName = "Fenstername";
    public static final String PROP_WIDGET_NAME = "widgetName";
    protected static final Icon DEFAULT_ICON = new javax.swing.ImageIcon(AbstractWidget.class.getResource("/de/cismet/commons/architecture/resource/icon/cismetlogo16.png"));
    public static final String PROP_WIDGET_ICON = "widgetIcon";
    protected Icon widgetIcon;  
    //Edit properties    
    private boolean isCoreWidget = false;    
    private boolean isReadOnlyWidget=true;    
    // Validation    
    /** Creates a new instance of AbstractWidget */
    public AbstractWidget(AdvancedPluginBroker broker) {
        this.broker = broker;        
    }   
    
    //TODO Refactor why a abstract class ? better a default Widget ?
    //CLASS BUILD BECAUSE NEED TO BE A COMPONENT --> NOT POSSIBLE WITH INTERFACES
    @Override
    public String getWidgetName() {
        return widgetName;
    }

    @Override
    public void setWidgetName(final String widgetName) {
        final String oldWidgetName =getWidgetName();
        this.widgetName = widgetName;
        firePropertyChange(PROP_WIDGET_NAME, oldWidgetName, widgetName);
    }

    @Override
    public Icon getWidgetIcon() {
        if (widgetIcon != null) {
            return widgetIcon;
        } else {
            widgetIcon = DEFAULT_ICON;
            return DEFAULT_ICON;
        }
    }

    public void setWidgetIcon(final String iconName) {
        try {            
            setWidgetIcon(new javax.swing.ImageIcon(getClass().getResource(iconName)));
        } catch (Exception ex) {
            log.warn("Fehler beim setzen des Icons: ", ex);
            setWidgetIcon(DEFAULT_ICON);
        }
    }
    
    public void setWidgetIcon(final Icon widgetIcon) {
       final Icon oldWidgetIcon = getWidgetIcon();
       this.widgetIcon = widgetIcon;
       firePropertyChange(PROP_WIDGET_NAME, oldWidgetIcon, widgetIcon);
    }
                   
    
    @Override
    public boolean isCoreWidget() {
        return isCoreWidget;
    }

    @Override
    public void setIsCoreWidget(final boolean isCoreWidget) {
        this.isCoreWidget = isCoreWidget;
    }
    
    @Override
    public boolean isReadOnlyWidget(){
       return isReadOnlyWidget;
    }    
    
    @Override
    public void setReadOnlyWidget(final boolean isReadOnlyWidget){
        this.isReadOnlyWidget = isReadOnlyWidget;
    }    
    
    public void objectChanged(Object changedObject) {
        if(currentWorker != null && !currentWorker.isDone()){
            currentWorker.cancel(true);
        }
        currentWorker = new UpdateWorker(changedObject);
        broker.execute(currentWorker);
    }        
    
    class UpdateWorker extends SwingWorker<Void, Void> {
        private Object changedObject;
        UpdateWorker(Object changedObject) {
            this.changedObject = changedObject;
        }

        protected Void doInBackground() throws Exception {
            guiObjectChanged(changedObject);
            return null;
        }

        protected void done() {            
            if (isCancelled()) {
                log.warn("UpdateWorker is canceled --> nothing to do in method done()");
                return;
            }
            try {                
               guiObjectChanged(changedObject);
               broker.fireChangeFinished(AbstractWidget.this);
            } catch (Exception ex) {
                log.error("Failure during processing UpdateWorker results", ex);
                return;
            }
        }
    }
        
    public void removeValidationStateChangedListener(ValidationStateChangedListener l) {
        validationListeners.remove(l);
    }

    
    public void addValidationStateChangedListener(ValidationStateChangedListener l) {
        validationListeners.add(l);
    }
    
     @Override
    public void fireValidationStateChanged(Object validatedObject) {
        for (ValidationStateChangedListener listener : validationListeners) {
            listener.validationStateChanged(validatedObject);
        }
    }
    
    
    public abstract void guiObjectChanged(Object changedObject);

    public void propertyChange(PropertyChangeEvent evt) {
       log.debug("PropertyChange in Widget: "+getWidgetName());        
        updateUIPropertyChange();
    }

    
    
//    public BasicPluginBroker getBasicBroker() {
//        return broker;
//    }

//    public void setBasicBroker(BasicPluginBroker basicBroker) {
//        this.broker = basicBroker;
//    }   
    
    
}
