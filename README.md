# Client Side Capes

This is my first ever attempt at modding Minecraft!
It's been a fun learning experience.

This is a very simple mod that allows for the client-side use of custom capes.
This mod will allow for the application of custom capes to all player characters, even if their account does not have an associated Mojang cape.
Basically it injects some code to tell the game to, upon building a player model, always render a cape to a given UUID.
All the user needs to do is add a cape.png file to the `custom_capes` directory in their `.minecraft` folder and rename it to the UUID of the player they wish to match with the cape. 
This mod does not require any dependencies other than the Fabric Modloader and API for Minecraft version 26.1.2

I initially was trying to create a nice little config menu for doing this in-game, but it seemed redundant for such as simple mod, and I (right now) like the idea of having minimal dependencies on other mods like cloth config and mod menu. However, the initial experiments at creating a cloth config menu are still saved in `old_guis_and_configs`. I may revive this idea at some point, but not right now.

-4000Kelvin

## Thanks:
Kaupenjoe (www.youtube.com/@ModdingByKaupenjoe)

Fabric Docs and Developers (https://docs.fabricmc.net/)

*Various other, more complex Cape Mods used as reference:*

Capes: https://modrinth.com/mod/capes

Client Capes: https://modrinth.com/mod/client-capes (I actually only found this towards the end of the project. It is exactly what I was looking for, but is unfortunately unmaintained)

And of course, a moderately helpful hand from Google AI (Even if it did gaslight and misdirect me at every step lmao. That was a learning experience)

## License

This template is available under the CC0 license. (I don't really understand licensing yet, but I guess its creative commons, public domain, built from the Fabric Modding Template)
