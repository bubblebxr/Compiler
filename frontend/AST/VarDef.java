package frontend.AST;

import frontend.Token.TrueToken;

public class VarDef {
    protected TrueToken Ident;
    protected TrueToken LBRACK;
    protected ConstExp constExp;
    protected TrueToken RBRACK;
    protected TrueToken ASSIGN;
    protected InitVal initVal;

    public void setIdent(TrueToken ident) {
        Ident = ident;
    }

    public void setLBRACK(TrueToken LBRACK) {
        this.LBRACK = LBRACK;
    }

    public void setConstExp(ConstExp constExp) {
        this.constExp = constExp;
    }

    public void setRBRACK(TrueToken RBRACK) {
        this.RBRACK = RBRACK;
    }

    public void setASSIGN(TrueToken ASSIGN) {
        this.ASSIGN = ASSIGN;
    }

    public void setInitVal(InitVal initVal) {
        this.initVal = initVal;
    }

    public String outputVarDef() {
        StringBuilder a= new StringBuilder();
        a.append(Ident.toString());
        if(LBRACK!=null){
            a.append(LBRACK.toString());
            a.append(constExp.outputConstExp());
            a.append(RBRACK.toString());
        }
        if(ASSIGN!=null){
            a.append(ASSIGN.toString());
            a.append(initVal.outputInitVal());
        }
        a.append("<VarDef>\n");
        return a.toString();
    }

    public TrueToken getLBRACK() {
        return LBRACK;
    }

    public TrueToken getIdent() {
        return Ident;
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    public InitVal getInitVal() {
        return initVal;
    }
}
