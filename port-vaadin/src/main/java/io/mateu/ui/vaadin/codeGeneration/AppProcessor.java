package io.mateu.ui.vaadin.codeGeneration;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Set;

@SupportedAnnotationTypes({ "io.mateu.ui.core.app.App" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class AppProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementsUtils;
    private Types typeUtils;
    private Filer filer;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // reference often used tools from the processingEnv
        messager = processingEnv.getMessager();
        elementsUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();

        messager.printMessage(Kind.NOTE, "filer=" + filer.getClass().getCanonicalName());

        // generate code for annotated elements
        Set<? extends Element> annotatedElements;
        try {
            annotatedElements = roundEnv.getElementsAnnotatedWith((Class<? extends Annotation>) Class.forName("io.mateu.ui.core.app.App"));
            for (TypeElement element : ElementFilter.typesIn(annotatedElements)) {
                generateMainClass(element);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // claim the annotation
        return true;
    }

    private void generateMainClass(TypeElement clase) {
        messager.printMessage(Kind.NOTE, "generando implementación lado cliente...");
        try {
            JavaFileObject javaFile = filer.createSourceFile("io.mateu.ui.javafx.runner.Main", clase);
            messager.printMessage(Kind.NOTE, "generando " + javaFile.toUri() + "...");
            Writer writer = javaFile.openWriter();
            PrintWriter pw = new PrintWriter(writer);

            pw.println("package io.mateu.ui.javafx.runner;\n" +
                    "\n" +
                    "import io.mateu.ui.javafx.JavafxPort;\n" +
                    "import javafx.application.Application;\n" +
                    "import javafx.stage.Stage;\n" +
                    "import io.mateu.ui.sample.client.SampleApp;\n" +
                    "\n" +
                    "/**\n" +
                    " * Created by miguel on 9/8/16.\n" +
                    " */\n" +
                    "public class Main extends Application {\n" +
                    "\n" +
                    "    public void start(Stage primaryStage) throws Exception {\n" +
                    "\n" +
                    "        new JavafxPort(new " + clase.getQualifiedName() + "()).start(primaryStage);\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "    public static void main(String... args) {\n" +
                    "        launch(args);\n" +
                    "    }\n" +
                    "}\n");

            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
