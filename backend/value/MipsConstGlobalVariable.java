package backend.value;


import java.util.ArrayList;

/**
 * @className: MipsConstGlobalVariable
 * @author: bxr
 * @date: 2024/12/3 17:44
 * @description: 全局常量
 */

public class MipsConstGlobalVariable {
    protected Boolean isArray;
    protected ArrayList<Integer> valueList;
    protected String name;
    protected Boolean isChar;
    protected int elementNum;

    public MipsConstGlobalVariable(String name,Boolean isArray,ArrayList<Integer> valueList,Boolean isChar,int elementNum){
        this.name=name;
        this.isArray=isArray;
        this.valueList=valueList;
        this.isChar=isChar;
        this.elementNum=elementNum;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        return a.toString();
    }

    /**
     * @description: 获取变量的值
     * @date: 2024/12/5 11:12
     **/
    public int getFirstValue(){
        return valueList.get(0);
    }
}
