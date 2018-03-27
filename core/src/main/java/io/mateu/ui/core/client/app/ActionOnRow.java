package io.mateu.ui.core.client.app;

import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 30/12/16.
 */
public interface ActionOnRow {

    boolean isModifierPressed();

    void run(Data data);
}
