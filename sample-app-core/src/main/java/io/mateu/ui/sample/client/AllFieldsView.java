package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.*;
import io.mateu.ui.core.client.components.fields.grids.columns.TextColumn;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;

import java.util.Arrays;
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
    public void build() {
        add(new AutocompleteField("f1", "Autocomplete", "1", "aaaaaa", "2", "asxdd", "3", "bededeud", "4", "bbbb").setRequired(true));
        add(new CalendarField("f2", "Calendar", null).setBeginingOfLine(true));
        add(new WeekDaysField("fwd", "Week days"));
        add(new CheckBoxField("f3", "CheckBox"));
        add(new CheckBoxListField("f4", "CheckBoxList", "1", "V1", "2", "V2"));
        add(new ComboBoxField("f5", "ComboBox", "1", "V1", "2", "V2"));
        add(new DateTimeField("f31", "DateTime"));
        add(new DateField("f6", "Date"));
        add(new FileField("f7", "File"));
        add(new GridField("f8", "Grid", Arrays.asList(new TextColumn("colx", "X", 150, false))));
        add(new HtmlField("f24", "Html"));
        add(new LinkField("f9", "Link") {
            @Override
            public void run() {
                MateuUI.alert("Click on link field!");
            }
        });
        add(new RadioButtonField("f10", "RadioButton", "1", "V1", "2", "V2"));
        add(new RichTextField("f25", "RichText"));
        add(new SearchField("f25", "Search", new CRUDView()));
        add(new ShowImageField("f20", "ShowImage"));
        add(new ShowTextField("f21", "ShowText"));
        add(new SqlAutocompleteField("f12", "SqlAutocomplete", "select id, lastname || ', ' || firstname from customer order by firstname"));
        add(new SqlCheckBoxList("f13", "SqlCheckBoxList", "select id, name from currency order by name"));
        add(new SqlComboBoxField("f14", "SqlComboBoxField", "select id, lastname || ', ' || firstname from customer order by firstname"));
        add(new SqlRadioButtonField("f15", "SqlRadioButtonField", "select id, name from currency order by name"));
        add(new TextAreaField("f22", "TextArea"));
        add(new DoubleField("f17", "DoubleField"));
        add(new IntegerField("f18", "IntegerField"));
        add(new TextField("f19", "TextField"));
        add(new WebField("f23", "Web"));
    }
}
