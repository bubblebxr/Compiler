package frontend.AST;

import frontend.Token.TrueToken;

public class ConstDef {
    protected TrueToken Ident;
    protected TrueToken LBRACK;
    protected ConstExp constExp;
    protected TrueToken RBRACK;
    protected TrueToken ASSIGN;
    protected ConstInitVal constInitVal;
    /*1：普通变量，0：一维数组*/
    protected int Type;

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

    public void setConstInitVal(ConstInitVal constInitVal) {
        this.constInitVal = constInitVal;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getType() {
        return Type;
    }

    public String outputConstDef(){
        StringBuilder a= new StringBuilder();
        a.append(Ident.toString());
        if(LBRACK!=null){
            a.append(LBRACK.toString());
            a.append(constExp.outputConstExp());
            a.append(RBRACK.toString());
        }
        a.append(ASSIGN.toString());
        a.append(constInitVal.outputConstInitVal());
        a.append("<ConstDef>\n");
        return a.toString();
    }
}
