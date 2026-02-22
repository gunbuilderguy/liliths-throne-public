package com.lilithsthrone.game.character.npc.dominion;

import java.time.Month;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.lilithsthrone.game.character.CharacterImportSetting;
import com.lilithsthrone.game.character.EquipClothingSetting;
import com.lilithsthrone.game.character.body.coverings.BodyCoveringType;
import com.lilithsthrone.game.character.body.coverings.Covering;
import com.lilithsthrone.game.character.attributes.Attribute;
import com.lilithsthrone.game.character.body.types.PenisType;
import com.lilithsthrone.game.character.body.valueEnums.BodySize;
import com.lilithsthrone.game.character.body.valueEnums.Muscle;
import com.lilithsthrone.game.character.body.valueEnums.PenetrationGirth;
import com.lilithsthrone.game.character.body.valueEnums.PenetrationModifier;
import com.lilithsthrone.game.character.body.valueEnums.TesticleSize;
import com.lilithsthrone.game.character.effects.PerkCategory;
import com.lilithsthrone.game.character.effects.PerkManager;
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
import com.lilithsthrone.main.Main;
import com.lilithsthrone.utils.Util;
import com.lilithsthrone.utils.Util.Value;
import com.lilithsthrone.utils.colours.PresetColour;
import com.lilithsthrone.world.WorldType;
import com.lilithsthrone.world.places.PlaceType;

/**
 * A generic feral stray dog used as a second participant in Dogmeat's pack encounter.
 *
 * @since 0.4.11.3
 * @version 0.4.11.3
 */
public class FeralStrayDog extends NPC {

	public FeralStrayDog() {
		this(false);
	}

	public FeralStrayDog(boolean isImported) {
		super(isImported,
				new NameTriplet("Stray"),
				"",
				"A large male dog with a rough, wiry coat — clearly another denizen of Dominion's back alleys.",
				4, Month.JANUARY, 1,
				160, Gender.M_P_MALE, Subspecies.DOG_MORPH_GERMAN_SHEPHERD, RaceStage.FERAL,
				new CharacterInventory(false, 0),
				WorldType.EMPTY, PlaceType.GENERIC_HOLDING_CELL, true);
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
						new Value<>(PerkCategory.PHYSICAL, 2),
						new Value<>(PerkCategory.LUST, 0),
						new Value<>(PerkCategory.ARCANE, 0)));
	}

	@Override
	public void setStartingBody(boolean setPersona) {
		if (setPersona) {
			this.setPersonalityTraits(PersonalityTrait.BRAVE);
			this.setSexualOrientation(SexualOrientation.AMBIPHILIC);
			this.setHistory(Occupation.NPC_STRAY_DOG);
		}

		this.setFemininity(10);
		this.setMuscle(Muscle.TWO_TONED.getMedianValue());
		this.setBodySize(BodySize.TWO_AVERAGE.getMedianValue());

		// Mixed grey/brown fur:
		this.setSkinCovering(new Covering(BodyCoveringType.CANINE_FUR, PresetColour.COVERING_GREY), true);
		this.setHairCovering(new Covering(BodyCoveringType.CANINE_FUR, PresetColour.COVERING_BROWN), true);

		this.setPenisType(PenisType.DOG_MORPH);
		this.addPenisModifier(PenetrationModifier.KNOTTED);
		this.addPenisModifier(PenetrationModifier.TAPERED);
		this.setPenisSize(20);
		this.setPenisGirth(PenetrationGirth.TWO_SLIM);
		this.setTesticleSize(TesticleSize.THREE_LARGE);
		this.setInternalTesticles(false);
		this.setPenisCumStorage(40);
		this.fillCumToMaxStorage();
		this.setAttribute(Attribute.VIRILITY, 60);

		this.setFeral(Subspecies.DOG_MORPH_GERMAN_SHEPHERD);
	}

	@Override
	public void equipClothing(List<EquipClothingSetting> settings) {
	}

	@Override
	public boolean isUnique() {
		return true;
	}

	@Override
	public String getSpeechColour() {
		return "#8B7355";
	}

	@Override
	public boolean isAbleToBeImpregnated() {
		return false;
	}

	@Override
	public void changeFurryLevel() {
	}

	@Override
	public void turnUpdate() {
		if (!Main.game.getPlayer().getCompanions().contains(this)) {
			// Keep in holding cell when not actively in an encounter
			if (!this.getWorldLocation().equals(WorldType.EMPTY)) {
				this.setLocation(WorldType.EMPTY, PlaceType.GENERIC_HOLDING_CELL, false);
			}
		}
	}

	@Override
	public DialogueNode getEncounterDialogue() {
		return null;
	}

}
