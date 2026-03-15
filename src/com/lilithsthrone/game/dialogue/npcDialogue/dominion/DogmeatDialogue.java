package com.lilithsthrone.game.dialogue.npcDialogue.dominion;

import com.lilithsthrone.game.character.GameCharacter;
import com.lilithsthrone.game.character.attributes.CorruptionLevel;
import com.lilithsthrone.game.character.fetishes.Fetish;
import com.lilithsthrone.game.character.npc.dominion.Dogmeat;
import com.lilithsthrone.game.dialogue.DialogueNode;
import com.lilithsthrone.game.dialogue.responses.Response;
import com.lilithsthrone.game.dialogue.responses.ResponseSex;
import com.lilithsthrone.game.sex.SexControl;
import com.lilithsthrone.game.sex.managers.universal.SMGeneric;
import com.lilithsthrone.main.Main;
import com.lilithsthrone.utils.Util;
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
		return Main.game.getNpc(Dogmeat.class);
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
		}

		@Override
		public String getContent() {
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
						+ " His tag. His name. His property."
						+ " He rises and presses his broad muzzle against the leather, inhaling once"
						+ " &mdash; deep, satisfied, entirely possessive."
						+ "</p>"
						+ "<p>"
						+ "His tail begins to wag. Slow. Utterly certain."
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
						+ "You still haven't had it engraved. He knows."
						+ "</p>"
						+ "<p>"
						+ "[style.italicsQuest(Take the collar to Kate at Succubi's Secrets in the Shopping Arcade"
						+ " to have it re-engraved.)]"
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
						+ " He is not here to go for a walk."
						+ "</p>";
			}

			return "<p>"
					+ "The dog is there again, sitting in the same spot between the rubbish bins."
					+ " He spots you before you spot him &mdash; his head turns, ears pricking forward,"
					+ " amber eyes finding yours across the alley."
					+ " His tail begins a slow, deliberate sweep."
					+ "</p>"
					+ "<p>"
					+ "He remembers you."
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
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
							+ " against your throat. His. The tag says so. You say so."
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
					+ "You understand. He doesn't want it back."
					+ " He wants <i>you</i> to wear it."
					+ "</p>"
					+ "<p>"
					+ "The tag is scratched beyond legibility &mdash; it'll need to be re-engraved."
					+ " Your name on the front. And on the back..."
					+ "</p>"
					+ "<p>"
					+ "[style.italicsQuest(Find a tattoo artist to engrave the collar:"
					+ " your name on the front, 'Property of: Dogmeat' on the back.)]"
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
					+ " Whatever this cost you in his estimation, he's already decided you'll be back."
					+ "</p>"
					+ "<p>"
					+ "He's probably right."
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
}
