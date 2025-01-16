from builder.JBuilder import JavaProject,JavaGroup,javac,JFilter,java,jar

import time

BuildPath="./build/"

project=JavaProject()
tsot:JavaGroup=project.addGroup("TheShipOfTheseus",JavaGroup)
tsot.WorkPath="./"
tsot.JavaHome="D:\\app\java\java-se-7u75-ri\\bin"

(java@tsot).addArg(java.Arg.Version)

(javac@tsot).addDir("./src/",JFilter.JavaFile()
).addArg(javac.Arg.Dir,BuildPath
).addArg(javac.Arg.Class_Path,["../../starsector-core/*","./libs/*"]
).addArg(javac.Arg.Encoding,"UTF-8")

(jar@tsot).addFile(f"{BuildPath} ."
).addArg("-cvf"
).setCustom(jar.AvailableCustom.jar_path,"./jars/"
)

tsot.cmdlst()
project.runGroup("TheShipOfTheseus")

print(time.asctime(time.localtime()))