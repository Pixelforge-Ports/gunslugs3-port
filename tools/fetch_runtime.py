"""Fetch pinned open-source runtime JARs from Maven Central; never fetch game data."""
from pathlib import Path
import hashlib,json,urllib.request,zipfile
ROOT=Path(__file__).resolve().parents[1]
def fetch(offline=False, test_natives=False):
 entries=json.loads((ROOT/'tools/runtime-lock.json').read_text(encoding='utf-8'))
 for e in entries:
  if e['test_only'] and not test_natives: continue
  dest=ROOT/('build/windows-natives' if e['test_only'] else 'package/gunslugs3/runtime/lib')/e['name']
  dest.parent.mkdir(parents=True,exist_ok=True)
  if not dest.exists() or hashlib.sha256(dest.read_bytes()).hexdigest()!=e.get('sha256'):
   if offline: raise SystemExit('Missing/corrupt cached public runtime file: '+str(dest))
   data=urllib.request.urlopen(e['url'],timeout=60).read()
   if 'sha256' in e: assert hashlib.sha256(data).hexdigest()==e['sha256'],e['name']
   else:
    expected=urllib.request.urlopen(e['url']+'.sha1',timeout=30).read().decode().split()[0]
    assert hashlib.sha1(data).hexdigest()==expected,e['name']
   dest.write_bytes(data)
  e['sha256']=hashlib.sha256(dest.read_bytes()).hexdigest()
  with zipfile.ZipFile(dest) as z: assert z.testzip() is None
 (ROOT/'tools/runtime-lock.json').write_text(json.dumps(entries,indent=2)+'\n',encoding='utf-8')
 for e in json.loads((ROOT/'tools/license-lock.json').read_text(encoding='utf-8')):
  dest=ROOT/'package/gunslugs3/licenses'/e['name'];dest.parent.mkdir(parents=True,exist_ok=True)
  if not dest.exists() or hashlib.sha256(dest.read_bytes()).hexdigest()!=e['sha256']:
   if offline: raise SystemExit('Missing/corrupt cached public runtime file: '+str(dest))
   data=urllib.request.urlopen(e['url'],timeout=60).read()
   assert hashlib.sha256(data).hexdigest()==e['sha256'],e['name']
   dest.write_bytes(data)
 print('RUNTIME_OK' ,len(entries),'pinned archives')
if __name__=='__main__':
 import argparse
 p=argparse.ArgumentParser();p.add_argument('--offline',action='store_true');p.add_argument('--test-natives',action='store_true');a=p.parse_args()
 fetch(a.offline,a.test_natives)
