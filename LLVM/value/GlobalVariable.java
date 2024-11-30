package LLVM.value;

import LLVM.Type;
import LLVM.User;
import LLVM.type.CharType;
import LLVM.type.IntegerType;
import MIPS.Instruction.Memory.Li;
import MIPS.Instruction.Memory.Sb;
import MIPS.Instruction.Memory.Sw;
import MIPS.Instruction.MipsInstruction;
import MIPS.MIPSGenerator;
import MIPS.symbol.Symbol;
import MIPS.value.MipsGlobalVariable;

import java.util.ArrayList;

public class GlobalVariable extends User {
    protected Boolean isConst;//是否是常量

    protected int dimensions;
    protected ArrayList<Integer> valueList;
    protected Boolean isArray;

    /*
        初始化全局变量
     */
    public GlobalVariable(String name, Type type,int value,boolean isConst) {
        super(name, type);
        this.valueList=new ArrayList<>();
        this.valueList.add(value);
        this.isArray=false;
        this.isConst=isConst;
    }

    /*
        初始化全局数组
     */
    public GlobalVariable(String name, Type type,ArrayList<Integer> valueList,int dimensions,boolean isConst) {
        super(name, type);
        this.valueList=valueList;
        this.isArray=true;
        this.dimensions=dimensions;
        this.isConst=isConst;
    }


    public String toString(){
        if(!isArray){
            if(isConst){
                return name+"=dso_local constant "+type.toString()+" "+valueList.get(0);
            }else{
                return name+"=dso_local global "+type.toString()+" "+valueList.get(0);
            }
        }else{
            if(isConst){
                return name+"=dso_local constant "+type.toString(valueList);
            }else{
                return name+"=dso_local global "+type.toString(valueList);
            }
        }
    }

    /**
     * @description: 生成全局常量
     **/
    public MipsGlobalVariable generateMipsGlobalVariable() {
        if(isConst||isArray){
            if(isArray&&type.getType() instanceof CharType&&name.substring(0,name.length()-1).equals("@.str.")) {
                //is str
                String strConst=getStr(valueList);
                return new MipsGlobalVariable(name.substring(1),strConst);
            }else if(isArray&&type.getType() instanceof CharType){
                //TODO:添加array到符号表中
                Symbol symbol=new Symbol(name.substring(1),true,dimensions,true);
                MIPSGenerator.mipsModule.addGlobalSymbol(name,symbol);
                return new MipsGlobalVariable(name.substring(1),new CharType(),valueList,dimensions);
            }else if(isArray&&type.getType() instanceof IntegerType){
                //TODO:添加array到符号表中
                Symbol symbol=new Symbol(name.substring(1),false,dimensions,true);
                MIPSGenerator.mipsModule.addGlobalSymbol(name,symbol);
                return new MipsGlobalVariable(name.substring(1),new IntegerType(),valueList,dimensions);
            }else{
                Symbol symbol=new Symbol(name,type instanceof CharType?true:false,true);
                MIPSGenerator.mipsModule.addGlobalSymbol(name,symbol);
                return new MipsGlobalVariable(name.substring(1),type,valueList,dimensions);
            }
        }
        return null;
    }

    public String getStr(ArrayList<Integer> valueList) {
        StringBuilder a=new StringBuilder();
        a.append("\"");
        for(Integer i:valueList){
            a.append(AsciiToCharacter(i));
        }
        a.append("\"");
        return a.toString();
    }

    public String AsciiToCharacter(int ascii){
        return switch (ascii) {
            case 0 -> "";
            case 7 -> "\\a";
            case 8 -> "\\b";
            case 9 -> "\\t";
            case 10 -> "\\n";
            case 11 -> "\\v";
            case 12 -> "\\f";
            case 34 -> "\\\"";
            case 39 -> "\\'";
            case 92 -> "\\\\";
            default -> String.valueOf((char) ascii);
        };
    }

    /**
     * @description: 生成全局变量
     * @date: 2024/11/22 21:23
     **/
    public ArrayList<MipsInstruction> generateMipsGlobal() {
        if(isConst||isArray){
            return null;
        }
        Symbol symbol=new Symbol(name,type instanceof CharType?true:false);
        MIPSGenerator.mipsModule.addGlobalSymbol(name,symbol);
        int offset=symbol.getGp();
        ArrayList<MipsInstruction> list=new ArrayList<>();
        list.add(new Li(valueList.get(0),false));
        if(type instanceof CharType){
            list.add(new Sb("$v1",offset,"$gp"));
        }else{
            list.add(new Sw("$v1",offset,"$gp"));
        }
        return list;
    }
}
