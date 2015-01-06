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
public class PurchaseOrderHasBeenSentException extends Exception{
    private static final long serialVersionUID = 1L;

    public PurchaseOrderHasBeenSentException() {
        super();
    }
    
    public PurchaseOrderHasBeenSentException(String msg) {
        super(msg);
    }
}
