package io.mateu.ui.core.shared;

import io.mateu.ui.core.communication.Service;

import java.net.URL;

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


    public FileLocator upload(String fileName, byte[] bytes, boolean temporary) throws Exception;

    public UserData authenticate(String login, String password) throws Exception;

    public void changePassword(String login, String oldPassword, String newPassword) throws Exception;

    public void updateProfile(String login, String name, String email, FileLocator foto) throws Exception;

    public URL dump(Data parameters) throws Exception;

}
