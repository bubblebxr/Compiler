package midend.value.Instruction.Memory;


import backend.Instruction.Memory.*;
import backend.Instruction.MipsInstruction;
import backend.Instruction.Operate.Move;
import backend.MipsGenerator;
import backend.reg.MipsMem;
import midend.Type;
import midend.Value;
import midend.type.CharType;
import midend.value.Instruction.Instruction;

import java.util.ArrayList;

import static backend.MipsGenerator.*;

/**
 * @className: load
 * @author: bxr
 * @date: 2024/11/7 20:31
 * @description: 从寄存器中加载变量
 */

public class Load extends Instruction {

    public Load(String name, Type type) {
        super(name, type);
    }

    public Load(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append(name);
        a.append(" = load ");
        a.append(type.toString());
        a.append(",");
        a.append(operators.get(0).toString());
        a.append("\n");
        return a.toString();
    }

    @Override
    public ArrayList<MipsInstruction> genMips() {
        ArrayList<MipsInstruction> temp=new ArrayList<>();
        MipsMem mipsMem=getRel(operators.get(0).getName());
        if(mipsMem!=null){
            putLocalRel(name,mipsMem);
        }else{
            if(getConstGlobalValue(operators.get(0).getName())!=null){
                temp.add(new Li(getConstGlobalValue(operators.get(0).getName()),false));
                MipsMem reg=getEmptyLocalReg(type instanceof CharType);
                temp.add(new Move(reg.RegName,"$v1"));
                putLocalRel(name,reg);
            }
            if(getGlobalVariableValue(operators.get(0).getName(),0)!=null){
                temp.add(new La("$t0",operators.get(0).getName().substring(1)));
                MipsMem reg=getEmptyLocalReg(type instanceof CharType);
                if(type instanceof CharType){
                    temp.add(new Lb("$v0",0,"$t0"));
                }else{
                    temp.add(new Lw("$v0",0,"$t0"));
                }
                if(reg.isInReg){
                    temp.add(new Move(reg.RegName,"$v0"));
                }else{
                    if(type instanceof CharType){
                        temp.add(new Sb("$v0",reg.offset,"$sp"));
                    }else{
                        temp.add(new Sw("$v0",reg.offset,"$sp"));
                    }
                }
                putLocalRel(name,reg);
            }
        }
        return temp;
    }

}
