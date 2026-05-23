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
import com.lilithsthrone.game.character.body.valueEnums.PenetrationGirth;
import com.lilithsthrone.game.character.body.valueEnums.TesticleSize;
import com.lilithsthrone.game.character.body.valueEnums.TongueLength;
import com.lilithsthrone.game.character.body.valueEnums.Wetness;
import com.lilithsthrone.game.character.effects.Perk;
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
				"The owner and operator of Sally's Stables, this experienced horse-morph has been breeding and raising stallions for most of her life. She's tough, practical, and deeply knowledgeable about her charges.",
				42, Month.MARCH, 15,
				20, Gender.F_V_B_FEMALE, Subspecies.HORSE_MORPH, RaceStage.GREATER,
				new CharacterInventory(false, 10),
				WorldType.getWorldTypeFromId("innoxia_fields_sallyStables"), PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_office"),
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
		// Persona:
		if(setPersona) {
			this.setPersonalityTraits(
					PersonalityTrait.CONFIDENT,
					PersonalityTrait.KIND,
					PersonalityTrait.BRAVE);

			this.setSexualOrientation(SexualOrientation.AMBIPHILIC);

			this.setHistory(Occupation.NPC_BUSINESS_OWNER);

			this.addFetish(Fetish.FETISH_DOMINANT);
			this.addFetish(Fetish.FETISH_PENIS_RECEIVING);

			this.setFetishDesire(Fetish.FETISH_ORAL_GIVING, FetishDesire.THREE_LIKE);
			this.setFetishDesire(Fetish.FETISH_SIZE_QUEEN, FetishDesire.THREE_LIKE);

			this.setFetishDesire(Fetish.FETISH_MASOCHIST, FetishDesire.ZERO_HATE);
		}

		// Body:
		// Core:
		this.setHeight(180);
		this.setFemininity(70);
		this.setMuscle(Muscle.THREE_MUSCULAR.getMedianValue());
		this.setBodySize(BodySize.TWO_AVERAGE.getMedianValue());

		// Coverings:
		this.setEyeCovering(new Covering(BodyCoveringType.EYE_HORSE_MORPH, PresetColour.EYE_BROWN));
		this.setSkinCovering(new Covering(BodyCoveringType.HORSE_HAIR, CoveringPattern.NONE, CoveringModifier.SHORT, PresetColour.COVERING_BROWN, false, PresetColour.COVERING_WHITE, false), true);

		this.setHairCovering(new Covering(BodyCoveringType.HAIR_HORSE_HAIR, CoveringPattern.NONE, PresetColour.COVERING_BROWN_DARK, false, PresetColour.COVERING_WHITE, false), false);
		this.setHairLength(HairLength.THREE_SHOULDER_LENGTH.getMedianValue());
		this.setHairStyle(HairStyle.PONYTAIL);

		this.setHairCovering(new Covering(BodyCoveringType.BODY_HAIR_HORSE_HAIR, PresetColour.COVERING_BROWN_DARK), false);
		this.setUnderarmHair(BodyHair.TWO_MANICURED);
		this.setAssHair(BodyHair.ZERO_NONE);
		this.setPubicHair(BodyHair.TWO_MANICURED);
		this.setFacialHair(BodyHair.ZERO_NONE);

		// Face:
		this.setFaceVirgin(false);
		this.setLipSize(LipSize.TWO_FULL);
		this.setFaceCapacity(Capacity.THREE_SLIGHTLY_LOOSE, true);
		this.setTongueLength(TongueLength.ZERO_NORMAL.getMedianValue());

		// Chest:
		this.setNippleVirgin(true);
		this.setBreastSize(CupSize.D.getMeasurement());
		this.setBreastShape(BreastShape.ROUND);
		this.setNippleSize(NippleSize.TWO_BIG);
		this.setAreolaeSize(AreolaeSize.TWO_BIG);

		// Ass:
		this.setAssVirgin(true);
		this.setAssBleached(false);
		this.setAssSize(AssSize.THREE_NORMAL);
		this.setHipSize(HipSize.THREE_GIRLY);
		this.setAssCapacity(Capacity.ONE_EXTREMELY_TIGHT, true);
		this.setAssWetness(Wetness.ZERO_DRY);
		this.setAssElasticity(OrificeElasticity.ONE_RIGID.getValue());
		this.setAssPlasticity(OrificePlasticity.THREE_RESILIENT.getValue());

		// Vagina:
		this.setVaginaVirgin(false);
		this.setVaginaCapacity(Capacity.THREE_SLIGHTLY_LOOSE, true);
		this.setVaginaWetness(Wetness.THREE_WET);
		this.setVaginaElasticity(OrificeElasticity.THREE_FLEXIBLE.getValue());
		this.setVaginaPlasticity(OrificePlasticity.THREE_RESILIENT.getValue());
	}

	@Override
	public void equipClothing(List<EquipClothingSetting> settings) {
		this.unequipAllClothingIntoVoid(true, true);

		this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_groin_panties", PresetColour.CLOTHING_WHITE, false), true, this);
		this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_chest_sports_bra", PresetColour.CLOTHING_WHITE, false), true, this);
		this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_leg_jeans", PresetColour.CLOTHING_BLUE, false), true, this);
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
				// After 7pm, Sally moves to the breeding stalls (stall2) with stallions
				if(this.getLocationPlaceType() != PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_stall2")) {
					needsMoving = true;
				}
			} else if(hour >= 9 && hour < 17) {
				// During the day, Sally is in her office
				if(this.getLocationPlaceType() != PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_office")) {
					needsMoving = true;
				}
			} else {
				// Early morning / evening transition - she's in the corridor
				if(this.getLocationPlaceType() != PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_corridor")) {
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
			} else if(hour >= 9 && hour < 17) {
				this.setLocation(WorldType.getWorldTypeFromId("innoxia_fields_sallyStables"), PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_office"), false);
			} else {
				this.setLocation(WorldType.getWorldTypeFromId("innoxia_fields_sallyStables"), PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_corridor"), false);
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
		return SexPace.DOM_NORMAL;
	}

	@Override
	public SexPace getSexPaceSubPreference(GameCharacter character) {
		return SexPace.SUB_EAGER;
	}
}
