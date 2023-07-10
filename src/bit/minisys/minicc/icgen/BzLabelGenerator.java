package bit.minisys.minicc.icgen;

import bit.minisys.minicc.parser.ast.ASTNode;
import bit.minisys.minicc.parser.ast.ASTVisitor;

public class BzLabelGenerator extends ASTNode {
    public String Type;
    public Integer num;
    private Integer id;

    public BzLabelGenerator(Integer id, String Type, Integer num) {
        super("TemporaryValue");
        this.id = id;
        this.Type = Type;
        this.num = num;
    }

    public BzLabelGenerator(String type) {
        super(type);
    }

    public String name() {
        if (id == 0) {
            return "@" + Type;
        } else return "@" + id + Type;
    }

    @Override
    public void accept(ASTVisitor visitor) throws Exception {

    }
}