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
        StringBuilder a=new StringBuilder(name.substring(1)+": ");
        if(elementNum!=0){
            if(valueList.isEmpty()){
                //.space 12
                int spaceNum=isChar?1:4*elementNum;
                a.append(".space ").append(spaceNum);
                if(isChar){
                    int result=(spaceNum % 4 == 0) ? 0 : 4 - (spaceNum % 4);
                    if(result!=0){
                        a.append("\n    .align ").append(result);
                    }
                }
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
                if(isChar){
                    int result=(elementNum % 4 == 0) ? 0 : 4 - (elementNum % 4);
                    if(result!=0){
                        a.append("\n    .align ").append(result);
                    }
                }
            }
        }else{
            a.append(isChar?".byte ":".word ").append(valueList.get(0));
            if(isChar){
                a.append("\n    .align 3");
            }
        }
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
