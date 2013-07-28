#RTwUP - Realtime Twitter Url Popularity

Given a tweets stream from Twitter, filtered in a suitable way, RTwUP realize the real time statistics of most URLs twitted since the software is active.
These statistics show links by domain.  

For example:
| Domain | Link | Frequency |
| :----: | :--: | :-------: |
|foursquare.com | expanded.url.com/123 | 9 times |
| | expanded.url.com/456 |8 times | 
| youtube.com | ... | ... |  
| instagram.com | ... | ...|   
...  

##Data Flow Description
Tweets are captured from a Twitter's stream thanks to Twitter APIs, then are elaborated to filter it by geolocation (for example, according to the coordinates of a polygon centered of a city).  
Links found into the tweet are processed in this way:
* first of all, links are expanded from Twitter's shorting service (_t.co_) 
* after that, if the link is "expanded" yet, then the system count directly the frequency; 
* else, if the link is "shortned" (e.g. 'http://bit.ly/13NHE7v', 'http://goo.gl/uJH2Y', 'http://instagr.am/p/S3l5rQjCcA/', etc...) then it requires to arrive,  possibly with some expansions, to final form to count frequencies.
 
Starting from expanded URLs, RTwUP extract pages' domain to organize results.

##Technologies adopted
RTwUP is written in Java.  
To listen to Twitter's stream, it was chosen [Twitter4j][02] but an a alternative client is [Hosebird][03].
To process real time the tweets, it was chosen [Storm][01].
The user interface was written as a Node.js application.  

For more information, read wiki pages.

##Wiki

* [Setting up the Maven project] (https://github.com/Dani7B/RTwUP/wiki/Setting-up-the-Maven-project)
* [Creation of a Twitter account to use] (https://github.com/Dani7B/RTwUP/wiki/Creation-of-a-Twitter-account-to-use)
* [Installing and setting up Node.js] (https://github.com/Dani7B/RTwUP/wiki/Installing-and-setting-up-Node.js)
* [Setting up Redis] (https://github.com/Dani7B/RTwUP/wiki/Setting-up-Redis)
* [How to start RTwUP] (https://github.com/Dani7B/RTwUP/wiki/How-to-start-RTwUP)



[01]: https://github.com/nathanmarz/storm/wiki "Storm Wiki"

[02]: http://twitter4j.org/en/ "Twitter APIs in Java"

[03]: https://github.com/twitter/hbc "Hosebird client"