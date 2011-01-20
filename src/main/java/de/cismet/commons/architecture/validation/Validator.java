/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * Validator.java
 *
 * Created on 1. Februar 2005, 14:19
 */
package de.cismet.commons.architecture.validation;
import java.awt.Color;

import javax.swing.*;
/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class Validator implements ValidationStateChangedListener {

    //~ Instance fields --------------------------------------------------------

    javax.swing.JComponent comp = null;
    Validatable vali = null;
    javax.swing.ImageIcon valid = new javax.swing.ImageIcon(this.getClass().getResource(
                "/de/cismet/lagis/ressource/icons/validation/green.png"));
    javax.swing.ImageIcon warning = new javax.swing.ImageIcon(this.getClass().getResource(
                "/de/cismet/lagis/ressource/icons/validation/orange.png"));
    javax.swing.ImageIcon error = new javax.swing.ImageIcon(this.getClass().getResource(
                "/de/cismet/lagis/ressource/icons/validation/red.png"));
    JLabel iconContainer = new JLabel();
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of Validator.
     *
     * @param  comp  DOCUMENT ME!
     */
    public Validator(final javax.swing.JComponent comp) {
        this.comp = comp;
        // comp.setBackground(Color.red);
        iconContainer.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        iconContainer.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        iconContainer.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
        // iconContainer.setText("LALAL");
        comp.setLayout(new java.awt.BorderLayout());
        comp.add(iconContainer, java.awt.BorderLayout.EAST);
        iconContainer.setVisible(true);
        iconContainer.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    iconContainerMouseClicked(evt);
                }
            });
    }

    /**
     * Creates a new Validator object.
     *
     * @param  comp  DOCUMENT ME!
     * @param  vali  DOCUMENT ME!
     */
    public Validator(final javax.swing.JComponent comp, final Validatable vali) {
        this(comp);
        reSetValidator(vali);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    public void iconContainerMouseClicked(final java.awt.event.MouseEvent evt) {
        if ((evt.getClickCount() > 1) && (evt.getButton() == evt.BUTTON1) && (vali != null)) {
            vali.showAssistent(comp);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @param  vali  DOCUMENT ME!
     */
    public void reSetValidator(final Validatable vali) {
        if (vali == null) {
            log.warn("VALI == NULL");
            iconContainer.setVisible(false);
        } else {
            this.vali = vali;
            comp.remove(iconContainer);
            iconContainer.setVisible(true);
            comp.setLayout(new java.awt.BorderLayout());
            comp.add(iconContainer, java.awt.BorderLayout.EAST);
            vali.addValidationStateChangedListener(this);
            // TODO SIGNATURE CHANGED -- UGLY better possibilites ??
            validationStateChanged(null);
        }
    }
    // TODO SIGNATURE CHANGED
    @Override
    public void validationStateChanged(final Object validatedObject) {
        if (vali != null) {
            final int status = vali.getStatus();
            iconContainer.setToolTipText(vali.getValidationMessage());
            iconContainer.setVisible(true);
            switch (status) {
                case Validatable.ERROR: {
                    iconContainer.setIcon(error);
                    iconContainer.putClientProperty("state", "ERROR");
                    break;
                }
                case Validatable.WARNING: {
                    iconContainer.setIcon(warning);
                    iconContainer.putClientProperty("state", "WARNING");
                    break;
                }
                case Validatable.VALID: {
                    iconContainer.setIcon(valid);
                    iconContainer.putClientProperty("state", "VALID");
                    final Integer counter = (Integer)(iconContainer.getClientProperty("validCounter"));
                    if (counter != null) {
                        iconContainer.putClientProperty("validCounter", new Integer(counter.intValue() + 1));
                    } else {
                        iconContainer.putClientProperty("validCounter", new Integer(1));
                    }
                    final java.awt.event.ActionListener timerAction = new java.awt.event.ActionListener() {

                            @Override
                            public void actionPerformed(final java.awt.event.ActionEvent event) {
                                if (iconContainer.getClientProperty("state").equals("VALID")) {
                                    final Integer counter = (Integer)(iconContainer.getClientProperty("validCounter"));
                                    iconContainer.putClientProperty(
                                        "validCounter",
                                        new Integer(counter.intValue() - 1));
                                    if (counter.equals(new Integer(1))) {
                                        iconContainer.setVisible(false);
                                    }
                                } else {
                                    iconContainer.putClientProperty("validCounter", new Integer(0));
                                }
                            }
                        };

                    final javax.swing.Timer timer = new javax.swing.Timer(4000, timerAction);
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getValidationState() {
        if (vali == null) {
            return Validatable.ERROR;
        } else {
            return vali.getStatus();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getValidationMessage() {
        if ((vali == null) && (vali.getValidationMessage() != null)) {
            return "Keine Fehlernachricht vorhanden";
        } else {
            return vali.getValidationMessage();
        }
    }
}
