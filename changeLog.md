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

# Implementing Server Communication
- [Added ProxyController] that handles all server requests and running the game
- [Added Json Records] for easy JsonNode creation
  - MessageJson
  - Coord Json
  - FleetJson
  - ShipJson
  - VolleyJson
- Changed ship record to use Orientation enum instead of boolean
- Added GameType enum for join server json response
- [Added toJson() methods to Coord and Ship]
- [Added to original class methods to Json classes]
  - ex. CoordJson -> toCoord() -> Coord
- [Created delegate json and helper methods] to handle json messages from the server
  - methods and structure were based on Lab 6
- Added view to proxyController

# Improving Battle Salvo AI
- [Made Ai smart when encountering a hit]
  - Added TargetingLine class to go out in a line from a known hit until a miss occurs
  - Starts in all 4 directions initially
  - Efficiently sinks opponent ship