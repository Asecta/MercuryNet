# Mercury

**[WIP] Please note, this is a work in progress, and is not perfect [WIP]**

Mercury is a distributed, scalable, and expandable data platform for minecraft servers, backed by hazelcast. 

The idea of Mercury came to me when thinking about how to do data distribution for something like a minecraft server network. Minecraft is interesting, as unlike in other scalable systems, the player is always connected through to a single end server. Queries don't get handled by other nodes such as in a microservice architecture, and therefor it makes sense to have cached data for that player moved to the server on which they're playing. 

## Concepts

Distributed caching of data, with the ability to expand to multiple servers. Using distributed caches, data about a player is moved around such that it's always local to the player, and instantly accessable. No need to repeatedly query databases when a player switches servers. 

Automatic discovery of server on the network using multicast (Use at your own risk, ideally within a private network)

Servers can be given "groups". A group can be subsitutdes in most places (i.e commands) for the server name, to denote you want to target all of the servers in the group. 


Dependencies can be downloaded at runtime to lower the package size. I don't know if i'll keep this feature, but i added it to make it more convinient to send over discord (hazelcast is big, and made it larger than the size limit)

## Built-ins


### Commands

Alert <server/group> <message> - allows messages to be sent to all servers, groups, or a specific server

Dispatch <server/group> <command> - allows commands to be sent to all servers, groups, or a specific server

Find <playername> - finds a player on the network, and returns the server they're on

Join <targetname> - joins the sender to a server containing the targetted player, or group of servers 

Redirect <playername> <targetname> - redirects the player to the target server, or group of servers

list - lists all servers and online players

restart - restarts the server in the given amount of time

staffchat <message> - sends a message to all staff on staffchat enabled servers

whereami - returns the server you're on

### Features

scheduled restarts - allows servers to be restarted on a schedule

healthcheck - allows servers to be monitored for health, and restarted if they're not healthy. Can also be piped into dashboards for monitoring.


