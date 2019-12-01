package com.craftinginterpreters.lox;

import com.craftinginterpreters.lox.Expr.Binary;
import com.craftinginterpreters.lox.Expr.Literal;

class ReturnType implements Expr.Visitor<TokenType> {
  private TokenType computeReturnType(Expr expression) {
    return expression.accept(this);
  }

  @Override
  public TokenType visitBinaryExpr(Binary expr) {
    TokenType leftType = computeReturnType(expr.left);
    TokenType rightType = computeReturnType(expr.right);
    if (leftType.equals(TokenType.STRING) || rightType.equals(TokenType.STRING)) {
      return TokenType.STRING;
    } else {
      return TokenType.NUMBER;
    }
  }

  @Override
  public TokenType visitLiteralExpr(Literal expr) {
    // TODO: implement other literals (nil, true)
    if (expr.value instanceof String) {
      return TokenType.STRING;
    } else if (expr.value instanceof Double) {
      return TokenType.NUMBER;
    } else if (expr.value.equals(false)) {
      return TokenType.FALSE;
    }
    return null;
  }
}
