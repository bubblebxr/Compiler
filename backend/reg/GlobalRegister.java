package backend.reg;


import backend.Instruction.Memory.Lw;
import backend.Instruction.Memory.Sw;
import backend.Instruction.MipsInstruction;
import backend.MipsGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @className: GlobalRegister
 * @author: bxr
 * @date: 2024/12/3 23:19
 * @description: 全局寄存器，用于保存变量
 */

public class GlobalRegister {
    //存储可以使用的寄存器
    public Stack<String> registerStack;
    //存储ir寄存器对应的mips寄存器
    public Map<String,MipsMem> irToMips;
    //存储本来可以使用的临时寄存器，防止回收了不该使用的寄存器
    public static Map<String,Integer> registerAlready=new HashMap<>();

    static {
        registerAlready.put("$s0",0);
        registerAlready.put("$s1",4);
        registerAlready.put("$s2",8);
        registerAlready.put("$s3",12);
        registerAlready.put("$s4",16);
        registerAlready.put("$s5",20);
        registerAlready.put("$s6",24);
        registerAlready.put("$s7",28);
    }

    public GlobalRegister(){
        this.registerStack=new Stack<>();
        this.irToMips=new HashMap<>();
        registerStack.add("$s0");
        registerStack.add("$s1");
        registerStack.add("$s2");
        registerStack.add("$s3");
        registerStack.add("$s4");
        registerStack.add("$s5");
        registerStack.add("$s6");
        registerStack.add("$s7");
    }

    public MipsMem getEmptyReg(Boolean isChar){
        if(registerStack.isEmpty()){
            int sp=MipsGenerator.spNum;
            MipsGenerator.spNum+=isChar?1:4;
            return new MipsMem(sp);
        }else{
            return new MipsMem(registerStack.pop());
        }
    }

    public MipsMem getArrayMem(Boolean isChar,int elementNum){
        int sp=MipsGenerator.spNum;
        MipsGenerator.spNum+=isChar?elementNum:4*elementNum;
        return new MipsMem(sp,elementNum);
    }

    public void returnReg(String name){
        if(registerAlready.containsKey(name)){
            registerStack.add(name);
        }
    }

    /**
     * @description: 增加ir寄存器到mips寄存器的关系
     * @date: 2024/12/4 9:09
     **/
    public void putGlobalRel(String irReg,MipsMem mipsReg){
        irToMips.put(irReg,mipsReg);
    }

    public MipsMem checkRel(String regName){
        if(irToMips.containsKey(regName)){
            return irToMips.get(regName);
        }else{
            return null;
        }
    }

    public ArrayList<MipsInstruction> storeGlobal(){
        ArrayList<MipsInstruction> temp=new ArrayList<>();
        for (String key : registerAlready.keySet()) {
            if (!registerStack.contains(key)) {
                Integer value = registerAlready.get(key);
                temp.add(new Sw(key,value,"$gp"));
            }
        }
        return temp;
    }

    public ArrayList<MipsInstruction> loadGlobal() {
        ArrayList<MipsInstruction> temp=new ArrayList<>();
        for (String key : registerAlready.keySet()) {
            if (!registerStack.contains(key)) {
                Integer value = registerAlready.get(key);
                temp.add(new Lw(key,value,"$gp"));
            }
        }
        return temp;
    }
}
