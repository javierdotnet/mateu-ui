package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.components.fields.*;
import io.mateu.ui.core.client.views.AbstractView;

/**
 * Created by miguel on 10/8/16.
 */
public class FaresView extends AbstractView {
    @Override
    public String getTitle() {
        return "Fares";
    }

    @Override
    public void build() {
        add(new TextField("_id", "_id"))
                .add(new SupplementOrPositiveField("valor", "Suppl o Valor"))

        ;
    }

}
