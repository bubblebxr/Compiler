package llvm.value.Instruction.Memory;


import llvm.Type;
import llvm.value.Instruction.Instruction;

/**
 * @className: alloca
 * @author: bxr
 * @date: 2024/11/7 20:30
 * @description:
 */   

public class Alloca extends Instruction {
    protected int id; //局部变量的id
    protected int elementNum; //如果是数组，那么数组的元素个数

    /*普通变量*/
    public Alloca(String name, Type type) {
        super(name, type);
        this.id=Integer.parseInt(name.substring(1));
        elementNum=0;
    }

    /*数组*/
    public Alloca(String name, Type type,int elementNum) {
        super(name, type);
        this.id=Integer.parseInt(name.substring(1));
        this.elementNum=elementNum;
    }

    public String toString(){
        return name+" = alloca "+type.toString()+"\n";
    }
}
