package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.*;
import io.mateu.ui.core.client.components.fields.grids.CalendarField;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.ViewForm;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;

import java.util.Date;

/**
 * Created by miguel on 3/1/17.
 */
public class AllFieldsView extends AbstractView {

    @Override
    public Data initializeData() {
        return new Data("f9", "link", "f5", new Pair("2", "V2"), "f10", new Pair("2", "V2")
                , "f21", "Hello!", "f23", "http://www.elpais.es"
        , "f24", "<b>This is html!</b>", "f2", new Date(), "f20", "https://blogamericanuestra.files.wordpress.com/2016/04/che-guevara.jpg");
    }

    @Override
    public String getTitle() {
        return "All fields";
    }

    @Override
    public AbstractForm createForm() {
        AbstractForm f = new ViewForm(this);

        f.add(new AutocompleteField("f1", "Autocomplete", "1", "aaaaaa", "2", "asxdd", "3", "bededeud", "4", "bbbb").setRequired(true));
        f.add(new CalendarField("f2", "Calendar"));
        f.add(new CheckBoxField("f3", "CheckBox"));
        f.add(new CheckBoxListField("f4", "CheckBoxList", "1", "V1", "2", "V2"));
        f.add(new ComboBoxField("f5", "ComboBox", "1", "V1", "2", "V2"));
        f.add(new DateField("f6", "Date"));
        f.add(new FileField("f7", "File"));
        f.add(new GridField("f8", "Grid"));
        f.add(new HtmlField("f24", "Html"));
        f.add(new LinkField("f9", "Link") {
            @Override
            public void run() {
                MateuUI.alert("Click on link field!");
            }
        });
        f.add(new RadioButtonField("f10", "RadioButton", "1", "V1", "2", "V2"));
        f.add(new RichTextField("f25", "RichText"));
        f.add(new SearchField("f25", "Search", new CRUDView()));
        f.add(new ShowImageField("f20", "ShowImage"));
        f.add(new ShowTextField("f21", "ShowText"));
        f.add(new SqlAutocompleteField("f12", "SqlAutocomplete", "select id, lastname || ', ' || firstname from customer order by firstname"));
        f.add(new SqlCheckBoxList("f13", "SqlCheckBoxList", "select id, name from currency order by name"));
        f.add(new SqlComboBoxField("f14", "SqlComboBoxField", "select id, lastname || ', ' || firstname from customer order by firstname"));
        f.add(new SqlRadioButtonField("f15", "SqlRadioButtonField", "select id, name from currency order by name"));
        f.add(new TextAreaField("f22", "TextArea"));
        f.add(new DoubleField("f17", "DoubleField"));
        f.add(new IntegerField("f18", "IntegerField"));
        f.add(new TextField("f19", "TextField"));
        f.add(new WebField("f23", "Web"));

        return f;
    }
}
