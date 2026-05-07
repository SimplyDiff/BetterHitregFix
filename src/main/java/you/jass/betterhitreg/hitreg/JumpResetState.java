package you.jass.betterhitreg.hitreg;

public class JumpResetState {
    public long lastJumpTimestamp;
    public boolean wasOnGround;
    public int lastJumpAge;
    public int hurtAge = -9999; // init to avoid false positives on first jump
    public boolean wasOnGroundWhenHit;
}
