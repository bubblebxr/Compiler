package frontend.AST;

import frontend.Token.TrueToken;

public class ForStmt {
    protected LVal lVal;
    protected TrueToken ASSIGN;
    protected Exp exp;

    public void setlVal(LVal lVal) {
        this.lVal = lVal;
    }

    public void setASSIGN(TrueToken ASSIGN) {
        this.ASSIGN = ASSIGN;
    }

    public void setExp(Exp exp) {
        this.exp = exp;
    }

    public String outputForStmt() {
        StringBuilder a=new StringBuilder();
        a.append(lVal.outputLVal());
        a.append(ASSIGN.toString());
        a.append(exp.outputExp());
        a.append("<ForStmt>\n");
        return a.toString();
    }

    public LVal getlVal() {
        return lVal;
    }

    public Exp getExp() {
        return exp;
    }
}
