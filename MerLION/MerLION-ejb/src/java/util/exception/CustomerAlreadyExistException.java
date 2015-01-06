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
public class CustomerAlreadyExistException extends Exception{
    private static final long serialVersionUID = 1L;

    public CustomerAlreadyExistException() {
        super();
    }
    
    public CustomerAlreadyExistException(String msg) {
        super(msg);
    }
}
