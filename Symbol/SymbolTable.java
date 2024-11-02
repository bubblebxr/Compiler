package Symbol;


import Symbol.Symbol.Symbol;

import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTable {
    protected int id; 		// 当前符号表的id。
    protected int fatherId; 	// 外层符号表的id。
    protected Boolean isInFor;
    protected Map<String, Symbol> directory;

    public SymbolTable(int id,int fatherId){
        this.id=id;
        this.fatherId=fatherId;
        isInFor=false;
        this.directory=new LinkedHashMap<>();
    }

    public void setIsInForTrue(){
        this.isInFor=true;
    }

    public void addDirectory(String Ident,Symbol symbol){
        directory.put(Ident,symbol);
    }

    public Boolean getNameOverLoad(String Ident){
        return directory.containsKey(Ident);
    }

    public Map<String, Symbol> getDirectory() {
        return directory;
    }

    public int getFatherId() {
        return fatherId;
    }
}
