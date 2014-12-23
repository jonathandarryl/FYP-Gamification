/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author HanXiangyu
 */
public class VehicleNoDriversException extends Exception{
    private static final long serialVersionUID = 1L;

    public VehicleNoDriversException() {
    }

    public VehicleNoDriversException(String message) {
        super(message);
    }
    
}
