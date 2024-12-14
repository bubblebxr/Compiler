package midend.value.Instruction.Memory;


import backend.Instruction.*;
import backend.reg.MipsMem;
import midend.Type;
import midend.type.ArrayType;
import midend.type.CharType;
import midend.type.PointerType;
import midend.value.Instruction.Instruction;
import java.util.ArrayList;
import static backend.MipsGenerator.*;

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

    @Override
    public String toString(){
        return name+" = alloca "+type.toString()+"\n";
    }

    @Override
    public ArrayList<MipsInstruction> genMips() {
        ArrayList<MipsInstruction> temp=new ArrayList<>();
        if(elementNum==0&& !(type instanceof PointerType)){
            // 变量
            MipsMem reg= getEmptyGlobalReg(type instanceof CharType);
            putGlobalRel(name,reg);
        }else{
            //数组
            if(type instanceof ArrayType){
                MipsMem reg=getArrayMem(type.getType() instanceof CharType,elementNum);
                putGlobalRel(name,reg);
            }else{
                // 传的数组型参数
                MipsMem reg= getEmptyGlobalReg(false);
                reg.isPointer=true;
                putGlobalRel(name,reg);
            }
        }
        return temp;
    }
}
