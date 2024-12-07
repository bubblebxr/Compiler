package midend.value.Instruction.Operate;


import backend.Instruction.Memory.*;
import backend.Instruction.MipsInstruction;
import backend.Instruction.Operate.Move;
import backend.Instruction.Operate.Mult;
import backend.Instruction.Operate.Sll;
import backend.MipsGenerator;
import backend.reg.MipsMem;
import midend.Type;
import midend.Value;
import midend.type.CharType;
import midend.value.Instruction.Instruction;

import java.util.ArrayList;

import static backend.MipsGenerator.*;

/**
 * @className: Mul
 * @author: bxr
 * @date: 2024/11/11 18:52
 * @description: 乘
 */

public class Mul extends Instruction {

    public Mul(String name, Type type) {
        super(name, type);
    }

    public Mul(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append(name);
        a.append(" = mul ");
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
        int isSll=0;
        if(operators.get(0).getName().equals("0")){
            MipsMem mipsMem= MipsGenerator.getEmptyLocalReg(type instanceof CharType);
            if(mipsMem.isInReg){
                list.add(new Move(mipsMem.RegName,"$zero"));
            }else{
                if(type instanceof CharType){
                    list.add(new Sb("$zero",mipsMem.offset,"$sp"));
                }else{
                    list.add(new Sw("$zero",mipsMem.offset,"$sp"));
                }
            }
            putLocalRel(name,mipsMem);
            return list;
        }else if(operators.get(0).getName().charAt(0)!='%'){
            if(isPowerOfTwo(Integer.parseInt(operators.get(0).getName()))){
                isSll=1;
                label1= String.valueOf(getPowerOfTwo(Integer.parseInt(operators.get(0).getName())));
            }else{
                list.add(new Li(Integer.parseInt(operators.get(0).getName()),false));
                label1= "$v1";
            }
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
            MipsMem mipsMem= MipsGenerator.getEmptyLocalReg(type instanceof CharType);
            if(mipsMem.isInReg){
                list.add(new Move(mipsMem.RegName,"$zero"));
            }else{
                if(type instanceof CharType){
                    list.add(new Sb("$zero",mipsMem.offset,"$sp"));
                }else{
                    list.add(new Sw("$zero",mipsMem.offset,"$sp"));
                }
            }
            putLocalRel(name,mipsMem);
            returnReg(label1);
            return list;
        }else if(operators.get(1).getName().charAt(0)!='%'){
            if(isSll!=1&&isPowerOfTwo(Integer.parseInt(operators.get(1).getName()))){
                label2= String.valueOf(getPowerOfTwo(Integer.parseInt(operators.get(1).getName())));
                isSll=2;
            }else{
                list.add(new Li(Integer.parseInt(operators.get(1).getName()),false));
                label2="$v1";
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
        if(isSll==0){
            //正常做乘法
            list.add(new Mult(reg.RegName,label1,label2));
        }else if(isSll==1){
            //第一个label是2的次方
            list.add(new Sll(reg.RegName,label2,label1));
        }else{
            //第2个label是2的次方
            list.add(new Sll(reg.RegName,label1,label2));
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
