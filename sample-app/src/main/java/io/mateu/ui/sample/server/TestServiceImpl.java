package io.mateu.ui.sample.server;

import io.mateu.ui.core.communication.ServiceImpl;
import io.mateu.ui.sample.shared.TestService;

/**
 * Created by miguel on 8/12/16.
 */
@ServiceImpl(url = "")
public class TestServiceImpl implements TestService {
    @Override
    public String echo(String msg) {
        return "Hola xxxx " + msg + ".";
    }
}
