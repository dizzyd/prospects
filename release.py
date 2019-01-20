
import os
import json
import argparse
import subprocess
import requests
import sys
import logging

# These two lines enable debugging at httplib level (requests->urllib3->http.client)
# You will see the REQUEST, including HEADERS and DATA, and RESPONSE with HEADERS but without DATA.
# The only thing missing will be the response.body which is not logged.
# try:
#     import http.client as http_client
# except ImportError:
#     # Python 2
#     import httplib as http_client
# http_client.HTTPConnection.debuglevel = 1

# # You must initialize logging, otherwise you'll not see debug output.
# logging.basicConfig()
# logging.getLogger().setLevel(logging.DEBUG)
# requests_log = logging.getLogger("requests.packages.urllib3")
# requests_log.setLevel(logging.DEBUG)
# requests_log.propagate = True

#########################################################

def run(cmd):
    process = subprocess.Popen(cmd, stdout=subprocess.PIPE)
    (output, _err) = process.communicate()
    if process.returncode != 0:
        return None, process.returncode
    return output.strip().decode(), None

def runNoOutput(cmd):
    process = subprocess.Popen(cmd)
    process.communicate()
    if process.returncode == 0:
        return None
    else:
        return process.returncode

# Uploading to CF requires a API token (CF_API_TOKEN)
apiToken = os.getenv("CF_API_TOKEN")
if apiToken == None or len(apiToken) < 1:
    print("No CF_API_TOKEN available!")
    sys.exit(1)

headers = {'X-Api-Token': apiToken}

parser = argparse.ArgumentParser()
parser.add_argument("-project", help="Mod project ID", required=True)
parser.add_argument("-name", help="Mod filename", required=True)
parser.add_argument("-tag", help="Tag to use for this release", default="")
parser.add_argument("-vsns", help="Game versions supported by this release", default="1.12.2")
parser.add_argument("-rel", help="Release type (alpha|beta|release)", default="alpha")
parser.add_argument("-skipupload", help="Skip uploading to CF", default=False)
args = parser.parse_args()

# Pull the list of game versions
versionsReq = requests.get("https://minecraft.curseforge.com/api/game/versions", headers=headers)
versions = {}
for obj in versionsReq.json():
   versions[obj["name"]] = obj["id"]

# Translate requested versions to game version IDs
gameIds = [versions[args.vsns]]

# If no tag was provided, use the last one on the repo
if args.tag == "":
    args.tag, err = run(["git", "describe", "--abbrev=0", "--tags"])
    if err != None:
        print("Failed to find most recent tag!")
        sys.exit(1)

print(args.tag)

# Checkout the appropriate release from git
_, err = run(["git", "checkout", "-q", args.tag])
if err != None:
    print("Failed to checkout tag %s" % (args.tag))
    sys.exit(1)

# Get the tag preceding the requested one
lastTag, err = run(["git", "describe", "--abbrev=0", "--tags", args.tag+"^"])
if err != None:
    print("Failed to get tag preceding %s" % (args.tag))
    sys.exit(1)

# Pull the changelog between the two tags
changelog, err = run(["git", "log", "--format=%h: %s", lastTag + ".." + args.tag])
if err != None:
    print("Failed to get changelog for %s -> %s" % (lastTag, args.tag))
    sys.exit(1)

# Do the actual build
err = runNoOutput(["./gradlew", "-Pversion=" + args.tag, "clean", "build"])
if err != None:
    print(err)
    print("Build of %s tag failed" % (args.tag))
    sys.exit(1)

if args.skipupload:
    print("Skipping upload!")
    sys.exit(0)

# Setup metadata field of our POST
metadata = json.dumps({"changelog": changelog, "gameVersions": gameIds, "releaseType": args.rel})

# Setup files list for POST
files = [('metadata', (None, metadata, "application/json")),
         ('file', (open("build/libs/%s-%s.jar" % (args.name, args.tag), "rb")))]
resp = requests.post("https://minecraft.curseforge.com/api/projects/%s/upload-file" % args.project, headers=headers, files=files)
print(resp.text)
resp.raise_for_status()


