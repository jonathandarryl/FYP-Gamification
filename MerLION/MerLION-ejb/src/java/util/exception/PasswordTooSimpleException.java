/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.exception;

/**
 *
 * @author chongyangsun
 */
public class PasswordTooSimpleException extends Exception {

    public PasswordTooSimpleException() {
        super();
    }
    
    public PasswordTooSimpleException(String msg) {
        super(msg);
    }
}    
