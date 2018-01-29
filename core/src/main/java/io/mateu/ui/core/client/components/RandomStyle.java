package io.mateu.ui.core.client.components;

import java.util.ArrayList;
import java.util.List;

public class RandomStyle {

    private static List<String> csss;

    static {

        csss = new ArrayList<>();

        for (int i = 0; i < 20; i++) csss.add("random-" + i);

    }


    public static List<String> getCsss() {
        return csss;
    }
}
