package io.mateu.ui.sample;

import io.mateu.ui.core.server.ServerSideHelper;
import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 2/7/17.
 */
public class SampleServiceImpl implements SampleService {
    @Override
    public String eco(String msg) {
        System.out.println("Esto pasa en el servidor!");
        return "Hola " + msg + "!";
    }

    @Override
    public Data eco(Data msg) {
        System.out.println("Esto pasa en el servidor!");
        System.out.println("data=" + msg);
        return msg;
    }

    @Override
    public int error() throws Throwable {



        throw new Exception("error xxx");
    }
}
