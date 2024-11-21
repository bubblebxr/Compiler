package LLVM.value.Instruction.Jump;


import LLVM.Type;
import LLVM.Value;
import LLVM.value.Instruction.Instruction;

import java.util.ArrayList;

/**
 * @className: Br
 * @author: bxr
 * @date: 2024/11/18 9:57
 * @description: 无条件跳转和有条件跳转
 */

public class Br extends Instruction {
    protected String cond;
    protected String label1;
    protected String label2;
    public Br(String name, Type type) {
        super(name, type);
    }

    public Br(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
        this.cond=null;
    }

    /**
     * @description: 无条件跳转
     * @author: bxr
     * @date: 2024/11/18 10:34
     * @param: [name, type, operators]
     * @return: void
     **/
    public Br(String name, Type type,String label1) {
        super(name,type);
        this.cond=null;
        this.label1=label1;
        this.label2=null;
    }

    /**
     * @description: 有条件跳转
     * @author: bxr
     * @date: 2024/11/18 10:34
     * @param: [name, type, cond：条件, operators]
     * @return: void
     **/
    public Br(String name, Type type,String cond,String label1,String label2) {
        super(name,type);
        this.cond=cond;
        this.label1=label1;
        this.label2=label2;
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append("br ");
        if(cond!=null){
            a.append(type.toString()).append(" ").append(cond).append(",");
        }
        a.append("label ").append(label1);
        if(label2!=null)a.append(",label ").append(label2);
        a.append("\n");
        return a.toString();
    }

    public String getLabel1(){
        return label1;
    }

    public void setLabel1(String name){
        label1=name;
    }

    public String getLabel2(){
        return label2;
    }

    public void setLabel2(String name){
        label2=name;
    }
}
