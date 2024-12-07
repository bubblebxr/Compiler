package midend.type;

import midend.Type;

import java.util.ArrayList;

public class ArrayType extends Type {
    protected Type type;//int or char
    protected int elementNum;

    public ArrayType(Type type,int elementNum){
        this.type=type;
        this.elementNum=elementNum;
    }

    public String toString(ArrayList<Integer> valueList) {
        StringBuilder a=new StringBuilder("["+elementNum+" x "+type.toString()+"]");
        if(!valueList.isEmpty()){
            a.append("[");
            for(int i=0;i<valueList.size();i++){
                a.append(type.toString()+" "+valueList.get(i));
                if(i<valueList.size()-1){
                    a.append(",");
                }
            }
            for(int i=valueList.size();i<elementNum;i++){
                a.append(",");
                a.append(type.toString()+" "+0);
            }
            a.append("]");
        }else{
            a.append(" zeroinitializer");
        }
        return a.toString();
    }

    public String toString(){
        return "["+elementNum+" x "+type.toString()+"]";
    }

    public Type getType(){
        return type;
    }
}
