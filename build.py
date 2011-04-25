import os,platform,shutil

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
def dpf(s):
    if VERBOSE: print s

def walk(dir):
    for root,dirs,files in os.walk(dir):
        for file in files:
            yield os.path.join(root,file)

# determine platform to build for
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
    raise Exception("Cannot determine platform to build for")

# extract important enviroment variables
SCALA_HOME = os.environ["SCALA_HOME"]
dpf ("SCALA_HOME = %s" % SCALA_HOME)
JAVA_HOME = os.environ["JAVA_HOME"]
dpf ("JAVA_HOME  = %s" % JAVA_HOME)

# set up scala
def sh(s): return s if PLATFORM>0 else sh+".bat"
SCc    = os.path.abspath(os.path.join(SCALA_HOME,"bin",sh("scalac")))
SCargs = ""
SClib  = os.path.abspath(os.path.join(SCALA_HOME,"lib","scala-library.jar"))

#
# build
#

# set up
LIB = os.path.join(OUT,"lib")
CLS = os.path.join(OUT,"class")
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

# compile scala files
cl = ' '.join([
    SCc,
    "-classpath "+SEP.join(JARS),
    SCargs,
    "-d",CLS,
    " ".join(source)
])
dpf (cl)
os.system (cl)

# emit start scripts
jars = [ CLS, SClib ] + JARS
java =' '.join([
    os.path.join(JAVA_HOME,"bin","java"),
    "-cp",SEP.join(os.path.abspath(s) for s in jars),
    '-Djava.library.path=%s' % NATIVES[PLATFORM],
    ENTRYOBJ,
])
target = os.path.join(OUT,sh(NAME))
with open(target,"w") as f:
    f.write ("%s\n" % java)
if PLATFORM > 0:
    os.system ("chmod +x %s" % target)
dpf (java)
