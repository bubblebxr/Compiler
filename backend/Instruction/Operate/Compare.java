package backend.Instruction.Operate;


import backend.Instruction.MipsInstruction;

/**
 * @className: Icmp
 * @author: bxr
 * @date: 2024/11/22 8:47
 * @description:
 */

public class Compare extends MipsInstruction {
    protected CompareType type;
    protected String register;
    protected String label1;
    protected String label2;
    protected Boolean hasCond;

    /**
     * @description: 不是和0比
     * @date: 2024/12/3 22:45
     **/
    public Compare(CompareType type,String register,String label1,String label2){
        this.hasCond=true;
        this.type=type;
        this.register=register;
        this.label1=label1;
        this.label2=label2;
    }

    /**
     * @description: 和0比
     * @date: 2024/12/3 22:46
     **/
    public Compare(CompareType type,String register,String label1){
        this.hasCond=false;
        this.type=type;
        this.register=register;
        this.label1=label1.substring(1);
    }

    @Override
    public String toString(){
        if(hasCond){
            return type+" "+register+", "+label1+", "+label2;
        }else{
            if(type==CompareType.bnez||type==CompareType.beqz){
                return type+" "+register+", ."+label1;
            }else{
                return type+" "+register+", "+label1;
            }
        }
    }
}
