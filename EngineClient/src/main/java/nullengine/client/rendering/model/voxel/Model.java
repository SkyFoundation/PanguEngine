package nullengine.client.rendering.model.voxel;

import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.model.BakedModel;
import nullengine.client.rendering.texture.TextureAtlasPart;

import java.util.Collection;
import java.util.function.Function;

public interface Model {

    BakedModel bake(Function<AssetURL, TextureAtlasPart> textureGetter);

    Collection<AssetURL> getTextures();
}
