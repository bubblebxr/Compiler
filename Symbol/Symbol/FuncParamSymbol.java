package Symbol.Symbol;

public class FuncParamSymbol extends Symbol{
    protected int dimension;
    protected Boolean isLoad;

    public FuncParamSymbol(SymbolType type, String Ident,int dimension) {
        super(type, Ident);
        this.dimension=dimension;
        this.isLoad=false;
    }

    public FuncParamSymbol(SymbolType type, String Ident,int dimension,String id) {
        super(type, Ident,id);
        this.dimension=dimension;
        this.isLoad=false;
    }

    public int getDimension() {
        return dimension;
    }

    public void setLoad(){
        this.isLoad=true;
    }

    public Boolean getIsLoad(){
        return isLoad;
    }
}
