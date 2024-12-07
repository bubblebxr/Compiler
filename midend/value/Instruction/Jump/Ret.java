package midend.value.Instruction.Jump;


import backend.Instruction.Jump.Jal;
import backend.Instruction.Jump.Jr;
import backend.Instruction.Memory.Lb;
import backend.Instruction.Memory.Li;
import backend.Instruction.Memory.Lw;
import backend.Instruction.MipsInstruction;
import backend.Instruction.Operate.Compare;
import backend.Instruction.Operate.CompareType;
import backend.Instruction.Operate.Move;
import backend.MipsGenerator;
import backend.reg.MipsMem;
import midend.Type;
import midend.Value;
import midend.type.CharType;
import midend.value.Instruction.Instruction;

import java.util.ArrayList;

import static backend.MipsGenerator.checkAtMain;

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

    @Override
    public ArrayList<MipsInstruction> genMips() {
        ArrayList<MipsInstruction> temp=new ArrayList<>();
        if(checkAtMain()){
            //如果是main函数，不处理返回值，直接增加syscall
            temp.add(new Li(10,true));
            return temp;
        }
        if(!operators.isEmpty()){
            String reg="";
            if(operators.get(0).getName().equals("0")){
                reg="$zero";
                temp.add(new Move("$v0",reg));
            }else if(operators.get(0).getName().charAt(0)!='%'){
                temp.add(new Li(Integer.parseInt(operators.get(0).getName()),false));
                reg="$v1";
                temp.add(new Move("$v0",reg));
            }else{
                MipsMem mipsMem= MipsGenerator.getRel(operators.get(0).getName());
                if(mipsMem!=null){
                    if(mipsMem.isInReg){
                        temp.add(new Move("$v0",mipsMem.RegName));
                    }else{
                        if(type instanceof CharType){
                            temp.add(new Lb("$v0",mipsMem.offset,"$sp"));
                        }else{
                            temp.add(new Lw("$v0",mipsMem.offset,"$sp"));
                        }
                    }
                }
            }
        }
        temp.add(new Jr("$ra"));
        return temp;
    }
}
