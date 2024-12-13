package midend.value;

import backend.value.MipsConstGlobalVariable;
import backend.value.MipsGlobalVariable;
import backend.value.MipsStr;
import midend.Type;
import midend.User;
import midend.type.CharType;

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
     * @description: 生成print的str
     * @date: 2024/12/3 17:06
     **/
    public MipsStr genMipsStr() {
        if(isArray&& name.startsWith("@.str.")){
            String strConst=getStr(valueList);
            return new MipsStr(name.substring(1),strConst);
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

    public MipsConstGlobalVariable genMipsConstGlobal() {
        if(isConst&&!name.startsWith("@.str.")){
            if(isArray){
                // 全局常量数组
                return new MipsConstGlobalVariable(name, true,valueList,type.getType() instanceof CharType,dimensions);
            }else{
                // 全局常量变量
                return new MipsConstGlobalVariable(name,false,valueList,type instanceof CharType,dimensions);
            }
        }
        return null;
    }

    public MipsGlobalVariable genMipsGlobalVariable() {
        if(!isConst){
            if(isArray){
                // 全局变量数组
                return new MipsGlobalVariable(name,type.getType() instanceof CharType,valueList,dimensions);
            }else{
                // 全局变量
                return new MipsGlobalVariable(name,type instanceof CharType,valueList,dimensions);
            }
        }
        return null;
    }
}
