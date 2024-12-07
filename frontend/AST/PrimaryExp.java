package frontend.AST;

import frontend.Token.TrueToken;

import static midend.LLVMManager.CharacterToAscii;

public class PrimaryExp {
    protected TrueToken LPARENT;
    protected Exp exp;
    protected TrueToken RPARENT;
    protected LVal lVal;
    protected TrueToken NumberToken;//IntConst
    protected TrueToken CharacterToken;//CharConst
    /*1:'(' Exp ')' | 2:LVal | 3:Number | 4:Character*/
    protected int type;

    public void setLPARENT(TrueToken LPARENT) {
        this.LPARENT = LPARENT;
    }

    public void setExp(Exp exp) {
        this.exp = exp;
    }

    public void setRPARENT(TrueToken RPARENT) {
        this.RPARENT = RPARENT;
    }

    public void setlVal(LVal lVal) {
        this.lVal = lVal;
    }

    public void setNumberToken(TrueToken numberToken) {
        NumberToken = numberToken;
    }

    public void setCharacterToken(TrueToken characterToken) {
        CharacterToken = characterToken;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String outputPrimaryExp() {
        StringBuilder a=new StringBuilder();
        if(LPARENT!=null){
            a.append(LPARENT.toString());
            a.append(exp.outputExp());
            a.append(RPARENT.toString());
        }
        if(lVal!=null){
            a.append(lVal.outputLVal());
        }
        if(NumberToken !=null){
            a.append(NumberToken.toString());
            a.append("<Number>\n");
        }
        if(CharacterToken !=null){
            a.append(CharacterToken.toString());
            a.append("<Character>\n");
        }
        a.append("<PrimaryExp>\n");
        return a.toString();
    }

    public Exp getExp() {
        return exp;
    }

    public LVal getlVal() {
        return lVal;
    }

    public int getType() {
        return type;
    }

    public int getPrimaryExpValue() {
        if(LPARENT!=null){
            return exp.getExpValue();
        }else if(lVal!=null){
            return lVal.getLValValue();
        }else if(NumberToken!=null){
            return Integer.parseInt(NumberToken.getName());
        }else{
            if(CharacterToken.getName().length()==3){
                return CharacterToken.getName().charAt(1);
            }else{
                return Integer.parseInt(CharacterToAscii(CharacterToken.getName().substring(1,CharacterToken.getName().length()-1)));
            }

        }
    }

    public TrueToken getNumberToken() {
        return NumberToken;
    }

    public TrueToken getCharacterToken() {
        return CharacterToken;
    }
}
