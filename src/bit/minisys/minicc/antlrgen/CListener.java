// Generated from C.g4 by ANTLR 4.8
package bit.minisys.minicc.antlrgen;

import org.antlr.v4.runtime.tree.ParseTreeListener;

import java.io.IOException;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CParser}.
 */
public interface CListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpression(CParser.PrimaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpression(CParser.PrimaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#tokenId}.
	 * @param ctx the parse tree
	 */
	void enterTokenId(CParser.TokenIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#tokenId}.
	 * @param ctx the parse tree
	 */
	void exitTokenId(CParser.TokenIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#tokenConstant}.
	 * @param ctx the parse tree
	 */
	void enterTokenConstant(CParser.TokenConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#tokenConstant}.
	 * @param ctx the parse tree
	 */
	void exitTokenConstant(CParser.TokenConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#tokenStringLiteral}.
	 * @param ctx the parse tree
	 */
	void enterTokenStringLiteral(CParser.TokenStringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#tokenStringLiteral}.
	 * @param ctx the parse tree
	 */
	void exitTokenStringLiteral(CParser.TokenStringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#genericSelection}.
	 * @param ctx the parse tree
	 */
	void enterGenericSelection(CParser.GenericSelectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#genericSelection}.
	 * @param ctx the parse tree
	 */
	void exitGenericSelection(CParser.GenericSelectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#genericAssocList}.
	 * @param ctx the parse tree
	 */
	void enterGenericAssocList(CParser.GenericAssocListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#genericAssocList}.
	 * @param ctx the parse tree
	 */
	void exitGenericAssocList(CParser.GenericAssocListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#genericAssociation}.
	 * @param ctx the parse tree
	 */
	void enterGenericAssociation(CParser.GenericAssociationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#genericAssociation}.
	 * @param ctx the parse tree
	 */
	void exitGenericAssociation(CParser.GenericAssociationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code postfixExpression_arrayaccess}
	 * labeled alternative in {@link CParser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostfixExpression_arrayaccess(CParser.PostfixExpression_arrayaccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code postfixExpression_arrayaccess}
	 * labeled alternative in {@link CParser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostfixExpression_arrayaccess(CParser.PostfixExpression_arrayaccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code postfixExpression_point}
	 * labeled alternative in {@link CParser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostfixExpression_point(CParser.PostfixExpression_pointContext ctx);
	/**
	 * Exit a parse tree produced by the {@code postfixExpression_point}
	 * labeled alternative in {@link CParser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostfixExpression_point(CParser.PostfixExpression_pointContext ctx);
	/**
	 * Enter a parse tree produced by the {@code postfixExpression_funcall}
	 * labeled alternative in {@link CParser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostfixExpression_funcall(CParser.PostfixExpression_funcallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code postfixExpression_funcall}
	 * labeled alternative in {@link CParser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostfixExpression_funcall(CParser.PostfixExpression_funcallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code postfixExpression_pass}
	 * labeled alternative in {@link CParser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostfixExpression_pass(CParser.PostfixExpression_passContext ctx);
	/**
	 * Exit a parse tree produced by the {@code postfixExpression_pass}
	 * labeled alternative in {@link CParser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostfixExpression_pass(CParser.PostfixExpression_passContext ctx);
	/**
	 * Enter a parse tree produced by the {@code postfixExpression_member}
	 * labeled alternative in {@link CParser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostfixExpression_member(CParser.PostfixExpression_memberContext ctx);
	/**
	 * Exit a parse tree produced by the {@code postfixExpression_member}
	 * labeled alternative in {@link CParser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostfixExpression_member(CParser.PostfixExpression_memberContext ctx);
	/**
	 * Enter a parse tree produced by the {@code postfixExpression_}
	 * labeled alternative in {@link CParser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostfixExpression_(CParser.PostfixExpression_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code postfixExpression_}
	 * labeled alternative in {@link CParser#postfixExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostfixExpression_(CParser.PostfixExpression_Context ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#argumentExpressionList}.
	 * @param ctx the parse tree
	 */
	void enterArgumentExpressionList(CParser.ArgumentExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#argumentExpressionList}.
	 * @param ctx the parse tree
	 */
	void exitArgumentExpressionList(CParser.ArgumentExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unaryExpression_pass}
	 * labeled alternative in {@link CParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression_pass(CParser.UnaryExpression_passContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unaryExpression_pass}
	 * labeled alternative in {@link CParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression_pass(CParser.UnaryExpression_passContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unaryExpression_}
	 * labeled alternative in {@link CParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression_(CParser.UnaryExpression_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code unaryExpression_}
	 * labeled alternative in {@link CParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression_(CParser.UnaryExpression_Context ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#unaryOperator}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOperator(CParser.UnaryOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#unaryOperator}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOperator(CParser.UnaryOperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code castExpression_pass}
	 * labeled alternative in {@link CParser#castExpression}.
	 * @param ctx the parse tree
	 */
	void enterCastExpression_pass(CParser.CastExpression_passContext ctx);
	/**
	 * Exit a parse tree produced by the {@code castExpression_pass}
	 * labeled alternative in {@link CParser#castExpression}.
	 * @param ctx the parse tree
	 */
	void exitCastExpression_pass(CParser.CastExpression_passContext ctx);
	/**
	 * Enter a parse tree produced by the {@code castExpression_}
	 * labeled alternative in {@link CParser#castExpression}.
	 * @param ctx the parse tree
	 */
	void enterCastExpression_(CParser.CastExpression_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code castExpression_}
	 * labeled alternative in {@link CParser#castExpression}.
	 * @param ctx the parse tree
	 */
	void exitCastExpression_(CParser.CastExpression_Context ctx);
	/**
	 * Enter a parse tree produced by the {@code multiplicativeExpression_}
	 * labeled alternative in {@link CParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression_(CParser.MultiplicativeExpression_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code multiplicativeExpression_}
	 * labeled alternative in {@link CParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression_(CParser.MultiplicativeExpression_Context ctx);
	/**
	 * Enter a parse tree produced by the {@code multiplicativeExpression_pass}
	 * labeled alternative in {@link CParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression_pass(CParser.MultiplicativeExpression_passContext ctx);
	/**
	 * Exit a parse tree produced by the {@code multiplicativeExpression_pass}
	 * labeled alternative in {@link CParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression_pass(CParser.MultiplicativeExpression_passContext ctx);
	/**
	 * Enter a parse tree produced by the {@code additiveExpression_pass}
	 * labeled alternative in {@link CParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression_pass(CParser.AdditiveExpression_passContext ctx);
	/**
	 * Exit a parse tree produced by the {@code additiveExpression_pass}
	 * labeled alternative in {@link CParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression_pass(CParser.AdditiveExpression_passContext ctx);
	/**
	 * Enter a parse tree produced by the {@code additiveExpression_}
	 * labeled alternative in {@link CParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression_(CParser.AdditiveExpression_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code additiveExpression_}
	 * labeled alternative in {@link CParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression_(CParser.AdditiveExpression_Context ctx);
	/**
	 * Enter a parse tree produced by the {@code shiftExpression_}
	 * labeled alternative in {@link CParser#shiftExpression}.
	 * @param ctx the parse tree
	 */
	void enterShiftExpression_(CParser.ShiftExpression_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code shiftExpression_}
	 * labeled alternative in {@link CParser#shiftExpression}.
	 * @param ctx the parse tree
	 */
	void exitShiftExpression_(CParser.ShiftExpression_Context ctx);
	/**
	 * Enter a parse tree produced by the {@code shiftExpression_pass}
	 * labeled alternative in {@link CParser#shiftExpression}.
	 * @param ctx the parse tree
	 */
	void enterShiftExpression_pass(CParser.ShiftExpression_passContext ctx);
	/**
	 * Exit a parse tree produced by the {@code shiftExpression_pass}
	 * labeled alternative in {@link CParser#shiftExpression}.
	 * @param ctx the parse tree
	 */
	void exitShiftExpression_pass(CParser.ShiftExpression_passContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relationalExpression_pass}
	 * labeled alternative in {@link CParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpression_pass(CParser.RelationalExpression_passContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relationalExpression_pass}
	 * labeled alternative in {@link CParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpression_pass(CParser.RelationalExpression_passContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relationalExpression_}
	 * labeled alternative in {@link CParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpression_(CParser.RelationalExpression_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code relationalExpression_}
	 * labeled alternative in {@link CParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpression_(CParser.RelationalExpression_Context ctx);
	/**
	 * Enter a parse tree produced by the {@code equalityExpression_pass}
	 * labeled alternative in {@link CParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpression_pass(CParser.EqualityExpression_passContext ctx);
	/**
	 * Exit a parse tree produced by the {@code equalityExpression_pass}
	 * labeled alternative in {@link CParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpression_pass(CParser.EqualityExpression_passContext ctx);
	/**
	 * Enter a parse tree produced by the {@code equalityExpression_}
	 * labeled alternative in {@link CParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpression_(CParser.EqualityExpression_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code equalityExpression_}
	 * labeled alternative in {@link CParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpression_(CParser.EqualityExpression_Context ctx);
	/**
	 * Enter a parse tree produced by the {@code andExpression_pass}
	 * labeled alternative in {@link CParser#andExpression}.
	 * @param ctx the parse tree
	 */
	void enterAndExpression_pass(CParser.AndExpression_passContext ctx);
	/**
	 * Exit a parse tree produced by the {@code andExpression_pass}
	 * labeled alternative in {@link CParser#andExpression}.
	 * @param ctx the parse tree
	 */
	void exitAndExpression_pass(CParser.AndExpression_passContext ctx);
	/**
	 * Enter a parse tree produced by the {@code andExpression_}
	 * labeled alternative in {@link CParser#andExpression}.
	 * @param ctx the parse tree
	 */
	void enterAndExpression_(CParser.AndExpression_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code andExpression_}
	 * labeled alternative in {@link CParser#andExpression}.
	 * @param ctx the parse tree
	 */
	void exitAndExpression_(CParser.AndExpression_Context ctx);
	/**
	 * Enter a parse tree produced by the {@code exclusiveOrExpression_}
	 * labeled alternative in {@link CParser#exclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterExclusiveOrExpression_(CParser.ExclusiveOrExpression_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code exclusiveOrExpression_}
	 * labeled alternative in {@link CParser#exclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitExclusiveOrExpression_(CParser.ExclusiveOrExpression_Context ctx);
	/**
	 * Enter a parse tree produced by the {@code exclusiveOrExpression_pass}
	 * labeled alternative in {@link CParser#exclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterExclusiveOrExpression_pass(CParser.ExclusiveOrExpression_passContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exclusiveOrExpression_pass}
	 * labeled alternative in {@link CParser#exclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitExclusiveOrExpression_pass(CParser.ExclusiveOrExpression_passContext ctx);
	/**
	 * Enter a parse tree produced by the {@code inclusiveOrExpression_pass}
	 * labeled alternative in {@link CParser#inclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterInclusiveOrExpression_pass(CParser.InclusiveOrExpression_passContext ctx);
	/**
	 * Exit a parse tree produced by the {@code inclusiveOrExpression_pass}
	 * labeled alternative in {@link CParser#inclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitInclusiveOrExpression_pass(CParser.InclusiveOrExpression_passContext ctx);
	/**
	 * Enter a parse tree produced by the {@code inclusiveOrExpression_}
	 * labeled alternative in {@link CParser#inclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterInclusiveOrExpression_(CParser.InclusiveOrExpression_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code inclusiveOrExpression_}
	 * labeled alternative in {@link CParser#inclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitInclusiveOrExpression_(CParser.InclusiveOrExpression_Context ctx);
	/**
	 * Enter a parse tree produced by the {@code logicalAndExpression_pass}
	 * labeled alternative in {@link CParser#logicalAndExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalAndExpression_pass(CParser.LogicalAndExpression_passContext ctx);
	/**
	 * Exit a parse tree produced by the {@code logicalAndExpression_pass}
	 * labeled alternative in {@link CParser#logicalAndExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalAndExpression_pass(CParser.LogicalAndExpression_passContext ctx);
	/**
	 * Enter a parse tree produced by the {@code logicalAndExpression_}
	 * labeled alternative in {@link CParser#logicalAndExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalAndExpression_(CParser.LogicalAndExpression_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code logicalAndExpression_}
	 * labeled alternative in {@link CParser#logicalAndExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalAndExpression_(CParser.LogicalAndExpression_Context ctx);
	/**
	 * Enter a parse tree produced by the {@code logicalOrExpression_}
	 * labeled alternative in {@link CParser#logicalOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOrExpression_(CParser.LogicalOrExpression_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code logicalOrExpression_}
	 * labeled alternative in {@link CParser#logicalOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOrExpression_(CParser.LogicalOrExpression_Context ctx);
	/**
	 * Enter a parse tree produced by the {@code logicalOrExpression_pass}
	 * labeled alternative in {@link CParser#logicalOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOrExpression_pass(CParser.LogicalOrExpression_passContext ctx);
	/**
	 * Exit a parse tree produced by the {@code logicalOrExpression_pass}
	 * labeled alternative in {@link CParser#logicalOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOrExpression_pass(CParser.LogicalOrExpression_passContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression(CParser.ConditionalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression(CParser.ConditionalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentExpression_pass}
	 * labeled alternative in {@link CParser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentExpression_pass(CParser.AssignmentExpression_passContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentExpression_pass}
	 * labeled alternative in {@link CParser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentExpression_pass(CParser.AssignmentExpression_passContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentExpression_}
	 * labeled alternative in {@link CParser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentExpression_(CParser.AssignmentExpression_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentExpression_}
	 * labeled alternative in {@link CParser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentExpression_(CParser.AssignmentExpression_Context ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator(CParser.AssignmentOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator(CParser.AssignmentOperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expression_}
	 * labeled alternative in {@link CParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression_(CParser.Expression_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code expression_}
	 * labeled alternative in {@link CParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression_(CParser.Expression_Context ctx);
	/**
	 * Enter a parse tree produced by the {@code expression_pass}
	 * labeled alternative in {@link CParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression_pass(CParser.Expression_passContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expression_pass}
	 * labeled alternative in {@link CParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression_pass(CParser.Expression_passContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void enterConstantExpression(CParser.ConstantExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void exitConstantExpression(CParser.ConstantExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(CParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(CParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#declarationSpecifiers}.
	 * @param ctx the parse tree
	 */
	void enterDeclarationSpecifiers(CParser.DeclarationSpecifiersContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#declarationSpecifiers}.
	 * @param ctx the parse tree
	 */
	void exitDeclarationSpecifiers(CParser.DeclarationSpecifiersContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#declarationSpecifiers2}.
	 * @param ctx the parse tree
	 */
	void enterDeclarationSpecifiers2(CParser.DeclarationSpecifiers2Context ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#declarationSpecifiers2}.
	 * @param ctx the parse tree
	 */
	void exitDeclarationSpecifiers2(CParser.DeclarationSpecifiers2Context ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#declarationSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterDeclarationSpecifier(CParser.DeclarationSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#declarationSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitDeclarationSpecifier(CParser.DeclarationSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#initDeclaratorList}.
	 * @param ctx the parse tree
	 */
	void enterInitDeclaratorList(CParser.InitDeclaratorListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#initDeclaratorList}.
	 * @param ctx the parse tree
	 */
	void exitInitDeclaratorList(CParser.InitDeclaratorListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#initDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterInitDeclarator(CParser.InitDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#initDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitInitDeclarator(CParser.InitDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#storageClassSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterStorageClassSpecifier(CParser.StorageClassSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#storageClassSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitStorageClassSpecifier(CParser.StorageClassSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code typeSpecifier_}
	 * labeled alternative in {@link CParser#typeSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterTypeSpecifier_(CParser.TypeSpecifier_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code typeSpecifier_}
	 * labeled alternative in {@link CParser#typeSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitTypeSpecifier_(CParser.TypeSpecifier_Context ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#structOrUnionSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterStructOrUnionSpecifier(CParser.StructOrUnionSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#structOrUnionSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitStructOrUnionSpecifier(CParser.StructOrUnionSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#structOrUnion}.
	 * @param ctx the parse tree
	 */
	void enterStructOrUnion(CParser.StructOrUnionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#structOrUnion}.
	 * @param ctx the parse tree
	 */
	void exitStructOrUnion(CParser.StructOrUnionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#structDeclarationList}.
	 * @param ctx the parse tree
	 */
	void enterStructDeclarationList(CParser.StructDeclarationListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#structDeclarationList}.
	 * @param ctx the parse tree
	 */
	void exitStructDeclarationList(CParser.StructDeclarationListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#structDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterStructDeclaration(CParser.StructDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#structDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitStructDeclaration(CParser.StructDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#specifierQualifierList}.
	 * @param ctx the parse tree
	 */
	void enterSpecifierQualifierList(CParser.SpecifierQualifierListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#specifierQualifierList}.
	 * @param ctx the parse tree
	 */
	void exitSpecifierQualifierList(CParser.SpecifierQualifierListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#structDeclaratorList}.
	 * @param ctx the parse tree
	 */
	void enterStructDeclaratorList(CParser.StructDeclaratorListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#structDeclaratorList}.
	 * @param ctx the parse tree
	 */
	void exitStructDeclaratorList(CParser.StructDeclaratorListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#structDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterStructDeclarator(CParser.StructDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#structDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitStructDeclarator(CParser.StructDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#enumSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterEnumSpecifier(CParser.EnumSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#enumSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitEnumSpecifier(CParser.EnumSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#enumeratorList}.
	 * @param ctx the parse tree
	 */
	void enterEnumeratorList(CParser.EnumeratorListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#enumeratorList}.
	 * @param ctx the parse tree
	 */
	void exitEnumeratorList(CParser.EnumeratorListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#enumerator}.
	 * @param ctx the parse tree
	 */
	void enterEnumerator(CParser.EnumeratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#enumerator}.
	 * @param ctx the parse tree
	 */
	void exitEnumerator(CParser.EnumeratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#enumerationConstant}.
	 * @param ctx the parse tree
	 */
	void enterEnumerationConstant(CParser.EnumerationConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#enumerationConstant}.
	 * @param ctx the parse tree
	 */
	void exitEnumerationConstant(CParser.EnumerationConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#atomicTypeSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterAtomicTypeSpecifier(CParser.AtomicTypeSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#atomicTypeSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitAtomicTypeSpecifier(CParser.AtomicTypeSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#typeQualifier}.
	 * @param ctx the parse tree
	 */
	void enterTypeQualifier(CParser.TypeQualifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#typeQualifier}.
	 * @param ctx the parse tree
	 */
	void exitTypeQualifier(CParser.TypeQualifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#functionSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterFunctionSpecifier(CParser.FunctionSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#functionSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitFunctionSpecifier(CParser.FunctionSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#alignmentSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterAlignmentSpecifier(CParser.AlignmentSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#alignmentSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitAlignmentSpecifier(CParser.AlignmentSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#declarator}.
	 * @param ctx the parse tree
	 */
	void enterDeclarator(CParser.DeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#declarator}.
	 * @param ctx the parse tree
	 */
	void exitDeclarator(CParser.DeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code directDeclarator_array}
	 * labeled alternative in {@link CParser#directDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterDirectDeclarator_array(CParser.DirectDeclarator_arrayContext ctx);
	/**
	 * Exit a parse tree produced by the {@code directDeclarator_array}
	 * labeled alternative in {@link CParser#directDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitDirectDeclarator_array(CParser.DirectDeclarator_arrayContext ctx);
	/**
	 * Enter a parse tree produced by the {@code directDeclarator_pass}
	 * labeled alternative in {@link CParser#directDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterDirectDeclarator_pass(CParser.DirectDeclarator_passContext ctx);
	/**
	 * Exit a parse tree produced by the {@code directDeclarator_pass}
	 * labeled alternative in {@link CParser#directDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitDirectDeclarator_pass(CParser.DirectDeclarator_passContext ctx);
	/**
	 * Enter a parse tree produced by the {@code directDeclarator_func}
	 * labeled alternative in {@link CParser#directDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterDirectDeclarator_func(CParser.DirectDeclarator_funcContext ctx);
	/**
	 * Exit a parse tree produced by the {@code directDeclarator_func}
	 * labeled alternative in {@link CParser#directDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitDirectDeclarator_func(CParser.DirectDeclarator_funcContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#gccDeclaratorExtension}.
	 * @param ctx the parse tree
	 */
	void enterGccDeclaratorExtension(CParser.GccDeclaratorExtensionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#gccDeclaratorExtension}.
	 * @param ctx the parse tree
	 */
	void exitGccDeclaratorExtension(CParser.GccDeclaratorExtensionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#gccAttributeSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterGccAttributeSpecifier(CParser.GccAttributeSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#gccAttributeSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitGccAttributeSpecifier(CParser.GccAttributeSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#gccAttributeList}.
	 * @param ctx the parse tree
	 */
	void enterGccAttributeList(CParser.GccAttributeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#gccAttributeList}.
	 * @param ctx the parse tree
	 */
	void exitGccAttributeList(CParser.GccAttributeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#gccAttribute}.
	 * @param ctx the parse tree
	 */
	void enterGccAttribute(CParser.GccAttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#gccAttribute}.
	 * @param ctx the parse tree
	 */
	void exitGccAttribute(CParser.GccAttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#nestedParenthesesBlock}.
	 * @param ctx the parse tree
	 */
	void enterNestedParenthesesBlock(CParser.NestedParenthesesBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#nestedParenthesesBlock}.
	 * @param ctx the parse tree
	 */
	void exitNestedParenthesesBlock(CParser.NestedParenthesesBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#pointer}.
	 * @param ctx the parse tree
	 */
	void enterPointer(CParser.PointerContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#pointer}.
	 * @param ctx the parse tree
	 */
	void exitPointer(CParser.PointerContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#typeQualifierList}.
	 * @param ctx the parse tree
	 */
	void enterTypeQualifierList(CParser.TypeQualifierListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#typeQualifierList}.
	 * @param ctx the parse tree
	 */
	void exitTypeQualifierList(CParser.TypeQualifierListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#parameterTypeList}.
	 * @param ctx the parse tree
	 */
	void enterParameterTypeList(CParser.ParameterTypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#parameterTypeList}.
	 * @param ctx the parse tree
	 */
	void exitParameterTypeList(CParser.ParameterTypeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(CParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(CParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#parameterDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterParameterDeclaration(CParser.ParameterDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#parameterDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitParameterDeclaration(CParser.ParameterDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#identifierList}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierList(CParser.IdentifierListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#identifierList}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierList(CParser.IdentifierListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#typeName}.
	 * @param ctx the parse tree
	 */
	void enterTypeName(CParser.TypeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#typeName}.
	 * @param ctx the parse tree
	 */
	void exitTypeName(CParser.TypeNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#abstractDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterAbstractDeclarator(CParser.AbstractDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#abstractDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitAbstractDeclarator(CParser.AbstractDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#directAbstractDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterDirectAbstractDeclarator(CParser.DirectAbstractDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#directAbstractDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitDirectAbstractDeclarator(CParser.DirectAbstractDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#initializer}.
	 * @param ctx the parse tree
	 */
	void enterInitializer(CParser.InitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#initializer}.
	 * @param ctx the parse tree
	 */
	void exitInitializer(CParser.InitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#initializerList}.
	 * @param ctx the parse tree
	 */
	void enterInitializerList(CParser.InitializerListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#initializerList}.
	 * @param ctx the parse tree
	 */
	void exitInitializerList(CParser.InitializerListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#designation}.
	 * @param ctx the parse tree
	 */
	void enterDesignation(CParser.DesignationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#designation}.
	 * @param ctx the parse tree
	 */
	void exitDesignation(CParser.DesignationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#designatorList}.
	 * @param ctx the parse tree
	 */
	void enterDesignatorList(CParser.DesignatorListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#designatorList}.
	 * @param ctx the parse tree
	 */
	void exitDesignatorList(CParser.DesignatorListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#designator}.
	 * @param ctx the parse tree
	 */
	void enterDesignator(CParser.DesignatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#designator}.
	 * @param ctx the parse tree
	 */
	void exitDesignator(CParser.DesignatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#staticAssertDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterStaticAssertDeclaration(CParser.StaticAssertDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#staticAssertDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitStaticAssertDeclaration(CParser.StaticAssertDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(CParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(CParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#labeledStatement}.
	 * @param ctx the parse tree
	 */
	void enterLabeledStatement(CParser.LabeledStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#labeledStatement}.
	 * @param ctx the parse tree
	 */
	void exitLabeledStatement(CParser.LabeledStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#compoundStatement}.
	 * @param ctx the parse tree
	 */
	void enterCompoundStatement(CParser.CompoundStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#compoundStatement}.
	 * @param ctx the parse tree
	 */
	void exitCompoundStatement(CParser.CompoundStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#blockItemList}.
	 * @param ctx the parse tree
	 */
	void enterBlockItemList(CParser.BlockItemListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#blockItemList}.
	 * @param ctx the parse tree
	 */
	void exitBlockItemList(CParser.BlockItemListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#blockItem}.
	 * @param ctx the parse tree
	 */
	void enterBlockItem(CParser.BlockItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#blockItem}.
	 * @param ctx the parse tree
	 */
	void exitBlockItem(CParser.BlockItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStatement(CParser.ExpressionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStatement(CParser.ExpressionStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code selectionStatement_if}
	 * labeled alternative in {@link CParser#selectionStatement}.
	 * @param ctx the parse tree
	 */
	void enterSelectionStatement_if(CParser.SelectionStatement_ifContext ctx);
	/**
	 * Exit a parse tree produced by the {@code selectionStatement_if}
	 * labeled alternative in {@link CParser#selectionStatement}.
	 * @param ctx the parse tree
	 */
	void exitSelectionStatement_if(CParser.SelectionStatement_ifContext ctx);
	/**
	 * Enter a parse tree produced by the {@code selectionStatement_switch}
	 * labeled alternative in {@link CParser#selectionStatement}.
	 * @param ctx the parse tree
	 */
	void enterSelectionStatement_switch(CParser.SelectionStatement_switchContext ctx);
	/**
	 * Exit a parse tree produced by the {@code selectionStatement_switch}
	 * labeled alternative in {@link CParser#selectionStatement}.
	 * @param ctx the parse tree
	 */
	void exitSelectionStatement_switch(CParser.SelectionStatement_switchContext ctx);
	/**
	 * Enter a parse tree produced by the {@code iterationStatement_while}
	 * labeled alternative in {@link CParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void enterIterationStatement_while(CParser.IterationStatement_whileContext ctx);
	/**
	 * Exit a parse tree produced by the {@code iterationStatement_while}
	 * labeled alternative in {@link CParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void exitIterationStatement_while(CParser.IterationStatement_whileContext ctx);
	/**
	 * Enter a parse tree produced by the {@code iterationStatement_dowhile}
	 * labeled alternative in {@link CParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void enterIterationStatement_dowhile(CParser.IterationStatement_dowhileContext ctx);
	/**
	 * Exit a parse tree produced by the {@code iterationStatement_dowhile}
	 * labeled alternative in {@link CParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void exitIterationStatement_dowhile(CParser.IterationStatement_dowhileContext ctx);
	/**
	 * Enter a parse tree produced by the {@code iterationStatement_for}
	 * labeled alternative in {@link CParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void enterIterationStatement_for(CParser.IterationStatement_forContext ctx);
	/**
	 * Exit a parse tree produced by the {@code iterationStatement_for}
	 * labeled alternative in {@link CParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void exitIterationStatement_for(CParser.IterationStatement_forContext ctx);
	/**
	 * Enter a parse tree produced by the {@code iterationStatement_forDeclared}
	 * labeled alternative in {@link CParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void enterIterationStatement_forDeclared(CParser.IterationStatement_forDeclaredContext ctx);
	/**
	 * Exit a parse tree produced by the {@code iterationStatement_forDeclared}
	 * labeled alternative in {@link CParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void exitIterationStatement_forDeclared(CParser.IterationStatement_forDeclaredContext ctx);
	/**
	 * Enter a parse tree produced by the {@code jumpStatement_goto}
	 * labeled alternative in {@link CParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterJumpStatement_goto(CParser.JumpStatement_gotoContext ctx);
	/**
	 * Exit a parse tree produced by the {@code jumpStatement_goto}
	 * labeled alternative in {@link CParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitJumpStatement_goto(CParser.JumpStatement_gotoContext ctx);
	/**
	 * Enter a parse tree produced by the {@code jumpStatement_continue}
	 * labeled alternative in {@link CParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterJumpStatement_continue(CParser.JumpStatement_continueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code jumpStatement_continue}
	 * labeled alternative in {@link CParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitJumpStatement_continue(CParser.JumpStatement_continueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code jumpStatement_break}
	 * labeled alternative in {@link CParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterJumpStatement_break(CParser.JumpStatement_breakContext ctx);
	/**
	 * Exit a parse tree produced by the {@code jumpStatement_break}
	 * labeled alternative in {@link CParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitJumpStatement_break(CParser.JumpStatement_breakContext ctx);
	/**
	 * Enter a parse tree produced by the {@code jumpStatement_return}
	 * labeled alternative in {@link CParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterJumpStatement_return(CParser.JumpStatement_returnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code jumpStatement_return}
	 * labeled alternative in {@link CParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitJumpStatement_return(CParser.JumpStatement_returnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code jumpStatement_}
	 * labeled alternative in {@link CParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterJumpStatement_(CParser.JumpStatement_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code jumpStatement_}
	 * labeled alternative in {@link CParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitJumpStatement_(CParser.JumpStatement_Context ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(CParser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(CParser.CompilationUnitContext ctx) throws IOException;
	/**
	 * Enter a parse tree produced by {@link CParser#translationUnit}.
	 * @param ctx the parse tree
	 */
	void enterTranslationUnit(CParser.TranslationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#translationUnit}.
	 * @param ctx the parse tree
	 */
	void exitTranslationUnit(CParser.TranslationUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#externalDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterExternalDeclaration(CParser.ExternalDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#externalDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitExternalDeclaration(CParser.ExternalDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#functionDefinition}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDefinition(CParser.FunctionDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#functionDefinition}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDefinition(CParser.FunctionDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CParser#declarationList}.
	 * @param ctx the parse tree
	 */
	void enterDeclarationList(CParser.DeclarationListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CParser#declarationList}.
	 * @param ctx the parse tree
	 */
	void exitDeclarationList(CParser.DeclarationListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code functionCall_}
	 * labeled alternative in {@link CParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall_(CParser.FunctionCall_Context ctx);
	/**
	 * Exit a parse tree produced by the {@code functionCall_}
	 * labeled alternative in {@link CParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall_(CParser.FunctionCall_Context ctx);
}