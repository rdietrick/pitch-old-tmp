package com.pitchplayer.server.game;

public enum GameType {
	
	SINGLES(1), DOUBLES(2), SIM_SINGLES(3), SIM_DOUBLES(4), AUTO_START(5);
	
	private int typeNum;
	GameType(int n) {
		this.typeNum = n;
	}
	public byte getDbFlag() {
		if (this.typeNum == AUTO_START.typeNum) {
			return (byte)SINGLES.typeNum;
		}
		return (byte)this.typeNum;
	}
	public String toCommandValue() {
		return String.valueOf(this.typeNum);
	}
	
	public static GameType fromDbFlag(byte dbFlag) {
		switch (dbFlag) {
		case 1:
			return SINGLES;
		case 2:
			return DOUBLES;
		case 3:
			return SIM_SINGLES;
		case 4:
			return SIM_DOUBLES;
		case 5:
			return AUTO_START;
		default:
			throw new RuntimeException("Unknown gameType flag:" + dbFlag);
		}
	}
}
