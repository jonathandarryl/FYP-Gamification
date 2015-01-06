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
public class RoutesNotExistException extends Exception{

    public RoutesNotExistException() {
    }

    public RoutesNotExistException(String message) {
        super(message);
    }
    private static final long serialVersionUID = 1L;
    
}
