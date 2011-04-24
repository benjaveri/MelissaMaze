import os,platform

env = Environment()

#
# determine platform to build for
#
print platform.system()
env["windows"] = platform.system()=="Windows"
env["mac"]     = platform.system()=="Mac"
env["linux"]   = platform.system()=="Linux"

# platform derivatives
env["sep"] = ';' if env["windows"] else ':'

#
# set up scala enviroment
#
env["scalapath"] = r"c:/users/Ben/Programs/scala-2.8.1.final/bin"
env["scalac"]    = os.path.join(env["scalapath"],"fsc.bat")
env["scalaargs"] = "-classpath "+env["sep"].join([
                        "lib/lwjgl-2.7.1/jar/lwjgl.jar",
                        "lib/lwjgl-2.7.1/jar/lwjgl_util.jar",
                        "lib/slick-util/slick-util.jar"])
env["src"] = r"./src"
env["bin"] = r"./out/bin"
env["out"] = r"./out/class"

#
# scala builders
#
def scala_emitter(env,target,source):
    for root,dirs,files in os.walk(env["src"]):
        for file in files:
            if file.endswith('.scala'):
                path = os.path.join(root,file)
                source.append (path)
    target.append (os.path.join(env["bin"],"MelissaMaze"))
    return target,source
    
def scala_builder(env,target,source):
    # create target dirs
    try: os.makedirs (env["bin"])
    except: pass
    try: os.makedirs (env["out"])
    except: pass
    
    # compile scala files
    cl = "%s %s -d %s %s" % (env["scalac"],env["scalaargs"],env["out"]," ".join(str(s) for s in source))
    print cl
    os.system (cl)
    
    # package into jar
    cl = 'jar -cvfm "%s/MelissaMaze.jar" "%s/MelissaMaze.mf" -C "%s" .' % (env["bin"],env["src"],env["out"])
    print cl
    os.system (cl)
    
    # emit start scripts
    with open(str(target[0]),"w") as f:
        f.write ("#!/bin/bash\n")
    with open(str(target[0])+".bat","w") as f:
        f.write ("@echo off\n")

#
# go
#
scala = Builder(action=scala_builder,emitter=scala_emitter)
env["BUILDERS"].update({'scala': scala})
env.scala()

