package io.mateu.ui.sample;

import com.google.common.collect.Lists;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.shared.Data;

import java.util.List;

/**
 * Created by miguel on 2/7/17.
 */
public class SampleApp extends AbstractApplication {
    @Override
    public String getName() {
        return "Mi aplicación";
    }

    @Override
    public List<AbstractArea> getAreas() {
        return Lists.newArrayList(

                new AbstractArea("Area 1") {
                    @Override
                    public List<AbstractModule> getModules() {
                        return Lists.newArrayList(new AbstractModule() {
                            @Override
                            public List<MenuEntry> getMenu() {
                                return Lists.newArrayList(
                                        (MenuEntry) new AbstractAction("Test 1") {
                                            @Override
                                            public void run() {
                                                MateuUI.alert("test 1");
                                            }
                                        }
                                        , new AbstractAction("Test 2") {
                                            @Override
                                            public void run() {
                                                MateuUI.alert("test 2");
                                            }
                                        }
                                );
                            }
                        });
                    }
                }


        , new AbstractArea("Area 2") {
            @Override
            public List<AbstractModule> getModules() {
                return Lists.newArrayList(new AbstractModule() {
                    @Override
                    public List<MenuEntry> getMenu() {
                        return Lists.newArrayList(
                                (MenuEntry) new AbstractAction("Open simple view") {
                                    @Override
                                    public void run() {
                                        MateuUI.openView(new SimpleView());
                                    }
                                }
                                , new AbstractAction("Eco to server") {
                                    @Override
                                    public void run() {

                                        ((SampleServiceAsync) MateuUI.create(SampleService.class)).eco("Mateu", new Callback<String>() {
                                            @Override
                                            public void onSuccess(String result) {
                                                MateuUI.alert(result);
                                            }
                                        });

                                    }
                                }
                                , new AbstractAction("Eco data to server") {
                                    @Override
                                    public void run() {

                                        ((SampleServiceAsync) MateuUI.create(SampleService.class)).eco(new Data("nombre", "Mateu", "edad", 9), new Callback<Data>() {
                                            @Override
                                            public void onSuccess(Data result) {
                                                MateuUI.alert("" + result);
                                            }
                                        });

                                    }
                                }
                                , new AbstractAction("Error at server") {
                                    @Override
                                    public void run() {

                                        ((SampleServiceAsync) MateuUI.create(SampleService.class)).error(new Callback<Integer>() {
                                            @Override
                                            public void onSuccess(Integer result) {
                                                MateuUI.alert("" + result);
                                            }
                                        });

                                    }
                                }                        );
                    }
                });
            }
        }

        );
    }

}
