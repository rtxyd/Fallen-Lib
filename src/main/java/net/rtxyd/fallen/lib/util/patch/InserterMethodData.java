package net.rtxyd.fallen.lib.util.patch;

import org.objectweb.asm.tree.MethodInsnNode;

import java.util.Collections;
import java.util.Set;

public class InserterMethodData {
    private final MethodInsnNode inserterMethod;
    private final InserterType type;
    private final Params params;
    private final Set<PatchOption> patchOptions;

    public InserterMethodData(MethodInsnNode inserterMethod, InserterType type, Params params, Set<PatchOption> patchOptions) {
        this.inserterMethod = inserterMethod;
        this.type = type;
        this.params = params;
        this.patchOptions = Collections.unmodifiableSet(patchOptions);
    }


    public MethodInsnNode getInserterMethod() {
        return inserterMethod;
    }

    int[] getLoadOuterArgsOrdinal() {
        return params.loadOuterArgsOrdinal;
    }

    int getModifyArgOrdinal() {
        return params.modifyArgOrdinal;
    }

    public Set<PatchOption> getPatchOptions() {
        return patchOptions;
    }

    public InserterType getType() {
        return type;
    }

    public static class Params {
        private final int[] loadOuterArgsOrdinal;
        private final int modifyArgOrdinal;

        public Params(int[] loadOuterArgsOrdinal, int modifyArgOrdinal) {
            this.loadOuterArgsOrdinal = loadOuterArgsOrdinal;
            this.modifyArgOrdinal = modifyArgOrdinal;
        }

        public static Params createDefault() {
            return new Params(new int[0], -1);
        }
    }
}
