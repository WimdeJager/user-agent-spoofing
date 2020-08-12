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
import uaspoofing.output.OutputHandler;

/**
 * Tries to find a User Agent in the .java files that are present in the
 * given (underlying) directory(ies).
 */
public class UAFinder {

  /**
   * The main directory. Contains a decompiled APK
   */
  private File dir;

  /**
   * List of UAs, empty initially
   */
  private UAList uas;

  /**
   * Current file that is processed
   */
  private File currentFile;

  /**
   * Names of variables that possibly hold UAs. Is reset for every
   * ```currentFile```
   */
  private ArrayList<String> varNames;

  /**
   * Constructor
   * @param dir
   */
  public UAFinder(File dir) {
    this.dir      = dir;
    this.uas      = new UAList();
    this.varNames = new ArrayList<String>();
  }

  /**
   * Process all files in directory by recursion
   * @return uas: list of all found UAs in directory
   */
  public UAList find() {
    _find(dir);
    return uas;
  }

  /**
   * Helper function for ```find()```
   * @param d current directory
   */
  private void _find(File d) {
    // skip directories from Android, Google and Apache
    if (d.getName().contains("android")
        || d.getName().contains("google")
        || d.getName().contains("apache")) {
      return;
    }

    // loop through all files in directory
    for (final File f : d.listFiles()) {
      if (f.isDirectory()) {
        // if subdirectory, recurse
        _find(f);
      } else {
        // else, process file
        currentFile = f;

        try {
          // visit file
          CompilationUnit cu = StaticJavaParser.parse(f);
          MethodCallVisitor mv = new MethodCallVisitor();
          mv.visit(cu, null);

          // if any variables possibly containing a UA found, visit these
          // variables
          if (!varNames.isEmpty()) {
            AssignVisitor nv = new AssignVisitor();
            nv.visit(cu, null);
          }
        }

        catch (ParseProblemException e) {
          // File could not be parsed, probably due to an error during
          // decompilation. This is not a problem, and the user does not need
          // to know about this.
        } catch (FileNotFoundException e) {
          OutputHandler.print(OutputHandler.Type.WRN,
              "File " + f.getPath() + " could not be found!");
        }
      }
    }
  }

  /**
   * ```e``` is suspected to hold a UA, try to extract the UA from it
   * @param e Expression suspected to hold UA
   */
  private void extractUA(Expression e) {
    if (e.isStringLiteralExpr()) {
      // if string, just add UA to list
      String str = e.toString().replaceAll("^(['\"])(.*)\\1$", "$2");

      uas.add(new UserAgent(str, currentFile));
    }

    else if (e.isNameExpr()) {
      // if variable, add to list of variables possibly containing UA
      varNames.add(e.toString());
    }

    else if (e.isFieldAccessExpr()) {
      // if field access, remove 'this.' (if present), and add to list
      FieldAccessExpr fae = e.asFieldAccessExpr();
      if (fae.getScope().toString().equals("this")) {
        varNames.add(fae.getName().toString());
      }
    }
  }

  /**
   * Visitor looking for ```MethodCallExpr```s
   */
  private class MethodCallVisitor extends VoidVisitorAdapter<Object> {
    @Override
    public void visit(MethodCallExpr e, Object arg) {
      super.visit(e, arg);

      // when MethodCallExpr found, check if method can be used to set or
      // change UA
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
        extractUA(e.getArgument(1));
      }

      else if (call.equals("setUserAgentString")) {
        extractUA(e.getArgument(0));
      }

    }
  }

  /**
   * Visitor looking for variables with a name from ```varNames```
   */
  private class AssignVisitor extends VoidVisitorAdapter<Object> {
    @Override
    public void visit(AssignExpr e, Object arg) {
      super.visit(e, arg);
      int i = varNames.indexOf(e.getTarget().toString());
      if (i >= 0) {
        extractUA(e.getValue());
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
            extractUA(v.getInitializer().get());
          }
        }
      }
    }
  }
}
