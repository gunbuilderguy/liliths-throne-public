package com.lilithsthrone.game.character.npc.fields;

import java.time.Month;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.lilithsthrone.game.Game;
import com.lilithsthrone.game.character.CharacterImportSetting;
import com.lilithsthrone.game.character.EquipClothingSetting;
import com.lilithsthrone.game.character.GameCharacter;
import com.lilithsthrone.game.character.body.coverings.BodyCoveringType;
import com.lilithsthrone.game.character.body.coverings.Covering;
import com.lilithsthrone.game.character.body.valueEnums.AreolaeSize;
import com.lilithsthrone.game.character.body.valueEnums.AssSize;
import com.lilithsthrone.game.character.body.valueEnums.BodyHair;
import com.lilithsthrone.game.character.body.valueEnums.BodySize;
import com.lilithsthrone.game.character.body.valueEnums.BreastShape;
import com.lilithsthrone.game.character.body.valueEnums.Capacity;
import com.lilithsthrone.game.character.body.valueEnums.CoveringModifier;
import com.lilithsthrone.game.character.body.valueEnums.CoveringPattern;
import com.lilithsthrone.game.character.body.valueEnums.CupSize;
import com.lilithsthrone.game.character.body.valueEnums.HairLength;
import com.lilithsthrone.game.character.body.valueEnums.HairStyle;
import com.lilithsthrone.game.character.body.valueEnums.HipSize;
import com.lilithsthrone.game.character.body.valueEnums.LipSize;
import com.lilithsthrone.game.character.body.valueEnums.Muscle;
import com.lilithsthrone.game.character.body.valueEnums.NippleSize;
import com.lilithsthrone.game.character.body.valueEnums.OrificeElasticity;
import com.lilithsthrone.game.character.body.valueEnums.OrificePlasticity;
import com.lilithsthrone.game.character.body.valueEnums.TongueLength;
import com.lilithsthrone.game.character.body.valueEnums.Wetness;
import com.lilithsthrone.game.character.effects.PerkCategory;
import com.lilithsthrone.game.character.effects.PerkManager;
import com.lilithsthrone.game.character.fetishes.Fetish;
import com.lilithsthrone.game.character.fetishes.FetishDesire;
import com.lilithsthrone.game.character.gender.Gender;
import com.lilithsthrone.game.character.npc.NPC;
import com.lilithsthrone.game.character.persona.NameTriplet;
import com.lilithsthrone.game.character.persona.Occupation;
import com.lilithsthrone.game.character.persona.PersonalityTrait;
import com.lilithsthrone.game.character.persona.SexualOrientation;
import com.lilithsthrone.game.character.race.RaceStage;
import com.lilithsthrone.game.character.race.Subspecies;
import com.lilithsthrone.game.dialogue.DialogueNode;
import com.lilithsthrone.game.inventory.CharacterInventory;
import com.lilithsthrone.game.sex.SexPace;
import com.lilithsthrone.main.Main;
import com.lilithsthrone.utils.Util;
import com.lilithsthrone.utils.Util.Value;
import com.lilithsthrone.utils.colours.PresetColour;
import com.lilithsthrone.world.WorldType;
import com.lilithsthrone.world.places.PlaceType;

/**
 * @since 0.4.15
 * @version 0.4.15
 * @author Innoxia
 */
public class Sally extends NPC {

	public Sally() {
		this(false);
	}

	public Sally(boolean isImported) {
		super(isImported,
				new NameTriplet("Sally"), "Hayward",
				"Having taken over her parents' ranch at a young age, Sally is the sole owner and operator of Sally's Stables, a horse-morph breeding farm out in the Foloi Fields.",
				19, Month.JUNE, 8,
				10, Gender.F_V_B_FEMALE, Subspecies.HUMAN, RaceStage.HUMAN,
				new CharacterInventory(false, 10),
				WorldType.getWorldTypeFromId("innoxia_fields_sallyStables"), PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_reception"),
				true);

		if(!isImported) {
			this.setPlayerKnowsName(false);
		}
	}

	@Override
	public void loadFromXML(Element parentElement, Document doc, CharacterImportSetting... settings) {
		loadNPCVariablesFromXML(this, null, parentElement, doc, settings);
	}

	@Override
	public void setupPerks(boolean autoSelectPerks) {
		PerkManager.initialisePerks(this,
				Util.newArrayListOfValues(),
				Util.newHashMapOfValues(
						new Value<>(PerkCategory.PHYSICAL, 1),
						new Value<>(PerkCategory.LUST, 0),
						new Value<>(PerkCategory.ARCANE, 0)));
	}

