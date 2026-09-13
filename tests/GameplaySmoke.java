package org.portmaster.gunslugs3;
import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
public class GameplaySmoke extends Main {
 boolean resumeSeen; int frame,play,previous=-1; long started=System.nanoTime();
 public static void main(String[] args) throws Exception { new Lwjgl3Application(new GameplaySmoke(),configuration()); }
 void key(int code,boolean down) {if(down)Gdx.input.getInputProcessor().keyDown(code);else Gdx.input.getInputProcessor().keyUp(code);}
 Object read(String name)throws Exception {java.lang.reflect.Field f=com.orangepixel.gunslugs3.myCanvas.class.getDeclaredField(name);f.setAccessible(true);return f.get(null);}
 @Override public void render() {
  frame++;
  if(frame>240){
   if(frame%90==0){if(GameState==12){key(Input.Keys.F1,true);key(Input.Keys.ESCAPE,true);}else{key(Input.Keys.X,true);key(Input.Keys.ENTER,true);}}
   if(frame%90==3){key(Input.Keys.X,false);key(Input.Keys.ENTER,false);key(Input.Keys.F1,false);key(Input.Keys.ESCAPE,false);}
  }
  if(GameState==27) {
   key(Input.Keys.RIGHT,myPlayer.x<634 && com.orangepixel.gunslugs3.World.inPlayerTalk<0);
   key(Input.Keys.UP,frame%90<12);
   if(frame%15==0)key(Input.Keys.A,true);
   if(frame%15==2)key(Input.Keys.A,false);
  }
  if(GameState==7){
   play++;
   if(play%60==1)key(Input.Keys.RIGHT,true);
   if(play%60==5)key(Input.Keys.RIGHT,false);
  }
  super.render();
  if(play==0 && mySaveGame!=null && mySaveGame.hasSavegame && !resumeSeen){resumeSeen=true;System.out.println("RESUME_DATA_FOUND");}
  java.nio.IntBuffer v=com.badlogic.gdx.utils.BufferUtils.newIntBuffer(4);Gdx.gl20.glGetIntegerv(com.badlogic.gdx.graphics.GL20.GL_VIEWPORT,v);
  if(v.get(0)!=layout.x || v.get(1)!=layout.y || v.get(2)!=layout.width || v.get(3)!=layout.height)throw new IllegalStateException("Viewport lost");
  if(physicalGraphics.getWidth()!=Integer.getInteger("gunslugs3.width",640) || physicalGraphics.getHeight()!=Integer.getInteger("gunslugs3.height",480))throw new IllegalStateException("Physical size changed");
  if(GameState!=previous){System.out.println("STATE "+frame+" "+GameState);previous=GameState;}
  if(frame%600==0)System.out.println("PLAYER "+myPlayer.x+","+myPlayer.y+" talk="+com.orangepixel.gunslugs3.World.inPlayerTalk);
  if(frame%600==0)capture(System.getProperty("gunslugs3.output")+"/frame"+frame+".png");
  if(play>=360 || frame>=5400){
   capture(System.getProperty("gunslugs3.output")+"/final.png");
   if(play<360)throw new IllegalStateException("Gameplay not reached long enough: "+play);
   if(Boolean.getBoolean("gunslugs3.expectResume") && !resumeSeen)throw new IllegalStateException("Saved game was not found on restart");
   double seconds=(System.nanoTime()-started)/1e9;if(seconds<(frame-2)/60.0)throw new IllegalStateException("Frame cap exceeded");
   if(Boolean.getBoolean("gunslugs3.testSave")){try {com.orangepixel.gunslugs3.myCanvas.SaveGame(PROFILEID); com.orangepixel.gunslugs3.myCanvas.LoadGame(PROFILEID); if(!mySaveGame.hasSavegame)throw new IllegalStateException("Save readback failed"); System.out.println("SAVE_READBACK_OK");}catch(Exception e){throw new IllegalStateException(e);}}
   System.out.println("GAMEPLAY_OK frames="+frame+" play="+play+" seconds="+seconds);Gdx.app.exit();
  }
 }
}
