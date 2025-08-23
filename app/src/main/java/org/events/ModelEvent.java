package org.events;

/**
 * tipi di evento emessi dal model
 */

public enum ModelEvent{
	CARDS_DEALT,
	TURN_STARTED,
	TURN_ENDED,
    TRICK_STARTED,
    TRICK_ENDED,
    ROUND_STARTED,
    ROUND_ENDED,
    GAME_STARTED,
    GAME_OVER,
    PROFILE_CHANGED,
    PLAYER_JOINED,
    PLAYER_LEFT,
}
