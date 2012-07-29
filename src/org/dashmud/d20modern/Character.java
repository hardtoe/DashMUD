package org.dashmud.d20modern;

public class Character extends Creature {
	private CharacterClass characterClass;
	
	@Override
	public int getBaseAttackBonus() {
		return characterClass.getBaseAttackBonus(getLevel());
	}

	@Override
	public int getFortBaseSave() {
		return characterClass.getBaseFortSaveBonus(getLevel());
	}

	@Override
	public int getRefBaseSave() {
		return characterClass.getBaseRefSaveBonus(getLevel());
	}

	@Override
	public int getWillBaseSave() {
		return characterClass.getBaseWillSaveBonus(getLevel());
	}

	@Override
	public int getReputationBonus() {
		return characterClass.getReputationBonus(getLevel());
	}

	@Override
	public int getDefenseBonus() {
		return characterClass.getDefenseBonus(getLevel());
	}

	@Override
	public int getNumAttacks() {
		return characterClass.getNumAttacks(getLevel());
	}

	@Override
	public int getSpeed() {
		return 30;
	}
}
