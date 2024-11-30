package MIPS;
import LLVM.Module;
import LLVM.value.Function;
import LLVM.value.GlobalVariable;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import MIPS.symbol.MipsSymbolTable;
import MIPS.symbol.Symbol;

/**
 * @className: MIPSGenerator
 * @author: bxr
 * @date: 2024/11/21 14:56
 * @description: 生成mips的module，用于输出目标代码mips
 */

public class MIPSGenerator {

    protected Module irModule;
    public static MipsModule mipsModule=new MipsModule();
    protected static int presentId=1;
    //存储可以使用的寄存器
    public static Stack<String> registerStack=new Stack<>();
    //存储ir寄存器对应的mips寄存器
    public static Map<String,String> irToMips=new HashMap<>();

    static{
        registerStack.add("$t1");
        registerStack.add("$t2");
        registerStack.add("$t3");
        registerStack.add("$t4");
        registerStack.add("$t5");
        registerStack.add("$t6");
        registerStack.add("$t7");
        registerStack.add("$t8");
        registerStack.add("$t9");
        registerStack.add("$s0");
        registerStack.add("$s1");
        registerStack.add("$s2");
        registerStack.add("$s3");
        registerStack.add("$s4");
        registerStack.add("$s5");
        registerStack.add("$s6");
        registerStack.add("$s7");
        registerStack.add("$k0");
        registerStack.add("$k1");
    }

    public MIPSGenerator(Module irModule){
        this.irModule=irModule;
    }

    public void generateMipsModule(){
        for(GlobalVariable globalVariable:irModule.getGlobalVariableList()){
            mipsModule.addMipsGlobalVariableList(globalVariable.generateMipsGlobalVariable());
        }
        for(GlobalVariable globalVariable:irModule.getGlobalVariableList()){
            mipsModule.addMipsGlobalList(globalVariable.generateMipsGlobal());
        }
        for(Function function:irModule.getFunctionList()){
            if(!function.getDeclare()){
                Symbol.spPresentNum=0;
                MipsSymbolTable.spNum=0;
                mipsModule.addFunctionList(function.generateMipsFunction());
                Symbol.forLocalFunGp=Symbol.gpPresentNum;
            }
        }
    }

    public String outputMips(){
        return mipsModule.toString();
    }

    /**
     * @description: 判断是否是2的次方

     **/
    public static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    /**
     * @description: 计算是2的几次方
     **/
    public static int getPowerOfTwo(int n) {
        int power = 0;
        while (n > 1) {
            n >>= 1;
            power++;
        }
        return power;
    }
}
