package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.components.fields.SelectByIdField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.Data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public class ComponentsModule extends AbstractModule {
    @Override
    public String getName() {
        return "Components";
    }

    public List<MenuEntry> getMenu() {
        return Arrays.asList((MenuEntry) new AbstractMenu("Views") {

            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction("EditorView") {
                    @Override
                    public void run() {
                        MateuUI.openView(new EditorView());
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
                }, (MenuEntry) new AbstractAction("Crimes CRUD view") {
                    @Override
                    public void run() {
                        MateuUI.openView(new CRUDCrimesView());
                    }
                }, (MenuEntry) new AbstractAction("AbstractCRUDView 2") {
                    @Override
                    public void run() {
                        MateuUI.openView(new CRUDView2());
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
                            public void build() {
                                        add(new TextField("_id", "_id"))
                                        .add(new TextField("field1", "Label for textfield"))
                                        .add(new TextField("field2", "Label for textfield").setUnmodifiable(true))
                                        ;
                            }
                        });
                    }
                }, (MenuEntry) new AbstractAction("Wizard") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().open(new BaseWizard("My first wizard") {
                            @Override
                            public void onOk(Data data) throws Throwable {
                                System.out.println("data=" + data);
                                //throw new Exception("error xxx");
                            }
                        }.addPage(new BaseWizardPageView("First page") {

                            @Override
                            public void build() {
                                add(new TextField("f1", "Field 1").setHelp("This is the first field bla bla, bla"));
                            }
                        }).addPage(new BaseWizardPageView("Second page") {

                            @Override
                            public void build() {
                                add(new TextField("f2", "Field 2").setRequired(true));
                            }
                        }).addPage(new BaseWizardPageView("Third page") {
                            @Override
                            public void build() {
                                add(new TextField("f3", "Field 3"));
                            }
                        }));
                    }
                });
            }
        });
    }
}
