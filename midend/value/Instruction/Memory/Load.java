package midend.value.Instruction.Memory;


import backend.Instruction.Memory.*;
import backend.Instruction.MipsInstruction;
import backend.Instruction.Operate.Move;
import backend.MipsGenerator;
import backend.reg.MipsMem;
import midend.Type;
import midend.Value;
import midend.type.CharType;
import midend.type.PointerType;
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
        // 如果是要load出来数组传参
        if(operators.get(0).getType().getType() instanceof PointerType){
            MipsMem mipsMem=getRel(operators.get(0).getName());
            if(mipsMem!=null){
                mipsMem.isArrayParam=true;
                putLocalRel(name,mipsMem);
            }
            return temp;
        }
        MipsMem mipsMem=getRel(operators.get(0).getName());
        if(mipsMem!=null){
            // 如果是数组指针则需要从地址中load出来
            if(mipsMem.isPointer!=null&&mipsMem.isPointer){
                // 先获取数组的指针，并取出需要load的元素
                if(!mipsMem.isInReg){
                    // 指向数组的指针不在内存中，需要先load出来
                    temp.add(new Lw("$t0",mipsMem.offset,"$sp"));
                    mipsMem.RegName="$t0";
                }
                if(type instanceof CharType){
                    temp.add(new Lb("$t0",0,mipsMem.RegName));
                }else{
                    temp.add(new Lw("$t0",0,mipsMem.RegName));
                }
                // 为临时变量分配可用的空间并存入其中
                MipsMem reg=getEmptyLocalReg(type instanceof CharType);
                if(reg.isInReg){
                    temp.add(new Move(reg.RegName,"$t0"));
                }else{
                    if(type instanceof CharType){
                        temp.add(new Sb("$t0", reg.offset,"$sp"));
                    }else{
                        temp.add(new Sw("$t0", reg.offset,"$sp"));
                    }
                }
                putLocalRel(name,reg);
            }else{
                putLocalRel(name,mipsMem);
            }
        }else{
            // 从常量中获取
            if(getConstGlobalValue(operators.get(0).getName())!=null){
                temp.add(new Li((long)getConstGlobalValue(operators.get(0).getName()),false));
                MipsMem reg=getEmptyLocalReg(type instanceof CharType);
                if(reg.isInReg){
                    temp.add(new Move(reg.RegName,"$v1"));
                }else{
                    if(type instanceof CharType){
                        temp.add(new Sb("$v1", reg.offset,"$sp"));
                    }else{
                        temp.add(new Sw("$v1", reg.offset,"$sp"));
                    }
                }
                putLocalRel(name,reg);
            }
            // 从全局变量中获取
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
