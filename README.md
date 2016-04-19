# GJBot

## Introduction

This Twitch chat bot was originally created as a project for my third Object Oriented Programming assignment in 2nd Year. I chose to pursue this project due to my own personal interest in Twitch as a whole. I have been using the website for almost four years now, and have seen many different chat bots, some similar, some varied. I decided I would create a basic command driven bot as my first Twitch bot, as it seems like the most prevalent type of bot that I have seen on the website.

## How it works

You must pass your bot's username, oauth token and client ID to the application in order to run it. These are given as arguments when starting the program.

As mentioned above, this bot works using commands. A command is any message that starts with a '!' character, and any message that is supported as a command by the bot. At this moment in time, the commands that can be given to the bot are as follows:

 * !help - Returns a list of possible commands
 * !botinfo - Gives information about the bot, and links back to this GitHub page.
 * !test - Simple test command to confirm the bot is working, sends back test as a message
 * !motd - Returns the message of the day if it is set for the channel the command was sent in
 * !motd set <message> - Sets the message of the day to be the message given
 * !motd delete - Deletes the message of the day currently set for the channel
 * !uptime - Returns the amount of time that a stream
 * !title - Returns the title of the stream
 * !game - Returns the currently set game for the stream
 * !age - Returns the account age of the channel the bot is currently in
 * !myage - Returns the account age of the person who sent the command
 
There are also two commands that can currently be accessed from my stream chat, which are:

 * !join <channel> - Connects the bot to the specified channel
 * !leave <channel> - Disconnects the bot from the specified channel
 
## Can I use this?

This project is created under the MIT License, so feel free to use, modify, and distribute this project under the terms of that license.

Copyright (c) 2016 Graham Byrne
 