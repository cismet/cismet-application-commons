/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.commons.architecture.widget;

import de.cismet.commons.architecture.broker.AdvancedPluginBroker;
import de.cismet.tools.configuration.NoWriteError;
import de.cismet.commons.architecture.validation.Validatable;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JComponent;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Binding.ValueResult;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdom.Element;

/**
 *
 * @author spuhl
 */
public class DefaultWidget extends AbstractWidget {

    protected final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    protected String validationMessage = "";

    public DefaultWidget(AdvancedPluginBroker broker) {
        super(broker);
    }

    @Override
    public void objectChanged(Object changedObject) {
    }

    //Proper implementation --> class all Container something and calls the clear Method
    public void clearComponent() {
        log.debug(getName() + " Widget cleared");
    }

    public void setWidgetEditable(boolean isEditable) {
        log.debug(getName() + " Widget setEditable: " + isEditable);
    }

    public void refresh(Object refreshedObject) {
        log.debug(getName() + " Widget refreshed");

    }

    public void configure(Element parent) {
    }

    //ToDo proper defaultConfiguration
    public Element getConfiguration() throws NoWriteError {
        return null;
    }

    public void masterConfigure(Element parent) {
        clearComponent();
    }

    public void guiObjectChanged(Object changedObject) {
    }

    public ArrayList<JComponent> getCustomButtons() {
        return new ArrayList<JComponent>();
    }

    public void backgroundObjectChanged(Object changedObject) {
    }

//    @Override
//    public String getValidationMessage() {
//        return validationMessage;
//    }
//
//    @Override
//    public int getStatus() {
//        return Validatable.VALID;
//    }

    //ToDo print the right messages from the validators
    @Override
    public String getValidationMessage() {
//        return "Nicht alle Einträge sind korrekt.\nBitte korrigieren Sie die Fehlerhaften.";
        return validationMessage;
    }
//

    @Override
    public int getStatus() {
//        if (validationState.size() != 0) {
//            log.info("There are bindings which are not valid. Errorcount: "+validationState.size());
//            return Validatable.ERROR;
//        }
        if (getBindingGroup() != null) {
            for (Binding curBinding : getBindingGroup().getBindings()) {
                if (this.isAncestorOf((Component) curBinding.getTargetObject())) {
//            Validator currentValidator = curBinding.getValidator();
//            if(currentValidator != null){
//                log.debug("Validator of Binding != null. Validating Property: "+curBinding.getSourceProperty());
//
//            }
                    ValueResult result = curBinding.getTargetValueForSource();
                    if (result != null && result.failed() && result.getFailure().getType() == Binding.SyncFailureType.VALIDATION_FAILED) {
                        log.info("Validation of property " + curBinding.getSourceProperty() + "has failed: " + result);
                        log.info("Description: " + result.getFailure().getValidationResult().getDescription());
                        validationMessage = result.getFailure().getValidationResult().getDescription();
                        return Validatable.ERROR;
                    } else {
                        log.info("Validation of property " + curBinding.getSourceProperty() + "is valid: " + result);
                        try {
                            log.info("Check has failure: " + result.failed());
                            if (result.failed()) {
                                log.info("failure " + result.getFailure());
                                log.info("manual check: " + curBinding.getValidator().validate(curBinding.getTargetProperty().getValue(curBinding.getTargetObject())));
                            } else {
                                log.debug("value: " + result.getValue());
                            }
                        } catch (Exception ex) {
                            log.debug("manual check failed");
                        }
                    }
                } else {
                    //log.debug("Validation is skipped because binding does not belong to currentPanel.");
                }
            }

        }
        validationMessage = "";
        return Validatable.VALID;
    }
    protected org.jdesktop.beansbinding.BindingGroup bindingGroup;

    @Override
    public BindingGroup getBindingGroup() {
        return bindingGroup;
    }

    @Override
    public void setBindingGroup(BindingGroup bindingGroup) {
        this.bindingGroup = bindingGroup;
    }

    @Override
    public void showAssistent(Component parent) {
    }

    public void updateUIPropertyChange() {
    }
}
