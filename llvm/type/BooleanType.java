package llvm.type;


import llvm.Type;

/**
 * @className: BooleanType
 * @author: bxr
 * @date: 2024/11/18 17:25
 * @description:
 */

public class BooleanType extends Type {
    protected static int bits=1;

    public String toString(){
        return "i" + bits;
    }
}
