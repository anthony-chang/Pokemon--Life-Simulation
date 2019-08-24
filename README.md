# Pokemon: Life-Simulation
 A Pokemon game inspired by Conway's Game of LIfe. Created for an ICS project in grade 11.
 
 Developer guide found [here.](https://docs.google.com/document/d/1_vzKa1IXUmjHE51Q5V_yHW4ps7rV7HLbjJ6k8_2RCOo/edit?usp=sharing)

## User Guide

### Getting Started
The Pokémon life simulation is a Java-based simulation that can be opened, interacted with, and modified on a computer. Make sure that the Pokemon.jar, PlayerSprite folder, and Graphics folder are all in the same folder when running.
On startup, you will be greeted with a menu screen with four buttons. Clicking the “Simulate” button will begin the game. 
Pokémon life simulation is roughly based on the original Pokémon game. The organisms in the game are visually based, and named after, similar creatures in the original Pokémon game. Similarly, the environment and surroundings are based on features in the original Pokémon game.

### User Interface
On the main menu, there are four options. The “Inventory” button opens your inventory (empty to start with), “Information” which provides instructions on how to play, “Quit” which exits the program, and “Simulate which begins the game.
In the game, there are two buttons on the window. The pause button in the top right corner pauses the simulation and opens the main menu, and the inventory button in the bottom right corner opens your current inventory. 

### Life Simulation Rules
The user controls a pokemon trainer, which will spawn at the very top left most corner in the window. 
To move the pokemon trainer, use either the arrow keys or the WASD keys
Trainers can catch a Pokémon. To do this, move the trainer on top of a Pokémon, and there is a chance of catching that Pokémon, depending on the health of the Pokémon. 
Pokémon are first created with a randomized amount of health points (HP) between the following predefined values:
Growlithe: 20-100 HP
Gastly: 80-100 HP
Squirtle: 60-100 HP
Beedrill: 20-100 HP
Pokémon move randomly, but with natural tendencies or restrictions:
Squirtles move towards water
Beedrills move towards trees
Gastlys move away from other Pokémon
Growlithes are attracted to other Growlithes and tend to form herds
A Pokémon that contacts with another Pokémon will cause both to lose health:
-50 HP each
When a Pokémon dies (0 or less HP), a small red square will appear at the location where the Pokémon dies. 
When the trainer successfully catches a Pokémon, a small green square will appear at the position of the catch for a second indicating the catch.
Other Rules:
Beedrills and Squirtles, and Gastlys are the Pokémon who can travel over water tiles
The user character can’t travel in lakes (water tiles). However, they can walk along the shore or in the shallow parts of the lake.
The user cannot have more than six pokemon at once in the inventory

### Program Capabilities and Features
The program is able to generates a basic menu, from which the user can interact with and can access the simulation, inventory, and information pages. The program responds to user input/interaction to create an inventory of Pokémon organisms, and information pages which explains the trends and tendencies of the various different Pokémon organisms in the simulation. Within the inventory, which can also be accessed through the simulation page, users can view various Pokémon collected and view current health levels by clicking on the Pokémon. Through all these pages, the user can easily navigate, understand, and use the program in a user-friendly and efficient manner.

If prompted by the user, the program can create a Pokémon-based simulation that shows interactions between Pokémon organisms and their environment. The program is able to create a random environment comprised of lakes (water tiles), trees, and land (grass tiles); when creating a random environment, it creates various different Pokémon creatures with both randomized and pre-defined characteristics. It is able to track and modify these characteristics depending on external factors throughout the game. As well, it is able to create an interactive character within the game, through which the user can travel to various parts of the screen using keyboard arrows (and or a/w/s/d keys for left, up, down, and right, respectively) and observe different Pokémon (which are designed follow the aforementioned rules and restrictions).

As well, the program allows the user to “collect” Pokémon by travelling close to them. Based on chance (which varies based on Pokémon health level), the user may collect the Pokémon, which can then later be released on a different section of the map.

### Limitations or Bugs of the Program
There are some bugs within the program. For instance, if the randomly generated lake is too big or shaped in a way that extends to the edge of the screen, the character isn’t able to travel to some portions of the screen/game (as characters can’t travel on water tiles). It is sometimes possible to get trapped within a small portion of land if the land is generated so that the character can’t move. As well, the character is able to travel under the inventory button, where it is no longer visible.

As well, there are some visual bugs. For instance, when the character goes to the far right of the screen before turning left and walking, there is sometimes a white space left behind the character. Similarly, when the simulation is paused, it is sometimes possible for character to move around, and for some Pokemon to disappear if the user character does pass over it.
