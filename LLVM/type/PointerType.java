package LLVM.type;

import LLVM.Type;

public class PointerType extends Type {
    protected Type PointingType;

    public PointerType(Type type){
        this.PointingType=type;
    }

    public String toString(){
        return PointingType.toString()+"*";
    }
}
