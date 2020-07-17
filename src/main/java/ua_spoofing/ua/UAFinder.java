package ua_spoofing.ua;

import java.io.File;
import java.io.IOException;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import ua_spoofing.OutputHandler;

/**
 * Tries to find a User Agent in the .java files that are present in the given (underlying)
 * directory(ies).
 */
public class UAFinder {

  private File dir;
  private String apkName;

  public UAFinder(File dir, String apkName) {
    this.dir     = dir;
    this.apkName = apkName;
  }

  public void find() throws IOException {
    _find(dir);
  }

  private void _find(File d) throws IOException {
//    OutputHandler.print(OutputHandler.Type.INF,
//        "Looking in dir " + d.getPath());

    if (d.getName().contains("android")
        || d.getName().contains("google")
        || d.getName().contains("apache")) {
      return;
    }

    for (final File f : d.listFiles()) {
      if (f.isDirectory()) {
        _find(f);
      } else {
//        OutputHandler.print(OutputHandler.Type.INF,
//            "Processing file " + f.getName() + " ...");

        try {
          CompilationUnit cu = StaticJavaParser.parse(f);

          new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(MethodCallExpr n, Object arg) {
              super.visit(n, arg);
              String call = n.getNameAsString();

              if (call.equals("setHeader")) {
                if (n.getArgument(0).toString().equals("\"User-Agent\"")) {
                  OutputHandler.newline();
                  OutputHandler.print(OutputHandler.Type.INF,
                      "UA change using setHeader detected in file: " +
                          f.getPath());

                  checkUA(n.getArgument(1));
                }
              }

              if (call.equals("addHeader")) {
                if (n.getArgument(0).toString().equals("\"User-Agent\"")) {
                  OutputHandler.newline();
                  OutputHandler.print(OutputHandler.Type.INF,
                      "UA change using addHeader detected in file: " +
                          f.getPath());

                  checkUA(n.getArgument(1));
                }
              }

              if (call.equals("setParameter")) {
                if (n.getArgument(0).toString().equals("\"User-Agent\"")) {
                  OutputHandler.newline();
                  OutputHandler.print(OutputHandler.Type.INF,
                      "UA change using setParameter detected in file: " +
                          f.getPath());

                  checkUA(n.getArgument(1));
                }
              }

              if (call.equals("setUserAgentString")) {
                if (n.getArgument(0).toString().equals("\"User-Agent\"")) {
                  OutputHandler.newline();
                  OutputHandler.print(OutputHandler.Type.INF,
                      "UA change using setHeader detected in file " +
                          f.getPath());

                  checkUA(n.getArgument(1));
                }
              }

              if (call.equals("setRequestProperty")) {
                if (n.getArgument(0).toString().equals("\"User-Agent\"")) {
                  OutputHandler.newline();
                  OutputHandler.print(OutputHandler.Type.INF,
                      "UA change using setRequestProperty detected in file " +
                          f.getPath());

                  checkUA(n.getArgument(1));
                }
              }

            }
          }.visit(cu, null);
        }

        catch (ParseProblemException e) {
          OutputHandler.print(OutputHandler.Type.WRN,
              "File " + f.getPath() + " could not be parsed!");
        }
      }
    }
  }

  private void checkUA(Expression e) {
    if (e.isStringLiteralExpr()) {
      OutputHandler.print(OutputHandler.Type.INF,
          "UA is " + e.toString());
    } else if (e.isNameExpr()) {
      OutputHandler.print(OutputHandler.Type.INF,
          "UA is contained in variable: " + e);
    } else if (e.isMethodCallExpr()) {
      OutputHandler.print(OutputHandler.Type.INF,
          "UA is generated by method: " + e);
    } else if (e.isFieldAccessExpr()) {
      OutputHandler.print(OutputHandler.Type.INF,
          "UA is contained in field: " + e);
    } else {
      OutputHandler.print(OutputHandler.Type.INF,
          "UA is contained in some other container: " + e);
    }
  }

}
