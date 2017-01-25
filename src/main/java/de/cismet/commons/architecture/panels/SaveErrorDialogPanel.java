/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * AlreadyLockedObjectsPanel.java
 *
 * Created on 31. März 2009, 12:40
 */
package de.cismet.commons.architecture.panels;

import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Converter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.ArrayList;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public class SaveErrorDialogPanel extends javax.swing.JPanel {

    //~ Instance fields --------------------------------------------------------

    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AlreadyLockedObjectsPanel.
     */
    public SaveErrorDialogPanel() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Add PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    @Override
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });

        jLabel2.setText(
            "<html><table width=\"250\" border=\"0\"><tr><td>Fehler beim speichern der Objekte. Ihre Änderungen konnten nicht gespeichert werden.</td></tr></table></html>");

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                layout.createSequentialGroup().addContainerGap().addComponent(
                    jLabel1,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    125,
                    Short.MAX_VALUE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                    12,
                    Short.MAX_VALUE).addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addGroup(
                        layout.createSequentialGroup().addComponent(
                            jButton1,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            72,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addGap(21, 21, 21)).addGroup(
                        layout.createSequentialGroup().addComponent(
                            jLabel2,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            278,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()))));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                layout.createSequentialGroup().addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addGroup(
                        layout.createSequentialGroup().addGap(34, 34, 34).addComponent(
                            jLabel1,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            98,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED)).addGroup(
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        layout.createSequentialGroup().addContainerGap(
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE).addComponent(jLabel2).addGap(31, 31, 31))).addComponent(jButton1)
                            .addContainerGap()));
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        ((JDialog)this.getRootPane().getParent()).dispose();
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed
}
