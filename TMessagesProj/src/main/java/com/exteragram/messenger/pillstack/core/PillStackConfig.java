package com.exteragram.messenger.pillstack.core;

import java.util.ArrayList;

public final class PillStackConfig {
    private PillStackConfig() {
    }

    public static ArrayList<Integer> getActivePills() {
        app.kiwigram.pillstack.PillStackConfig.loadConfig(false);
        return app.kiwigram.pillstack.PillStackConfig.getActivePills();
    }

    public static ArrayList<Integer> getHiddenPills() {
        app.kiwigram.pillstack.PillStackConfig.loadConfig(false);
        return app.kiwigram.pillstack.PillStackConfig.getHiddenPills();
    }

    public static int getLastActivePillId() {
        app.kiwigram.pillstack.PillStackConfig.loadConfig(false);
        return app.kiwigram.pillstack.PillStackConfig.lastActivePillId.Int();
    }
}
