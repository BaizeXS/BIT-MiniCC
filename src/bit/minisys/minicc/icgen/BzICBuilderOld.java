package bit.minisys.minicc.icgen;

import bit.minisys.minicc.parser.ast.*;
import bit.minisys.minicc.semantic.FuncTable;
import bit.minisys.minicc.semantic.VarRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class BzICBuilderOld {
    public static List<FuncTable> procTable = new LinkedList<>();               // 函数表
    public static List<VarRecord> globalVarTable = new LinkedList<>();          // 全局变量表
    public static List<String> errorTable = new LinkedList<>();                 // 错误表
    public static int tmpRegID = 0;
    public static int tmpLabelID = 1;
    public static int ifLabelID = 0;
    public static Map<ASTNode, ASTNode> map;
    public static List<Quat> quats;
    public static List<VarRecord> scope = new LinkedList<>();
    public static Stack<List<VarRecord>> localVarTables = new Stack<>();
    private static ASTNode loopStartLabel = null;
    private static ASTNode loopNextLabel = null;
    private static ASTNode loopEndLabel = null;
    private static Map<String, ASTNode> labelMap;
    public String errorFilename;
    public String icFilename;
    public String symbolName;

    public BzICBuilderOld() {
        map = new HashMap<>();
        labelMap = new HashMap<>();
        quats = new LinkedList<>();
        tmpRegID = 0;
    }

    public static void visit(ASTCompilationUnit astCompilationUnit) {
        for (ASTNode child : astCompilationUnit.items) {
            if (child instanceof ASTDeclaration) {
                visit((ASTDeclaration) child);
            } else if (child instanceof ASTFunctionDefine) {
                visit((ASTFunctionDefine) child);
            }
        }
    }

    // 获取函数定义表
    public static void getFuncDeclara(FuncTable funcTable, ASTFunctionDeclarator declarator) {
        ASTVariableDeclarator variableDeclarator = (ASTVariableDeclarator) declarator.declarator;
        ASTIdentifier identifier = variableDeclarator.identifier;
        funcTable.funcName = identifier.value;
        funcTable.type = declarator.getType();
    }

    // 获取变量表
    public static void getVarTable(VarRecord varRecord, ASTInitList initList) {
        ASTNode child = initList.declarator;
        if (child.getClass() == ASTVariableDeclarator.class) {
            ASTIdentifier identifier = ((ASTVariableDeclarator) child).identifier;
            varRecord.name = identifier.value;
            varRecord.dimension = 1;
            varRecord.length = 1;
            varRecord.type = child.getType();
            if (initList.exprs != null) {
                varRecord.value = initList.exprs.get(0);
                visit(varRecord.value);
                ASTNode opnd = map.get(varRecord.value);
                quats.add(new Quat("=", identifier, opnd, null));
            }
        } else if (child.getClass() == ASTArrayDeclarator.class) {
            varRecord.type = child.getType();
            int dim = 0;
            int length = 1;
            while (child.getClass() != ASTVariableDeclarator.class) {
                ASTNode integer = ((ASTArrayDeclarator) child).expr;
                length = length * ((ASTIntegerConstant) integer).value;
                dim = dim + 1;
                child = ((ASTArrayDeclarator) child).declarator;
            }
            ASTIdentifier identifier = ((ASTVariableDeclarator) child).identifier;
            varRecord.name = identifier.value;
            varRecord.dimension = dim;
            varRecord.length = length;
        }
    }

    /**
     * Declaration
     * FunctionDefine
     */
    public static void visit(ASTDeclaration declaration) {
        if (declaration.parent.getClass() == ASTCompilationUnit.class) {
            // 全局声明
            ASTToken token = declaration.specifiers.get(0);
            for (ASTInitList initList : declaration.initLists) {
                ASTDeclarator declarator = initList.declarator;
                if (declarator.getClass() == ASTFunctionDeclarator.class) {
                    FuncTable funcTable = new FuncTable();
                    funcTable.varTable = new LinkedList<>();
                    funcTable.specifiers = declaration.specifiers.get(0).value;
                    getFuncDeclara(funcTable, (ASTFunctionDeclarator) declarator);
                    procTable.add(funcTable);
                } else {
                    VarRecord varRecord = new VarRecord();
                    varRecord.specifiers = token.value;
                    getVarTable(varRecord, initList);
                    // 检查重复定义
                    boolean alreadyExistFlag = true;
                    for (VarRecord var : globalVarTable) {
                        if (var.name.equals(varRecord.name)) {
                            alreadyExistFlag = false;
                            break;
                        }
                    }
                    if (!alreadyExistFlag) {
                        String Error = "ES02: var " + varRecord.name + " defined again\n";
                        errorTable.add(Error);
                    }
                    globalVarTable.add(varRecord);
                }
            }
        } else if (declaration.parent.getClass() == ASTIterationDeclaredStatement.class) {
            // 循环体内声明
            ASTToken token = declaration.specifiers.get(0);
            for (ASTInitList initList : declaration.initLists) {
                ASTDeclarator declarator = initList.declarator;
                VarRecord varRecord = new VarRecord();
                varRecord.specifiers = token.value;
                getVarTable(varRecord, initList);
                // 检查重复定义
                boolean alreadyExistFlag = true;
                for (VarRecord var : scope) {
                    if (var.name.equals(varRecord.name)) {
                        alreadyExistFlag = false;
                        break;
                    }
                }
                if (!alreadyExistFlag) {
                    String Error = "ES02: var " + varRecord.name + " defined_again\n";
                    errorTable.add(Error);
                }
                scope.add(varRecord);
            }
        } else if (declaration.parent.getClass() == ASTCompoundStatement.class) {
            // 函数体内定义
            ASTToken token = declaration.specifiers.get(0);
            for (ASTInitList initList : declaration.initLists) {
                ASTDeclarator declarator = initList.declarator;
                VarRecord varRecord = new VarRecord();
                varRecord.specifiers = token.value;
                getVarTable(varRecord, initList);
                // 检查重复定义
                boolean alreadyExistFlag = true;
                for (VarRecord var : scope) {
                    if (var.name.equals(varRecord.name)) {
                        alreadyExistFlag = false;
                        break;
                    }
                }
                if (!alreadyExistFlag) {
                    String Error = "ES02: var_" + varRecord.name + "_defined_again\n";
                    errorTable.add(Error);
                }
                scope.add(varRecord);
            }
        } else {
            ASTCompoundStatement compoundStat = (ASTCompoundStatement) declaration.parent;
            ASTFunctionDefine functionDefine = (ASTFunctionDefine) compoundStat.parent;
            ASTFunctionDeclarator functionDeclarator = (ASTFunctionDeclarator) functionDefine.declarator;
            ASTVariableDeclarator variableDeclarator = (ASTVariableDeclarator) functionDeclarator.declarator;
            ASTIdentifier identifier = variableDeclarator.identifier;
            for (ASTInitList initList : declaration.initLists) {
                VarRecord varRecord = new VarRecord();
                varRecord.specifiers = declaration.specifiers.get(0).value;
                getVarTable(varRecord, initList);
                for (FuncTable funcTable : procTable) {
                    if (funcTable.funcName.equals(identifier.value)) {
                        funcTable.varTable.add(varRecord);
                    }
                }
            }
        }
    }

    public static void visit(ASTFunctionDefine functionDefine) {
        FuncTable funcTable = new FuncTable();
        funcTable.varTable = new LinkedList<>();
        funcTable.type = functionDefine.getType();
        ASTFunctionDeclarator functionDeclarator = (ASTFunctionDeclarator) functionDefine.declarator;
        ASTVariableDeclarator variableDeclarator = (ASTVariableDeclarator) functionDeclarator.declarator;
        for (int i = 0; i < functionDeclarator.params.size(); i++) {
            VarRecord varRecord = new VarRecord();
            ASTParamsDeclarator paramsDeclarator = functionDeclarator.params.get(i);
            ASTVariableDeclarator params = (ASTVariableDeclarator) paramsDeclarator.declarator;
            ASTIntegerConstant integerConst = new ASTIntegerConstant();
            integerConst.value = i;
            ASTNode opnd1 = params.identifier;
            varRecord.type = paramsDeclarator.getType();
            varRecord.specifiers = paramsDeclarator.specfiers.get(0).value;
            varRecord.name = params.identifier.value;
            funcTable.varTable.add(varRecord);
            quats.add(new Quat("param", integerConst, opnd1, null));
        }
        ASTIdentifier identifier = variableDeclarator.identifier;
        funcTable.funcName = identifier.value;
        // 检查重复定义
        boolean existFlag = true;
        for (FuncTable func : procTable) {
            if (func.funcName.equals(funcTable.funcName)) {
                existFlag = false;
                break;
            }
        }
        if (!existFlag) {
            String ERROR = "ES02: Function " + funcTable.funcName + " defined again\n";
            errorTable.add(ERROR);
        }
        funcTable.specifiers = functionDefine.specifiers.get(0).value;
        procTable.add(funcTable);
        quats.add(new Quat("func", identifier, null, null));
        ASTCompoundStatement compoundStatement = functionDefine.body;
        visit(compoundStatement);
    }

    /**
     * Expression
     *
     * @JsonSubTypes.Type(value = ASTIdentifier.class, name = "Identifier"),
     * @JsonSubTypes.Type(value = ASTArrayAccess.class, name = "ArrayAccess"),
     * @JsonSubTypes.Type(value = ASTBinaryExpression.class, name = "BinaryExpression"),
     * @JsonSubTypes.Type(value = ASTCastExpression.class, name = "CastExpression"),              TODO
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


    public static void visit(ASTExpression expression) {
        if (expression instanceof ASTArrayAccess) {
            visit((ASTArrayAccess) expression);
        } else if (expression instanceof ASTBinaryExpression) {
            visit((ASTBinaryExpression) expression);
        } else if (expression instanceof ASTCastExpression) {
            visit(expression);
        } else if (expression instanceof ASTCharConstant) {
            visit(expression);
        } else if (expression instanceof ASTConditionExpression) {
            visit(expression);
        } else if (expression instanceof ASTFloatConstant) {
            visit(expression);
        } else if (expression instanceof ASTFunctionCall) {
            visit((ASTFunctionCall) expression);
        } else if (expression instanceof ASTIdentifier) {
            visit((ASTIdentifier) expression);
        } else if (expression instanceof ASTIntegerConstant) {
            visit((ASTIntegerConstant) expression);
        } else if (expression instanceof ASTMemberAccess) {
            visit(expression);
        } else if (expression instanceof ASTPostfixExpression) {
            visit((ASTPostfixExpression) expression);
        } else if (expression instanceof ASTStringConstant) {
            visit((ASTStringConstant) expression);
        } else if (expression instanceof ASTUnaryExpression) {
            visit((ASTUnaryExpression) expression);
        } else if (expression instanceof ASTUnaryTypename) {
            visit(expression);
        }
    }

    public static void visit(ASTUnaryExpression unaryExpression) {
        // 一元表达式处理
        String op = unaryExpression.op.value;
        visit(unaryExpression.expr);
        ASTNode opnd1 = map.get(unaryExpression.expr);
        quats.add(new Quat(op, opnd1, null, opnd1));
        map.put(unaryExpression, opnd1);
    }

    public static void visit(ASTPostfixExpression postfixExpression) {
        String op = postfixExpression.op.value;
        ASTNode res = null;
        ASTNode opnd1 = null;
        ASTNode opnd2 = null;
        visit(postfixExpression.expr);
        opnd1 = map.get(postfixExpression.expr);
        quats.add(new Quat(op, opnd1, null, opnd1));
        map.put(postfixExpression, opnd1);
    }

    public static void visit(ASTBinaryExpression binaryExpression) {
        String op = binaryExpression.op.value;
        ASTNode res = null;
        ASTNode opnd1 = null;
        ASTNode opnd2 = null;
        if (op.equals("=") || op.equals("+=") || op.equals("-=") || op.equals("*=") || op.equals("/=") || op.equals("%=") || op.equals("<<=") || op.equals(">>=") || op.equals("&=") || op.equals("^=") || op.equals("|=")) {
            visit(binaryExpression.expr1);
            res = map.get(binaryExpression.expr1);
            if (binaryExpression.expr2 instanceof ASTIdentifier) {
                opnd1 = binaryExpression.expr2;
            } else if (binaryExpression.expr2 instanceof ASTIntegerConstant) {
                opnd1 = binaryExpression.expr2;
            } else if (binaryExpression.expr2 instanceof ASTStringConstant) {
                opnd1 = binaryExpression.expr2;
            } else if (binaryExpression.expr2 instanceof ASTBinaryExpression) {
                ASTBinaryExpression value = (ASTBinaryExpression) binaryExpression.expr2;
                op = value.op.value;
                visit(value.expr1);
                opnd1 = map.get(value.expr1);
                visit(value.expr2);
                opnd2 = map.get(value.expr2);
            } else if (binaryExpression.expr2 instanceof ASTUnaryExpression) {
                ASTUnaryExpression value = (ASTUnaryExpression) binaryExpression.expr2;
                op = value.op.value;
                visit(value);
                opnd1 = map.get(value.expr);
            } else if (binaryExpression.expr2 instanceof ASTPostfixExpression) {
                ASTPostfixExpression value = (ASTPostfixExpression) binaryExpression.expr2;
                op = value.op.value;
                visit(value);
                opnd2 = map.get(value.expr);
            } else {
                visit(binaryExpression.expr2);
                opnd1 = map.get(binaryExpression.expr2);
                // else ...
            }
        } else {
            res = new TemporaryValue(++tmpRegID);
            VarRecord varRecord = new VarRecord();
            varRecord.name = ((TemporaryValue) res).name();
            varRecord.type = "VariableDeclarator";
            scope.add(varRecord);
            visit(binaryExpression.expr1);
            opnd1 = map.get(binaryExpression.expr1);
            visit(binaryExpression.expr2);
            opnd2 = map.get(binaryExpression.expr2);
        }
        // 构建四元式
        quats.add(new Quat(op, res, opnd1, opnd2));
        map.put(binaryExpression, res);
    }

    public static void visit(ASTIdentifier identifier) {
        // Set flag
        boolean defineFlag = false;
        // Search in the scope
        if (scope.size() > 0) {
            for (VarRecord varRecord : scope) {
                if (identifier.value.equals(varRecord.name)) {
                    defineFlag = true;
                    break;
                }
            }
        }
        // Search in the function table
        FuncTable funcTable = procTable.get(procTable.size() - 1);
        if (funcTable.varTable.size() > 0 && (!defineFlag)) {
            for (VarRecord varRecord : funcTable.varTable) {
                if (identifier.value.equals(varRecord.name)) {
                    defineFlag = true;
                    break;
                }
            }
        }
        // Search in the global variable table
        if (globalVarTable.size() > 0 && (!defineFlag)) {
            for (VarRecord varRecord : globalVarTable) {
                if (identifier.value.equals(varRecord.name)) {
                    defineFlag = true;
                    break;
                }
            }
        }
        // Check weather the variable is defined
        if (!defineFlag) {
            String ERROR = "ES01: " + identifier.value + "_is_not_defined\n";
            errorTable.add(ERROR);
        }
        map.put(identifier, identifier);
    }

    public static void visit(ASTIntegerConstant astIntegerConstant) {
        map.put(astIntegerConstant, astIntegerConstant);
    }

    public static void visit(ASTStringConstant stringConst) {
        map.put(stringConst, stringConst);
    }

    public static void visit(ASTFunctionCall funcCall) {
        String type = "";
        // Check and finish argList
        if (funcCall.argList != null) {
            for (int i = 0; i < funcCall.argList.size(); i++) {
                ASTExpression expression = funcCall.argList.get(i);
                visit(expression);
                ASTNode opnd1 = map.get(expression);
                ASTIntegerConstant integerConstant = new ASTIntegerConstant();
                integerConstant.value = i;
                quats.add(new Quat("param", integerConstant, opnd1, null));
            }
        }
        // Check function name
        boolean funcFlag = false;
        for (FuncTable funcTable : procTable) {
            if (funcTable.funcName.equals(((ASTIdentifier) funcCall.funcname).value)) {
                funcFlag = true;
                type = funcTable.specifiers;
                break;
            }
        }
        // ERROR Handler 01
        if (!funcFlag) {
            String ERROR = "ES01: " + ((ASTIdentifier) funcCall.funcname).value + "_is_not_defined\n";
            errorTable.add(ERROR);
        }
        // Call function
        ASTNode res = funcCall.funcname;
        String name = ((ASTIdentifier) funcCall.funcname).value;
        quats.add(new Quat("call", res, null, null));
        // Specialise Mars_GetInt
        if (type.equals("int") || name.equals("Mars_GetInt")) {
            TemporaryValue tempValue = new TemporaryValue(++tmpRegID);
            VarRecord varRecord = new VarRecord();
            varRecord.name = tempValue.name();
            varRecord.type = "VariableDeclarator";
            scope.add(varRecord);
            map.put(funcCall, tempValue);
            quats.add(new Quat("return", tempValue, null, null));
        } else {
            // quats.add(new Quat("return",null,null,null));
        }
    }

    public static void visit(ASTArrayAccess arrayAccess) {
        ASTNode res = null;
        ASTNode opnd1 = null;
        ASTNode opnd2 = null;
        visit(arrayAccess.elements.get(0));
        opnd2 = map.get(arrayAccess.elements.get(0));
        ASTExpression astExpression = arrayAccess.arrayName;
        if (astExpression.getClass() == ASTIdentifier.class) {
            boolean flag = false;
            if (scope.size() > 0) {
                for (VarRecord varRecord : scope) {
                    if (((ASTIdentifier) astExpression).value.equals(varRecord.name)) {
                        flag = true;
                        break;
                    }
                }
            }
            FuncTable funcTable = procTable.get(procTable.size() - 1);
            if (funcTable.varTable.size() > 0 && (!flag)) {
                for (VarRecord varRecord : funcTable.varTable) {
                    if (((ASTIdentifier) astExpression).value.equals(varRecord.name)) {
                        flag = true;
                        break;
                    }
                }
            }
            if (globalVarTable.size() > 0 && (!flag)) {
                for (VarRecord varRecord : globalVarTable) {
                    if (((ASTIdentifier) astExpression).value.equals(varRecord.name)) {
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                String ERROR = "ES01: " + ((ASTIdentifier) astExpression).value + "_is_not_defined\n";
                errorTable.add(ERROR);
            }
            opnd1 = astExpression;
            map.put(arrayAccess.arrayName, astExpression);
        } else {
            visit(astExpression);
            opnd1 = map.get(astExpression);
        }
        res = new TemporaryValue(++tmpRegID);
        VarRecord varRecord = new VarRecord();
        varRecord.name = ((TemporaryValue) res).name();
        varRecord.type = "VariableDeclarator";
        scope.add(varRecord);
        map.put(arrayAccess, res);
        quats.add(new Quat("[]", res, opnd1, opnd2));
    }

    /**
     * Statement
     *
     * @JsonSubTypes.Type(value = ASTBreakStatement.class,name = "BreakStatement"),
     * @JsonSubTypes.Type(value = ASTCompoundStatement.class,name = "CompoundStatement"),
     * @JsonSubTypes.Type(value = ASTContinueStatement.class,name="ContinueStatement"),
     * @JsonSubTypes.Type(value = ASTExpressionStatement.class,name = "ExpressionStatement"),
     * @JsonSubTypes.Type(value = ASTGotoStatement.class,name = "GotoStatement"),
     * @JsonSubTypes.Type(value = ASTIterationDeclaredStatement.class,name = "IterationDeclaredStatement"),
     * @JsonSubTypes.Type(value = ASTIterationStatement.class,name = "IterationStatement"),
     * @JsonSubTypes.Type(value = ASTLabeledStatement.class,name = "LabeledStatement"),
     * @JsonSubTypes.Type(value = ASTReturnStatement.class,name = "ReturnStatement"),
     * @JsonSubTypes.Type(value = ASTSelectionStatement.class,name = "SelectionStatement")
     */
    public static void visit(ASTStatement statement) {
        if (statement instanceof ASTIterationDeclaredStatement) {
            visit((ASTIterationDeclaredStatement) statement);
        } else if (statement instanceof ASTIterationStatement) {
            visit((ASTIterationStatement) statement);
        } else if (statement instanceof ASTCompoundStatement) {
            visit((ASTCompoundStatement) statement);
        } else if (statement instanceof ASTSelectionStatement) {
            visit((ASTSelectionStatement) statement);
        } else if (statement instanceof ASTExpressionStatement) {
            visit((ASTExpressionStatement) statement);
        } else if (statement instanceof ASTBreakStatement) {
            visit((ASTBreakStatement) statement);
        } else if (statement instanceof ASTContinueStatement) {
            visit((ASTContinueStatement) statement);
        } else if (statement instanceof ASTReturnStatement) {
            visit((ASTReturnStatement) statement);
        } else if (statement instanceof ASTGotoStatement) {
            visit((ASTGotoStatement) statement);
        } else if (statement instanceof ASTLabeledStatement) {
            visit((ASTLabeledStatement) statement);
        }
    }

    public static void visit(ASTSelectionStatement selectionStat) {
        ifLabelID++;
        ASTNode startCheckIfLabel = new BzLabelGenerator(ifLabelID, "If", 0);
        ASTNode otherwiseLabel = new BzLabelGenerator(ifLabelID, "Else", 0);
        ASTNode endIfLabel = new BzLabelGenerator(ifLabelID, "Endif", 0);
        // If Label
        quats.add(new Quat("Label", startCheckIfLabel, null, null));
        // Visit Condition
        for (ASTExpression expression : selectionStat.cond) {
            visit(expression);
        }
        ASTNode res = map.get(selectionStat.cond.get(0));
        // Check Otherwise
        if (selectionStat.otherwise != null) {
            quats.add(new Quat("JF", otherwiseLabel, res, null));
        } else {
            quats.add(new Quat("JF", endIfLabel, null, null));
        }
        // Visit Then
        visit(selectionStat.then);
        if (selectionStat.otherwise != null) {
            quats.add(new Quat("JMP", endIfLabel, null, null));
            quats.add(new Quat("Label", otherwiseLabel, null, null));
            visit(selectionStat.otherwise);
        }
        quats.add(new Quat("Label", endIfLabel, null, null));
    }

    public static void visit(ASTCompoundStatement compoundStat) {
        // Check if this compoundStatement is FunctionDefine
        boolean flag = compoundStat.parent.getClass() == ASTFunctionDefine.class;
        // Push new local variable table
        localVarTables.push(new LinkedList<>());
        // Visit BlockItems
        for (ASTNode blockItem : compoundStat.blockItems) {
            if (blockItem instanceof ASTDeclaration) {
                // Declaration
                scope = localVarTables.peek();
                visit((ASTDeclaration) blockItem);
            } else if (blockItem instanceof ASTStatement) {
                // Statement
                visit((ASTStatement) blockItem);
            }
        }
        // If this is FunctionDefine block, add it to the procTable
        if (flag) {
            FuncTable funcTable = procTable.get(procTable.size() - 1);
            scope = localVarTables.peek();
            if (scope.size() != 0) {
                funcTable.varTable.addAll(scope);
                scope.clear();
            }
        }
        localVarTables.pop();
    }

    public static void visit(ASTIterationDeclaredStatement iterationDeclaredStat) {
        // Stash Previous Loop Labels
        ASTNode preStartLabel = loopStartLabel;
        ASTNode preNextLabel = loopNextLabel;
        ASTNode preEndLabel = loopEndLabel;
        // Set Loop Check Label
        ASTNode loopCheckLabel = new BzLabelGenerator(tmpLabelID, "loopCheckLabel", 0);
        // Set Current Loop Label
        loopStartLabel = new BzLabelGenerator(tmpLabelID, "loopStartLabel", 0);
        loopNextLabel = new BzLabelGenerator(tmpLabelID, "loopNextLabel", 0);
        loopEndLabel = new BzLabelGenerator(tmpLabelID, "loopEndLabel", 0);
        tmpLabelID++;
        quats.add(new Quat("Label", loopStartLabel, null, null));
        // Visit Initiation
        // Differences
        if (iterationDeclaredStat.init != null) {
            visit(iterationDeclaredStat.init);
        }
        quats.add(new Quat("Label", loopCheckLabel, null, null));
        // Visit Condition
        if (iterationDeclaredStat.cond != null) {
            for (ASTExpression expression : iterationDeclaredStat.cond) {
                visit(expression);
            }
            ASTNode res = map.get(iterationDeclaredStat.cond.get(0));
            quats.add(new Quat("JF", loopEndLabel, res, null));
        }
        // Visit Statement
        if (iterationDeclaredStat.stat != null) {
            visit(iterationDeclaredStat.stat);
        }
        quats.add(new Quat("Label", loopNextLabel, null, null));
        // Visit Step
        if (iterationDeclaredStat.step != null) {
            for (ASTExpression expression : iterationDeclaredStat.step) {
                visit(expression);
            }
        }
        quats.add(new Quat("JMP", loopCheckLabel, null, null));
        quats.add(new Quat("Label", loopEndLabel, null, null));
        // Recovery Loop Labels
        loopStartLabel = preStartLabel;
        loopNextLabel = preNextLabel;
        loopEndLabel = preEndLabel;
        scope.remove(scope.size() - 1);
    }

    public static void visit(ASTIterationStatement iterationStat) {
        // Stash Previous Loop Label
        ASTNode preStartLabel = loopStartLabel;
        ASTNode preNextLabel = loopNextLabel;
        ASTNode preEndLabel = loopEndLabel;
        // Set Loop Check Label
        ASTNode loopCheckLabel = new BzLabelGenerator(tmpLabelID, "loopCheckLabel", 0);
        // Set Current Loop Label
        loopStartLabel = new BzLabelGenerator(tmpLabelID, "loopStartLabel", 0);
        loopNextLabel = new BzLabelGenerator(tmpLabelID, "loopNextLabel", 0);
        loopEndLabel = new BzLabelGenerator(tmpLabelID, "loopEndLabel", 0);
        tmpLabelID++;
        quats.add(new Quat("Label", loopStartLabel, null, null));
        // Visit Initiation
        if (iterationStat.init != null) {
            for (ASTExpression expression : iterationStat.init) {
                visit(expression);
            }
        }
        quats.add(new Quat("Label", loopCheckLabel, null, null));
        // Visit Condition
        if (iterationStat.cond != null) {
            for (ASTExpression expression : iterationStat.cond) {
                visit(expression);
            }
            ASTNode res = map.get(iterationStat.cond.get(0));
            quats.add(new Quat("JF", loopEndLabel, res, null));
        }
        // Visit Statement
        if (iterationStat.stat != null) {
            visit(iterationStat.stat);
        }
        quats.add(new Quat("Label", loopNextLabel, null, null));
        // Visit Step
        if (iterationStat.step != null) {
            for (ASTExpression expression : iterationStat.step) {
                visit(expression);
            }
        }
        // Loop End
        quats.add(new Quat("JMP", loopCheckLabel, null, null));
        quats.add(new Quat("Label", loopEndLabel, null, null));
        // Recovery Loop Labels
        loopStartLabel = preStartLabel;
        loopNextLabel = preNextLabel;
        loopEndLabel = preEndLabel;
        tmpLabelID--;
    }

    public static void visit(ASTExpressionStatement astExpressionStatement) {
        for (ASTExpression astExpression : astExpressionStatement.exprs) {
            visit(astExpression);
        }
    }

    public static void visit(ASTBreakStatement astBreakStatement) {
        if (loopEndLabel == null) {
            String ERROR = "ES03: break_is_not_in_loop\n";
            errorTable.add(ERROR);
        }
        quats.add(new Quat("JMP", loopEndLabel, null, null));
    }

    public static void visit(ASTContinueStatement astContinueStatement) {
        if (loopNextLabel == null) {
            String ERROR = "ES09: continue_is_not_in_loop\n";
            errorTable.add(ERROR);
        }
        quats.add(new Quat("JMP", loopNextLabel, null, null));
    }

    public static void visit(ASTReturnStatement returnStat) {
        if (returnStat.expr == null || returnStat.expr.isEmpty()) {
            quats.add(new Quat("RET", null, null, null));
        } else {
            for (ASTExpression astExpression : returnStat.expr) {
                visit(astExpression);
            }
            ASTNode res = map.get(returnStat.expr.get(0));
            quats.add(new Quat("RET", res, null, null));
        }
    }

    public static void visit(ASTLabeledStatement labeledStat) {
        if (labeledStat.label != null) {
            ASTNode res = new BzLabelGenerator(0, labeledStat.label.value, 0);
            labelMap.put(labeledStat.label.value, res);
            quats.add(new Quat("Label", res, null, null));
        }
        if (labeledStat.stat != null) {
            visit(labeledStat.stat);
        }
    }

    public static void visit(ASTGotoStatement astGotoStatement) {
        if (astGotoStatement.label != null) {
            ASTNode res = labelMap.get(astGotoStatement.label.value);
            quats.add(new Quat("goto", res, null, null));
        }
    }

    public static void printIC(String ICFilename) {
        BzICPrinter bzICPrinter = new BzICPrinter(quats);
        bzICPrinter.print(ICFilename);
    }

    public static void printSymbol(String symbolTable) {
        BzSymbolPrinter bzSymbolPrinter = new BzSymbolPrinter();
        bzSymbolPrinter.setProcTable(procTable);
        bzSymbolPrinter.setGlobalVarTable(globalVarTable);
        bzSymbolPrinter.printProcTable(symbolTable);
    }

    public void setOutFiles(String icFilename, String errorFilename, String symbolName) {
        this.icFilename = icFilename;
        this.errorFilename = errorFilename;
        this.symbolName = symbolName;
    }

    public void run(ASTNode node) throws IOException {
        visit((ASTCompilationUnit) node);
        printIC(icFilename);
        printSymbol(symbolName);
        FileWriter fileWriter = new FileWriter(new File(errorFilename));
        for (String Error : errorTable) {
            fileWriter.write(Error);
        }
        fileWriter.close();
    }
}