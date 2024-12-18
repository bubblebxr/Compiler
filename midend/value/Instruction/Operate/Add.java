package midend.value.Instruction.Operate;


import backend.Instruction.Memory.*;
import backend.Instruction.MipsInstruction;
import backend.Instruction.Operate.*;
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
 * @className: Add
 * @author: bxr
 * @date: 2024/11/7 20:37
 * @description: 加
 */

public class Add extends Instruction {

    public Add(String name, Type type) {
        super(name, type);
    }

    public Add(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append(name);
        a.append(" = add ");
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
            Long result=op1+op2;
            MipsMem reg=getEmptyLocalReg(type instanceof CharType);
            list.add(new Li(result,false));
            if(reg.isInReg){
                list.add(new Move(reg.RegName,"$v1"));
            }else{
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
        int isImmediate=0;
        if(operators.get(0).getName().equals("0")){
            label1="$zero";
        }else if(operators.get(0).getName().charAt(0)!='%'){
            label1= operators.get(0).getName();
            isImmediate=1;
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
            if(isImmediate==1){
                list.add(new Li(Long.parseLong(operators.get(1).getName()),false));
                label2="$v1";
            }else{
                label2= operators.get(1).getName();
                isImmediate=2;
            }
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
        if(isImmediate==0){
            //正常做加法
            list.add(new Addu(reg.RegName,label1,label2));
        }else if(isImmediate==1){
            //第一个label是立即数
            list.add(new Addi(reg.RegName,label2,label1));
        }else{
            //第2个label是立即数
            list.add(new Addi(reg.RegName,label1,label2));
        }
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
