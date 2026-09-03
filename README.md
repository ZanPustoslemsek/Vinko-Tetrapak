# Vinko

**Vinko** is a game-playing engine developed for the in-class machine
competition in the *Programiranje 1* course at FRI, University of Ljubljana
— and it won the competition.

Vinko implements the `Stroj` (machine/player) interface for the game
Tetrapak, described below, and plays it automatically against other
machines or human opponents.

## Demo

https://github.com/user-attachments/assets/c8bbf867-fca3-4db6-8509-8435e34b5cc6

## About the game: Tetrapak

Tetrapak is a two-player tile-placement game. Players take turns placing
tetrominoes from a shared, predetermined set onto a rectangular board of
height `h` and width `w`. On each turn, a player chooses one of the
tetrominoes still available in the set and places it on any free spot on
the board. A player who cannot place any of the remaining tetrominoes
loses; if all tetrominoes in the set are used up, the game ends in a draw.

## How to Compile

Simply use the provided shell script:

```bash
./prevedi.sh
```

Compiled classes are placed under `classes/`, in subdirectories matching
their package names.

## How to Run

Vinko cannot be run on its own — it must be run through the game framework.
From the base directory using the provided script:

```bash
./pozeni.sh <parameters>
```

For example, to play against Vinko as the second player on a 15×12 board:

```bash
./pozeni.sh 15x12 -2 s_vinko.Vinko
```

Run `./pozeni.sh -?` to see the full list of available
parameters at any time.

### Parameters

All parameters are optional and can be given in any order (though some are
mutually exclusive, as noted below).

| Parameter | Description |
|---|---|
| `hxw` | Board size: height `h` and width `w`, each between 4 and 20 (e.g. `15x12`). Defaults to `7x8`. |
| `-m tetromine` | Comma-separated indices (no spaces) of the tetrominoes allowed in the set, e.g. `-m 3,5,13`. If omitted, any tetromino can appear. |
| `-r datoteka` | Load a fixed board size and tetromino arrangement from the given file, instead of generating one randomly. Cannot be combined with `hxw`, `-m`, or `-s`. |
| `-ri datoteka` | Save the randomly generated target arrangement to the given file. Only meaningful when the arrangement is generated randomly (i.e. `-r` is not used). |
| `-s seme` | Seed for the random generator used to build the tetromino set. The generator is initialized once per session, not per game, so the same seed plus board size reproduces the same sequence of sets across games. |
| `-1 stroj` | Class name of the machine playing as the first player (must implement `Stroj`). If omitted, a human plays first. |
| `-2 stroj` | Class name of the machine playing as the second player. Can be omitted along with `-1` for machine-vs-machine games. |
| `-t čas` | Time limit per machine, given as `t`, `ts` (seconds), or `tms` (milliseconds). Humans always have unlimited time. If omitted, machines have no time limit. |
| `-d datoteka` | Append the course of each game to the given file. |
| `-b` | Run the framework in text mode instead of graphical mode. |
| `-n številoPartij` | Number of games to play automatically in sequence, without user interaction. Only valid for machine-vs-machine play in combination with `-b`. |
| `-c čas` | In graphical machine-vs-machine mode, the pause after each move (same time format as `-t`). Only affects viewing, not the machines' available time. Defaults to `500ms`. |
| `-?` | Print a description of the parameters instead of running the framework. |

Examples:

```bash
./pozeni.sh
```
Human vs. human on a 7×8 board with a randomly generated tetromino set.

```bash
./pozeni.sh 15x12 -2 s_vinko.Vinko -m 0,1,4
```
Human (first) vs. Vinko (second) on a 15×12 board, using only tetrominoes 0, 1, and 4.

```bash
./pozeni.sh 10x10 s_vinko.Vinko s_vinko.Vinko -n 100 -b -t 20ms -d log.txt -s 12345
```
Vinko plays 100 games against itself on a 10×10 board in text mode, each with a 20 ms time limit per move, logging results to `log.txt`, using random seed `12345`.

## Example Reference Machines

A few example `Stroj` implementations are commonly used for testing and demos alongside custom machines like Vinko:

- **`s12345678.Testko`** - a simple baseline machine.
- **`s00000000.Pocasko`** - a slow machine.
- **`s99999999.Napacko`** - a machine that can output wrong moves.

## Credits

The Tetrapak game and competition framework were created by **FRI,
University of Ljubljana** (Faculty of Computer and Information Science)
for the *Programiranje 1* course. Vinko (`Vinko.java`) is my own work.
