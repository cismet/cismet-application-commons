/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.commons.architecture.broker;

import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.NoWriteError;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Element;

/**
 *
 * @author spuhl
 */
public class BrokerLookup implements Configurable {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BrokerLookup.class);
    final HashMap<String, Class> nameToClass = new HashMap();
    final HashMap<String, AdvancedPluginBroker> nameToInstance = new HashMap();
    private static BrokerLookup brokerLookup;

    private BrokerLookup() {
    }

    public static BrokerLookup getInstance() {
        if (brokerLookup == null) {
            brokerLookup = new BrokerLookup();
        }
        return brokerLookup;
    }

    //ToDo Exceptionhandling
    public AdvancedPluginBroker getBrokerForName(final String brokerName) {        
        AdvancedPluginBroker brokerInstance;
        if ((brokerInstance = nameToInstance.get(brokerName)) != null) {
            log.debug("Instance for: "+brokerName+" already created");
            return brokerInstance;
        } else {
            Class<AdvancedPluginBroker> brokerClass;
            if ((brokerClass = nameToClass.get(brokerName)) != null) {
                try {
                    //Constructor constructor = brokerClass.getConstructor();
                    //brokerInstance = (AdvancedPluginBroker)constructor.newInstance();
                    log.debug("Creating new Instance for: "+brokerName);
                    brokerInstance = brokerClass.newInstance();
                    nameToInstance.put(brokerName,brokerInstance);
                    return brokerInstance;
                } catch (Exception ex){
                 log.error("Error during creation of instance "+brokerClass.getName()+" for broker "+brokerName,ex);
                 return null;  
                }                
            } else {
                log.info("There is no class registered for " + brokerName);
                return null;
            }
        }
    }
        
    public void configure(Element parent) {
    }

    public Element getConfiguration() throws NoWriteError {
        return null;
    }

    public void masterConfigure(final Element parent) {
        try {

            final Element brokerLookupConf = parent.getChild("BrokerConfiguration");

            for (Element broker : (List<Element>) brokerLookupConf.getChildren()) {
                try {
//                    log.debug("Setting Interface/Implemation for Broker" + broker.getChildText("BrokerName"));
//                    final Class brokerInterface = Class.forName(broker.getChildText("BrokerInterface"));
//                    final Class brokerImplementation = Class.forName(broker.getChildText("BrokerImplementation"));
//                    if (!BasicPluginBroker.class.isAssignableFrom(brokerImplementation)) {
//                        throw new Exception("Broker implemnation class is not derived from " + BasicPluginBroker.class.getName());
//                    }
                    log.debug("Creating instance of Broker" + broker.getChildText("BrokerClass") +" with name: "+broker.getChildText("BrokerName"));
                    final Class brokerClass = Class.forName(broker.getChildText("BrokerClass"));                    
                    if (!AdvancedPluginBroker.class.isAssignableFrom(brokerClass)) {
                        throw new Exception("Broker implemnation class is not derived from " + AdvancedPluginBroker.class.getName());
                    }
                    nameToClass.put(broker.getChildText("BrokerName"), brokerClass);
                    //getBrokerForName(broker.getChildText("BrokerName"));
                } catch (Exception ex) {
                    log.warn("Error while configuring Interface/Implemantion for one broker --> skipped", ex);
                }
            }
        } catch (Exception ex) {
            log.error("Error during configuration of plugin brokers", ex);
        }
    }
}
