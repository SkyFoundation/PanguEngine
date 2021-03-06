package engine.entity;

import engine.entity.component.TwoHands;
import engine.item.ItemStack;
import engine.world.World;

public class CameraEntity extends BaseEntity {

    public CameraEntity(int id, World world, double x, double y, double z) {
        super(id, world, x, y, z);
        setComponent(TwoHands.class, new TwoHands.Impl());
        getComponent(TwoHands.class).ifPresent(twoHands -> twoHands.setMainHand(ItemStack.EMPTY));
    }

    @Override
    public boolean hasCollision() {
        return false;
    }
}
