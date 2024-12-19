package midend.value.Instruction.Operate;


import backend.Instruction.Memory.Lb;
import backend.Instruction.Memory.Li;
import backend.Instruction.Memory.Lw;
import backend.Instruction.MipsInstruction;
import backend.Instruction.Operate.Compare;
import backend.Instruction.Operate.CompareType;
import backend.Instruction.Operate.Move;
import backend.Instruction.Operate.Xori;
import backend.MipsGenerator;
import backend.reg.MipsMem;
import midend.Type;
import midend.Value;
import midend.type.CharType;
import midend.value.Instruction.Instruction;

import java.util.ArrayList;

import static backend.MipsGenerator.putGlobalRel;
import static backend.MipsGenerator.returnReg;

/**
 * @className: Icmp
 * @author: bxr
 * @date: 2024/11/12 22:12
 * @description: int Compare
 */

public class Icmp extends Instruction {
    protected IcmpType icmpType;

    public Icmp(String name, Type type) {
        super(name, type);
    }

    public Icmp(String name, Type type, ArrayList<Value> operators,IcmpType icmpType) {
        super(name,type,operators);
        this.icmpType=icmpType;
    }

    public IcmpType getIcmpType() {
        return icmpType;
    }

    @Override
    public String toString(){
        return name +
                " = " +
                "icmp " +
                icmpType +
                " " +
                operators.get(0).getType().toString() +
                " " +
                operators.get(0).getName() +
                "," +
                operators.get(1).getName() +
                "\n";
    }

    @Override
    public ArrayList<MipsInstruction> genMips() {
        ArrayList<MipsInstruction> list=new ArrayList<>();
        String label1 = "",label2="";
        if(operators.get(0).getName().equals("0")){
            label1="$zero";
        }else if(operators.get(0).getName().charAt(0)!='%'){
            list.add(new Li(Long.parseLong(operators.get(0).getName()),"$v0"));
            label1="$v0";
        }else{
            MipsMem mipsMem= MipsGenerator.getRel(operators.get(0).getName());
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
        if(icmpType==IcmpType.eq){
            // ==
            list.add(new Compare(CompareType.seq,"$t0",label1,label2));
        }else if(icmpType==IcmpType.ne){
            // !=
            list.add(new Compare(CompareType.sne,"$t0",label1,label2));
        }else if(icmpType==IcmpType.sgt){
            // >
            list.add(new Compare(CompareType.sgt,"$t0",label1,label2));
        }else if(icmpType==IcmpType.sge){
            // >=
            /*
             sge $t0, $s0, $s1:
             sgt $t0,$s1,$s0
             xori $t0,$t0,1
             **/
//            list.add(new Compare(CompareType.sge,"$t0",label1,label2));
            list.add(new Compare(CompareType.sgt,"$t0",label2,label1));
            list.add(new Xori("$t0","$t0","1"));
        }else if(icmpType==IcmpType.slt){
            // <
            list.add(new Compare(CompareType.slt,"$t0",label1,label2));
        }else{
            // <=
            /*
             sle $t0, $s0, $s1:
             slt $t0,$s1,$s0
             xori $t0,$t0,1
             **/
            list.add(new Compare(CompareType.slt,"$t0",label2,label1));
            list.add(new Xori("$t0","$t0","1"));
        }
        MipsMem reg=new MipsMem("$t0");
        putGlobalRel(name,reg);
        returnReg(label1);
        returnReg(label2);
        return list;
    }
}
