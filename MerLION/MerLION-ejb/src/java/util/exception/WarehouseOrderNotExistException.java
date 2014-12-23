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
public class WarehouseOrderNotExistException extends Exception {

    public WarehouseOrderNotExistException() {
        super();
    }
    
    public WarehouseOrderNotExistException(String msg) {
        super(msg);
    }
}