	@Override
	public void setStartingBody(boolean setPersona) {
		if(setPersona) {
			this.setPersonalityTraits(
					PersonalityTrait.NAIVE,
					PersonalityTrait.KIND,
					PersonalityTrait.INNOCENT);

			this.setSexualOrientation(SexualOrientation.GYNEPHILIC);

			this.setHistory(Occupation.NPC_BUSINESS_OWNER);

			this.clearFetishes();
			this.clearFetishDesires();

			this.setFetishDesire(Fetish.FETISH_VAGINAL_RECEIVING, FetishDesire.TWO_NEUTRAL);
			this.setFetishDesire(Fetish.FETISH_DOMINANT, FetishDesire.ONE_DISLIKE);
			this.setFetishDesire(Fetish.FETISH_SUBMISSIVE, FetishDesire.ONE_DISLIKE);
			this.setFetishDesire(Fetish.FETISH_SADIST, FetishDesire.ZERO_HATE);
			this.setFetishDesire(Fetish.FETISH_MASOCHIST, FetishDesire.ZERO_HATE);
		}

		// Body:
		this.setHeight(163);
		this.setFemininity(80);
		this.setMuscle(Muscle.TWO_TONED.getMedianValue());
		this.setBodySize(BodySize.ONE_SLENDER.getMedianValue());

		// Coverings - freckled skin, ginger hair, green eyes:
		this.setSkinCovering(new Covering(BodyCoveringType.HUMAN, CoveringPattern.FRECKLED_FACE, CoveringModifier.SMOOTH, PresetColour.SKIN_LIGHT, false, PresetColour.SKIN_LIGHT, false), true);
		this.setEyeCovering(new Covering(BodyCoveringType.EYE_HUMAN, PresetColour.EYE_GREEN));

		this.setHairCovering(new Covering(BodyCoveringType.HAIR_HUMAN, CoveringPattern.NONE, PresetColour.COVERING_GINGER, false, PresetColour.COVERING_GINGER, false), false);
		this.setHairLength(HairLength.FOUR_MID_BACK.getMedianValue());
		this.setHairStyle(HairStyle.BRAIDED);

		this.setHairCovering(new Covering(BodyCoveringType.BODY_HAIR_HUMAN, PresetColour.COVERING_GINGER), false);
		this.setUnderarmHair(BodyHair.ZERO_NONE);
		this.setAssHair(BodyHair.ZERO_NONE);
		this.setPubicHair(BodyHair.TWO_MANICURED);
		this.setFacialHair(BodyHair.ZERO_NONE);

		// Face:
		this.setFaceVirgin(true);
		this.setLipSize(LipSize.TWO_FULL);
		this.setFaceCapacity(Capacity.ZERO_IMPENETRABLE, true);
		this.setTongueLength(TongueLength.ZERO_NORMAL.getMedianValue());

		// Chest:
		this.setNippleVirgin(true);
		this.setBreastSize(CupSize.C.getMeasurement());
		this.setBreastShape(BreastShape.PERKY);
		this.setNippleSize(NippleSize.ONE_SMALL);
		this.setAreolaeSize(AreolaeSize.ONE_SMALL);

		// Ass:
		this.setAssVirgin(true);
		this.setAssBleached(false);
		this.setAssSize(AssSize.THREE_NORMAL);
		this.setHipSize(HipSize.THREE_GIRLY);
		this.setAssCapacity(Capacity.ZERO_IMPENETRABLE, true);
		this.setAssWetness(Wetness.ZERO_DRY);
		this.setAssElasticity(OrificeElasticity.ONE_RIGID.getValue());
		this.setAssPlasticity(OrificePlasticity.THREE_RESILIENT.getValue());

		// Vagina - virgin:
		this.setVaginaVirgin(true);
		this.setVaginaCapacity(Capacity.ZERO_IMPENETRABLE, true);
		this.setVaginaWetness(Wetness.TWO_MOIST);
		this.setVaginaElasticity(OrificeElasticity.ONE_RIGID.getValue());
		this.setVaginaPlasticity(OrificePlasticity.THREE_RESILIENT.getValue());
	}

	@Override
	public void equipClothing(List<EquipClothingSetting> settings) {
		this.unequipAllClothingIntoVoid(true, true);

		this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_groin_panties", PresetColour.CLOTHING_WHITE, false), true, this);
		this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_chest_sports_bra", PresetColour.CLOTHING_WHITE, false), true, this);
		this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_leg_shorts", PresetColour.CLOTHING_BLUE, false), true, this);
		this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_torso_plaid_shirt", PresetColour.CLOTHING_RED, false), true, this);
		this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_foot_boots", PresetColour.CLOTHING_DESATURATED_BROWN, false), true, this);
	}

	@Override
	public boolean isUnique() {
		return true;
	}

	private boolean needsMoving = false;

	@Override
	public void hourlyUpdate(int hour) {
		if(!Main.game.getCharactersPresent().contains(this)) {
			if(hour >= 19 || hour < 5) {
				if(this.getLocationPlaceType() != PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_stall2")) {
					needsMoving = true;
				}
			} else {
				if(this.getLocationPlaceType() != PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_reception")) {
					needsMoving = true;
				}
			}
		}
	}

	@Override
	public void turnUpdate() {
		if(needsMoving && !Main.game.getCharactersPresent().contains(this)) {
			int hour = Main.game.getHourOfDay();
			if(hour >= 19 || hour < 5) {
				this.setLocation(WorldType.getWorldTypeFromId("innoxia_fields_sallyStables"), PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_stall2"), true);
			} else {
				this.setLocation(WorldType.getWorldTypeFromId("innoxia_fields_sallyStables"), PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_reception"), false);
			}
			needsMoving = false;
			Main.game.updateResponses();
		}
	}

	@Override
	public void endSex() {
		this.cleanAllDirtySlots(true);
		this.cleanAllClothing(true, false);
	}

	@Override
	public boolean isAbleToBeImpregnated() {
		return true;
	}

	@Override
	public void changeFurryLevel() {
	}

	@Override
	public DialogueNode getEncounterDialogue() {
		return null;
	}

	@Override
	public SexPace getSexPaceDomPreference() {
		return SexPace.DOM_GENTLE;
	}

	@Override
	public SexPace getSexPaceSubPreference(GameCharacter character) {
		return SexPace.SUB_NORMAL;
	}
}
