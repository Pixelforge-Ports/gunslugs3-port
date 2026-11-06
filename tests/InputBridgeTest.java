package org.portmaster.gunslugs3;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputBridgeTest {
    private static final class RecordingInput extends InputAdapter {
        final List<Integer> pressed = new ArrayList<>();
        final List<Integer> released = new ArrayList<>();
        int acceptedKey = 131;

        @Override public boolean keyDown(int keycode) {
            pressed.add(keycode);
            return keycode == acceptedKey;
        }

        @Override public boolean keyUp(int keycode) {
            released.add(keycode);
            return keycode == acceptedKey;
        }
    }

    public static void main(String[] args) {
        DisplayLayout layout = new DisplayLayout();
        layout.resize(720,480);
        RecordingInput target = new RecordingInput();
        InputProcessor bridge = Main.inputBridge(target,layout);
        require(bridge.keyDown(Input.Keys.ESCAPE),"Legacy Escape was not handled");
        require(bridge.keyUp(Input.Keys.ESCAPE),"Legacy Escape was not released");
        require(target.pressed.equals(Arrays.asList(131,Input.Keys.ESCAPE)),"Missing Escape alias press");
        require(target.released.equals(target.pressed),"Escape aliases have unmatched releases");
        target.acceptedKey = Input.Keys.ESCAPE;
        require(bridge.keyDown(Input.Keys.ESCAPE),"Modern saved Escape binding was not handled");
        require(bridge.keyUp(Input.Keys.ESCAPE),"Modern saved Escape binding was not released");
        target.pressed.clear();
        target.released.clear();
        for (int keycode : new int[]{Input.Keys.ENTER,Input.Keys.X,Input.Keys.UP,Input.Keys.F1}) {
            bridge.keyDown(keycode);
            bridge.keyUp(keycode);
        }
        require(target.pressed.equals(Arrays.asList(Input.Keys.ENTER,Input.Keys.X,Input.Keys.UP,Input.Keys.F1)),"Other keys changed");
        require(target.released.equals(target.pressed),"Other keys have unmatched releases");
        System.out.println("INPUT_BRIDGE_OK legacy Escape, modern saved bindings, balanced releases, unchanged keys");
    }

    private static void require(boolean condition,String message) {
        if (!condition) throw new AssertionError(message);
    }
}
