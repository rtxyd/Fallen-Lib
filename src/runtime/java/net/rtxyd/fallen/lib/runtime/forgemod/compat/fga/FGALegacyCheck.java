package net.rtxyd.fallen.lib.runtime.forgemod.compat.fga;

public class FGALegacyCheck {
    boolean oldVersion;
    boolean hasLib;
    private FGAVersionStage stage;

    public FGALegacyCheck(boolean hasLib, boolean oldVersion) {
        this.hasLib = hasLib;
        this.oldVersion = oldVersion;
    }

    private FGAVersionStage parseStage() {
        if (!hasLib) {
            return FGAVersionStage.FL_ONE_TWO;
        } else {
            if (oldVersion) {
                return FGAVersionStage.FL_ONE_THREE_TWO;
            } else {
                return FGAVersionStage.FL_ONE_THREE_THREE;
            }
        }
    }

    public FGAVersionStage getStage() {
        if (stage == null) {
            stage = parseStage();
        }
        return stage;
    }
}
