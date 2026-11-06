"""Run real game input tests at five display sizes using a local Java 17 runtime."""
import argparse, os, shutil, struct, subprocess, zipfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
p=argparse.ArgumentParser();p.add_argument('--host',type=Path,help='Fresh compiled host to test without changing package/');p.add_argument('--java',type=Path,required=True);p.add_argument('--jdk',type=Path,required=True);p.add_argument('--game-jar',type=Path,required=True);p.add_argument('--seed-saves',type=Path);p.add_argument('--sizes',nargs='+',default=['640x480','720x480','720x720','1024x768','1280x720']);p.add_argument('--test-save',action='store_true');p.add_argument('--expect-resume',action='store_true');p.add_argument('--output-root',type=Path)
a=p.parse_args();java=a.java.resolve();game=a.game_jar.resolve()
host = a.host or ROOT/'build/artifacts/gunslugs3-host.jar'
if not a.host and not host.is_file():
 host = ROOT/'package/gunslugs3/runtime/gunslugs3-host.jar'
host = host.resolve()
classes=ROOT/'build/test-classes';classes.mkdir(parents=True,exist_ok=True)
compile_cp=ROOT/'build/test-compile-classpath'
compile_cp.mkdir(parents=True,exist_ok=True)
with zipfile.ZipFile(game) as archive:
 for entry in archive.infolist():
  if entry.filename.endswith('.class'):
   target=(compile_cp/entry.filename).resolve();target.relative_to(compile_cp.resolve())
   target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(archive.read(entry))
cp=os.pathsep.join(map(str,[host,compile_cp]))
subprocess.run([str(a.jdk.resolve()/'bin'/('javac.exe' if os.name=='nt' else 'javac')),'--release','8','-Xlint:-options','-cp',cp,'-d',str(classes),str(ROOT/'tests/GameplaySmoke.java')],check=True)
cp=os.pathsep.join(map(str,[classes,host,ROOT/'package/gunslugs3/runtime/lib/*',ROOT/'build/windows-natives/*',game]))
for width,height in [tuple(map(int,size.split("x"))) for size in a.sizes]:
 out=(a.output_root.resolve() if a.output_root else ROOT/'build/resolutions')/f'{width}x{height}';out.mkdir(parents=True,exist_ok=True)
 saves=out/'saves'
 if a.seed_saves and not saves.exists(): shutil.copytree(a.seed_saves,saves)
 command=[str(java),'-Xms32m','-Xmx256m','-XX:+UseSerialGC','-Xlog:class+load=info','-Dgunslugs3.hidden=true',f'-Dgunslugs3.width={width}',f'-Dgunslugs3.height={height}',f'-Dgunslugs3.saves={saves}',f'-Dgunslugs3.output={out}','-cp',cp,'org.portmaster.gunslugs3.GameplaySmoke']
 if a.expect_resume: command.insert(1,'-Dgunslugs3.expectResume=true')
 if a.test_save: command.insert(1,'-Dgunslugs3.testSave=true')
 with (out/'run.log').open('w') as log: subprocess.run(command,stdout=log,stderr=subprocess.STDOUT,check=True,timeout=360)
 log=(out/'run.log').read_text();assert 'GAMEPLAY_OK' in log
 logical_width = 1080 if width * 9 < height * 16 else 1280
 assert f'GAME_RESIZE_OK {width}x{height} view={logical_width}x720' in log, log
 assert 'Exception:' not in log and 'Exception in thread' not in log, out
 assert 'com.codedisaster.steamworks.SteamAPI source:' not in log
 assert 'com.studiohartman.jamepad.ControllerManager source:' not in log
 assert 'com.orangepixel.gunslugs3.desktop.EpicGames source:' not in log
 png=(out/'final.png').read_bytes();assert struct.unpack('>II',png[16:24])==(width,height)
 assert any(p.is_file() for p in saves.rglob('*'))
 print(f'RESOLUTION_OK {width}x{height}',flush=True)
