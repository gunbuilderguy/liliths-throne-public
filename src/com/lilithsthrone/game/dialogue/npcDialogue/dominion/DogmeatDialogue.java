package com.lilithsthrone.game.dialogue.npcDialogue.dominion;

import com.lilithsthrone.game.character.npc.dominion.Dogmeat;
import com.lilithsthrone.game.dialogue.DialogueNode;
import com.lilithsthrone.game.dialogue.responses.Response;
import com.lilithsthrone.main.Main;
import com.lilithsthrone.world.WorldType;
import com.lilithsthrone.world.places.PlaceType;

/**
 * Dialogue for discovering and adopting Dogmeat in the alleyways of Dominion.
 *
 * @since 0.4.11.3
 * @version 0.4.11.3
 */
public class DogmeatDialogue {

	public static final DialogueNode DOGMEAT_ENCOUNTER = new DialogueNode("A stray dog", ".", true) {

		@Override
		public int getSecondsPassed() {
			return 2 * 60;
		}

		@Override
		public void applyPreParsingEffects() {
			// Mark Dogmeat as found so the encounter doesn't re-trigger
			Main.game.getDialogueFlags().setSavedLong("dogmeat_found", Main.game.getMinutesPassed());
		}

		@Override
		public String getContent() {
			return "<p>"
					+ "Rounding a corner deeper into the alley, your eye is caught by a large dog sitting in the shadows between two rubbish bins."
					+ " It's a powerfully-built animal — broad shoulders, tan and black fur matted with the dust of Dominion's streets — watching you with steady, amber eyes."
					+ "</p>"
					+ "<p>"
					+ "The dog doesn't growl or cower. It simply holds your gaze, tail giving one slow, measured wag, as if it has been waiting for precisely <i>you</i> to come along."
					+ " A worn collar hangs loose around its neck, the tag too scratched to read."
					+ "</p>"
					+ "<p>"
					+ "Stray dogs aren't uncommon in Dominion, but something about this one feels different — calm, intelligent, and utterly unafraid."
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				if (!Main.game.getPlayer().canHaveMoreCompanions()) {
					return new Response("Take him",
							"You'd love to bring this dog along, but your party is already full.",
							null);
				}
				return new Response("Take him",
						"Crouch down and call the dog over. Invite him to travel with you.",
						DOGMEAT_ADOPTED) {
					@Override
					public void effects() {
						Dogmeat dogmeat = Main.game.getNpc(Dogmeat.class);
						dogmeat.setLocation(Main.game.getPlayer(), true);
						dogmeat.setPlayerKnowsName(true);
						Main.game.getPlayer().addCompanion(dogmeat);
					}
				};
			}

			if (index == 2) {
				return new Response("Leave him",
						"Leave the dog where he is and continue through the alley.",
						Main.game.getDefaultDialogue(false)) {
					@Override
					public void effects() {
						// Place Dogmeat at a fixed alley spot so the player can return later
						Dogmeat dogmeat = Main.game.getNpc(Dogmeat.class);
						dogmeat.setLocation(WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);
					}
				};
			}

			return null;
		}
	};

	public static final DialogueNode DOGMEAT_ADOPTED = new DialogueNode("New companion", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 0;
		}

		@Override
		public String getContent() {
			return "<p>"
					+ "You crouch down and hold out your hand."
					+ " The dog sniffs it once, then pushes its broad head firmly against your palm."
					+ "</p>"
					+ "<p>"
					+ "You scratch behind his ears and he leans into it with full canine commitment."
					+ " When you stand and start walking, he falls into step beside you without hesitation —"
					+ " as if he's been your companion for years."
					+ "</p>"
					+ "<p>"
					+ "<b>Dogmeat</b> has joined your party."
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Continue", "Head onward with your new companion.", Main.game.getDefaultDialogue(false));
			}
			return null;
		}
	};
}
