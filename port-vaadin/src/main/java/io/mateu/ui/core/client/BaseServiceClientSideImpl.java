package io.mateu.ui.core.client;

import io.mateu.ui.core.shared.*;
import io.mateu.ui.core.client.app.MateuUI;

import java.net.URL;

/**
 * Created by miguel on 29/12/16.
 */
public class BaseServiceClientSideImpl implements BaseServiceAsync {
    @Override
    public void select(String sql, AsyncCallback<Object[][]> callback) {

        try {

                    Object[][] r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).select(sql);


                            callback.onSuccess(r);



                } catch (Exception e) {

                    e.printStackTrace();


                            callback.onFailure(e);
                }

    }



    @Override
    public void selectSingleValue(String sql, AsyncCallback<Object> callback) {



                try {

                    Object r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).selectSingleValue(sql);

                            callback.onSuccess(r);



                } catch (Exception e) {

                    e.printStackTrace();


                            callback.onFailure(e);


                }

    }

    @Override
    public void execute(String sql, AsyncCallback<Void> callback) {


                try {

                    ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).execute(sql);

                            callback.onSuccess(null);



                } catch (Exception e) {

                    e.printStackTrace();

                            callback.onFailure(e);


                }


    }

    @Override
    public void selectPaginated(Data parameters, AsyncCallback<Data> callback) {

                 try {

                    Data r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).selectPaginated(parameters);

                    callback.onSuccess(r);


                } catch (Exception e) {

                    e.printStackTrace();

                   callback.onFailure(e);
                }

    }

    @Override
    public void set(String serverSideControllerKey, Data data, AsyncCallback<Data> callback) {


                try {

                    Data r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).set(serverSideControllerKey, data);


                            callback.onSuccess(r);


                } catch (Exception e) {

                    e.printStackTrace();

                            callback.onFailure(e);

                }


    }

    @Override
    public void get(String serverSideControllerKey, Object id, AsyncCallback<Data> callback) {


                try {

                    Data r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).get(serverSideControllerKey, id);


                            callback.onSuccess(r);



                } catch (Exception e) {

                    e.printStackTrace();


                            callback.onFailure(e);


                }


    }

    @Override
    public void authenticate(String login, String password, AsyncCallback<UserData> callback) {


                try {

                    UserData result = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).authenticate(login, password);

                            callback.onSuccess(result);



                } catch (Exception e) {

                    e.printStackTrace();

                            callback.onFailure(e);

                }


    }

    @Override
    public void changePassword(String login, String oldPassword, String newPassword, AsyncCallback<Void> callback) {


                try {

                    ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).changePassword(login, oldPassword, newPassword);

                            callback.onSuccess(null);



                } catch (Exception e) {

                    e.printStackTrace();

                            callback.onFailure(e);


                }


    }

    @Override
    public void updateProfile(String login, String name, String email, FileLocator foto, AsyncCallback<Void> callback) {


                try {

                    ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).updateProfile(login, name, email, null);


                            callback.onSuccess(null);




                } catch (Exception e) {

                    e.printStackTrace();

                            callback.onFailure(e);


                }

    }

    @Override
    public void dump(Data parameters, AsyncCallback<URL> callback) {


                try {

                    URL r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).dump(parameters);


                            callback.onSuccess(r);



                } catch (Exception e) {

                    e.printStackTrace();

                            callback.onFailure(e);


                }

    }

    @Override
    public void upload(String fileName, byte[] bytes, boolean temporary, AsyncCallback<FileLocator> callback) {


                try {

                    FileLocator result = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).upload(fileName, bytes, temporary);


                            callback.onSuccess(result);



                } catch (Exception e) {

                    e.printStackTrace();

                            callback.onFailure(e);

                }


    }
}
