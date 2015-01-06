/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.exception;

/**
 *
 * @author songhan
 */
public class ReceiptNotExistException extends Exception{
    private static final long serialVersionUID = 1L;

    public ReceiptNotExistException() {
        super();
    }
    
    public ReceiptNotExistException(String msg) {
        super(msg);
    }
}
