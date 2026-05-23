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
import com.lilithsthrone.game.character.persona.NameTriplet;
import com.lilithsthrone.game.character.persona.PersonalityTrait;
import com.lilithsthrone.game.character.persona.SexualOrientation;
import com.lilithsthrone.game.character.race.RaceStage;
import com.lilithsthrone.game.character.race.Subspecies;
import com.lilithsthrone.game.dialogue.DialogueNode;
import com.lilithsthrone.game.inventory.CharacterInventory;
import com.lilithsthrone.game.sex.SexPace;
import com.lilithsthrone.utils.colours.Colour;
import com.lilithsthrone.utils.colours.PresetColour;
import com.lilithsthrone.world.WorldType;
import com.lilithsthrone.world.places.PlaceType;

/**
 * @since 0.4.15
 * @version 0.4.15
 * @author Innoxia
 */
public class Blaze extends NPC {

	public Blaze() {
		this(false);
	}

	public Blaze(boolean isImported) {
		super(isImported,
				new NameTriplet("Blaze"), "",
				"A spirited chestnut stallion with excellent conformation, Blaze is Sally's top competition prospect.",
				5, Month.FEBRUARY, 20,
				8, Gender.M_P_MALE, Subspecies.HORSE_MORPH, RaceStage.GREATER,
				new CharacterInventory(false, 10),
				WorldType.getWorldTypeFromId("innoxia_fields_sallyStables"), PlaceType.getPlaceTypeFromId("innoxia_fields_sallyStables_stall1"),
				true);

		if(!isImported) {
			this.setPlayerKnowsName(true);
			this.setFeral(Subspecies.HORSE_MORPH);
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
			this.setPersonalityTraits(PersonalityTrait.BRAVE);
			this.setSexualOrientation(SexualOrientation.AMBIPHILIC);
		}

		this.setHeight(195);
		this.setFemininity(0);
		this.setMuscle(Muscle.FOUR_RIPPED.getMedianValue());
		this.setBodySize(BodySize.THREE_LARGE.getMedianValue());
		this.setBreastSize(CupSize.FLAT.getMeasurement());

		this.setSkinCovering(new Covering(BodyCoveringType.HORSE_HAIR, CoveringPattern.NONE, PresetColour.COVERING_GINGER, false, PresetColour.COVERING_GINGER, false), true);

		this.setPenisVirgin(false);
		this.setPenisGirth(PenetrationGirth.FOUR_GIRTHY);
		this.setPenisSize(32);
		this.setTesticleSize(TesticleSize.FOUR_HUGE);
		this.setPenisCumStorage(450);
		this.fillCumToMaxStorage();
	}

	@Override
	public void equipClothing(List<EquipClothingSetting> settings) {
		this.unequipAllClothingIntoVoid(true, true);
	}

	@Override
	public boolean isUnique() {
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
		return SexPace.DOM_ROUGH;
	}
}
