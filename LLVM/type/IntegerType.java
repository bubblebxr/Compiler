package LLVM.type;

import LLVM.Type;

public class IntegerType extends Type {
    protected static int bits=32;

    public String toString(){
        return "i" + bits;
    }
}
