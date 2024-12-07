package midend.value.Instruction.Memory;


import backend.Instruction.Memory.La;
import backend.Instruction.Memory.Lb;
import backend.Instruction.Memory.Lw;
import backend.Instruction.MipsInstruction;
import backend.reg.MipsMem;
import midend.Type;
import midend.Value;
import midend.type.CharType;
import midend.value.Instruction.Instruction;

import java.util.ArrayList;

import static backend.MipsGenerator.getRel;

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
            String label1="",label2="";
            MipsMem mipsMem1=getRel(operators.get(0).getName());
            if(mipsMem1!=null){
                if(mipsMem1.isInReg){
                    label1=mipsMem1.RegName;
                }else{
                    if(type instanceof CharType){
                        list.add(new Lb("$v0",mipsMem1.offset,"$sp"));
                    }else{
                        list.add(new Lw("$v0",mipsMem1.offset,"$sp"));
                    }
                    label1="$v0";
                }
            }
            MipsMem mipsMem2=getRel(operators.get(0).getName());
            if(mipsMem2!=null){
                if(mipsMem2.isInReg){
                    label2=mipsMem2.RegName;
                }else{
                    if(type instanceof CharType){
                        list.add(new Lb("$v0",mipsMem2.offset,"$sp"));
                    }else{
                        list.add(new Lw("$v0",mipsMem2.offset,"$sp"));
                    }
                    label2="$v0";
                }
            }

        }
        return list;
    }
}
