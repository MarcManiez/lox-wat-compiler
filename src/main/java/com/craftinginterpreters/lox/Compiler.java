package com.craftinginterpreters.lox;

import java.util.List;

import com.craftinginterpreters.lox.Expr.Binary;
import com.craftinginterpreters.lox.Expr.Literal;
import com.craftinginterpreters.lox.Stmt.Expression;

class Compiler implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
  private final List<Stmt> statements;
  // Declare some kind of receptacle for instructions

  Compiler(List<Stmt> statements) {
    this.statements = statements;
  }

  void compile() {
    for (Stmt statement : this.statements) {
      compile(statement);
    }
  }

  private Object compile(Stmt statement) {
    return statement.accept(this);
  }

  private Object compile(Expr expression) {
    return expression.accept(this);
  }

  @Override
  public Void visitExpressionStmt(Expression stmt) {
    compile(stmt.expression);
    return null;
  }

  @Override
  public Object visitBinaryExpr(Binary expr) {
    compile(expr.left);
    compile(expr.right);

    switch (expr.operator.type) {
      case PLUS:
      System.out.println("Add two things on the stck pls :)"); // instead of producing an instruction
      break;
    }
    return null;
  }

  @Override
  public Object visitLiteralExpr(Literal expr) {
    System.out.println("Put this on the stack " + expr.value.toString()); // instead of producing an instruction
    return null;
  }
}
