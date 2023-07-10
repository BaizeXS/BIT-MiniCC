package bit.minisys.minicc.icgen;

import bit.minisys.minicc.parser.ast.*;
import bit.minisys.minicc.semantic.FuncTable;
import bit.minisys.minicc.semantic.VarRecord;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class BzICBuilder implements ASTVisitor {
    // 语义分析
    public static List<FuncTable> procTable = new LinkedList<>();               // 函数列表
    public static List<VarRecord> globalVarTable = new LinkedList<>();          // 全局变量表
    public static List<String> errorTable = new LinkedList<>();                 // 错误列表
    public static List<VarRecord> scope = new LinkedList<>();                   // 域
    public static Stack<List<VarRecord>> localVarTables = new Stack<>();        // 局部变量表栈
    // ID
    public static int tmpRegID = 0;
    public static int tmpLabelID = 1;
    public static int ifLabelID = 0;
    // IR 构建
    private static Map<ASTNode, ASTNode> map = new HashMap<>();                 // 使用map存储子节点的返回值，key对应子节点，value对应返回值，value目前类别包括ASTIdentifier,ASTIntegerConstant,TemportaryValue...
    public static List<Quat> quats = new ArrayList<>();                        // 四元式列表
    private static Map<String, ASTNode> labelMap = new HashMap<>();             // 标签表
    // ASTNode Label
    private static ASTNode loopStartLabel = null;                               // 循环开始标签
    private static ASTNode loopNextLabel = null;                                // 循环迭代标签
    private static ASTNode loopEndLabel = null;                                 // 循环结束标签
    String icOutputFile;
    String symbolOutputFile;
    String errorOutputFile;

    public void setOutFiles(String icFilename, String symbolOutputFile, String errorFilename) {
        this.icOutputFile = icFilename;
        this.symbolOutputFile = symbolOutputFile;
        this.errorOutputFile = errorFilename;
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
    public void run(ASTNode node) throws Exception {
        visit((ASTCompilationUnit) node);
        printIC(icOutputFile);
        printSymbol(symbolOutputFile);
        FileWriter fileWriter = new FileWriter(new File(errorOutputFile));
        for (String Error : errorTable) {
            fileWriter.write(Error);
        }
        fileWriter.close();
    }

    public List<Quat> getQuats() {
        return quats;
    }
    public void getVarTable(VarRecord varRecord, ASTInitList initList) throws Exception {
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


    public void getFuncDeclara(FuncTable funcTable, ASTFunctionDeclarator declarator) {
        ASTVariableDeclarator variableDeclarator = (ASTVariableDeclarator) declarator.declarator;
        ASTIdentifier identifier = variableDeclarator.identifier;
        funcTable.funcName = identifier.value;
        funcTable.type = declarator.getType();
    }



    @Override
    public void visit(ASTCompilationUnit program) throws Exception {
        for (ASTNode node : program.items) {
            if (node instanceof ASTFunctionDefine) {
                visit((ASTFunctionDefine) node);
            } else if (node instanceof ASTDeclaration) {
                visit((ASTDeclaration) node);
            }
        }
    }
    /**
     * Declaration
     * FunctionDefine
     * InitList
     * Typename
     * Token
     */
    @Override
    public void visit(ASTDeclaration declaration) throws Exception {
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
                    String Error = "ES02: var " + varRecord.name + " defined again\n";
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

    @Override
    public void visit(ASTFunctionDefine functionDefine) throws Exception {
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
        // Visit function body
        visit(functionDefine.body);
    }

    @Override
    public void visit(ASTInitList initList) throws Exception {
        // TODO: To be finished. This is contained in another function.
    }

    @Override
    public void visit(ASTTypename typename) throws Exception {
        // TODO: To be finished.
    }

    @Override
    public void visit(ASTToken token) throws Exception {
        // TODO: To be finished. This is contained in another function.
    }

    /**
     * Declarator
     *
     * @JsonSubTypes.Type(value = ASTArrayDeclarator.class, name = "ArrayDeclarator"),
     * @JsonSubTypes.Type(value = ASTFunctionDeclarator.class, name = "FunctionDeclarator"),
     * @JsonSubTypes.Type(value = ASTParamsDeclarator.class, name = "ParamsDeclarator"),
     * @JsonSubTypes.Type(value = ASTVariableDeclarator.class, name = "VariableDeclarator")
     */
    @Override
    public void visit(ASTDeclarator declarator) throws Exception {
        // TODO: To be finished. This is contained in another function.
    }

    @Override
    public void visit(ASTArrayDeclarator arrayDeclarator) throws Exception {
        // TODO: To be finished. This is contained in another function.
    }

    @Override
    public void visit(ASTVariableDeclarator variableDeclarator) throws Exception {
        // TODO: To be finished. This is contained in another function.
    }

    @Override
    public void visit(ASTFunctionDeclarator functionDeclarator) throws Exception {
        // TODO: To be finished. This is contained in another function.
    }

    @Override
    public void visit(ASTParamsDeclarator paramsDeclarator) throws Exception {
        // TODO: To be finished. This is contained in another function.
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
    @Override
    public void visit(ASTExpression expression) throws Exception {
        if (expression instanceof ASTArrayAccess) {
            visit((ASTArrayAccess) expression);
        } else if (expression instanceof ASTBinaryExpression) {
            visit((ASTBinaryExpression) expression);
        } else if (expression instanceof ASTCastExpression) {
            visit((ASTCastExpression) expression);
        } else if (expression instanceof ASTCharConstant) {
            visit((ASTCharConstant) expression);
        } else if (expression instanceof ASTConditionExpression) {
            visit((ASTConditionExpression) expression);
        } else if (expression instanceof ASTFloatConstant) {
            visit((ASTFloatConstant) expression);
        } else if (expression instanceof ASTFunctionCall) {
            visit((ASTFunctionCall) expression);
        } else if (expression instanceof ASTIdentifier) {
            visit((ASTIdentifier) expression);
        } else if (expression instanceof ASTIntegerConstant) {
            visit((ASTIntegerConstant) expression);
        } else if (expression instanceof ASTMemberAccess) {
            visit((ASTMemberAccess) expression);
        } else if (expression instanceof ASTPostfixExpression) {
            visit((ASTPostfixExpression) expression);
        } else if (expression instanceof ASTStringConstant) {
            visit((ASTStringConstant) expression);
        } else if (expression instanceof ASTUnaryExpression) {
            visit((ASTUnaryExpression) expression);
        } else if (expression instanceof ASTUnaryTypename) {
            visit((ASTUnaryTypename) expression);
        }
    }

    @Override
    public void visit(ASTArrayAccess arrayAccess) throws Exception {
        TemporaryValue res = null;
        ASTNode opnd1 = null;
        ASTNode opnd2 = null;
        visit(arrayAccess.elements.get(0));
        opnd2 = map.get(arrayAccess.elements.get(0));
        // TODO: To create another function to check existence
        ASTExpression arrayName = arrayAccess.arrayName;
        if (arrayName.getClass() == ASTIdentifier.class) {
            boolean existFlag = false;
            if (scope.size() > 0) {
                for (VarRecord varRecord : scope) {
                    if (((ASTIdentifier) arrayName).value.equals(varRecord.name)) {
                        existFlag = true;
                        break;
                    }
                }
            }
            FuncTable funcTable = procTable.get(procTable.size() - 1);
            if (funcTable.varTable.size() > 0 && (!existFlag)) {
                for (VarRecord varRecord : funcTable.varTable) {
                    if (((ASTIdentifier) arrayName).value.equals(varRecord.name)) {
                        existFlag = true;
                        break;
                    }
                }
            }
            if (globalVarTable.size() > 0 && (!existFlag)) {
                for (VarRecord varRecord : globalVarTable) {
                    if (((ASTIdentifier) arrayName).value.equals(varRecord.name)) {
                        existFlag = true;
                        break;
                    }
                }
            }
            if (!existFlag) {
                String ERROR = "ES01: " + ((ASTIdentifier) arrayName).value + " is not defined\n";
                errorTable.add(ERROR);
            }
            opnd1 = arrayName;
            map.put(arrayAccess.arrayName, arrayName);
        } else {
            visit(arrayName);
            opnd1 = map.get(arrayName);
        }
        res = new TemporaryValue(++tmpRegID);
        VarRecord varRecord = new VarRecord();
        varRecord.name = res.name();
        varRecord.type = "VariableDeclarator";
        scope.add(varRecord);
        map.put(arrayAccess, res);
        quats.add(new Quat("[]", res, opnd1, opnd2));
    }

    @Override
    public void visit(ASTBinaryExpression binaryExpression) throws Exception {
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

    @Override
    public void visit(ASTCastExpression castExpression) throws Exception {
        // TODO: To be finished.
    }

    @Override
    public void visit(ASTCharConstant charConst) throws Exception {
        // TODO: To be finished.
    }

    @Override
    public void visit(ASTConditionExpression conditionExpression) throws Exception {
        // TODO: To be finished.
    }

    @Override
    public void visit(ASTFloatConstant floatConst) throws Exception {
        // TODO: To be finished.
    }

    @Override
    public void visit(ASTFunctionCall funcCall) throws Exception {
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
            String ERROR = "ES01: " + ((ASTIdentifier) funcCall.funcname).value + " is not defined\n";
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

    @Override
    public void visit(ASTIdentifier identifier) throws Exception {
        // Check ERROR 01
        //-----Check Start
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
        //-----Check End
        // Add identifier to the map
        map.put(identifier, identifier);
    }

    @Override
    public void visit(ASTIntegerConstant intConst) throws Exception {
        map.put(intConst, intConst);
    }

    @Override
    public void visit(ASTMemberAccess memberAccess) throws Exception {
        // TODO: To be finished.
    }

    @Override
    public void visit(ASTPostfixExpression postfixExpression) throws Exception {
        // 后缀表达式处理(Similar to unaryExpression)
        String op = postfixExpression.op.value;
        ASTNode res = null;
        ASTNode opnd1 = null;
        ASTNode opnd2 = null;
        visit(postfixExpression.expr);
        opnd1 = map.get(postfixExpression.expr);
        quats.add(new Quat(op, opnd1, null, opnd1));
        map.put(postfixExpression, opnd1);
    }

    @Override
    public void visit(ASTStringConstant stringConst) throws Exception {
        map.put(stringConst, stringConst);
    }

    @Override
    public void visit(ASTUnaryExpression unaryExpression) throws Exception {
        // 一元表达式处理
        String op = unaryExpression.op.value;
        ASTNode res = null;
        ASTNode opnd1 = null;
        ASTNode opnd2 = null;
        visit(unaryExpression.expr);
        opnd1 = map.get(unaryExpression.expr);
        quats.add(new Quat(op, opnd1, null, opnd1));
        map.put(unaryExpression, opnd1);
    }

    @Override
    public void visit(ASTUnaryTypename unaryTypename) throws Exception {
        // TODO: To be finished.
    }

    /**
     * Statement
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
    @Override
    public void visit(ASTStatement statement) throws Exception {
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

    @Override
    public void visit(ASTIterationDeclaredStatement iterationDeclaredStat) throws Exception {
        // IterationDeclaredStatement 处理
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

    @Override
    public void visit(ASTIterationStatement iterationStat) throws Exception {
        // IterationStatement 处理
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

    @Override
    public void visit(ASTCompoundStatement compoundStat) throws Exception {
        // CompoundStatement 处理
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

    @Override
    public void visit(ASTSelectionStatement selectionStat) throws Exception {
        // 分支语句处理
        ifLabelID += 1;
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

    @Override
    public void visit(ASTExpressionStatement expressionStat) throws Exception {
        // ExpressionStatement 处理
        for (ASTExpression expression : expressionStat.exprs) {
            visit(expression);
        }
    }

    @Override
    public void visit(ASTBreakStatement breakStat) throws Exception {
        // Break 语句处理
        // 检查是否在循环体内(End Label 不为空)
        if (loopEndLabel == null) {
            String ERROR = "ES03: break_is_not_in_loop\n";
            errorTable.add(ERROR);
        }
        quats.add(new Quat("JMP", loopEndLabel, null, null));
    }

    @Override
    public void visit(ASTContinueStatement continueStatement) throws Exception {
        // Continue 语句处理
        // 检查是否在循环体内(Next Label 不为空)
        if (loopNextLabel == null) {
            String ERROR = "ES09: continue_is_not_in_loop\n";
            errorTable.add(ERROR);
        }
        quats.add(new Quat("JMP", loopNextLabel, null, null));
    }

    @Override
    public void visit(ASTReturnStatement returnStat) throws Exception {
        // Return 语句处理
        if (returnStat.expr == null || returnStat.expr.isEmpty()) {
            // 无返回值或返回值为空时
            quats.add(new Quat("RET", null, null, null));
        } else {
            // 返回值不为空时
            for (ASTExpression expression : returnStat.expr) {
                visit(expression);
            }
            ASTNode res = map.get(returnStat.expr.get(0));
            quats.add(new Quat("RET", res, null, null));
        }
    }

    @Override
    public void visit(ASTGotoStatement gotoStat) throws Exception {
        // Goto 语句处理
        if (gotoStat.label != null) {
            // 标签不为空时
            ASTNode res = labelMap.get(gotoStat.label.value);
            quats.add(new Quat("goto", res, null, null));
        }
    }

    @Override
    public void visit(ASTLabeledStatement labeledStat) throws Exception {
        if (labeledStat.label != null) {
            ASTNode res = new BzLabelGenerator(0, labeledStat.label.value, 0);
            labelMap.put(labeledStat.label.value, res);
            quats.add(new Quat("Label", res, null, null));
        }
        if (labeledStat.stat != null) {
            visit(labeledStat.stat);
        }
    }
}
