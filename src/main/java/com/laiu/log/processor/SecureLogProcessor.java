package com.laiu.log.processor;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

//@AutoService(Processor.class)
@SupportedAnnotationTypes("com.laiu.log.annotation.SecureLog")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SecureLogProcessor extends AbstractProcessor {

    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Names names;
    private Context context;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
        this.context = context;
        this.messager = processingEnv.getMessager();
    }

    private void debug(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, "SecureLogProcessor: " + msg);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        debug("Starting annotation processing");
        if (!roundEnv.processingOver()) {
            for (TypeElement annotation : annotations) {
                debug("Processing annotation: " + annotation);
                Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
                debug("Found " + elements.size() + " annotated elements");
                elements.stream()
                        .filter(element -> element.getKind() == ElementKind.CLASS)
                        .forEach(element -> {
                            JCTree tree = trees.getTree(element);
                            tree.accept(new TreeTranslator() {
                                @Override
                                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                                    super.visitClassDef(jcClassDecl);
                                    // 只有在字段不存在时才添加
                                    if (!hasLoggerField(jcClassDecl)) {
                                        jcClassDecl.defs = jcClassDecl.defs.prepend(createLoggerField(element.getSimpleName().toString()));
                                    }
                                }
                            });
                        });
            }
        }
        return true;
    }

    private boolean hasLoggerField(JCTree.JCClassDecl classDecl) {
        for (JCTree def : classDecl.defs) {
            if (def instanceof JCTree.JCVariableDecl) {
                JCTree.JCVariableDecl var = (JCTree.JCVariableDecl) def;
                if (var.name.toString().equals("logger")) {
                    return true;
                }
            }
        }
        return false;
    }

    private JCTree.JCVariableDecl createLoggerField(String className) {
        // 创建 LoggerFactory.getLogger(XXX.class) 表达式
        JCTree.JCFieldAccess loggerFactorySelect = treeMaker.Select(
                treeMaker.Ident(names.fromString("LoggerFactory")),
                names.fromString("getLogger"));

        JCTree.JCFieldAccess classSelect = treeMaker.Select(
                treeMaker.Ident(names.fromString(className)),
                names.fromString("class"));

        JCTree.JCMethodInvocation getLoggerCall = treeMaker.Apply(
                List.nil(),
                loggerFactorySelect,
                List.of(classSelect));

        // 创建 new SecureLogger(...) 表达式
        JCTree.JCNewClass newSecureLogger = treeMaker.NewClass(
                null,
                List.nil(),
                treeMaker.Ident(names.fromString("SecureLogger")),
                List.of(getLoggerCall),
                null);

        // 创建字段声明
        return treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PRIVATE | Flags.STATIC | Flags.FINAL),
                names.fromString("logger"),
                treeMaker.Ident(names.fromString("SecureLogger")),
                newSecureLogger);
    }
}