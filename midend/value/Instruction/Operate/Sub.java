package midend.value.Instruction.Operate;


import backend.Instruction.Memory.*;
import backend.Instruction.MipsInstruction;
import backend.Instruction.Operate.Addi;
import backend.Instruction.Operate.Addu;
import backend.Instruction.Operate.Move;
import backend.Instruction.Operate.Subu;
import backend.MipsGenerator;
import backend.reg.MipsMem;
import midend.Type;
import midend.Value;
import midend.type.CharType;
import midend.value.Instruction.Instruction;

import java.util.ArrayList;

import static backend.MipsGenerator.*;
import static backend.MipsGenerator.returnReg;

/**
 * @className: Sub
 * @author: bxr
 * @date: 2024/11/7 20:37
 * @description: 减
 */

public class Sub extends Instruction {

    public Sub(String name, Type type) {
        super(name, type);
    }

    public Sub(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append(name);
        a.append(" = sub ");
        a.append(type.toString());
        a.append(" ");
        for(int i=0;i<operators.size();i++){
            a.append(operators.get(i).getName());
            if(i<operators.size()-1){
                a.append(",");
            }
        }
        a.append("\n");
        return a.toString();
    }

    @Override
    public ArrayList<MipsInstruction> genMips() {
        ArrayList<MipsInstruction> list = new ArrayList<>();
        String label1="",label2="";
        if(operators.get(0).getName().equals("0")){
            label1="$zero";
        }else if(operators.get(0).getName().charAt(0)!='%'){
            list.add(new Li(Integer.parseInt(operators.get(0).getName()),false));
            list.add(new Move("$v0","$v1"));
            label1="$v0";
        }else{
            MipsMem mipsMem=getRel(operators.get(0).getName());
            if(mipsMem!=null){
                if(mipsMem.isInReg){
                    label1=mipsMem.RegName;
                }else{
                    if(type instanceof CharType){
                        list.add(new Lb("$v0",mipsMem.offset,"$sp"));
                    }else{
                        list.add(new Lw("$v0",mipsMem.offset,"$sp"));
                    }
                    label1="$v0";
                }
            }
        }
        if(operators.get(1).getName().equals("0")){
            label2="$zero";
        }else if(operators.get(1).getName().charAt(0)!='%'){
            list.add(new Li(Integer.parseInt(operators.get(1).getName()),false));
            label2="$v1";
        }else{
            MipsMem mipsMem= MipsGenerator.getRel(operators.get(1).getName());
            if(mipsMem!=null){
                if(mipsMem.isInReg){
                    label2=mipsMem.RegName;
                }else{
                    if(type instanceof CharType){
                        list.add(new Lb("$v1",mipsMem.offset,"$sp"));
                    }else{
                        list.add(new Lw("$v1",mipsMem.offset,"$sp"));
                    }
                    label2="$v1";
                }
            }
        }

        MipsMem reg=getEmptyLocalReg(type instanceof CharType);
        if(!reg.isInReg)reg.RegName="$t0";
        list.add(new Subu(reg.RegName,label1,label2));
        if(!reg.isInReg){
            if(type instanceof CharType){
                list.add(new Sb("$t0",reg.offset,"$sp"));
            }else{
                list.add(new Sw("$t0",reg.offset,"$sp"));
            }
        }
        putLocalRel(name,reg);
        returnReg(label1);
        returnReg(label2);

        return list;
    }
}
