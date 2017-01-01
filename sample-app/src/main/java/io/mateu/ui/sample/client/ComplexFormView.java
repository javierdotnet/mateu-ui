package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.GridField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.components.fields.grids.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.LinkColumn;
import io.mateu.ui.core.client.components.fields.grids.TextColumn;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.ViewForm;

import java.util.List;

/**
 * Created by miguel on 10/8/16.
 */
public class ComplexFormView extends AbstractView {
    @Override
    public String getTitle() {
        return "Complex form";
    }

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> as = super.createActions();

        as.add(new AbstractAction() {
            @Override
            public String getName() {
                return "Test action";
            }

            @Override
            public void run() {
                System.out.println(getForm().getData());
                MateuUI.alert("Hola!!!");
            }
        });

        return as;
    }


    @Override
    public Data initializeData() {
        return new Data("f1", "AAA", "f2", "BBB");
    }

    @Override
    public AbstractForm createForm() {
        return new ViewForm(this)
                .setLastFieldMaximized(true)
                .add(new TextField("f1", "Name"))
                .add(new TextField("f2", "Adress"))
                .add(new TextField("f3", "Country"))
                .add(new TextField("f1", "Name repeated"))
                .add(new GridField("g1", "Hobbies", new AbstractColumn[]{
                        new TextColumn("col1", "Col1", 100, true)
                        , new TextColumn("col2", "Col2", 100, true)
                        , new TextColumn("col2", "Col2", 100, true)
                        , new LinkColumn("col2", "Col2", 100) {
                    @Override
                    public void run(Data data) {
                        MateuUI.alert("Hola!!!");
                    }
                }
                        , new TextColumn("col2", "Col2", 100, true)
                        , new TextColumn("col2", "Col2", 100, true)
                }))
                ;
    }
}
