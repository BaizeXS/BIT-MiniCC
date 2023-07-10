package bit.minisys.minicc.parser;

import bit.minisys.minicc.antlrgen.CBaseListener;
import bit.minisys.minicc.antlrgen.CParser;
import bit.minisys.minicc.parser.ast.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;

public class BzCListener extends CBaseListener {
    public String oFile;                                        // 输出文件名
    public Stack<ASTNode> NodeStack = new Stack<>();            // 分析栈

    public BzCListener(String oFile) {
        this.oFile = oFile;
    }

    public void saveParserTemps(String oFile, ASTNode node) {
        // 结果展示
        String[] dummyStrs = new String[16];
        TreeViewer viewer = new TreeViewer(Arrays.asList(dummyStrs), node);
        // viewer.open();
        // 保存中间结果文件
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(oFile), node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 1. CompilationUnit
     * */
    @Override
    public void enterCompilationUnit(CParser.CompilationUnitContext ctx) {
        // 入栈
        ASTCompilationUnit node = new ASTCompilationUnit();
        NodeStack.push(node);
    }

    @Override
    public void exitCompilationUnit(CParser.CompilationUnitContext ctx) throws IOException {
        // 出栈并挂载
        ASTNode node = NodeStack.pop();
        node.children.addAll(((ASTCompilationUnit) node).items);
        saveParserTemps(oFile, node);
        NodeStack.push(node);
    }

    @Override
    public void enterInitDeclaratorList(CParser.InitDeclaratorListContext ctx) {
        // 入栈
        ASTNode node = new ASTInitList();
        NodeStack.push(node);
    }

    @Override
    public void exitInitDeclaratorList(CParser.InitDeclaratorListContext ctx) {
        // 弹栈
        ASTNode node = NodeStack.pop();
        // 获取父节点
        ASTNode parentNode = NodeStack.peek();
        // 1. 检查声明域
        if (((ASTInitList) node).declarator != null) {
            node.children.add(((ASTInitList) node).declarator);
        }
        // 2. 检查表达式域
        if (((ASTInitList) node).exprs != null) {
            node.children.addAll(((ASTInitList) node).exprs);
        }
        // 3. 判断声明/初始化
        if (parentNode.getClass() == ASTDeclaration.class) {
            if (((ASTDeclaration) parentNode).initLists == null) {
                ((ASTDeclaration) parentNode).initLists = new LinkedList<>();
            }
            ((ASTDeclaration) parentNode).initLists.add((ASTInitList) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTInitList.class) {
            Stack<ASTNode> tmpStack = new Stack<>();
            while (parentNode.getClass() != ASTDeclaration.class) {
                tmpStack.push(NodeStack.pop());
                parentNode = NodeStack.peek();
            }
            if (((ASTDeclaration) parentNode).initLists == null) {
                ((ASTDeclaration) parentNode).initLists = new LinkedList<>();
            }
            ((ASTDeclaration) parentNode).initLists.add((ASTInitList) node);
            node.parent = parentNode;
            while (tmpStack.size() != 0) {
                NodeStack.push(tmpStack.pop());
            }
        }
    }

    /**
     * 2. Declaration
     */
    @Override
    public void enterDeclaration(CParser.DeclarationContext ctx) {
        ASTNode parentNode = NodeStack.peek();
        if (parentNode.getClass() == ASTDeclaration.class) {
            // Do nothing
        } else {
            ASTDeclaration node = new ASTDeclaration();
            NodeStack.push(node);
        }
    }

    @Override
    public void exitDeclaration(CParser.DeclarationContext ctx) {
        ASTNode node = NodeStack.pop();
        if (((ASTDeclaration) node).specifiers != null) {
            node.children.addAll(((ASTDeclaration) node).specifiers);
        }
        if (((ASTDeclaration) node).initLists != null) {
            node.children.addAll(((ASTDeclaration) node).initLists);
        }
        ASTNode parentNode = NodeStack.peek();
        if (parentNode.getClass() == ASTCompilationUnit.class) {
            if (((ASTCompilationUnit) parentNode).items == null) {
                ((ASTCompilationUnit) parentNode).items = new LinkedList<>();
            }
            ((ASTCompilationUnit) parentNode).items.add(node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCompoundStatement.class) {
            if (((ASTCompoundStatement) parentNode).blockItems == null) {
                ((ASTCompoundStatement) parentNode).blockItems = new LinkedList<>();
            }
            ((ASTCompoundStatement) parentNode).blockItems.add(node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).init == null) {
                ((ASTIterationDeclaredStatement) parentNode).init = (ASTDeclaration) node;
            }
            node.parent = parentNode;
        }
    }

    /**
     * 3. FunctionDefinition
     */
    @Override
    public void enterFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        ASTNode parentNode = NodeStack.peek();
        if (parentNode.getClass() == ASTDeclaration.class) {
            parentNode = NodeStack.pop();
        }
        ASTNode node = new ASTFunctionDefine();
        NodeStack.push(node);
    }

    @Override
    public void exitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();
        if (((ASTFunctionDefine) node).specifiers != null) {
            node.children.addAll(((ASTFunctionDefine) node).specifiers);
        }
        if (((ASTFunctionDefine) node).declarator != null) {
            node.children.add(((ASTFunctionDefine) node).declarator);
        }
        if (((ASTFunctionDefine) node).body != null) {
            node.children.add(((ASTFunctionDefine) node).body);
        }
        if (parentNode.getClass() == ASTCompilationUnit.class) {
            if (((ASTCompilationUnit) parentNode).items == null) {
                ((ASTCompilationUnit) parentNode).items = new LinkedList<>();
            }
            ((ASTCompilationUnit) parentNode).items.add(node);
            node.parent = parentNode;
        }
    }

    /**
     * 4. Declarator
     *
     * @JsonSubTypes.Type(value = ASTArrayDeclarator.class, name = "ArrayDeclarator"),
     * @JsonSubTypes.Type(value = ASTFunctionDeclarator.class, name = "FunctionDeclarator"),
     * @JsonSubTypes.Type(value = ASTParamsDeclarator.class, name = "ParamsDeclarator"),
     * @JsonSubTypes.Type(value = ASTVariableDeclarator.class, name = "VariableDeclarator")
     */
    // ArrayDeclarator
    @Override
    public void enterDirectDeclarator_array(CParser.DirectDeclarator_arrayContext ctx) {
        ASTNode node = new ASTArrayDeclarator();
        NodeStack.push(node);
    }

