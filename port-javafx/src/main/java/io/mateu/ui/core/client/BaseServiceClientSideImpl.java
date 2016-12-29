package io.mateu.ui.core.client;

import io.mateu.ui.core.app.AsyncCallback;
import io.mateu.ui.core.shared.BaseService;
import io.mateu.ui.core.shared.BaseServiceAsync;

/**
 * Created by miguel on 29/12/16.
 */
public class BaseServiceClientSideImpl implements BaseServiceAsync {
    @Override
    public void sql(String sql, AsyncCallback<String[][]> callback) {

        try {

            String[][] r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).sql(sql);

            callback.onSuccess(r);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}
