package io.mateu.ui.core.shared;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 3/7/17.
 */
public class Jsonizer {

    private static final int INITIAL = 0;
    private static final int INSIDEDATA = 1;
    private static final int INSIDELIST = 2;
    private static final int INSIDEKEY = 3;
    private static final int INSIDEVALUE = 4;
    private static final int INSIDESTRING = 5;
    private static final int ESCAPED = 6;


    public Object parseJson(String json) {

        json = json.trim();

        int estado = INITIAL;
        List<Object> pila = new ArrayList<>();
        List<Integer> estados = new ArrayList<>();
        Object o = null;

        List<String> clave = new ArrayList<>();

        for (int pos = 0; pos < json.length(); pos++) {
            //System.out.println("leido: " + json.substring(0, pos) + ", estado: " + estado);
            char c = json.charAt(pos);
            if (estado == ESCAPED) { // solo sucede cuando estamos dentro de un string

                ((StringBuffer)o).append(c); // no añadimos al string
                // volvemos al estado anterior (dentro de un string)
                if (estados.size() > 0) estado = estados.remove(estados.size() - 1);
                else estado = INITIAL;

            } else if (estado == INSIDESTRING) {

                switch (c) {
                    case '\\': { // pasamos al estado escapado
                        estados.add(estado);
                        estado = ESCAPED;
                    } break;
                    case '"': { // fin del string
                        ((StringBuffer)o).append(c);
                        if (estados.size() > 0) estado = estados.remove(estados.size() - 1);
                        else estado = INITIAL;
                    } break;
                    default: ((StringBuffer)o).append(c);
                }

            } else {

                switch (c) {
                    case ' ': case '\t':case '\n': {} break; // si no estamos dentro de un string estos caracteres se ignoran
                    case '{': {
                        if (estado == INSIDELIST) {
                            estados.add(estado);
                            estado = INSIDEVALUE;
                        }
                        estados.add(estado);
                        estado = INSIDEDATA;
                        if (!(o instanceof StringBuffer)) pila.add(o);
                        o = new Data();
                    } break;
                    case '}': {
                        if (estado == INSIDEVALUE) {
                            Object valor = o;

                            if (o instanceof StringBuffer) {

                                String s = ((StringBuffer)o).toString();

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

                            }

                            if (pila.size() > 0) o = pila.remove(pila.size() - 1);
                            else o = null;

                            if (estados.size() > 0) estado = estados.remove(estados.size() - 1);
                            else estado = INITIAL;
                            if (estado == INSIDEDATA) {
                                String pn = clave.remove(clave.size() - 1);
                                if ("___dataclassname".equalsIgnoreCase(pn)) {
                                    try {
                                        Data old = (Data) o;
                                        o = Class.forName((String) valor).newInstance();
                                        ((Data)o).setAll(old);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    ((Data)o).set(pn, valor);
                                }
                            } else if (estado == INSIDELIST) {
                                ((List)o).add(valor);
                            }

                        }

                        if (estados.size() > 0) estado = estados.remove(estados.size() - 1);
                        else estado = INITIAL;

                    } break;

                    case '[': {
                        estados.add(estado);
                        estado = INSIDELIST;
                        if (!(o instanceof StringBuffer)) pila.add(o);
                        o = new ArrayList();
                    } break;
                    case ']': {

                        if (estado == INSIDEVALUE) {

                            Object valor = o;

                            if (o instanceof StringBuffer) {

                                String s = ((StringBuffer)o).toString();

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

                            }

                            if (pila.size() > 0) o = pila.remove(pila.size() - 1);
                            else o = null;


                            if (estados.size() > 0) estado = estados.remove(estados.size() - 1);
                            else estado = INITIAL;
                            if (estado == INSIDEDATA) {
                                String pn = clave.remove(clave.size() - 1);
                                if ("___dataclassname".equalsIgnoreCase(pn)) {
                                    try {
                                        Data old = (Data) o;
                                        o = Class.forName((String) valor).newInstance();
                                        ((Data)o).setAll(old);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    ((Data)o).set(pn, valor);
                                }
                            } else if (estado == INSIDELIST) {
                                ((List)o).add(valor);
                            }

                        }

                        if (estados.size() > 0) estado = estados.remove(estados.size() - 1);
                        else estado = INITIAL;

                    } break;

                    case ':': {

                        String s = ((StringBuffer)o).toString();
                        if (s.startsWith("\"")) s = s.substring(1, s.length() - 1);
                        clave.add(s);


                        o = new StringBuffer();

                        estado = INSIDEVALUE;

                    } break;
                    case ',': {

                        if (estado == INSIDEVALUE) {

                            Object valor = o;

                            if (o instanceof StringBuffer) {

                                String s = ((StringBuffer) o).toString();

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

                            }

                            if (pila.size() > 0) o = pila.remove(pila.size() - 1);
                            else o = null;

                            if (estados.size() > 0) estado = estados.remove(estados.size() - 1);
                            else estado = INITIAL;
                            if (estado == INSIDEDATA) {
                                String pn = clave.remove(clave.size() - 1);
                                if ("___dataclassname".equalsIgnoreCase(pn)) {
                                    try {
                                        Data old = (Data) o;
                                        o = Class.forName((String) valor).newInstance();
                                        ((Data)o).setAll(old);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    ((Data)o).set(pn, valor);
                                }
                            } else if (estado == INSIDELIST) {
                                ((List)o).add(valor);
                            }
                        }


                    } break;

                    default: {
                        if (!(o instanceof StringBuffer)) {
                            pila.add(o);
                            o = new StringBuffer();
                        }
                        if (estado == INSIDEDATA) {
                            estados.add(estado);
                            estado = INSIDEKEY;
                        }
                        if (estado == INSIDELIST) {
                            estados.add(estado);
                            estado = INSIDEVALUE;
                        }
                        ((StringBuffer)o).append(c);

                        if (c == '\"') {
                            estados.add(estado);
                            estado = INSIDESTRING;
                        }

                    } break;


                }
            }

        }

        while (estado != INITIAL) {
            if (estado == INSIDEVALUE) {
                Object valor = o;
                if (estados.size() > 0) estado = estados.remove(estados.size() - 1);
                else estado = INITIAL;
                if (pila.size() > 0) o = pila.remove(pila.size() - 1);
                else o = null;
                if (estado == INSIDEDATA) {
                    String pn = clave.remove(clave.size() - 1);
                    if ("___dataclassname".equalsIgnoreCase(pn)) {
                        try {
                            Data old = (Data) o;
                            o = Class.forName((String) valor).newInstance();
                            ((Data)o).setAll(old);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ((Data)o).set(pn, valor);
                    }
                } else if (estado == INSIDELIST) {
                    ((List)o).add(valor);
                }
            } else {
                if (estados.size() > 0) estado = estados.remove(estados.size() - 1);
                else estado = INITIAL;
            }
        }

        return o;

    }

    public String toJson(Object o) {
        StringBuffer sb = new StringBuffer("");

        if (o instanceof Data) {
            sb.append("{\n");

            Map<String, Object> m = ((Data)o).getProperties();

            int pos = 0;
            for (String n : m.keySet()) {
                if (pos++ > 0) sb.append(", ");
                sb.append("\"" + n + "\" :" + toJson(m.get(n)) + "\n");
            }
            if (pos++ > 0) sb.append(", ");
            sb.append("\"___dataclassname\" :\"" + o.getClass().getName()  + "\"\n");

            sb.append("}");
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
}
