/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.commons.architecture.exception;

/**
 *
 * @author spuhl
 */
public class LockingNotSuccessfulException extends Exception {

    public LockingNotSuccessfulException(String message) {
        super(message);
    }

}
