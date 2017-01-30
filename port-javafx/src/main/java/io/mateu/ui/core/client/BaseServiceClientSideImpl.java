package io.mateu.ui.core.client;

import io.mateu.ui.core.shared.*;
import io.mateu.ui.core.client.app.MateuUI;
import javafx.application.Platform;

/**
 * Created by miguel on 29/12/16.
 */
public class BaseServiceClientSideImpl implements BaseServiceAsync {
    @Override
    public void select(String sql, AsyncCallback<Object[][]> callback) {

        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    Object[][] r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).select(sql);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(r);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });

    }



    @Override
    public void selectSingleValue(String sql, AsyncCallback<Object> callback) {

        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    Object r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).selectSingleValue(sql);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(r);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });

    }

    @Override
    public void execute(String sql, AsyncCallback<Void> callback) {

        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).execute(sql);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(null);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });

    }

    @Override
    public void selectPaginated(Data parameters, AsyncCallback<Data> callback) {

        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    Data r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).selectPaginated(parameters);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(r);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });

    }

    @Override
    public void set(String serverSideControllerKey, Data data, AsyncCallback<Data> callback) {

        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    Data r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).set(serverSideControllerKey, data);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(r);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });

    }

    @Override
    public void get(String serverSideControllerKey, Object id, AsyncCallback<Data> callback) {

        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    Data r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).get(serverSideControllerKey, id);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(r);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });

    }

    @Override
    public void upload(String fileName, byte[] bytes, boolean temporary, AsyncCallback<FileLocator> callback) {
        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    FileLocator result = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).upload(fileName, bytes, temporary);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(result);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });

    }

    @Override
    public void authenticate(String login, String password, AsyncCallback<UserData> callback) {
        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    UserData result = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).authenticate(login, password);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(result);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });

    }

    @Override
    public void changePassword(String login, String oldPassword, String newPassword, AsyncCallback<Void> callback) {
        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).changePassword(login, oldPassword, newPassword);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(null);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });
    }

    @Override
    public void updateProfile(String login, String name, String email, FileLocator foto, AsyncCallback<Void> callback) {
        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).updateProfile(login, name, email, null);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(null);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });
    }
}
