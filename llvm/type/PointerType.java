package llvm.type;

import llvm.Type;

public class PointerType extends Type {
    protected Type PointingType;

    public PointerType(Type type){
        this.PointingType=type;
    }

    public String toString(){
        return PointingType.toString()+"*";
    }

    public Type getType(){return PointingType;}
}
