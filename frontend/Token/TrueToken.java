package frontend.Token;

import java.util.HashMap;
import java.util.Map;

public class TrueToken {
    protected String name;
    protected TrueType type;
    protected int lineNumber;
    public static Map<String, TrueType> tokenMap=new HashMap<>();

    public TrueType getType() {
        return type;
    }

    static{
        tokenMap.put("main", TrueType.MAINTK);
        tokenMap.put("const", TrueType.CONSTTK);
        tokenMap.put("int", TrueType.INTTK);
        tokenMap.put("char", TrueType.CHARTK);
        tokenMap.put("break", TrueType.BREAKTK);
        tokenMap.put("continue", TrueType.CONTINUETK);
        tokenMap.put("if", TrueType.IFTK);
        tokenMap.put("else", TrueType.ELSETK);
        tokenMap.put("for", TrueType.FORTK);
        tokenMap.put("getint", TrueType.GETINTTK);
        tokenMap.put("getchar", TrueType.GETCHARTK);
        tokenMap.put("printf", TrueType.PRINTFTK);
        tokenMap.put("return", TrueType.RETURNTK);
        tokenMap.put("void", TrueType.VOIDTK);
    }

    public TrueToken(String name) {
        this.name=name;
    }

    public TrueToken(String name, TrueType type,int lineNumber) {
        this.name=name;
        this.type=type;
        this.lineNumber=lineNumber;
    }

    public static TrueType checkToken(String s){
        if(tokenMap.containsKey(s)){
            return tokenMap.get(s);
        }
        return TrueType.IDENFR;
    }

    @Override
    public String toString() {
        return this.type.toString()+" "+this.name+"\n";
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getName() {
        return name;
    }
}
