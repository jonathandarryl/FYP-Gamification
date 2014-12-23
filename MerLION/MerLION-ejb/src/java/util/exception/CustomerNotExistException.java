/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.exception;

/**
 *
 * @author Zhong Wei
 */
public class CustomerNotExistException extends Exception{
    private static final long serialVersionUID = 1L;

    public CustomerNotExistException() {
        super();
    }
    
    public CustomerNotExistException(String msg) {
        super(msg);
    }
}
