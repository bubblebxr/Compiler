package midend.type;


import midend.Type;

/**
 * @className: CharType
 * @author: bxr
 * @date: 2024/11/5 14:49
 * @description: 字符
 */

public class CharType extends Type {
    protected static int bits=8;

    public String toString(){
        return "i" + bits;
    }
}
