/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Motor.syntax;

import Motor.automatos.TypeClass;
import Motor.automatos.TypeClass;

/**
 *
 * @author Gustavo Liberatti
 */
public class Var {

    public Var(String ident) {
        this.ident = ident;
    }

    public Var() {
        this.ident = null;
    }

    @Override
    public String toString(){
       if (this.ident!=null) {
           return this.ident;
       } else{
           return "null";
       }
    }
    public String ident;
    public String tipo;
}
