package midend.value.Instruction.ConversionType;


import backend.Instruction.Memory.Sb;
import backend.Instruction.Memory.Sw;
import backend.Instruction.MipsInstruction;
import backend.Instruction.Operate.Move;
import backend.MipsGenerator;
import backend.reg.MipsMem;
import midend.Type;
import midend.Value;
import midend.type.CharType;
import midend.value.Instruction.Instruction;

import java.util.ArrayList;
import static backend.MipsGenerator.putGlobalRel;

/**
 * @className: Trunc
 * @author: bxr
 * @date: 2024/11/11 21:21
 * @description: int 类型值赋给 char 类型变量
 */

public class Trunc extends Instruction {

    public Trunc(String name, Type type) {
        super(name, type);
    }

    public Trunc(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }

    @Override
    public String toString(){
        return name +
                " = " +
                "trunc " +
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
