package LLVM.value;

import LLVM.Type;
import LLVM.User;

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
}
