package io.mateu.ui.core.shared;

import io.mateu.ui.core.communication.Service;

/**
 * Created by miguel on 27/12/16.
 */
@Service()
public interface BaseService {

    public Object[][] select(String sql) throws Exception;

    public Object selectSingleValue(String sql) throws Exception;

    public void execute(String sql) throws Exception;

    public Data selectPaginated(Data parameters) throws Exception;


    public Data set(String serverSideControllerKey, Data data) throws Exception;

    public Data get(String serverSideControllerKey, Object id) throws IllegalAccessException, InstantiationException, Exception;


    public FileLocator upload(byte[] bytes) throws Exception;

}
