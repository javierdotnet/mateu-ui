package io.mateu.ui.core.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 4/1/17.
 */
public class PairList {

    private List<Pair> values = new ArrayList<>();

    public PairList(PairList l) {
        values.addAll(l.getValues());
    }

    public PairList() {

    }


    public List<Pair> getValues() {
        return values;
    }

    public void setValues(List<Pair> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        boolean primero = true;
        for (Pair p : getValues()) {
            if (primero) primero = false;
            else sb.append(", ");
            sb.append(p);
        }
        sb.append("]");
        return sb.toString();
    }
}
