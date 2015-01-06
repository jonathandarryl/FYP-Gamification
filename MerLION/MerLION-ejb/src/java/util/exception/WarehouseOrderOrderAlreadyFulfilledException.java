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
public class WarehouseOrderOrderAlreadyFulfilledException extends Exception {

    public WarehouseOrderOrderAlreadyFulfilledException() {
        super();
    }
    
    public WarehouseOrderOrderAlreadyFulfilledException(String msg) {
        super(msg);
    }
}
