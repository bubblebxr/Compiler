package frontend.AST;

import symbol.Symbol.SymbolType;
import frontend.Token.TrueToken;
import frontend.Token.TrueType;

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

    public TrueToken getIdent() {
        return Ident;
    }

    public SymbolType handleSymbolType() {
        if(BType.getType()== TrueType.INTTK&&LBRACK!=null){
            return SymbolType.IntArray;
        }else if(BType.getType()== TrueType.INTTK&&LBRACK==null){
            return SymbolType.Int;
        }else if(BType.getType()== TrueType.CHARTK&&LBRACK!=null){
            return SymbolType.CharArray;
        }else{
            return SymbolType.Char;
        }
    }
}
