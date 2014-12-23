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
public class TitleExistException extends Exception {

    public TitleExistException() {
        super();
    }
    
    public TitleExistException(String msg) {
        super(msg);
    }
}
