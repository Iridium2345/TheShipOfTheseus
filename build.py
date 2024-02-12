import pathlib
import os

ClassPath=[
    "../../starsector-core/*",
]

BuildPath="./build"

Args=["-encoding UTF-8"]

BuildCommand="javac -d {build_path} -classpath {class_path} {args} {file_path}\n"

JarName="TheShipOfTheseus.jar"

JarCommand="jar -cvf {jar_name} {class_list}"

path=pathlib.Path(__file__).parent

def getList(path:pathlib.Path,FileType:str):
    for file in path.iterdir():
        if file.is_file():
            if file.suffix==FileType:
                yield file
        else:
            yield from getSrcList(file)

def getSrcList(path:pathlib.Path):
    return getList(path,".java")

def getClassList(path:pathlib.Path):
    return getList(path,".class")

with open(f"build.bat","w") as f:
    f.write(BuildCommand.format(
        build_path=BuildPath,
        class_path=" ".join(ClassPath),
        args=" ".join(Args),
        file_path=" ".join([str(i.relative_to(path)) for i in getSrcList(path)])
        ))
    os.chdir(BuildPath)
    f.write(JarCommand.format(
        jar_name=JarName,
        class_list=" ".join([str(i.relative_to(BuildPath)) for i in getClassList(BuildPath)])
    ))