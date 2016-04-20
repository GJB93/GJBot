# GJBot

## Dependencies 

org.json - http://mvnrepository.com/artifact/org.json/json/20160212

Java 8 JDK - http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

## Introduction

This Twitch chat bot was originally created as a project for my third Object Oriented Programming assignment in 2nd Year. I chose to pursue this project due to my own personal interest in Twitch as a whole. I have been using the website for almost four years now, and have seen many different chat bots. I decided I would create a basic command driven bot as my first Twitch bot, as it seems like the most prevalent type of bot on the website. I wanted to build the project from the ground up, so I decided against using pirc to aid in the creation of this bot.

## How it works

You must pass your bot's username, oauth token and client ID to the application in order to run it. These are given as arguments when starting the program.

As mentioned above, this bot works using commands. A command is any message that starts with a '!' character, and any message that is supported as a command by the bot. At this moment in time, the commands that can be given to the bot are as follows:

 * **!help** - Returns a list of possible commands
 * **!botinfo** - Gives information about the bot, and links back to this GitHub page.
 * **!test** - Simple test command to confirm the bot is working, sends back test as a message
 * **!motd** - Returns the message of the day if it is set for current channel
 * **!motd set "message"** - Sets the message of the day to be the message given
 * **!motd delete** - Deletes the message of the day for the current channel
 * **!uptime** - Returns the amount of time that a stream
 * **!title** - Returns the title of the stream
 * **!game** - Returns the currently set game for the stream
 * **!age** - Returns the account age of the current channel
 * **!myage** - Returns the account age of the sender
 * **!followage** - Returns the amount of time the sender has been following the channel
 * **!viewers** - Returns the number of live viewers the channel currently has
 * **!followers** - Returns the number of followers the channel has
 
There are also two commands that can currently be accessed from my stream chat, which are:

 * **!join "channel"** - Connects the bot to the specified channel
 * **!leave "channel"** - Disconnects the bot from the specified channel
 
## Can I use this?

This project is created under the MIT License, so feel free to use, modify, and distribute this project under the terms of that license.

Copyright (c) 2016 Graham Byrne
 
