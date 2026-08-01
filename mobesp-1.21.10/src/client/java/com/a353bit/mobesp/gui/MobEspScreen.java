package com.a353bit.mobesp.gui;

import com.a353bit.mobesp.client.MobEspClient;
import com.a353bit.mobesp.config.MobEspConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * Menu: text field for the mob ID, on/off toggle, and a button
 * to rebind the open key directly from here
 * (same logic as vanilla Controls menu: click, then press the key).
 */
public class MobEspScreen extends Screen {

    private TextFieldWidget mobIdField;
    private ButtonWidget rebindButton;
    private final MobEspConfig config = MobEspConfig.get();

    // true while waiting for the user to press a new key
    private boolean listeningForKey = false;

    public MobEspScreen() {
        super(Text.translatable("mobesp.screen.title"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 2 - 45;

        this.mobIdField = new TextFieldWidget(
                this.textRenderer,
                centerX - 100, startY,
                200, 20,
                Text.translatable("mobesp.screen.mob_id")
        );
        this.mobIdField.setMaxLength(64);
        this.mobIdField.setText(config.mobId);
        this.addDrawableChild(this.mobIdField);
        this.setInitialFocus(this.mobIdField);

        // On/off toggle
        this.addDrawableChild(
                CyclingButtonWidget.onOffBuilder(config.enabled)
                        .build(centerX - 100, startY + 30, 200, 20,
                                Text.translatable("mobesp.screen.enabled"),
                                (button, value) -> config.enabled = value)
        );

        // Button to rebind the menu open key
        this.rebindButton = ButtonWidget.builder(currentKeyText(), button -> startListening())
                .dimensions(centerX - 100, startY + 60, 200, 20)
                .build();
        this.addDrawableChild(this.rebindButton);

        // "Done" button
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("mobesp.screen.done"), button -> this.close())
                        .dimensions(centerX - 100, startY + 90, 200, 20)
                        .build()
        );
    }

    private Text currentKeyText() {
        KeyBinding key = MobEspClient.getOpenMenuKey();
        if (listeningForKey) {
            return Text.translatable("mobesp.screen.press_a_key");
        }
        return Text.translatable("mobesp.screen.rebind", key.getBoundKeyLocalizedText());
    }

    private void startListening() {
        this.listeningForKey = true;
        this.rebindButton.setMessage(currentKeyText());
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (this.listeningForKey) {
            if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
                // Cancel without changing anything
                this.listeningForKey = false;
                this.rebindButton.setMessage(currentKeyText());
                return true;
            }

            KeyBinding key = MobEspClient.getOpenMenuKey();
            key.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(input.key()));
            KeyBinding.updateKeysByCode();
            // Save immediately to options.txt, like the vanilla Controls menu does
            MinecraftClient.getInstance().options.write();

            this.listeningForKey = false;
            this.rebindButton.setMessage(currentKeyText());
            return true;
        }
        // If we're not listening, let the text field/other widgets handle the key
        return super.keyPressed(input);
    }

    @Override
    public void close() {
        String typed = this.mobIdField.getText().trim();
        if (!typed.isEmpty()) {
            config.mobId = typed;
        }
        config.save();
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
