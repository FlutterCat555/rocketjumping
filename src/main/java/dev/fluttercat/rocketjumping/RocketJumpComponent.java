package dev.fluttercat.rocketjumping;

//import net.minecraft.resources.Identifier;
//import net.minecraft.world.level.storage.ValueInput;
//import net.minecraft.world.level.storage.ValueOutput;
//import org.ladysnake.cca.api.v3.component.ComponentKey;
//import org.ladysnake.cca.api.v3.component.ComponentRegistry;
//import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
//
//public abstract class RocketJumpComponent implements ModComponents {
//
//
//    private boolean value = false;
//
//    @Override
//    public boolean getRocketJump() {
//        return this.value;
//    }
//
//    @Override
//    public void setRocketJump(boolean b) {
//        this.value = b;
//    }
//
//    @Override
//    public void readData(ValueInput readView) {
//        this.value = readView.getBooleanOr("rocketjump",false);
//    }
//
//    @Override
//    public void writeData(ValueOutput writeView) {
//        writeView.putBoolean("rocketjumping",this.value);
//    }
//}
