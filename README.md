#RTwUP

##Realtime Twitter Url Popularity

=======
Dato un flusso di documenti provenienti da una query su Twitter, filtrata in modo opportuno , realizzare delle statistiche aggiornate in tempo reale che mostrino la classifica degli URL più twittati da quando il sistema è attivo.
Le statistiche vengono stampate a schermo ogni N secondi.  
Le statistiche mostrano i link organizzati in varie categorie di domini, ognuno con il suo conteggio di popolarità:  
foursquare.com: expanded.url.com/123 - 9 times; expanded.url.com/456 - 8 times  
  youtube.com: ...  
	instagram.com: ...  
	...  
	Other  
	All  

###Descrizione del flusso dei dati: 
I Tweets sono scaricati a partire da una query con le Twitter APIs (ad esempio [Twitter4j][02], [Hosebird][03]), filtrate opportunamente (ad esempio, in base alle coordinate di un poligono centrato su Roma, oppure Milano, etc... a scelta).  
I link trovati nel campo entities/urls del json del tweet sono quelli di interesse: 
* se è già presente nel tweet la forma espansa dell’url, il conteggio viene assegnato allo stesso; 
* se è presente una forma “shortened” (ad esempio bit.ly/13NHE7v , goo.gl/uJH2Y , http://instagr.am/p/S3l5rQjCcA/, ecc ...), allora è necessario espanderla, arrivando (eventualmente con diverse espansioni) alla forma finale completamente espansa.
 
A partire dalla forma finale espansa, si può estrarre la categoria del dominio a cui questa appartiene, per organizzare i risultati attuali.
Tutto questo verrà effettuato in tempo reale, usando [Storm][01]


[01]: https://github.com/nathanmarz/storm/wiki "Wiki di Storm"

[02]: http://twitter4j.org/en/ "Sito di riferimento per le APIs di Twitter in Java"

[03]: https://github.com/twitter/hbc "Hosebird client"

##Setting up the Maven project

* If you haven't got *Eclipse IDE for Java EE Developers*, you should download and install the M2E plugin for Eclipse from the Eclipse Marketplace
* In the Project Explorer, create a new Maven Project. Make sure only "Use default workspace location" is checked and press Next. Wait for the windows to download all the archetypes, then select maven-archetype-webapp. In the next screen you'll have to fill in some details, such as the group Id (e.g. ggd), archetype id (e.g. RTwUP). Then you can press "Finish" and the project will be created.
It may depends on how your buildpath is configured, but if Eclipse highlights a problem with the Java version set in your buildpath, go fixing the issue.
* You can integrate the latest Twitter4J build easily by just including the following lines in your pom.xml (We are using Twitter Stream API).
<pre class="codeSample">   &lt;dependencies&gt;<br/>      &lt;dependency&gt;<br/>           &lt;groupId&gt;org.twitter4j&lt;/groupId&gt;<br/>           &lt;artifactId&gt;twitter4j-stream&lt;/artifactId&gt;<br/>           &lt;version&gt;[3.0,)&lt;/version&gt;<br/>       &lt;/dependency&gt;<br/>       ...<br/>   &lt;/dependencies&gt;<br/></pre>
If you use Eclipse to open and modify pom.xml, hit the Dependencies tab and click Add (on the left), then fill in the fields with the details given above in the XML format.
* You should also integrate Storm in your Maven project. The first thing to do is to add a dependency in your pom.xml (before the &lt;dependencies&gt; tag) like this:
<pre class="codeSample">   &lt;repositories&gt;<br/>      &lt;repository&gt;<br/>           &lt;id&gt;clojars.org&lt;/id&gt;<br/>           &lt;url&gt;http://clojars.org/repo &lt;/url&gt;<br/>       &lt;/repository&gt;<br/>   &lt;/repositories&gt;<br/></pre>
Then, like what you did before with the Twitter4J dependency, add the Storm dependency:
<pre class="codeSample">   &lt;dependencies&gt;<br/>      &lt;dependency&gt;<br/>           &lt;groupId&gt;storm&lt;/groupId&gt;<br/>           &lt;artifactId&gt;storm&lt;/artifactId&gt;<br/>           &lt;version&gt;[0.7.2,)&lt;/version&gt;<br/>           &lt;scope&gt;test&lt;/scope&gt;<br/>       &lt;/dependency&gt;<br/>   &lt;/dependencies&gt;<br/></pre>
Refresh your project if required.

##Creation of a Twitter account to use

* Basically, you have to grant access rights to your account for the application. You have to go through this process only once, really, and never again. This being the case, you can skip this and the next step
Go to https://dev.twitter.com/apps and login. Click on "Create an Application" and fill in the details: the application name, a short description and your application's publicly accessible home page (the git page, for instance) and hit the "Create your Twitter application" button. In Settings you can define the type of access your application needs (Read / Read and Write / Read, Write and Access direct messages).
* Create the access token (lower on the page) to obtain the "access token" and "access token secret". 
* At this point, you've got everything you need to configure Twitter4j: Consumer key, Consumer secret, Access token and Access Token Secret.
 
##Test Twitter4j

* There are several ways to set oAuth properties. Here's some [example] (http://twitter4j.org/en/configuration.html)
* You can test that Twitter4j is working properly and that you've got everything set up. You can create a Test class in your project and start playing around with Twitter4j.

##The story so far...
After properly setting up the project, we started working with Storm and created a Spout that retrieves all the tweets with links from Rome urban area. Test class is used to try methods without involving the Spout.

