import os,platform,shutil,glob

#
# project settings
#
VERBOSE  = True
NAME     = "MelissaMaze"    # output script name
ENTRYOBJ = "Main"           # main object to invoke on start
SRC      = "src"            # source code
OUT      = "out"            # output directory
JARS     = [                # jars to include
    os.path.abspath("lib/lwjgl-2.7.1/jar/lwjgl.jar"),
    os.path.abspath("lib/lwjgl-2.7.1/jar/lwjgl_util.jar"),
    os.path.abspath("lib/slick-util/slick-util.jar"),
]
NATIVES  = [                # native directories to include (in PLATFORM order)
    os.path.abspath("lib/lwjgl-2.7.1/native/windows"),
    os.path.abspath("lib/lwjgl-2.7.1/native/macosx"),
    os.path.abspath("lib/lwjgl-2.7.1/native/linux"),
]

#
# working environment
#
# determine platform to build for
def dpf(s):
    if VERBOSE: print s

if platform.system()=="Windows":
    dpf ("Building on Windows")
    PLATFORM = 0
elif platform.system()=="Darwin":
    dpf ("Building on a Mac")
    PLATFORM = 1
elif platform.system()=="Linux":
    dpf ("Building on good old Linux")
    PLATFORM = 2
else:
    raise Exception("Cannot determine platform we're building on")

def sh(s):
    return s if PLATFORM>0 else s+".bat"

def join(*s):
    return "/".join(s)

def pathToPlatform(s):
    return s if PLATFORM>0 else s.replace("/","\\")

def walk(dir):
    for root,dirs,files in os.walk(dir):
        for file in files:
            yield os.path.join(root,file)

# extract important enviroment variables
SCALA_HOME = os.environ["SCALA_HOME"]
dpf ("SCALA_HOME = %s" % SCALA_HOME)
#JAVA_HOME = os.environ["JAVA_HOME"]
#dpf ("JAVA_HOME  = %s" % JAVA_HOME)

# set up scala
SCc    = os.path.abspath(os.path.join(SCALA_HOME,"bin",sh("scalac")))
SCargs = ""
SClib  = os.path.abspath(os.path.join(SCALA_HOME,"lib","scala-library.jar"))

#
# build
#

# set up
CLS = join(OUT,"class")
LIB = join(OUT,"lib")
SEP = ";::"[PLATFORM]

# find all source files
source = []
for file in walk(SRC):
    if file.endswith('.scala'):
        source.append (file)

# make target dirs
try: os.makedirs(OUT)
except: pass
try: os.makedirs(CLS)
except: pass
try: os.makedirs(LIB)
except: pass

# copy all dependencies to be local (so we can deploy easily)
libs = []
dpf ("Copying libraries:")
for src in [ SClib ] + JARS:
    dst = os.path.join(LIB,os.path.basename(src))
    dpf ("  %s -> %s" % (src,dst))
    shutil.copy (src,dst)
    libs.append (join("lib",os.path.basename(dst)))

dpf ("Copying native libraries:")
for path in NATIVES:
    for src in walk(path):
        dst = os.path.join(LIB,os.path.basename(src))
        dpf ("  %s -> %s" % (src,dst))
        shutil.copy (src,dst)

# compile scala files
cl = ' '.join([
    '"'+SCc+'"',
    "-classpath "+SEP.join('"'+jar+'"' for jar in JARS),
    SCargs,
    "-d",'"'+CLS+'"',
    "-deprecation",
    " ".join('"'+s+'"' for s in source)
])
dpf ("Compiling:")
dpf ("  "+cl)
os.system (cl)

# emit start scripts
cp = [ "class" ] + libs
java =' '.join([
    "java",
    "-cp","{1}".join('"{0}/'+s+'"' for s in cp),
    '-Djava.library.path="{0}/lib"',
    ENTRYOBJ,
])
dpf ("Run invocation:")
dpf (java)

target = os.path.join(OUT,NAME)
dpf ("Creating mc & linix scripts")
with open(target,"w") as f:
    f.write ("#!/bin/bash\n")
    f.write ("DIR=`dirname $0`\n")
    f.write ("%s\n" % java.format("$DIR",":"))
if PLATFORM > 0:
    os.system ("chmod +x %s" % target)

dpf ("Creating windows script")
target = os.path.join(OUT,NAME+".bat")
with open(target,"w") as f:
    f.write ("@echo off\n")
    f.write ("%s\n" % java.format("%~dp0",";"))

dpf ("Done")
