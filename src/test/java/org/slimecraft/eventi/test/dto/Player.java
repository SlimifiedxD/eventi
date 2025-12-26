package org.slimecraft.eventi.test.dto;

import java.util.List;

public record Player(String name, List<Block> blocksBroken, int mobsKilled) {
}
