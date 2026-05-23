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
import com.lilithsthrone.main.Main;
import com.lilithsthrone.utils.Util;
import com.lilithsthrone.utils.colours.Colour;
import com.lilithsthrone.utils.colours.PresetColour;
import com.lilithsthrone.world.WorldType;
import com.lilithsthrone.world.places.PlaceType;

/**
 * @since 0.4.15
 * @version 0.4.15
 * @author Innoxia
 */
public class SallyStallion extends NPC {

	public SallyStallion() {
		this(false);
	}

	public SallyStallion(boolean isImported) {
		super(isImported, null, null, "",
				Util.random.nextInt(10)+18, Util.randomItemFrom(Month.values()), 1+Util.random.nextInt(25),
				5+Util.random.nextInt(10),
				Gender.M_P_MALE, Subspecies.HORSE_MORPH, RaceStage.GREATER,
				new CharacterInventory(false, 10), WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);

		if(!isImported) {
			this.setPlayerKnowsName(false);
			this.setGenericName("stallion");

			this.setName(Name.getRandomTriplet(Subspecies.HORSE_MORPH));
			this.setPlayerKnowsName(true);

			setStartingBody(true);
		}
	}

	@Override
	public void loadFromXML(Element parentElement, Document doc, CharacterImportSetting... settings) {
		loadNPCVariablesFromXML(this, null, parentElement, doc, settings);
	}

	@Override
	public void setupPerks(boolean autoSelectPerks) {
	}

	@Override
	public void setStartingBody(boolean setPersona) {
		if(setPersona) {
			this.setPersonalityTraits(
					PersonalityTrait.CONFIDENT);

			this.setSexualOrientation(SexualOrientation.AMBIPHILIC);
		}

		// Strong, muscular stallion body:
		this.setHeight(185 + Util.random.nextInt(20));
		this.setFemininity(5 + Util.random.nextInt(15));
		this.setMuscle(Muscle.FOUR_RIPPED.getMedianValue());
		this.setBodySize(BodySize.THREE_LARGE.getMedianValue());

		this.setBreastSize(CupSize.FLAT.getMeasurement());

		// Randomized coat colour:
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

		// Large penis and testicles for breeding stallion:
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

	/**
	 * @return true if this is one of Sally's 3 permanent stallions (not a boarded guest)
	 */
	public boolean isPermanentStallion() {
		return Main.game.getDialogueFlags().hasFlag("innoxia_sally_stallion_permanent_" + this.getId());
	}
}
