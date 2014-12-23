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
public class TitleNotExistException extends Exception {

    public TitleNotExistException() {
        super();
    }
    
    public TitleNotExistException(String msg) {
        super(msg);
    }
}
