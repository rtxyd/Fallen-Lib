package net.rtxyd.fallen.lib.type.service;

import net.rtxyd.fallen.lib.util.patch.InserterKey;
import net.rtxyd.fallen.lib.util.patch.InserterMethodData;
import org.apache.commons.lang3.NotImplementedException;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.Set;

public interface IFallenPatchContext {

    ClassNode getClassNode();

    Set<String> currentClassPatchesApplied();

    InserterMethodData getInserterMethodData(InserterKey inserterKey);

    @Deprecated
    default MethodInsnNode getFallenInserter(InserterKey inserterKey) {
        throw new NotImplementedException("Old api implementation is missing!");
    }
}