# Minesweeper Game

## Overview

This project implements the classic **Minesweeper** game, where players must uncover cells in a grid without triggering any mines. The goal is to uncover all safe cells based on numerical clues that indicate the number of adjacent mines. Flagging suspected mines is also a key part of the game. The project includes the core game logic in the `MinesweeperGame` class, with `Cell` representing individual cells on the game board.

---


## Game Features

- **Uncover cells** to reveal safe spots or mines.
- **Flag cells** to mark suspected mines.
- **Calculate adjacent mines** using numbers in uncovered cells.
- **Win condition**: Uncover all non-mine cells.

---

## Class: `MinesweeperGame`

### Overview

The `MinesweeperGame` class contains the main game logic: generating the board, uncovering cells, flagging cells, and checking for win/lose conditions.

### Methods

- **`void generateBoard(int rows, int cols, int mines)`**  
  Generates a board with a given number of rows, columns, and randomly placed mines.

- **`void uncoverCell(int row, int col)`**  
  Uncovers the cell at the specified coordinates and propagates the uncovering if necessary.

- **`boolean checkWin()`**  
  Returns `true` if the player has won by uncovering all non-mine cells.

- **`void flagCell(int row, int col)`**  
  Flags the specified cell as a suspected mine.

---

## Class: `Cell`

### Overview

The `Cell` class represents each individual cell on the Minesweeper board. It tracks the mine status, whether it's uncovered, flagged, and how many adjacent mines there are.

### Fields

- **`boolean isMine`**  
  True if the cell contains a mine.

- **`boolean uncovered`**  
  True if the cell has been uncovered.

- **`boolean flagged`**  
  True if the cell has been flagged by the player.

- **`int adjacentMines`**  
  The number of mines in adjacent cells.

### Methods

- **`void uncover()`**  
  Uncovers the cell and reveals its state.

- **`void flag()`**  
  Flags the cell as a suspected mine.

- **`String cellRepresentation()`**  
  Returns a string representation of the cell's current state (covered, uncovered, flagged).

---

## Class: `Tester`

### Overview

The `Tester` class includes unit tests that verify key functionality in the Minesweeper game, ensuring the game logic works as expected.

### Example Tests

- **`testGenerateBoard()`**  
  Verifies that the board is generated with the correct number of rows, columns, and mines.

- **`testUncoverCell()`**  
  Ensures that uncovering a cell works correctly, including the propagation of empty cells.

- **`testFlagCell()`**  
  Verifies that the flagging mechanism works as intended.

- **`testCheckWin()`**  
  Ensures that the win condition is checked properly when all non-mine cells are uncovered.

---


