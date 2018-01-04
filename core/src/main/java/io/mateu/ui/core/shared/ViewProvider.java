package io.mateu.ui.core.shared;

import io.mateu.ui.core.client.views.AbstractView;

import java.io.Serializable;

public interface ViewProvider extends Serializable {

    String getViewName(String viewNameAndParameters);

    AbstractView getView(String viewName);
}