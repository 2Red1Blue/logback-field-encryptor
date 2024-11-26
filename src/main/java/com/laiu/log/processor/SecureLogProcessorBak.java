package com.laiu.log.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


//@AutoService(Processor.class)
@SupportedAnnotationTypes("com.laiu.log.annotation.SecureLog")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SecureLogProcessorBak extends AbstractProcessor {
    //private Trees trees; //存在bug:processingEnv 没有正确设置，或者 Trees 实例无法在当前环境中创建.这是一个不推荐使用的内部 API，不建议在注解处理器中直接使用
    private Messager messager;
    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;
    // 添加 Logger 对象
    private Logger logger = Logger.getLogger(SecureLogProcessorBak.class.getName());


    // 添加调试日志


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        try {
            super.init(processingEnv);
            this.messager = processingEnv.getMessager();
            this.elementUtils = processingEnv.getElementUtils();
            this.typeUtils = processingEnv.getTypeUtils();
            this.filer = processingEnv.getFiler();

            logger.log(Level.INFO, "SecureLogProcessor initialized"); // 使用logger
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error initializing processor", e);
        }
    }

    private void debug(String message) {
        if (messager != null) {
            messager.printMessage(Diagnostic.Kind.NOTE, "DEBUG: " + message);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            for (TypeElement annotation : annotations) {
                Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
                debug("Found " + elements.size() + " elements with annotation");

                for (Element element : elements) {
                    if (element.getKind() == ElementKind.CLASS) {
                        TypeElement typeElement = (TypeElement) element;
                        try {
                            generateLogger(typeElement);
                        } catch (Exception e) {
                            error("Error processing element: " + typeElement, e);
                        }
                    }
                }
            }
        }
        return true;
    }


    private void generateLogger(TypeElement typeElement) {
        // 使用 JavaPoet 库生成代码
        String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
        String className = typeElement.getSimpleName().toString();

        logger.log(Level.INFO, "Generating logger for class: " + className + " in package: " + packageName); // 使用logger

        // 这里需要使用 JavaPoet 来生成代码
        // 建议使用 JavaPoet 替代直接操作 JCTree
        try {
            generateLoggerUsingJavaPoet(packageName, className);
            logger.log(Level.INFO, "Successfully generated logger for class: " + className); // 使用logger
        } catch (Exception e) {
            error("Failed to generate logger", e);
            logger.log(Level.SEVERE, "Failed to generate logger for class: " + className, e); // 使用logger记录异常
        }
    }

    private void error(String message, Exception e) {
        String fullMessage = message + ": " + e.getMessage() + "\n" +
                "Stack trace: " + Arrays.toString(e.getStackTrace());
        messager.printMessage(Diagnostic.Kind.ERROR, fullMessage);
        logger.log(Level.SEVERE, message, e); // 使用logger记录错误信息和堆栈跟踪
    }

    private void generateLoggerUsingJavaPoet(String packageName, String className) throws IOException {
        ClassName loggerClass = ClassName.get("org.slf4j", "Logger");
        ClassName loggerFactoryClass = ClassName.get("org.slf4j", "LoggerFactory");
        ClassName secureLoggerClass = ClassName.get("com.laiu.log.logger", "SecureLogger");

        // 创建 logger 字段
        FieldSpec loggerField = FieldSpec.builder(secureLoggerClass, "logger")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $T($T.getLogger($L.class))",
                        secureLoggerClass, loggerFactoryClass, className)
                .build();

        // 创建新的类文件
        TypeSpec loggerType = TypeSpec.classBuilder(className + "Logger")
                .addModifiers(Modifier.PUBLIC)
                .addField(loggerField)
                .build();

        // 写入文件
        JavaFile javaFile = JavaFile.builder(packageName, loggerType)
                .build();

        javaFile.writeTo(filer);
    }
}