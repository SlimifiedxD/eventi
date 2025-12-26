package org.slimecraft.eventi.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slimecraft.eventi.annotation.Listener;
import org.slimecraft.eventi.test.dto.Block;
import org.slimecraft.eventi.test.dto.Player;
import org.slimecraft.eventi.test.dto.PlayerBlockBreakEvent;

import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerBlockBreakEventTest {
    @ParameterizedTest
    @MethodSource("events")
    @Listener
    void listen_to_event_and_print_player_score(PlayerBlockBreakEvent e) {
        assertEquals(10, e.player().mobsKilled());
    }

    public static Stream<PlayerBlockBreakEvent> events() {
        Player player = new Player("Jeremy", Collections.emptyList(), 10);
        return Stream.of(
                new PlayerBlockBreakEvent(player, new Block(30, 50, 90)),
                new PlayerBlockBreakEvent(player, new Block(69, 67, 420))
        );
    }
}
