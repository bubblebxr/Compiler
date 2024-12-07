package midend.value.Instruction.ConversionType;


import backend.Instruction.MipsInstruction;
import backend.MipsGenerator;
import backend.reg.MipsMem;
import midend.Type;
import midend.Value;
import midend.value.Instruction.Instruction;

import java.util.ArrayList;

import static backend.MipsGenerator.putGlobalRel;

/**
 * @className: Zext
 * @author: bxr
 * @date: 2024/11/11 21:20
 * @description: char 类型值赋给 int 类型变量
 */

public class Zext extends Instruction {

    public Zext(String name, Type type) {
        super(name, type);
    }

    public Zext(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }

    @Override
    public String toString(){
        return name +
                " = " +
                "zext " +
                operators.get(0).toString() +
                " to " +
                operators.get(1).getType().toString() +
                "\n";
    }

    @Override
    public ArrayList<MipsInstruction> genMips() {
        ArrayList<MipsInstruction> temp=new ArrayList<>();
        MipsMem mipsMem= MipsGenerator.getRel(operators.get(0).getName());
        if(mipsMem!=null){
            putGlobalRel(name,mipsMem);
        }
        return temp;
    }
}
