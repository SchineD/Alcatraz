/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alcatraza2;

/**
 *
 * @author alexa
 */
public class UserExistsException extends java.lang.Exception {

    public UserExistsException() {}
  
    public UserExistsException(String msg) {
        super(msg);
    }
    
    public UserExistsException(String msg, Throwable cause) {
        super(msg,cause);
    }
  
    public UserExistsException(Throwable cause) {
        super(cause);
    }

}