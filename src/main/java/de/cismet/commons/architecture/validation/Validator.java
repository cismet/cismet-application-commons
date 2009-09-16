/*
 * Validator.java
 *
 * Created on 1. Februar 2005, 14:19
 */

package de.cismet.commons.architecture.validation;
import java.awt.Color;
import javax.swing.*;
/**
 *
 * @author hell
 */
public class Validator implements ValidationStateChangedListener{
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    javax.swing.JComponent comp=null;
    Validatable vali=null;
    javax.swing.ImageIcon valid=new javax.swing.ImageIcon(this.getClass().getResource("/de/cismet/lagis/ressource/icons/validation/green.png"));
    javax.swing.ImageIcon warning=new javax.swing.ImageIcon(this.getClass().getResource("/de/cismet/lagis/ressource/icons/validation/orange.png"));
    javax.swing.ImageIcon error=new javax.swing.ImageIcon(this.getClass().getResource("/de/cismet/lagis/ressource/icons/validation/red.png"));
    JLabel iconContainer=new JLabel();
    
    
    /** Creates a new instance of Validator */
    public Validator(javax.swing.JComponent comp) {
        this.comp=comp;
        //comp.setBackground(Color.red);
        iconContainer.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        iconContainer.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        iconContainer.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
        //iconContainer.setText("LALAL");
        comp.setLayout(new java.awt.BorderLayout());
        comp.add(iconContainer,java.awt.BorderLayout.EAST);
        iconContainer.setVisible(true);
        iconContainer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                iconContainerMouseClicked(evt);
            }
        });        
    }
    public void iconContainerMouseClicked(java.awt.event.MouseEvent evt){
        if (evt.getClickCount()>1 && evt.getButton()==evt.BUTTON1 && vali!=null) {
            vali.showAssistent(comp);
        }
    }

    public Validator(javax.swing.JComponent comp,Validatable vali) {
        this(comp);
        reSetValidator(vali);
    }
    public void reSetValidator(Validatable vali) {
        
        if (vali==null) {
            log.warn("VALI == NULL");
            iconContainer.setVisible(false);
        }
        else {
            this.vali=vali;
            comp.remove(iconContainer);
            iconContainer.setVisible(true);
            comp.setLayout(new java.awt.BorderLayout());
            comp.add(iconContainer,java.awt.BorderLayout.EAST);
            vali.addValidationStateChangedListener(this);
            //TODO SIGNATURE CHANGED -- UGLY better possibilites ?? 
            validationStateChanged(null);
        }
    }
    //TODO SIGNATURE CHANGED
    public void validationStateChanged(Object validatedObject) {
        if (vali!=null) {
            final int status=vali.getStatus();
            iconContainer.setToolTipText(vali.getValidationMessage());
            iconContainer.setVisible(true);
            switch (status) {
                case Validatable.ERROR: 
                    iconContainer.setIcon(error);
                    iconContainer.putClientProperty("state","ERROR");
                    break;
                case Validatable.WARNING: 
                    iconContainer.setIcon(warning);
                    iconContainer.putClientProperty("state","WARNING");
                    break;
                case Validatable.VALID: 
                    iconContainer.setIcon(valid);
                    iconContainer.putClientProperty("state","VALID");
                    Integer counter=(Integer)(iconContainer.getClientProperty("validCounter"));
                    if (counter!=null) {
                        iconContainer.putClientProperty("validCounter", new Integer(counter.intValue()+1));
                    }
                    else {
                         iconContainer.putClientProperty("validCounter", new Integer(1));
                    }
                    java.awt.event.ActionListener timerAction = new java.awt.event.ActionListener() {
                              public void actionPerformed( java.awt.event.ActionEvent event ) {
                                  if (iconContainer.getClientProperty("state").equals("VALID"))   {
                                      Integer counter=(Integer)(iconContainer.getClientProperty("validCounter"));
                                      iconContainer.putClientProperty("validCounter", new Integer(counter.intValue()-1));
                                      if (counter.equals(new Integer(1))) {
                                        iconContainer.setVisible(false);
                                      }
                                  }
                                  else {
                                        iconContainer.putClientProperty("validCounter", new Integer(0));
                                  }
                              }
                            };
                    javax.swing.Timer timer = new javax.swing.Timer(4000, timerAction );
                    timer.setRepeats(false);
                    timer.start();
            }
        }
    }

    public int getValidationState(){
        if(vali == null){
            return Validatable.ERROR;
        } else {
            return vali.getStatus();
        }
    }
    
    public String getValidationMessage(){
         if(vali == null && vali.getValidationMessage() != null ){
            return "Keine Fehlernachricht vorhanden";
        } else {
            return vali.getValidationMessage();
        }
    }
}
