# FlowTimer ⏱️

**FlowTimer** is a modern, high-performance, and ultra-precise speedrun plugin. It allows you to start, pause, and track a run in multiplayer mode without any bugs and with millisecond-level precision.

✨ Features

* 📊 **Global Timer:** Real-time display of elapsed time via a BossBar visible to all players.
* 🧊 **Freeze Mechanic:** Players are frozen (movement, interactions, damage, and hunger disabled) as long as the timer is not active (during the countdown or when paused).
* 🏁 **Automatic Run End:** The timer stops automatically to the millisecond as soon as a player jumps into the End portal after defeating the Ender Dragon.
* 💾 **Anti-Crash & Persistence:** Real-time, automatic saving of the timer’s state to a `data.yml` file to prevent data loss in case of a restart.
* 🌍 **Internationalization (i18n):** Full customization and translation of all messages and the BossBar via the `messages.yml` file.
* 🎵 **Immersive Sound:** Immersive sound effects during the countdown, launch, and victory.

## 💻 Commands & Permissions

All commands require the `flowtimer.admin` permission (granted by default to OPs).

* `/ft start`: Starts a configurable 5-second visual and audio countdown, then begins the run.
* `/ft pause`: Pauses the timer and freezes the screen
* `/ft stop`: Permanently stops the timer and resets it to zero.

## 🛠️ Configuration

The plugin automatically generates two configuration files when it is first launched:

1. `messages.yml`: Allows you to modify all text displayed in the chat and in the BossBar, with support for color codes (`&`).
2. `data.yml`: Manages the persistence of the timer’s state transparently for administrators.

## 🚀 Installation

1. Download the plugin’s `.jar` file.
2. Drag it into the `plugins/` folder on your server.
3. Restart your server.
4. Configure the messages to your liking and enjoy your runs!