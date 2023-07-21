# MilitaryChess

运行入口：`lwjgl3`模块里的`Lwjgl3Launcher`类的`main()`。

修改初始棋子位置：`core`模块里的`hundun.militarychess.logic`包的`LogicContext`类的`updateCrossScreenDataPackage()`。

# 程序原理

## logic包

和可视化界面无关的军棋逻辑。

- CrossScreenDataPackage类

连接logic包和ui包的工具。军棋领域的数据，都保存在此，logic包修改这些数据，ui包读取这些数据。

还负责某些数据修改后引起的额外处理，包括：

afterFight()：一次战斗后，行棋方变化，ChessState（待选择起始棋子、待选择目标棋子、待确认）重置。
update()：行棋方变化后，若当前是Ai方则生成一次Ai动作；行棋方变化后，若是暗棋则修改暗棋范围。
findAtPos()：棋局变化后，按最新状态，通过位置查找棋子。

- AiLogic类

主要方法是generateAiAction()，作用是提供Ai的一步棋。实现方法是遍历每个Ai方棋子的一步的每个可移动目的地，评估目的地的得分，选出得分最高的目的地。

- ChessRule类

作用管理行走和战斗规则。包括：

canMove(): 不能重叠自己的棋子；某些棋子不可移动；不能从大本营移出；不能移入非空行营

getFightResult(): 给出两个棋子交战结果是发起者胜、发起者败、同尽。特别地，本程序将空地也视为棋子，所以两个棋子“交战结果”还包括了不合法（!canMove()）、移动（目标棋子是空地）。

- GameboardPosRule类

作用管理和棋盘位置有关的规则。

SimplePos类：仅代表一组row和col，和军棋无关。
GameboardPos类：代表一个军棋棋盘上的位置，每个位置属于以下类型之一：铁路、兵站、行营、大本营

Map<Integer, SimplePos> simplePosMap：军棋棋盘的所有row和col。
Map<SimplePos, GameboardPos> gameboardPosMap：每个row和col对应的更多信息的映射关系。

属于和“对局”无关的静态类：不论是否在对局，不论什么状态的对局，这些关系都是静态且不变的。

- ArmyRuntimeData类

和“对局”有关的类，对应一场对局中的一方。管理这一方的所有棋子。

- ChessRuntimeData类

和“对局”有关的类，对应一场对局中的一个棋子。随着棋局进行，其中数据会变化，例如位置变化、类型变化（死亡时变为空地）。

## ui包

可视化有关的代码。可视化方案基于游戏引擎libgdx。官方手册：https://libgdx.com/wiki/

- MilitaryChessGame类

继承libgdx的ManagedGame类，是libgdx要求的游戏主体。

- XXXScreen类

继承libgdx的Screen类，对应一个可视化窗体。

MyMenuScreen：菜单窗体
PrepareScreen：游戏准备窗体
PlayScreen：游戏进行窗体

- CameraDataPackage类

libgdx提供Camera类，用于移动、拉远拉近显示出的图像。因为图形化窗口的大小是固定的，棋盘大小可以是任意数值。通过UI库的相机拉远拉进功能，让棋盘映射到UI上。

本程序想要修改相机参数时，并不是直接修改Camera类，而是先修改CameraDataPackage，再在统一的地方将CameraDataPackage修改到Camera类。更加方便理清逻辑。

- ChessVM类

一个棋子的可视化实现。每个ChessVM对应一个ChessRuntimeData，从ChessRuntimeData中读取棋子位置、类型，然后展示在libgdx提供的控件上（Label展示文本，Image展示图像）。

DeskClickListener类是它的点击事件监听器，注册到libgdx里，即可得到监听关系。

- AllButtonPageVM类

玩家的操作面板，拥有若干按钮。随着下棋状态ChessState不同而变化展示内容。

- PlayScreen类

为了方便管理，将主要的可视化相关的函数都放在本类。

onDeskClicked()：当棋子被点击。这将推进ChessState。

onCommitButtonClicked()：当确认被点击。这将调用ChessRule执行一次战斗，然后对战斗结果进行后续处理。

onClearButtonClicked()：当清空被点击。这将回退ChessState。

onLogicFrame()：已预先注册到libgdx里，使libgdx每秒调用一次。此时若是Ai在执棋，则模拟Ai操作一次（点击起始棋子、点击目标棋子、确认）。
