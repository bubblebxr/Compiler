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
import java.util.Map;

import static backend.MipsGenerator.*;

/**
 * @className: store
 * @author: bxr
 * @date: 2024/11/7 20:31
 * @description:
 */

public class Store extends Instruction {

    public Store(String name, Type type) {
        super(name, type);
    }

    public Store(String name, Type type, ArrayList<Value> operators) {
        super(name, type);
        this.operators=operators;
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append("store ");
        for(int i=0;i<operators.size();i++){
            a.append(operators.get(i).toString());
            if(i<operators.size()-1){
                a.append(",");
            }
        }
        a.append("\n");
        return a.toString();
    }

    @Override
    public ArrayList<MipsInstruction> genMips() {
        ArrayList<MipsInstruction> temp=new ArrayList<>();
        // 如果operator的第一个是指针，说明是函数传参中的数组传参，需要特殊处理
        if(operators.get(0).getType() instanceof PointerType){
            String reg="";
            if(operators.get(0).getName().equals("%0")){
                reg="$a0";
            }else if(operators.get(0).getName().equals("%1")){
                reg="$a1";
            }else if(operators.get(0).getName().equals("%2")){
                reg="$a2";
            }else if(operators.get(0).getName().equals("%3")){
                reg="$a3";
            }else{
                //TODO:从栈中取出
                int paramsIndex=Integer.parseInt(operators.get(0).getName().substring(1));

            }
            // 获取函数参数存储的位置
            MipsMem mipsMem= MipsGenerator.getRel(operators.get(1).getName());
            if(mipsMem!=null){
                if(mipsMem.isInReg){
                    temp.add(new Move(mipsMem.RegName,reg));
                }else{
                    if(operators.get(0).getType().getType() instanceof CharType){
                        temp.add(new Sb(reg,mipsMem.offset,"$sp"));
                    }else{
                        temp.add(new Sw(reg,mipsMem.offset,"$sp"));
                    }
                }
            }
            return temp;
        }
        String reg = "";
        // 处理第一个操作数
        if(operators.get(0).getName().charAt(0)!='%'){
            //如果是立即数，0就直接使用寄存器，非0就li出来
            reg="$zero";
            if(!operators.get(0).getName().equals("0")){
                temp.add(new Li(Integer.parseInt(operators.get(0).getName()),false));
                reg="$v1";
            }
        }else{
            //不是常量，从关系中找到他
            MipsMem mipsMem= MipsGenerator.getRel(operators.get(0).getName());
            if(mipsMem!=null){
                if(mipsMem.isInReg){
                    //在寄存器中，可以直接获得
                    reg=mipsMem.RegName;
                }else{
                    //在栈中，需要先load出来
                    if(type instanceof CharType){
                        temp.add(new Lb("$v0",mipsMem.offset,"$sp"));
                    }else{
                        temp.add(new Lw("$v0",mipsMem.offset,"$sp"));
                    }
                    reg="$v0";
                }
            }else{
                if(operators.get(0).getName().equals("%0")){
                    reg="$a0";
                }else if(operators.get(0).getName().equals("%1")){
                    reg="$a1";
                }else if(operators.get(0).getName().equals("%2")){
                    reg="$a2";
                }else if(operators.get(0).getName().equals("%3")){
                    reg="$a3";
                }else{
                    //TODO:从栈中取出
                    int paramIndex=Integer.parseInt(operators.get(0).getName().substring(1));
                    if(type instanceof CharType){
                        temp.add(new Lb("$t0",-paramIndex,"$gp"));
                    }else{
                        temp.add(new Lw("$t0",-paramIndex,"$gp"));
                    }
                    reg="$t0";
                }
            }
        }
        MipsMem mipsMem=MipsGenerator.getRel(operators.get(1).getName());
        if(mipsMem!=null){
            // 如果是存储到数组中，需要进行内存存储，同时可以释放用于存储内存的临时变量
            if(mipsMem.isPointer!=null&& mipsMem.isPointer&&mipsMem.isInReg){
                if(type instanceof CharType){
                    temp.add(new Sb(reg,0,mipsMem.RegName));
                }else{
                    temp.add(new Sw(reg,0,mipsMem.RegName));
                }
            }else if(mipsMem.isPointer != null && mipsMem.isPointer){
                // 先将索引从内存中load出来
                temp.add(new Lw("$t0",mipsMem.offset,"$sp"));
                if(type instanceof CharType){
                    temp.add(new Sb(reg,0,"$t0"));
                }else{
                    temp.add(new Sw(reg,0,"$t0"));
                }
            }else{
                if(mipsMem.isInReg){
                    temp.add(new Move(mipsMem.RegName,reg));
                }else{
                    if(type instanceof CharType){
                        temp.add(new Sb(reg,mipsMem.offset,"$sp"));
                    }else{
                        temp.add(new Sw(reg,mipsMem.offset,"$sp"));
                    }
                }
            }
        }else{
            if(operators.get(1).getName().charAt(0)=='@'){
                temp.add(new La("$t0",operators.get(1).getName().substring(1)));
                if(type instanceof CharType){
                    temp.add(new Sb(reg,0,"$t0"));
                }else{
                    temp.add(new Sw(reg,0,"$t0"));
                }
            }
        }
        returnReg(reg);
        return temp;
    }
}
