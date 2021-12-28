# README #

**Name:**	Darren Au

**Period:**	1

**Game Title:** Connect 4

## Game Proposal ##

It will be a simple game of tick tack toe, with options for single player, two players, and other misc things (music, leaderboard etc). The main thing I would like to focus on is the algorithm that
plays for the computer during single player. I haven't looked into it with too much depth yet but I believe it's called minimax. In the event that the game is too easy to create (not sure how long implementing the 
algorithm will take), I would like to try the same with chess, as I would believe it would be more difficult as there are more things to consider.

Game Controls:

Mouse to click and move the checkers into the selected column. Select and drag.

Game Elements:

Board and checkers

How to Win:

Get 4 in a row

## Link Examples ##
Provide links to examples of your game idea.  This can be a playable online game, screenshots, YouTube videos of gameplay, etc.

https://www.amazon.com/Hasbro-A5640-Connect-4-Game/dp/B00D8STBHY

## Teacher Response ##

**Approved**

Keep in mind that while you don't need to use the World/Actor game engine we already developed, it would be VERY nice if you
had interactivity and animation.  For example, when placing pieces it would be cool if they literally dropped into place.  This
isn't too hard using a Path Transition in JavaFX.  Once the animation finishes, you can set an onFinished handler to then analyze
the board and take action like say whose turn it is, who just won, etc.  And for computer AI, I've written a minmax (or minimax)
algorithm for Othello and basically you have to figure out how to assign a "score" to any given position and then you recurse
through the possible moves and look for the branch that leads to a the best scores for you and the worse scores for  your opponent.
It's not too bad but it's slow if you recurse too far.  Anyway, you have a good game idea and it's just a matter of how well
you execute it and how "interactive" you game ends up looking/feeling.


## Class Design and Brainstorm ##

Put all your brainstorm ideas, strategy approaches, and class outlines here

## Development Journal ##

Every day you work, keep track of it here.

**Date (time spent)**

Goal:  What are you trying to accomplish today?

Work accomplished:  Describe what you did today and how it went.

**Date (time spent)**

Goal:  What are you trying to accomplish today?

Work accomplished:  Describe what you did today and how it went.

