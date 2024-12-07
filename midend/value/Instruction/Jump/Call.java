package midend.value.Instruction.Jump;


import backend.Instruction.Jump.Jal;
import backend.Instruction.Memory.*;
import backend.Instruction.MipsInstruction;
import backend.Instruction.Operate.Move;
import backend.reg.MipsMem;
import midend.Type;
import midend.Value;
import midend.type.CharType;
import midend.value.Instruction.Instruction;

import java.util.ArrayList;

import static backend.MipsGenerator.*;

/**
 * @className: Cal
 * @author: bxr
 * @date: 2024/11/11 19:08
 * @description:
 */

public class Call extends Instruction {
    protected String functionName;

    public Call(String name, Type type,String functionName,ArrayList<Value> operators) {
        super(name, type,operators);
        this.functionName=functionName;
    }

    public Call(String name, Type type,String functionName) {
        super(name, type);
        this.functionName=functionName;
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
       if(name!=null){
           a.append(name);
           a.append(" = ");
       }
        a.append("call ");
        a.append(type.toString());
        a.append(" ");
        a.append(functionName);
        a.append("(");
        for(int i=0;i<operators.size();i++){
            a.append(operators.get(i).toString());
            if(i<operators.size()-1){
                a.append(",");
            }
        }
        a.append(")\n");
        return a.toString();
    }

    @Override
    public ArrayList<MipsInstruction> genMips() {
        ArrayList<MipsInstruction> list=new ArrayList<>();
        if(functionName.equals("@putstr")){
            list.add(new Li(4,true));
        }else if(functionName.equals("@getint")){
            list.add(new Li(5,true));
            MipsMem mipsMem=getEmptyLocalReg(false);
            if(mipsMem.isInReg){
                list.add(new Move(mipsMem.RegName,"$v0"));
            }else{
                list.add(new Sw("$v0",mipsMem.offset,"$sp"));
            }
            putLocalRel(name,mipsMem);
        }else if(functionName.equals("@getchar")){
            list.add(new Li(12,true));
            MipsMem mipsMem=getEmptyLocalReg(true);
            if(mipsMem.isInReg){
                list.add(new Move(mipsMem.RegName,"$v0"));
            }else{
                list.add(new Sb("$v0",mipsMem.offset,"$sp"));
            }
            putLocalRel(name,mipsMem);
        }else if(functionName.equals("@putint")){
            MipsMem mipsMem=getRel(operators.get(0).getName());
            if(mipsMem!=null){
                if(mipsMem.isInReg){
                    list.add(new Move("$a0",mipsMem.RegName));
                }else{
                    list.add(new Lw("$a0",mipsMem.offset,"$sp"));
                }
                list.add( new Li(1,true));
                returnReg(mipsMem.RegName);
            }
        }else if(functionName.equals("@putch")){
            MipsMem mipsMem=getRel(operators.get(0).getName());
            if(mipsMem.isInReg){
                list.add(new Move("$a0",mipsMem.RegName));
            }else{
                list.add(new Lb("$a0",mipsMem.offset,"$sp"));
            }
            returnReg(mipsMem.RegName);
            list.add(new Li(11,true));
        }else{
            // 调用自己定义的函数
            list.add(new Sw("$ra",32,"$gp"));
            ArrayList<String> regList=new ArrayList<>();
            regList.add("$a0");
            regList.add("$a1");
            regList.add("$a2");
            regList.add("$a3");
            for(int i=0;i<operators.size();i++){
                String label1 = "";
                if(operators.get(i).getName().equals("0")){
                    label1="$zero";
                    list.add(new Move(regList.get(i),label1));
                }else if(operators.get(i).getName().charAt(0)!='%'){
                    list.add(new Li(Integer.parseInt(operators.get(i).getName()),false));
                    label1= "$v1";
                    list.add(new Move(regList.get(i),label1));
                }else{
                    MipsMem mipsMem=getRel(operators.get(i).getName());
                    if(mipsMem!=null){
                        if(mipsMem.isInReg){
                            label1=mipsMem.RegName;
                            list.add(new Move(regList.get(i),label1));
                        }else{
                            if(type instanceof CharType){
                                list.add(new Lb(regList.get(i),mipsMem.offset,"$sp"));
                            }else{
                                list.add(new Lw(regList.get(i),mipsMem.offset,"$sp"));
                            }
                        }
                    }
                }
                if(i>=4){
                    //TODO:使用栈
                }
            }
            list.addAll(storeGlobal());
            list.add(new Jal(functionName));
            list.add(new Lw("$ra",32,"$gp"));
            list.addAll(loadGlobal());
;            if(name!=null){
                MipsMem mipsMem=getEmptyLocalReg(type instanceof CharType);
                if(mipsMem.isInReg){
                    list.add(new Move(mipsMem.RegName,"$v0"));
                }else{
                    if(type instanceof CharType){
                        list.add(new Sb("$v0",mipsMem.offset,"$sp"));
                    }else{
                        list.add(new Sw("$v0",mipsMem.offset,"$sp"));
                    }
                }
                putLocalRel(name,mipsMem);
            }
        }
        return list;
    }
}
