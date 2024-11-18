package Symbol.Symbol;

import LLVM.Value;

import java.util.ArrayList;

public class VariableSymbol extends Symbol {
    protected int dimension;
    protected ArrayList<Integer> value;
    protected int elementNum;

    public VariableSymbol(SymbolType type, String Ident,int dimension){
        super(type,Ident);
        this.dimension=dimension;
        this.value=new ArrayList<>();
    }

    public VariableSymbol(int elementNum,SymbolType type, String Ident,int dimension){
        super(type,Ident);
        this.dimension=dimension;
        this.value=new ArrayList<>();
        this.elementNum=elementNum;
    }


    /**
     * @description: 变量初始化
     */
    public VariableSymbol(SymbolType type, String Ident,int dimension,int value){
        super(type,Ident);
        this.dimension=dimension;
        this.value=new ArrayList<>();
        this.value.add(value);
    }

    public VariableSymbol(int elementNum,SymbolType type, String Ident,int dimension,int value){
        super(type,Ident);
        this.dimension=dimension;
        this.value=new ArrayList<>();
        this.value.add(value);
        this.elementNum=elementNum;
    }

    /**
     * @description: 获得变量值
     */
    public int getValue(){
        return value.get(0);
    }

    /**
     * @description: 获得数组值
     */
    public int getValue(int index){
        return value.get(index);
    }

    public int getElementNum() {
        return elementNum;
    }


}
