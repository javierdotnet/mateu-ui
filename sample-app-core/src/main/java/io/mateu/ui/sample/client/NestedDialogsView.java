package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.GridField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.LinkColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.TextColumn;
import io.mateu.ui.core.client.views.AbstractDialog;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.shared.Data;

import java.util.List;

/**
 * Created by miguel on 10/8/16.
 */
public class NestedDialogsView extends AbstractView {
    @Override
    public String getTitle() {
        return "Nested dialogs";
    }

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> as = super.createActions();

        as.add(new AbstractAction("Open dialog") {
            @Override
            public void run() {


                MateuUI.openView(new AbstractDialog() {
                    @Override
                    public void onOk(Data data) {

                    }

                    @Override
                    public String getTitle() {
                        return "Nivel 1";
                    }

                    @Override
                    public void build() {
                        add(new TextField("f1", "F1"));
                    }

                    @Override
                    public List<AbstractAction> createActions() {
                        List<AbstractAction> as = super.createActions();

                        AbstractDialog d1 = this;

                        as.add(new AbstractAction("Open dialog") {
                            @Override
                            public void run() {

                                MateuUI.openView(new AbstractDialog() {

                                    @Override
                                    public Data initializeData() {
                                        return d1.getData();
                                    }

                                    @Override
                                    public void onOk(Data data) {
                                        d1.setAll(data);
                                    }

                                    @Override
                                    public String getTitle() {
                                        return "Nivel 2";
                                    }

                                    @Override
                                    public void build() {
                                        add(new TextField("f2", "F2"));
                                    }

                                    @Override
                                    public List<AbstractAction> createActions() {
                                        List<AbstractAction> as = super.createActions();

                                        AbstractDialog d2 = this;

                                        as.add(new AbstractAction("Open dialog") {
                                            @Override
                                            public void run() {


                                                MateuUI.openView(new AbstractDialog() {

                                                    @Override
                                                    public Data initializeData() {
                                                        return d2.getData();
                                                    }

                                                    @Override
                                                    public void onOk(Data data) {
                                                        d2.setAll(data);
                                                    }

                                                    @Override
                                                    public String getTitle() {
                                                        return "Nivel 3";
                                                    }

                                                    @Override
                                                    public void build() {
                                                        add(new TextField("f3", "F3"));
                                                    }

                                                    @Override
                                                    public List<AbstractAction> createActions() {
                                                        return super.createActions();
                                                    }
                                                });

                                            }
                                        });

                                        return as;
                                    }

                                });

                            }
                        });

                        return as;
                    }
                });

            }
        });

        return as;
    }


    @Override
    public Data initializeData() {
        return new Data("f0", "AAA", "f2", "BBB");
    }


    @Override
    public void build() {
        add(new TextField("f0", "Name"));
    }
}
