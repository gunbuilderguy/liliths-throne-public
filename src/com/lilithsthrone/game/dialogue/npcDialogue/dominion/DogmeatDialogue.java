package com.lilithsthrone.game.dialogue.npcDialogue.dominion;

import com.lilithsthrone.game.character.GameCharacter;
import com.lilithsthrone.game.character.attributes.CorruptionLevel;
import com.lilithsthrone.game.character.fetishes.Fetish;
import com.lilithsthrone.game.character.npc.dominion.Dogmeat;
import com.lilithsthrone.game.character.npc.dominion.Kate;
import com.lilithsthrone.game.dialogue.DialogueNode;
import com.lilithsthrone.game.dialogue.responses.Response;
import com.lilithsthrone.game.dialogue.responses.ResponseSex;
import com.lilithsthrone.game.dialogue.utils.UtilText;
import com.lilithsthrone.game.sex.SexControl;
import com.lilithsthrone.game.sex.managers.universal.SMGeneric;
import com.lilithsthrone.main.Main;
import com.lilithsthrone.utils.Util;
import com.lilithsthrone.game.dialogue.utils.UtilText;
import com.lilithsthrone.world.WorldType;
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
		return Main.game.getDialogueFlags().getSavedLong("dogmeat_collar_state");
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
			// If Kate's hidden tracking enchantment is still on the collar, she now has a fix on his location.
			if (Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_tracking_active") == 1) {
				Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_schedule_active", 1);
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
						+ "The dog doesn't growl or cower. It simply holds your gaze, tail giving one slow, measured wag,"
						+ " as if it has been waiting for precisely <i>you</i> to come along."
						+ " A worn collar hangs loose around its neck, the tag too scratched to read."
						+ "</p>"
						+ "<p>"
						+ "Stray dogs aren't uncommon in Dominion, but something about this one feels different"
						+ " &mdash; calm, intelligent, and utterly unafraid."
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
						+ "He's waiting. That's the impression you get as you round the corner"
						+ " &mdash; he was already watching the entrance of the alley,"
						+ " already knew you'd come."
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
						+ " Satisfied. His property, used as his property should be."
						+ " His tail sweeps the cobblestones in slow, possessive arcs."
						+ "</p>";
			}

			return "<p>"
					+ "The dog steps back, panting softly, tail wagging in slow, satisfied sweeps."
					+ " He regards you with those calm amber eyes &mdash; no judgement, only the quiet warmth"
					+ " of an animal who has gotten exactly what he wanted."
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
					+ "He doesn't want it back."
					+ " He wants <i>you</i> to wear it."
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
						getDogmeat().setLocation(WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);
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
					+ " He watches you go with the patient, unhurried look of something"
					+ " that has all the time in the world."
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
				return new Response("Peek around the corner",
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
					+ "She cranes her neck around, flushed and dishevelled, hair loose, with the expression"
					+ " of someone rapidly evaluating how bad this actually is."
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
				return new Response("How did you find him?",
						"Ask Kate how she got here.",
						KATE_DOGMEAT_CAUGHT_HOW);
			}
			if (index == 2) {
				return new Response("I knew I should have said no.",
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
				return new Response("Fine. Just ask next time.",
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
					+ UtilText.parse(getKate(), "[npc.speech(Same time next week?)]")
					+ " Not quite a question."
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
					+ "Kate does not flinch. She looks back at him with the calm of someone who has met things"
					+ " considerably more alarming than a feral dog morph. She tilts her chin slightly &mdash; not a challenge, just an acknowledgement."
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
				return new ResponseSex("Present yourselves",
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
					+ "<p>"
					+ "She means it."
					+ "</p>";
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
}
