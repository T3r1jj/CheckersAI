# CheckersAI
CheckersAI project is a form of English draughts game with simple Artificial Intelligence based on alpha-beta algorithm. Beside standard gameplay the application displays AI grading info on the right side. It is also possible to run the game either with graphics interface or text interface. Grading configuration can be changed in game or saved and loaded from file. There are 3 gameplay modes:
+ AI vs AI;
+ player white (starting) vs AI;
+ player red vs AI (starting).

To run the game a JRE 1.7 or newer is needed.

## Overview
CheckersAI is an old unrefactored project. The AI implementation is based on alpha-beta algorithm with grading system. It was not enough to create very intelligent enemy (though sometimes, winning can prove to be hard) since number of turns predicted is limited by performance. The AI lacks some basic elements like basic/break/strategic moves. Nevertheless this could be a good starting point. Best move is chosen based on board value. It is possible to set own grading values:
+ checker pieces (pawn, king);
+ win, draw;
+ search depth (red, white).

Grading function takes the difference between values for both players (negative or positive) and divides it by number of remaining enemy pieces.

To start CheckersAI with text interface run the game with -ui=tui parameter (java -jar CheckersAI.jar -ui=tui). Unlike the GUI, the TUI is slightly limited and the grading configuration will be loaded from CheckersAI.properties file. If the file does not exist, default values will be loaded internally.

## Gallery
<p align="center">
<b>GUI</b><br/>
<img src="https://cloud.githubusercontent.com/assets/20327242/21468189/c3094cf0-ca07-11e6-8acb-648d57b23d6d.png" alt="CheckersAI GUI image">
<br/><br/>
<b>TUI</b><br/>
<img src="https://cloud.githubusercontent.com/assets/20327242/21468190/ccb0db10-ca07-11e6-8773-fe4428d6b6d5.png" alt="CheckersAI TUI image">
</p>

## License
Copyright 2015 Damian Terlecki

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
