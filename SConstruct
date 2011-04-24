import os,platform,shutil

env = Environment()
env["verbose"] = True
def dpf(s):
    if env["verbose"]: print s

#
# project settings
#         
env["name"]  = r"MelissaMaze"     # final script name               
env["entry"] = "Main"             # main entry point
env["src"]   = r"./src"           # source code
env["bin"]   = r"./out/bin"       # final binaries
env["out"]   = r"./out/class"     # intermediate class files
env["jars"]  = [                  # jars to include
    "lib/lwjgl-2.7.1/jar/lwjgl.jar",
    "lib/lwjgl-2.7.1/jar/lwjgl_util.jar",
    "lib/slick-util/slick-util.jar",
]

#
# working environment
#
# determine platform to build for
print platform.system()
if platform.system()=="Windows":
    dpf ("Building on Windows")
    env["platform"] = 0
elif platform.system()=="Mac":
    dpf ("Building on a Mac")
    env["platform"] = 1
elif platform.system()=="Linux":
    dpf ("Building on good old Linux")
    env["platform"] = 2
else:
    raise Exception("Cannot determine platform to build for")

# extract important enviroment variables
env["SCALA_HOME"] = os.environ["SCALA_HOME"]
env["JAVA_HOME"]  = os.environ["JAVA_HOME"]
dpf ("SCALA_HOME = %s" % env["SCALA_HOME"])
dpf ("JAVA_HOME  = %s" % env["JAVA_HOME"])

# platform derivatives
env["sep"] = ";::"[env["platform"]]
def sh(fn): return fn if env["platform"]!=0 else fn+".bat"

# set up scala
env["SCc"]    = os.path.join(env["SCALA_HOME"],"bin",sh("fsc"))
env["SCargs"] = ""
env["SClib"]  = os.path.join(env["SCALA_HOME"],"lib","scala-library.jar")

#
# scala builders
#
def scala_emitter(env,target,source):
    # use all .scala files in source folder
    for root,dirs,files in os.walk(env["src"]):
        for file in files:
            if file.endswith('.scala'):
                path = os.path.join(root,file)
                source.append (path)
    # final desitnation
    target.append (os.path.join(env["bin"],"MelissaMaze"))
    return target,source
    
def scala_builder(env,target,source):
    # create target dirs
    try: os.makedirs (env["bin"])
    except: pass
    try: os.makedirs (env["out"])
    except: pass
    
    
    # compile scala files
    cl = ' '.join([
        env["SCc"],
        "-classpath "+env["sep"].join(env["jars"]),
        env["SCargs"],
        "-d "+env["out"],
        " ".join([str(s) for s in source])
    ])
    dpf (cl)
    os.system (cl)
    
    # package into jar
    cl = ' '.join([
        "jar -cvf",
        os.path.join(env["bin"],env["name"]+".jar"),
        env["entry"],
        "-C "+env["out"], ".",
    ])
    dpf (cl)
    os.system (cl)
    
    # emit start scripts
    jars = [ 
        env["name"]+".jar",
        os.path.join(env["bin"],os.path.basename(env["SClib"])),
    ] + env["jars"]
    java =' '.join([
        "java",
        "-cp",env["sep"].join(os.path.basename(s) for s in jars),
        "-Djava.library.path:%s" % [
            r"../../lib/lwjgl-2.7.1/native/windows",
            r"../../lib/lwjgl-2.7.1/native/macos",
            r"../../lib/lwjgl-2.7.1/native/linux",
        ][env["platform"]],
        env["entry"],
    ])
    with open(sh(str(target[0])),"w") as f:
        f.write ("cd %s\n" % env["bin"])
        f.write ("%s\n" % java)
        f.write ("cd %s\n" % os.path.join("..",".."))
#
# go
#
scala = Builder(action=scala_builder,emitter=scala_emitter)
env["BUILDERS"].update({'scala': scala})
env.scala()

