package backend.value;


import java.util.ArrayList;

/**
 * @className: mipsGlobalVariable
 * @author: bxr
 * @date: 2024/11/21 15:14
 * @description: mips module 中的全局变量
 */

public class MipsGlobalVariable {
    protected String name; //调用时的id
    protected Boolean isChar;  //类型：只有int或char
    protected ArrayList<Integer> valueList;  //初始值
    protected int elementNum;


    public MipsGlobalVariable(String name,Boolean isChar,ArrayList<Integer> valueList,int elementNum){
        this.name=name.substring(1);
        this.isChar=isChar;
        this.valueList=valueList;
        this.elementNum=elementNum;
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder(name+": ");
        if(elementNum!=0){
            if(valueList.isEmpty()){
                //.space 12
                int spaceNum=isChar?1:4*elementNum;
                a.append(".space ").append(spaceNum);
            }else{
                a.append(isChar?".byte ":".word ");
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
            a.append(isChar?".byte ":".word ").append(valueList.get(0));
        }
        return a.toString();
    }

    public String getName() {
        return name;
    }

    public int getGlobalVariableValue(int index){
        return valueList.get(index);
    }
}
