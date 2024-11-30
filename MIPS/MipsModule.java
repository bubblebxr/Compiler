package MIPS;


import MIPS.Instruction.MipsInstruction;
import MIPS.symbol.MipsSymbolTable;
import MIPS.value.MipsFunction;
import MIPS.value.MipsGlobalVariable;
import MIPS.symbol.Symbol;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @className: mipsModule
 * @author: bxr
 * @date: 2024/11/21 15:18
 * @description: mips module
 */

public class MipsModule {

    protected ArrayList<MipsGlobalVariable> globalConstVariableList;
    protected ArrayList<MipsInstruction> globalVariableList;
    protected ArrayList<MipsFunction> functionList;
    protected Map<String, MipsSymbolTable> symbolTables;
    public static String curFuncName="Global";

    public MipsModule(){
        this.globalConstVariableList =new ArrayList<>();
        this.globalVariableList=new ArrayList<>();
        this.functionList=new ArrayList<>();
        this.symbolTables=new LinkedHashMap<>();
        this.symbolTables.put("Global",new MipsSymbolTable());
    }

    public void addMipsGlobalVariableList(MipsGlobalVariable mipsGlobalVariable){
        if(mipsGlobalVariable!=null){
            this.globalConstVariableList.add(mipsGlobalVariable);
        }
    }

    public void addFunctionList(MipsFunction mipsFunction){
        this.functionList.add(mipsFunction);
    }

    public void addSymbolTable(String name){
        curFuncName=name;
        this.symbolTables.put(name,new MipsSymbolTable());
    }

    public void addMipsGlobalList(ArrayList<MipsInstruction> instruction){
        if(instruction!=null){
            this.globalVariableList.addAll(instruction);
        }
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append(".data\n");
        for(MipsGlobalVariable mipsGlobalVariable: globalConstVariableList){
            a.append("    ");
            a.append(mipsGlobalVariable.toString());
        }
        a.append(".text\n");
        for(MipsInstruction instruction:globalVariableList){
            a.append(instruction.toString()).append("\n");
        }
        a.append("\n");
        for(int i=functionList.size()-1;i>=0;i--){
            a.append(functionList.get(i).toString());
        }
        return a.toString();
    }

    public void addGlobalSymbol(String name,Symbol symbol){
        symbolTables.get("Global").addSymbol(name,symbol);
    }

    public void addCurSymbol(String name,Symbol symbol){
        symbolTables.get(curFuncName).addSymbol(name,symbol);
    }

    public MipsSymbolTable getCurTable(){
        return this.symbolTables.get(curFuncName);
    }

    public Symbol getSymbol(String name){
        if(getCurTable().getDirectory().containsKey(name)){
            return getCurTable().getDirectory().get(name);
        }
        return symbolTables.get("Global").getDirectory().get(name);
    }

    public MipsGlobalVariable getGlobalSymbol(String name){
        for (MipsGlobalVariable variable : globalConstVariableList) {
            if (variable.getName().equals(name)) {
                return variable;
            }
        }
        return null;
    }

}
