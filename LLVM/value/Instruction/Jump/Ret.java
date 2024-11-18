package LLVM.value.Instruction.Jump;


import LLVM.Type;
import LLVM.Value;
import LLVM.value.Instruction.Instruction;

import java.util.ArrayList;

/**
 * @className: Ret
 * @author: bxr
 * @date: 2024/11/11 19:17
 * @description: return
 */

public class Ret extends Instruction {
    public Ret(String name, Type type) {
        super(name, type);
    }

    public Ret(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append("ret ");
        if(!operators.isEmpty()){
            a.append(operators.get(0).toString());
        }else{
            a.append("void");
        }
        a.append("\n");
        return a.toString();
    }
}
