package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.components.Tab;
import io.mateu.ui.core.client.components.Tabs;
import io.mateu.ui.core.client.components.fields.IntegerField;
import io.mateu.ui.core.client.components.fields.PKField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.BaseEditorView;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class SampleApp extends AbstractApplication
{

    public String getName() {
        return "Sample Application";
    }

    public List<AbstractArea> buildAreas() {
        return Arrays.asList(new AboutArea(), new ComponentsArea(), new AdminArea(), new CommunicationArea());
    }

    @Override
    public AbstractView getPublicHome() {
        return new BaseEditorView() {

/*            @Override
            public Data initializeData() {
                Data data = super.initializeData();
                data.set("name", "hola!!!");
                return data;
            }*/

            @Override
            public String getServerSideControllerKey() {
                return "currencycrud";
            }

            @Override
            public String getTitle() {
                return "Currency";
            }

            @Override
            public void build() {
                Tabs ts = new Tabs("z");
                ts.add(new Tab(getForm(), "Info").add(new PKField("id", "Code"))
                        .add(new TextField("name", "Name").setBeginingOfLine(true))
                        .add(new IntegerField("decimals", "Decimals").setBeginingOfLine(true)));
                ts.add(new Tab(getForm(), "Config").add(new TextField("name", "Name").setBeginingOfLine(true))
                        .add(new IntegerField("decimals", "Decimals").setBeginingOfLine(true)));
                add(ts);
            }

            @Override
            public List<AbstractAction> createActions() {
                List<AbstractAction> l = super.createActions();

                l.add(new AbstractAction("test") {
                    @Override
                    public void run() {
                        getForm().setData(new Data("name", "adiós!!!"));
                    }
                });

                return l;
            }
        };
    }

    @Override
    public AbstractView getPrivateHome() {
        return new TextFieldView();
    }


    @Override
    public boolean isFavouritesAvailable() {
        return true;
    }

    @Override
    public void getFavourites(UserData user, Callback<Data> callback) {
        Data data = new Data();

        {
            List<Data> l = new ArrayList<>();
            l.add(new Data("id", 1, "url", "#!mui/io.mateu.ui.sample.client.TextFieldView", "name", "Campo texto"));
            l.add(new Data("id", 2, "url", "#!components/components/mui/io.mateu.ui.sample.client.TabsView", "name", "Pestañas"));
            l.add(new Data("id", 3, "url", "#!components/components/mui/io.mateu.ui.sample.client.FileFieldView", "name", "Campo upload"));
            data.set("links", l);
        }

        List<Data> g = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<Data> l = new ArrayList<>();
            l.add(new Data("id", 1, "url", "#!mui/io.mateu.ui.sample.client.TextFieldView", "name", "Campo texto"));
            l.add(new Data("id", 2, "url", "#!components/components/mui/io.mateu.ui.sample.client.TabsView", "name", "Pestañas"));
            l.add(new Data("id", 3, "url", "#!components/components/mui/io.mateu.ui.sample.client.FileFieldView", "name", "Campo upload"));
            g.add(new Data("links", l, "id", i, "name", "Grupo " + i));

        }
        data.set("groups", g);

        callback.onSuccess(data);
    }

    @Override
    public boolean isLastEditedAvailable() {
        return true;
    }

    @Override
    public void getLastEdited(UserData user, Callback<Data> callback) {
        Data data = new Data();

        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/M HH:mm:ss");

        List<Data> g = new ArrayList<>();
        for (int i = 0; i < 400; i++) {
            g.add(new Data("id", i, "url", "#!components/components/mui/io.mateu.ui.sample.client.FileFieldView", "name", "Campo upload " + i, "when", LocalDateTime.now().format(f), "icon", "edit"));
        }
        data.set("records", g);

        callback.onSuccess(data);
    }
}
