package asm.n1luik.KAllFix.asm;

import asm.n1luik.K_multi_threading.asm.JavaAgent.AgentAPI;
import asm.n1luik.K_multi_threading.asm.all.AgentAPI2Forge;

public class KAllFixAsmForge extends AgentAPI2Forge {
    public KAllFixAsmForge() {
        super(new KAllFixAsm());
    }
}
