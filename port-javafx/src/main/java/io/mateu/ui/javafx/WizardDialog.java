package io.mateu.ui.javafx;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.DataViewerField;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.javafx.data.DataStore;
import io.mateu.ui.javafx.views.ViewNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 23/4/17.
 */
public class WizardDialog extends Dialog {
    private final AbstractWizard wizard;
    private final StackPane p;
    private Map<ButtonType, AbstractAction> bts = new HashMap<>();
    private ViewNode currentViewNode;
    private DataStore dataStore;

    public WizardDialog(AbstractWizard wizard) {
        this.wizard = wizard;

        setTitle(wizard.getTitle());
        setResizable(true);

        dataStore = new DataStore(wizard.initializeData());
        wizard.getForm().addDataSetterListener(new DataSetterListener() {
            @Override
            public void setted(Data newData) {
                dataStore.setData(newData);
            }

            @Override
            public void setted(String k, Object v) {
                dataStore.set(k, v);
            }

            @Override
            public void idsResetted() {
                dataStore.resetIds();
            }
        });
        wizard.addListener(new ViewListener() {
            @Override
            public void onClose() {
                close();
            }
        });
        //alert.setHeaderText("Look, an Error Dialog");

        getDialogPane().setContent(p = new StackPane());
        p.setPrefWidth(900);
        p.setPrefHeight(500);

        try {
            wizard.execute(null, dataStore.getData(), new Callback<AbstractWizardPageView>() {
                @Override
                public void onSuccess(AbstractWizardPageView result) {
                    update(result);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

//        ButtonType loginButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
//        getDialogPane().getButtonTypes().addAll(loginButtonType, CANCEL);

    }

    public void update(AbstractWizardPageView view) {

        p.getChildren().clear();
        p.getChildren().add(currentViewNode = new ViewNode(view, dataStore));

        //setHeaderText((view.getTitle() != null)?view.getTitle():"");

        getDialogPane().getButtonTypes().clear();
        bts.clear();
        List<ButtonType> bs = new ArrayList<>();
        for (AbstractAction a : view.getActions()) {
            ButtonType bt;
            bs.add(bt = new ButtonType(a.getName(), ButtonBar.ButtonData.OTHER));
            bts.put(bt, a);
        }
        ButtonType btData = null;
        if (AbstractApplication.PORT_JAVAFX.equals(MateuUI.getApp().getPort())) {
            bs.add(btData = new ButtonType("Data", ButtonBar.ButtonData.OTHER));

            bs.add(ButtonType.CANCEL);
            if (!view.isFirstPage()) bs.add(ButtonType.PREVIOUS);
            if (view.isLastPage()) bs.add(ButtonType.OK);
            else bs.add(ButtonType.NEXT);
        }
        getDialogPane().getButtonTypes().addAll(bs);

        for (ButtonType bt : bts.keySet()) {
            ((Button) getDialogPane().lookupButton(bt)).setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    bts.get(bt).run();
                }
            });
        }

        if (!view.isFirstPage()) ((Button) getDialogPane().lookupButton(ButtonType.PREVIOUS)).addEventFilter(ActionEvent.ACTION, ae -> {
            ae.consume(); //not valid
            try {
                wizard.execute(AbstractWizard.Actions.GOBACK, dataStore.getData(), new Callback<AbstractWizardPageView>() {
                    @Override
                    public void onSuccess(AbstractWizardPageView result) {
                        update(result);
                    }
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        if (view.isLastPage()) ((Button) getDialogPane().lookupButton(ButtonType.OK)).addEventFilter(ActionEvent.ACTION, ae -> {
            ae.consume(); //not valid
            List<String> errors = view.getForm().validate();
            if (errors.size() > 0) {
                MateuUI.notifyErrors(errors);
            } else {
                Data d = dataStore.getData();
                if (MateuUI.getApp().getUserData() != null) d.set("_user", MateuUI.getApp().getUserData().getLogin());
                try {

                    wizard.onOk(d);

                    if (view.getWizard().closeOnOk()) wizard.close();

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    MateuUI.alert("" + throwable.getClass().getName() + ":" + throwable.getMessage());
                }
            }
        });
        else ((Button) getDialogPane().lookupButton(ButtonType.NEXT)).addEventFilter(ActionEvent.ACTION, ae -> {
            ae.consume(); //not valid
            List<String> errors = view.getForm().validate();
            if (errors.size() > 0) {
                MateuUI.notifyErrors(errors);
            } else {
                Data d = dataStore.getData();
                if (MateuUI.getApp().getUserData() != null) d.set("_user", MateuUI.getApp().getUserData().getLogin());
                try {
                    wizard.execute(AbstractWizard.Actions.GONEXT, d, new Callback<AbstractWizardPageView>() {
                        @Override
                        public void onSuccess(AbstractWizardPageView result) {
                            update(result);
                        }
                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });


        if (btData != null) ((Button) getDialogPane().lookupButton(btData)).addEventFilter(ActionEvent.ACTION, ae -> {
            ae.consume(); //not valid
            MateuUI.openView(new AbstractDialog() {
                @Override
                public void onOk(Data data) {

                }

                @Override
                public String getTitle() {
                    return "Data";
                }

                @Override
                public Data initializeData() {
                    Data d = super.initializeData();
                    d.set("data", dataStore.getData());
                    return d;
                }

                @Override
                public void build() {
                    add(new DataViewerField("data"));
                }
            });
        });

        if (currentViewNode.getFirstField() != null) MateuUI.runInUIThread(new Runnable() {
            @Override
            public void run() {
                currentViewNode.getFirstField().requestFocus();
            }
        });



    }
}