    @Override
    public void exitDirectDeclarator_array(CParser.DirectDeclarator_arrayContext ctx) {
        ASTNode node = NodeStack.pop();
        if (((ASTArrayDeclarator) node).declarator != null) {
            node.children.add(((ASTArrayDeclarator) node).declarator);
        }
        if (((ASTArrayDeclarator) node).expr != null) {
            node.children.add(((ASTArrayDeclarator) node).expr);
        }
        ASTNode parentNode = NodeStack.peek();
        if (parentNode.getClass() == ASTArrayDeclarator.class) {
            ((ASTArrayDeclarator) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTInitList.class) {
            ((ASTInitList) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTFunctionDeclarator.class) {
            ((ASTFunctionDeclarator) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTFunctionDefine.class) {
            ((ASTFunctionDefine) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTParamsDeclarator.class) {
            ((ASTParamsDeclarator) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTTypename.class) {
            ((ASTTypename) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        }
    }

    // FunctionDeclarator
    @Override
    public void enterDirectDeclarator_func(CParser.DirectDeclarator_funcContext ctx) {
        ASTNode node = new ASTFunctionDeclarator();
        NodeStack.push(node);
    }

    @Override
    public void exitDirectDeclarator_func(CParser.DirectDeclarator_funcContext ctx) {
        ASTNode node = NodeStack.pop();
        if (((ASTFunctionDeclarator) node).declarator != null) {
            node.children.add(((ASTFunctionDeclarator) node).declarator);
        }
        if (((ASTFunctionDeclarator) node).params != null) {
            node.children.addAll(((ASTFunctionDeclarator) node).params);
        }
        ASTNode parentNode = NodeStack.peek();
        if (parentNode.getClass() == ASTArrayDeclarator.class) {
            ((ASTArrayDeclarator) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTInitList.class) {
            ((ASTInitList) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTFunctionDeclarator.class) {
            ((ASTFunctionDeclarator) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTFunctionDefine.class) {
            ((ASTFunctionDefine) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTParamsDeclarator.class) {
            ((ASTParamsDeclarator) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTTypename.class) {
            ((ASTTypename) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        }
    }

    // ParamsDeclarator
    @Override
    public void enterParameterDeclaration(CParser.ParameterDeclarationContext ctx) {
        ASTNode node = new ASTParamsDeclarator();
        NodeStack.push(node);
    }

    @Override
    public void exitParameterDeclaration(CParser.ParameterDeclarationContext ctx) {
        ASTNode node = NodeStack.pop();
        if (((ASTParamsDeclarator) node).specfiers != null) {
            node.children.addAll(((ASTParamsDeclarator) node).specfiers);
        }
        if (((ASTParamsDeclarator) node).declarator != null) {
            node.children.add(((ASTParamsDeclarator) node).declarator);
        }
        ASTNode parentNode = NodeStack.peek();
        if (parentNode.getClass() == ASTFunctionDeclarator.class) {
            if (((ASTFunctionDeclarator) parentNode).params == null) {
                ((ASTFunctionDeclarator) parentNode).params = new LinkedList<>();
            }
            ((ASTFunctionDeclarator) parentNode).params.add((ASTParamsDeclarator) node);
            node.parent = parentNode;
        }
    }

    // VariableDeclarator
    @Override
    public void enterDirectDeclarator_pass(CParser.DirectDeclarator_passContext ctx) {
        ASTNode node = new ASTVariableDeclarator();
        NodeStack.push(node);
    }

    @Override
    public void exitDirectDeclarator_pass(CParser.DirectDeclarator_passContext ctx) {
        ASTNode node = NodeStack.pop();
        if (((ASTVariableDeclarator) node).identifier != null) {
            node.children.add(((ASTVariableDeclarator) node).identifier);
        }
        ASTNode parentNode = NodeStack.peek();
        if (parentNode.getClass() == ASTArrayDeclarator.class) {
            ((ASTArrayDeclarator) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTInitList.class) {
            ((ASTInitList) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTFunctionDeclarator.class) {
            ((ASTFunctionDeclarator) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTFunctionDefine.class) {
            ((ASTFunctionDefine) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTParamsDeclarator.class) {
            ((ASTParamsDeclarator) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTTypename.class) {
            ((ASTTypename) parentNode).declarator = (ASTDeclarator) node;
            node.parent = parentNode;
        }
    }


    /**
     * 5. Expression
     *
     * @JsonSubTypes.Type(value = ASTIdentifier.class, name = "Identifier"),
     * @JsonSubTypes.Type(value = ASTArrayAccess.class, name = "ArrayAccess"),
     * @JsonSubTypes.Type(value = ASTBinaryExpression.class, name = "BinaryExpression"),
     * @JsonSubTypes.Type(value = ASTCastExpression.class, name = "CastExpression"),
     * @JsonSubTypes.Type(value = ASTCharConstant.class, name = "CharConstant"),                  UNDO
     * @JsonSubTypes.Type(value = ASTConditionExpression.class, name = "ConditionExpression"),    UNDO
     * @JsonSubTypes.Type(value = ASTFloatConstant.class, name = "FloatConstant"),                UNDO
     * @JsonSubTypes.Type(value = ASTFunctionCall.class, name = "FunctionCall"),
     * @JsonSubTypes.Type(value = ASTIntegerConstant.class, name = "IntegerConstant"),
     * @JsonSubTypes.Type(value = ASTMemberAccess.class, name = "MemberAccess"),                  UNDO
     * @JsonSubTypes.Type(value = ASTPostfixExpression.class, name = "PostfixExpression"),
     * @JsonSubTypes.Type(value = ASTStringConstant.class, name = "StringConstant"),
     * @JsonSubTypes.Type(value = ASTUnaryExpression.class, name = "UnaryExpression"),
     * @JsonSubTypes.Type(value = ASTUnaryTypename.class, name = "UnaryTypename")                 UNDO
     */
    // Identifier
    @Override
    public void enterTokenId(CParser.TokenIdContext ctx) {
        ASTNode node = new ASTIdentifier();
        NodeStack.push(node);
    }

    @Override
    public void exitTokenId(CParser.TokenIdContext ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();
        if (parentNode.getClass() == ASTArrayDeclarator.class) {
            if (((ASTArrayDeclarator) parentNode).expr == null) {
                ((ASTArrayDeclarator) parentNode).expr = (ASTExpression) node;
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTArrayAccess.class) {
            if (((ASTArrayAccess) parentNode).arrayName == null) {
                ((ASTArrayAccess) parentNode).arrayName = (ASTExpression) node;
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements == null) {
                ((ASTArrayAccess) parentNode).elements = new LinkedList<>();
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements != null) {
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTBinaryExpression.class) {
            if (((ASTBinaryExpression) parentNode).expr1 == null) {
                ((ASTBinaryExpression) parentNode).expr1 = (ASTExpression) node;
                node.parent = parentNode;
            } else if (((ASTBinaryExpression) parentNode).expr2 == null) {
                ((ASTBinaryExpression) parentNode).expr2 = (ASTExpression) node;
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTCastExpression.class) {
            if (((ASTCastExpression) parentNode).expr == null) {
                ((ASTCastExpression) parentNode).expr = (ASTExpression) node;
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTFunctionCall.class) {
            if (((ASTFunctionCall) parentNode).funcname == null) {
                ((ASTFunctionCall) parentNode).funcname = (ASTExpression) node;
                node.parent = parentNode;
            } else if (((ASTFunctionCall) parentNode).argList == null) {
                ((ASTFunctionCall) parentNode).argList = new LinkedList<>();
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
                node.parent = parentNode;
            } else if (((ASTFunctionCall) parentNode).argList != null) {
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTPostfixExpression.class) {
            if (((ASTPostfixExpression) parentNode).expr == null) {
                ((ASTPostfixExpression) parentNode).expr = (ASTExpression) node;
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTUnaryExpression.class) {
            if (((ASTUnaryExpression) parentNode).expr == null) {
                ((ASTUnaryExpression) parentNode).expr = (ASTExpression) node;
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTExpressionStatement.class) {
            if (((ASTExpressionStatement) parentNode).exprs == null) {
                ((ASTExpressionStatement) parentNode).exprs = new LinkedList<>();
            }
            ((ASTExpressionStatement) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTInitList.class) {
            if (((ASTInitList) parentNode).exprs == null) {
                ((ASTInitList) parentNode).exprs = new LinkedList<>();
            }
            ((ASTInitList) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).cond == null) {
                ((ASTIterationDeclaredStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).cond.add((ASTExpression) node);
                node.parent = parentNode;
            } else if (((ASTIterationDeclaredStatement) parentNode).step == null) {
                ((ASTIterationDeclaredStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).step.add((ASTExpression) node);
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).init == null) {
                ((ASTIterationStatement) parentNode).init = new LinkedList<>();
                ((ASTIterationStatement) parentNode).init.add((ASTExpression) node);
                node.parent = parentNode;
            } else if (((ASTIterationStatement) parentNode).cond == null) {
                ((ASTIterationStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationStatement) parentNode).cond.add((ASTExpression) node);
                node.parent = parentNode;
            } else if (((ASTIterationStatement) parentNode).step == null) {
                ((ASTIterationStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationStatement) parentNode).step.add((ASTExpression) node);
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTReturnStatement.class) {
            if (((ASTReturnStatement) parentNode).expr == null) {
                ((ASTReturnStatement) parentNode).expr = new LinkedList<>();
            }
            ((ASTReturnStatement) parentNode).expr.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).cond == null) {
                ((ASTSelectionStatement) parentNode).cond = new LinkedList<>();
                ((ASTSelectionStatement) parentNode).cond.add((ASTExpression) node);
                node.parent = parentNode;
            }
        }
    }

    // ArrayAccess
    @Override
    public void enterPostfixExpression_arrayaccess(CParser.PostfixExpression_arrayaccessContext ctx) {
        ASTNode node = new ASTArrayAccess();
        NodeStack.push(node);
    }

    @Override
    public void exitPostfixExpression_arrayaccess(CParser.PostfixExpression_arrayaccessContext ctx) {
        ASTNode node = NodeStack.pop();
        if (((ASTArrayAccess) node).arrayName != null) {
            node.children.add(((ASTArrayAccess) node).arrayName);
        }
        if (((ASTArrayAccess) node).elements != null) {
            node.children.addAll(((ASTArrayAccess) node).elements);
        }
        ASTNode parentNode = NodeStack.peek();
        if (parentNode.getClass() == ASTArrayDeclarator.class) {
            if (((ASTArrayDeclarator) parentNode).expr == null) {
                ((ASTArrayDeclarator) parentNode).expr = (ASTExpression) node;
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTArrayAccess.class) {
            if (((ASTArrayAccess) parentNode).arrayName == null) {
                ((ASTArrayAccess) parentNode).arrayName = (ASTExpression) node;
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements == null) {
                ((ASTArrayAccess) parentNode).elements = new LinkedList<>();
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements != null) {
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTBinaryExpression.class) {
            if (((ASTBinaryExpression) parentNode).expr1 == null) {
                ((ASTBinaryExpression) parentNode).expr1 = (ASTExpression) node;

            } else if (((ASTBinaryExpression) parentNode).expr2 == null) {
                ((ASTBinaryExpression) parentNode).expr2 = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCastExpression.class) {
            if (((ASTCastExpression) parentNode).expr == null) {
                ((ASTCastExpression) parentNode).expr = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTFunctionCall.class) {
            if (((ASTFunctionCall) parentNode).funcname == null) {
                ((ASTFunctionCall) parentNode).funcname = (ASTExpression) node;
            } else if (((ASTFunctionCall) parentNode).argList == null) {
                ((ASTFunctionCall) parentNode).argList = new LinkedList<>();
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
            } else if (((ASTFunctionCall) parentNode).argList != null) {
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTPostfixExpression.class) {
            if (((ASTPostfixExpression) parentNode).expr == null) {
                ((ASTPostfixExpression) parentNode).expr = (ASTExpression) node;
            }
        } else if (parentNode.getClass() == ASTUnaryExpression.class) {
            if (((ASTUnaryExpression) parentNode).expr == null) {
                ((ASTUnaryExpression) parentNode).expr = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTExpressionStatement.class) {
            if (((ASTExpressionStatement) parentNode).exprs == null) {
                ((ASTExpressionStatement) parentNode).exprs = new LinkedList<>();
            }
            ((ASTExpressionStatement) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTInitList.class) {
            if (((ASTInitList) parentNode).exprs == null) {
                ((ASTInitList) parentNode).exprs = new LinkedList<>();
            }
            ((ASTInitList) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).cond == null) {
                ((ASTIterationDeclaredStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).cond.add((ASTExpression) node);
            } else if (((ASTIterationDeclaredStatement) parentNode).step == null) {
                ((ASTIterationDeclaredStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).step.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).init == null) {
                ((ASTIterationStatement) parentNode).init = new LinkedList<>();
                ((ASTIterationStatement) parentNode).init.add((ASTExpression) node);
            } else if (((ASTIterationStatement) parentNode).cond == null) {
                ((ASTIterationStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationStatement) parentNode).cond.add((ASTExpression) node);
            } else if (((ASTIterationStatement) parentNode).step == null) {
                ((ASTIterationStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationStatement) parentNode).step.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTReturnStatement.class) {
            if (((ASTReturnStatement) parentNode).expr == null) {
                ((ASTReturnStatement) parentNode).expr = new LinkedList<>();
            }
            ((ASTReturnStatement) parentNode).expr.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).cond == null) {
                ((ASTSelectionStatement) parentNode).cond = new LinkedList<>();
                ((ASTSelectionStatement) parentNode).cond.add((ASTExpression) node);
            }
            node.parent = parentNode;
        }
    }

    // BinaryExpression
    public void enterBinaryExpression() {
        ASTNode node = new ASTBinaryExpression();
        NodeStack.push(node);
    }

    public void exitBinaryExpression() {
        ASTNode node = NodeStack.pop();
        if (((ASTBinaryExpression) node).expr1 != null) {
            node.children.add(((ASTBinaryExpression) node).expr1);
        }
        if (((ASTBinaryExpression) node).expr2 != null) {
            node.children.add(((ASTBinaryExpression) node).expr2);
        }
        ASTNode parentNode = NodeStack.peek();
        if (parentNode.getClass() == ASTArrayDeclarator.class) {
            if (((ASTArrayDeclarator) parentNode).expr == null) {
                ((ASTArrayDeclarator) parentNode).expr = (ASTExpression) node;
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTArrayAccess.class) {
            if (((ASTArrayAccess) parentNode).arrayName == null) {
                ((ASTArrayAccess) parentNode).arrayName = (ASTExpression) node;
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements == null) {
                ((ASTArrayAccess) parentNode).elements = new LinkedList<>();
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements != null) {
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTBinaryExpression.class) {
            if (((ASTBinaryExpression) parentNode).expr1 == null) {
                ((ASTBinaryExpression) parentNode).expr1 = (ASTExpression) node;

            } else if (((ASTBinaryExpression) parentNode).expr2 == null) {
                ((ASTBinaryExpression) parentNode).expr2 = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCastExpression.class) {
            if (((ASTCastExpression) parentNode).expr == null) {
                ((ASTCastExpression) parentNode).expr = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTFunctionCall.class) {
            if (((ASTFunctionCall) parentNode).funcname == null) {
                ((ASTFunctionCall) parentNode).funcname = (ASTExpression) node;
            } else if (((ASTFunctionCall) parentNode).argList == null) {
                ((ASTFunctionCall) parentNode).argList = new LinkedList<>();
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
            } else if (((ASTFunctionCall) parentNode).argList != null) {
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTPostfixExpression.class) {
            if (((ASTPostfixExpression) parentNode).expr == null) {
                ((ASTPostfixExpression) parentNode).expr = (ASTExpression) node;
            }
        } else if (parentNode.getClass() == ASTUnaryExpression.class) {
            if (((ASTUnaryExpression) parentNode).expr == null) {
                ((ASTUnaryExpression) parentNode).expr = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTExpressionStatement.class) {
            if (((ASTExpressionStatement) parentNode).exprs == null) {
                ((ASTExpressionStatement) parentNode).exprs = new LinkedList<>();
            }
            ((ASTExpressionStatement) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTInitList.class) {
            if (((ASTInitList) parentNode).exprs == null) {
                ((ASTInitList) parentNode).exprs = new LinkedList<>();
            }
            ((ASTInitList) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).cond == null) {
                ((ASTIterationDeclaredStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).cond.add((ASTExpression) node);
            } else if (((ASTIterationDeclaredStatement) parentNode).step == null) {
                ((ASTIterationDeclaredStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).step.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).init == null) {
                ((ASTIterationStatement) parentNode).init = new LinkedList<>();
                ((ASTIterationStatement) parentNode).init.add((ASTExpression) node);
            } else if (((ASTIterationStatement) parentNode).cond == null) {
                ((ASTIterationStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationStatement) parentNode).cond.add((ASTExpression) node);
            } else if (((ASTIterationStatement) parentNode).step == null) {
                ((ASTIterationStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationStatement) parentNode).step.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTReturnStatement.class) {
            if (((ASTReturnStatement) parentNode).expr == null) {
                ((ASTReturnStatement) parentNode).expr = new LinkedList<>();
            }
            ((ASTReturnStatement) parentNode).expr.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).cond == null) {
                ((ASTSelectionStatement) parentNode).cond = new LinkedList<>();
                ((ASTSelectionStatement) parentNode).cond.add((ASTExpression) node);
            }
            node.parent = parentNode;
        }
    }

    @Override
    public void enterMultiplicativeExpression_(CParser.MultiplicativeExpression_Context ctx) {
        enterBinaryExpression();
    }

    @Override
    public void exitMultiplicativeExpression_(CParser.MultiplicativeExpression_Context ctx) {
        exitBinaryExpression();
    }

    @Override
    public void enterAdditiveExpression_(CParser.AdditiveExpression_Context ctx) {
        enterBinaryExpression();
    }

    @Override
    public void exitAdditiveExpression_(CParser.AdditiveExpression_Context ctx) {
        exitBinaryExpression();
    }

    @Override
    public void enterShiftExpression_(CParser.ShiftExpression_Context ctx) {
        enterBinaryExpression();
    }

    @Override
    public void exitShiftExpression_(CParser.ShiftExpression_Context ctx) {
        exitBinaryExpression();
    }

    @Override
    public void enterRelationalExpression_(CParser.RelationalExpression_Context ctx) {
        enterBinaryExpression();
    }

    @Override
    public void exitRelationalExpression_(CParser.RelationalExpression_Context ctx) {
        exitBinaryExpression();
    }

    @Override
    public void enterEqualityExpression_(CParser.EqualityExpression_Context ctx) {
        enterBinaryExpression();
    }

    @Override
    public void exitEqualityExpression_(CParser.EqualityExpression_Context ctx) {
        exitBinaryExpression();
    }

    @Override
    public void enterAndExpression_(CParser.AndExpression_Context ctx) {
        enterBinaryExpression();
    }

    @Override
    public void exitAndExpression_(CParser.AndExpression_Context ctx) {
        exitBinaryExpression();
    }

    @Override
    public void enterInclusiveOrExpression_(CParser.InclusiveOrExpression_Context ctx) {
        enterBinaryExpression();
    }

    @Override
    public void exitInclusiveOrExpression_(CParser.InclusiveOrExpression_Context ctx) {
        exitBinaryExpression();
    }

    @Override
    public void enterLogicalAndExpression_(CParser.LogicalAndExpression_Context ctx) {
        enterBinaryExpression();
    }

    @Override
    public void exitLogicalAndExpression_(CParser.LogicalAndExpression_Context ctx) {
        exitBinaryExpression();
    }

    @Override
    public void enterLogicalOrExpression_(CParser.LogicalOrExpression_Context ctx) {
        enterBinaryExpression();
    }

    @Override
    public void exitLogicalOrExpression_(CParser.LogicalOrExpression_Context ctx) {
        exitBinaryExpression();
    }

    @Override
    public void enterAssignmentExpression_(CParser.AssignmentExpression_Context ctx) {
        enterBinaryExpression();
    }

    @Override
    public void exitAssignmentExpression_(CParser.AssignmentExpression_Context ctx) {
        exitBinaryExpression();
    }

    // CastExpression
    @Override
    public void enterCastExpression_(CParser.CastExpression_Context ctx) {
        ASTNode node = new ASTCastExpression();
        NodeStack.push(node);
    }

    @Override
    public void exitCastExpression_(CParser.CastExpression_Context ctx) {
        ASTNode node = NodeStack.pop();
        if (((ASTCastExpression) node).typename != null) {
            node.children.add(((ASTCastExpression) node).typename);
        }
        if (((ASTCastExpression) node).expr != null) {
            node.children.add(((ASTCastExpression) node).expr);
        }
        ASTNode parentNode = NodeStack.peek();
        if (parentNode.getClass() == ASTArrayDeclarator.class) {
            if (((ASTArrayDeclarator) parentNode).expr == null) {
                ((ASTArrayDeclarator) parentNode).expr = (ASTExpression) node;
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTArrayAccess.class) {
            if (((ASTArrayAccess) parentNode).arrayName == null) {
                ((ASTArrayAccess) parentNode).arrayName = (ASTExpression) node;
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements == null) {
                ((ASTArrayAccess) parentNode).elements = new LinkedList<>();
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements != null) {
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTBinaryExpression.class) {
            if (((ASTBinaryExpression) parentNode).expr1 == null) {
                ((ASTBinaryExpression) parentNode).expr1 = (ASTExpression) node;

            } else if (((ASTBinaryExpression) parentNode).expr2 == null) {
                ((ASTBinaryExpression) parentNode).expr2 = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCastExpression.class) {
            if (((ASTCastExpression) parentNode).expr == null) {
                ((ASTCastExpression) parentNode).expr = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTFunctionCall.class) {
            if (((ASTFunctionCall) parentNode).funcname == null) {
                ((ASTFunctionCall) parentNode).funcname = (ASTExpression) node;
            } else if (((ASTFunctionCall) parentNode).argList == null) {
                ((ASTFunctionCall) parentNode).argList = new LinkedList<>();
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
            } else if (((ASTFunctionCall) parentNode).argList != null) {
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTPostfixExpression.class) {
            if (((ASTPostfixExpression) parentNode).expr == null) {
                ((ASTPostfixExpression) parentNode).expr = (ASTExpression) node;
            }
        } else if (parentNode.getClass() == ASTUnaryExpression.class) {
            if (((ASTUnaryExpression) parentNode).expr == null) {
                ((ASTUnaryExpression) parentNode).expr = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTExpressionStatement.class) {
            if (((ASTExpressionStatement) parentNode).exprs == null) {
                ((ASTExpressionStatement) parentNode).exprs = new LinkedList<>();
            }
            ((ASTExpressionStatement) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTInitList.class) {
            if (((ASTInitList) parentNode).exprs == null) {
                ((ASTInitList) parentNode).exprs = new LinkedList<>();
            }
            ((ASTInitList) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).cond == null) {
                ((ASTIterationDeclaredStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).cond.add((ASTExpression) node);
            } else if (((ASTIterationDeclaredStatement) parentNode).step == null) {
                ((ASTIterationDeclaredStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).step.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).init == null) {
                ((ASTIterationStatement) parentNode).init = new LinkedList<>();
                ((ASTIterationStatement) parentNode).init.add((ASTExpression) node);
            } else if (((ASTIterationStatement) parentNode).cond == null) {
                ((ASTIterationStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationStatement) parentNode).cond.add((ASTExpression) node);
            } else if (((ASTIterationStatement) parentNode).step == null) {
                ((ASTIterationStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationStatement) parentNode).step.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTReturnStatement.class) {
            if (((ASTReturnStatement) parentNode).expr == null) {
                ((ASTReturnStatement) parentNode).expr = new LinkedList<>();
            }
            ((ASTReturnStatement) parentNode).expr.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).cond == null) {
                ((ASTSelectionStatement) parentNode).cond = new LinkedList<>();
                ((ASTSelectionStatement) parentNode).cond.add((ASTExpression) node);
            }
            node.parent = parentNode;
        }
    }


    @Override
    public void enterTokenConstant(CParser.TokenConstantContext ctx) {
        ASTNode node = new ASTIntegerConstant();
        NodeStack.push(node);
    }

    @Override
    public void exitTokenConstant(CParser.TokenConstantContext ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();
        if (parentNode.getClass() == ASTArrayDeclarator.class) {
            if (((ASTArrayDeclarator) parentNode).expr == null) {
                ((ASTArrayDeclarator) parentNode).expr = (ASTExpression) node;
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTArrayAccess.class) {
            if (((ASTArrayAccess) parentNode).arrayName == null) {
                ((ASTArrayAccess) parentNode).arrayName = (ASTExpression) node;
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements == null) {
                ((ASTArrayAccess) parentNode).elements = new LinkedList<>();
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements != null) {
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTBinaryExpression.class) {
            if (((ASTBinaryExpression) parentNode).expr1 == null) {
                ((ASTBinaryExpression) parentNode).expr1 = (ASTExpression) node;

            } else if (((ASTBinaryExpression) parentNode).expr2 == null) {
                ((ASTBinaryExpression) parentNode).expr2 = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCastExpression.class) {
            if (((ASTCastExpression) parentNode).expr == null) {
                ((ASTCastExpression) parentNode).expr = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTFunctionCall.class) {
            if (((ASTFunctionCall) parentNode).funcname == null) {
                ((ASTFunctionCall) parentNode).funcname = (ASTExpression) node;
            } else if (((ASTFunctionCall) parentNode).argList == null) {
                ((ASTFunctionCall) parentNode).argList = new LinkedList<>();
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
            } else if (((ASTFunctionCall) parentNode).argList != null) {
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTPostfixExpression.class) {
            if (((ASTPostfixExpression) parentNode).expr == null) {
                ((ASTPostfixExpression) parentNode).expr = (ASTExpression) node;
            }
        } else if (parentNode.getClass() == ASTUnaryExpression.class) {
            if (((ASTUnaryExpression) parentNode).expr == null) {
                ((ASTUnaryExpression) parentNode).expr = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTExpressionStatement.class) {
            if (((ASTExpressionStatement) parentNode).exprs == null) {
                ((ASTExpressionStatement) parentNode).exprs = new LinkedList<>();
            }
            ((ASTExpressionStatement) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTInitList.class) {
            if (((ASTInitList) parentNode).exprs == null) {
                ((ASTInitList) parentNode).exprs = new LinkedList<>();
            }
            ((ASTInitList) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).cond == null) {
                ((ASTIterationDeclaredStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).cond.add((ASTExpression) node);
            } else if (((ASTIterationDeclaredStatement) parentNode).step == null) {
                ((ASTIterationDeclaredStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).step.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).init == null) {
                ((ASTIterationStatement) parentNode).init = new LinkedList<>();
                ((ASTIterationStatement) parentNode).init.add((ASTExpression) node);
            } else if (((ASTIterationStatement) parentNode).cond == null) {
                ((ASTIterationStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationStatement) parentNode).cond.add((ASTExpression) node);
            } else if (((ASTIterationStatement) parentNode).step == null) {
                ((ASTIterationStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationStatement) parentNode).step.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTReturnStatement.class) {
            if (((ASTReturnStatement) parentNode).expr == null) {
                ((ASTReturnStatement) parentNode).expr = new LinkedList<>();
            }
            ((ASTReturnStatement) parentNode).expr.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).cond == null) {
                ((ASTSelectionStatement) parentNode).cond = new LinkedList<>();
                ((ASTSelectionStatement) parentNode).cond.add((ASTExpression) node);
            }
            node.parent = parentNode;
        }
    }

    // FunctionCall
    @Override
    public void enterPostfixExpression_funcall(CParser.PostfixExpression_funcallContext ctx) {
        ASTNode node = new ASTFunctionCall();
        NodeStack.push(node);
    }

    @Override
    public void exitPostfixExpression_funcall(CParser.PostfixExpression_funcallContext ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();
        if (((ASTFunctionCall) node).funcname != null) {
            node.children.add(((ASTFunctionCall) node).funcname);
        }
        if (((ASTFunctionCall) node).argList != null) {
            node.children.addAll(((ASTFunctionCall) node).argList);
        }
        if (parentNode.getClass() == ASTArrayDeclarator.class) {
            if (((ASTArrayDeclarator) parentNode).expr == null) {
                ((ASTArrayDeclarator) parentNode).expr = (ASTExpression) node;
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTArrayAccess.class) {
            if (((ASTArrayAccess) parentNode).arrayName == null) {
                ((ASTArrayAccess) parentNode).arrayName = (ASTExpression) node;
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements == null) {
                ((ASTArrayAccess) parentNode).elements = new LinkedList<>();
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements != null) {
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTBinaryExpression.class) {
            if (((ASTBinaryExpression) parentNode).expr1 == null) {
                ((ASTBinaryExpression) parentNode).expr1 = (ASTExpression) node;

            } else if (((ASTBinaryExpression) parentNode).expr2 == null) {
                ((ASTBinaryExpression) parentNode).expr2 = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCastExpression.class) {
            if (((ASTCastExpression) parentNode).expr == null) {
                ((ASTCastExpression) parentNode).expr = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTFunctionCall.class) {
            if (((ASTFunctionCall) parentNode).funcname == null) {
                ((ASTFunctionCall) parentNode).funcname = (ASTExpression) node;
            } else if (((ASTFunctionCall) parentNode).argList == null) {
                ((ASTFunctionCall) parentNode).argList = new LinkedList<>();
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
            } else if (((ASTFunctionCall) parentNode).argList != null) {
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTPostfixExpression.class) {
            if (((ASTPostfixExpression) parentNode).expr == null) {
                ((ASTPostfixExpression) parentNode).expr = (ASTExpression) node;
            }
        } else if (parentNode.getClass() == ASTUnaryExpression.class) {
            if (((ASTUnaryExpression) parentNode).expr == null) {
                ((ASTUnaryExpression) parentNode).expr = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTExpressionStatement.class) {
            if (((ASTExpressionStatement) parentNode).exprs == null) {
                ((ASTExpressionStatement) parentNode).exprs = new LinkedList<>();
            }
            ((ASTExpressionStatement) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTInitList.class) {
            if (((ASTInitList) parentNode).exprs == null) {
                ((ASTInitList) parentNode).exprs = new LinkedList<>();
            }
            ((ASTInitList) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).cond == null) {
                ((ASTIterationDeclaredStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).cond.add((ASTExpression) node);
            } else if (((ASTIterationDeclaredStatement) parentNode).step == null) {
                ((ASTIterationDeclaredStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).step.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).init == null) {
                ((ASTIterationStatement) parentNode).init = new LinkedList<>();
                ((ASTIterationStatement) parentNode).init.add((ASTExpression) node);
            } else if (((ASTIterationStatement) parentNode).cond == null) {
                ((ASTIterationStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationStatement) parentNode).cond.add((ASTExpression) node);
            } else if (((ASTIterationStatement) parentNode).step == null) {
                ((ASTIterationStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationStatement) parentNode).step.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTReturnStatement.class) {
            if (((ASTReturnStatement) parentNode).expr == null) {
                ((ASTReturnStatement) parentNode).expr = new LinkedList<>();
            }
            ((ASTReturnStatement) parentNode).expr.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).cond == null) {
                ((ASTSelectionStatement) parentNode).cond = new LinkedList<>();
                ((ASTSelectionStatement) parentNode).cond.add((ASTExpression) node);
            }
            node.parent = parentNode;
        }
    }

    // PostfixExpression
    @Override
    public void enterPostfixExpression_(CParser.PostfixExpression_Context ctx) {
        ASTNode node = new ASTPostfixExpression();
        NodeStack.push(node);
    }

    @Override
    public void exitPostfixExpression_(CParser.PostfixExpression_Context ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();
        if (((ASTPostfixExpression) node).expr != null) {
            node.children.add(((ASTPostfixExpression) node).expr);
        }
        if (parentNode.getClass() == ASTArrayDeclarator.class) {
            if (((ASTArrayDeclarator) parentNode).expr == null) {
                ((ASTArrayDeclarator) parentNode).expr = (ASTExpression) node;
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTArrayAccess.class) {
            if (((ASTArrayAccess) parentNode).arrayName == null) {
                ((ASTArrayAccess) parentNode).arrayName = (ASTExpression) node;
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements == null) {
                ((ASTArrayAccess) parentNode).elements = new LinkedList<>();
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements != null) {
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTBinaryExpression.class) {
            if (((ASTBinaryExpression) parentNode).expr1 == null) {
                ((ASTBinaryExpression) parentNode).expr1 = (ASTExpression) node;

            } else if (((ASTBinaryExpression) parentNode).expr2 == null) {
                ((ASTBinaryExpression) parentNode).expr2 = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCastExpression.class) {
            if (((ASTCastExpression) parentNode).expr == null) {
                ((ASTCastExpression) parentNode).expr = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTFunctionCall.class) {
            if (((ASTFunctionCall) parentNode).funcname == null) {
                ((ASTFunctionCall) parentNode).funcname = (ASTExpression) node;
            } else if (((ASTFunctionCall) parentNode).argList == null) {
                ((ASTFunctionCall) parentNode).argList = new LinkedList<>();
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
            } else if (((ASTFunctionCall) parentNode).argList != null) {
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTPostfixExpression.class) {
            if (((ASTPostfixExpression) parentNode).expr == null) {
                ((ASTPostfixExpression) parentNode).expr = (ASTExpression) node;
            }
        } else if (parentNode.getClass() == ASTUnaryExpression.class) {
            if (((ASTUnaryExpression) parentNode).expr == null) {
                ((ASTUnaryExpression) parentNode).expr = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTExpressionStatement.class) {
            if (((ASTExpressionStatement) parentNode).exprs == null) {
                ((ASTExpressionStatement) parentNode).exprs = new LinkedList<>();
            }
            ((ASTExpressionStatement) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTInitList.class) {
            if (((ASTInitList) parentNode).exprs == null) {
                ((ASTInitList) parentNode).exprs = new LinkedList<>();
            }
            ((ASTInitList) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).cond == null) {
                ((ASTIterationDeclaredStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).cond.add((ASTExpression) node);
            } else if (((ASTIterationDeclaredStatement) parentNode).step == null) {
                ((ASTIterationDeclaredStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).step.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).init == null) {
                ((ASTIterationStatement) parentNode).init = new LinkedList<>();
                ((ASTIterationStatement) parentNode).init.add((ASTExpression) node);
            } else if (((ASTIterationStatement) parentNode).cond == null) {
                ((ASTIterationStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationStatement) parentNode).cond.add((ASTExpression) node);
            } else if (((ASTIterationStatement) parentNode).step == null) {
                ((ASTIterationStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationStatement) parentNode).step.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTReturnStatement.class) {
            if (((ASTReturnStatement) parentNode).expr == null) {
                ((ASTReturnStatement) parentNode).expr = new LinkedList<>();
            }
            ((ASTReturnStatement) parentNode).expr.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).cond == null) {
                ((ASTSelectionStatement) parentNode).cond = new LinkedList<>();
                ((ASTSelectionStatement) parentNode).cond.add((ASTExpression) node);
            }
            node.parent = parentNode;
        }
    }


    @Override
    public void enterTokenStringLiteral(CParser.TokenStringLiteralContext ctx) {
        ASTNode node = new ASTStringConstant();
        NodeStack.push(node);
    }

    @Override
    public void exitTokenStringLiteral(CParser.TokenStringLiteralContext ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();

        if (parentNode.getClass() == ASTArrayDeclarator.class) {
            if (((ASTArrayDeclarator) parentNode).expr == null) {
                ((ASTArrayDeclarator) parentNode).expr = (ASTExpression) node;
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTArrayAccess.class) {
            if (((ASTArrayAccess) parentNode).arrayName == null) {
                ((ASTArrayAccess) parentNode).arrayName = (ASTExpression) node;
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements == null) {
                ((ASTArrayAccess) parentNode).elements = new LinkedList<>();
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements != null) {
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTBinaryExpression.class) {
            if (((ASTBinaryExpression) parentNode).expr1 == null) {
                ((ASTBinaryExpression) parentNode).expr1 = (ASTExpression) node;

            } else if (((ASTBinaryExpression) parentNode).expr2 == null) {
                ((ASTBinaryExpression) parentNode).expr2 = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCastExpression.class) {
            if (((ASTCastExpression) parentNode).expr == null) {
                ((ASTCastExpression) parentNode).expr = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTFunctionCall.class) {
            if (((ASTFunctionCall) parentNode).funcname == null) {
                ((ASTFunctionCall) parentNode).funcname = (ASTExpression) node;
            } else if (((ASTFunctionCall) parentNode).argList == null) {
                ((ASTFunctionCall) parentNode).argList = new LinkedList<>();
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
            } else if (((ASTFunctionCall) parentNode).argList != null) {
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTPostfixExpression.class) {
            if (((ASTPostfixExpression) parentNode).expr == null) {
                ((ASTPostfixExpression) parentNode).expr = (ASTExpression) node;
            }
        } else if (parentNode.getClass() == ASTUnaryExpression.class) {
            if (((ASTUnaryExpression) parentNode).expr == null) {
                ((ASTUnaryExpression) parentNode).expr = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTExpressionStatement.class) {
            if (((ASTExpressionStatement) parentNode).exprs == null) {
                ((ASTExpressionStatement) parentNode).exprs = new LinkedList<>();
            }
            ((ASTExpressionStatement) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTInitList.class) {
            if (((ASTInitList) parentNode).exprs == null) {
                ((ASTInitList) parentNode).exprs = new LinkedList<>();
            }
            ((ASTInitList) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).cond == null) {
                ((ASTIterationDeclaredStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).cond.add((ASTExpression) node);
            } else if (((ASTIterationDeclaredStatement) parentNode).step == null) {
                ((ASTIterationDeclaredStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).step.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).init == null) {
                ((ASTIterationStatement) parentNode).init = new LinkedList<>();
                ((ASTIterationStatement) parentNode).init.add((ASTExpression) node);
            } else if (((ASTIterationStatement) parentNode).cond == null) {
                ((ASTIterationStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationStatement) parentNode).cond.add((ASTExpression) node);
            } else if (((ASTIterationStatement) parentNode).step == null) {
                ((ASTIterationStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationStatement) parentNode).step.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTReturnStatement.class) {
            if (((ASTReturnStatement) parentNode).expr == null) {
                ((ASTReturnStatement) parentNode).expr = new LinkedList<>();
            }
            ((ASTReturnStatement) parentNode).expr.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).cond == null) {
                ((ASTSelectionStatement) parentNode).cond = new LinkedList<>();
                ((ASTSelectionStatement) parentNode).cond.add((ASTExpression) node);
            }
            node.parent = parentNode;
        }
    }

    // UnaryExoression
    @Override
    public void enterUnaryExpression_(CParser.UnaryExpression_Context ctx) {
        ASTNode node = new ASTUnaryExpression();
        NodeStack.push(node);
    }

    @Override
    public void exitUnaryExpression_(CParser.UnaryExpression_Context ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();
        if (((ASTUnaryExpression) node).expr != null) {
            node.children.add(((ASTUnaryExpression) node).expr);
        }
        if (parentNode.getClass() == ASTArrayDeclarator.class) {
            if (((ASTArrayDeclarator) parentNode).expr == null) {
                ((ASTArrayDeclarator) parentNode).expr = (ASTExpression) node;
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTArrayAccess.class) {
            if (((ASTArrayAccess) parentNode).arrayName == null) {
                ((ASTArrayAccess) parentNode).arrayName = (ASTExpression) node;
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements == null) {
                ((ASTArrayAccess) parentNode).elements = new LinkedList<>();
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            } else if (((ASTArrayAccess) parentNode).elements != null) {
                ((ASTArrayAccess) parentNode).elements.add((ASTExpression) node);
                node.parent = parentNode;
            }
        } else if (parentNode.getClass() == ASTBinaryExpression.class) {
            if (((ASTBinaryExpression) parentNode).expr1 == null) {
                ((ASTBinaryExpression) parentNode).expr1 = (ASTExpression) node;

            } else if (((ASTBinaryExpression) parentNode).expr2 == null) {
                ((ASTBinaryExpression) parentNode).expr2 = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCastExpression.class) {
            if (((ASTCastExpression) parentNode).expr == null) {
                ((ASTCastExpression) parentNode).expr = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTFunctionCall.class) {
            if (((ASTFunctionCall) parentNode).funcname == null) {
                ((ASTFunctionCall) parentNode).funcname = (ASTExpression) node;
            } else if (((ASTFunctionCall) parentNode).argList == null) {
                ((ASTFunctionCall) parentNode).argList = new LinkedList<>();
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
            } else if (((ASTFunctionCall) parentNode).argList != null) {
                ((ASTFunctionCall) parentNode).argList.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTPostfixExpression.class) {
            if (((ASTPostfixExpression) parentNode).expr == null) {
                ((ASTPostfixExpression) parentNode).expr = (ASTExpression) node;
            }
        } else if (parentNode.getClass() == ASTUnaryExpression.class) {
            if (((ASTUnaryExpression) parentNode).expr == null) {
                ((ASTUnaryExpression) parentNode).expr = (ASTExpression) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTExpressionStatement.class) {
            if (((ASTExpressionStatement) parentNode).exprs == null) {
                ((ASTExpressionStatement) parentNode).exprs = new LinkedList<>();
            }
            ((ASTExpressionStatement) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTInitList.class) {
            if (((ASTInitList) parentNode).exprs == null) {
                ((ASTInitList) parentNode).exprs = new LinkedList<>();
            }
            ((ASTInitList) parentNode).exprs.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).cond == null) {
                ((ASTIterationDeclaredStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).cond.add((ASTExpression) node);
            } else if (((ASTIterationDeclaredStatement) parentNode).step == null) {
                ((ASTIterationDeclaredStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationDeclaredStatement) parentNode).step.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).init == null) {
                ((ASTIterationStatement) parentNode).init = new LinkedList<>();
                ((ASTIterationStatement) parentNode).init.add((ASTExpression) node);
            } else if (((ASTIterationStatement) parentNode).cond == null) {
                ((ASTIterationStatement) parentNode).cond = new LinkedList<>();
                ((ASTIterationStatement) parentNode).cond.add((ASTExpression) node);
            } else if (((ASTIterationStatement) parentNode).step == null) {
                ((ASTIterationStatement) parentNode).step = new LinkedList<>();
                ((ASTIterationStatement) parentNode).step.add((ASTExpression) node);
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTReturnStatement.class) {
            if (((ASTReturnStatement) parentNode).expr == null) {
                ((ASTReturnStatement) parentNode).expr = new LinkedList<>();
            }
            ((ASTReturnStatement) parentNode).expr.add((ASTExpression) node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).cond == null) {
                ((ASTSelectionStatement) parentNode).cond = new LinkedList<>();
                ((ASTSelectionStatement) parentNode).cond.add((ASTExpression) node);
            }
            node.parent = parentNode;
        }
    }


    /**
     * 6. Statement
     *
     * @JsonSubTypes.Type(value = ASTBreakStatement.class, name = "BreakStatement"),
     * @JsonSubTypes.Type(value = ASTCompoundStatement.class, name = "CompoundStatement"),
     * @JsonSubTypes.Type(value = ASTContinueStatement.class, name = "ContinueStatement"),
     * @JsonSubTypes.Type(value = ASTExpressionStatement.class, name = "ExpressionStatement"),
     * @JsonSubTypes.Type(value = ASTGotoStatement.class, name = "GotoStatement"),
     * @JsonSubTypes.Type(value = ASTIterationDeclaredStatement.class, name = "IterationDeclaredStatement"),
     * @JsonSubTypes.Type(value = ASTIterationStatement.class, name = "IterationStatement"),
     * @JsonSubTypes.Type(value = ASTLabeledStatement.class, name = "LabeledStatement"),
     * @JsonSubTypes.Type(value = ASTReturnStatement.class, name = "ReturnStatement"),
     * @JsonSubTypes.Type(value = ASTSelectionStatement.class, name = "SelectionStatement")
     */
    // BreakStatement
    @Override
    public void enterJumpStatement_break(CParser.JumpStatement_breakContext ctx) {
        ASTNode node = new ASTBreakStatement();
        NodeStack.push(node);
    }

    @Override
    public void exitJumpStatement_break(CParser.JumpStatement_breakContext ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();
        if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).then == null) {
                ((ASTSelectionStatement) parentNode).then = (ASTStatement) node;
            } else if (((ASTSelectionStatement) parentNode).otherwise == null) {
                ((ASTSelectionStatement) parentNode).otherwise = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTLabeledStatement.class) {
            if (((ASTLabeledStatement) parentNode).stat == null) {
                ((ASTLabeledStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).stat == null) {
                ((ASTIterationStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).stat == null) {
                ((ASTIterationDeclaredStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCompoundStatement.class) {
            if (((ASTCompoundStatement) parentNode).blockItems == null) {
                ((ASTCompoundStatement) parentNode).blockItems = new LinkedList<>();
            }
            ((ASTCompoundStatement) parentNode).blockItems.add(node);
            node.parent = parentNode;
        }
    }

    // CompoundStatement
    @Override
    public void enterCompoundStatement(CParser.CompoundStatementContext ctx) {
        ASTNode node = new ASTCompoundStatement();
        NodeStack.push(node);
    }

    @Override
    public void exitCompoundStatement(CParser.CompoundStatementContext ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();
        if (((ASTCompoundStatement) node).blockItems != null) {
            node.children.addAll(((ASTCompoundStatement) node).blockItems);
        }
        if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).then == null) {
                ((ASTSelectionStatement) parentNode).then = (ASTStatement) node;
            } else if (((ASTSelectionStatement) parentNode).otherwise == null) {
                ((ASTSelectionStatement) parentNode).otherwise = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTLabeledStatement.class) {
            if (((ASTLabeledStatement) parentNode).stat == null) {
                ((ASTLabeledStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).stat == null) {
                ((ASTIterationStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).stat == null) {
                ((ASTIterationDeclaredStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCompoundStatement.class) {
            if (((ASTCompoundStatement) parentNode).blockItems == null) {
                ((ASTCompoundStatement) parentNode).blockItems = new LinkedList<>();
            }
            ((ASTCompoundStatement) parentNode).blockItems.add(node);
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTFunctionDefine.class) {
            if (((ASTFunctionDefine) parentNode).body == null) {
                ((ASTFunctionDefine) parentNode).body = (ASTCompoundStatement) node;
            }
            node.parent = parentNode;
        }
    }

    // ContinueStatement
    @Override
    public void enterJumpStatement_continue(CParser.JumpStatement_continueContext ctx) {
        ASTNode node = new ASTContinueStatement();
        NodeStack.push(node);
    }

    @Override
    public void exitJumpStatement_continue(CParser.JumpStatement_continueContext ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();
        if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).then == null) {
                ((ASTSelectionStatement) parentNode).then = (ASTStatement) node;
            } else if (((ASTSelectionStatement) parentNode).otherwise == null) {
                ((ASTSelectionStatement) parentNode).otherwise = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTLabeledStatement.class) {
            if (((ASTLabeledStatement) parentNode).stat == null) {
                ((ASTLabeledStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).stat == null) {
                ((ASTIterationStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).stat == null) {
                ((ASTIterationDeclaredStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCompoundStatement.class) {
            if (((ASTCompoundStatement) parentNode).blockItems == null) {
                ((ASTCompoundStatement) parentNode).blockItems = new LinkedList<>();
            }
            ((ASTCompoundStatement) parentNode).blockItems.add(node);
            node.parent = parentNode;
        }
    }

    // ExpressionStatement
    @Override
    public void enterExpressionStatement(CParser.ExpressionStatementContext ctx) {
        ASTNode node = new ASTExpressionStatement();
        NodeStack.push(node);
    }

    @Override
    public void exitExpressionStatement(CParser.ExpressionStatementContext ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();
        if (((ASTExpressionStatement) node).exprs != null) {
            node.children.addAll(((ASTExpressionStatement) node).exprs);
        }
        if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).then == null) {
                ((ASTSelectionStatement) parentNode).then = (ASTStatement) node;
            } else if (((ASTSelectionStatement) parentNode).otherwise == null) {
                ((ASTSelectionStatement) parentNode).otherwise = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).stat == null) {
                ((ASTIterationStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTLabeledStatement.class) {
            if (((ASTLabeledStatement) parentNode).stat == null) {
                ((ASTLabeledStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).stat == null) {
                ((ASTIterationDeclaredStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCompoundStatement.class) {
            if (((ASTCompoundStatement) parentNode).blockItems == null) {
                ((ASTCompoundStatement) parentNode).blockItems = new LinkedList<>();
            }
            ((ASTCompoundStatement) parentNode).blockItems.add(node);
            node.parent = parentNode;
        }
    }

    // GotoStatement
    @Override
    public void enterJumpStatement_goto(CParser.JumpStatement_gotoContext ctx) {
        ASTNode node = new ASTGotoStatement();
        NodeStack.push(node);
    }

    @Override
    public void exitJumpStatement_goto(CParser.JumpStatement_gotoContext ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();
        if (((ASTGotoStatement) node).label != null) {
            node.children.add(((ASTGotoStatement) node).label);
        }
        if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).then == null) {
                ((ASTSelectionStatement) parentNode).then = (ASTStatement) node;
            } else if (((ASTSelectionStatement) parentNode).otherwise == null) {
                ((ASTSelectionStatement) parentNode).otherwise = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).stat == null) {
                ((ASTIterationStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTLabeledStatement.class) {
            if (((ASTLabeledStatement) parentNode).stat == null) {
                ((ASTLabeledStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).stat == null) {
                ((ASTIterationDeclaredStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCompoundStatement.class) {
            if (((ASTCompoundStatement) parentNode).blockItems == null) {
                ((ASTCompoundStatement) parentNode).blockItems = new LinkedList<>();
            }
            ((ASTCompoundStatement) parentNode).blockItems.add(node);
            node.parent = parentNode;
        }
    }

    // IterationDeclaredStatement
    @Override
    public void enterIterationStatement_forDeclared(CParser.IterationStatement_forDeclaredContext ctx) {
        ASTNode node = new ASTIterationDeclaredStatement();
        NodeStack.push(node);
    }

    @Override
    public void exitIterationStatement_forDeclared(CParser.IterationStatement_forDeclaredContext ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();
        if (((ASTIterationDeclaredStatement) node).init != null) {
            node.children.add(((ASTIterationDeclaredStatement) node).init);
        }
        if (((ASTIterationDeclaredStatement) node).cond != null) {
            node.children.addAll(((ASTIterationDeclaredStatement) node).cond);
        }
        if (((ASTIterationDeclaredStatement) node).step != null) {
            node.children.addAll(((ASTIterationDeclaredStatement) node).step);
        }
        if (((ASTIterationDeclaredStatement) node).stat != null) {
            node.children.add(((ASTIterationDeclaredStatement) node).stat);
        }
        if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).then == null) {
                ((ASTSelectionStatement) parentNode).then = (ASTStatement) node;
            } else if (((ASTSelectionStatement) parentNode).otherwise == null) {
                ((ASTSelectionStatement) parentNode).otherwise = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).stat == null) {
                ((ASTIterationStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTLabeledStatement.class) {
            if (((ASTLabeledStatement) parentNode).stat == null) {
                ((ASTLabeledStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).stat == null) {
                ((ASTIterationDeclaredStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCompoundStatement.class) {
            if (((ASTCompoundStatement) parentNode).blockItems == null) {
                ((ASTCompoundStatement) parentNode).blockItems = new LinkedList<>();
            }
            ((ASTCompoundStatement) parentNode).blockItems.add(node);
            node.parent = parentNode;
        }
    }

    // IterationStatement
    @Override
    public void enterIterationStatement_for(CParser.IterationStatement_forContext ctx) {
        ASTNode node = new ASTIterationStatement();
        NodeStack.push(node);
    }

    @Override
    public void exitIterationStatement_for(CParser.IterationStatement_forContext ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();
        if (((ASTIterationStatement) node).init != null) {
            node.children.addAll(((ASTIterationStatement) node).init);
        }
        if (((ASTIterationStatement) node).cond != null) {
            node.children.addAll(((ASTIterationStatement) node).cond);
        }
        if (((ASTIterationStatement) node).step != null) {
            node.children.addAll(((ASTIterationStatement) node).step);
        }
        if (((ASTIterationStatement) node).stat != null) {
            node.children.add(((ASTIterationStatement) node).stat);
        }
        if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).then == null) {
                ((ASTSelectionStatement) parentNode).then = (ASTStatement) node;
            } else if (((ASTSelectionStatement) parentNode).otherwise == null) {
                ((ASTSelectionStatement) parentNode).otherwise = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTLabeledStatement.class) {
            if (((ASTLabeledStatement) parentNode).stat == null) {
                ((ASTLabeledStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).stat == null) {
                ((ASTIterationStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).stat == null) {
                ((ASTIterationDeclaredStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCompoundStatement.class) {
            if (((ASTCompoundStatement) parentNode).blockItems == null) {
                ((ASTCompoundStatement) parentNode).blockItems = new LinkedList<>();
            }
            ((ASTCompoundStatement) parentNode).blockItems.add(node);
            node.parent = parentNode;
        }
    }

    // LabeledStatement
    @Override
    public void enterLabeledStatement(CParser.LabeledStatementContext ctx) {
        ASTNode node = new ASTLabeledStatement();
        NodeStack.push(node);
    }

    @Override
    public void exitLabeledStatement(CParser.LabeledStatementContext ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();
        if (((ASTLabeledStatement) node).label != null) {
            node.children.add(((ASTLabeledStatement) node).label);
        }
        if (((ASTLabeledStatement) node).stat != null) {
            node.children.add(((ASTLabeledStatement) node).stat);
        }
        if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).then == null) {
                ((ASTSelectionStatement) parentNode).then = (ASTStatement) node;
            } else if (((ASTSelectionStatement) parentNode).otherwise == null) {
                ((ASTSelectionStatement) parentNode).otherwise = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTLabeledStatement.class) {
            if (((ASTLabeledStatement) parentNode).stat == null) {
                ((ASTLabeledStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).stat == null) {
                ((ASTIterationStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).stat == null) {
                ((ASTIterationDeclaredStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCompoundStatement.class) {
            if (((ASTCompoundStatement) parentNode).blockItems == null) {
                ((ASTCompoundStatement) parentNode).blockItems = new LinkedList<>();
            }
            ((ASTCompoundStatement) parentNode).blockItems.add(node);
            node.parent = parentNode;
        }
    }

    // ReturnStatement
    @Override
    public void enterJumpStatement_return(CParser.JumpStatement_returnContext ctx) {
        ASTNode node = new ASTReturnStatement();
        NodeStack.push(node);
    }

    @Override
    public void exitJumpStatement_return(CParser.JumpStatement_returnContext ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();
        if (((ASTReturnStatement) node).expr != null) {
            node.children.addAll(((ASTReturnStatement) node).expr);
        }
        if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).then == null) {
                ((ASTSelectionStatement) parentNode).then = (ASTStatement) node;
            } else if (((ASTSelectionStatement) parentNode).otherwise == null) {
                ((ASTSelectionStatement) parentNode).otherwise = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).stat == null) {
                ((ASTIterationStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).stat == null) {
                ((ASTIterationDeclaredStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTLabeledStatement.class) {
            if (((ASTLabeledStatement) parentNode).stat == null) {
                ((ASTLabeledStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCompoundStatement.class) {
            if (((ASTCompoundStatement) parentNode).blockItems == null) {
                ((ASTCompoundStatement) parentNode).blockItems = new LinkedList<>();
            }
            ((ASTCompoundStatement) parentNode).blockItems.add(node);
            node.parent = parentNode;
        }
    }

    //SelectionStatement
    @Override
    public void enterSelectionStatement_if(CParser.SelectionStatement_ifContext ctx) {
        ASTNode node = new ASTSelectionStatement();
        NodeStack.push(node);
    }

    @Override
    public void exitSelectionStatement_if(CParser.SelectionStatement_ifContext ctx) {
        ASTNode node = NodeStack.pop();
        ASTNode parentNode = NodeStack.peek();
        if (((ASTSelectionStatement) node).cond != null) {
            node.children.addAll(((ASTSelectionStatement) node).cond);
        }
        if (((ASTSelectionStatement) node).then != null) {
            node.children.add(((ASTSelectionStatement) node).then);
        }
        if (((ASTSelectionStatement) node).otherwise != null) {
            node.children.add(((ASTSelectionStatement) node).otherwise);
        }
        if (parentNode.getClass() == ASTSelectionStatement.class) {
            if (((ASTSelectionStatement) parentNode).then == null) {
                ((ASTSelectionStatement) parentNode).then = (ASTStatement) node;
            } else if (((ASTSelectionStatement) parentNode).otherwise == null) {
                ((ASTSelectionStatement) parentNode).otherwise = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationStatement.class) {
            if (((ASTIterationStatement) parentNode).stat == null) {
                ((ASTIterationStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTLabeledStatement.class) {
            if (((ASTLabeledStatement) parentNode).stat == null) {
                ((ASTLabeledStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTIterationDeclaredStatement.class) {
            if (((ASTIterationDeclaredStatement) parentNode).stat == null) {
                ((ASTIterationDeclaredStatement) parentNode).stat = (ASTStatement) node;
            }
            node.parent = parentNode;
        } else if (parentNode.getClass() == ASTCompoundStatement.class) {
            if (((ASTCompoundStatement) parentNode).blockItems == null) {
                ((ASTCompoundStatement) parentNode).blockItems = new LinkedList<>();
            }
            ((ASTCompoundStatement) parentNode).blockItems.add(node);
            node.parent = parentNode;
        }
    }


    /**
     * 7. Terminal
     */
    @Override
    public void visitTerminal(TerminalNode node) {
        int id = node.getSymbol().getType();
        if (id == 12 || id == 13 || id == 16 || id == 17 || id == 18 || id == 20 || id == 24 || id == 25 || id == 26 || id == 32 || id == 38 || (id >= 43 && id <= 60) || id == 83) {
            return;
        }
        ASTNode parentNode = NodeStack.peek();
        if (parentNode.getClass() == ASTFunctionDefine.class) {
            if (((ASTFunctionDefine) parentNode).specifiers == null) {
                ((ASTFunctionDefine) parentNode).specifiers = new LinkedList<>();
            }
            ((ASTFunctionDefine) parentNode).specifiers.add(new ASTToken(node.getSymbol().getText(), node.getSymbol().getTokenIndex()));
        } else if (parentNode.getClass() == ASTParamsDeclarator.class) {
            if (((ASTParamsDeclarator) parentNode).specfiers == null) {
                ((ASTParamsDeclarator) parentNode).specfiers = new LinkedList<>();
            }
            ((ASTParamsDeclarator) parentNode).specfiers.add(new ASTToken(node.getSymbol().getText(), node.getSymbol().getTokenIndex()));
        } else if (parentNode.getClass() == ASTDeclaration.class) {
            if (((ASTDeclaration) parentNode).specifiers == null) {
                ((ASTDeclaration) parentNode).specifiers = new LinkedList<>();
            }
            ((ASTDeclaration) parentNode).specifiers.add(new ASTToken(node.getSymbol().getText(), node.getSymbol().getTokenIndex()));
        } else if (parentNode.getClass() == ASTIdentifier.class) {
            ((ASTIdentifier) parentNode).value = node.getSymbol().getText();
            ((ASTIdentifier) parentNode).tokenId = node.getSymbol().getTokenIndex();
        } else if (parentNode.getClass() == ASTIntegerConstant.class) {
            ((ASTIntegerConstant) parentNode).value = Integer.parseInt(node.getSymbol().getText());
            ((ASTIntegerConstant) parentNode).tokenId = node.getSymbol().getTokenIndex();
        } else if (parentNode.getClass() == ASTStringConstant.class) {
            ((ASTStringConstant) parentNode).value = (node.getSymbol().getText());
            ((ASTStringConstant) parentNode).tokenId = node.getSymbol().getTokenIndex();
        } else if (parentNode.getClass() == ASTBinaryExpression.class) {
            ((ASTBinaryExpression) parentNode).op = new ASTToken(node.getSymbol().getText(), node.getSymbol().getTokenIndex());
        } else if (parentNode.getClass() == ASTPostfixExpression.class) {
            ((ASTPostfixExpression) parentNode).op = new ASTToken(node.getSymbol().getText(), node.getSymbol().getTokenIndex());
        } else if (parentNode.getClass() == ASTUnaryExpression.class) {
            ((ASTUnaryExpression) parentNode).op = new ASTToken(node.getSymbol().getText(), node.getSymbol().getTokenIndex());
        } else if (parentNode.getClass() == ASTVariableDeclarator.class) {
            ((ASTVariableDeclarator) parentNode).identifier = new ASTIdentifier(node.getSymbol().getText(), node.getSymbol().getTokenIndex());
        } else if (parentNode.getClass() == ASTLabeledStatement.class) {
            if (((ASTLabeledStatement) parentNode).label == null) {
                ((ASTLabeledStatement) parentNode).label = new ASTIdentifier(node.getSymbol().getText(), node.getSymbol().getTokenIndex());
            }
        } else if (parentNode.getClass() == ASTGotoStatement.class) {
            if (((ASTGotoStatement) parentNode).label == null) {
                ((ASTGotoStatement) parentNode).label = new ASTIdentifier(node.getSymbol().getText(), node.getSymbol().getTokenIndex());
            }
        } else if (parentNode.getClass() == ASTReturnStatement.class) {
            if (((ASTReturnStatement) parentNode).expr == null) {
                ((ASTReturnStatement) parentNode).expr = new LinkedList<>();
            }
            ((ASTReturnStatement) parentNode).expr.add(new ASTIdentifier(node.getSymbol().getText(), node.getSymbol().getTokenIndex()));
        }
    }
}
