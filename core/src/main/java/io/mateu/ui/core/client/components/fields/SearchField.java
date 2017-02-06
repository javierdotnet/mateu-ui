package io.mateu.ui.core.client.components.fields;

import io.mateu.ui.core.client.views.AbstractCRUDView;

/**
 * Created by miguel on 3/1/17.
 */
public class SearchField extends AbstractField<SearchField> {
    private AbstractCRUDView crud;

    public SearchField(String id) {
        super(id);
    }

    public SearchField(String id, String label) {
        super(id, label);
    }

    public SearchField(String id, String label, AbstractCRUDView crud) {
        super(id, label); this.crud = crud;
    }


    public AbstractCRUDView getCrud() {
        return crud;
    }

    public SearchField setCrud(AbstractCRUDView crud) {
        this.crud = crud;
        return this;
    }
}
