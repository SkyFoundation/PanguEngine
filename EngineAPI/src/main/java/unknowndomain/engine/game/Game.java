package unknowndomain.engine.game;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.Prototype;
import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.world.World;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * The game shares the same set of mod and resources pack manifest
 */
public interface Game extends RuntimeObject, Prototype<RuntimeObject, Game> {
    GameContext getContext();

    Collection<World> getWorlds();

    @Nullable
    World getWorld(String name);

    void tick();

    // World spawnWorld(WorldConfig? config); // TODO: design this
}
