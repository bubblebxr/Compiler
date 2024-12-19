package backend.value;


/**
 * @className: MipsStr
 * @author: bxr
 * @date: 2024/12/3 16:53
 * @description: print中输出的字符串存放
 */

public class MipsStr {
    protected String name;// str的名字
    protected String strConst; //str的内容

    public MipsStr(String name,String strConst){
        this.name=name;
        this.strConst=strConst;
    }

    @Override
    public String toString(){
        return name+": .asciiz "+strConst;
    }
}
