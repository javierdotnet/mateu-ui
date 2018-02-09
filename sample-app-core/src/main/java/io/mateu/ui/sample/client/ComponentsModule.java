package io.mateu.ui.sample.client;

import com.google.common.collect.Lists;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.components.fields.GridField;
import io.mateu.ui.core.client.components.fields.IntegerField;
import io.mateu.ui.core.client.components.fields.SelectByIdField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.components.fields.grids.columns.*;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
                }, (MenuEntry) new AbstractAction("Multilanguage") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new MultilanguageView());
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
                }, (MenuEntry) new AbstractAction("Styled Grid") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new StyledGridView());
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
        }, (MenuEntry) new AbstractMenu("Trees") {

            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction("Tree") {
                    @Override
                    public void run() {
                        MateuUI.openView(new TreeView());
                    }
                }, (MenuEntry) new AbstractAction("TreeGrid") {
                    @Override
                    public void run() {
                        MateuUI.openView(new TreeGridView());
                    }
                });
            }

        }, (MenuEntry) new AbstractMenu("Dates") {

            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction("Date & datetime") {
                    @Override
                    public void run() {
                        MateuUI.openView(new DateFieldView());
                    }
                }, (MenuEntry) new AbstractAction("Calendar") {
                    @Override
                    public void run() {
                        MateuUI.openView(new CalendarFieldView());
                    }
                });
            }

        }, (MenuEntry) new AbstractMenu("Fares") {

            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction("Fares") {
                    @Override
                    public void run() {
                        MateuUI.openView(new FaresView());
                    }
                });
            }


        }, (MenuEntry) new AbstractMenu("More components") {
            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction("View pdf") {
                    @Override
                    public void run() {
                        try {
                            MateuUI.getClientSideHelper().open(new URI("file:/home/miguel/quonext/tmp/6fbf01b0-bf45-4067-9c4a-9601fee488e9.pdf").toURL());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }, (MenuEntry) new AbstractAction("Complex form") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new ComplexFormView());
                    }
                }, (MenuEntry) new AbstractAction("Nested dialogs") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new NestedDialogsView());
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
                        }.addPage(new BaseWizardPageView("Page 0") {

                            @Override
                            public void build() {
                                add(new TextField("fa1", "Field 1").setHelp("This is the first field bla bla, bla"));
                            }
                        }).addPage(new BaseWizardPageView("First page") {

                            @Override
                            public Data initializeData() {
                                Data data = new Data();
                                List<Data> l = Lists.newArrayList();
                                l.add(new Data("a", "hdwhdehw"));
                                l.add(new Data("a", "uhwihduehd"));
                                l.add(new Data("a", "qwt6qwtw"));
                                data.set("g", l);
                                return data;
                            }

                            @Override
                            public void build() {
                                add(new TextField("f1", "Field 1").setHelp("This is the first field bla bla, bla"));
                                add(new GridField("g", Arrays.asList(
                                        new TextColumn("a", "a", 100, true)
                                        , new IntegerColumn("b", "b", 100, true)
                                        , new DoubleColumn("c", "c", 100, true)
                                        , new CheckBoxColumn("d", "d", 100, true)
                                        , new ComboBoxColumn("cb", "cb", 100, Arrays.asList(
                                                new Pair(1, "1")
                                                , new Pair(2, "2")
                                                , new Pair(3, "3")
                                                , new Pair(4, "4")
                                        ))
                                        , new SqlComboBoxColumn("scb", "scb", 100, "select id, name from currency order by 2")
                                        , new LinkColumn("e", "e", 100) {
                                            @Override
                                            public void run(Data data) {
                                                MateuUI.alert("Hello!");
                                            }
                                        }
                                        , new TextColumn("z", "a", 100, true)
                                )) {
                                    @Override
                                    public AbstractForm getDataForm(Data initialData) {
                                        AbstractForm f = new AbstractForm() {
                                        };
                                        f.add(new TextField("a", "A"));
                                        f.add(new IntegerField("b", "B"));
                                        return f;
                                    }
                                });
                            }
                        }).addPage(new BaseWizardPageView("First page") {

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
                }, (MenuEntry) new AbstractAction("Wizard 2") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().open(new BaseWizard("My first wizard") {

                            BaseWizard w = this;

                            @Override
                            public void onOk(Data data) throws Throwable {
                                System.out.println("data=" + data);
                                //throw new Exception("error xxx");
                            }

                            @Override
                            public void execute(Object action, Data data, Callback<AbstractWizardPageView> callback) throws Throwable {
                                if (action == null) {
                                    set("_gonextaction", "1");
                                    set("_gobackaction", null);
                                    callback.onSuccess(new BaseWizardPageView("Page 0") {

                                        @Override
                                        public AbstractWizard getWizard() {
                                            return w;
                                        }

                                        @Override
                                        public boolean isFirstPage() {
                                            return true;
                                        }

                                        @Override
                                        public boolean isLastPage() {
                                            return false;
                                        }

                                        @Override
                                        public void build() {
                                            add(new TextField("fa1", "Field 1").setHelp("This is the first field bla bla, bla"));
                                        }
                                    });
                                } else {

                                    if (action instanceof Actions) {
                                        Actions a = (Actions) action;
                                        switch (a) {
                                            case GONEXT:
                                                set("_gonextaction", "2");
                                                set("_gobackaction", "1");

                                                Data d = new Data();
                                                List<Data> l = Lists.newArrayList();
                                                l.add(new Data("a", "hdwhdehw"));
                                                l.add(new Data("a", "uhwihduehd"));
                                                l.add(new Data("a", "qwt6qwtw"));
                                                d.set("g_data", l);
                                                setAll(d);

                                                callback.onSuccess(new BaseWizardPageView("First page") {

                                                    @Override
                                                    public AbstractWizard getWizard() {
                                                        return w;
                                                    }


                                                    @Override
                                                    public void build() {
                                                        add(new TextField("f1", "Field 1").setHelp("This is the first field bla bla, bla"));
                                                        add(new GridField("g", Arrays.asList(
                                                                new TextColumn("a", "a", 100, true)
                                                                , new IntegerColumn("b", "b", 100, true)
                                                                , new DoubleColumn("c", "c", 100, true)
                                                                , new CheckBoxColumn("d", "d", 100, true)
                                                                , new ComboBoxColumn("cb", "cb", 100, Arrays.asList(
                                                                        new Pair(1, "1")
                                                                        , new Pair(2, "2")
                                                                        , new Pair(3, "3")
                                                                        , new Pair(4, "4")
                                                                ))
                                                                , new SqlComboBoxColumn("scb", "scb", 100, "select id, name from currency order by 2")
                                                                , new LinkColumn("e", "e", 100) {
                                                                    @Override
                                                                    public void run(Data data) {
                                                                        MateuUI.alert("Hello!");
                                                                    }
                                                                }
                                                                , new TextColumn("z", "a", 100, true)
                                                        )) {
                                                            @Override
                                                            public AbstractForm getDataForm(Data initialData) {
                                                                AbstractForm f = new AbstractForm() {
                                                                };
                                                                f.add(new TextField("a", "A"));
                                                                f.add(new IntegerField("b", "B"));
                                                                return f;
                                                            }
                                                        }.setUsedToSelect(true));
                                                    }
                                                });
                                                break;
                                            case GOBACK:
                                                set("_gonextaction", "1");
                                                set("_gobackaction", null);
                                                callback.onSuccess(new BaseWizardPageView("Page 0") {

                                                    @Override
                                                    public AbstractWizard getWizard() {
                                                        return w;
                                                    }

                                                    @Override
                                                    public boolean isFirstPage() {
                                                        return true;
                                                    }

                                                    @Override
                                                    public boolean isLastPage() {
                                                        return false;
                                                    }

                                                    @Override
                                                    public void build() {
                                                        add(new TextField("fa1", "Field 1").setHelp("This is the first field bla bla, bla"));
                                                    }
                                                });
                                                break;
                                            case END:
                                                close();
                                                break;
                                        }
                                    }

                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
