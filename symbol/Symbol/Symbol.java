package symbol.Symbol;

import java.util.ArrayList;
import java.util.List;

public class Symbol {
    protected SymbolType type;
    protected String Ident;
    protected String id;//局部变量的id

    public Symbol(SymbolType type,String Ident){
        this.type=type;
        this.Ident=Ident;
    }

    public Symbol(SymbolType type,String Ident,String id){
        this.type=type;
        this.Ident=Ident;
        this.id=id;
    }

    public void setId(String id){
        this.id=id;
    }

    public String getId() {
        return id;
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

    public int getValue(){
        return 0;
    }

    public int getValue(int index){
        return 0;
    }

    public int getElementNum() {
        return 0;
    }

    public void setLoad() {
    }

    public void setValue(ArrayList<Integer> value){}

    public void addValue(int value){    }

    public int getDimension() {
        return 0;
    }
}
