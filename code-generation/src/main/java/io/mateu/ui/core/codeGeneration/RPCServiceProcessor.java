package io.mateu.ui.core.codeGeneration;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes({ "io.mateu.ui.core.communication.Service" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class RPCServiceProcessor extends AbstractProcessor {

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

        messager.printMessage(Kind.WARNING, "Generando fuentes: Inicio");

        messager.printMessage(Kind.NOTE, "filer=" + filer.getClass().getCanonicalName());


        List<TypeElement> services = new ArrayList<>();

        // generate code for annotated elements
        Set<? extends Element> annotatedElements;
        try {
            annotatedElements = roundEnv.getElementsAnnotatedWith((Class<? extends Annotation>) Class.forName("io.mateu.ui.core.communication.Service"));
            for (TypeElement element : ElementFilter.typesIn(annotatedElements)) {
                messager.printMessage(Kind.WARNING, "Generando fuentes para " + element.getQualifiedName());
                generateInterfazAsincrona(messager, elementsUtils, typeUtils, filer, element);
                generateClientSideImpl(element);
                generateTeavmClientSideImpl(element);
                generateRESTResource(element);
                services.add(element);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //generateRestServlet(services);
        generateTeavmClientFactory(services);

        // claim the annotation
        return false;
    }

    private void generateRESTResource(TypeElement clase) {
        messager.printMessage(Kind.NOTE, "generando implementación lado cliente...");
        try {
            String simpleName = clase.getSimpleName().toString();
            String packageName = elementsUtils.getPackageOf(clase).getQualifiedName().toString();
            String typeName = clase.getSimpleName().toString() + "Resource";

            String pn = packageName;


            JavaFileObject javaFile = filer.createSourceFile("io.mateu.ui.rest." + pn + "." + typeName, clase);
            messager.printMessage(Kind.NOTE, "generando " + javaFile.toUri() + "...");
            Writer writer = javaFile.openWriter();
            PrintWriter pw = new PrintWriter(writer);

            pw.println("package io.mateu.ui.rest." + pn + ";");
            pw.println("");
            pw.println("import io.mateu.ui.core.server.ServerSideHelper;");
            pw.println("import javax.ws.rs.GET;\n" +
                    "import javax.ws.rs.POST;\n" +
                    "import javax.ws.rs.Path;\n" +
                    "import javax.ws.rs.FormParam;" +
                    "import javax.ws.rs.Produces;" +
                    "import javax.ws.rs.core.MediaType;");
            pw.println("");
            pw.println("");
            pw.println("");
            pw.println("/**");
            pw.println(" * Generated class creating a default implementation the");
            pw.println(" * for the class {@link " + clase.getQualifiedName().toString() + "}");
            pw.println(" * ");
            pw.println(" * @author Miguel");
            pw.println(" */");
            pw.println("@Path(\"" + pn.replaceAll("\\.", "_") + "_" + clase.getSimpleName().toString() + "\")");
            pw.println("public class " + typeName + " {");
            pw.println();
            pw.println();

            pw.println("public static " + packageName + "." + simpleName + " s = ServerSideHelper.findImplementation(" + packageName + "." + simpleName + ".class);");
            pw.println();
            pw.println();


            List<String> usedPaths = new ArrayList<>();

            for (ExecutableElement m : ElementFilter.methodsIn(clase.getEnclosedElements())) {

                String path = m.getSimpleName().toString();

                int aux = 1;
                String originalPath = path;
                while (usedPaths.contains(path)) {
                    path = originalPath + aux++;
                }
                usedPaths.add(path);

                pw.println("\t@POST @Path(\"/" + path + "\") @Produces(MediaType.APPLICATION_JSON)");

                String rt = m.getReturnType().toString();

                if ("io.mateu.ui.core.shared.Data".equals(m.getReturnType().toString())) rt = "java.util.Map";


                String s = "\tpublic " + rt + " ";

                s += m.getSimpleName();
                s += "(";
                int pos = 0;
                for (VariableElement p : m.getParameters()) {
                    if (pos++ > 0) s += ", ";
                    s += " @FormParam(\"" + p.getSimpleName() + "\") ";
                    s += p.asType().toString();
                    s += " ";
                    s += p.getSimpleName();
                }
                s += ") throws Throwable";

                s += " {\n\n";

                s += "";


                if ("io.mateu.ui.core.shared.Data".equals(m.getReturnType().toString())) {

                    s += " io.mateu.ui.core.shared.Data dataqhdqwhdoqwhdqwohd = ";
                    s += "s.";

                    s += m.getSimpleName();
                    s += "(";
                    pos = 0;
                    for (VariableElement p : m.getParameters()) {
                        if (pos++ > 0) s += ", ";
                        s += p.getSimpleName();
                    }
                    s += ")";

                    s += ";";

                    s += " return (dataqhdqwhdoqwhdqwohd != null)?dataqhdqwhdoqwhdqwohd.getProperties():null; ";

                } else {

                    if (!TypeKind.VOID.equals(m.getReturnType().getKind())) s += " return ";
                    s += "s.";

                    s += m.getSimpleName();
                    s += "(";
                    pos = 0;
                    for (VariableElement p : m.getParameters()) {
                        if (pos++ > 0) s += ", ";
                        s += p.getSimpleName();
                    }
                    s += ")";

                    s += ";";

                }


                s += "\n\n";

                s += "\n\n\t\t}";

                s += "            ";

                pw.println(s);
                pw.println();


            }

            pw.println("}");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateRestServlet(List<TypeElement> services) {

        messager.printMessage(Kind.NOTE, "generando implementación lado cliente...");
        try {

            JavaFileObject javaFile = filer.createSourceFile("io.mateu.ui.teavm.AsyncFactory", null);
            messager.printMessage(Kind.NOTE, "generando " + javaFile.toUri() + "...");
            Writer writer = javaFile.openWriter();
            PrintWriter pw = new PrintWriter(writer);

            pw.println("package io.mateu.ui.teavm;");
            pw.println("");
            pw.println("import io.mateu.ui.core.shared.AsyncCallback;");
            pw.println("");

            for (TypeElement clase : services) {

                String simpleName = clase.getSimpleName().toString();
                String packageName = elementsUtils.getPackageOf(clase).getQualifiedName().toString();
                String typeName = clase.getSimpleName().toString() + "ClientSideImpl";

                String pn = packageName;


                String pnc = packageName.replaceAll("\\.shared\\.", ".client.");
                if (pnc.endsWith(".shared")) pnc = pnc.substring(0, pnc.lastIndexOf(".") + 1) + "client";

                String pns = packageName.replaceAll("\\.shared\\.", ".server.");
                if (pns.endsWith(".shared")) pns = pns.substring(0, pns.lastIndexOf(".") + 1) + "server";

                pw.println("import " + pnc + "." + typeName + ";");

                messager.printMessage(Kind.NOTE, "" + packageName + "->" + pn);

            }



            pw.println("");
            pw.println("/**");
            pw.println(" * Generated class creating a default implementation the");
            pw.println(" * ");
            pw.println(" * @author Miguel");
            pw.println(" */");
            pw.println("public class AsyncFactory implements io.mateu.ui.core.shared.AsyncFactory {");
            pw.println();



            pw.println("\t\tpublic Object create(Class<?> serviceInterface) {");



            int pos = 0;
            for (TypeElement clase : services) {

                String simpleName = clase.getSimpleName().toString();
                String packageName = elementsUtils.getPackageOf(clase).getQualifiedName().toString();
                String typeName = clase.getSimpleName().toString() + "TeavmClientSideImpl";
                String typeNameForAsync = clase.getSimpleName().toString() + "Async";

                String pn = packageName;


                String pnc = packageName.replaceAll("\\.shared\\.", ".client.");
                if (pnc.endsWith(".shared")) pnc = pnc.substring(0, pnc.lastIndexOf(".") + 1) + "client";

                String pns = packageName.replaceAll("\\.shared\\.", ".server.");
                if (pns.endsWith(".shared")) pns = pns.substring(0, pns.lastIndexOf(".") + 1) + "server";

                pw.println("\t\t\t" + ((pos > 0)?"else ":"") + "if (\"" + packageName + "." + simpleName + "\".equals(serviceInterface.getName())) return new " + pnc + "." + typeName + "();");

                messager.printMessage(Kind.NOTE, "" + packageName + "->" + pn);

                pos++;
            }
            pw.println("\t\t\telse return null;");
            pw.println("\t\t}");

            pw.println("}");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void generateTeavmClientFactory(List<TypeElement> services) {

        messager.printMessage(Kind.NOTE, "generando implementación lado cliente...");
        try {

            if (services.size() > 0 && !(services.size() == 1 && "io.mateu.ui.core.shared.BaseService".equals(elementsUtils.getPackageOf(services.get(0)).getQualifiedName().toString() + "." + services.get(0).getSimpleName().toString()))) {

                JavaFileObject javaFile = filer.createSourceFile("io.mateu.ui.teavm.AsyncFactory", services.toArray(new TypeElement[0]));
                messager.printMessage(Kind.NOTE, "generando " + javaFile.toUri() + "...");
                Writer writer = javaFile.openWriter();
                PrintWriter pw = new PrintWriter(writer);

                pw.println("package io.mateu.ui.teavm;");
                pw.println("");
                pw.println("import io.mateu.ui.core.shared.AsyncCallback;");
                pw.println("");

                pw.println("//" + elementsUtils.getPackageOf(services.get(0)).getQualifiedName().toString());
                pw.println("//" + services.get(0).getSimpleName().toString());

                for (TypeElement clase : services) {

                    String simpleName = clase.getSimpleName().toString();
                    String packageName = elementsUtils.getPackageOf(clase).getQualifiedName().toString();
                    String typeName = clase.getSimpleName().toString() + "ClientSideImpl";

                    String pn = packageName;


                    String pnc = packageName.replaceAll("\\.shared\\.", ".client.");
                    if (pnc.endsWith(".shared")) pnc = pnc.substring(0, pnc.lastIndexOf(".") + 1) + "client";

                    String pns = packageName.replaceAll("\\.shared\\.", ".server.");
                    if (pns.endsWith(".shared")) pns = pns.substring(0, pns.lastIndexOf(".") + 1) + "server";

                    pw.println("import " + pnc + "." + typeName + ";");

                    messager.printMessage(Kind.NOTE, "" + packageName + "->" + pn);

                }



                pw.println("");
                pw.println("/**");
                pw.println(" * Generated class creating a default implementation the");
                pw.println(" * ");
                pw.println(" * @author Miguel");
                pw.println(" */");
                pw.println("public class AsyncFactory implements io.mateu.ui.core.shared.AsyncFactory {");
                pw.println();



                pw.println("\t\tpublic Object create(Class<?> serviceInterface) {");


                pw.println("if (\"io.mateu.ui.core.shared.BaseService\".equals(serviceInterface.getName())) return new io.mateu.ui.core.client.BaseServiceTeavmClientSideImpl();");

                int pos = 1;
                for (TypeElement clase : services) {

                    String simpleName = clase.getSimpleName().toString();
                    String packageName = elementsUtils.getPackageOf(clase).getQualifiedName().toString();
                    String typeName = clase.getSimpleName().toString() + "TeavmClientSideImpl";
                    String typeNameForAsync = clase.getSimpleName().toString() + "Async";

                    String pn = packageName;


                    String pnc = packageName.replaceAll("\\.shared\\.", ".client.");
                    if (pnc.endsWith(".shared")) pnc = pnc.substring(0, pnc.lastIndexOf(".") + 1) + "client";

                    String pns = packageName.replaceAll("\\.shared\\.", ".server.");
                    if (pns.endsWith(".shared")) pns = pns.substring(0, pns.lastIndexOf(".") + 1) + "server";

                    pw.println("\t\t\t" + ((pos > 0)?"else ":"") + "if (\"" + packageName + "." + simpleName + "\".equals(serviceInterface.getName())) return new " + pnc + "." + typeName + "();");

                    messager.printMessage(Kind.NOTE, "" + packageName + "->" + pn);

                    pos++;
                }
                pw.println("\t\t\telse return null;");
                pw.println("\t\t}");

                pw.println("}");
                pw.close();

            }



        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void generateTeavmClientSideImpl(TypeElement clase) {
        messager.printMessage(Kind.NOTE, "generando implementación lado cliente...");
        try {
            String simpleName = clase.getSimpleName().toString();
            String packageName = elementsUtils.getPackageOf(clase).getQualifiedName().toString();
            String typeName = clase.getSimpleName().toString() + "TeavmClientSideImpl";

            String pn = packageName;


            String pnc = packageName.replaceAll("\\.shared\\.", ".client.");
            if (pnc.endsWith(".shared")) pnc = pnc.substring(0, pnc.lastIndexOf(".") + 1) + "client";

            String pns = packageName.replaceAll("\\.shared\\.", ".server.");
            if (pns.endsWith(".shared")) pns = pns.substring(0, pns.lastIndexOf(".") + 1) + "server";

            messager.printMessage(Kind.NOTE, "" + packageName + "->" + pn);

            JavaFileObject javaFile = filer.createSourceFile(pnc + "." + typeName, clase);
            messager.printMessage(Kind.NOTE, "generando " + javaFile.toUri() + "...");
            Writer writer = javaFile.openWriter();
            PrintWriter pw = new PrintWriter(writer);

            pw.println("package " + pnc + ";");
            pw.println("");
            pw.println("import io.mateu.ui.core.shared.AsyncCallback;");
            pw.println("import org.teavm.jso.ajax.XMLHttpRequest;\n" +
                    "\n" +
                    "import java.io.UnsupportedEncodingException;\n" +
                    "import java.net.URLEncoder;");
            pw.println("");
            pw.println("");
            pw.println("/**");
            pw.println(" * Generated class creating a default implementation the");
            pw.println(" * for the class {@link " + clase.getQualifiedName().toString() + "}");
            pw.println(" * ");
            pw.println(" * @author Miguel");
            pw.println(" */");
            pw.println("public class " + typeName + " implements " + pnc + "." + clase.getSimpleName().toString() + "Async {");
            pw.println();

            List<String> usedPaths = new ArrayList<>();
            for (ExecutableElement m : ElementFilter.methodsIn(clase.getEnclosedElements())) {

                pw.println("\t@Override");

                String s = "\tpublic void ";

                s += m.getSimpleName();
                s += "(";
                for (VariableElement p : m.getParameters()) {
                    s += p.asType().toString();
                    s += " ";
                    s += p.getSimpleName();
                    s += ", ";
                }
                s += "AsyncCallback<" + getTipoCallback(m.getReturnType()) + "> callback";
                s += ")";

                s += " {\n\n";

                s += "";

                s += "\t\ttry {\n\n\t\t\t\t";

                String path = m.getSimpleName().toString();

                int aux = 1;
                String originalPath = path;
                while (usedPaths.contains(path)) {
                    path = originalPath + aux++;
                }
                usedPaths.add(path);

                s += "XMLHttpRequest xhr = XMLHttpRequest.create();\n" +
                        //"        xhr.onComplete(() -> receiveResponse(xhr.getResponseText()));\n" +
                        "        xhr.onComplete(() -> {\n" +
                        "            if (xhr.getStatus() == 200) {\n";
                String tcb = getTipoCallback(m.getReturnType());
                if ("Void".equals(tcb)) {
                    s += "                callback.onSuccess(null);\n";
                } else if (String.class.getName().equals(tcb)) {
                    s += "                callback.onSuccess(xhr.getResponseText());\n";
                } else if ("int".equals(tcb)) {
                    s += "                callback.onSuccess(Integer.parseLong(xhr.getResponseText()));\n";
                } else if ("long".equals(tcb)) {
                    s += "                callback.onSuccess(Long.parseInt(xhr.getResponseText()));\n";
                } else if ("double".equals(tcb)) {
                    s += "                callback.onSuccess(Double.parseDouble(xhr.getResponseText()));\n";
                } else if ("boolean".equals(tcb)) {
                    s += "                callback.onSuccess(Boolean.parseBoolean(xhr.getResponseText()));\n";
                } else if ("io.mateu.ui.core.shared.Data".equals(tcb)) {
                    s += "                callback.onSuccess(new io.mateu.ui.core.shared.Data(xhr.getResponseText()));\n";
                }
                        s += "            } else {\n" +
                        "                callback.onFailure(new Throwable(\"\" + xhr.getStatus() + \":\" + xhr.getStatusText() + \": \" + xhr.getResponseText()));\n" +
                        "            }\n" +
                        "        });\n" +
                        "        xhr.open(\"POST\", \"resources/" + packageName.replaceAll("\\.", "_") + "_" + simpleName + "/" + path + "\");\n" +
                        "        xhr.setRequestHeader(\"Accept\", \"application/json\");\n" +
                        "        xhr.setRequestHeader(\"Content-Type\", \"application/x-www-form-urlencoded\");\n" +
                        "        String c = \"\";\n";

                for (VariableElement p : m.getParameters()) {
                    //s += "        try {\n";

                    if (!("boolean".equals(p.asType().toString()) || "int".equals(p.asType().toString())
                            || "long".equals(p.asType().toString())  || "double".equals(p.asType().toString())))
                        s +=                " if (" + p.getSimpleName() + " != null) ";

                    s += " {";

                    s +=                 "            if (!\"\".equals(c)) ";
                    s += " c += \"&\";\n" +
                                    //"            c += \"" + p.getSimpleName() + "=\" + URLEncoder.encode(\"\" + " + p.getSimpleName() + ");\n" +
                            "            c += \"" + p.getSimpleName() + "=\" + " + p.getSimpleName() + ";\n" +
                                    "        } " +
                            //"} catch (UnsupportedEncodingException e) {\n" +
                              //      "            e.printStackTrace();\n" +
                                //    "        " +
                             //       "}" +
                            "\n";
                }

                s +=
                        "        xhr.send(c);";

/*
XMLHttpRequest xhr = XMLHttpRequest.create();
        xhr.onComplete(() -> receiveResponse(xhr.getResponseText()));
        xhr.setRequestHeader("Accept", "application/json");
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.open("POST", "http://xxx");
        String c = "";
        try {
            if (!"".equals(c)) c += "&";
            c += "parametro=" + URLEncoder.encode("", "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        xhr.send(c);
*/

/*
                if (!TypeKind.VOID.equals(m.getReturnType().getKind())) s += getTipoCallback(m.getReturnType()) + " r = ";
                //s += "new " + pns + "." + simpleName + "Impl().";
                s += "((" + clase.asType().toString() + ")Class.forName(\"" + pns + "." + simpleName + "Impl\").newInstance()).";

                s += m.getSimpleName();
                s += "(";
                int pos = 0;
                for (VariableElement p : m.getParameters()) {
                    if (pos++ > 0) s += ", ";
                    s += p.getSimpleName();
                }
                s += ")";

                s += ";";

                s += "\n\n";

                s += "" +
                        "\n" +
                        "                            callback.onSuccess(" + ((TypeKind.VOID.equals(m.getReturnType().getKind()))?"null":"r") + ");\n" +
                        "\n" +
                        "                        ";

*/


                s += "\n\n\t\t} catch (Throwable e) {\n" +
                        "e.printStackTrace();";

                s += "\n" +
                        "\n" +
                        "                            callback.onFailure(e);\n" +
                        "\n" +
                        "                        ";

                s += "\n\n\t\t}";

                s += "         }   ";

                pw.println(s);
                pw.println();


            }

            pw.println("}");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void generateTeavmClientSideImpl0(TypeElement clase) {
        messager.printMessage(Kind.NOTE, "generando implementación lado cliente...");
        try {
            String simpleName = clase.getSimpleName().toString();
            String packageName = elementsUtils.getPackageOf(clase).getQualifiedName().toString();
            String typeName = clase.getSimpleName().toString() + "TeavmClientSideImpl";

            String pn = packageName;


            String pnc = packageName.replaceAll("\\.shared\\.", ".client.");
            if (pnc.endsWith(".shared")) pnc = pnc.substring(0, pnc.lastIndexOf(".") + 1) + "client";

            String pns = packageName.replaceAll("\\.shared\\.", ".server.");
            if (pns.endsWith(".shared")) pns = pns.substring(0, pns.lastIndexOf(".") + 1) + "server";

            messager.printMessage(Kind.NOTE, "" + packageName + "->" + pn);

            JavaFileObject javaFile = filer.createSourceFile(pnc + "." + typeName, clase);
            messager.printMessage(Kind.NOTE, "generando " + javaFile.toUri() + "...");
            Writer writer = javaFile.openWriter();
            PrintWriter pw = new PrintWriter(writer);

            pw.println("package " + pnc + ";");
            pw.println("");
            pw.println("import io.mateu.ui.core.shared.AsyncCallback;");
            pw.println("");
            pw.println("/**");
            pw.println(" * Generated class creating a default implementation the");
            pw.println(" * for the class {@link " + clase.getQualifiedName().toString() + "}");
            pw.println(" * ");
            pw.println(" * @author Miguel");
            pw.println(" */");
            pw.println("public class " + typeName + " implements " + pnc + "." + clase.getSimpleName().toString() + "Async {");
            pw.println();

            for (ExecutableElement m : ElementFilter.methodsIn(clase.getEnclosedElements())) {

                pw.println("\t@Override");

                String s = "\tpublic void ";

                s += m.getSimpleName();
                s += "(";
                for (VariableElement p : m.getParameters()) {
                    s += p.asType().toString();
                    s += " ";
                    s += p.getSimpleName();
                    s += ", ";
                }
                s += "AsyncCallback<" + getTipoCallback(m.getReturnType()) + "> callback";
                s += ")";

                s += " {\n\n";

                s += "";

                s += "\t\ttry {\n\n\t\t\t\t";


                s += "\t\tSystem.out.println(\"called " + m.getSimpleName() + "\");";


                /*
                if (!TypeKind.VOID.equals(m.getReturnType().getKind())) s += getTipoCallback(m.getReturnType()) + " r = ";
                //s += "new " + pns + "." + simpleName + "Impl().";
                s += "((" + clase.asType().toString() + ")Class.forName(\"" + pns + "." + simpleName + "Impl\").newInstance()).";

                s += m.getSimpleName();
                s += "(";
                int pos = 0;
                for (VariableElement p : m.getParameters()) {
                    if (pos++ > 0) s += ", ";
                    s += p.getSimpleName();
                }
                s += ")";

                s += ";";

                s += "\n\n";

                s += "" +
                        "\n" +
                        "                            callback.onSuccess(" + ((TypeKind.VOID.equals(m.getReturnType().getKind()))?"null":"r") + ");\n" +
                        "\n" +
                        "                        ";


                        */

                s += "\n\n\t\t} catch (Throwable e) {\n" +
                        "e.printStackTrace();";

                s += "\n" +
                        "\n" +
                        "                            callback.onFailure(e);\n" +
                        "\n" +
                        "                        ";

                s += "\n\n\t\t}";

                s += "            ";

                pw.println(s);
                pw.println("\n\n\t}");
                pw.println();


            }

            pw.println("}");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateClientSideImpl(TypeElement clase) {
        messager.printMessage(Kind.NOTE, "generando implementación lado cliente...");
        try {
            String simpleName = clase.getSimpleName().toString();
            String packageName = elementsUtils.getPackageOf(clase).getQualifiedName().toString();
            String typeName = clase.getSimpleName().toString() + "ClientSideImpl";

            String pn = packageName;


            String pnc = packageName.replaceAll("\\.shared\\.", ".client.");
            if (pnc.endsWith(".shared")) pnc = pnc.substring(0, pnc.lastIndexOf(".") + 1) + "client";

            String pns = packageName.replaceAll("\\.shared\\.", ".server.");
            if (pns.endsWith(".shared")) pns = pns.substring(0, pns.lastIndexOf(".") + 1) + "server";

            messager.printMessage(Kind.NOTE, "" + packageName + "->" + pn);

            JavaFileObject javaFile = filer.createSourceFile(pnc + "." + typeName, clase);
            messager.printMessage(Kind.NOTE, "generando " + javaFile.toUri() + "...");
            Writer writer = javaFile.openWriter();
            PrintWriter pw = new PrintWriter(writer);

            pw.println("package " + pnc + ";");
            pw.println("");
            pw.println("import io.mateu.ui.core.shared.AsyncCallback;");
            pw.println("");
            pw.println("/**");
            pw.println(" * Generated class creating a default implementation the");
            pw.println(" * for the class {@link " + clase.getQualifiedName().toString() + "}");
            pw.println(" * ");
            pw.println(" * @author Miguel");
            pw.println(" */");
            pw.println("public class " + typeName + " implements " + pnc + "." + clase.getSimpleName().toString() + "Async {");
            pw.println();

            for (ExecutableElement m : ElementFilter.methodsIn(clase.getEnclosedElements())) {

                pw.println("\t@Override");

                String s = "\tpublic void ";

                s += m.getSimpleName();
                s += "(";
                for (VariableElement p : m.getParameters()) {
                    s += p.asType().toString();
                    s += " ";
                    s += p.getSimpleName();
                    s += ", ";
                }
                s += "AsyncCallback<" + getTipoCallback(m.getReturnType()) + "> callback";
                s += ")";

                s += " {\n\n";

                s += "";

                s += "\t\ttry {\n\n\t\t\t\t";


                if (!TypeKind.VOID.equals(m.getReturnType().getKind())) s += getTipoCallback(m.getReturnType()) + " r = ";
                //s += "new " + pns + "." + simpleName + "Impl().";
                s += "((" + clase.asType().toString() + ")Class.forName(\"" + pns + "." + simpleName + "Impl\").newInstance()).";

                s += m.getSimpleName();
                s += "(";
                int pos = 0;
                for (VariableElement p : m.getParameters()) {
                    if (pos++ > 0) s += ", ";
                    s += p.getSimpleName();
                }
                s += ")";

                s += ";";

                s += "\n\n";

                s += "" +
                        "\n" +
                        "                            callback.onSuccess(" + ((TypeKind.VOID.equals(m.getReturnType().getKind()))?"null":"r") + ");\n" +
                        "\n" +
                        "                        ";

                s += "\n\n\t\t} catch (Throwable e) {\n" +
                        "e.printStackTrace();";

                s += "\n" +
                        "\n" +
                        "                            callback.onFailure(e);\n" +
                        "\n" +
                        "                        ";

                s += "\n\n\t\t}";

                s += "            ";

                pw.println(s);
                pw.println("\n\n\t}");
                pw.println();


            }

            pw.println("}");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void generateThreadedClientSideImpl(TypeElement clase) {
        messager.printMessage(Kind.NOTE, "generando implementación lado cliente...");
        try {
            String simpleName = clase.getSimpleName().toString();
            String packageName = elementsUtils.getPackageOf(clase).getQualifiedName().toString();
            String typeName = clase.getSimpleName().toString() + "ClientSideImpl";

            String pn = packageName;


            String pnc = packageName.replaceAll("\\.shared\\.", ".client.");
            if (pnc.endsWith(".shared")) pnc = pnc.substring(0, pnc.lastIndexOf(".") + 1) + "client";

            String pns = packageName.replaceAll("\\.shared\\.", ".server.");
            if (pns.endsWith(".shared")) pns = pns.substring(0, pns.lastIndexOf(".") + 1) + "server";

            messager.printMessage(Kind.NOTE, "" + packageName + "->" + pn);

            JavaFileObject javaFile = filer.createSourceFile(pnc + "." + typeName, clase);
            messager.printMessage(Kind.NOTE, "generando " + javaFile.toUri() + "...");
            Writer writer = javaFile.openWriter();
            PrintWriter pw = new PrintWriter(writer);

            pw.println("package " + pnc + ";");
            pw.println("");
            pw.println("import io.mateu.ui.core.shared.AsyncCallback;");
            pw.println("import io.mateu.ui.core.client.app.MateuUI;");
            pw.println("");
            pw.println("/**");
            pw.println(" * Generated class creating a default implementation the");
            pw.println(" * for the class {@link " + clase.getQualifiedName().toString() + "}");
            pw.println(" * ");
            pw.println(" * @author Miguel");
            pw.println(" */");
            pw.println("public class " + typeName + " implements " + pnc + "." + clase.getSimpleName().toString() + "Async {");
            pw.println();

            for (ExecutableElement m : ElementFilter.methodsIn(clase.getEnclosedElements())) {

                pw.println("\t@Override");

                String s = "\tpublic void ";

                s += m.getSimpleName();
                s += "(";
                for (VariableElement p : m.getParameters()) {
                    s += p.asType().toString();
                    s += " ";
                    s += p.getSimpleName();
                    s += ", ";
                }
                s += "AsyncCallback<" + getTipoCallback(m.getReturnType()) + "> callback";
                s += ")";

                s += " {\n\n";

                s += "MateuUI.run(new Runnable() {\n" +
                        "            @Override\n" +
                        "            public void run() {";

                s += "\t\ttry {\n\n\t\t\t\t";


                if (!TypeKind.VOID.equals(m.getReturnType().getKind())) s += getTipoCallback(m.getReturnType()) + " r = ";
                //s += "new " + pns + "." + simpleName + "Impl().";
                s += "((" + clase.asType().toString() + ")Class.forName(\"" + pns + "." + simpleName + "Impl\").newInstance()).";

                s += m.getSimpleName();
                s += "(";
                int pos = 0;
                for (VariableElement p : m.getParameters()) {
                    if (pos++ > 0) s += ", ";
                    s += p.getSimpleName();
                }
                s += ")";

                s += ";";

                s += "\n\n";

                s += "MateuUI.runInUIThread(new Runnable() {\n" +
                        "                        @Override\n" +
                        "                        public void run() {\n" +
                        "\n" +
                        "                            callback.onSuccess(" + ((TypeKind.VOID.equals(m.getReturnType().getKind()))?"null":"r") + ");\n" +
                        "\n" +
                        "                        }\n" +
                        "                    });";

                s += "\n\n\t\t} catch (Exception e) {\n" +
                        "e.printStackTrace();";

                s += "MateuUI.runInUIThread(new Runnable() {\n" +
                        "                        @Override\n" +
                        "                        public void run() {\n" +
                        "\n" +
                        "                            callback.onFailure(e);\n" +
                        "\n" +
                        "                        }\n" +
                        "                    });";

                s += "\n\n\t\t}";

                s += "            }\n" +
                        "        });";

                pw.println(s);
                pw.println("\n\n\t}");
                pw.println();


            }

            pw.println("}");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void generateHessian(TypeElement clase) {
        messager.printMessage(Kind.NOTE, "generando hessian...");
        try {
            String simpleName = clase.getSimpleName().toString();
            String packageName = elementsUtils.getPackageOf(clase).getQualifiedName().toString();
            String typeName = clase.getSimpleName().toString() + "Hessian";

            String pn = packageName.replaceAll("\\.client\\.", ".server.");
            if (pn.endsWith(".client")) pn = pn.substring(0, pn.lastIndexOf(".") + 1) + "server";

            messager.printMessage(Kind.NOTE, "" + packageName + "->" + pn);

            JavaFileObject javaFile = filer.createSourceFile(pn + "." + typeName, clase);
            messager.printMessage(Kind.NOTE, "generando " + javaFile.toUri() + "...");
            Writer writer = javaFile.openWriter();
            PrintWriter pw = new PrintWriter(writer);

            pw.println("package " + pn + ";");
            pw.println("");
            pw.println("import com.caucho.hessian.server.HessianServlet;");
            pw.println("");
            pw.println("/**");
            pw.println(" * Generated class creating an hessian implementation the");
            pw.println(" * for the class {@link " + clase.getQualifiedName().toString() + "}");
            pw.println(" * ");
            pw.println(" * @author Miguel");
            pw.println(" */");
            pw.println("public class " + typeName + " extends HessianServlet implements " + clase.asType().toString() + " {");
            pw.println();


            for (ExecutableElement m : ElementFilter.methodsIn(clase.getEnclosedElements())) {

				/*
				 	@Override
	public AlmacenDatos getDatosCaja(String id) throws Exception {
		return new CarteraServiceImpl().getDatosCaja(id);
	}

				 */

                pw.println("\t@Override");
                String pars = "";
                int pos = 0;
                for (VariableElement p : m.getParameters()) {
                    if (pos++ > 0) pars += ", ";
                    pars += p.asType().toString() + " " + p.getSimpleName();
                }

                String z = "";
                if (m.getThrownTypes().size() > 0) {
                    z += " throws ";
                    pos = 0;
                    for (TypeMirror t : m.getThrownTypes()) {
                        if (pos++ > 0) z += ", ";
                        z += t.toString();
                    }
                }

                pw.println("\tpublic " + ((TypeKind.VOID.equals(m.getReturnType().getKind()))?"void":m.getReturnType().toString()) + " " + m.getSimpleName() + "(" + pars + ") " + z +" {");

                String s = "\t\t";
                if (!TypeKind.VOID.equals(m.getReturnType().getKind())) s += "return ";
                s += "new " + simpleName + "Impl().";

                s += m.getSimpleName();
                s += "(";
                pos = 0;
                for (VariableElement p : m.getParameters()) {
                    if (pos++ > 0) s += ", ";
                    s += p.getSimpleName();
                }
                s += ")";

                s += ";";

                pw.println(s);
                pw.println("\t}");
                pw.println();


            }


            pw.println("}");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateInterfazAsincrona(Messager messager, Elements elementsUtils, Types typeUtils, Filer filer, TypeElement clase) {
        messager.printMessage(Kind.NOTE, "generando interfaz asíncrona...");
        try {
            String simpleName = clase.getSimpleName().toString();
            String packageName = elementsUtils.getPackageOf(clase).getQualifiedName().toString();
            String typeName = clase.getSimpleName().toString() + "Async";

            packageName = packageName.replaceAll("\\.shared\\.", ".client.");
            if (packageName.endsWith(".shared")) packageName = packageName.substring(0, packageName.lastIndexOf(".") + 1) + "client";


            JavaFileObject javaFile = filer.createSourceFile(packageName + "." + typeName, clase);
            messager.printMessage(Kind.NOTE, "generando " + javaFile.toUri() + "...");
            Writer writer = javaFile.openWriter();
            PrintWriter pw = new PrintWriter(writer);

            pw.println("package " + packageName + ";");
            pw.println("");
            pw.println("import io.mateu.ui.core.shared.AsyncCallback;");
            pw.println("");
            pw.println("/**");
            pw.println(" * Generated class creating an async interface for the");
            pw.println(" * for the class {@link " + clase.getQualifiedName().toString() + "}");
            pw.println(" * ");
            pw.println(" * @author Miguel");
            pw.println(" */");
            pw.println("public interface " + typeName + " {");
            pw.println();

            for (ExecutableElement m : ElementFilter.methodsIn(clase.getEnclosedElements())) {

                String s = "\tpublic void ";

                s += m.getSimpleName();
                s += "(";
                for (VariableElement p : m.getParameters()) {
                    s += p.asType().toString();
                    s += " ";
                    s += p.getSimpleName();
                    s += ", ";
                }
                s += "AsyncCallback<" + getTipoCallback(m.getReturnType()) + "> callback";
                s += ")";

                s += ";";

                pw.println(s);
                pw.println();


            }


            pw.println("}");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getTipoCallback(TypeMirror typeMirror) {
        TypeKind k = typeMirror.getKind();
        if (TypeKind.VOID.equals(k)) return "Void";
        else if (k.isPrimitive()) {
            if (TypeKind.INT.equals(k)) return "Integer";
            else if (TypeKind.LONG.equals(k)) return "Long";
            else if (TypeKind.BOOLEAN.equals(k)) return "Boolean";
            else if (TypeKind.DOUBLE.equals(k)) return "Double";
            else return typeMirror.getKind().name();
        } else return typeMirror.toString();
    }

}
