package io.mateu.ui.core.shared;

import io.mateu.ui.core.communication.Service;

/**
 * Created by miguel on 27/12/16.
 */
@Service()
public interface BaseService {

    public String[][] sql(String sql);

}
