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
public class Thunder extends NPC implements StallionTraitsProvider {

	private static final List<StallionTrait> TRAITS = Util.newArrayListOfValues(
			StallionTrait.DOCILE, StallionTrait.HEAVY_HUNG, StallionTrait.SLOW_BURN);

	@Override
	public List<StallionTrait> getStallionTraits() {
		return TRAITS;
	}

	public Thunder() {
		this(false);
	}

	public Thunder(boolean isImported) {
		super(isImported,
				new NameTriplet("Thunder"), "",
				"Sally's largest and oldest stallion, Thunder is a huge bay with a gentle temperament.",
				8, Month.APRIL, 12,
				5, Gender.M_P_MALE, Subspecies.HORSE_MORPH, RaceStage.GREATER,
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
			this.setPersonalityTraits(PersonalityTrait.KIND);
			this.setSexualOrientation(SexualOrientation.AMBIPHILIC);
		}

		this.setHeight(200);
		this.setFemininity(0);
		this.setMuscle(Muscle.FOUR_RIPPED.getMedianValue());
		this.setBodySize(BodySize.FOUR_HUGE.getMedianValue());
		this.setBreastSize(CupSize.FLAT.getMeasurement());

		this.setSkinCovering(new Covering(BodyCoveringType.HORSE_HAIR, CoveringPattern.NONE, PresetColour.COVERING_BROWN, false, PresetColour.COVERING_BROWN, false), true);

		this.setPenisVirgin(false);
		this.setPenisGirth(PenetrationGirth.FOUR_GIRTHY);
		this.setPenisSize(35);
		this.setTesticleSize(TesticleSize.FOUR_HUGE);
		this.setPenisCumStorage(500);
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
		return SexPace.DOM_GENTLE;
	}
}
