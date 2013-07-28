#RTwUP

##Realtime Twitter Url Popularity
Given a suitably filtered stream of documents returned from a Twitter query, calculate real-time statistics and show the ranking of the most twittered URLs since system activation.
The statistics must be updated on screen every N seconds.  
They show the links organized into various domain categories, each with its counting popularity:  
| Domain | Link | Frequency |
| :----: | :--: | :-------: |
|foursquare.com | expanded.url.com/123 | 9 times |
| foursquare.com | expanded.url.com/456 |8 times | 
| youtube.com | ... | ... |  
| instagram.com | ... | ...|   
...  

###Data Stream Description and Requirements: 
The system has to use Twitter APIs ([Twitter4j][02], [Hosebird][03] for instance) to perform queries and retrieve Tweets, suitably filter them (e.g. according to the coordinates of a polygon centered on Rome, Milan or a city of your choice).  
The links of interest are the ones retrieved from the entities/urls field of the Tweet json: 
* first of all, links have to be expanded, reversing the output of Twitter's shortening service (_t.co_) 
* if the Tweet contains the expanded form of the URL, the count is assigned to it;
* if the Tweet contains a “shortened” form of the URL(e.g. bit.ly/13NHE7v , goo.gl/uJH2Y , http://instagr.am/p/S3l5rQjCcA/, ecc ...), then it has to be expanded in order to obtain the completely expanded form (eventually after several expansions); the count can then be assigned to it.
 
Starting from the final expanded form, domain information can be extracted to organize the current results.
This must be done in real-time, using [Storm][01]

##Technologies adopted
RTwUP is developed in Java.  
To listen to Twitter's stream, it was chosen [Twitter4j][02], Twitter Stream API in particular.
To process the Tweets real time, it was chosen [Storm][01].
The user interface is written as a Node.js application, making use of socket.io and Redis.  

For more information, you can refer to the wiki pages.

##Wiki

* [Setting up the Maven project] (https://github.com/Dani7B/RTwUP/wiki/Setting-up-the-Maven-project)
* [Creation of a Twitter account to use] (https://github.com/Dani7B/RTwUP/wiki/Creation-of-a-Twitter-account-to-use)
* [Installing and setting up Node.js] (https://github.com/Dani7B/RTwUP/wiki/Installing-and-setting-up-Node.js)
* [Setting up Redis] (https://github.com/Dani7B/RTwUP/wiki/Setting-up-Redis)
* [How to start RTwUP] (https://github.com/Dani7B/RTwUP/wiki/How-to-start-RTwUP)



[01]: https://github.com/nathanmarz/storm/wiki "Storm Wiki"

[02]: http://twitter4j.org/en/ "Twitter APIs in Java"

[03]: https://github.com/twitter/hbc "Hosebird client"
