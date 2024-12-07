package midend.type;

import midend.Type;

import java.util.ArrayList;
import java.util.List;

public class FunctionType extends Type {
    protected List<Type> paramTypeList;
    protected Type returnType;
    protected Boolean isDeclare;//是否是库函数

    public FunctionType(ArrayList<Type> paramTypeList,Type returnType,Boolean isDeclare){
        this.paramTypeList=paramTypeList;
        this.returnType=returnType;
        this.isDeclare=isDeclare;
    }

    public String toString(String name){
        if(isDeclare){
            return "declare "+returnType.toString()+" "+name+"("+eachToString()+")";
        }else{
            return "define dso_local "+returnType.toString()+" "+name+"("+eachToString()+")";
        }
    }

    public String eachToString(){
        StringBuilder a=new StringBuilder();
        if(isDeclare){
            for(Type type:paramTypeList){
                a.append(type.toString());
            }
        }else{
            for(int i=0;i<paramTypeList.size();i++){
                a.append(paramTypeList.get(i)).append(" %").append(i);
                if(i<paramTypeList.size()-1){
                    a.append(",");
                }
            }
        }
        return a.toString();
    }
}
