import pathlib
import os
ClassPath=[
    "../../starsector-core/*",
]

BuildPath="build"

Args=["-encoding UTF-8"]

BuildCommand="javac -d {build_path} -classpath {class_path} {args} {file_path}\n"

JarName="../jars/TheShipOfTheseus.jar"

JarCommand="jar -cvf {jar_name} {class_list}"

path=pathlib.Path(__file__).parent

def getList(path:pathlib.Path,FileType:str):
    for file in path.iterdir():
        if file.is_file():
            if file.suffix==FileType:
                yield file
        else:
            yield from getList(file,FileType)

def getSrcList(path:pathlib.Path):
    return getList(path,".java")

def getClassList(path:pathlib.Path):
    return getList(path,".class")

def getVariantList(path:pathlib.Path):
    return getList(path,".variant")

os.system(BuildCommand.format(
        build_path=BuildPath,
        class_path=" ".join(ClassPath),
        args=" ".join(Args),
        file_path=" ".join([str(i.relative_to(path)) for i in getSrcList(path)])
        ))
os.chdir(BuildPath)

os.system(JarCommand.format(
        jar_name=JarName,
        class_list=" ".join([str(i.relative_to(path.joinpath(BuildPath))) for i in getClassList(path.joinpath(BuildPath))])
    ))

os.chdir(str(path))

with open("data/campaign/sim_opponents.csv","w",encoding="utf-8") as f:
    f.write("variant id\n"+"\n".join(filter(lambda x:x.find("guardian")!=-1,[i.stem for i in (getVariantList(path))])))

with open("data/campaign/sim_opponents_dev.csv","w",encoding="utf-8") as f:
    f.write("variant id\n"+"\n".join([i.stem for i in getVariantList(path)]))