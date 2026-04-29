package net.rtxyd.fallen.lib.runtime.forgemod.patch;

import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import net.rtxyd.fallen.lib.api.IFallenPatch;
import net.rtxyd.fallen.lib.api.annotation.FallenPatch;
import net.rtxyd.fallen.lib.api.annotation.Targets;
import net.rtxyd.fallen.lib.runtime.forgemod.patch.inserters.Inserters;
import net.rtxyd.fallen.lib.type.service.IFallenPatchContext;
import net.rtxyd.fallen.lib.type.service.IFallenPatchCtorContext;
import net.rtxyd.fallen.lib.type.service.IPatchDescriptor;
import net.rtxyd.fallen.lib.util.PatchUtil;
import net.rtxyd.fallen.lib.util.patch.InserterKey;
import net.rtxyd.fallen.lib.util.patch.InserterMethodData;
import net.rtxyd.fallen.lib.util.patch.InserterType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

@FallenPatch(
        targets = @Targets(AffixHelper.class),
        inserters = {Inserters.class}
)
public class PatchAffixHelper implements IFallenPatch{
    private final IPatchDescriptor desc;

    public PatchAffixHelper(IFallenPatchCtorContext iFallenPatchCtorContext) {
        desc = iFallenPatchCtorContext.currentPatch();
    }

    @Override
    public void apply(IFallenPatchContext ctx) {
        ClassNode cn = ctx.getClassNode();
        InserterMethodData hookData = ctx.getFallenInserter(
                InserterKey.of("net.rtxyd.fallen.lib.runtime.forgemod.patch.inserters.Inserters",
                        "hookAffixApply",
                        InserterType.BEFORE_MODIFY_ARG));
        if (hookData == null) return;
        for (MethodNode method : cn.methods) {
            InsnList insns = method.instructions;
            // we must ensure the record class is clean, or it could be risky when we create instance.
            if (insns == null) continue;
            if (!(method.name.equals("applyAffix") && method.desc.equals("(Lnet/minecraft/world/item/ItemStack;Ldev/shadowsoffire/apotheosis/adventure/affix/AffixInstance;)V"))) continue;
            // only patches like Map.get(key), or Map.getOrDefault(key),
            // and there are other cases, but will be strictly filtered.
            PatchUtil.insertMethodHook(cn,
                    method,
                    m ->
                            m.getOpcode() == Opcodes.INVOKEVIRTUAL
                            && m instanceof MethodInsnNode mn
                            && mn.owner.endsWith("Map")
                            && mn.name.equals("put"),
                    hookData);
        }
    }
}
