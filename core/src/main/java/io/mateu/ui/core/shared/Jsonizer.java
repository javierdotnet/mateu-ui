package io.mateu.ui.core.shared;

import com.google.common.collect.Lists;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.mateu.ui.core.shared.Jsonizer.ESTADO.*;

/**
 * Created by miguel on 3/7/17.
 */
public class Jsonizer {

    enum ESTADO {
        INITIAL, INSIDEDATA, INSIDELIST, INSIDELISTELEMENT, INSIDEKEY, INSIDEVALUE, INSIDESTRING, ESCAPED, INSIDEPAIRLIST, INSIDEPAIRLISTVALUE, INSIDEPAIRLISTTEXT
    }

    private boolean debug = false;


    ESTADO estadoActual = INITIAL;
    List<Object> pilaObjetos = new ArrayList<>();
    List<ESTADO> pilaEstados = new ArrayList<>();
    List<String> pilaClaves = new ArrayList<>();
    Object objetoEnCurso = null;
    StringBuffer lineaActual = new StringBuffer();
    Object valorPendiente = null;
    Pair parActual = null;

    public Object parseJson(String json) {

        json = json.trim();


        for (int pos = 0; pos < json.length(); pos++) {

            if (debug) System.out.println("leido: " + json.substring(0, pos) + ", estadoActual: " + estadoActual);

            char c = json.charAt(pos);


            if (estadoActual == ESCAPED) { // solo sucede cuando estamos dentro de un string

                lineaActual.append(c); // lo añadimos al string

                // volvemos al estadoActual anterior (dentro de un string)
                back();

            } else if (estadoActual == INSIDESTRING) {

                switch (c) {
                    case '\\': { // pasamos al estadoActual escapado
                        setEstado(ESCAPED, null);
                    } break;

                    case '"': { // fin del string
                        back();
                    }
                    default: lineaActual.append(c);
                        break;
                }

            } else {

                if (c != ',' && c != ' ' && c != '\t' && c != '\n') {
                    if (estadoActual == INSIDEDATA) {
                        setEstado(INSIDEKEY, null);
                    }

                    if (estadoActual == INSIDELIST && c != ']') {
                        setEstado(INSIDELISTELEMENT, null);
                    }

                    if (estadoActual == INSIDEPAIRLIST && c != '!') {
                        setEstado((parActual == null)?INSIDEPAIRLISTVALUE:INSIDEPAIRLISTTEXT, null);
                    }
                }


                switch (c) {
                    case ' ': case '\t':case '\n': {} break; // si no estamos dentro de un string estos caracteres se ignoran


                    case '{': {
                        setEstado(INSIDEDATA, new Data());
                    } break;
                    case '}': {
                        cerrar();
                    } break;


                    case '¡': {
                        setEstado(INSIDEPAIRLIST, new PairList());
                    } break;
                    case '!': {
                        cerrar();
                    } break;


                    case '[': {
                        setEstado(INSIDELIST, new ArrayList<>());
                    } break;
                    case ']': {
                        cerrar();
                    } break;


                    case ':': {

                        String s = lineaActual.toString();
                        if (s.startsWith("\"")) s = s.substring(1, s.length() - 1);
                        pilaClaves.add(s);

                        lineaActual = new StringBuffer();

                       back();
                       setEstado(INSIDEVALUE, null);

                    } break;

                    case ',': {

                        if (estadoActual == INSIDEVALUE || estadoActual == INSIDELISTELEMENT || estadoActual == INSIDEPAIRLISTVALUE || estadoActual == INSIDEPAIRLISTTEXT) acumular();

                    } break;

                    default: {

                        lineaActual.append(c);

                        if (c == '\"') {
                            setEstado(INSIDESTRING, null);
                        }

                    } break;


                }
            }

        }


        // hemos terminado. cerramos lo que tengamos abierto

        while (estadoActual != INITIAL) {
            cerrar();
        }

        return objetoEnCurso;

    }

