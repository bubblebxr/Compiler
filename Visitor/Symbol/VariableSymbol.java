package Visitor.Symbol;

public class VariableSymbol extends Symbol {
    protected int dimension;

    public VariableSymbol(SymbolType type, String Ident, int dimension){
        super(type,Ident);
        this.dimension=dimension;
    }
}
