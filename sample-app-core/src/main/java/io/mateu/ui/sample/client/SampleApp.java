package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.components.Tab;
import io.mateu.ui.core.client.components.Tabs;
import io.mateu.ui.core.client.components.fields.IntegerField;
import io.mateu.ui.core.client.components.fields.PKField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.BaseEditorView;
import io.mateu.ui.core.shared.Data;

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

    public List<AbstractArea> getAreas() {
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
}
