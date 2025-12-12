# Custom Server Teleport

A simple Minecraft **Velocity** plugin that registers **custom commands** to access servers replacing the need to use the `/server` command.

This plugin also adds **custom permissions** for the commands used to access the servers


> [!IMPORTANT]  
> This plugin is written with the ass, there is no check for server of commands validity
> use it at your own risk

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
