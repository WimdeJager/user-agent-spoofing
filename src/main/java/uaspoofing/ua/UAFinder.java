package uaspoofing.ua;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import uaspoofing.apk.UAList;
import uaspoofing.output.OutputHandler;

/**
 * Tries to find a User Agent in the .java files that are present in the given (underlying)
 * directory(ies).
 */
public class UAFinder {

  private File dir;

  private UAList uas;

  private File currentFile;

  private ArrayList<String> varNames;

  public UAFinder(File dir) {
    this.dir      = dir;
    this.uas      = new UAList();
    this.varNames = new ArrayList<String>();
  }

  public UAList find() {
    _find(dir);
    return uas;
  }

  private void _find(File d) {
    if (d.getName().contains("android")
        || d.getName().contains("google")
        || d.getName().contains("apache")) {
      return;
    }

    for (final File f : d.listFiles()) {
      if (f.isDirectory()) {
        _find(f);
      } else {
        currentFile = f;

        try {
          CompilationUnit cu = StaticJavaParser.parse(f);
          MethodCallVisitor mv = new MethodCallVisitor();
          mv.visit(cu, null);

          if (!varNames.isEmpty()) {
            AssignVisitor nv = new AssignVisitor();
            nv.visit(cu, null);
          }
        }

        catch (ParseProblemException e) {
          OutputHandler.print(OutputHandler.Type.WRN,
              "File " + f.getPath() + " could not be parsed!");
        } catch (FileNotFoundException e) {
          OutputHandler.print(OutputHandler.Type.WRN,
              "File " + f.getPath() + " could not be found!");
        }
      }
    }
  }

  private void getUAFromExpression(Expression e) {
    if (e.isStringLiteralExpr()) {
      String str = e.toString().replaceAll("^(['\"])(.*)\\1$", "$2");

      uas.add(new UserAgent(str, currentFile));
    }

    else if (e.isNameExpr()) {
      varNames.add(e.toString());
    }

    else if (e.isFieldAccessExpr()) {
      FieldAccessExpr fae = e.asFieldAccessExpr();
      if (fae.getScope().toString().equals("this")) {
        varNames.add(fae.getName().toString());
      }
    }
  }

  private class MethodCallVisitor extends VoidVisitorAdapter<Object> {
    @Override
    public void visit(MethodCallExpr e, Object arg) {
      super.visit(e, arg);

      String call = e.getNameAsString();
      if (
          (call.equals("setHeader")
              || call.equals("addHeader")
              || call.equals("setParameter")
              || call.equals("setRequestProperty")
          )
              && e.getArguments().size() > 1
              && e.getArgument(0).toString().equals("\"User-Agent\"")
          ) {
        getUAFromExpression(e.getArgument(1));
      }

      else if (call.equals("setUserAgentString")) {
        getUAFromExpression(e.getArgument(0));
      }

    }
  }

  private class AssignVisitor extends VoidVisitorAdapter<Object> {
    @Override
    public void visit(AssignExpr e, Object arg) {
      super.visit(e, arg);
      int i = varNames.indexOf(e.getTarget().toString());
      if (i >= 0) {
        getUAFromExpression(e.getValue());
      }
    }

    @Override
    public void visit(VariableDeclarationExpr e, Object arg) {
      super.visit(e, arg);

      analyzeDeclarators(e.getVariables());
    }

    @Override
    public void visit(FieldDeclaration e, Object arg) {
      super.visit(e, arg);

      analyzeDeclarators(e.getVariables());
    }

    private void analyzeDeclarators(NodeList<VariableDeclarator> vars) {
      for (VariableDeclarator v : vars) {
        if (v.getInitializer().isPresent()) {
          int i = varNames.indexOf(v.getName().toString());
          if (i >= 0) {
            getUAFromExpression(v.getInitializer().get());
          }
        }
      }
    }
  }
}
