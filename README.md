# Custom Server Teleport

![GitHub release (latest by date)](https://img.shields.io/github/v/release/LucernaSancta/Custom-Server-Teleport)
[![Custom-Server-Teleport](https://img.shields.io/hangar/dt/Custom-Server-Teleport?link=https%3A%2F%2Fhangar.papermc.io%2FLucernaSancta%2FCustom-Server-Teleport&style=flat)](https://hangar.papermc.io/LucernaSancta/Custom-Server-Teleport)
![GitHub License](https://img.shields.io/github/license/LucernaSancta/Custom-Server-Teleport?color=green)
[![Custom-Server-Teleport](https://img.shields.io/hangar/views/Custom-Server-Teleport?link=https%3A%2F%2Fhangar.papermc.io%2FLucernaSancta%2FCustom-Server-Teleport&style=flat)](https://hangar.papermc.io/LucernaSancta/Custom-Server-Teleport)

###### Download and views badges are referred to the [Velocity Hangar](https://hangar.papermc.io/LucernaSancta/Custom-Server-Teleport)

A simple Minecraft **Velocity** plugin that registers **custom commands** to access servers replacing the need to use the `/server` command.

This plugin also adds **custom permissions** for the commands used to access the servers


> [!IMPORTANT]  
> This plugin is still in development, bugs are expected, please report them at
> [github.com/LucernaSancta/Custom-Server-Teleport/issues](https://github.com/LucernaSancta/Custom-Server-Teleport/issues)

---

## Config

You can configure the plugin after the first run in the `config.yml`

```yaml
# Default config
servers:
  #- example:
  #    commands: ["example","ex"]
  #    perm_suff: "example" # This will became "cst.server.example"
  #    send_message: "<aqua>Sending <green>%playername%</green> to </aqua><green>%servername%</green>"
  - hub:
      commands: ["hub","lobby","l"]
      permission: "hub"
      send_message: "" # Leave empty to disable the message
```

The permissions are created using the following pattern `cst.server.` + suffix specified in the config.
So as for the example, the permission suffix in the config is **"example"** so the permission will be `cst.server.example`

### Placeholders

There are currently only two supported placeholders for the `send_message` config:

- __%playername%__ replaced with the name of the player executing the command
- __%servername%__ replaced with the name of the target server 

## Reloading while running

Use the `/customserverteleport reload` (or the alias `/cst reload`) command to reload the config file mid game
(requires the `cst.admin.use` permission)

## Notify updates

Updates will be notified in the console at the start of the server or to any player having the `cst.notify_update` permission when they enter the server

## Permissions recap

| Permission            | Action                                                    |
|-----------------------|-----------------------------------------------------------|
| `cst.admin.use`       | Use the **/cst** command                                  |
| `cst.notify_update`   | **Recive notification** when an update is available       |
| `cst.server.*`        | Use the commands to **access the servers**                |
| `cst.server.*.others` | Use the commands to **send other players** to the servers |

## Code utilization

Some code in this repository comes from the [PterodactylPowerAction](https://github.com/Quozul/PterodactylPowerAction) plugin made by [Quozul](https://github.com/Quozul)
