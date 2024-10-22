package Visitor.Symbol;

public class FuncParamSymbol extends Symbol{
    protected int dimension;

    public FuncParamSymbol(SymbolType type, String Ident,int dimension) {
        super(type, Ident);
        this.dimension=dimension;
    }

    public int getDimension() {
        return dimension;
    }
}
