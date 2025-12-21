# Custom Server Teleport

![GitHub release (latest by date)](https://img.shields.io/github/v/release/LucernaSancta/Custom-Server-Teleport)
[![Custom-Server-Teleport](https://img.shields.io/hangar/dt/Custom-Server-Teleport?link=https%3A%2F%2Fhangar.papermc.io%2FLucernaSancta%2FCustom-Server-Teleport&style=flat)](https://hangar.papermc.io/LucernaSancta/Custom-Server-Teleport)
![License: MIT](https://img.shields.io/badge/License-MIT-greeb.svg)
[![Custom-Server-Teleport](https://img.shields.io/hangar/views/Custom-Server-Teleport?link=https%3A%2F%2Fhangar.papermc.io%2FLucernaSancta%2FCustom-Server-Teleport&style=flat)](https://hangar.papermc.io/LucernaSancta/Custom-Server-Teleport)

A simple Minecraft **Velocity** plugin that registers **custom commands** to access servers replacing the need to use the `/server` command.

This plugin also adds **custom permissions** for the commands used to access the servers


> [!IMPORTANT]  
> This plugin is still in development, bugs are expected, please report them at
> [github.com/LucernaSancta/Custom-Server-Teleport/issues](https://github.com/LucernaSancta/Custom-Server-Teleport/issues)

---

## Config

You can configure the plugin after the first run in the `config.yml`

```yml
# Default config
servers:
    #- example:
    #    commands: ["example","ex"]
    #    permission: "customcommand.example"
    - hub:
        commands: ["hub","lobby","l"]
        permission: "customcommand.hub"
```

## Code utilization

Some code in this repository comes from the [PterodactylPowerAction](https://github.com/Quozul/PterodactylPowerAction) plugin made by [Quozul](https://github.com/Quozul)
