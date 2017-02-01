package io.mateu.ui.sample.shared;

import io.mateu.ui.core.communication.Service;

/**
 * Created by miguel on 8/12/16.
 */
@Service(url = "")
public interface TestService {

    public String echo(String msg);

}
