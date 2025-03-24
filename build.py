from builder.JBuilder import JavaProject,JavaGroup,javac,JFilter,java,jar
import time
import sys
from pathlib import Path
import os

BuildPath="./build/"

project=JavaProject()
tsot:JavaGroup=project.addGroup("TheShipOfTheseus",JavaGroup)
tsot.WorkPath="./"
# tsot.JavaHome="D:\\app\java\java-se-7u75-ri\\bin"
pkg:JavaGroup=project.addGroup("pkg",JavaGroup)
pkg.WorkPath="./build"
# pkg.JavaHome="D:\\app\java\java-se-7u75-ri\\bin"

(java@tsot).addArg(java.Arg.Version)

(javac@tsot).addDir("./src/",JFilter.JavaFile()
).addArg(javac.Arg.Dir,BuildPath
).addArg(javac.Arg.Class_Path,["../../starsector-core/*","./libs/*"]
).addArg(javac.Arg.Encoding,"UTF-8")

(jar@pkg).addFile(f" ."
).addArg("-cvf"
).setCustom(jar.AvailableCustom.jar_path,Path("./jars/").absolute()
).setCustom(jar.AvailableCustom.jar_file,"TheShipOfTheseus.jar")

project.start()
print(time.asctime(time.localtime()))


if "start" in sys.argv :
    os.chdir("../../")
    os.system(
        "start starsector.exe",
    )
