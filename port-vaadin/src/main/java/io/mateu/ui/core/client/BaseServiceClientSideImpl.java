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



                } catch (Throwable e) {

                    e.printStackTrace();


                            callback.onFailure(e);
                }

    }



    @Override
    public void selectSingleValue(String sql, AsyncCallback<Object> callback) {



                try {

                    Object r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).selectSingleValue(sql);

                            callback.onSuccess(r);



                } catch (Throwable e) {

                    e.printStackTrace();


                            callback.onFailure(e);


                }

    }

    @Override
    public void execute(String sql, AsyncCallback<Void> callback) {


                try {

                    ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).execute(sql);

                            callback.onSuccess(null);



                } catch (Throwable e) {

                    e.printStackTrace();

                            callback.onFailure(e);


                }


    }

    @Override
    public void selectPaginated(Data parameters, AsyncCallback<Data> callback) {

                 try {

                    Data r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).selectPaginated(parameters);

                    callback.onSuccess(r);


                } catch (Throwable e) {

                    e.printStackTrace();

                   callback.onFailure(e);
                }

    }

    @Override
    public void set(String serverSideControllerKey, Data data, AsyncCallback<Data> callback) {


                try {

                    Data r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).set(serverSideControllerKey, data);


                            callback.onSuccess(r);


                } catch (Throwable e) {

                    e.printStackTrace();

                            callback.onFailure(e);

                }


    }

    @Override
    public void get(String serverSideControllerKey, long id, AsyncCallback<Data> callback) {


                try {

                    Data r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).get(serverSideControllerKey, id);


                            callback.onSuccess(r);



                } catch (Throwable e) {

                    e.printStackTrace();


                            callback.onFailure(e);


                }


    }

    @Override
    public void get(String serverSideControllerKey, int id, AsyncCallback<Data> callback) {


        try {

            Data r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).get(serverSideControllerKey, id);


            callback.onSuccess(r);



        } catch (Throwable e) {

            e.printStackTrace();


            callback.onFailure(e);


        }


    }

    @Override
    public void get(String serverSideControllerKey, String id, AsyncCallback<Data> callback) {


        try {

            Data r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).get(serverSideControllerKey, id);


            callback.onSuccess(r);



        } catch (Throwable e) {

            e.printStackTrace();


            callback.onFailure(e);


        }


    }

    @Override
    public void authenticate(String login, String password, AsyncCallback<UserData> callback) {


                try {

                    UserData result = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).authenticate(login, password);

                            callback.onSuccess(result);



                } catch (Throwable e) {

                    e.printStackTrace();

                            callback.onFailure(e);

                }


    }

    @Override
    public void changePassword(String login, String oldPassword, String newPassword, AsyncCallback<Void> callback) {


                try {

                    ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).changePassword(login, oldPassword, newPassword);

                            callback.onSuccess(null);



                } catch (Throwable e) {

                    e.printStackTrace();

                            callback.onFailure(e);


                }


    }

    @Override
    public void updateProfile(String login, String name, String email, FileLocator foto, AsyncCallback<Void> callback) {


                try {

                    ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).updateProfile(login, name, email, null);


                            callback.onSuccess(null);




                } catch (Throwable e) {

                    e.printStackTrace();

                            callback.onFailure(e);


                }

    }

    @Override
    public void updateFoto(String login, FileLocator foto, AsyncCallback<Void> callback) {
        try {

            ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).updateFoto(login, foto);


            callback.onSuccess(null);


        } catch (Throwable e) {

            e.printStackTrace();

            callback.onFailure(e);


        }

    }

    @Override
    public void dump(Data parameters, AsyncCallback<URL> callback) {


                try {

                    URL r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).dump(parameters);


                            callback.onSuccess(r);



                } catch (Throwable e) {

                    e.printStackTrace();

                            callback.onFailure(e);


                }

    }

}
