# 🚢 Battleship Game in Java

This is a terminal-based Battleship game implemented in Java with support for multiple game modes, including:

- **Singleplayer** (Zen mode, just shoot at a couple target ships)
- **PvPvE** (Two players against a shared target fleet)
- **PvE** (You vs a computer enemy)
- **Online Multiplayer** (Coming soon)

Built with modularity and extensibility in mind, the game provides a solid base for further enhancements such as AI difficulty, board customization, and multiplayer networking.

---

## 📦 Features

- Text-based user interface using `System.in` / `System.out`
- Smart ship placement ensuring no overlaps or illegal placements
- Bonus turn if a ship is hit 🔁
- Game modes selectable from a main menu
- Separate handling for UI and core game logic
- ASCII-art welcome screen and selection menu
- Clear board display with coordinates (A-J, 1-10)

---

## 🔧 Usage

### ✅ Running the Game
Make sure you're using Java 17 or later.

```bash
javac *.java
java UI
