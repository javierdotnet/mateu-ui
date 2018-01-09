package io.mateu.ui.core.client.views;

import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.GridData;
import io.mateu.ui.core.shared.UserData;

public interface RPCView<T, S> {

    public GridData rpc() throws Throwable;

    public Data get(UserData user, Object id) throws Throwable;

}
