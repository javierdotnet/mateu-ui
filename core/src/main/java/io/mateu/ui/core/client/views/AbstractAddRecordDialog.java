package io.mateu.ui.core.client.views;

import io.mateu.ui.core.shared.Data;

public abstract class AbstractAddRecordDialog extends AbstractDialog {

    public abstract void addAndClean(Data data);

    @Override
    public void onOk(Data data) {

    }
}
