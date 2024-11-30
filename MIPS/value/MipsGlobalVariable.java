package MIPS.value;


import LLVM.Type;
import LLVM.type.CharType;

import java.util.ArrayList;

/**
 * @className: mipsGlobalVariable
 * @author: bxr
 * @date: 2024/11/21 15:14
 * @description: mips module 中的全局变量
 */

public class MipsGlobalVariable {
    protected String name; //调用时的id
    protected Type type;  //类型：只有int或char
    protected ArrayList<Integer> valueList;  //初始值
    protected int elementNum;
    protected Boolean isStr; //是否是字符串，如果是直接输出下面的strConst
    protected String strConst;  //保存字符串

    /**
     * @description: 生成str全局变量
     * @param: [name, strConst]
     * @return: 构造函数
     **/
    public MipsGlobalVariable(String name,String strConst){
        this.name=name;
        this.strConst=strConst;
        this.isStr=true;
    }

    public MipsGlobalVariable(String name,Type type,ArrayList<Integer> valueList,int elementNum){
        this.isStr=false;
        this.name=name;
        this.type=type;
        this.valueList=valueList;
        this.elementNum=elementNum;
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder(name+": ");
        if(isStr){
            a.append(".asciiz ").append(strConst);
        }else if(elementNum!=0){
            if(valueList.isEmpty()){
                //.space 12
                int spaceNum=type instanceof CharType?1:4*elementNum;
                a.append(".space ").append(spaceNum);
            }else{
                a.append(type instanceof CharType?".byte ":".word ");
                for(int i=0;i<valueList.size();i++){
                    a.append(valueList.get(i));
                    if(i<valueList.size()-1){
                        a.append(",");
                    }
                }
                for(int i=valueList.size();i<elementNum;i++){
                    a.append(",0");
                }
            }
        }else{
            a.append(type instanceof CharType?".byte ":".word ").append(valueList.get(0));
        }
        a.append("\n");
        return a.toString();
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
