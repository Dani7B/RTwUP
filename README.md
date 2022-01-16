# **RTwUP - Realtime Twitter User Profile**

Given a suitably filtered stream of documents returned by a Twitter query, calculate and show real-time statistics; these consist of the amount of single-time users per hour/day/month.  
As well as counting, the system stores User profiles in a repository, adding a so called "snapshot" only if some criteria is met (e.g. changed profile image URL or description or more than 1% increase/decrease in friends or follower amounts).  

## Data Stream Description and Requirements: 
The system uses Twitter APIs ([Twitter4j][02], [Hosebird][03] for instance) to perform queries and retrieve Tweets, suitably filters them (e.g. according to specified coordinates or keywords).  
If the URL contained in the user profile is in a shortned form of some kind, it's expanded to its original form (performing the expansion multiple times if required).  
  
This must be done in real time, using [Storm][01].

## Adopted Technologies
RTwUP is developed in *Java*.  
To listen to Twitter's stream, it was chosen [Twitter4j][02], *Twitter Stream API* in particular.  
To process the Tweets real time, it was chosen [Apache Storm][01].
The user interface is written in *Javascript* as a [Node.js][04] application, making use of [socket.io][05] and [Redis][06] to display statistics in real time.  
Persistence of the retrieved Twitter User Profiles is obtained by means of a repository based on [Elasticsearch][07].

For more information, you can refer to the wiki pages.

## Wiki

* [Setting up the Maven project] (https://github.com/Dani7B/RTwUP/wiki/Setting-up-the-Maven-project)
* [Creation of a Twitter account to use] (https://github.com/Dani7B/RTwUP/wiki/Creation-of-a-Twitter-account-to-use)
* [Installing and setting up Node.js] (https://github.com/Dani7B/RTwUP/wiki/Installing-and-setting-up-Node.js)
* [Setting up Redis] (https://github.com/Dani7B/RTwUP/wiki/Setting-up-Redis)
* [How to start RTwUP] (https://github.com/Dani7B/RTwUP/wiki/How-to-start-RTwUP)



[01]: https://storm.apache.org "Apache Storm"

[02]: http://twitter4j.org/en/ "Twitter APIs in Java"

[03]: https://github.com/twitter/hbc "Hosebird client"

[04]: http://nodejs.org/ "Node.js web page"

[05]: http://socket.io/ "socket.io web page"

[06]: http://redis.io/ "Redis web page"

[07]: http://www.elasticsearch.org/ "Elasticsearch web page"
