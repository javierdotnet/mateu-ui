package io.mateu.ui.core.shared;

import io.mateu.ui.core.communication.Service;

import java.net.URL;

/**
 * Created by miguel on 27/12/16.
 */
@Service()
public interface BaseService {

    public Object[][] select(String sql) throws Throwable;

    public Object selectSingleValue(String sql) throws Throwable;

    public void execute(String sql) throws Throwable;

    public Data selectPaginated(Data parameters) throws Throwable;


    public Data set(String serverSideControllerKey, Data data) throws Throwable;

    public Data get(String serverSideControllerKey, Object id) throws Throwable;


    public FileLocator upload(String fileName, byte[] bytes, boolean temporary) throws Throwable;

    public UserData authenticate(String login, String password) throws Throwable;

    public void changePassword(String login, String oldPassword, String newPassword) throws Throwable;

    public void updateProfile(String login, String name, String email, FileLocator foto) throws Throwable;

    public void updateFoto(String login, FileLocator foto) throws Throwable;

    public URL dump(Data parameters) throws Throwable;

}
