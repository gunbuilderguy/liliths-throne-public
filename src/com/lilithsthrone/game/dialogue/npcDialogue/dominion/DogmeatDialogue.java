package com.lilithsthrone.game.dialogue.npcDialogue.dominion;

import java.util.Map;

import com.lilithsthrone.game.character.GameCharacter;
import com.lilithsthrone.game.character.attributes.CorruptionLevel;
import com.lilithsthrone.game.character.fetishes.Fetish;
import com.lilithsthrone.game.character.npc.dominion.Dogmeat;
import com.lilithsthrone.game.character.npc.dominion.Kate;
import com.lilithsthrone.game.character.quests.Quest;
import com.lilithsthrone.game.character.quests.QuestLine;
import com.lilithsthrone.game.inventory.item.ItemType;
import com.lilithsthrone.game.dialogue.DialogueNode;
import com.lilithsthrone.game.dialogue.responses.Response;
import com.lilithsthrone.game.dialogue.responses.ResponseSex;
import com.lilithsthrone.game.dialogue.utils.UtilText;
import com.lilithsthrone.game.sex.SexControl;
import com.lilithsthrone.game.sex.managers.universal.SMGeneric;
import com.lilithsthrone.main.Main;
import com.lilithsthrone.utils.Util;
import com.lilithsthrone.world.AbstractWorldType;
import com.lilithsthrone.world.WorldType;
import com.lilithsthrone.world.places.AbstractPlaceType;
import com.lilithsthrone.world.places.PlaceType;

/**
 * Dialogue for Dogmeat encounters in the alleyways of Dominion.
 *
 * Every submission leads to a mounting sex scene. After the 3rd mounting,
 * Dogmeat shakes off his collar and offers it to the player. The player
 * must then find a tattoo artist to engrave it: their name on the front,
 * "Property of: Dogmeat" on the back.
 *
 * Collar state (dogmeat_collar_state):
 *   0 = no collar event yet
 *   1 = collar given, quest active (needs engraving)
 *   2 = collar engraved and worn
 *
 * @since 0.4.11.3
 * @version 0.4.11.4
 */
public class DogmeatDialogue {

	private static Dogmeat getDogmeat() {
		return (Dogmeat) Main.game.getNpc(Dogmeat.class);
	}

	private static Kate getKate() {
		return (Kate) Main.game.getNpc(Kate.class);
	}

	/** Returns true when Kate is scheduled and currently at Dogmeat's location. */
	private static boolean isKatePresent() {
		Kate kate = getKate();
		Dogmeat dogmeat = getDogmeat();
		if (kate == null || dogmeat == null) {
			return false;
		}
		return Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_schedule_active") == 1
				&& kate.getWorldLocation().equals(dogmeat.getWorldLocation())
				&& kate.getLocationPlaceType().equals(dogmeat.getLocationPlaceType());
	}

	private static long getCollarState() {
		return Math.max(0, Main.game.getDialogueFlags().getSavedLong("dogmeat_collar_state"));
	}

	private static void setCollarState(long state) {
		Main.game.getDialogueFlags().setSavedLong("dogmeat_collar_state", state);
	}

	private static SMGeneric createMountingManager() {
		return new SMGeneric(
				Util.newArrayListOfValues((GameCharacter) getDogmeat()),
				Util.newArrayListOfValues(Main.game.getPlayer()),
				null, null) {
			@Override
			public SexControl getSexControl(GameCharacter character) {
				if (character.isPlayer()) {
					return SexControl.ONGOING_PLUS_LIMITED_PENETRATIONS;
				}
				return super.getSexControl(character);
			}
		};
	}

	// =========================================================================
	// ENCOUNTER (entry point)
	// =========================================================================

