package Symbol.Symbol;

import java.util.List;

public class Symbol {
    protected SymbolType type;
    protected String Ident;

    public Symbol(SymbolType type,String Ident){
        this.type=type;
        this.Ident=Ident;
    }

    public void addFuncParams(FuncParamSymbol funcParamSymbol){

    }

    public SymbolType getType() {
        return type;
    }

    public String getIdent() {
        return Ident;
    }

    public List<FuncParamSymbol> getFuncParams(){
        return null;
    }

    @Override
    public String toString(){
        return Ident+" "+type.toString()+"\n";
    }
}
