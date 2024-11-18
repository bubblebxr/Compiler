package LLVM.value.Instruction.Jump;


import LLVM.Type;
import LLVM.Value;
import LLVM.type.CharType;
import LLVM.type.PointerType;
import LLVM.value.Instruction.Instruction;
import LLVM.value.Instruction.Memory.Getelementptr;

import java.util.ArrayList;

/**
 * @className: Cal
 * @author: bxr
 * @date: 2024/11/11 19:08
 * @description:
 */

public class Call extends Instruction {
    protected String functionName;

    public Call(String name, Type type,String functionName,ArrayList<Value> operators) {
        super(name, type,operators);
        this.functionName=functionName;
    }

    public Call(String name, Type type,String functionName) {
        super(name, type);
        this.functionName=functionName;
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
       if(name!=null){
           a.append(name);
           a.append(" = ");
       }
        a.append("call ");
        a.append(type.toString());
        a.append(" ");
        a.append(functionName);
        a.append("(");
        for(int i=0;i<operators.size();i++){
            a.append(operators.get(i).toString());
            if(i<operators.size()-1){
                a.append(",");
            }
        }
        a.append(")\n");
        return a.toString();
    }
}