	public static final DialogueNode DOGMEAT_ENCOUNTER = new DialogueNode("A stray dog", ".", true) {

		@Override
		public int getSecondsPassed() {
			return 2 * 60;
		}

		@Override
		public void applyPreParsingEffects() {
			Main.game.getDialogueFlags().setSavedLong("dogmeat_found", Main.game.getMinutesPassed());
			// If Kate's hidden tracking enchantment was placed on the collar, activate the schedule.
			if (Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_tracking_active") == 1) {
				Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_schedule_active", 1);
				// Check if the player already removed the tracking enchantment before visiting.
				// If they did, the collar won't have the CLOTHING_TRACKING effect anymore.
				AbstractClothing collar = Main.game.getPlayer().getClothingInSlot(
						com.lilithsthrone.game.inventory.InventorySlot.NECK);
				boolean trackingStillPresent = collar != null
						&& collar.getEffects().stream().anyMatch(
								e -> e.getSecondaryModifier() == com.lilithsthrone.game.inventory.enchanting.TFModifier.CLOTHING_TRACKING);
				if (!trackingStillPresent) {
					// Player removed the enchantment before coming here — mark it as removed.
					Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_tracking_removed", 1);
				}
				Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_tracking_active", 0);
			}
		}

		@Override
		public String getContent() {
			// Player-initiated: player is bringing Kate to meet Dogmeat for the first time
			if (Main.game.getDialogueFlags().getSavedLong("kate_bring_active") == 1) {
				return DOGMEAT_ENCOUNTER_WITH_KATE.getContent();
			}
			// If Kate is here on her daily visit, redirect to the discovery scene
			if (isKatePresent()) {
				return KATE_DOGMEAT_APPROACH.getContent();
			}

			int count = getDogmeat().getPlayerSurrenderCount();
			long collarState = getCollarState();

			// First meeting
			if (count == 0) {
				return "<p>"
						+ "Rounding a corner deeper into the alley, your eye is caught by a large dog sitting in the shadows between two rubbish bins."
						+ " It's a powerfully-built animal &mdash; broad shoulders, tan and black fur matted with the dust of Dominion's streets"
						+ " &mdash; watching you with steady, amber eyes."
						+ "</p>"
						+ "<p>"
						+ "The dog doesn't growl or cower. It simply holds your gaze, tail giving one slow, measured wag."
						+ " A worn collar hangs loose around its neck, the tag too scratched to read."
						+ "</p>";
			}

			// Wearing engraved collar
			if (collarState == 2) {
				return "<p>"
						+ "He's there before you round the corner &mdash; you feel him first."
						+ " A low, pleased rumble carries through the alley, and you look down to see Dogmeat"
						+ " sitting squarely in your path, tail sweeping once across the cobblestones."
						+ "</p>"
						+ "<p>"
						+ "His amber eyes find the collar around your neck immediately."
						+ " He rises and presses his broad muzzle against the leather, inhaling once"
						+ " &mdash; deep, satisfied, entirely possessive."
						+ "</p>"
						+ "<p>"
						+ "His tail begins a slow, deliberate wag."
						+ "</p>";
			}

			// Has un-engraved collar (quest active)
			if (collarState == 1) {
				return "<p>"
						+ "The dog is waiting in his usual spot, amber eyes tracking you as you approach."
						+ " His gaze lingers on your bare neck &mdash; the spot where the collar should be"
						+ " &mdash; and a low, disapproving sound rumbles in his chest."
						+ "</p>"
						+ "<p>"
						+ "You still haven't had it engraved."
						+ "</p>"
						+ "<p>"
						+ "[style.italicsQuestRelationship(Find someone to re-engrave the tag.)]"
						+ "</p>";
			}

			// Returning (count >= 1, no collar yet)
			if (count >= 3) {
				return "<p>"
						+ "He's already facing the entrance when you round the corner,"
						+ " ears forward, watching."
						+ " He rises before you've fully stopped walking, stretching once with languid confidence,"
						+ " then fixes you with those amber eyes."
						+ "</p>"
						+ "<p>"
						+ "His gaze drops to your hips, then returns to your face. He steps closer, nostrils flaring."
						+ "</p>";
			}

			return "<p>"
					+ "The dog is there again, sitting in the same spot between the rubbish bins."
					+ " He spots you before you spot him &mdash; his head turns, ears pricking forward,"
					+ " amber eyes finding yours across the alley."
					+ " His tail begins a slow, deliberate sweep."
					+ "</p>"
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			// Player-initiated Kate introduction
			if (Main.game.getDialogueFlags().getSavedLong("kate_bring_active") == 1) {
				return DOGMEAT_ENCOUNTER_WITH_KATE.getResponse(responseTab, index);
			}
			// If Kate is here, use the discovery scene responses instead
			if (isKatePresent()) {
				return KATE_DOGMEAT_APPROACH.getResponse(responseTab, index);
			}

			int count = getDogmeat().getPlayerSurrenderCount();
			long collarState = getCollarState();

			// Determine post-sex destination.
			// If this is the 3rd mounting (count == 2 before increment), trigger the collar scene.
			DialogueNode postSex;
			if (count == 2 && collarState == 0) {
				postSex = DOGMEAT_COLLAR_SCENE;
			} else {
				postSex = DOGMEAT_AFTER_SEX;
			}

			// --- Index 1: Submit / Kneel -> mounting sex ---
			if (index == 1) {
				String title;
				String tooltip;
				String startContent;

				if (count == 0) {
					title = "Kneel before him";
					tooltip = "Lower yourself before the powerful stray."
							+ " Something about those amber eyes compels you."
							+ "<br/>[style.italicsSex(Kneeling will lead to him mounting you.)]";
					if (!Main.game.getPlayer().hasQuest(QuestLine.SIDE_DOGMEAT_COMPANION)
							&& Main.game.getDialogueFlags().getSavedLong("dogmeat_companion_forfeited") != 1) {
						tooltip += "<br/>[style.italicsMinorBad(Choosing this will close off the option to bring him home.)]";
					}
					startContent = "<p>"
							+ "You slowly lower yourself to your knees. The dog watches you with calm, steady eyes,"
							+ " then steps forward and sniffs along the side of your face &mdash; slow, deliberate, appraising."
							+ "</p>"
							+ "<p>"
							+ "Apparently satisfied with what he finds, he circles behind you."
							+ " His forelegs lock around your hips with sudden, startling certainty."
							+ " There is nothing tentative about it. He mounts you like he owns you."
							+ "</p>";

				} else if (collarState == 2) {
					title = "Present yourself";
					tooltip = "Lower yourself before your master."
							+ " The collar around your neck says everything."
							+ "<br/>[style.italicsSex(He will mount you.)]";
					startContent = "<p>"
							+ "You sink to your knees without hesitation, feeling the weight of the engraved collar"
							+ " against your throat."
							+ "</p>"
							+ "<p>"
							+ "He mounts you with the unhurried confidence of absolute ownership,"
							+ " forelegs locking around your hips as his weight settles over you."
							+ "</p>";

				} else if (count >= 3) {
					title = "Submit";
					tooltip = "Lower yourself before your master."
							+ " You both know what comes next."
							+ "<br/>[style.italicsSex(He will mount you.)]";
					startContent = "<p>"
							+ "You lower yourself. It's automatic now &mdash; knees to the cobblestones, head dipping."
							+ " He's behind you before you've even settled, forelegs catching your hips,"
							+ " broad chest pressing against your back."
							+ " He mounts you with practised certainty."
							+ "</p>";

				} else {
					title = "Kneel before him";
					tooltip = "Lower yourself before the powerful stray again, offering your submission."
							+ "<br/>[style.italicsSex(He will mount you.)]";
					startContent = "<p>"
							+ "You kneel. His ears prick forward &mdash; he remembers this."
							+ " He circles you once, then mounts you from behind without preamble,"
							+ " forelegs locking around your hips with the same blunt authority as before."
							+ "</p>";
				}

				return new ResponseSex(title, tooltip,
						Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE),
						null,
						CorruptionLevel.THREE_DIRTY,
						null, null, null,
						true, false,
						createMountingManager(),
						postSex,
						startContent) {
					@Override
					public void effects() {
						// If companion quest is active and player submits, forfeit the quest.
						if (Main.game.getPlayer().hasQuest(QuestLine.SIDE_DOGMEAT_COMPANION)
								&& Main.game.getPlayer().getQuest(QuestLine.SIDE_DOGMEAT_COMPANION) == Quest.SIDE_DOGMEAT_COMPANION_START) {
							Main.game.getDialogueFlags().setSavedLong("dogmeat_companion_forfeited", 1);
						}
						getDogmeat().incrementPlayerSurrenderCount(1);
					}
				};
			}

			// --- Leave ---
			if (index == 2) {
				return new Response("Leave",
						count >= 3
								? "Walk away &mdash; if he lets you."
								: "Leave the dog and continue through the alley.",
						Main.game.getDefaultDialogue(false)) {
					@Override
					public void effects() {
						getDogmeat().setLocation(WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);
					}
				};
			}

			// --- Push past (count >= 3 only) ---
			if (index == 3 && count >= 3) {
				return new Response("Push past him",
						"Assert yourself. You're not his &mdash; not today."
								+ "<br/>[style.italicsMinorBad(He won't be happy about this.)]",
						DOGMEAT_RESIST) {
					@Override
					public void effects() {
						getDogmeat().setLocation(WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);
					}
				};
			}

			// --- Companion path (count == 0, never submitted, not forfeited) ---
			if (index == 3 && count == 0
					&& Main.game.getDialogueFlags().getSavedLong("dogmeat_companion_forfeited") != 1) {
				boolean questStarted  = Main.game.getPlayer().hasQuest(QuestLine.SIDE_DOGMEAT_COMPANION);
				boolean questActive   = questStarted
						&& Main.game.getPlayer().getQuest(QuestLine.SIDE_DOGMEAT_COMPANION) == Quest.SIDE_DOGMEAT_COMPANION_START;
				boolean questComplete = questStarted
						&& Main.game.getPlayer().getQuest(QuestLine.SIDE_DOGMEAT_COMPANION) == Quest.SIDE_DOGMEAT_COMPANION_COMPLETE;

				if (!questStarted && !questComplete) {
					return new Response("Hold your ground",
							"Don't kneel. You want him on your terms."
									+ " You'll need something to earn his trust first.",
							DOGMEAT_BRING_HOME_PROMPT);
				}

				if (questActive) {
					int have = Main.game.getPlayer().getInventory().getAllItemsInInventory().entrySet().stream()
							.filter(e -> e.getKey().getItemType().getId().equals("innoxia_race_dog_canine_crunch"))
							.mapToInt(Map.Entry::getValue)
							.sum();
					if (have >= 5) {
						return new Response("Offer the treats",
								"You have five Canine Crunches. Set them down and see what he does.",
								DOGMEAT_BRING_HOME_COMPLETE);
					} else {
						return new Response("Offer the treats",
								"You only have " + have + " of 5 Canine Crunches."
										+ " Come back when you have enough.",
								null);
					}
				}
			}

			return null;
		}
	};

	// =========================================================================
	// AFTER SEX (generic post-sex)
	// =========================================================================

	public static final DialogueNode DOGMEAT_AFTER_SEX = new DialogueNode("Aftermath", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			long collarState = getCollarState();

			if (collarState == 2) {
				return "<p>"
						+ "He steps back, panting softly, and nudges the tag hanging from your collar with his nose."
						+ " His tail sweeps the cobblestones in slow, possessive arcs."
						+ "</p>";
			}

			return "<p>"
					+ "The dog steps back, panting softly, tail wagging in slow, satisfied sweeps."
					+ " He regards you with those calm amber eyes."
					+ "</p>"
					+ "<p>"
					+ "He nudges your hand with his broad muzzle, then sits, watching to see what you'll do next."
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Continue",
						"Continue on your way.",
						Main.game.getDefaultDialogue(false)) {
					@Override
					public void effects() {
						getDogmeat().setLocation(WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);
					}
				};
			}
			return null;
		}
	};

	// =========================================================================
	// COLLAR SCENE (after 3rd mounting)
	// =========================================================================

	public static final DialogueNode DOGMEAT_COLLAR_SCENE = new DialogueNode("The collar", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			return "<p>"
					+ "He steps back from you, panting softly, and shakes himself."
					+ " Then, with a sudden, violent wrench of his head, something flies loose &mdash;"
					+ " his collar."
					+ " The worn leather strap skids across the cobblestones and lands at your feet,"
					+ " its metal tag glinting dully in the light."
					+ "</p>"
					+ "<p>"
					+ "He sits. Looks at the collar. Looks at you."
					+ " His nose drops and pushes it deliberately toward your hand."
					+ " Then he sits back and watches, amber eyes steady and expectant."
					+ "</p>"
					+ "<p>"
					+ "The tag is scratched beyond legibility &mdash; it'll need to be re-engraved."
					+ "</p>"
					+ "<p>"
					+ "[style.italicsQuestRelationship(Find someone to re-engrave the tag.)]"
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Pick it up",
						"Take the collar. You know what it means.",
						Main.game.getDefaultDialogue(false)) {
					@Override
					public void effects() {
						setCollarState(1);
						// Only move Dogmeat back to the alley if he's not a companion
						if (!Main.game.getPlayer().getCompanions().contains(getDogmeat())) {
							getDogmeat().setLocation(WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);
						}
					}
				};
			}
			return null;
		}
	};

	// =========================================================================
	// RESIST (pushing past him)
	// =========================================================================

	public static final DialogueNode DOGMEAT_RESIST = new DialogueNode("Pushing back", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			return "<p>"
					+ "You step forward. He shifts to block you."
					+ " You step again &mdash; firmly, with intent &mdash; and press past him."
					+ " He lets out a low, displeased sound, deep in his chest,"
					+ " and for a moment his amber eyes are very sharp."
					+ "</p>"
					+ "<p>"
					+ "But he doesn't stop you."
					+ " He watches you go."
					+ ""
					+ "</p>"
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Leave", "Walk away.", Main.game.getDefaultDialogue(false));
			}
			return null;
		}
	};

	// =========================================================================
	// KATE DISCOVERY SCENES
	// =========================================================================

	/**
	 * Player hears something before rounding the corner.
	 * Gives the option to peek first or walk straight in.
	 */
	public static final DialogueNode KATE_DOGMEAT_APPROACH = new DialogueNode("The back alley", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 2 * 60;
		}

		@Override
		public String getContent() {
			return "<p>"
					+ "You slow down before you round the corner."
					+ "</p>"
					+ "<p>"
					+ "A voice &mdash; low and breathless, cut off by something that isn't quite a gasp."
					+ " Familiar."
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Peek",
						"Angle yourself so you can see without being seen.",
						KATE_DOGMEAT_VOYEUR);
			}
			if (index == 2) {
				return new Response("Walk in",
						"Round the corner. Whatever's happening in there, it's your alley.",
						KATE_DOGMEAT_CAUGHT_ENTRY);
			}
			if (index == 3) {
				return new Response("Leave",
						"Walk away. You don't need to know.",
						Main.game.getDefaultDialogue(false));
			}
			return null;
		}
	};

	/**
	 * Player peeks around the corner. Descriptive voyeur content leads into the sex scene.
	 */
	public static final DialogueNode KATE_DOGMEAT_VOYEUR = new DialogueNode("The back alley", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			boolean kateTold = Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_informed") == 1;
			return "<p>"
					+ "You angle yourself around the corner, staying in the shadow of the wall."
					+ "</p>"
					+ "<p>"
					+ "Kate is there."
					+ " Not in her shop clothes &mdash; she's in considerably less."
					+ " She has her hands braced against the alley wall, her hair loose,"
					+ " and Dogmeat is behind her, forelegs locked around her hips with his"
					+ " usual total, unhurried certainty."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(~Aah!~ &mdash; yeah &mdash; yeah, like &mdash; ~Mmm!~ &mdash; don't stop &mdash;)]")
					+ "</p>"
					+ "<p>"
					+ "She hasn't noticed you."
					+ " Dogmeat has. His amber eyes find you across the alley."
					+ " His tail moves &mdash; once, slow, satisfied &mdash; and he doesn't stop."
					+ "</p>"
					+ (kateTold
						? "<p>You told her where to find him.</p>"
						: "<p>She found him.</p>");
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			Kate kate = getKate();
			if (index == 1) {
				String startContent = "<p>"
						+ "You step around the corner."
						+ " Kate hears you a half-second before she sees you &mdash;"
						+ " her head whips around, flushed and wide-eyed."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(kate, "[npc.speech(~Aah!~ &mdash; oh &mdash; oh, shit &mdash;)]")
						+ "</p>"
						+ "<p>"
						+ "Dogmeat does not stop."
						+ "</p>";
				return new ResponseSex("Join them",
						"Step in. Dogmeat won't object. Kate clearly won't either.",
						Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE),
						null,
						CorruptionLevel.FOUR_LUSTFUL,
						null, null, null,
						true, true,
						new SMGeneric(
								Util.newArrayListOfValues((GameCharacter) getDogmeat()),
								Util.newArrayListOfValues((GameCharacter) kate, Main.game.getPlayer()),
								null, null),
						KATE_DOGMEAT_AFTER_SEX,
						startContent);
			}
			if (index == 2) {
				return new Response("Leave quietly",
						"Back away before she sees you.",
						Main.game.getDefaultDialogue(false));
			}
			return null;
		}
	};

	/**
	 * Player walks straight in. Kate is caught mid-act.
	 */
	public static final DialogueNode KATE_DOGMEAT_CAUGHT_ENTRY = new DialogueNode("The back alley", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			return "<p>"
					+ "You round the corner."
					+ "</p>"
					+ "<p>"
					+ "Kate hears you a half-second before she sees you."
					+ " The noise she makes is not dignified."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(~Aah!~ &mdash; oh &mdash; oh, shit &mdash;)]")
					+ "</p>"
					+ "<p>"
					+ "She cranes her neck around, flushed and dishevelled, hair loose."
					+ "</p>"
					+ "<p>"
					+ "Dogmeat's tail wags."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(...Hi.)]")
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			Kate kate = getKate();
			if (index == 1) {
				return new Response("How'd you...?",
						"Ask Kate how she got here.",
						KATE_DOGMEAT_CAUGHT_HOW);
			}
			if (index == 2) {
				return new Response("I knew it.",
						"",
						KATE_DOGMEAT_CAUGHT_KNEW_IT);
			}
			if (index == 3) {
				String startContent = "<p>"
						+ "You don't say anything. You step forward."
						+ "</p>"
						+ "<p>"
						+ "Kate's expression does something that's mostly relief."
						+ " "
						+ UtilText.parse(kate, "[npc.speech(Oh, thank god.)]")
						+ "</p>"
						+ "<p>"
						+ "Dogmeat does not stop."
						+ "</p>";
				return new ResponseSex("Join them",
						"Say nothing. Just step in.",
						Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE),
						null,
						CorruptionLevel.FOUR_LUSTFUL,
						null, null, null,
						true, true,
						new SMGeneric(
								Util.newArrayListOfValues((GameCharacter) getDogmeat()),
								Util.newArrayListOfValues((GameCharacter) kate, Main.game.getPlayer()),
								null, null),
						KATE_DOGMEAT_AFTER_SEX,
						startContent);
			}
			return null;
		}
	};

	public static final DialogueNode KATE_DOGMEAT_CAUGHT_HOW = new DialogueNode("The back alley", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			boolean kateTold = Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_informed") == 1;
			return "<p>"
					+ (kateTold
						? UtilText.parse(getKate(), "[npc.speech(Y'know, you literally told me where he was.)]")
								+ " She pushes her hair out of her face."
								+ " "
								+ UtilText.parse(getKate(), "[npc.speech(I just... I wanted to come and see him. Obviously.)]")
						: UtilText.parse(getKate(), "[npc.speech(You had dog hair on the collar when you brought it in.)]")
								+ " She sits up slightly and pushes her hair back."
								+ " "
								+ UtilText.parse(getKate(), "[npc.speech(I just... I needed to know. So I put a little something on the edge of it and...)]")
								+ " She gestures at the general situation."
								+ " "
								+ UtilText.parse(getKate(), "[npc.speech(And here I am.)]"))
					+ "</p>"
					+ "<p>"
					+ "She has the grace to look at least somewhat sheepish."
					+ " "
					+ UtilText.parse(getKate(), "[npc.speech(Sorry. Y'know, it's pretty hard for us demons sometimes...)]")
					+ "</p>"
					+ "<p>"
					+ "Dogmeat licks her cheek. She doesn't seem to mind at all."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(He's very good, by the way. Just so you know.)]")
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			Kate kate = getKate();
			if (index == 1) {
				String startContent = "<p>"
						+ "You don't say anything else. You step forward."
						+ "</p>"
						+ "<p>"
						+ "Kate's mouth curves, just slightly."
						+ "</p>";
				return new ResponseSex("Join them",
						"That's enough talking.",
						Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE),
						null,
						CorruptionLevel.FOUR_LUSTFUL,
						null, null, null,
						true, true,
						new SMGeneric(
								Util.newArrayListOfValues((GameCharacter) getDogmeat()),
								Util.newArrayListOfValues((GameCharacter) kate, Main.game.getPlayer()),
								null, null),
						KATE_DOGMEAT_AFTER_SEX,
						startContent);
			}
			if (index == 2) {
				return new Response("Just ask first.",
						"",
						KATE_DOGMEAT_CAUGHT_FINE);
			}
			return null;
		}
	};

	public static final DialogueNode KATE_DOGMEAT_CAUGHT_KNEW_IT = new DialogueNode("The back alley", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			boolean kateTold = Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_informed") == 1;
			return "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(Probably?)]")
					+ " She doesn't sound particularly sorry."
					+ " "
					+ (kateTold
						? UtilText.parse(getKate(), "[npc.speech(But y'know, you literally told me where he was, so.)]")
						: UtilText.parse(getKate(), "[npc.speech(But you were being cagey, and I was curious, and &mdash;)]"))
					+ "</p>"
					+ "<p>"
					+ "She gestures expressively at Dogmeat, at herself, at the general situation."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(Look at him.)]")
					+ "</p>"
					+ "<p>"
					+ "You look at Dogmeat."
					+ "</p>"
					+ "<p>"
					+ "He looks back."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(Exactly,)]")
					+ " Kate says."
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			Kate kate = getKate();
			if (index == 1) {
				String startContent = "<p>"
						+ "You step forward. She doesn't say anything. Neither do you."
						+ "</p>";
				return new ResponseSex("Join them",
						"",
						Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE),
						null,
						CorruptionLevel.FOUR_LUSTFUL,
						null, null, null,
						true, true,
						new SMGeneric(
								Util.newArrayListOfValues((GameCharacter) getDogmeat()),
								Util.newArrayListOfValues((GameCharacter) kate, Main.game.getPlayer()),
								null, null),
						KATE_DOGMEAT_AFTER_SEX,
						startContent);
			}
			if (index == 2) {
				return new Response("Leave.",
						"Walk away.",
						Main.game.getDefaultDialogue(false));
			}
			return null;
		}
	};

	public static final DialogueNode KATE_DOGMEAT_CAUGHT_FINE = new DialogueNode("The back alley", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			return "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(Deal.)]")
					+ "</p>"
					+ "<p>"
					+ "She turns back around."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(Now if you don't mind, I only have, like, forty minutes left of my lunch break.)]")
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			Kate kate = getKate();
			if (index == 1) {
				String startContent = "<p>"
						+ "You step forward. Kate turns back around, spine straightening."
						+ "</p>";
				return new ResponseSex("Join them",
						"",
						Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE),
						null,
						CorruptionLevel.FOUR_LUSTFUL,
						null, null, null,
						true, true,
						new SMGeneric(
								Util.newArrayListOfValues((GameCharacter) getDogmeat()),
								Util.newArrayListOfValues((GameCharacter) kate, Main.game.getPlayer()),
								null, null),
						KATE_DOGMEAT_AFTER_SEX,
						startContent);
			}
			if (index == 2) {
				return new Response("Leave.",
						"Walk away. She's made her feelings clear.",
						Main.game.getDefaultDialogue(false));
			}
			return null;
		}
	};

	/**
	 * After the Kate+Dogmeat(+player) sex scene ends.
	 * Kate always returns to her shop immediately after sex — the hour-13 hourlyUpdate is a fallback
	 * only for days where the player doesn't encounter her during the window.
	 */
	public static final DialogueNode KATE_DOGMEAT_AFTER_SEX = new DialogueNode("The back alley", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			int tier = Kate.getDogmeatRelationshipTier();

			if (tier >= 4) {
				return "<p>"
						+ "Dogmeat steps back, panting softly. Kate doesn't move."
						+ " She reaches for him as he settles beside her &mdash; her hand finds his cock,"
						+ " wraps around it, thumb stroking slowly as he softens."
						+ " She leans down and licks him clean &mdash; thoroughly, unhurriedly."
						+ "</p>"
						+ "<p>"
						+ "When she's done she stays there. Doesn't get up."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(), "[npc.speech(I hate going back to the shop after this.)]")
						+ "</p>";
			}

			if (tier == 3) {
				return "<p>"
						+ "Dogmeat steps back, panting. Kate stays on the ground."
						+ " She reaches for him &mdash; her hand finding his sheath,"
						+ " two fingers working inside in slow circles while he softens."
						+ " She leans over and licks him clean with careful attention."
						+ "</p>"
						+ "<p>"
						+ "She doesn't rush to stand. Instead she pulls him closer,"
						+ " his head settling against her side."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(), "[npc.speech(Five more minutes.)]")
						+ "</p>";
			}

			if (tier == 2) {
				return "<p>"
						+ "Dogmeat steps back, panting, and shakes himself. Kate catches her breath,"
						+ " then reaches for him &mdash; her hand finding his sheath,"
						+ " fingers working in idle circles while he softens."
						+ "</p>"
						+ "<p>"
						+ "She glances at you. Shrugs."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(), "[npc.speech(Same time tomorrow.)]")
						+ "</p>";
			}

			// Tier 1 or 0
			return "<p>"
					+ "Dogmeat steps back, panting, and shakes himself. Kate is still on the ground."
					+ " She doesn't get up immediately."
					+ "</p>"
					+ "<p>"
					+ "Instead she reaches for him as he settles beside her &mdash; one hand finding"
					+ " his sheath, working two fingers inside in slow idle circles while he softens."
					+ " Not pushing anything, just keeping contact."
					+ " She's looking at the far end of the alley while she does it,"
					+ " half her attention somewhere else entirely."
					+ "</p>"
					+ "<p>"
					+ "He breathes out slow. His tail sweeps the cobblestones once."
					+ "</p>"
					+ "<p>"
					+ "At some point she glances at you. Doesn't say anything. Just checks."
					+ "</p>"
					+ "<p>"
					+ "When she finally withdraws her hand she does it unhurriedly, brings her fingers to"
					+ " her mouth and licks them clean with the same lack of ceremony she does everything."
					+ " Then she stands, straightens her jacket, pushes her hair back."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(Same time next week.)]")
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Continue",
						"Continue on your way.",
						Main.game.getDefaultDialogue(false)) {
					@Override
					public void effects() {
						getDogmeat().setLocation(WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);
						getKate().setLocation(WorldType.SHOPPING_ARCADE, PlaceType.SHOPPING_ARCADE_KATES_SHOP, false);
					}
				};
			}
			return null;
		}
	};

	// =========================================================================
	// PLAYER-INITIATED KATE INTRODUCTION
	// =========================================================================

	/**
	 * Player brings Kate to meet Dogmeat for the first time.
	 * Triggers when player visits the alley with kate_bring_active == 1.
	 */
	public static final DialogueNode DOGMEAT_ENCOUNTER_WITH_KATE = new DialogueNode("The back alley", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			return "<p>"
					+ "Dogmeat is in his usual spot. His ears come forward the moment he sees you &mdash;"
					+ " then go flat."
					+ "</p>"
					+ "<p>"
					+ "His gaze moves to Kate. His hackles rise, just slightly. He looks between the two of you"
					+ " with a long, assessing stare, nostrils working."
					+ "</p>"
					+ "<p>"
					+ "Kate does not flinch. She looks back at him steadily. Tilts her chin slightly."
					+ "</p>"
					+ "<p>"
					+ "The two of them seem to reach some kind of arrangement without either of them speaking."
					+ "</p>"
					+ "<p>"
					+ "Dogmeat's hackles settle. His tail moves &mdash; once, slow, measuring."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(He's exactly what I pictured.)]")
					+ " Kate says, to you."
					+ " Then, almost to herself: "
					+ UtilText.parse(getKate(), "[npc.speech(Hm.)]")
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			Kate kate = getKate();
			if (index == 1) {
				String startContent = "<p>"
						+ "You kneel. Kate, after a brief pause, lowers herself beside you."
						+ " Dogmeat looks between you both &mdash; then, apparently deciding the situation is"
						+ " entirely acceptable, steps forward."
						+ "</p>";
				return new ResponseSex("Present",
						"Lower yourself beside Kate. Dogmeat will make his own decisions.",
						Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE),
						null,
						CorruptionLevel.FOUR_LUSTFUL,
						null, null, null,
						true, true,
						new SMGeneric(
								Util.newArrayListOfValues((GameCharacter) getDogmeat()),
								Util.newArrayListOfValues((GameCharacter) kate, Main.game.getPlayer()),
								null, null) {
							@Override
							public SexControl getSexControl(GameCharacter character) {
								if (character.isPlayer() || character == kate) {
									return SexControl.ONGOING_PLUS_LIMITED_PENETRATIONS;
								}
								return super.getSexControl(character);
							}
						},
						DOGMEAT_ENCOUNTER_WITH_KATE_AFTER,
						startContent);
			}
			if (index == 2) {
				return new Response("Leave",
						"This isn't the right moment.",
						Main.game.getDefaultDialogue(false)) {
					@Override
					public void effects() {
						getDogmeat().setLocation(WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);
					}
				};
			}
			return null;
		}
	};

	/**
	 * After the player-initiated threesome with Kate and Dogmeat.
	 * Sets kate_collar_state to 2.
	 */
	public static final DialogueNode DOGMEAT_ENCOUNTER_WITH_KATE_AFTER = new DialogueNode("The back alley", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public void applyPreParsingEffects() {
			Main.game.getDialogueFlags().setSavedLong("kate_collar_state", 2);
			Main.game.getDialogueFlags().setSavedLong("kate_bring_active", 0);
		}

		@Override
		public String getContent() {
			return "<p>"
					+ "Kate is quiet for a moment. Dogmeat settles, panting softly, and she reaches for"
					+ " him before she's even properly caught her breath &mdash; her hand finding his cock,"
					+ " wrapping around it loosely, thumb moving in slow strokes as he softens."
					+ " Not going anywhere. Just keeping her hand there."
					+ "</p>"
					+ "<p>"
					+ "She leans down and licks him clean &mdash; unhurried, thorough,"
					+ " like it's obvious. He stands very still for it."
					+ "</p>"
					+ "<p>"
					+ "When she's done she stays crouched at his level, one hand still resting on him."
					+ " She's looking at the ground. Thinking about something."
					+ "</p>"
					+ "<p>"
					+ "Eventually she stands. Smooths her skirt."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(...He's welcome at the shop.)]")
					+ "</p>"
					;
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Continue",
						"Head back into the city.",
						Main.game.getDefaultDialogue(false)) {
					@Override
					public void effects() {
						getDogmeat().setLocation(WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);
						getKate().setLocation(WorldType.SHOPPING_ARCADE, PlaceType.SHOPPING_ARCADE_KATES_SHOP, false);
					}
				};
			}
			return null;
		}
	};

	// =========================================================================
	// COMPANION PATH — QUEST NODES
	// =========================================================================

	/**
	 * Quest start: player holds their ground rather than kneeling.
	 * Requires count == 0 and the companion quest not yet started.
	 */
	public static final DialogueNode DOGMEAT_BRING_HOME_PROMPT = new DialogueNode("The back alley", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			return "<p>"
					+ "You don't kneel."
					+ "</p>"
					+ "<p>"
					+ "He tilts his head. His amber eyes move over you with that same measuring look,"
					+ " and you hold it &mdash; not challenging, not yielding."
					+ " You reach your hand out flat, the way you'd approach any large animal."
					+ " He steps forward. Sniffs along your fingers, up your wrist, pausing at your pulse point."
					+ " Then he steps back and sits."
					+ "</p>"
					+ "<p>"
					+ "He's waiting to see what comes next."
					+ "</p>"
					+ "<p>"
					+ "[style.italicsQuestSide(Bring five Canine Crunches to the stray in the back alley.)]"
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Leave for now",
						"Come back when you have what you need.",
						Main.game.getDefaultDialogue(false)) {
					@Override
					public void effects() {
						Main.game.getTextEndStringBuilder().append(
								Main.game.getPlayer().startQuest(QuestLine.SIDE_DOGMEAT_COMPANION));
						getDogmeat().setLocation(WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);
					}
				};
			}
			return null;
		}
	};

	/**
	 * Quest completion: player returns with 5 Canine Crunches.
	 * Removes items, adds Dogmeat as companion, prompts room choice.
	 */
	public static final DialogueNode DOGMEAT_BRING_HOME_COMPLETE = new DialogueNode("The back alley", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 10 * 60;
		}

		@Override
		public String getContent() {
			return "<p>"
					+ "You set the treats down one by one on the ground in front of him."
					+ " He watches each one, nose working."
					+ " Then he looks up at you."
					+ "</p>"
					+ "<p>"
					+ "You hold his gaze. You wait."
					+ "</p>"
					+ "<p>"
					+ "He walks to the first one, sniffs it carefully, and sits back down."
					+ " His tail moves once, low and deliberate."
					+ " He looks at the alley entrance. Looks at you."
					+ "</p>"
					+ "<p>"
					+ "You start walking. He falls into step beside you."
					+ "</p>"
					+ "<p>"
					+ "[style.italicsQuestSide(Where will he sleep?)]"
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				// Consume the drinks and add companion first
				return new Response("Take him home",
						"Walk out of the alley together.",
						Main.game.getDefaultDialogue(false)) {
					@Override
					public void effects() {
						Main.game.getPlayer().removeItemByType(
								ItemType.getItemTypeFromId("innoxia_race_dog_canine_crunch"), 5, false);
						Main.game.getPlayer().addCompanion(getDogmeat());
						Main.game.getTextEndStringBuilder().append(
								Main.game.getPlayer().setQuestProgress(
										QuestLine.SIDE_DOGMEAT_COMPANION,
										Quest.SIDE_DOGMEAT_COMPANION_COMPLETE));
					}
				};
			}
			if (index == 2) {
				return new Response("Bedroom", "He can sleep in your room.",
						Main.game.getDefaultDialogue(false)) {
					@Override
					public void effects() {
						getDogmeat().setHomeLocation(
								WorldType.LILAYAS_HOUSE_FIRST_FLOOR,
								PlaceType.LILAYA_HOME_ROOM_PLAYER);
					}
				};
			}
			if (index == 3) {
				return new Response("Kitchen", "He can sleep in the kitchen.",
						Main.game.getDefaultDialogue(false)) {
					@Override
					public void effects() {
						getDogmeat().setHomeLocation(
								WorldType.LILAYAS_HOUSE_GROUND_FLOOR,
								PlaceType.LILAYA_HOME_KITCHEN);
					}
				};
			}
			return null;
		}
	};

	// =========================================================================
	// HOME ENCOUNTER — bedroom/kitchen submission after becoming companion
	// =========================================================================

	public static final DialogueNode DOGMEAT_HOME_ENCOUNTER = new DialogueNode("Your bedroom", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 2 * 60;
		}

		@Override
		public String getContent() {
			int count = getDogmeat().getPlayerSurrenderCount();
			long collarState = getCollarState();

			if (collarState == 2) {
				return "<p>"
						+ "He lifts his head when you come in &mdash; has been watching the door."
						+ " His amber eyes find the collar around your neck immediately,"
						+ " and the slow wag that starts is entirely possessive."
						+ "</p>"
						+ "<p>"
						+ "He rises and presses his muzzle against the leather, inhaling once."
						+ " Satisfied. His property, where it belongs."
						+ "</p>";
			}

			if (collarState == 1) {
				return "<p>"
						+ "He's at the foot of the bed, watching you come in."
						+ " His gaze drops to your bare neck &mdash; the spot where the collar should sit"
						+ " &mdash; and a low, disapproving sound moves through his chest."
						+ "</p>"
						+ "<p>"
						+ "You still haven't had it engraved."
						+ "</p>"
						+ "<p>"
						+ "[style.italicsQuestRelationship(Find someone to re-engrave the tag.)]"
						+ "</p>";
			}

			if (count == 0) {
				return "<p>"
						+ "He's made himself at home."
						+ " Sprawled at the foot of the bed with the calm of an animal who has decided"
						+ " this is his space and you are a welcome addition to it."
						+ " His ears flick toward you as you come in, tail giving one slow, considering sweep."
						+ "</p>"
						+ "<p>"
						+ "He watches you. Amber eyes, unhurried."
						+ "</p>";
			}

			return "<p>"
					+ "His head comes up as you enter &mdash; he was already watching the door."
					+ " He rises, stretches once with easy confidence, and fixes you with those amber eyes."
					+ " His tail begins a slow, deliberate wag."
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			int count = getDogmeat().getPlayerSurrenderCount();
			long collarState = getCollarState();

			DialogueNode postSex;
			if (count == 2 && collarState == 0) {
				postSex = DOGMEAT_COLLAR_SCENE;
			} else {
				postSex = DOGMEAT_HOME_AFTER_SEX;
			}

			if (index == 1) {
				String title;
				String tooltip;
				String startContent;

				if (count == 0) {
					title = "Kneel before him";
					tooltip = "Lower yourself. You're on his territory now."
							+ "<br/>[style.italicsSex(He will mount you.)]";
					startContent = "<p>"
							+ "You sink to your knees. He watches you with steady eyes,"
							+ " then steps forward and sniffs along your face and neck &mdash;"
							+ " slow, deliberate, entirely sure of himself."
							+ "</p>"
							+ "<p>"
							+ "Apparently satisfied, he moves behind you."
							+ " His forelegs settle around your hips with the same unhurried certainty"
							+ " he brings to everything."
							+ " He mounts you like he owns you."
							+ " In here, he might."
							+ "</p>";
				} else if (collarState == 2) {
					title = "Present yourself";
					tooltip = "The collar says what you are. He knows it."
							+ "<br/>[style.italicsSex(He will mount you.)]";
					startContent = "<p>"
							+ "You sink to your knees, the weight of the engraved collar"
							+ " familiar against your throat."
							+ " He's behind you before you've finished settling,"
							+ " forelegs locking around your hips with absolute ease."
							+ "</p>";
				} else {
					title = "Present yourself";
					tooltip = "Lower yourself before him."
							+ "<br/>[style.italicsSex(He will mount you.)]";
					startContent = "<p>"
							+ "You lower yourself to the floor. He crosses to you at once,"
							+ " nose moving along your neck and shoulder, then settling into position"
							+ " with the matter-of-fact efficiency of routine."
							+ "</p>";
				}

				return new ResponseSex(title, tooltip,
						Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE),
						null,
						CorruptionLevel.THREE_DIRTY,
						null, null, null,
						true, false,
						createMountingManager(),
						postSex,
						startContent) {
					@Override
					public void effects() {
						getDogmeat().incrementPlayerSurrenderCount(1);
					}
				};
			}

			if (index == 2) {
				return new Response("Not now",
						"Leave him to it.",
						Main.game.getDefaultDialogue(false));
			}

			return null;
		}
	};

	public static final DialogueNode DOGMEAT_HOME_AFTER_SEX = new DialogueNode("Your bedroom", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			long collarState = getCollarState();

			if (collarState == 2) {
				return "<p>"
						+ "He steps back, panting softly, and nudges the tag on your collar with his nose."
						+ " Satisfied. He settles at the foot of the bed, tail sweeping the floor once."
						+ "</p>";
			}

			return "<p>"
					+ "He steps back, panting softly, tail moving in slow, satisfied arcs."
					+ " He turns a circle on the rug and settles, chin resting on his paws,"
					+ " watching you with those calm amber eyes."
					+ "</p>"
					+ "<p>"
					+ "He looks entirely at home."
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Leave him to rest",
						"Let him settle.",
						Main.game.getDefaultDialogue(false));
			}
			return null;
		}
	};

	// =========================================================================
	// KATE HOME VISIT
	// =========================================================================

	/**
	 * Short scene for when Kate is visiting the player's home in the evening.
	 * Triggered from the bedroom response button when kate_home_visit_active == 1 and Kate is present.
	 */
	public static final DialogueNode KATE_HOME_VISIT = new DialogueNode("Your bedroom", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			int tier = Kate.getDogmeatRelationshipTier();

			if (tier >= 4) {
				return "<p>"
						+ "Kate has brought a bag. Not a large one, but the kind that says"
						+ " she's thought about staying. She's kicked her shoes off by the door"
						+ " and is sitting on the floor next to Dogmeat, her back against the bed,"
						+ " one hand resting on his flank. She's changed into something that isn't her shop clothes."
						+ "</p>"
						+ "<p>"
						+ "Dogmeat has his chin on her thigh. His tail sweeps the rug slowly."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(), "[npc.speech(I was thinking I might stay tonight,)]")
						+ " she says, not quite looking at you."
						+ " "
						+ UtilText.parse(getKate(), "[npc.speech(If that's okay.)]")
						+ "</p>";
			}

			if (tier == 3) {
				return "<p>"
						+ "Kate is on the floor with Dogmeat, cross-legged,"
						+ " her hand moving through his fur in slow, absent strokes."
						+ " He's lying on his side, completely relaxed, amber eyes half-closed."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(), "[npc.speech(He likes it when you get the spot behind his left ear,)]")
						+ " she says, demonstrating."
						+ " His tail thumps the floor twice."
						+ "</p>"
						+ "<p>"
						+ "She glances up at you with genuine warmth."
						+ " "
						+ UtilText.parse(getKate(), "[npc.speech(Thanks for letting me come over.)]")
						+ "</p>";
			}

			if (tier == 2) {
				return "<p>"
						+ "Kate is perched on the edge of your armchair, legs crossed."
						+ " She has a glass of something in one hand and is watching Dogmeat"
						+ " with casual, familiar attention."
						+ "</p>"
						+ "<p>"
						+ "Dogmeat is in his usual spot. He acknowledges her with a slow blink."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(), "[npc.speech(He's calmer here,)]")
						+ " Kate says."
						+ " "
						+ UtilText.parse(getKate(), "[npc.speech(I like it.)]")
						+ "</p>";
			}

			// Tier 0-1
			return "<p>"
					+ "Kate is perched on the edge of your armchair, legs crossed, looking entirely at home."
					+ " She has a glass of something in one hand and is watching Dogmeat with the particular"
					+ " attention of someone who hasn't quite decided whether to be affectionate or analytical."
					+ "</p>"
					+ "<p>"
					+ "Dogmeat, for his part, has not moved from his spot."
					+ " He acknowledges her with a single slow blink and goes back to watching the door."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(He's very different here, you know,)]")
					+ " Kate says, to no one in particular."
					+ " "
					+ UtilText.parse(getKate(), "[npc.speech(Calmer. I wasn't expecting that.)]")
					+ "</p>"
					+ "<p>"
					+ "She glances at you."
					+ " "
					+ UtilText.parse(getKate(), "[npc.speech(Good different.)]")
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			Kate kate = getKate();
			if (index == 1) {
				return new Response("Enjoy the company",
						"Sit with them for a while.",
						Main.game.getDefaultDialogue(false));
			}
			if (index == 2) {
				String startContent = "<p>"
						+ "You kneel. Kate watches you for a moment, then lowers herself beside you."
						+ " Dogmeat rises, stretches, and crosses to the two of you"
						+ " with unhurried certainty."
						+ "</p>";
				return new ResponseSex("Join them",
						"You, Kate, and Dogmeat.",
						Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE),
						null,
						CorruptionLevel.FOUR_LUSTFUL,
						null, null, null,
						true, true,
						new SMGeneric(
								Util.newArrayListOfValues((GameCharacter) getDogmeat()),
								Util.newArrayListOfValues((GameCharacter) kate, Main.game.getPlayer()),
								null, null) {
							@Override
							public SexControl getSexControl(GameCharacter character) {
								if (character.isPlayer() || character == getKate()) {
									return SexControl.ONGOING_PLUS_LIMITED_PENETRATIONS;
								}
								return super.getSexControl(character);
							}
						},
						DOGMEAT_HOME_AFTER_SEX,
						startContent);
			}
			return null;
		}
	};

	// =========================================================================
	// APARTMENT HELPERS
	// =========================================================================

	private static AbstractWorldType getKateApartment() {
		return WorldType.getWorldTypeFromId("innoxia_dominion_kate_apartment");
	}

	private static AbstractPlaceType getKateApartmentBedroom() {
		return PlaceType.getPlaceTypeFromId("innoxia_dominion_kate_apartment_bedroom");
	}

	/** Returns true when both Kate and Dogmeat are at Kate's apartment bedroom. */
	public static boolean isBothAtApartment() {
		Kate kate = getKate();
		Dogmeat dogmeat = getDogmeat();
		if (kate == null || dogmeat == null) {
			return false;
		}
		AbstractWorldType apt = getKateApartment();
		return kate.getWorldLocation().equals(apt) && dogmeat.getWorldLocation().equals(apt);
	}

	// =========================================================================
	// APARTMENT DISCOVERY — player walks in on Kate & Dogmeat at her place
	// =========================================================================

	public static final DialogueNode KATE_APARTMENT_DISCOVERY = new DialogueNode("Kate's Bedroom", ".", true) {

		@Override
		public int getSecondsPassed() {
			return 2 * 60;
		}

		@Override
		public String getContent() {
			int tier = Kate.getDogmeatRelationshipTier();

			if (tier >= 4) {
				return "<p>"
						+ "The bedroom door is ajar. You can hear them before you see them."
						+ "</p>"
						+ "<p>"
						+ "Kate is on the bed, tangled in the sheets she clearly stopped caring about some time ago."
						+ " Dogmeat is behind her with the same unhurried certainty he brings to everything."
						+ " There's a large dog bed in the corner &mdash; clearly new, clearly expensive, clearly slept in."
						+ "</p>"
						+ "<p>"
						+ "She looks over her shoulder when you come in. Doesn't stop. Doesn't flinch."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(), "[npc.speech(Took you long enough.)]")
						+ "</p>";
			}

			if (tier == 3) {
				return "<p>"
						+ "The bedroom door is open. You step through."
						+ "</p>"
						+ "<p>"
						+ "Kate is on the floor beside the bed, cross-legged, with Dogmeat's broad head resting"
						+ " in her lap. Her hand is moving through his fur in slow, idle strokes."
						+ " A folded blanket sits in the corner &mdash; his, clearly."
						+ " The scene is almost domestic until you notice the state of her clothes."
						+ "</p>"
						+ "<p>"
						+ "She looks up at you. No surprise, no embarrassment."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(), "[npc.speech(Hey.)]")
						+ "</p>";
			}

			if (tier == 2) {
				return "<p>"
						+ "You push the bedroom door open."
						+ "</p>"
						+ "<p>"
						+ "Kate glances over her shoulder. Dogmeat is behind her, forelegs locked"
						+ " around her hips with that same unhurried authority."
						+ " She's braced against the side of the bed, hair loose, breathing hard."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(), "[npc.speech(Door was unlocked.)]")
						+ " She doesn't stop. Neither does he."
						+ "</p>";
			}

			// Tier 1 — new & nervous
			return "<p>"
					+ "You push the bedroom door open."
					+ "</p>"
					+ "<p>"
					+ "Kate scrambles. There's a sound that's half-gasp, half-yelp"
					+ " &mdash; hers, not his &mdash; and a flurry of limbs and tangled sheets."
					+ " Dogmeat, for his part, does not scramble. He sits back on his haunches"
					+ " and regards you with calm amber eyes, tail sweeping once across the floor."
					+ "</p>"
					+ "<p>"
					+ "Kate's face is very red."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(This &mdash; I can &mdash; this isn't &mdash;)]")
					+ "</p>"
					+ "<p>"
					+ "She gives up trying to form a sentence."
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			Kate kate = getKate();
			int tier = Kate.getDogmeatRelationshipTier();

			if (index == 1) {
				String startContent;
				if (tier >= 3) {
					startContent = "<p>"
							+ "You step forward. Kate shifts to make room &mdash; not much,"
							+ " but enough. Dogmeat's tail wags once, slow and approving."
							+ "</p>";
				} else {
					startContent = "<p>"
							+ "You step forward. Kate's expression does something complicated"
							+ " that resolves mostly into relief."
							+ "</p>"
							+ "<p>"
							+ "Dogmeat's tail wags."
							+ "</p>";
				}
				return new ResponseSex("Join them",
						tier >= 2
								? "She's not going to stop. Neither should you."
								: "Whatever this is, you're part of it now.",
						Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE),
						null,
						CorruptionLevel.FOUR_LUSTFUL,
						null, null, null,
						true, true,
						new SMGeneric(
								Util.newArrayListOfValues((GameCharacter) getDogmeat()),
								Util.newArrayListOfValues((GameCharacter) kate, Main.game.getPlayer()),
								null, null) {
							@Override
							public SexControl getSexControl(GameCharacter character) {
								if (character.isPlayer() || character == getKate()) {
									return SexControl.ONGOING_PLUS_LIMITED_PENETRATIONS;
								}
								return super.getSexControl(character);
							}
						},
						KATE_APARTMENT_AFTER_SEX,
						startContent);
			}

			if (index == 2) {
				return new Response("Watch",
						"Stay in the doorway. See how this plays out.",
						KATE_APARTMENT_VOYEUR);
			}

			if (index == 3) {
				return new Response("Leave",
						"Back out quietly.",
						Main.game.getDefaultDialogue(false));
			}

			return null;
		}
	};

	// =========================================================================
	// APARTMENT VOYEUR — watching from the doorway
	// =========================================================================

	public static final DialogueNode KATE_APARTMENT_VOYEUR = new DialogueNode("Kate's Bedroom", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			int tier = Kate.getDogmeatRelationshipTier();

			String core = "<p>"
					+ "You lean against the door frame and watch."
					+ "</p>"
					+ "<p>"
					+ "Dogmeat has her pinned with his weight, forelegs locked around her hips."
					+ " Kate's hands are fisted in the sheets, her head down, her breathing"
					+ " coming in sharp, broken sounds that she's not trying to control."
					+ "</p>";

			if (tier >= 3) {
				core += "<p>"
						+ "There's a familiarity to it. She pushes back into him with practised ease,"
						+ " and his pace is steady &mdash; unhurried, thorough, possessive."
						+ " At one point she reaches back and scratches behind his ear without breaking rhythm."
						+ "</p>";
			} else {
				core += "<p>"
						+ "She's louder than you expected. Dogmeat's pace is steady,"
						+ " unhurried, entirely sure of himself."
						+ "</p>";
			}

			core += "<p>"
					+ "His amber eyes find you across the room. His tail moves &mdash; once, slow."
					+ " He doesn't stop."
					+ "</p>";

			return core;
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			Kate kate = getKate();

			if (index == 1) {
				String startContent = "<p>"
						+ "You step into the room. Kate lifts her head, sees you, and doesn't resist"
						+ " when Dogmeat's next thrust pushes her forward."
						+ "</p>";
				return new ResponseSex("Join them",
						"You've seen enough. Time to participate.",
						Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE),
						null,
						CorruptionLevel.FOUR_LUSTFUL,
						null, null, null,
						true, true,
						new SMGeneric(
								Util.newArrayListOfValues((GameCharacter) getDogmeat()),
								Util.newArrayListOfValues((GameCharacter) kate, Main.game.getPlayer()),
								null, null),
						KATE_APARTMENT_AFTER_SEX,
						startContent);
			}

			if (index == 2) {
				return new Response("Leave",
						"Slip away.",
						Main.game.getDefaultDialogue(false));
			}

			return null;
		}
	};

	// =========================================================================
	// APARTMENT AFTER SEX
	// =========================================================================

	public static final DialogueNode KATE_APARTMENT_AFTER_SEX = new DialogueNode("Kate's Bedroom", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			int tier = Kate.getDogmeatRelationshipTier();

			if (tier >= 4) {
				return "<p>"
						+ "Kate doesn't move for a long time. She's lying on her side, Dogmeat's"
						+ " broad flank against her back, one hand resting loosely on his cock"
						+ " as he softens. Her thumb moves in slow, idle circles."
						+ "</p>"
						+ "<p>"
						+ "She leans down and licks him clean &mdash; thoroughly, unhurriedly,"
						+ " as though this is the most natural thing in the world. He holds very still for it."
						+ "</p>"
						+ "<p>"
						+ "When she's done, she stays where she is. Doesn't get up."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(), "[npc.speech(I hate going back to the shop after this.)]")
						+ "</p>";
			}

			if (tier == 3) {
				return "<p>"
						+ "Kate sits up slowly, one hand still on Dogmeat's side."
						+ " She leans over and licks him clean with careful, unhurried attention."
						+ " He breathes out, tail sweeping the floor once."
						+ "</p>"
						+ "<p>"
						+ "She doesn't rush to get dressed. Instead she sits cross-legged on the floor,"
						+ " pulling him closer, his broad head settling in her lap."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(), "[npc.speech(Five more minutes.)]")
						+ "</p>";
			}

			if (tier == 2) {
				return "<p>"
						+ "Dogmeat steps back, panting softly. Kate rolls onto her back on the floor,"
						+ " catching her breath, staring at the ceiling."
						+ "</p>"
						+ "<p>"
						+ "She reaches over and strokes down his side once, absently."
						+ " Then she sits up and starts pulling her clothes on with efficient, practised motions."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(), "[npc.speech(Same time tomorrow.)]")
						+ "</p>";
			}

			// Tier 1
			return "<p>"
					+ "Dogmeat steps back, panting softly, and shakes himself."
					+ " Kate is on the floor, very still, not looking at you."
					+ "</p>"
					+ "<p>"
					+ "She gets dressed quickly. Avoids eye contact. Her hands are shaking slightly"
					+ " &mdash; not from fear, you notice, but from something else entirely."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(...Don't tell anyone about this.)]")
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Continue",
						"Leave Kate's apartment.",
						Main.game.getDefaultDialogue(false)) {
					@Override
					public void effects() {
						// Don't move Kate or Dogmeat — they stay at the apartment until the window expires.
					}
				};
			}
			return null;
		}
	};

	// =========================================================================
	// ASK ABOUT DOGMEAT — conversation at Kate's shop
	// =========================================================================

	public static final DialogueNode KATE_TALK_ABOUT_DOGMEAT = new DialogueNode("Succubi's Secrets", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			int tier = Kate.getDogmeatRelationshipTier();

			if (tier >= 4) {
				return "<p>"
						+ UtilText.parse(getKate(),
								"[npc.speech(I've been thinking about closing early on Tuesdays,)]")
						+ " she says, like it's the most natural thing in the world."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(),
								"[npc.speech(He gets restless if I'm too long. I can tell.)]")
						+ " She looks at you. Zero shame."
						+ " "
						+ UtilText.parse(getKate(), "[npc.speech(Don't.)]")
						+ "</p>"
						+ "<p>"
						+ "She goes back to whatever she was doing."
						+ " After a moment:"
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(),
								"[npc.speech(I bought him a bed. A proper one. Memory foam.)]")
						+ " She says it with the defensive tone of someone who knows"
						+ " exactly how that sounds and doesn't care."
						+ "</p>";
			}

			if (tier == 3) {
				return "<p>"
						+ "Kate actually smiles. It's a real one &mdash; not her usual smirk."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(),
								"[npc.speech(He's really something, you know? He just... he knows.)]")
						+ " She traces something on the counter absently."
						+ " "
						+ UtilText.parse(getKate(),
								"[npc.speech(I got him a blanket. For the bedroom. He likes the spot by the window.)]")
						+ "</p>"
						+ "<p>"
						+ "She catches herself and shrugs."
						+ " "
						+ UtilText.parse(getKate(),
								"[npc.speech(Anyway. He waits at the back door now. Every day, same time."
								+ " I don't even have to go get him anymore.)]")
						+ "</p>";
			}

			if (tier == 2) {
				return "<p>"
						+ UtilText.parse(getKate(), "[npc.speech(He's good.)]")
						+ " She doesn't look up from what she's doing."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(),
								"[npc.speech(Showed up right on time today. Knows the routine now.)]")
						+ " A pause."
						+ " "
						+ UtilText.parse(getKate(),
								"[npc.speech(...He waits at the back door.)]")
						+ "</p>"
						+ "<p>"
						+ "She turns a nail file over in her fingers."
						+ " "
						+ UtilText.parse(getKate(),
								"[npc.speech(It's nice. Having something that's just... simple, y'know?)]")
						+ "</p>";
			}

			if (tier == 1) {
				return "<p>"
						+ UtilText.parse(getKate(),
								"[npc.speech(It's just &mdash; look, it's a thing. Don't make it weird.)]")
						+ " She fidgets with a nail file."
						+ "</p>"
						+ "<p>"
						+ UtilText.parse(getKate(), "[npc.speech(He's... fine. It's fine.)]")
						+ " Her cheeks are slightly flushed."
						+ " "
						+ UtilText.parse(getKate(),
								"[npc.speech(It's a demon thing, okay? We get... urges."
								+ " And he's &mdash; he's very &mdash;)]")
						+ "</p>"
						+ "<p>"
						+ "She waves a hand vaguely and changes the subject."
						+ "</p>";
			}

			// Tier 0 — shouldn't normally reach here, but fallback
			return "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(What about him?)]")
					+ " Kate looks at you blankly."
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Back",
						"Return to the shop.",
						Main.game.getDefaultDialogue(false));
			}
			return null;
		}
	};
}
