package backend.reg;


import backend.MipsGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static backend.MipsGenerator.updateSp;

/**
 * @className: Register
 * @author: bxr
 * @date: 2024/12/3 21:59
 * @description: 用于分配临时寄存器,由于$t0用于Icmp比较，所以$t1-$t9用于临时寄存器
 */

public class LocalRegister {
    //存储可以使用的寄存器
    public Stack<String> registerStack;
    //存储ir寄存器对应的mips寄存器
    public Map<String,MipsMem> irToMips;
    //存储本来可以使用的临时寄存器，防止回收了不该使用的寄存器
    public static ArrayList<String> registerAlready=new ArrayList<>();

    static {
        registerAlready.add("$k0");
        registerAlready.add("$k1");
        registerAlready.add("$t1");
        registerAlready.add("$t2");
        registerAlready.add("$t3");
        registerAlready.add("$t4");
        registerAlready.add("$t5");
        registerAlready.add("$t6");
        registerAlready.add("$t7");
        registerAlready.add("$t8");
        registerAlready.add("$t9");
    }

    public LocalRegister(){
        this.registerStack=new Stack<>();
        this.irToMips=new HashMap<>();
        registerStack.add("$k0");
        registerStack.add("$k1");
        registerStack.add("$t1");
        registerStack.add("$t2");
        registerStack.add("$t3");
        registerStack.add("$t4");
        registerStack.add("$t5");
        registerStack.add("$t6");
        registerStack.add("$t7");
        registerStack.add("$t8");
        registerStack.add("$t9");
    }

    public MipsMem getEmptyReg(Boolean isChar,int sp){
        if(registerStack.isEmpty()){
            updateSp(4);
            return new MipsMem(sp);
        }else{
            return new MipsMem(registerStack.pop());
        }
    }

    public void returnReg(String name){
        if(registerAlready.contains(name)){
            registerStack.add(name);
        }
    }

    /**
     * @description: 增加ir寄存器到mips寄存器的关系
     * @date: 2024/12/4 9:09
     **/
    public void putRel(String irReg,MipsMem mipsReg){
        irToMips.put(irReg,mipsReg);
    }

    public MipsMem checkRel(String regName){
        if(irToMips.containsKey(regName)){
            return irToMips.get(regName);
        }else{
            return null;
        }
    }
}
