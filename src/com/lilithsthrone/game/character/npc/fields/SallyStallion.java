package com.lilithsthrone.game.character.npc.fields;

import java.time.Month;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.lilithsthrone.game.character.CharacterImportSetting;
import com.lilithsthrone.game.character.EquipClothingSetting;
import com.lilithsthrone.game.character.body.coverings.BodyCoveringType;
import com.lilithsthrone.game.character.body.coverings.Covering;
import com.lilithsthrone.game.character.body.valueEnums.BodySize;
import com.lilithsthrone.game.character.body.valueEnums.CoveringPattern;
import com.lilithsthrone.game.character.body.valueEnums.CupSize;
import com.lilithsthrone.game.character.body.valueEnums.Muscle;
import com.lilithsthrone.game.character.body.valueEnums.PenetrationGirth;
import com.lilithsthrone.game.character.body.valueEnums.TesticleSize;
import com.lilithsthrone.game.character.gender.Gender;
import com.lilithsthrone.game.character.npc.NPC;
import com.lilithsthrone.game.character.persona.Name;
import com.lilithsthrone.game.character.persona.PersonalityTrait;
import com.lilithsthrone.game.character.persona.SexualOrientation;
import com.lilithsthrone.game.character.race.RaceStage;
import com.lilithsthrone.game.character.race.Subspecies;
import com.lilithsthrone.game.dialogue.DialogueNode;
import com.lilithsthrone.game.inventory.CharacterInventory;
import com.lilithsthrone.utils.Util;
import com.lilithsthrone.utils.colours.Colour;
import com.lilithsthrone.utils.colours.PresetColour;
import com.lilithsthrone.world.WorldType;
import com.lilithsthrone.world.places.PlaceType;

/**
 * Randomly generated boarded stallion at Sally's Stables.
 * Each has a semen value multiplier (1x-10x) and a difficulty rating
 * that affects how easy they are to work with.
 *
 * @since 0.4.15
 * @version 0.4.15
 * @author Innoxia
 */
public class SallyStallion extends NPC {

	private int semenValueMultiplier = 1;
	private int difficulty = 0;

	public SallyStallion() {
		this(false);
	}

	public SallyStallion(boolean isImported) {
		super(isImported, null, null, "",
				3+Util.random.nextInt(12), Util.randomItemFrom(Month.values()), 1+Util.random.nextInt(25),
				5+Util.random.nextInt(10),
				Gender.M_P_MALE, Subspecies.HORSE_MORPH, RaceStage.GREATER,
				new CharacterInventory(false, 10), WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);

		if(!isImported) {
			this.setPlayerKnowsName(false);
			this.setGenericName("stallion");

			this.setName(Name.getRandomTriplet(Subspecies.HORSE_MORPH));
			this.setPlayerKnowsName(true);

			setStartingBody(true);
			this.setFeral(Subspecies.HORSE_MORPH);
			rollTraits();
		}
	}

	@Override
	public void loadFromXML(Element parentElement, Document doc, CharacterImportSetting... settings) {
		loadNPCVariablesFromXML(this, null, parentElement, doc, settings);
	}

	@Override
	public void setupPerks(boolean autoSelectPerks) {
	}

	private void rollTraits() {
		// Semen value rolled independently: 40% 1x-2x, 30% 3x-5x, 20% 6x-8x, 10% 10x
		int valueRoll = Util.random.nextInt(100);
		if(valueRoll < 40) {
			semenValueMultiplier = 1 + Util.random.nextInt(2);
		} else if(valueRoll < 70) {
			semenValueMultiplier = 3 + Util.random.nextInt(3);
		} else if(valueRoll < 90) {
			semenValueMultiplier = 6 + Util.random.nextInt(3);
		} else {
			semenValueMultiplier = 10;
		}

		// Difficulty rolled independently: 40% docile, 30% spirited, 20% difficult, 10% very difficult
		int diffRoll = Util.random.nextInt(100);
		if(diffRoll < 40) {
			difficulty = 0;
		} else if(diffRoll < 70) {
			difficulty = 1;
		} else if(diffRoll < 90) {
			difficulty = 2;
		} else {
			difficulty = 3;
		}
	}

	public int getSemenValueMultiplier() {
		return semenValueMultiplier;
	}

	/**
	 * 0 = docile, 1 = spirited, 2 = difficult, 3 = very difficult
	 */
	public int getDifficulty() {
		return difficulty;
	}

	public String getDifficultyName() {
		switch(difficulty) {
			case 0: return "docile";
			case 1: return "spirited";
			case 2: return "difficult";
			case 3: return "very difficult";
			default: return "docile";
		}
	}

	public String getValueTierName() {
		if(semenValueMultiplier >= 10) return "prize";
		if(semenValueMultiplier >= 6) return "high-value";
		if(semenValueMultiplier >= 3) return "good";
		return "standard";
	}

	@Override
	public void setStartingBody(boolean setPersona) {
		if(setPersona) {
			this.setPersonalityTraits(PersonalityTrait.CONFIDENT);
			this.setSexualOrientation(SexualOrientation.AMBIPHILIC);
		}

		this.setHeight(185 + Util.random.nextInt(20));
		this.setFemininity(5 + Util.random.nextInt(15));
		this.setMuscle(Muscle.FOUR_RIPPED.getMedianValue());
		this.setBodySize(BodySize.THREE_LARGE.getMedianValue());
		this.setBreastSize(CupSize.FLAT.getMeasurement());

		Colour[] coatColours = {
			PresetColour.COVERING_BROWN,
			PresetColour.COVERING_BROWN_DARK,
			PresetColour.COVERING_BLACK,
			PresetColour.COVERING_WHITE,
			PresetColour.COVERING_TAN,
			PresetColour.COVERING_GREY,
		};
		Colour coat = coatColours[Util.random.nextInt(coatColours.length)];
		this.setSkinCovering(new Covering(BodyCoveringType.HORSE_HAIR, CoveringPattern.NONE, coat, false, coat, false), true);

		this.setPenisVirgin(false);
		this.setPenisGirth(PenetrationGirth.FOUR_GIRTHY);
		this.setPenisSize(25 + Util.random.nextInt(15));
		this.setTesticleSize(TesticleSize.FOUR_HUGE);
		this.setPenisCumStorage(200 + Util.random.nextInt(300));
		this.fillCumToMaxStorage();
	}

	@Override
	public void equipClothing(List<EquipClothingSetting> settings) {
		this.unequipAllClothingIntoVoid(true, true);
	}

	@Override
	public boolean isUnique() {
		return false;
	}

	@Override
	public void changeFurryLevel() {
	}

	@Override
	public DialogueNode getEncounterDialogue() {
		return null;
	}
}
