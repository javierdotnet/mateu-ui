package io.mateu.ui.core.shared;

/**
 * Created by miguel on 11/4/17.
 */
public interface CellStyleGenerator {

    String getStyle(Object value);

    boolean isContentShown();

}
