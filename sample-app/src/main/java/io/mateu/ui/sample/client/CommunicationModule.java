package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.BaseService;
import io.mateu.ui.core.client.BaseServiceAsync;
import io.mateu.ui.sample.shared.TestService;
import io.mateu.ui.sample.shared.TestServiceAsync;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 27/12/16.
 */
public class CommunicationModule extends AbstractModule {
    @Override
    public List<MenuEntry> getMenu() {
        return Arrays.asList((MenuEntry) new AbstractAction() {
            @Override
            public String getName() {
                return "echo";
            }

            @Override
            public void run() {
                TestServiceAsync s = MateuUI.create(TestService.class);
                s.echo("Hola!!", new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        MateuUI.alert("error!");
                    }

                    @Override
                    public void onSuccess(String result) {
                        MateuUI.alert("resultado: " + result);
                    }
                });
            }
        }, (MenuEntry) new AbstractAction() {
            @Override
            public String getName() {
                return "sql";
            }

            @Override
            public void run() {
                BaseServiceAsync s = MateuUI.create(BaseService.class);
                s.select("Hola!!", new AsyncCallback<Object[][]>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        MateuUI.alert("error!");
                    }

                    @Override
                    public void onSuccess(Object[][] result) {
                        MateuUI.alert("resultado: " + result);
                    }
                });
            }
        });
    }
}
