package midend.value.Instruction.Memory;


import backend.Instruction.Memory.*;
import backend.Instruction.MipsInstruction;
import backend.Instruction.Operate.Addi;
import backend.Instruction.Operate.Addu;
import backend.Instruction.Operate.Sll;
import backend.reg.MipsMem;
import midend.Type;
import midend.Value;
import midend.type.CharType;
import midend.value.Instruction.Instruction;

import java.util.ArrayList;

import static backend.MipsGenerator.*;

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

    @Override
    public ArrayList<MipsInstruction> genMips() {
        ArrayList<MipsInstruction> list=new ArrayList<>();
        if(operators.get(0).getName().startsWith("@.str.")){
            list.add(new La("$a0",operators.get(0).getName().substring(1)));
        }else{
            // 真正的数组
            String label1="",label2="";
            MipsMem mipsMem1=getRel(operators.get(0).getName());
            if(mipsMem1!=null){
                if(mipsMem1.isInReg){
                    label1=mipsMem1.RegName;
                }else{
                    if(mipsMem1.offset!=0){
                        list.add(new Addi("$v0","$sp",String.valueOf(mipsMem1.offset)));
                        label1="$v0";
                    }else{
                        label1="$sp";
                    }
                }
            }else{
                //TODO: 如果是全局常量数组
                // 是全局数组
                list.add(new La("$v0",operators.get(0).getName().substring(1)));
                label1="$v0";
            }
            MipsMem reg=getEmptyLocalReg(type.getType() instanceof CharType);
            reg.isPointer=true;
            if(!reg.isInReg)reg.RegName="$t0";
            String temp=operators.get(1).getName();
            if(temp.equals("0")){
                temp=operators.get(2).getName();
            }
            if(temp.charAt(0)!='%'){
                //offset是数字
                list.add(new Addi(reg.RegName,label1,temp));
            }else{
                MipsMem mipsMem2=getRel(temp);
                if(mipsMem2!=null){
                    if(mipsMem2.isInReg){
                        label2=mipsMem2.RegName;
                    }else{
                        if(type.getType() instanceof CharType){
                            list.add(new Lb("$v1",mipsMem2.offset,"$sp"));
                        }else{
                            list.add(new Lw("$v1",mipsMem2.offset,"$sp"));
                        }
                        label2="$v1";
                    }
                    // 计算出数组的地址，如果是int，需要现将offset*4
                    if(!(type.getType() instanceof CharType)){
                        list.add(new Sll(label2,label2,"2"));
                    }
                    list.add(new Addu(reg.RegName,label1,label2));
                }
            }
            if(!reg.isInReg){
                if(type.getType() instanceof CharType){
                    list.add(new Sb(reg.RegName,reg.offset,"$sp"));
                }else{
                    list.add(new Sw(reg.RegName,reg.offset,"$sp"));
                }
            }
            putLocalRel(name,reg);
        }
        return list;
    }
}
