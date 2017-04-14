package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.components.fields.SelectByIdField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.views.AbstractDialog;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.ViewForm;
import io.mateu.ui.core.shared.Data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public class ComponentsModule extends AbstractModule {
    public List<MenuEntry> getMenu() {
        return Arrays.asList((MenuEntry) new AbstractMenu("App") {
            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction("AbstractApplication") {
                    @Override
                    public void run() {

                    }
                }, (MenuEntry) new AbstractAction("AbstractArea") {
                    @Override
                    public void run() {

                    }
                }, (MenuEntry) new AbstractAction("AbstractModule") {
                    @Override
                    public void run() {

                    }
                });
            }
        }, (MenuEntry) new AbstractMenu("Views") {

            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction("AbstractView") {
                     @Override
                    public void run() {

                    }
                }, (MenuEntry) new AbstractAction("AbstractListView") {
                    @Override
                    public void run() {
                        MateuUI.openView(new ListView());
                    }
                }, (MenuEntry) new AbstractAction("AbstractCRUDView") {
                    @Override
                    public void run() {
                        MateuUI.openView(new CRUDView());
                    }
                }, (MenuEntry) new AbstractAction("Currencies CRUD view") {
                    @Override
                    public void run() {
                        MateuUI.openView(new CRUDViewForCurrencies());
                    }
                }, (MenuEntry) new AbstractAction("AbstractCRUDView 2") {
                    @Override
                    public void run() {
                        MateuUI.openView(new CRUDView2());
                    }
                }, (MenuEntry) new AbstractAction("AbstractForm") {
                    @Override
                    public void run() {

                    }
                });
            }
        }, (MenuEntry) new AbstractMenu("Components") {

            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction("All fields") {
                    @Override
                    public void run() {
                        MateuUI.openView(new AllFieldsView());
                    }
                }, (MenuEntry) new AbstractAction("TextField") {
                     @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new TextFieldView());
                    }
                }, (MenuEntry) new AbstractAction("ComboBoxField") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new ComboBoxFieldView());
                    }
                }, (MenuEntry) new AbstractAction("ListSelectionField") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new ListSelectionFieldView());
                    }
                }, (MenuEntry) new AbstractAction("Property listener") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new PropertyListenerView());
                    }
                }, (MenuEntry) new AbstractAction("Search field") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new SearchFieldView());
                    }
                }, (MenuEntry) new AbstractAction("Data viewer") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new DataViewerView());
                    }
                }, (MenuEntry) new AbstractAction("Grid") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new GridView());
                    }
                }, (MenuEntry) new AbstractAction("Tabs") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new TabsView());
                    }
                }, (MenuEntry) new AbstractAction("File field") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new FileFieldView());
                    }
                });
            }
        }, (MenuEntry) new AbstractMenu("More components") {
            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction("Complex form") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new ComplexFormView());
                    }
                }, (MenuEntry) new AbstractAction("Dialog") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new AbstractDialog() {
                            @Override
                            public void onOk(Data data) {
                                MateuUI.alert("ok!!!");
                            }

                            @Override
                            public String getTitle() {
                                return "Test dialog";
                            }

                            @Override
                            public AbstractForm createForm() {
                                return new ViewForm(this)
                                        .add(new TextField("_id", "_id"))
                                        .add(new TextField("field1", "Label for textfield"))
                                        .add(new TextField("field2", "Label for textfield").setUnmodifiable(true))
                                        ;
                            }
                        });
                    }
                });
            }
        });
    }
}
