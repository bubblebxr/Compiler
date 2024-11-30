package LLVM.value.Instruction.Memory;


import LLVM.Type;
import LLVM.Value;
import LLVM.type.ArrayType;
import LLVM.value.Instruction.Instruction;
import MIPS.Instruction.Jump.J;
import MIPS.Instruction.Memory.La;
import MIPS.Instruction.MipsInstruction;
import MIPS.Instruction.Operate.Compare;
import MIPS.Instruction.Operate.CompareType;

import java.util.ArrayList;

/**
 * @className: Getelementptr
 * @author: bxr
 * @date: 2024/11/8 17:32
 * @description:
 */

public class Getelementptr extends Instruction {

    public Getelementptr(String name, Type type) {
        super(name, type);
    }

    public Getelementptr(String name, Type type, ArrayList<Value> operators){
        super(name,type,operators);
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        if(name!=null){
            a.append(name);
            a.append(" = ");
        }
        a.append("getelementptr inbounds ");
        a.append(type.toString());
        a.append(",");
        for(int i=0;i<operators.size();i++){
            a.append(operators.get(i).toString());
            if(i<operators.size()-1){
                a.append(",");
            }
        }
        if(name!=null){
            a.append("\n");
        }
        return a.toString();
    }

    public ArrayList<MipsInstruction> generateMips() {
        ArrayList<MipsInstruction> list=new ArrayList<>();
        if(operators.get(0).getName().startsWith("@.str.")){
            list.add(new La("$a0",operators.get(0).getName().substring(1)));
        }
        return list;
    }
}
