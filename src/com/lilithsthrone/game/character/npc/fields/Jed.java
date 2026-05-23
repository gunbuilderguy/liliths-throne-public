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
import com.lilithsthrone.game.sex.SexAreaOrifice;
import com.lilithsthrone.game.sex.SexAreaPenetration;
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
public class Jed extends NPC {

	public Jed() {
		this(false);
	}

	public Jed(boolean isImported) {
		super(isImported, new NameTriplet("Jed"), "Hartley",
				"Jed is the head stable hand and receptionist at Sally's Stables. A friendly, hard-working horse-morph who has been with Sally for years.",
				31, Month.AUGUST, 18,
				15,
				Gender.M_P_MALE, Subspecies.HORSE_MORPH, RaceStage.GREATER,
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
		// Persona:
		if(setPersona) {
			this.setPersonalityTraits(
					PersonalityTrait.CONFIDENT,
					PersonalityTrait.KIND);

			this.setSexualOrientation(SexualOrientation.GYNEPHILIC);

			this.setHistory(Occupation.NPC_RECEPTIONIST);

			this.addFetish(Fetish.FETISH_DOMINANT);

			this.setFetishDesire(Fetish.FETISH_ORAL_RECEIVING, FetishDesire.THREE_LIKE);
			this.setFetishDesire(Fetish.FETISH_VAGINAL_GIVING, FetishDesire.THREE_LIKE);
			this.setFetishDesire(Fetish.FETISH_PENIS_GIVING, FetishDesire.THREE_LIKE);

			this.setFetishDesire(Fetish.FETISH_MASOCHIST, FetishDesire.ZERO_HATE);
			this.setFetishDesire(Fetish.FETISH_SADIST, FetishDesire.ZERO_HATE);
		}

		// Body:
		// Core:
		this.setHeight(190);
		this.setFemininity(15);
		this.setMuscle(Muscle.FOUR_RIPPED.getMedianValue());
		this.setBodySize(BodySize.THREE_LARGE.getMedianValue());

		// Coverings:
		this.setEyeCovering(new Covering(BodyCoveringType.EYE_HORSE_MORPH, PresetColour.EYE_HAZEL));
		this.setSkinCovering(new Covering(BodyCoveringType.HORSE_HAIR, CoveringPattern.NONE, CoveringModifier.SHORT, PresetColour.COVERING_BROWN, false, PresetColour.COVERING_WHITE, false), true);

		this.setHairCovering(new Covering(BodyCoveringType.HAIR_HORSE_HAIR, CoveringPattern.NONE, PresetColour.COVERING_BROWN_DARK, false, PresetColour.COVERING_WHITE, false), false);
		this.setHairLength(8);
		this.setHairStyle(HairStyle.NONE);

		this.setHairCovering(new Covering(BodyCoveringType.BODY_HAIR_HORSE_HAIR, PresetColour.COVERING_BROWN_DARK), false);
		this.setUnderarmHair(BodyHair.FOUR_NATURAL);
		this.setAssHair(BodyHair.FOUR_NATURAL);
		this.setPubicHair(BodyHair.FOUR_NATURAL);
		this.setFacialHair(BodyHair.ZERO_NONE);

		// Face:
		this.setFaceVirgin(true);
		this.setLipSize(LipSize.ONE_AVERAGE);
		this.setFaceCapacity(Capacity.ZERO_IMPENETRABLE, true);
		this.setTongueLength(TongueLength.ZERO_NORMAL.getMedianValue());

		// Chest:
		this.setNippleVirgin(true);
		this.setBreastSize(CupSize.FLAT.getMeasurement());
		this.setBreastShape(BreastShape.SIDE_SET);
		this.setNippleSize(NippleSize.ZERO_TINY);
		this.setAreolaeSize(AreolaeSize.ZERO_TINY);

		// Ass:
		this.setAssVirgin(true);
		this.setAssBleached(false);
		this.setAssSize(AssSize.TWO_SMALL);
		this.setHipSize(HipSize.TWO_NARROW);
		this.setAssCapacity(Capacity.ZERO_IMPENETRABLE, true);
		this.setAssWetness(Wetness.ZERO_DRY);
		this.setAssElasticity(OrificeElasticity.ONE_RIGID.getValue());
		this.setAssPlasticity(OrificePlasticity.THREE_RESILIENT.getValue());

		// Penis:
		this.setPenisVirgin(false);
		this.setPenisGirth(PenetrationGirth.FOUR_GIRTHY);
		this.setPenisSize(28);
		this.setTesticleSize(TesticleSize.THREE_LARGE);
		this.setPenisCumStorage(150);
		this.fillCumToMaxStorage();
	}

	@Override
	public void equipClothing(List<EquipClothingSetting> settings) {
		this.unequipAllClothingIntoVoid(true, true);

		if(this.getLocationPlaceType()==PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_bunkhouse")) {
			// Sleeping - minimal clothing
		} else {
			this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_groin_boxers", PresetColour.CLOTHING_GREY, false), true, this);
			this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_leg_jeans", PresetColour.CLOTHING_BLUE, false), true, this);
			this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_torso_plaid_shirt", PresetColour.CLOTHING_GREEN, false), true, this);
			this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_foot_boots", PresetColour.CLOTHING_DESATURATED_BROWN_DARK, false), true, this);
		}
	}

	@Override
	public boolean isUnique() {
		return true;
	}

	private boolean needsMoving = false;

	@Override
	public void hourlyUpdate(int hour) {
		if(!Main.game.getCharactersPresent().contains(this)) {
			if(((hour>=1 && hour<5) && this.getLocationPlaceType()!=PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_bunkhouse"))
					 || (!(hour>=1 && hour<5) && this.getLocationPlaceType()!=PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_reception"))) {
				needsMoving = true;
			}
		}
	}

	@Override
	public void turnUpdate() {
		if(needsMoving && !Main.game.getCharactersPresent().contains(this)) {
			if(Main.game.isHourBetween(1, 5)) {
				this.setLocation(WorldType.getWorldTypeFromId("innoxia_fields_sallyStables"), PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_bunkhouse"), true);
				this.equipClothing();
				Main.game.updateResponses();
			} else {
				this.setLocation(WorldType.getWorldTypeFromId("innoxia_fields_sallyStables"), PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_reception"), false);
				this.equipClothing();
			}
		}
	}

	@Override
	public void endSex() {
		this.cleanAllDirtySlots(true);
		this.cleanAllClothing(true, false);
	}

	@Override
	public boolean isAbleToBeImpregnated() {
		return false;
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
		return SexPace.SUB_EAGER;
	}
}
