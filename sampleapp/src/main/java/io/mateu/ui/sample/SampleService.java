package io.mateu.ui.sample;

import io.mateu.ui.core.communication.Service;
import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 2/7/17.
 */
@Service
public interface SampleService {

    public String eco(String msg);

    public Data eco(Data msg);

    public int error() throws Throwable;

}
