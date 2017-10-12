package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.BaseServiceAsync;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.BaseService;
import io.mateu.ui.sample.shared.TestService;
import io.mateu.ui.sample.client.TestServiceAsync;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 27/12/16.
 */
public class CommunicationModule extends AbstractModule {

//    public void x(LocalDate d) {
//        System.out.println(d);
//    }

    @Override
    public String getName() {
        return "Communication";
    }

    @Override
    public List<MenuEntry> getMenu() {


        //TestDate.x();

        //x(LocalDate.now());

        return Arrays.asList((MenuEntry) new AbstractAction("echo") {
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
        }, (MenuEntry) new AbstractAction("sql") {
            @Override
            public void run() {
                BaseServiceAsync s = MateuUI.create(BaseService.class);
                s.select("select * from customer", new Callback<Object[][]>() {
                    @Override
                    public void onSuccess(Object[][] result) {
                        MateuUI.alert("resultado: " + result);
                    }
                });
            }
        }, (MenuEntry) new AbstractAction("test dates") {
            @Override
            public void run() {
                //MateuUI.alert("resultado: " + LocalDate.now());
            }
        });
    }
}
