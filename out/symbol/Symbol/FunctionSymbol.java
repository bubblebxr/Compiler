package symbol.Symbol;

import java.util.ArrayList;
import java.util.List;

public class FunctionSymbol extends Symbol {
    protected List<FuncParamSymbol> FuncParams;

    public FunctionSymbol(SymbolType type, String Ident) {
        super(type, Ident);
        this.FuncParams=new ArrayList<>();
    }

    public void addFuncParams(FuncParamSymbol funcParamSymbol){
        FuncParams.add(funcParamSymbol);
    }

    public List<FuncParamSymbol> getFuncParams() {
        return FuncParams;
    }
}
