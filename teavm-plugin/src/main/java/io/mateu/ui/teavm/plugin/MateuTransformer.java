package io.mateu.ui.teavm.plugin;

import org.teavm.diagnostics.Diagnostics;
import org.teavm.model.*;
import org.teavm.model.util.ModelUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by miguel on 15/8/17.
 */
public class MateuTransformer implements ClassHolderTransformer {

    public MateuTransformer() {

    }

    public void transformClass(ClassHolder cls, ClassReaderSource innerSource, Diagnostics diagnostics) {

        System.out.println("revisando " + cls.getName() + "");

        if (cls.getName().equals(LocalDate.class.getName())) {
            substitute(cls, innerSource, SimpleLocalDate.class);
        } else if (cls.getName().equals(LocalDateTime.class.getName())) {
            substitute(cls, innerSource, SimpleLocalDateTime.class);
        } else return;
    }

    private void substitute(ClassHolder cls, ClassReaderSource classSource, Class clase) {
        System.out.println("sustituyendo " + cls.getName() + " por " + clase.getName());

        ClassReader subst = classSource.get(clase.getName());

        // vacia la clase (campos y metodos)
        for (FieldHolder field : cls.getFields().toArray(new FieldHolder[0])) {
            cls.removeField(field);
        }
        for (MethodHolder method : cls.getMethods().toArray(new MethodHolder[0])) {
            cls.removeMethod(method);
        }

        // copia el contenido (campos y metodos) de la clase sustituta
        for (FieldReader field : subst.getFields()) {
            cls.addField(ModelUtils.copyField(field));
        }
        for (MethodReader method : subst.getMethods()) {
            cls.addMethod(ModelUtils.copyMethod(method));
        }
    }
}