    private void acumular() {

        if (estadoActual == INSIDEVALUE || estadoActual == INSIDELISTELEMENT || estadoActual == INSIDEPAIRLISTVALUE || estadoActual == INSIDEPAIRLISTTEXT) {

            Object valor = extractValue();

            addValor(valor);

        }

        if (estadoActual != INSIDELIST && estadoActual != INSIDEPAIRLIST) back();

    }

    /**
     * acumula el valor actual en el objeto en curso, y sube un nivel
     *
     */
    private void cerrar() {

        acumular();

        back();

        valorPendiente = objetoEnCurso;
        objetoEnCurso = desempilar();
    }

    /**
     * añade un valor al objeto en curso. Su comportamiento depende del tipo del objeto actual
     *
     * @param valor
     */
    private void addValor(Object valor) {

        if (estadoActual == INSIDEPAIRLISTVALUE) {

            Pair p = new Pair();
            p.setValue(valor);

            parActual = p;

            ((PairList) objetoEnCurso).getValues().add(p);

        } else if (estadoActual == INSIDEPAIRLISTTEXT) {

            Pair p = parActual;
            p.setText("" + valor);

            parActual = null;

        } else if (estadoActual == INSIDEVALUE) {
            String pn = pilaClaves.remove(pilaClaves.size() - 1);
            if ("___dataclassname".equalsIgnoreCase(pn)) {
                try {
                    Data old = (Data) objetoEnCurso;
                    objetoEnCurso = Class.forName((String) valor).newInstance();
                    ((Data) objetoEnCurso).setAll(old);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ((Data) objetoEnCurso).set(pn, valor);
            }
        } else if (estadoActual == INSIDELISTELEMENT) {
            ((List) objetoEnCurso).add(valor);
        }

    }

    /**
     * desempila on objeto
     *
     * @return
     */
    private Object desempilar() {

        Object o = null;
        if (pilaObjetos.size() > 0) o = pilaObjetos.remove(pilaObjetos.size() - 1);
        else o = objetoEnCurso;

        return o;
    }

    /**
     * convierte la línea actual en un valor
     *
     * @return
     */
    private Object extractValue() {
        Object valor = valorPendiente;

        if (valor == null) {

            String s = lineaActual.toString();

            if (s.length() > 0) {

                valor = s;
                if (s.length() > 1 && s.startsWith("\"")) valor = s.substring(1, s.length() - 1);
                else if (s.contains(".")) valor = Double.parseDouble(s);
                else if ("true".equals(s)) valor = true;
                else if ("false".equals(s)) valor = false;
                else if ("null".equals(s)) valor = null;
                else if (s.endsWith("l")) valor = Long.parseLong(s.replaceAll("l", ""));
                else if (s.contains("-")) valor = LocalDate.parse(s);
                else valor = Integer.parseInt(s);

            }

            lineaActual = new StringBuffer();
        }


        valorPendiente = null;

        return valor;
    }

    /**
     * acumula el estado actual en la pila de estados
     * acumula el objeto actual en la pila de objetos
     * el objeto actual es el nuevo objeto
     * si el estado en INSIDEDATA, INSIDELIST o INSIDEPAIRLIST acumula un estado adicional
     *
     * @param nuevoEstado
     * @param nuevoObjetoEnCurso si es null, entonces solo es un cambio de estado. No apilamos ni cambiamos el objeto en curso
     */
    private void setEstado(ESTADO nuevoEstado, Object nuevoObjetoEnCurso) {

        pilaEstados.add(estadoActual);
        estadoActual = nuevoEstado;

        if (nuevoObjetoEnCurso != null) {
            if (objetoEnCurso != null) pilaObjetos.add(objetoEnCurso);
            objetoEnCurso = nuevoObjetoEnCurso;
        }

    }

    /**
     * vuelve al estadoActual anterior, objetoEnCurso a INITIAL si la pilaObjetos de pilaEstados está vacía
     */
    private void back() {
        if (pilaEstados.size() > 0) estadoActual = pilaEstados.remove(pilaEstados.size() - 1);
        else estadoActual = INITIAL;
    }

    public String toJson(Object o) {
        StringBuffer sb = new StringBuffer("");

        if (o instanceof Data) {
            sb.append("{\n");

            Map<String, Object> m = ((Data) o).getProperties();

            int pos = 0;
            for (String n : m.keySet()) {
                if (pos++ > 0) sb.append(", ");
                sb.append("\"" + n + "\" :" + toJson(m.get(n)) + "\n");
            }
            if (pos++ > 0) sb.append(", ");
            sb.append("\"___dataclassname\" :\"" + o.getClass().getName() + "\"\n");

            sb.append("}");
        } else if (o instanceof PairList) {
            sb.append("¡\n");

            List<Pair> m = ((PairList) o).getValues();

            int pos = 0;
            if (m != null) for (Pair w : m) {
                if (pos++ > 0) sb.append(", ");
                sb.append(toJson(w.getValue()));
                sb.append(",");
                sb.append(toJson(w.getText()));
                sb.append("\n");
            }

            sb.append("!");
        } else if (o instanceof String) {
            return "\"" + ((String) o).replaceAll("\"", "\\\\\"") + "\"";
        } else if (o instanceof Object[]) {
            sb.append("[\n");

            int pos = 0;
            for (Object w : (Object[]) o) {
                if (pos++ > 0) sb.append(", ");
                sb.append(toJson(w));
            }
            sb.append("]");
        } else if (o instanceof int[]) {
            sb.append("[");

            int pos = 0;
            for (Object w : (int[]) o) {
                if (pos++ > 0) sb.append(", ");
                sb.append(toJson(w));
            }
            sb.append("]");
        } else if (o instanceof long[]) {
            sb.append("[");

            int pos = 0;
            for (Object w : (long[]) o) {
                if (pos++ > 0) sb.append(", ");
                sb.append(toJson(w));
            }
            sb.append("]");
        } else if (o instanceof double[]) {
            sb.append("[");

            int pos = 0;
            for (Object w : (double[]) o) {
                if (pos++ > 0) sb.append(", ");
                sb.append(toJson(w));
            }
            sb.append("]");
        } else if (o instanceof boolean[]) {
            sb.append("[");

            int pos = 0;
            for (Object w : (boolean[]) o) {
                if (pos++ > 0) sb.append(", ");
                sb.append(toJson(w));
            }
            sb.append("]");
        } else if (o instanceof List) {
            sb.append("[");

            int pos = 0;
            for (Object w : (List) o) {
                if (pos++ > 0) sb.append(", ");
                sb.append(toJson(w));
            }
            sb.append("]");
        } else if (o instanceof Long) {
            return "" + o + "l";
        } else {
            return "" + o;
        }

        return sb.toString();
    }


    public static void main(String[] args) {

        Data d = new Data("a", 1221, "b", "iws{qw\"qq}hwi", "c", new Data("a", 1, "b", 2), "d", new Pair(23, "veititres")); //, "e", new PairList()
        //Data d = new Data("a", 1221, "b", "iws{qw\"qq}hwi", "c", new Data("a", 1, "b", 2), "d", new Pair(23, "veititres"), "l", Lists.newArrayList(new Data("a", 1, "b", 2), new Data("a", 1, "b", 2))); //, "e", new PairList()

        //Data d = new Data("a", 1221, "l", Lists.newArrayList(new Data("a", 1, "b", 2), new Data("a", 1, "b", 2))); //, "e", new PairList()

        //Data d = new Data("a", 1221, "e", new PairList());

        //Data d = new Data("a", 1221, "e", new PairList(1, "uno", "2", "dos"));

        //Data d = new Data("l", new ArrayList<>()); //, "e", new PairList()

        System.out.println(d.toJson());

        System.out.println("************************************************");

        Data d2 = new Data(d.toJson());

        System.out.println(d2.toJson());

    }

}
