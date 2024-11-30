package frontend.AST;

import frontend.Token.TrueToken;

import static llvm.LLVMManager.*;

public class LVal {
    protected TrueToken Ident;
    protected TrueToken LBRACK;
    protected Exp exp;
    protected TrueToken RBRACK;

    public void setIdent(TrueToken ident) {
        Ident = ident;
    }

    public void setLBRACK(TrueToken LBRACK) {
        this.LBRACK = LBRACK;
    }

    public void setExp(Exp exp) {
        this.exp = exp;
    }

    public void setRBRACK(TrueToken RBRACK) {
        this.RBRACK = RBRACK;
    }

    public String outputLVal() {
        StringBuilder a=new StringBuilder();
        a.append(Ident.toString());
        if(LBRACK!=null){
            a.append(LBRACK.toString());
            a.append(exp.outputExp());
            a.append(RBRACK.toString());
        }
        a.append("<LVal>\n");
        return a.toString();
    }

    public TrueToken getIdent() {
        return Ident;
    }

    public Exp getExp() {
        return exp;
    }

    public int getLValValue() {
        if(exp!=null){
            return getArrayValue(Ident,exp.getExpValue(),presentId);
        }
        return getVarValue(Ident, presentId);
    }
}
