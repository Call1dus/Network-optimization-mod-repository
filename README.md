# Network Optimization Mod

This Fabric mod prioritizes client-side food consumption and then notifies the server to apply
consumption effects for multiplayer synchronization. It also prioritizes client-side splash and
lingering potion throws and forwards locally received status effects to the server.

## How it works
- The client processes food usage first.
- After consumption completes locally, the client sends a lightweight packet to the server with
  the hand used.
- The server validates the held item is still food and applies the same consumption logic.
- Splash or lingering potion throws are handled client-side immediately, with the throw notified
  to the server for authoritative spawning.
- When the client applies a status effect, it sends the effect details to the server so the server
  can reconcile quickly.

## Requirements
- Minecraft 1.21.11
- Fabric Loader 0.16.9+
- Fabric API compatible with Minecraft 1.21.11
- Java 21

## Notes
- Install the mod on both client and server for multiplayer.
- The server remains authoritative; this mod only prioritizes the client experience and then
  informs the server so it can reconcile.
