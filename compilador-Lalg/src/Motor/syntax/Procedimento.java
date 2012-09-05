package Motor.syntax;

import Motor.automatos.TypeClass;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gustavo Liberatti
 */
public class Procedimento {

    public String ident;
    public int num_par;
    private List<Var> var;
    private List<Var> par;

    public Procedimento(String ident) {
        this.ident = ident;
        var = new ArrayList<Var>();
        par = new ArrayList<Var>();

    }

    public Procedimento() {
        this.ident = null;
        var = new ArrayList<Var>();
        par = new ArrayList<Var>();

    }

    public void add_var(List<Var> var) {
        for (Var var1 : var) {
            this.getVar().add(var1);
        }
    }

    public void add_par(List<Var> par) {
        for (Var var_t : par) {
            this.getPar().add(var_t);
        }
        this.num_par += par.size();
    }

    public boolean contains_var(Var var) {
        boolean rs = false;
        for (Var var1 : this.getVar()) {
            if (var1.equals(var)) {
                rs = true;
            }
        }
        return rs;
    }

    public boolean contains_par(Var par) {
        boolean rs = false;
        for (Var var1 : this.getPar()) {
            if (var1.equals(par)) {
                rs = true;
            }
        }
        return rs;
    }

    public Var request_var(Var var) {
        Var return_var = new Var();
        for (Var var1 : this.getVar()) {
            if (var1.ident.equals(var.ident)) {
                return_var = var1;
                return return_var;
            }
        }
        return return_var;
    }

    public Var request_par(Var par) {
        Var return_par = new Var();
        for (Var par1 : this.getPar()) {
            if (par1.ident.equals(par.ident)) {
                return_par = par1;
                return return_par;
            }
        }
        return return_par;
    }



    public int par_count() {
        return getPar().size();
    }

    public int var_count() {
        return getVar().size();
    }

    /**
     * @return the par
     */
    public List<Var> getPar() {
        return par;
    }

    /**
     * @return the var
     */
    public List<Var> getVar() {
        return var;
    }
}
