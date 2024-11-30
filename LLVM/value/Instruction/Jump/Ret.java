package LLVM.value.Instruction.Jump;


import LLVM.Type;
import LLVM.Value;
import LLVM.value.Instruction.Instruction;
import MIPS.Instruction.Jump.J;
import MIPS.Instruction.Jump.Jr;
import MIPS.Instruction.Memory.Li;
import MIPS.Instruction.MipsInstruction;
import MIPS.Instruction.Operate.*;
import MIPS.MIPSGenerator;
import MIPS.symbol.Symbol;

import java.util.ArrayList;

/**
 * @className: Ret
 * @author: bxr
 * @date: 2024/11/11 19:17
 * @description: return
 */

public class Ret extends Instruction {
    protected Boolean isVoid;
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
            isVoid=false;
        }else{
            a.append("void");
            isVoid=true;
        }
        a.append("\n");
        return a.toString();
    }

    public ArrayList<MipsInstruction> generateMips() {
        ArrayList<MipsInstruction> list=new ArrayList<>();
        if(Symbol.spPresentNum!=0){
            list.add(new Addu("$sp","$sp", String.valueOf(Symbol.spPresentNum)));
        }
        if(isVoid){
            list.add(new Jr("$ra"));
        }else{
            //TODO:将参数存储在$v0中
            String reg = "";
            if(operators.get(0).getName().equals("0")){
                reg="$zero";
            }else if(operators.get(0).getName().charAt(0)!='%'){
                list.add(new Li(Integer.parseInt(operators.get(0).getName()),false));
                reg="$v1";
            }else{
                if(MIPSGenerator.irToMips.containsKey(operators.get(0).getName())){
                    reg= MIPSGenerator.irToMips.get(operators.get(0).getName());
                }
            }
            list.add(new Move("$v0",reg));
            list.add(new Jr("$ra"));
        }
        return list;
    }
}
