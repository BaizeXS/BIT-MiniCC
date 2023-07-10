package bit.minisys.minicc.icgen;

import bit.minisys.minicc.parser.ast.ASTNode;

public class Quat {
    private final String op;
    private final ASTNode res;
    private final ASTNode opnd1;
    private final ASTNode opnd2;

    public Quat(String op, ASTNode res, ASTNode opnd1, ASTNode opnd2) {
        this.op = op;
        this.res = res;
        this.opnd1 = opnd1;
        this.opnd2 = opnd2;
    }

    public String getOp() {
        return op;
    }

    public ASTNode getOpnd1() {
        return opnd1;
    }

    public ASTNode getOpnd2() {
        return opnd2;
    }

    public ASTNode getRes() {
        return res;
    }
}
