package frontend.AST;

import frontend.Token.TrueToken;

public class FuncFParam {
    protected TrueToken BType;
    protected TrueToken Ident;
    protected TrueToken LBRACK;
    protected TrueToken RBRACK;

    public void setBType(TrueToken BType) {
        this.BType = BType;
    }

    public void setIdent(TrueToken ident) {
        Ident = ident;
    }

    public void setLBRACK(TrueToken LBRACK) {
        this.LBRACK = LBRACK;
    }

    public void setRBRACK(TrueToken RBRACK) {
        this.RBRACK = RBRACK;
    }

    public String outputFuncFParam() {
        StringBuilder a=new StringBuilder();
        a.append(BType.toString());
        a.append(Ident.toString());
        if(LBRACK!=null){
            a.append(LBRACK.toString());
            a.append(RBRACK.toString());
        }
        a.append("<FuncFParam>\n");
        return a.toString();
    }
}
