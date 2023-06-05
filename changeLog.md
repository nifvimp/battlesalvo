## Change Log
# Project Merging
- [Merged View interface and implementation] to be a combination of both programs
  - We wanted to keep the best part of both programs so we combined them
  - adapted board view with number guides
- [Merged board state logic] to be a combination of both programs
  - Used sets for keeping tracking of valid and taken shots
  - Used 2d for representation and ship logic that tracks itself
  - Introduced new cell class to keep track of board state more cleanly
- [Merged local player logic] 
  - Choose to use robust randomize ship placement
  - Decided to use the observer pattern for the board
  - Decided to use loadShots() abstraction for local player subclasses
- Merged rest of project
  - Went with controller and model that corresponded with player implementation