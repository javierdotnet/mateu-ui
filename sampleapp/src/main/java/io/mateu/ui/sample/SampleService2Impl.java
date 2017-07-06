package io.mateu.ui.sample;

/**
 * Created by miguel on 2/7/17.
 */
public class SampleService2Impl implements SampleService2 {
    @Override
    public String eco(String msg) {
        System.out.println("Esto pasa en el servidor en el servicio 2!");
        return "Hola " + msg + "!";
    }
}
