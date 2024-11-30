package MIPS.symbol;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @className: MipsSymbolTable
 * @author: bxr
 * @date: 2024/11/22 21:27
 * @description: 用于储存寄存器等信息的符号表
 */

public class MipsSymbolTable {
    protected static int idCnt=0;
    protected int id;
    public static int spNum=0;// 为+表示分配出去了，为-表示未分配
    protected Map<String,Symbol> Directory;

    public MipsSymbolTable(){
        this.id=idCnt++;
        this.Directory=new HashMap<>();
    }

    public void addSymbol(String name,Symbol symbol){
        Directory.put(name,symbol);
    }

    public void updateSp(int a){
        spNum+=a;
    }

    public int getSp(){
        return spNum;
    }

    public Map<String, Symbol> getDirectory() {
        return Directory;
    }
}
