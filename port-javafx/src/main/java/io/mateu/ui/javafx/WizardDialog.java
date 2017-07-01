package io.mateu.ui.javafx;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.DataViewerField;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.javafx.data.DataStore;
import io.mateu.ui.javafx.views.ViewNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

        //alert.setHeaderText("Look, an Error Dialog");

        getDialogPane().setContent(p = new StackPane());
        p.setPrefWidth(600);
        p.setPrefHeight(400);

        try {
            update(wizard.execute(null, null));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

//        ButtonType loginButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
//        getDialogPane().getButtonTypes().addAll(loginButtonType, CANCEL);

    }

    public void update(AbstractWizardPageView view) {

        view.setInitialData(dataStore.getData());

        p.getChildren().clear();
        p.getChildren().add(currentViewNode = new ViewNode(view));

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
                dataStore.setData(currentViewNode.getDataStore().getData());
                update(wizard.execute(AbstractWizard.Actions.GOBACK, currentViewNode.getDataStore().getData()));
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
                Data d = view.getForm().getData();
                if (MateuUI.getApp().getUserData() != null) d.set("_user", MateuUI.getApp().getUserData().getLogin());
                try {
                    dataStore.setData(d);
                    update(wizard.execute(AbstractWizard.Actions.END, currentViewNode.getDataStore().getData()));
                    close();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        else ((Button) getDialogPane().lookupButton(ButtonType.NEXT)).addEventFilter(ActionEvent.ACTION, ae -> {
            ae.consume(); //not valid
            List<String> errors = view.getForm().validate();
            if (errors.size() > 0) {
                MateuUI.notifyErrors(errors);
            } else {
                Data d = view.getForm().getData();
                if (MateuUI.getApp().getUserData() != null) d.set("_user", MateuUI.getApp().getUserData().getLogin());
                try {
                    dataStore.setData(d);
                    update(wizard.execute(AbstractWizard.Actions.GONEXT, currentViewNode.getDataStore().getData()));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });


        if (btData != null) ((Button) getDialogPane().lookupButton(btData)).addEventFilter(ActionEvent.ACTION, ae -> {
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
                    d.set("data", view.getForm().getData());
                    return d;
                }

                @Override
                public void build() {
                    add(new DataViewerField("data"));
                }
            });
        });


    }
}
