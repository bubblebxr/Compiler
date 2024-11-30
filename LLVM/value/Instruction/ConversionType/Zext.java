package LLVM.value.Instruction.ConversionType;


import LLVM.Type;
import LLVM.Value;
import LLVM.value.Instruction.Instruction;
import MIPS.Instruction.MipsInstruction;
import MIPS.MIPSGenerator;

import java.util.ArrayList;

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

    public ArrayList<MipsInstruction> generateMips() {
        if(MIPSGenerator.irToMips.containsKey(operators.get(0).getName())){
            String mipsReg=MIPSGenerator.irToMips.get(operators.get(0).getName());
            MIPSGenerator.irToMips.remove(operators.get(0).getName());
            MIPSGenerator.irToMips.put(name,mipsReg);
        }
        return new ArrayList<>();
    }
}
