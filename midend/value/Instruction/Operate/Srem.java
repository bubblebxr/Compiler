package midend.value.Instruction.Operate;


import backend.Instruction.Memory.*;
import backend.Instruction.MipsInstruction;
import backend.Instruction.Operate.Div;
import backend.Instruction.Operate.Mfhi;
import backend.Instruction.Operate.Mflo;
import backend.Instruction.Operate.Move;
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
 * @className: Srem
 * @author: bxr
 * @date: 2024/11/11 18:53
 * @description: 取余a % b = a - a / b * a
 */

public class Srem extends Instruction {

    public Srem(String name, Type type) {
        super(name, type);
    }

    public Srem(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append(name);
        a.append(" = srem ");
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
        // 两个都是整数的时候优化
        if(operators.get(0).getName().charAt(0)!='%'&&operators.get(1).getName().charAt(0)!='%'){
            //用于看是不是两个除数都是整数
            Long op1=Long.parseLong(operators.get(0).getName());
            Long op2=Long.parseLong(operators.get(1).getName());
            Long result=op1%op2;
            MipsMem reg=getEmptyLocalReg(type instanceof CharType);
            list.add(new Li(result,reg.isInReg?reg.RegName:"$v1"));
            if(!reg.isInReg){
                if(type instanceof CharType){
                    list.add(new Sb("$v1",reg.offset,"$sp"));
                }else{
                    list.add(new Sw("$v1",reg.offset,"$sp"));
                }
            }
            putLocalRel(name,reg);
            return list;
        }
        String label1="",label2="";
        if(operators.get(0).getName().equals("0")){
            label1="$zero";
        }else if(operators.get(0).getName().charAt(0)!='%'){
            list.add(new Li(Long.parseLong(operators.get(0).getName()),"$v0"));
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
            list.add(new Li(Long.parseLong(operators.get(1).getName()),"$v1"));
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
        list.add(new Div(label1,label2));
        list.add(new Mfhi(reg.RegName));
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
