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
 * @className: Sdiv
 * @author: bxr
 * @date: 2024/11/11 18:52
 * @description: 除
 */

public class Sdiv extends Instruction {

    public Sdiv(String name, Type type) {
        super(name, type);
    }

    public Sdiv(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append(name);
        a.append(" = sdiv ");
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
            Long result=op1/op2;
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
        Boolean isSrl=false;
        if(operators.get(0).getName().equals("0")){
            label1="$zero";
            // 0除以任何数仍等于0
            MipsMem reg=getEmptyLocalReg(type instanceof CharType);
            if(reg.isInReg){
                list.add(new Move(reg.RegName,label1));
            }
            else{
                if(type instanceof CharType){
                    list.add(new Sb(label1,reg.offset,"$sp"));
                }else{
                    list.add(new Sw(label1,reg.offset,"$sp"));
                }
            }
            putLocalRel(name,reg);
            return list;
        }else if(operators.get(0).getName().charAt(0)!='%'){
            list.add(new Li(Long.parseLong(operators.get(0).getName()),false));
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
        }else if(operators.get(1).getName().equals("1")){
            // 被除数是1
            MipsMem reg=getEmptyLocalReg(type instanceof CharType);
            if(reg.isInReg){
                list.add(new Move(reg.RegName,label1));
            }
            else{
                if(type instanceof CharType){
                    list.add(new Sb(label1,reg.offset,"$sp"));
                }else{
                    list.add(new Sw(label1,reg.offset,"$sp"));
                }
            }
            putLocalRel(name,reg);
            return list;
        }else if(operators.get(1).getName().charAt(0)!='%'){
            if(isPowerOfTwo(Integer.parseInt(operators.get(1).getName()))){
                isSrl=true;
                label2= String.valueOf(getPowerOfTwo(Integer.parseInt(operators.get(1).getName())));
            }else{
                list.add(new Li(Long.parseLong(operators.get(1).getName()),false));
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
        if(isSrl){
            /*TODO:
                srl $3, $2, 31
                sll $4, $3, n
                subu $4, $4, $3
                addu $4, $4, $2
                sra $1, $4, n
                $3:$t0,$4:$v1
             */
            list.add(new Srl("$t0",label1,"31"));
            list.add(new Sll("$v1","$t0",label2));
            list.add(new Subu("$v1","$v1","$t0"));
            list.add(new Addu("$v1","$v1",label1));
            list.add(new Sra(reg.RegName,"$v1",label2));
        }else{
            list.add(new Div(label1,label2));
            list.add(new Mflo(reg.RegName));
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
