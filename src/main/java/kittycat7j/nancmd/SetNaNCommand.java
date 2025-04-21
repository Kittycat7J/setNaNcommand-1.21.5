package kittycat7j.nancmd;

import net.fabricmc.api.ModInitializer;

public class SetNaNCommand implements ModInitializer {
    @Override
    public void onInitialize() {
        NaNCommands.register();
    }
}