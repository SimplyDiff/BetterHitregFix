package you.jass.betterhitreg.hitreg;

import you.jass.betterhitreg.utility.RegQueue;

public class FightState {
    public boolean fighting = false;
    public long fightStartedAt;
    public boolean wasGhosted;
    public boolean newTarget = true;
    public boolean hitByAnother;
    public long lastNonGhost;
    public int fightsThisSession;
    public RegQueue last100Regs = new RegQueue(100);
    public boolean sprintIsReset = true;
    public boolean wasMovingForward;
}
